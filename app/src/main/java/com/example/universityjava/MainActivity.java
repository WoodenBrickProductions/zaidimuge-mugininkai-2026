package com.example.universityjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button _button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _button = (Button) findViewById(R.id.button);
        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("OnClick");
                Intent intent = new Intent(getBaseContext(), SecondWindow.class);
                intent.putExtra("Data", "Hello World");
                startActivity(intent);
            }
        });
    }
}