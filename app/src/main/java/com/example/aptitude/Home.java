package com.example.aptitude;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aptitude.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Existing setup code for Navigation Drawer
        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        // Set Welcome message
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userName = sh.getString("user_name", "User"); // Default to "User" if not found

        TextView tvWelcome = findViewById(R.id.tv_welcome);
        tvWelcome.setText("Welcome, " + userName + "!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.nav_home) {
            Intent i = new Intent(getApplicationContext(), Home.class);
            startActivity(i);
        } else if (id==R.id.myprofile) {
            Intent i = new Intent(getApplicationContext(), MyProfile.class);
            startActivity(i);
//        } else if (id==R.id.nav_complaint) {
//            Intent i = new Intent(getApplicationContext(), SendComplaints.class);
//            startActivity(i);
        } else if (id==R.id.nav_results) {
            Intent i = new Intent(getApplicationContext(), Results.class);
            startActivity(i);
        } else if (id==R.id.nav_study_materials) {
            Intent i = new Intent(getApplicationContext(), StudyMaterials.class);
            i.putExtra("content_type", "Logical");
            startActivity(i);
        } else if (id==R.id.nav_feedback) {
            Intent i = new Intent(getApplicationContext(), ViewFeedbacks.class);
            startActivity(i);
        } else if (id==R.id.taketest) {
            Intent i = new Intent(getApplicationContext(), TestSelect.class);
            startActivity(i);
        } else {
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
        }
        return true;
    }
}