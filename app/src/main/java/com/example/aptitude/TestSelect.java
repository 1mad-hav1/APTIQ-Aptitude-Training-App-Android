package com.example.aptitude;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class TestSelect extends AppCompatActivity implements View.OnClickListener {
    CheckBox cb_logical, cb_verbal, cb_quant;
    RadioButton rb_easy, rb_medium, rb_hard, rb_twenty, rb_fourty, rb_sixty;
    String difficulty = "", num_qns = "", time;
    Boolean verbal = false, logical = false, quant = false;
    EditText test_name;
    TextView tv_time, recommended;
    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_select);

        test_name = findViewById(R.id.test_name);
        recommended = findViewById(R.id.recommended);
        cb_logical = findViewById(R.id.logical);
        cb_quant = findViewById(R.id.quant);
        cb_verbal = findViewById(R.id.verbal);

        rb_easy = findViewById(R.id.easy);
        rb_medium = findViewById(R.id.medium);
        rb_hard = findViewById(R.id.hard);

        rb_twenty = findViewById(R.id.twenty);
        rb_fourty = findViewById(R.id.fourty);
        rb_sixty = findViewById(R.id.sixty);

        tv_time = findViewById(R.id.time);
        tv_time.setText("_");
        start = findViewById(R.id.start);
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user_level = sh.getString("user_level","Beginner");
        if (user_level.equals("Beginner")){
            rb_easy.setChecked(true);
            recommended.setHint("Recommended: Easy");
        }

        else if (user_level.equals("Amateur")){
            rb_medium.setChecked(true);
            recommended.setHint("Recommended: Medium");
        }

        else {
            rb_hard.setChecked(true);
            recommended.setHint("Recommended: Hard");
        }

        start.setOnClickListener(this);

        // Add listeners to update time whenever options are selected
        addListeners();
    }

    private void addListeners() {
        // Difficulty RadioButtons
        View.OnClickListener difficultyListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb_easy.isChecked()) {
                    difficulty = "Easy";
                } else if (rb_medium.isChecked()) {
                    difficulty = "Medium";
                } else if (rb_hard.isChecked()) {
                    difficulty = "Hard";
                }
                updateTimeTextView();
            }
        };

        rb_easy.setOnClickListener(difficultyListener);
        rb_medium.setOnClickListener(difficultyListener);
        rb_hard.setOnClickListener(difficultyListener);

        // Number of Questions RadioButtons
        View.OnClickListener numQnsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb_twenty.isChecked()) {
                    num_qns = "20";
                } else if (rb_fourty.isChecked()) {
                    num_qns = "40";
                } else if (rb_sixty.isChecked()) {
                    num_qns = "60";
                }
                updateTimeTextView();
            }
        };

        rb_twenty.setOnClickListener(numQnsListener);
        rb_fourty.setOnClickListener(numQnsListener);
        rb_sixty.setOnClickListener(numQnsListener);
    }

    private void updateTimeTextView() {
        if (difficulty.isEmpty() || num_qns.isEmpty()) {
            tv_time.setText("_"); // Default value
        } else {
            // Calculate time
            double baseTime = 0;
            if (difficulty.equals("Easy")) {
                baseTime = 1.0; // 1 minute per question
            } else if (difficulty.equals("Medium")) {
                baseTime = 1.25; // 1.25 minutes per question
            } else if (difficulty.equals("Hard")) {
                baseTime = 1.5; // 1.5 minutes per question
            }
            // Convert total time to an integer value
            time = String.valueOf((int) Math.round(baseTime * Integer.parseInt(num_qns)));
            tv_time.setText(time); // Update TextView
        }
    }

    @Override
    public void onClick(View v) {
        if (v == start) {
            StringBuilder selectedTopics = new StringBuilder();
            // Validate topics
            logical=false; verbal=false; quant=false;
            if (cb_verbal.isChecked() || cb_quant.isChecked() || cb_logical.isChecked()) {
                if (cb_logical.isChecked()) {
                    logical = true;
                    selectedTopics.append("Logical ");
                }
                if (cb_quant.isChecked()) {
                    quant = true;
                    selectedTopics.append("Quantitative ");
                }
                if (cb_verbal.isChecked()) {
                    verbal = true;
                    selectedTopics.append("Verbal ");
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please select your topics", Toast.LENGTH_LONG).show();
                return;
            }

            // Validate difficulty and number of questions
            if (difficulty.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please select difficulty", Toast.LENGTH_LONG).show();
                return;
            } else if (num_qns.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please select number of questions", Toast.LENGTH_LONG).show();
                return;
            }
            int pass_mark = (int) (Integer.parseInt(num_qns) * 0.4);
            // Show dialog with test information
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Test Information")
                    .setMessage("Difficulty: " + difficulty + "\n" +
                            "Number of Questions: " + num_qns + "\n" +
                            "Time: " + time + " minutes\n" +
                            "Pass Mark: " + pass_mark + "\n" +
                            "Selected Topics: " + selectedTopics.toString().trim() + "\n\n" +
                            "Are you sure you want to continue to the test?")
                    .setCancelable(false)
                    .setPositiveButton("Start Test", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Create an Intent to pass data to the Home activity
                            Intent intent = new Intent(TestSelect.this, TestPage.class);

                            // Pass selected values through intent
                            intent.putExtra("difficulty", difficulty);
                            intent.putExtra("num_qns", num_qns);
                            intent.putExtra("time", time);
                            intent.putExtra("verbal", verbal);
                            intent.putExtra("logical", logical);
                            intent.putExtra("quant", quant);
                            intent.putExtra("pass_mark", pass_mark);
                            intent.putExtra("test_name", test_name.getText().toString());
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", null); // Optionally add a cancel button
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
