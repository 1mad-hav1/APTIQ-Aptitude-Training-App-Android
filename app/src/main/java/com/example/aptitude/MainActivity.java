package com.example.aptitude;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences; 
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String ip;
    EditText e_ip;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        e_ip = findViewById(R.id.editTextText);
        bt = findViewById(R.id.button);
        bt.setOnClickListener(this);
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        e_ip.setText(sh.getString("ip",""));
    }

    @Override
    public void onClick(View view) {
        String ip = e_ip.getText().toString();
        if (ip.length() < 1) {
            e_ip.setError("");
        }
        else{
            SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor ed = sh.edit();
            ed.putString("ip",ip);
            ed.putString("url","http://"+ip+":8888/");
            ed.putString("iurl","http://"+ip+":8888/");
            ed.commit();
            Intent i = new Intent(getApplicationContext(),Login.class);
            startActivity(i);
        }


    }
}