package com.example.aptitude;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class CompanyQuestions extends AppCompatActivity {
    RecyclerView c_questionRecyclerView;
    String company_name;
    TextView heading;
    QuestionAdapter c_questionAdapter;
    SharedPreferences sh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_questions);

        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        company_name = sh.getString("cname", "TCS");
        heading = findViewById(R.id.companyheading);
        heading.setText(company_name);
        c_questionRecyclerView = findViewById(R.id.company_question_recycler_view);

        c_questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        c_questionAdapter = new QuestionAdapter(new ArrayList<>());
        c_questionRecyclerView.setAdapter(c_questionAdapter);

        String url = sh.getString("url", "") + "and_get_company_questions";
        if (url.trim().isEmpty()) {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject js = new JSONObject(response);
                if (js.getString("status").equalsIgnoreCase("ok")) {
                    JSONArray questionDetails = js.getJSONArray("data"); // Directly get data as array
                    ArrayList<Question> questionList = new ArrayList<>();
                    for (int i = 0; i < questionDetails.length(); i++) {
                        JSONObject questionObj = questionDetails.getJSONObject(i);
                        String question = questionObj.getString("question");
                        String correctAnswer = questionObj.getString("correctAnswer");
                        String explanation = questionObj.getString("answerDescription");
                        questionList.add(new Question(question, correctAnswer, explanation));
                    }
                    c_questionAdapter.updateQuestions(questionList);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to fetch test result", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                heading.setText(e.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, error -> Toast.makeText(getApplicationContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cname", company_name);
                return params;
            }
        };
        requestQueue.add(postRequest);
    }
}