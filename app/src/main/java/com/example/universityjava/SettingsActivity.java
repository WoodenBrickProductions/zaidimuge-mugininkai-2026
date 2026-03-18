package com.example.universityjava;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class SettingsActivity extends NavigationActivity {

    AppDatabase db;
    ScrollView scrollView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppActivity.getDatabase();
        List<User> userList = db.userDAO().getAllUsers();
        scrollView = findViewById(R.id.scrollView);
        textView = findViewById(R.id.textView);
        for (User user : userList) {
            textView.append(user.getName() + " " + user.getEmail() + "\n");
        }

        setupNavigation(4);
    }
}