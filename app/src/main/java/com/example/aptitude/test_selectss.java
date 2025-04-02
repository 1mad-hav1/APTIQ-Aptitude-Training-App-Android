package com.example.aptitude;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class test_selectss extends AppCompatActivity implements View.OnClickListener {
    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_selectss);

        start = findViewById(R.id.start);
        start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == start) {
            // Directly navigate to TestPage on button click
            Intent intent = new Intent(test_selectss.this, QuestionInput.class);
            startActivity(intent);
        }
    }
}
