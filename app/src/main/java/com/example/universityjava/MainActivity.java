package com.example.universityjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
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
    Button _button;
    Button _fragmentTestingButton;
    BottomNavigationView _bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(new HomeFragment());

        db = AppActivity.getDatabase();
        _button = (Button) findViewById(R.id.cartButton);
        _fragmentTestingButton = (Button) findViewById(R.id.fragmentTestingButton);
        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*User user = new User();
                user.setName("Cheesy");
                user.setEmail("CheesyMail");
                user.setPassword("Password");
                db.userDAO().insert(user);*/


                System.out.println("OnClick");
                Intent intent = new Intent(getBaseContext(), SecondWindow.class);
                intent.putExtra("Data", "Hello World");
                startActivity(intent);
            }
        });

        _fragmentTestingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //db.close();
                //deleteDatabase("my_app_db");
                /*User user = new User();
                user.setName("Cheesy");
                user.setEmail("CheesyMail");
                user.setPassword("Password");
                db.userDAO().insert(user);*/
                //List<User> userList = db.userDAO().getAllUsers();
                //User u = userList.get(0);
                //Toast.makeText(getApplicationContext(), u.getName() + u.getEmail(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();

                Intent fragIntent = new Intent(getBaseContext(), FragmentTestingActivity.class);
                //fragIntent.putExtra("UserID",u.getId());
                startActivity(fragIntent);
            }
        });
//        _button = (Button) findViewById(R.id.button);

        _bottomNavigationView = findViewById(R.id.bottomNavigationView);
        _bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.navigation_home:
//                        replaceFragment(new HomeFragment());
//                        break;
//
//                }
                int itemId = item.getItemId();

                // switch statement doesn't want to work with R.id, if-else it is then
                if (itemId == R.id.navigation_home) {
                    replaceFragment(new HomeFragment());
                }
                else if (itemId == R.id.navigation_cart) {
                    replaceFragment(new HomeFragment());
                }
                else if (itemId == R.id.navigation_favorites) {
                    replaceFragment(new HomeFragment());
                }
                else if (itemId == R.id.navigation_profile) {
                    replaceFragment(new ProfileFragment());
                }
                else if (itemId == R.id.navigation_settings) {
                    replaceFragment(new SettingsFragment());
                }
                return true;
            }
        });

//        _button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("OnClick");
//                Intent intent = new Intent(getBaseContext(), SecondWindow.class);
//                intent.putExtra("Data", "Hello World");
//                startActivity(intent);
//            }
//        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutMain, fragment);
        fragmentTransaction.commit();
    }
}