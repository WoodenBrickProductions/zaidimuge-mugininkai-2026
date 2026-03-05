package com.example.universityjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SecondWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_window);

        TextView textView = findViewById(R.id.textView);
        String data = getIntent().getStringExtra("Data");
        textView.setText(data);

        EditText editText = (EditText) findViewById(R.id.editTextText);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("OnClick in SecondWindow");
                Intent intent = new Intent(getBaseContext(), SecondWindow.class);
                intent.putExtra("Data", "Hello World");
                startActivity(intent);
            }
        });
    }
}