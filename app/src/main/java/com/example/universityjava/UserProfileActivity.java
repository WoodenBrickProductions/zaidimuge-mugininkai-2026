package com.example.universityjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserProfileActivity extends NavigationActivity {

    AppDatabase db;
    TextView _textViewProfileUsername;
    Button _buttonMyListings;
    Button _buttonMyHistory;
    Button _buttonLogOut;
    long userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupNavigation(3);

        db = AppActivity.getDatabase();
        userID = getIntent().getLongExtra("UserID", -1);

        // verification
        if (userID < 0) {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }

        User user = db.userDAO().getUserByID(userID);
        _textViewProfileUsername = (TextView) findViewById(R.id.textViewProfileUsername);
        _textViewProfileUsername.setText(user.getName());
        _buttonMyListings = (Button) findViewById(R.id.buttonMyListings);
        _buttonMyListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
//                startActivity(intent);
            }
        });

        _buttonMyHistory = (Button) findViewById(R.id.buttonMyHistory);
        _buttonMyHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
//                startActivity(intent);
            }
        });

        _buttonLogOut = (Button) findViewById(R.id.buttonLogOut);
        _buttonLogOut.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_favorite_border_24, 0,0,0);

        _buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}