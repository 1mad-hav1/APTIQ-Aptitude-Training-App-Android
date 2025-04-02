package com.example.aptitude;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
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

public class DetailedContent_new extends AppCompatActivity {
    TextView heading, content, difficulty, type;
    Button test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_content_new);
        heading = findViewById(R.id.heading);
        content = findViewById(R.id.content);
        difficulty = findViewById(R.id.difficulty);
        type = findViewById(R.id.type);
        test = findViewById(R.id.section_test);
        test.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SectionTest.class);
            startActivity(i);
        });
        Intent j = getIntent();
        if (j.getStringExtra("completed").equals("Yes")) {
            test.setEnabled(false);
            test.setText("Completed");
        }
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String url = sh.getString("url", "") + "and_get_detailed_content";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("status").equals("ok")) {
                        JSONObject js1 = obj.getJSONObject("content");
                        JSONArray js2 = obj.getJSONArray("video_links");
                        heading.setText(js1.getString("title"));
                        content.setText(js1.getString("description"));
                        difficulty.setText("Difficulty: " + js1.getString("difficulty"));
                        type.setText("Content Type: " + js1.getString("content_type"));

                        // Reference to your links container
                        LinearLayout linksContainer = findViewById(R.id.linksContainer);
                        linksContainer.removeAllViews(); // Clear any existing views

                        for (int i = 0; i < js2.length(); i++) {
                            JSONObject u = js2.getJSONObject(i);
                            String videoLink = u.getString("link");

                            // Create a new TextView for each link
                            TextView linkTextView = new TextView(getApplicationContext());
                            linkTextView.setText(videoLink);
                            linkTextView.setTextColor(Color.BLUE);
                            linkTextView.setPaintFlags(linkTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            linkTextView.setPadding(8, 8, 8, 8);
                            linkTextView.setTextSize(16);

                            // Make the link clickable
                            linkTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Create an Intent to view the video link
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink));

                                    // Check if any app can handle this intent
                                    if (intent.resolveActivity(getPackageManager()) != null) {
                                        // Open the link in the appropriate app (e.g., YouTube, WhatsApp, etc.)
                                        startActivity(intent);
                                    } else {
                                        // If no app is available, open the link in a browser
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink));
                                        startActivity(browserIntent);
                                    }
                                }
                            });

                            // Add the TextView to the container
                            linksContainer.addView(linkTextView);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    content.setText(e.getMessage().toString());
                }
            }
        },


                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(getApplicationContext(), "Error" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            // value Passing android to python
            @Override
            protected Map<String, String> getParams() {
                //   SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Map<String,String>params=new HashMap<String,String>();
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                params.put("cid", sh.getString("cid",""));//passing to python
                return params;
            }
        };

        int MY_SOCKET_TIMEOUT_MS = 100000;

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(postRequest);
    }
}