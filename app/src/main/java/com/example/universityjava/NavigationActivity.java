package com.example.universityjava;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationActivity extends AppCompatActivity {
    protected void setupNavigation(int currentSelection) {
        BottomNavigationView _bottomNavigation = findViewById(R.id.bottomNavigationView);
        int currentSelectionId;
        switch (currentSelection) {
            case 0:
                currentSelectionId = R.id.navigation_home;
                break;
            case 1:
                currentSelectionId = R.id.navigation_cart;
                break;
            case 2:
                currentSelectionId = R.id.navigation_favorites;
                break;
            case 3:
                currentSelectionId = R.id.navigation_profile;
                break;
            case 4:
                currentSelectionId = R.id.navigation_settings;
                break;
            default:
                currentSelectionId = R.id.navigation_home;
        }

        _bottomNavigation.getMenu().findItem(currentSelectionId).setChecked(true);
        _bottomNavigation.setOnItemSelectedListener(item -> {
            Intent navigationIntent;
            int itemId = item.getItemId();

            // switch statement doesn't want to work with R.id, if-else it is then
            if (itemId == R.id.navigation_home) {
                navigationIntent = new Intent(getBaseContext(), HomeActivity.class);
                switchActivity(navigationIntent);
            }
            else if (itemId == R.id.navigation_cart) {
                navigationIntent = new Intent(getBaseContext(), HomeActivity.class);
                switchActivity(navigationIntent);
            }
            else if (itemId == R.id.navigation_favorites) {
                navigationIntent = new Intent(getBaseContext(), HomeActivity.class);
                switchActivity(navigationIntent);
            }
            else if (itemId == R.id.navigation_profile) {
                navigationIntent = new Intent(getBaseContext(), UserProfileActivity.class);
                switchActivity(navigationIntent);
            }
            else if (itemId == R.id.navigation_settings) {
                navigationIntent = new Intent(getBaseContext(), SettingsActivity.class);
                switchActivity(navigationIntent);
            }
            return true;
        });
    }
    public void switchActivity(Intent intent) {
        intent.putExtra("UserID", getIntent().getLongExtra("UserID", -1));
        startActivity(intent);
    }
}
