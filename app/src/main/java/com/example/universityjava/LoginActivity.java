package com.example.universityjava;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private AppDatabase db;
    private EditText _editTextUsername;
    private EditText _editTextPassword;
    private Button _buttonLogin;
    private Button _buttonRegister;
    private Button _buttonGuest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppActivity.getDatabase();
        _editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        _editTextPassword = (EditText) findViewById(R.id.editTextTextPassword);
        _buttonLogin = (Button) findViewById(R.id.buttonLogin);
        _buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = _editTextUsername.getText().toString().trim();
                String password = _editTextPassword.getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Name and Password must be entered", Toast.LENGTH_SHORT).show();
                }
                else {
                    List<User> userList = db.userDAO().getUserByName(name);
                    if (userList.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "User with this username doesn't exist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    User user = userList.get(0);
                    if (!TextUtils.equals(password, user.getPassword())) {
                        Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Welcome, " + name, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("UserID", user.getId());
                    startActivity(intent);
                }
            }
        });
        _buttonRegister = (Button) findViewById(R.id.buttonRegister);
        _buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        _buttonGuest = (Button) findViewById(R.id.buttonGuest);
        _buttonGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}