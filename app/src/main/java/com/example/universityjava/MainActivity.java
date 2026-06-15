package com.example.universityjava;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements ItemRecyclerViewEvent {

    public interface GotFileCallback {
        public void gotFile(File file);
    }
    public static ActivityResultLauncher<String> imageLauncher;
    public static GotFileCallback gotFileCallback;
    private AppDatabase db;
    //Button _button;
    BottomNavigationView _bottomNavigationView;
    BannerGyroController gyroController;
    OverlayController overlayController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
            setRootFragment(new HomeFragment());

        db = AppActivity.getDatabase();
        imageLauncher = AppActivity.registerImagePickerLauncher(this, "my banner",
                file -> {
                    if (file != null) {
                           System.out.println("Cache Saved to: " + file.getAbsolutePath());
                           gotFileCallback.gotFile(file);
                           gotFileCallback = null;
                    }
                });

        long userID = AppActivity.getCurrentUserID();

        _bottomNavigationView = findViewById(R.id.bottomNavigationView);
        _bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.menu_color_state_list));
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

                // Animation
                View clickedView = getMenuItemView(itemId);
                if (clickedView == null)
                    return true;
                Animator iconAnimator = AnimatorInflater.loadAnimator(getBaseContext(), R.animator.navigation_icon_click);
                iconAnimator.setTarget(clickedView);
                if (AnimationSettings.areAnimationsEnabled())
                    iconAnimator.start();

                return true;
            }
        });

        gyroController = new BannerGyroController(this);

        View contentView = findViewById(R.id.frameLayoutMain);
        View overlayView = findViewById(R.id.lightOverlay);
        overlayController = new OverlayController(this, contentView, overlayView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gyroController.start();
        if (getOverlaySetting())
            overlayController.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gyroController.stop();
        overlayController.stop();
    }

    private View getMenuItemView(int itemId) {
        ViewGroup group = (ViewGroup) _bottomNavigationView.getChildAt(0);

        for (int i = 0; i < group.getChildCount(); i++) {
            if (_bottomNavigationView.getMenu().getItem(i).getItemId() == itemId) {
                return group.getChildAt(i);
            }
        }
        return null;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (AnimationSettings.areAnimationsEnabled())
            fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            );
        fragmentTransaction.replace(R.id.frameLayoutMain, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void setRootFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayoutMain, fragment);
        transaction.commit();
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
        replaceFragment(EditListingFragment.newInstance(item.getId()));
    }

    public void updateOverlaySetting() {
        boolean isEnabled = getOverlaySetting();
        if (isEnabled)
            overlayController.start();
        else {
            overlayController.stop();
            overlayController.reset();
        }
    }

    public boolean getOverlaySetting() {
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        return prefs.getBoolean("auto_brightness", false);
    }
}