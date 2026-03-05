package com.example.universityjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RandomActivity extends AppCompatActivity {

    CustomState state;
    int randomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        state = getIntent().getParcelableExtra("Data", CustomState.class);

        EditText editText = (EditText) findViewById(R.id.editTextNumber);
        Button button = (Button) findViewById(R.id.back_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("OnClick in RandomActivity");
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("Data", state);
                startActivity(intent);
            }
        });

        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        Button rndButton = (Button) findViewById(R.id.rnd_button);
        TextWatcher insertRandomNumberTextWatcher;
        insertRandomNumberTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() > 0) {
                    if(Integer.parseInt(s.toString()) == randomNumber) {
                        getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    }
                    else {
                        getWindow().getDecorView().setBackgroundColor(Color.RED);
                    }
                } else {
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editText.addTextChangedListener(insertRandomNumberTextWatcher);

        rndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomNumber = (int) (Math.random() * 1000);
            }
        });
    }
}
