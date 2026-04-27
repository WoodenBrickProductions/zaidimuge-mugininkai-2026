package com.example.universityjava;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;

public class EditListingFragment extends Fragment {
    public EditListingFragment() {
        // Required empty public constructor
    }

    EditText addListingGame;
    EditText addListingPrice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_listing, container, false);

        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);
        addListingGame = view.findViewById(R.id.addListingGame);
        addListingPrice = view.findViewById(R.id.addListingPrice);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                var dao = AppActivity.getDatabase().gameDAO();
                Game game;
                var gameList = dao.getGameByName(addListingGame.getText().toString());
                if(gameList.size() > 0)
                {
                    game = gameList.get(0);
                }
                else
                {
                    game = new Game();
                    game.setTitle(addListingGame.getText().toString());
                    game.setId(AppActivity.getDatabase().gameDAO().insert(game));
                }

                SharedPreferences prefs = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);

                var listing = new Listing();
                listing.setFk_gameid(game.getId());
                listing.setFk_seller(AppActivity.getCurrentUserID());
                listing.setPrice(Double.parseDouble(addListingPrice.getText().toString()));
                final int DUMMY_ID = 100000; // TODO(Woody): implement
                listing.setFk_platform(DUMMY_ID);

                listing.setId(AppActivity.getDatabase().listingDAO().insert(listing));
                var fragment = ListingPageFragment.newInstance(listing.getId(), "");
                ((MainActivity)getActivity()).replaceFragment(new HomeFragment());
            }
        });

        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Spinner languageSpinner = view.findViewById(R.id.spinnerLanguage);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (TextUtils.equals(adapterView.getItemAtPosition(i).toString(), "Lietuvių")
                        && !TextUtils.equals(prefs.getString("app_lang", "en-US"), "lt-LT")) {
                    editor.putString("app_lang", "lt-LT");
                    editor.apply();
                    LocaleListCompat appLocale = LocaleListCompat.forLanguageTags("lt-LT");
                    AppCompatDelegate.setApplicationLocales(appLocale);
//                    ((MainActivity)getActivity()).replaceFragment(new SettingsFragment());
                }
                else if (TextUtils.equals(adapterView.getItemAtPosition(i).toString(), "English")
                        && !TextUtils.equals(prefs.getString("app_lang", "en-US"), "en-US")) {
                    editor.putString("app_lang", "en-US");
                    editor.apply();
                    LocaleListCompat appLocale = LocaleListCompat.forLanguageTags("en-US");
                    AppCompatDelegate.setApplicationLocales(appLocale);
//                    ((MainActivity)getActivity()).replaceFragment(new SettingsFragment());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.languages_array, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);

        String selection = "";

        switch (prefs.getString("app_lang", "en-US")) {
            case "lt-LT":
                selection = "Lietuvių";
                break;
            case "en-US":
                selection = "English";
                break;
            default:
                selection = "Lietuvių";
        }
        int spinnerPosition = languageAdapter.getPosition(selection);
        languageSpinner.setSelection(spinnerPosition);


        Spinner themeSpinner = view.findViewById(R.id.spinnerTheme);
        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.themes_array, android.R.layout.simple_spinner_item);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(themeAdapter);

        return view;
    }
}