package com.example.universityjava;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DialogTitle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.universityjava.database.Listing;
import com.example.universityjava.database.Order;
import com.example.universityjava.database.OrderState;
import com.example.universityjava.database.PickupPoint;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import kotlinx.serialization.StringFormat;

public class MapsFragment extends Fragment {
    AppDatabase db = AppActivity.getDatabase();
    PickupPoint selectedPickupPoint;
    List<PickupPoint> pickupPoints;
    private GoogleMap map;
    private LatLng lastLocation;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker;
    private Button button;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            // Example camera position (Kaunas)
            LatLng kaunas = new LatLng(54.8985, 23.9036);

            googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(kaunas, 12f)
            );

            // Get pickup points from Room
            pickupPoints =
                    db.pickupPointDAO().getAllPickupPoints();

            for (PickupPoint point : pickupPoints) {

                LatLng position = new LatLng(
                        point.getLatitude(),
                        point.getLongitude()
                );

                Marker marker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(position)
                                .title(point.getName())
                                .snippet(point.getAddress())
                );

                // Store the PickupPoint object in marker
                marker.setTag(point);
            }

            // Handle marker clicks
//            googleMap.setOnMarkerClickListener(marker -> {
//
//                PickupPoint selectedPoint = (PickupPoint) marker.getTag();
//
//                if (selectedPoint != null) {
//
//                    selectedPickupPoint = selectedPoint;
//
//                    Toast.makeText(getContext(), "Selected: " + selectedPoint.getName(), Toast.LENGTH_SHORT).show();
//                }
//
//                // Show default info window
//                return false;
//            });

            googleMap.setOnMarkerClickListener(marker -> {

                PickupPoint selectedPoint = (PickupPoint) marker.getTag();

                if (selectedPoint != null) {

                    new AlertDialog.Builder(getContext())
                            .setTitle(selectedPoint.getName())
                            .setMessage("Confirm this pickup location?\n\n" + selectedPoint.getAddress())
                            .setPositiveButton("Confirm", (dialog, which) -> {

                                selectedPickupPoint = selectedPoint;

                                Toast.makeText(
                                        getContext(),
                                        "Confirmed: " + selectedPoint.getName(),
                                        Toast.LENGTH_SHORT
                                ).show();

                                // TODO:
                                // continue checkout / return result / save selection
                                AddOrders(selectedPickupPoint);
                                Fragment fragment = HistoryFragment.newInstance(AppActivity.getCurrentUserID());
                                ((MainActivity)getActivity()).replaceFragment(fragment);

                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                } else{
                    new AlertDialog.Builder(getContext())
                            .setTitle(marker.getTitle()).show();
                }

                // Return true → prevent default behavior (no info window)
                return true;
            });

            // Optional:
            // when info window clicked -> confirm selection
            googleMap.setOnInfoWindowClickListener(marker -> {

                PickupPoint selectedPoint =
                        (PickupPoint) marker.getTag();

                if (selectedPoint != null) {

                    selectedPickupPoint = selectedPoint;

                    // Example:
                    // save selected point
                    // return to previous screen
                    // continue checkout flow

                    Toast.makeText(getContext(), "Confirmed: " + selectedPoint.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.button);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000).build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if(locationResult==null){
                    return;
                }
                for (Location location : locationResult.getLocations()){
                    //update ui
                    if(marker == null && map != null){
                    AddMarker();}
                }
            }
        };

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        getLastKnownLocation();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastLocation != null && map != null){

                    double minDistance;
                    PickupPoint pmin = pickupPoints.get(0);
                    minDistance= Math.sqrt((lastLocation.longitude- pmin.getLongitude())*(lastLocation.longitude- pmin.getLongitude())
                            - (lastLocation.latitude-pmin.getLatitude())*(lastLocation.latitude-pmin.getLatitude()));
                    for (PickupPoint p: pickupPoints
                         ) {
                        double distance = Math.sqrt((lastLocation.longitude- p.getLongitude())*(lastLocation.longitude- p.getLongitude())
                                + (lastLocation.latitude-p.getLatitude())*(lastLocation.latitude-p.getLatitude()));
                        if(distance <= minDistance) {minDistance=distance;
                        pmin = p;}
                    }
                    LatLng n = new LatLng(pmin.getLatitude(), pmin.getLongitude());
                    map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(n, 15f)
                    );
                }
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        startLocationUpdates();
    }
    @Override
    public void onPause(){
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    PackageManager.PERMISSION_GRANTED);
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());
    }



    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
    private void getLastKnownLocation()
    {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            //Toast toast = Toast.makeText(getContext(), lastLocation.toString(),
                            //        Toast.LENGTH_SHORT);
                            //toast.show();
                            if(marker == null && map != null){
                            AddMarker();}
                            // Logic to handle location object
                        }
                    }
                });
    }

    private void AddMarker(){
        if(lastLocation == null) return;
        marker = map.addMarker(new MarkerOptions()
                .position(lastLocation)
                .title("My Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(lastLocation, 12f)
        );
    }

    private void AddOrders(PickupPoint pickupPoint){
        AppDatabase db = AppActivity.getDatabase();
        SharedPreferences prefs = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        long userID = prefs.getLong("user_id", -1);
        if(userID < 0) return;
        List<Listing> cartListings = db.listingDAO().getCartListingsByUserId(userID);
        for (Listing listing:cartListings) {
            Order order = new Order();
            order.setFk_pickuppoint(selectedPickupPoint.getId());
            order.setFk_userid(userID);
            order.setFk_listingid(listing.getId());
            order.setOrderstate(OrderState.Pending);
            db.orderDAO().insert(order);
            listing.setIssold(true);
            db.listingDAO().update(listing);
            db.cartListingDAO().removeCListingByListingAndUserID(listing.getId(), userID);
        }
    }
}