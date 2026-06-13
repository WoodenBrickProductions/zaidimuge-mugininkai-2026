package com.example.universityjava;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Random;

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

        Animator anim = AnimatorInflater.loadAnimator(this, R.animator.button_click_failed);
        Animator animUser = AnimatorInflater.loadAnimator(this, R.animator.text_field_jump);
        Animator animEmail = AnimatorInflater.loadAnimator(this, R.animator.text_field_jump);
        Animator animPass = AnimatorInflater.loadAnimator(this, R.animator.text_field_jump);
        Animator animPassR = AnimatorInflater.loadAnimator(this, R.animator.text_field_jump);
        anim.setTarget(_buttonRegisterConfirm);
        animUser.setTarget(_editTextRegisterUsername);
        animEmail.setTarget(_editTextRegisterEmail);
        animPass.setTarget(_editTextRegisterPassword);
        animPassR.setTarget(_editTextRegisterPasswordRepeat);

        _buttonRegisterConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = _editTextRegisterUsername.getText().toString().trim();
                String email = _editTextRegisterEmail.getText().toString().trim();
                String password = _editTextRegisterPassword.getText().toString().trim();
                String passwordRepeat = _editTextRegisterPasswordRepeat.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordRepeat)) {
                    Toast.makeText(getApplicationContext(), R.string.toast_missing_fields, Toast.LENGTH_SHORT).show();
                    anim.start();

                    if (TextUtils.isEmpty(username))
                        animUser.start();
                    if (TextUtils.isEmpty(email))
                        animEmail.start();
                    if (TextUtils.isEmpty(password))
                        animPass.start();
                    if (TextUtils.isEmpty(passwordRepeat))
                        animPassR.start();
                }
                else if (!TextUtils.equals(password, passwordRepeat)) {
                    Toast.makeText(getApplicationContext(), R.string.toast_password_mismatch, Toast.LENGTH_SHORT).show();
                    _editTextRegisterPasswordRepeat.setError(getResources().getString(R.string.toast_password_mismatch));
                    anim.start();
                    animPassR.start();
                }
                else if (!db.userDAO().getUserByName(username).isEmpty()) {
                    animUser.start();
                    _editTextRegisterUsername.setError(getString(R.string.acc_username_taken));
                    Toast.makeText(getApplicationContext(), getString(R.string.acc_username_taken), Toast.LENGTH_SHORT).show();
                }
                else if (!db.userDAO().getUserByEmail(email).isEmpty()) {
                    animEmail.start();
                    _editTextRegisterEmail.setError(getString(R.string.acc_email_taken));
                    Toast.makeText(getApplicationContext(), getString(R.string.acc_email_taken), Toast.LENGTH_SHORT).show();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    animEmail.start();
                    _editTextRegisterEmail.setError(getString(R.string.acc_email_invalid));
                    Toast.makeText(getApplicationContext(), getString(R.string.acc_email_invalid), Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 4) {
                    animPass.start();
                    _editTextRegisterPassword.setError(getString(R.string.acc_password_short));
                    Toast.makeText(getApplicationContext(), getString(R.string.acc_password_short), Toast.LENGTH_SHORT).show();
                }
                else {
                    String code = emailVerificationCode();
                    if (code.isEmpty())
                        return;

                    Toast.makeText(getApplicationContext(), getString(R.string.verification_code_sent) + email, Toast.LENGTH_SHORT).show();

                    EditText editText = new EditText(RegisterActivity.this);
                    editText.setText("");
                    AlertDialog dialog = new MaterialAlertDialogBuilder(RegisterActivity.this)
                            .setTitle(getString(R.string.verification_enter_code))
                            .setView(editText)
                            .setPositiveButton(getString(R.string.confirm), null)
                            .setNegativeButton(getString(R.string.cancel), null)
                            .create();
                    dialog.show();

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setOnClickListener(v -> {
                                String enteredCode = editText.getText().toString();
                                if (enteredCode.equals(code)) {
                                    dialog.dismiss();
                                    createAccount(username, email, password);
                                } else {
                                    editText.setError(getString(R.string.verification_code_incorrect));
                                    editText.requestFocus();
                                }
                            });
                }
            }
        });

        createNotificationChannel();
    }

    private void createAccount(String username, String email, String password) {
        User user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(password);
        db.userDAO().insert(user);
        Toast.makeText(getApplicationContext(), R.string.toast_registration_successful, Toast.LENGTH_SHORT).show();

        SharedPreferences prefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("user_id", db.userDAO().getUserByName(username).get(0).getId());
        editor.apply();

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    private String emailVerificationCode() {
        String code = String.valueOf(
                100000 + new Random().nextInt(900000));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "verification")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Email Verification")
                        .setContentText("Verification code: " + code)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED)
            requestNotificationPermission();
        else
            NotificationManagerCompat.from(this).notify(1, builder.build());
        return code;
    }

    private static final int NOTIF_PERMISSION_CODE = 100;
    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIF_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIF_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                "verification", "Verification Notifications", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}