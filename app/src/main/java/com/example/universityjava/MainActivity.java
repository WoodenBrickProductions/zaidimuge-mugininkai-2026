package com.example.universityjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.universityjava.database.Condition;
import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.PhysicalListingAttributes;
import com.example.universityjava.database.Platform;

import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    //Button _button;
    BottomNavigationView _bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(new HomeFragment());

        db = AppActivity.getDatabase();

        long userID = AppActivity.getCurrentUserID();

        _bottomNavigationView = findViewById(R.id.bottomNavigationView);
        _bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                // switch statement doesn't want to work with R.id, if-else it is then
                if (itemId == R.id.navigation_home) {
                    replaceFragment(new HomeFragment());
                }
                else if (itemId == R.id.navigation_settings) {
                    replaceFragment(new SettingsFragment());
                }
                else if (userID < 0) {
                    logOut();
                }
                else if (itemId == R.id.navigation_cart) {
                    replaceFragment(new CartFragment());
                }
                else if (itemId == R.id.navigation_favorites) {
                    replaceFragment(new WishlistFragment());
                }
                else if (itemId == R.id.navigation_profile) {
                    replaceFragment(new ProfileFragment());
                }

                return true;
            }
        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutMain, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void logOut() {
        startActivity(new Intent(getBaseContext(), LoginActivity.class));
    }
}