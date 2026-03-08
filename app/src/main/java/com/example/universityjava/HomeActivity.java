package com.example.universityjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    Button _buttonCreateListing;
    Button _buttonPopular;
    Button _buttonNewest;
    Button _buttonUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        _buttonCreateListing = (Button) findViewById(R.id.buttonCreateListing);
        _buttonCreateListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
//                startActivity(intent);
            }
        });

        _buttonPopular = (Button) findViewById(R.id.buttonPopular);
        _buttonPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MainCategoriesActivity.class);
                intent.putExtra("Data", "Populiariausios prekės");
                startActivity(intent);
            }
        });

        _buttonNewest = (Button) findViewById(R.id.buttonNewest);
        _buttonNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MainCategoriesActivity.class);
                intent.putExtra("Data", "Naujausios prekės");
                startActivity(intent);
            }
        });

        _buttonUserProfile = (Button) findViewById(R.id.buttonUserProfile);
        _buttonUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), UserProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}