package com.example.universityjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.universityjava.database.Condition;
import com.example.universityjava.database.Game;
import com.example.universityjava.database.Listing;
import com.example.universityjava.database.PhysicalListingAttributes;
import com.example.universityjava.database.Platform;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    Button _button;
    Button _fragmentTestingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }
}