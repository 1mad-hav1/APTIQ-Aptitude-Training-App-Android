package com.example.aptitude;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QuestionInput extends AppCompatActivity {

    // Declare UI elements
    TextView questionText, timerText, progressText, testName, passMark, questionNum;
    RadioGroup optionsGroup;
    ProgressBar progressBar;
    Button prevButton, nextButton, submitButton, clearButton;

    // Declare variables for test configuration
    String difficulty, numQns, time, test_name;
//    boolean verbal, logical, quant;
    int currentQuestionIndex = 0;
    int totalQuestions = 0;
    int pass_mark, test_id;
    int n_logical,n_verbal,n_quant;
    CountDownTimer countDownTimer;
    SharedPreferences sh;

    // Arrays to store questions, options, correct answers, and user answers
    String[] questions;
    String[] question_type;
    String[][] options;
    String[] correctAnswers;
    int[] selectedAnswers; // To store selected answers for each question
    int total_score =0, logical_score=0, quant_score=0, verbal_score=0, wrongAnswer=0;
    // Static values for question categories
    private static final boolean VERBAL = true;
    private static final boolean LOGICAL = true;
    private static final boolean QUANT = true;

    // Variables for test configuration
//    private String difficulty, numQns, time, test_name;
    private boolean verbal, logical, quant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_input);

        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Initialize UI elements
        questionText = findViewById(R.id.question_text);
        questionNum = findViewById(R.id.question_number);
        timerText = findViewById(R.id.timer_text);
        optionsGroup = findViewById(R.id.options_group);
        progressBar = findViewById(R.id.progress_bar);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        submitButton = findViewById(R.id.submit_button);
        clearButton = findViewById(R.id.clear_button);
        testName = findViewById(R.id.test_name_heading);
        passMark = findViewById((R.id.pass_mark_subheading));
        // Retrieve the data passed from TestSelect
        Intent intent = getIntent();
//        difficulty = intent.getStringExtra("difficulty");
        difficulty = "Hard";
//        numQns = intent.getStringExtra("num_qns");
        numQns = "90";
//        time = intent.getStringExtra("time");
        time = "90";
        test_name = "Career Prediction";
        verbal = VERBAL;
        logical = LOGICAL;
        quant = QUANT;

//        verbal = intent.getBooleanExtra("verbal", false);
//        logical = intent.getBooleanExtra("logical", false);
//        quant = intent.getBooleanExtra("quant", false);
//        pass_mark = intent.getIntExtra("pass_mark", 0);
        totalQuestions = Integer.parseInt(numQns);
//        totalQuestions = 30;

//        testName.setText(test_name);
//        passMark.setText("Pass Mark: " + pass_mark);

        // Initialize the selected answers array
        selectedAnswers = new int[totalQuestions];
        for (int i = 0; i < totalQuestions; i++) {
            selectedAnswers[i] = -1; // -1 means no answer selected
        }

        // Call the function to fetch questions
        fetchQuestions();

        // Set up a countdown timer based on the total number of questions and time limit
        startTimer(Integer.parseInt(time));

        // Set up button listeners
        nextButton.setOnClickListener(v -> {
            saveAnswer();
            if (currentQuestionIndex < totalQuestions - 1) {
                currentQuestionIndex++;
                setQuestion();
            } else {
                // Disable the next button since no more questions are available
                nextButton.setEnabled(false);
            }
        });

        prevButton.setOnClickListener(v -> {
            saveAnswer();
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                setQuestion();
            } else {
                // Disable the previous button since this is the first question
                prevButton.setEnabled(false);
            }
        });

        submitButton.setOnClickListener(v -> {
            // Check if all questions are answered before submitting
            saveAnswer();
            checkAnswersBeforeSubmit();
            // Update progress bar based on the current question index
            updateProgressBar();
        });

        clearButton.setOnClickListener(v -> {
            selectedAnswers[currentQuestionIndex] = -1;
            setQuestion();
        });
    }

    private void fetchQuestions() {
        String url = sh.getString("url", "") + "and_get_test_questions_cp";
        if (url.trim().isEmpty()) {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObj = new JSONObject(response);
                if (jsonObj.getString("status").equalsIgnoreCase("ok")) {

                    test_id=jsonObj.getInt("test_id");
                    JSONArray js = jsonObj.getJSONArray("data");

                    totalQuestions = js.length();
                    questions = new String[totalQuestions];
                    question_type = new String[totalQuestions];
                    options = new String[totalQuestions][4];
                    correctAnswers = new String[totalQuestions];

                    for (int i = 0; i < totalQuestions; i++) {
                        JSONObject questionObj = js.getJSONObject(i);
                        questions[i] = questionObj.getString("question");
                        question_type[i] = questionObj.getString("question_type");
                        options[i][0] = questionObj.getString("option1");
                        options[i][1] = questionObj.getString("option2");
                        options[i][2] = questionObj.getString("option3");
                        options[i][3] = questionObj.getString("option4");
                        correctAnswers[i] = questionObj.getString("correctAnswer");
                        if (question_type[i].equals("Logical"))
                            n_logical++;
                        else if (question_type[i].equals("Quantitative"))
                            n_quant++;
                        else
                            n_verbal++;
                    }

                    setQuestion();
                } else {
                    Toast.makeText(getApplicationContext(), "No questions found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, error -> Toast.makeText(getApplicationContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("test_name", test_name);
                params.put("difficulty", difficulty);
                params.put("num_qns", numQns);
                params.put("verbal", verbal ? "1" : "0");
                params.put("logical", logical ? "1" : "0");
                params.put("quant", quant ? "1" : "0");
                params.put("time", time);
                params.put("uid", sh.getString("uid", ""));
                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    private void setQuestion() {
        if (questions == null || questions.length == 0) {
            Toast.makeText(getApplicationContext(), "No questions available", Toast.LENGTH_SHORT).show();
            return;
        }

        questionText.setText(questions[currentQuestionIndex]);
        int qn=currentQuestionIndex+1;
        questionNum.setText("Question No: " + qn);
        RadioButton option1 = findViewById(R.id.option1);
        RadioButton option2 = findViewById(R.id.option2);
        RadioButton option3 = findViewById(R.id.option3);
        RadioButton option4 = findViewById(R.id.option4);

        option1.setText(options[currentQuestionIndex][0]);
        option2.setText(options[currentQuestionIndex][1]);
        option3.setText(options[currentQuestionIndex][2]);
        option4.setText(options[currentQuestionIndex][3]);

        // Clear previous selections
        optionsGroup.clearCheck();

        // Set the selected option for this question if any
        if (selectedAnswers[currentQuestionIndex] != -1) {
            RadioButton selectedRadioButton = (RadioButton) optionsGroup.getChildAt(selectedAnswers[currentQuestionIndex]);
            selectedRadioButton.setChecked(true);
        }

        // Update progress bar based on the current question index
        updateProgressBar();

        // Enable or disable buttons based on the current question index
        nextButton.setEnabled(currentQuestionIndex < totalQuestions - 1);
        prevButton.setEnabled(currentQuestionIndex > 0);
    }


    private void saveAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            int selectedIndex = optionsGroup.indexOfChild(findViewById(selectedId));
            selectedAnswers[currentQuestionIndex] = selectedIndex;
        }
    }

    private void updateProgressBar() {
        int answeredQuestions = 0;
        for (int answer : selectedAnswers) {
            if (answer != -1) {
                answeredQuestions++;
            }
        }
        int progressPercentage = (int) ((float) answeredQuestions / totalQuestions * 100);
        progressBar.setProgress(progressPercentage);
    }

    private void startTimer(int totalTime) {
        countDownTimer = new CountDownTimer(totalTime * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                timerText.setText(String.format("Time Left: %02d:%02d", minutes, seconds));

                // Alert at 5 minutes and 1 minute
                if (minutes == 5 && seconds == 0) {
                    showTimeAlert("Only 5 minutes left!");
                } else if (minutes == 1 && seconds == 0) {
                    showTimeAlert("Only 1 minute left!");
                }
            }

            @Override
            public void onFinish() {
                // Set the timer text to 00:00
                timerText.setText("00:00");

                // Create a popup dialog to notify the user that the time is up
                new AlertDialog.Builder(QuestionInput.this)
                        .setTitle("Time's Up!")
                        .setMessage("The test time is over.")
                        .setCancelable(false)
                        .setPositiveButton("Show Result", (dialog, which) -> showResult())
                        .show();
            }

        };
        countDownTimer.start();
    }

    private void showTimeAlert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void checkAnswersBeforeSubmit() {
        int unansweredQuestions = 0;
        for (int i = 0; i < totalQuestions; i++) {
            if (selectedAnswers[i] == -1) {
                unansweredQuestions++;
            }
        }

        String message;
        if (unansweredQuestions > 0) {
            message = "You have " + unansweredQuestions + " unanswered questions. Are you sure you want to submit?";
        } else {
            message = "Are you sure you want to submit the test?";
        }

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> {
                    showResult();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showResult() {
        // Reset scores
        total_score = 0;
        logical_score = 0;
        quant_score = 0;
        verbal_score = 0;
        wrongAnswer = 0;

        // Calculate scores
        for (int i = 0; i < totalQuestions; i++) {
            if (selectedAnswers[i] != -1) {
                if (options[i][selectedAnswers[i]].equals(correctAnswers[i])) {
                    total_score++;
                    if (question_type[i].equals("Logical")) {
                        logical_score++;
                    } else if (question_type[i].equals("Verbal")) {
                        verbal_score++;
                    } else {
                        quant_score++;
                    }
                } else {
                    wrongAnswer++;
                }
            }
        }

        // Store results via API
        String url = sh.getString("url", "") + "and_post_test_results_cp";
        if (url.trim().isEmpty()) {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject jsonObj = new JSONObject(response);
                if (jsonObj.getString("status").equalsIgnoreCase("ok")) {
                    // Show alert dialog
                    new AlertDialog.Builder(QuestionInput.this)
                            .setTitle("Test Completed")
                            .setMessage("Your test is successfully completed.")
                            .setCancelable(false)
                            .setPositiveButton("Go to Results", (dialog, which) -> {
                                // Navigate to TestResult page
                                Intent intent = new Intent(QuestionInput.this, TestResults.class);
                                try {
                                    intent.putExtra("result_id", Integer.parseInt(jsonObj.getString("result_id")));
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                startActivity(intent);
                                finish(); // Optional, to close the current activity
                            })
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error in submitting test results", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, error -> Toast.makeText(getApplicationContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", sh.getString("uid",""));
                params.put("test_id", String.valueOf(test_id));
                params.put("mark_scored", String.valueOf(total_score));
                params.put("logical_score", String.valueOf(logical_score));
                params.put("verbal_score", String.valueOf(verbal_score));
                params.put("quant_score", String.valueOf(quant_score));
                if (total_score >= pass_mark)
                    params.put("pass_fail", "Pass");
                else
                    params.put("pass_fail", "Fail");
                return params;
            }
        };
        requestQueue.add(postRequest);
    }


    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}


