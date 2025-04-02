package com.example.aptitude;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.aptitude.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Set refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh the activity
            refreshData();

            // Stop the refresh animation
            swipeRefreshLayout.setRefreshing(false);
        });

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
        TextView tv_user_level = findViewById(R.id.tv_user_level);
        String userLevel = sh.getString("user_level", "User");
        String progress_value = sh.getString("progress_value","0");

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        TextView progress = findViewById(R.id.tv_marks);
        tv_user_level.setText(userLevel + " Level");
        TextView tv_advice = findViewById(R.id.tv_advice);
        TextView tv_link_content = findViewById(R.id.tv_link_content);
        String adviceText = "";
        String motivationalText = "";
        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Chatbot_AI.class);
            startActivity(intent);
        });

        switch (userLevel) {
            case "Beginner":
                adviceText = "Keep up the good work! Focus more on easy questions and start exploring medium-level problems.";
                motivationalText = "Every expert was once a beginner. Keep pushing forward!";
                progressBar.setMax(1000);
                progressBar.setProgress(Integer.parseInt(progress_value));
                progress.setText(progress_value + "/" + 1000);
                break;
            case "Amateur":
                adviceText = "You're progressing well! Revisit beginner concepts, practice medium-level questions, and challenge yourself with hard ones.";
                motivationalText = "Success is the sum of small efforts, repeated day in and day out.";
                progressBar.setMax(1500);
                progress.setText(progress_value + "/" + 2500);
                progressBar.setProgress(Integer.parseInt(progress_value)-1000);
                break;
            case "Professional":
                adviceText = "You are doing fantastic! Your hard work is paying off. Focus on mastering hard topics and keep practicing consistently.";
                motivationalText = "Great things are not done by impulse, but by a series of small things brought together.";
                progressBar.setMax(2500);
                progressBar.setProgress(Integer.parseInt(progress_value)-2500);
                progress.setText(progress_value + "/" + 5000);
                break;
            default:
                adviceText = "Welcome! Start your journey by practicing easy questions and gradually move up to harder levels.";
                motivationalText = "Believe in yourself. Every step counts!";
        }

// Set the advice and motivational text
        tv_advice.setText(adviceText + "\n\n" + motivationalText);

// Set the OnClickListener for the link
        tv_link_content.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, StudyMaterials.class);
            intent.putExtra("content_type", "Logical");
            startActivity(intent);
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = binding.drawerLayout;

        // If navigation drawer is open, close it first
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);

            // If not on home fragment, navigate to home
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() != R.id.nav_home) {
                navController.navigate(R.id.nav_home);
            } else {
                // Show exit confirmation dialog
                new AlertDialog.Builder(this)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            finishAffinity();// Call the default behavior (exit app)
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        }
    }

    // Method to refresh data
    private void refreshData() {
        // You can reload data from SharedPreferences or fetch new data
//        recreate();  // Reloads the activity
        Intent j = new Intent(getApplicationContext(),Home.class);
        startActivity(j);
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
        }
        else if (id==R.id.taketest) {
            Intent i = new Intent(getApplicationContext(), TestSelect.class);
            startActivity(i);
        }
        else if (id==R.id.company) {
            Intent i = new Intent(getApplicationContext(), CompanyList.class);
            startActivity(i);
        }
//        else if (id==R.id.taketest) {
//            Intent i = new Intent(getApplicationContext(), TestSelect.class);
//            startActivity(i);
//        }

//        else if (id==R.id.button14)
//        {
//            Intent in=new Intent(getApplicationContext(),Course_recommendation.class);
//            startActivity(in);
//        }
        else if (id==R.id.test_new)
        {
            Intent in=new Intent(getApplicationContext(),test_selectss.class);
            startActivity(in);
        }


        else {
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
        }
        return true;
    }
}