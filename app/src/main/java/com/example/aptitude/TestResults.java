package com.example.aptitude;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.aptitude.QuestionAdapter;
import com.example.aptitude.Question;
// Career Prediction results
public class TestResults extends AppCompatActivity {

    SharedPreferences sh;
    int resultId;

    QuestionAdapter questionAdapter;
TextView tt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);

        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        resultId = getIntent().getIntExtra("result_id", -1);
        if (resultId == -1) {
            Toast.makeText(this, "Invalid Result ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tt=findViewById(R.id.textView11);
        // Find views
        TextView testNameView = findViewById(R.id.test_name);
        TextView testDetailsView = findViewById(R.id.test_details);
        TextView overallMarksView = findViewById(R.id.overall_marks);
        TextView logicalMarkView = findViewById(R.id.logical_mark);
        TextView verbalMarkView = findViewById(R.id.verbal_mark);
        TextView quantMarkView = findViewById(R.id.quant_mark);
        View overallCircle = findViewById(R.id.overall_circle);
        TextView predicted_career = findViewById(R.id.predicted_career);
        TextView career_description = findViewById(R.id.career_description);

        questionAdapter = new QuestionAdapter(new ArrayList<>());


        fetchTestResult(resultId, testNameView, testDetailsView, overallMarksView, overallCircle,
                logicalMarkView, verbalMarkView, quantMarkView, predicted_career, career_description);
    }

    @Override
    public void onBackPressed() {
        // Check if the activity is a subpage (not the main entry point)
        if (!isTaskRoot()) {
            Intent intent = new Intent(getApplicationContext(), Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // Close current activity
        } else {
            // Show exit confirmation dialog if it's the main activity
            new AlertDialog.Builder(this)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        finishAffinity(); // Close all activities and exit app
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss(); // Dismiss the dialog if "No" is clicked
                    })
                    .show();
        }
    }

    private void fetchTestResult(int resultId, TextView testNameView, TextView testDetailsView,
                                 TextView overallMarksView, View overallCircle,
                                 TextView logicalMarkView, TextView verbalMarkView,
                                 TextView quantMarkView,TextView predicted_career,TextView career_description) {
        String url = sh.getString("url", "") + "and_get_test_results_prediction";
        if (url.trim().isEmpty()) {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject js = new JSONObject(response);
                if (js.getString("status").equalsIgnoreCase("ok")) {
                    JSONObject jsonObj = js.getJSONObject("data");
                    String testName = "Career Prediction";
                    String testDate = jsonObj.getString("testDate");
                    int totalQuestions = jsonObj.getInt("totalQuestions");
                    String topics = jsonObj.getString("topics");
                    int overallScore = jsonObj.getInt("overallScore");
                    boolean isPassed = jsonObj.getBoolean("isPassed");

                    JSONObject topicScores = jsonObj.getJSONObject("topicScores");
                    int logicalScore = topicScores.getInt("logicalScore");
                    int logicalTotal = topicScores.getInt("logicalTotal");
                    int verbalScore = topicScores.getInt("verbalScore");
                    int verbalTotal = topicScores.getInt("verbalTotal");
                    int quantScore = topicScores.getInt("quantScore");
                    int quantTotal = topicScores.getInt("quantTotal");
                    String ss=js.getString("result");
                    String description=js.getString("description");
                    predicted_career.setText(ss);
                    career_description.setText(description);
                    JSONArray questionDetails = jsonObj.getJSONArray("questions_details");

                    // Update UI components
                    testNameView.setText(testName);

                    String testDetails = "Date: " + testDate   ;
                    testDetailsView.setText(testDetails);

                    overallMarksView.setText(overallScore + "/" + totalQuestions);

                    if (isPassed) {
                        overallCircle.setBackground(getResources().getDrawable(R.drawable.circle_green));
                    } else {
                        overallCircle.setBackground(getResources().getDrawable(R.drawable.circle_red));
                    }

                    logicalMarkView.setText(logicalScore + "/" + logicalTotal);
                    verbalMarkView.setText(verbalScore + "/" + verbalTotal);
                    quantMarkView.setText(quantScore + "/" + quantTotal);

                    // Populate question RecyclerView
                    ArrayList<Question> questionList = new ArrayList<>();
                    for (int i = 0; i < questionDetails.length(); i++) {
                        JSONObject questionObj = questionDetails.getJSONObject(i);
                        String question = questionObj.getString("question");
                        String correctAnswer = questionObj.getString("correctAnswer");
                        String explanation = questionObj.getString("answerDescription"); // Add default or fetch from the JSON if available
                        questionList.add(new Question(question, correctAnswer, explanation));
                    }
                    questionAdapter.updateQuestions(questionList);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to fetch test result", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, error -> Toast.makeText(getApplicationContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("result_id", String.valueOf(resultId));
                return params;
            }
        };
        requestQueue.add(postRequest);
    }


}
