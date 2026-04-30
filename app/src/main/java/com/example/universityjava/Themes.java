package com.example.universityjava;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class Themes {
    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (!prefs.contains("theme_mode")) {
            prefs.edit().putString("theme_mode", "light").apply();
        }

        String theme = prefs.getString("theme_mode", "light");
        int mode = "dark".equals(theme) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;

        // Only apply if different
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode);
        }
    }
}
