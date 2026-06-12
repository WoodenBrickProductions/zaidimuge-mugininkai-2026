package com.example.universityjava;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ProfileFragment extends Fragment {

    private ImageView imageViewProfile;
    private long userID;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    public ProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
                .getLong("user_id", -1);

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) launchCamera();
            else Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show();
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (!success || userID < 0) return;
            AppDatabase db = AppActivity.getDatabase();
            User user = db.userDAO().getUserByID(userID);
            user.setProfileImage("avatar_" + userID);
            db.userDAO().update(user);
            loadProfileImage(user);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences prefs = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);

        AppDatabase db = AppActivity.getDatabase();
        TextView textViewUsername = view.findViewById(R.id.textViewUsername);
        TextView textViewRating = view.findViewById(R.id.textViewUserRating);
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        imageViewProfile.setImageResource(R.drawable.baseline_person_outline_24);

        Button _buttonMyListings = view.findViewById(R.id.buttonMyListings);
        _buttonMyListings.setOnClickListener(v ->
                ((MainActivity) requireActivity()).replaceFragment(
                        SellerFragment.newInstance(prefs.getLong("user_id", -1), "")));

        Button _buttonMyHistory = view.findViewById(R.id.buttonMyHistory);
        _buttonMyHistory.setOnClickListener(v -> ((MainActivity) requireActivity()).replaceFragment(
                HistoryFragment.newInstance(prefs.getLong("user_id", -1))));

        Button _buttonLogOut = view.findViewById(R.id.buttonLogOut);
        _buttonLogOut.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("user_id", -1);
            editor.apply();
            startActivity(new Intent(requireActivity().getBaseContext(), LoginActivity.class));
        });

        if (userID >= 0) {
            User user = db.userDAO().getUserByID(userID);
            textViewUsername.setText(user.getName());
            textViewRating.setText("");
            loadProfileImage(user);
        }

        ImageButton buttonCamera = view.findViewById(R.id.buttonCameraProfile);
        buttonCamera.setOnClickListener(v -> checkPermissionAndLaunchCamera());

        return view;
    }

    private void checkPermissionAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        File photoFile = new File(requireContext().getCacheDir(), "game_icons/avatar_" + userID + ".jpg");
        Uri uri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".fileprovider", photoFile);
        cameraLauncher.launch(uri);
    }

    private void loadProfileImage(User user) {
        if (imageViewProfile == null || user.getProfileImage() == null) return;
        File f = AppActivity.getCachedImageFile(requireContext(), user.getProfileImage());
        if (f != null) imageViewProfile.setImageURI(Uri.fromFile(f));
    }
}
