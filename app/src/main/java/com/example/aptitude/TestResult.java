package com.example.aptitude;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.aptitude.QuestionAdapter;
import com.example.aptitude.Question;

public class TestResult extends AppCompatActivity {

    SharedPreferences sh;
    int resultId;
    RecyclerView questionRecyclerView;
    QuestionAdapter questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        resultId = getIntent().getIntExtra("result_id", -1);
        if (resultId == -1) {
            Toast.makeText(this, "Invalid Result ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Find views
        TextView testNameView = findViewById(R.id.test_name);
        TextView testDetailsView = findViewById(R.id.test_details);
        TextView overallMarksView = findViewById(R.id.overall_marks);
        TextView logicalMarkView = findViewById(R.id.logical_mark);
        TextView verbalMarkView = findViewById(R.id.verbal_mark);
        TextView quantMarkView = findViewById(R.id.quant_mark);
        View overallCircle = findViewById(R.id.overall_circle);
        questionRecyclerView = findViewById(R.id.question_recycler_view);

        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new QuestionAdapter(new ArrayList<>());
        questionRecyclerView.setAdapter(questionAdapter);

        fetchTestResult(resultId, testNameView, testDetailsView, overallMarksView, overallCircle,
                logicalMarkView, verbalMarkView, quantMarkView);
    }

    private void fetchTestResult(int resultId, TextView testNameView, TextView testDetailsView,
                                 TextView overallMarksView, View overallCircle,
                                 TextView logicalMarkView, TextView verbalMarkView,
                                 TextView quantMarkView) {
        String url = sh.getString("url", "") + "and_get_test_result";
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
                    String testName = jsonObj.getString("testName");
                    String testDate = jsonObj.getString("testDate");
                    String testDifficulty = jsonObj.getString("testDifficulty");
                    int totalQuestions = jsonObj.getInt("totalQuestions");
                    String topics = jsonObj.getString("topics");
                    String time = jsonObj.getString("time");
                    int overallScore = jsonObj.getInt("overallScore");
                    int passmark = jsonObj.getInt("passmark");
                    boolean isPassed = jsonObj.getBoolean("isPassed");

                    JSONObject topicScores = jsonObj.getJSONObject("topicScores");
                    int logicalScore = topicScores.getInt("logicalScore");
                    int logicalTotal = topicScores.getInt("logicalTotal");
                    int verbalScore = topicScores.getInt("verbalScore");
                    int verbalTotal = topicScores.getInt("verbalTotal");
                    int quantScore = topicScores.getInt("quantScore");
                    int quantTotal = topicScores.getInt("quantTotal");

                    JSONArray questionDetails = jsonObj.getJSONArray("questions_details");

                    // Update UI components
                    testNameView.setText(testName);

                    String testDetails = "Date: " + testDate +
                            "\nDifficulty: " + testDifficulty +
                            "\nTotal Questions: " + totalQuestions +
                            "\nTopics: " + topics +
                            "\nPass Mark: " + passmark +
                            "\nTime: " + time;
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
