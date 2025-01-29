package com.example.aptitude;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class bottom_study_navbar extends AppCompatActivity implements View.OnClickListener {
    TextView logical,verbal,quant;
    ImageView ic_logical,ic_verbal,ic_quant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_study_navbar);
        logical = findViewById(R.id.logical);
        quant = findViewById(R.id.quantitative);
        verbal = findViewById(R.id.verbal);
        ic_logical = findViewById(R.id.ic_logical);
        ic_quant = findViewById(R.id.ic_quantitative);
        ic_verbal = findViewById(R.id.ic_verbal);

        logical.setOnClickListener(this);
        quant.setOnClickListener(this);
        verbal.setOnClickListener(this);
        ic_logical.setOnClickListener(this);
        ic_quant.setOnClickListener(this);
        ic_verbal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == logical || v == ic_logical) {
            Intent i = new Intent(getApplicationContext(), StudyMaterials.class);
            i.putExtra("content_type", "Logical");
            startActivity(i);
        } else if (v == verbal || v == ic_verbal) {
            Intent i = new Intent(getApplicationContext(), StudyMaterials.class);
            i.putExtra("content_type", "Verbal");
            startActivity(i);
        } else if (v == quant || v == ic_quant) {
            Intent i = new Intent(getApplicationContext(), StudyMaterials.class);
            i.putExtra("content_type", "Quantitative");
            startActivity(i);
        }
    }
}