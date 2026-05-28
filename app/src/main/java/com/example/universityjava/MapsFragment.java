package com.example.universityjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.universityjava.database.PickupPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsFragment extends Fragment {
    AppDatabase db = AppActivity.getDatabase();
    PickupPoint selectedPickupPoint;
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
            // Example camera position (Kaunas)
            LatLng kaunas = new LatLng(54.8985, 23.9036);

            googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(kaunas, 12f)
            );

            // Get pickup points from Room
            List<PickupPoint> pickupPoints =
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

                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
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
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}