package com.example.universityjava;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class SettingsFragment extends Fragment {
    public SettingsFragment() {
        // Required empty public constructor
    }

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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("settings", MODE_PRIVATE);
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
                }
                else if (TextUtils.equals(adapterView.getItemAtPosition(i).toString(), "English")
                         && !TextUtils.equals(prefs.getString("app_lang", "en-US"), "en-US")) {
                    editor.putString("app_lang", "en-US");
                    editor.apply();
                    LocaleListCompat appLocale = LocaleListCompat.forLanguageTags("en-US");
                    AppCompatDelegate.setApplicationLocales(appLocale);
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