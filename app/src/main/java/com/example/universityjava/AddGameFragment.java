package com.example.universityjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.universityjava.database.Game;

import java.io.File;

public class AddGameFragment extends Fragment {

    public static final String REQUEST_KEY = "add_game_result";
    public static final String RESULT_GAME_ID = "game_id";

    private EditText nameField;
    private EditText descField;
    private ImageView imagePreview;
    private File pickedImageFile;

    public AddGameFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register here (with the Fragment lifecycle, not the view lifecycle) so the listener
        // stays alive while SteamSearchFragment is on top and AddGameFragment has no view.
        getParentFragmentManager().setFragmentResultListener(
                SteamSearchFragment.REQUEST_KEY, this, (key, result) -> {
                    String steamName = result.getString(SteamSearchFragment.RESULT_NAME, "");
                    String steamDesc = result.getString(SteamSearchFragment.RESULT_DESCRIPTION, "");
                    String imageUrl  = result.getString(SteamSearchFragment.RESULT_HEADER_URL, "");

                    if (nameField != null) nameField.setText(steamName);
                    if (descField != null) descField.setText(steamDesc);

                    if (!imageUrl.isEmpty() && imagePreview != null) {
                        File cacheDir = new File(requireContext().getCacheDir(), "game_icons");
                        cacheDir.mkdirs();
                        File tempFile = new File(cacheDir,
                                "steam_temp_" + System.currentTimeMillis() + ".jpg");
                        ImageManager.downloadUrlToFile(imageUrl, tempFile, () -> {
                            if (!isAdded()) return;
                            pickedImageFile = tempFile;
                            Bitmap bm = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                            if (bm != null && imagePreview != null) imagePreview.setImageBitmap(bm);
                        });
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_game, container, false);

        nameField    = view.findViewById(R.id.addGameName);
        descField    = view.findViewById(R.id.addGameDescription);
        imagePreview = view.findViewById(R.id.addGameImagePreview);

        view.findViewById(R.id.buttonPickGameImage).setOnClickListener(v -> {
            MainActivity.gotFileCallback = file -> {
                pickedImageFile = file;
                Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
                imagePreview.setImageBitmap(bm);
            };
            MainActivity.imageLauncher.launch("image/*");
        });

        view.findViewById(R.id.buttonFindOnSteam).setOnClickListener(v -> {
            String query = nameField.getText().toString().trim();
            ((MainActivity) requireActivity()).replaceFragment(
                    SteamSearchFragment.newInstance(query));
        });

        view.findViewById(R.id.buttonBackAddGame).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        view.findViewById(R.id.buttonSaveGame).setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            if (name.isEmpty()) {
                nameField.setError(getString(R.string.hint_game_name));
                return;
            }
            if (!AppActivity.getDatabase().gameDAO().getGameByName(name).isEmpty()) {
                nameField.setError("A game with this name already exists");
                return;
            }

            Game game = new Game();
            game.setTitle(name);
            game.setDescription(descField.getText().toString().trim());

            if (pickedImageFile != null) {
                AppActivity.savePickedImageToCache(getContext(), Uri.fromFile(pickedImageFile), name);
                game.setImage(name);
            } else {
                game.setImage(name);
            }

            long id = AppActivity.getDatabase().gameDAO().insert(game);

            Bundle result = new Bundle();
            result.putLong(RESULT_GAME_ID, id);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);

            Toast.makeText(requireContext(), getString(R.string.game_added_success), Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear view references so the onCreate listener doesn't hold stale views.
        nameField    = null;
        descField    = null;
        imagePreview = null;
    }
}
