package com.example.universityjava;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.universityjava.database.Condition;
import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.PhysicalListingAttributes;
import com.example.universityjava.database.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddListingFragment extends Fragment {

    private static final String ADD_NEW_GAME = "+ Add new game";

    ImageView imageView;
    boolean _isDigital = true;
    int _platform;
    Condition _condition = Condition.New;

    private long selectedGameId = -1;
    private List<Game> gameList = new ArrayList<>();

    private AutoCompleteTextView gameDropdown;
    private ImageView selectedGameIcon;
    private ArrayAdapter<String> gameAdapter;

    private int photoSlot = 0;
    private final String[] physicalPhotos = new String[3];
    private String photoPrefix;

    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private Button buttonAddPhoto;

    public AddListingFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoPrefix = "phys_" + System.currentTimeMillis();

        // Receive the newly created game back from AddGameFragment
        getParentFragmentManager().setFragmentResultListener(
                AddGameFragment.REQUEST_KEY, this, (requestKey, result) -> {
                    long newGameId = result.getLong(AddGameFragment.RESULT_GAME_ID);
                    Game newGame = AppActivity.getDatabase().gameDAO().getGameByID(newGameId);
                    if (newGame == null) return;
                    gameList.add(newGame);
                    selectedGameId = newGameId;
                    // Rebuild adapter so the filter's title list stays in sync
                    gameAdapter = buildGameAdapter();
                    gameDropdown.setAdapter(gameAdapter);
                    gameDropdown.setText(newGame.getTitle(), false);
                    gameDropdown.setVisibility(VISIBLE);
                    showGameIcon(selectedGameIcon, newGame);
                });

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

        gameList = AppActivity.getDatabase().gameDAO().getAllGames();

        gameDropdown = view.findViewById(R.id.gameDropdown);
        selectedGameIcon = view.findViewById(R.id.selectedGameIcon);

        gameAdapter = buildGameAdapter();
        gameDropdown.setAdapter(gameAdapter);
        gameDropdown.setThreshold(0);
        gameDropdown.setOnTouchListener((v, e) -> { gameDropdown.showDropDown(); return false; });

        gameDropdown.setOnItemClickListener((parent, v, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            if (ADD_NEW_GAME.equals(selected)) {
                gameDropdown.setText("", false);
                ((MainActivity) requireActivity()).replaceFragment(new AddGameFragment());
            } else {
                for (Game g : gameList) {
                    if (g.getTitle().equals(selected)) {
                        selectedGameId = g.getId();
                        showGameIcon(selectedGameIcon, g);
                        break;
                    }
                }
            }
        });

        imageView = view.findViewById(R.id.gameImage);
        EditText conditionDescription = view.findViewById(R.id.condition_description);
        CustomDropdown conditionDropdown = view.findViewById(R.id.dropdownCondition);
        Button buttonImage = view.findViewById(R.id.buttonImage);
        LinearLayout physicalImageButtons = view.findViewById(R.id.physicalImageButtons);
        Button buttonAddIcon = view.findViewById(R.id.buttonAddIcon);
        buttonAddPhoto = view.findViewById(R.id.buttonAddPhoto);
        view.findViewById(R.id.buttonSecond).setVisibility(GONE);

        Animator anim = AnimatorInflater.loadAnimator(getContext(), R.animator.overshoot_bounce);
        anim.setTarget(buttonImage);

        View.OnClickListener iconPickerClick = v -> {
            anim.start();
            MainActivity.gotFileCallback = file -> {
                String title = getCurrentGameTitle();
                AppActivity.savePickedImageToCache(getContext(), Uri.fromFile(file), title);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                selectedGameIcon.setImageBitmap(bitmap);
                selectedGameIcon.setVisibility(VISIBLE);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f);
                scaleX.setDuration(500);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 0f, 1f);
                scaleY.setDuration(500);
                AnimatorSet seq = new AnimatorSet();
                seq.playTogether(scaleX, scaleY);
                seq.start();
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

        LinearLayout conditionContainer = view.findViewById(R.id.condition_container);

        CustomDropdown typeDropdown = view.findViewById(R.id.dropdownType);
        typeDropdown.setItems(new String[]{getString(R.string.type_digital), getString(R.string.type_physical)},
                new String[]{"DIG", "PHY"});
        typeDropdown.setSelectedValue("DIG");
        typeDropdown.setOnValueChanged(type -> {
            _isDigital = "DIG".equals(type);
            buttonImage.setVisibility(_isDigital ? VISIBLE : GONE);
            physicalImageButtons.setVisibility(_isDigital ? GONE : VISIBLE);
            conditionContainer.setVisibility(_isDigital ? GONE : VISIBLE);
            conditionDescription.setVisibility(_isDigital ? GONE : VISIBLE);
            _condition = Condition.New;
        });

        List<Platform> platforms = AppActivity.getDatabase().platformDAO().getAllPlatforms();
        String[] platLabels = new String[platforms.size()];
        String[] platValues = new String[platforms.size()];
        for (int i = 0; i < platforms.size(); i++) {
            platLabels[i] = platforms.get(i).getName();
            platValues[i] = String.valueOf(platforms.get(i).getId());
        }
        if (!platforms.isEmpty()) _platform = platforms.get(0).getId();

        CustomDropdown platformDropdown = view.findViewById(R.id.dropdownPlatform);
        platformDropdown.setItems(platLabels, platValues);
        if (platValues.length > 0) platformDropdown.setSelectedValue(platValues[0]);
        platformDropdown.setOnValueChanged(p -> _platform = Integer.parseInt(p));

        conditionDropdown.setItems(new String[]{"New", "Used"}, new String[]{"NEW", "USED"});
        conditionDropdown.setSelectedValue("NEW");
        conditionDropdown.setOnValueChanged(c -> {
            _condition = "NEW".equals(c) ? Condition.New : Condition.Good;
        });

        EditText addListingPrice = view.findViewById(R.id.addListingPrice);
        view.findViewById(R.id.buttonSubmit).setOnClickListener(v -> {
            // Validate game selection
            if (selectedGameId < 0) {
                Toast.makeText(requireContext(), "Please select a game", Toast.LENGTH_SHORT).show();
                return;
            }
            Game game = AppActivity.getDatabase().gameDAO().getGameByID(selectedGameId);

            String priceStr = addListingPrice.getText().toString().trim().replace(',', '.');
            double price;
            try {
                price = Double.parseDouble(priceStr);
                price = Math.round(price * 100.0) / 100.0;
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(),
                        "Invalid price — use digits with an optional . or , for decimals",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            var listing = new Listing();
            listing.setFk_gameid(game.getId());
            listing.setFk_seller(AppActivity.getCurrentUserID());
            listing.setPrice(price);
            listing.setFk_platform(_platform);
            listing.setIsdigital(_isDigital);
            listing.setPhysicalPhoto1(physicalPhotos[0]);
            listing.setPhysicalPhoto2(physicalPhotos[1]);
            listing.setPhysicalPhoto3(physicalPhotos[2]);

            listing.setId(AppActivity.getDatabase().listingDAO().insert(listing));

            if (!_isDigital) {
                PhysicalListingAttributes attrs = new PhysicalListingAttributes();
                attrs.setFk_listingid(listing.getId());
                attrs.setFk_condition(_condition);
                attrs.setCondition_description(conditionDescription.getText().toString());
                AppActivity.getDatabase().physicalListingAttributesDAO().insert(attrs);
            }

            Toast.makeText(requireContext(), "Listing added successfully!", Toast.LENGTH_SHORT).show();

            if (AppActivity.getCachedImageFile(requireContext(), game.getImage()) == null) {
                String firstPhoto = null;
                for (String p : physicalPhotos) {
                    if (p != null) { firstPhoto = p; break; }
                }
                if (firstPhoto != null) {
                    game.setImage(firstPhoto);
                    AppActivity.getDatabase().gameDAO().update(game);
                }
            }

            ((MainActivity) requireActivity()).replaceFragment(new HomeFragment());
        });

        return view;
    }

    private String getCurrentGameTitle() {
        for (Game g : gameList) {
            if (g.getId() == selectedGameId) return g.getTitle();
        }
        return "";
    }

    private ArrayAdapter<String> buildGameAdapter() {
        List<String> titles = new ArrayList<>();
        for (Game g : gameList) titles.add(g.getTitle());

        List<String> initialList = new ArrayList<>(titles);
        initialList.add(ADD_NEW_GAME);

        return new ArrayAdapter<String>(requireContext(),
                R.layout.dropdown_game_item, R.id.game_title, initialList) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                String title = getItem(position);
                ImageView icon = row.findViewById(R.id.game_icon);
                // Find the matching game to load its icon
                Game matched = null;
                for (Game g : gameList) {
                    if (g.getTitle().equals(title)) { matched = g; break; }
                }
                if (matched != null) {
                    File f = AppActivity.getCachedImageFile(requireContext(), matched.getImage());
                    if (f != null) {
                        Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath());
                        icon.setImageBitmap(bm);
                    } else {
                        icon.setImageResource(R.drawable.ic_launcher_background);
                    }
                } else {
                    icon.setImageDrawable(null);
                }
                return row;
            }

            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        List<String> filtered = new ArrayList<>();
                        if (constraint == null || constraint.length() == 0) {
                            filtered.addAll(titles);
                        } else {
                            String q = constraint.toString().toLowerCase();
                            for (String t : titles) {
                                if (t.toLowerCase().contains(q)) filtered.add(t);
                            }
                        }
                        filtered.add(ADD_NEW_GAME);
                        results.values = filtered;
                        results.count = filtered.size();
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        clear();
                        addAll((List<String>) results.values);
                        notifyDataSetChanged();
                    }

                    @Override
                    public CharSequence convertResultToString(Object resultValue) {
                        return (String) resultValue;
                    }
                };
            }
        };
    }

    private void showGameIcon(ImageView iconView, Game game) {
        File f = AppActivity.getCachedImageFile(requireContext(), game.getImage());
        if (f != null) {
            iconView.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
        } else {
            iconView.setImageResource(R.drawable.ic_launcher_background);
        }
        iconView.setVisibility(VISIBLE);
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
