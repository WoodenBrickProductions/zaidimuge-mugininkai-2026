package com.example.universityjava;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

public class ProfileFragment extends Fragment {

    private ImageView imageViewProfile;
    private long userID;
    private User user;
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

        Button _buttonAddListing = view.findViewById(R.id.buttonAddListing);
        if (userID == AppActivity.getCurrentUserID()) {
            _buttonAddListing.setOnClickListener(v ->
                    ((MainActivity) requireActivity()).replaceFragment(new AddListingFragment()));
        } else {
            _buttonAddListing.setVisibility(View.GONE);
        }

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

        Button buttonChangePassword = view.findViewById(R.id.buttonChangePassword);
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).replaceFragment(new PasswordChangeFragment());
            }
        });

        if (userID >= 0) {
            user = db.userDAO().getUserByID(userID);
            textViewUsername.setText(user.getName());
            double rating = db.reviewDAO().getAverageRatingBySellerID(userID);
            textViewRating.setText(String.format("%,.2f / 5",rating));
            loadProfileImage(user);
        }

        ImageButton buttonCamera = view.findViewById(R.id.buttonCameraProfile);
        buttonCamera.setOnClickListener(v -> checkPermissionAndLaunchCamera());

        ImageButton buttonUsername = view.findViewById(R.id.buttonUsernameProfile);
        buttonUsername.setOnClickListener(v -> {
//            EditText editText = new EditText(requireContext());
//            editText.setText(user.getName());
//            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
//
//            builder.setTitle("Edit username")
//                    .setView(editText)
//                    .setPositiveButton("Save", (dialog, which) -> {
//                        String newName = editText.getText().toString();
//                        user.setName(newName);
//                        db.userDAO().update(user);
//                        textViewUsername.setText(newName);
//                    })
//                    .setNegativeButton("Cancel", null)
//                    .show();
            EditText editText = new EditText(requireContext());
            editText.setText(user.getName());
            AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.acc_new_username))
                    .setView(editText)
                    .setPositiveButton(getString(R.string.confirm), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v1 -> {
                        String newName = editText.getText().toString();
                        if (db.userDAO().getUserByName(newName).isEmpty()) {
                            dialog.dismiss();
                            user.setName(newName);
                            db.userDAO().update(user);
                            textViewUsername.setText(newName);
                        } else {
                            editText.setError(getString(R.string.acc_username_taken));
                            editText.requestFocus();
                        }
                    });
        });

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
