package com.example.universityjava;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.universityjava.database.Condition;
import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.PhysicalListingAttributes;

import java.io.File;

public class EditListingFragment extends Fragment {

    private static final String ARG_LISTING_ID = "listing_id";

    private long listingId;
    private Listing listing;
    private Game game;

    private final String[] physicalPhotos = new String[3];
    private int photoSlot = 0;
    private final String photoPrefix = "phys_edit_" + System.currentTimeMillis();

    private ImageView editIconPreview;
    private Button buttonChangePhoto;

    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;

    public EditListingFragment() {}

    public static EditListingFragment newInstance(long listingId) {
        EditListingFragment f = new EditListingFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_LISTING_ID, listingId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) listingId = getArguments().getLong(ARG_LISTING_ID);

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) launchCamera();
            else Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (!success) return;
            String name = photoPrefix + "_" + (photoSlot + 1);
            physicalPhotos[photoSlot] = name;
            if (photoSlot < 2) photoSlot++;
            buttonChangePhoto.setText("Add photo (" + countPhotos() + "/3)");
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_listing, container, false);

        view.findViewById(R.id.buttonBackEditListing).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        listing = AppActivity.getDatabase().listingDAO().getListingByID(listingId);
        game = AppActivity.getDatabase().gameDAO().getGameByID(listing.getFk_gameid());

        // Pre-load existing photos so unchanged ones are preserved on save
        physicalPhotos[0] = listing.getPhysicalPhoto1();
        physicalPhotos[1] = listing.getPhysicalPhoto2();
        physicalPhotos[2] = listing.getPhysicalPhoto3();
        // Start adding from the first empty slot
        for (int i = 0; i < 3; i++) {
            if (physicalPhotos[i] == null) { photoSlot = i; break; }
        }

        // Read-only info
        ((TextView) view.findViewById(R.id.editGameName)).setText(game.getTitle());
        String platformName = AppActivity.getDatabase().listingDAO().getPlatformNameByListingId(listingId);
        ((TextView) view.findViewById(R.id.editPlatform)).setText(platformName != null ? platformName : "");

        // Icon preview
        editIconPreview = view.findViewById(R.id.editIconPreview);
        File iconFile = AppActivity.getCachedImageFile(requireContext(), game.getImage());
        if (iconFile != null) editIconPreview.setImageBitmap(BitmapFactory.decodeFile(iconFile.getAbsolutePath()));

        // Change icon
        view.findViewById(R.id.buttonChangeIcon).setOnClickListener(v -> {
            MainActivity.gotFileCallback = file -> {
                AppActivity.savePickedImageToCache(getContext(), Uri.fromFile(file), game.getTitle());
                Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
                editIconPreview.setImageBitmap(bm);
            };
            MainActivity.imageLauncher.launch("image/*");
        });

        // Price
        EditText priceField = view.findViewById(R.id.editPrice);
        priceField.setText(String.valueOf(listing.getPrice()));

        // Physical-only fields
        EditText descField = view.findViewById(R.id.editConditionDescription);
        buttonChangePhoto = view.findViewById(R.id.buttonChangePhoto);

        if (!listing.getIsdigital()) {
            descField.setVisibility(View.VISIBLE);
            buttonChangePhoto.setVisibility(View.VISIBLE);
            buttonChangePhoto.setText("Add photo (" + countPhotos() + "/3)");

            PhysicalListingAttributes attrs = AppActivity.getDatabase()
                    .physicalListingAttributesDAO().getPhysAttrByListingId(listingId);
            if (attrs != null && attrs.getCondition_description() != null) {
                descField.setText(attrs.getCondition_description());
            }

            buttonChangePhoto.setOnClickListener(v -> {
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
        }

        // Save
        view.findViewById(R.id.buttonSave).setOnClickListener(v -> {
            String priceStr = priceField.getText().toString().trim().replace(',', '.');
            double price;
            try {
                price = Math.round(Double.parseDouble(priceStr) * 100.0) / 100.0;
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(),
                        "Invalid price — use digits with an optional . or , for decimals",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            AppActivity.getDatabase().listingDAO()
                    .updateFields(listingId, price, physicalPhotos[0], physicalPhotos[1], physicalPhotos[2]);

            if (!listing.getIsdigital()) {
                String desc = descField.getText().toString();
                PhysicalListingAttributes attrs = AppActivity.getDatabase()
                        .physicalListingAttributesDAO().getPhysAttrByListingId(listingId);
                if (attrs != null) {
                    AppActivity.getDatabase().physicalListingAttributesDAO().updateDescription(listingId, desc);
                } else {
                    PhysicalListingAttributes newAttrs = new PhysicalListingAttributes();
                    newAttrs.setFk_listingid(listingId);
                    newAttrs.setFk_condition(Condition.New);
                    newAttrs.setCondition_description(desc);
                    AppActivity.getDatabase().physicalListingAttributesDAO().insert(newAttrs);
                }
            }

            Toast.makeText(requireContext(), "Listing updated!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

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
