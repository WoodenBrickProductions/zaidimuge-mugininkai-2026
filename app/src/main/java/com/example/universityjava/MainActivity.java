package com.example.universityjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button _button, _gotoRandomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _button = (Button) findViewById(R.id.button2);
        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("OnClick");
                Intent intent = new Intent(getBaseContext(), SecondWindow.class);
                CustomState state = new CustomState();
                state.data.add(2, "Test2");
                intent.putExtra("Data", state);
                startActivity(intent);
            }
        });

        _gotoRandomButton = (Button) findViewById(R.id.button4);
        _gotoRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RandomActivity.class);
                CustomState state = new CustomState();
                state.data.add(10, "I'm a random Activity");
                intent.putExtra("Data", state);
                startActivity(intent);
            }
        });
    }
}