package com.example.universityjava;

import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
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

        Button buttonAbout = view.findViewById(R.id.buttonAbout);
        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragmentAbout = new AppInformationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", "Lorem ipsum");
                fragmentAbout.setArguments(bundle);
                ((MainActivity)getActivity()).replaceFragment(fragmentAbout);
            }
        });

        SharedPreferences prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);


        CustomDropdown dropdown = view.findViewById(R.id.languageDropdown);

        String[] labels = {"Lietuvių", "English"};
        String[] values = {"lt-LT", "en-US"};

        dropdown.setItems(labels, values);
        dropdown.setSelectedValue(prefs.getString("app_lang", "en-US"));
        dropdown.setOnValueChanged(lang -> {
            if (!lang.equals(prefs.getString("app_lang", "en-US"))) {

                prefs.edit().putString("app_lang", lang).apply();

                LocaleListCompat locale =
                        LocaleListCompat.forLanguageTags(lang);

                AppCompatDelegate.setApplicationLocales(locale);
            }
        });

        CustomDropdown themeDropdown = view.findViewById(R.id.themeDropdown);

        String[] themeLabels = {
            getContext().getString(R.string.theme_dark),
            getContext().getString(R.string.theme_light)};
        String[] themeValues = {"dark", "light"};

        themeDropdown.setItems(themeLabels, themeValues);
        themeDropdown.setSelectedValue(prefs.getString("theme_mode", "light"));

        themeDropdown.setOnValueChanged(theme -> {
            if (!theme.equals(prefs.getString("theme_mode", "light"))) {

                prefs.edit().putString("theme_mode", theme).apply();

                if (theme.equals("light"))
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        Switch brightnessSwitch = view.findViewById(R.id.switchBrightness);
        brightnessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean("auto_brightness", b).apply();
                ((MainActivity) requireActivity()).updateOverlaySetting();
            }
        });

        TextView banner = view.findViewById(R.id.bannerSettings);
        ((MainActivity) requireActivity()).gyroController.addBanner(banner);

        return view;
    }
}