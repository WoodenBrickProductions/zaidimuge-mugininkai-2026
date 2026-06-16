package com.example.universityjava;

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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.universityjava.database.Game;

import java.io.File;

public class AddGameFragment extends Fragment {

    public static final String REQUEST_KEY = "add_game_result";
    public static final String RESULT_GAME_ID = "game_id";

    private ImageView imagePreview;
    private File pickedImageFile;

    public AddGameFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_game, container, false);

        EditText nameField = view.findViewById(R.id.addGameName);
        EditText descField = view.findViewById(R.id.addGameDescription);
        imagePreview = view.findViewById(R.id.addGameImagePreview);

        view.findViewById(R.id.buttonPickGameImage).setOnClickListener(v -> {
            MainActivity.gotFileCallback = file -> {
                pickedImageFile = file;
                Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
                imagePreview.setImageBitmap(bm);
            };
            MainActivity.imageLauncher.launch("image/*");
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
                game.setImage(name); // no cached file — will show placeholder
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
}
