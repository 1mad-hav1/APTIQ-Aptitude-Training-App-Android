package com.example.aptitude;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SentFeedback extends AppCompatActivity implements View.OnClickListener {

    EditText ed_feedback;
    Button bt_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_feedback);
        ed_feedback= findViewById(R.id.editTextDescription);
        bt_send= findViewById(R.id.buttonSend);
        bt_send.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v==bt_send) {
            String feedback = ed_feedback.getText().toString();
            if (feedback.length()<1)
                ed_feedback.setError("Enter Description");
            else {
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String url = sh.getString("url", "") + "and_sent_feedback";
                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                try {
                                    JSONObject obj = new JSONObject(new String(response.data));

                                    if (obj.getString("status").equals("ok")) {
                                        Toast.makeText(getApplicationContext(), "Feedback sent successfully", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), ViewFeedbacks.class);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Feeback sent failed", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        String uid = sh.getString("uid", "None"); // Replace "default_value" with a fallback value
                        Map<String, String> params = new HashMap<>();
                        params.put("feedback", feedback);
                        params.put("uid", uid);
                        return params;
                    }
                };

                Volley.newRequestQueue(this).add(volleyMultipartRequest);
            }
        }
    }
}