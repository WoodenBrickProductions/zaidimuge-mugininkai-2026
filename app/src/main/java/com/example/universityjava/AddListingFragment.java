package com.example.universityjava;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;

import java.io.File;

public class AddListingFragment extends Fragment {

    ImageView imageView;
    EditText addListingGame;
    EditText addListingPrice;
    Spinner spinnerCondition;

    private int photoSlot = 0; // 0-2 for photos 1-3
    private final String[] physicalPhotos = new String[3]; // names stored in cache
    private String photoPrefix;

    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private Button buttonAddPhoto;

    public AddListingFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoPrefix = "phys_" + System.currentTimeMillis();

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) launchCamera();
            else Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (!success) return;
            String name = photoPrefix + "_" + (photoSlot + 1);
            physicalPhotos[photoSlot] = name;
            if (photoSlot < 2) photoSlot++;
            buttonAddPhoto.setText("Add photo (" + countPhotos() + "/3)");
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_listing, container, false);

        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);
        addListingGame = view.findViewById(R.id.addListingGame);
        imageView = view.findViewById(R.id.gameImage);
        addListingPrice = view.findViewById(R.id.addListingPrice);
        spinnerCondition = view.findViewById(R.id.spinnerCondition);
        Button buttonDelete = view.findViewById(R.id.buttonSecond);
        buttonDelete.setVisibility(GONE);

        Button buttonImage = view.findViewById(R.id.buttonImage);
        LinearLayout physicalImageButtons = view.findViewById(R.id.physicalImageButtons);
        Button buttonAddIcon = view.findViewById(R.id.buttonAddIcon);
        buttonAddPhoto = view.findViewById(R.id.buttonAddPhoto);

        Animator anim = AnimatorInflater.loadAnimator(getContext(), R.animator.overshoot_bounce);
        anim.setTarget(buttonImage);

        // Checkbox toggles between single image button and split icon/photo buttons
        CheckBox checkboxPhysical = view.findViewById(R.id.checkboxPhysical);
        checkboxPhysical.setOnCheckedChangeListener((cb, isChecked) -> {
            buttonImage.setVisibility(isChecked ? GONE : VISIBLE);
            physicalImageButtons.setVisibility(isChecked ? VISIBLE : GONE);
        });

        // Original gallery picker (now also used for Add Icon)
        View.OnClickListener iconPickerClick = v -> {
            anim.start();
            MainActivity.gotFileCallback = file -> {
                buttonImage.setText(addListingGame.getText().toString());
                buttonAddIcon.setText(addListingGame.getText().toString());
                AppActivity.savePickedImageToCache(getContext(), Uri.fromFile(file), addListingGame.getText().toString());
                var bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f);
                scaleX.setDuration(500);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 0f, 1f);
                scaleY.setDuration(500);
                AnimatorSet sequence = new AnimatorSet();
                sequence.playTogether(scaleX, scaleY);
                sequence.start();
            };
            MainActivity.imageLauncher.launch("image/*");
        };
        buttonImage.setOnClickListener(iconPickerClick);
        buttonAddIcon.setOnClickListener(iconPickerClick);

        buttonAddPhoto.setOnClickListener(v -> {
            if (countPhotos() >= 3) {
                Toast.makeText(requireContext(), "Maximum 3 photos already added", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        buttonSubmit.setOnClickListener(v -> {
            var dao = AppActivity.getDatabase().gameDAO();
            Game game;
            var gameList = dao.getGameByName(addListingGame.getText().toString());
            if (gameList.size() > 0) {
                game = gameList.get(0);
            } else {
                game = new Game();
                game.setImage(buttonImage.getText().toString());
                game.setTitle(addListingGame.getText().toString());
                game.setId(AppActivity.getDatabase().gameDAO().insert(game));
            }

            var listing = new Listing();
            listing.setFk_gameid(game.getId());
            listing.setFk_seller(AppActivity.getCurrentUserID());
            listing.setPrice(Double.parseDouble(addListingPrice.getText().toString()));
            listing.setFk_platform(1);
            listing.setIsdigital(!checkboxPhysical.isChecked());
            listing.setPhysicalPhoto1(physicalPhotos[0]);
            listing.setPhysicalPhoto2(physicalPhotos[1]);
            listing.setPhysicalPhoto3(physicalPhotos[2]);

            listing.setId(AppActivity.getDatabase().listingDAO().insert(listing));
            ((MainActivity) requireActivity()).replaceFragment(new HomeFragment());
        });

        // Language / theme spinners (unchanged)
        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Spinner languageSpinner = view.findViewById(R.id.spinnerType);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                if (TextUtils.equals(adapterView.getItemAtPosition(i).toString(), "Lietuvių")
                        && !TextUtils.equals(prefs.getString("app_lang", "en-US"), "lt-LT")) {
                    editor.putString("app_lang", "lt-LT"); editor.apply();
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("lt-LT"));
                } else if (TextUtils.equals(adapterView.getItemAtPosition(i).toString(), "English")
                        && !TextUtils.equals(prefs.getString("app_lang", "en-US"), "en-US")) {
                    editor.putString("app_lang", "en-US"); editor.apply();
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en-US"));
                }
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(
                view.getContext(), R.array.languages_array, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);

        String selection;
        switch (prefs.getString("app_lang", "en-US")) {
            case "lt-LT": selection = "Lietuvių"; break;
            default: selection = "English";
        }
        languageSpinner.setSelection(languageAdapter.getPosition(selection));

        Spinner themeSpinner = view.findViewById(R.id.spinnerPlatform);
        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(
                view.getContext(), R.array.themes_array, android.R.layout.simple_spinner_item);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(themeAdapter);

        return view;
    }

    private void launchCamera() {
        String name = photoPrefix + "_" + (photoSlot + 1);
        File photoFile = new File(requireContext().getCacheDir(), "game_icons/" + name + ".jpg");
        Uri uri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".fileprovider", photoFile);
        cameraLauncher.launch(uri);
    }

    private int countPhotos() {
        int count = 0;
        for (String p : physicalPhotos) if (p != null) count++;
        return count;
    }
}
