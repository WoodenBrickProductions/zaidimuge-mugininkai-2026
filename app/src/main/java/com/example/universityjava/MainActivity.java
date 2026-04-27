package com.example.universityjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements ItemRecyclerViewEvent {

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

    @Override
    public void onItemClick(Listing item) {
        Fragment page = ListingPageFragment.newInstance(item.getId(), "");
        replaceFragment(page);
        Toast.makeText(getBaseContext(), "Listing clicked ID: " + item.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Game item) {
        //Fragment page = ListingPageFragment.newInstance(item.getId(), "");
        //replaceFragment(page);
        Toast.makeText(getBaseContext(), "Game clicked ID: " + item.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Listing item) {
        Toast.makeText(getBaseContext(), "Edit clicked: " + item.getId(), Toast.LENGTH_SHORT).show();
    }
}