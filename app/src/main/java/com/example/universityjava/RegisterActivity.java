package com.example.universityjava;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private AppDatabase db;
    private Button _buttonRegisterConfirm;
    private EditText _editTextRegisterUsername;
    private EditText _editTextRegisterEmail;
    private EditText _editTextRegisterPassword;
    private EditText _editTextRegisterPasswordRepeat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppActivity.getDatabase();
        _editTextRegisterUsername = (EditText) findViewById(R.id.editTextRegisterUsername);
        _editTextRegisterEmail = (EditText) findViewById(R.id.editTextRegisterEmailAddress);
        _editTextRegisterPassword = (EditText) findViewById(R.id.editTextRegisterPassword);
        _editTextRegisterPasswordRepeat = (EditText) findViewById(R.id.editTextRegisterPasswordRepeat);
        _buttonRegisterConfirm = (Button) findViewById(R.id.buttonRegisterConfirm);
        _buttonRegisterConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = _editTextRegisterUsername.getText().toString().trim();
                String email = _editTextRegisterEmail.getText().toString().trim();
                String password = _editTextRegisterPassword.getText().toString().trim();
                String passwordRepeat = _editTextRegisterPasswordRepeat.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordRepeat)) {
                    Toast.makeText(getApplicationContext(), R.string.toast_missing_fields, Toast.LENGTH_SHORT).show();
                }
                else if (!TextUtils.equals(password, passwordRepeat)) {
                    Toast.makeText(getApplicationContext(), R.string.toast_password_mismatch, Toast.LENGTH_SHORT).show();
                }
                else {
                    User user = new User();
                    user.setName(username);
                    user.setEmail(email);
//                    user.setPassword(password);
                    try {
                        user.setPassword(PasswordHashing.hashPassword(password));
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    db.userDAO().insert(user);
                    Toast.makeText(getApplicationContext(), R.string.toast_registration_successful, Toast.LENGTH_SHORT).show();

                    SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("user_id", db.userDAO().getUserByName(username).get(0).getId());
                    editor.apply();

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });


    }
}