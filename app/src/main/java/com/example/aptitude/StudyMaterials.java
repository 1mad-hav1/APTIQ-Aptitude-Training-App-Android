package com.example.aptitude;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class StudyMaterials extends AppCompatActivity {
    TextView heading;
    ListView li;
    String[] title, id, difficulty, completed;
    Button bt_sent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_materials);

        li = findViewById(R.id.listView);
        heading = findViewById(R.id.heading);

        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String url = sh.getString("url", "") + "and_get_study_material";
        String contentType = getIntent().getStringExtra("content_type");
        heading.setText(contentType + " Study Materials");

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    if (jsonObj.getString("status").equalsIgnoreCase("ok")) {
                        JSONArray js = jsonObj.getJSONArray("data");
                        title = new String[js.length()];
                        difficulty = new String[js.length()];
                        completed = new String[js.length()];
                        id = new String[js.length()];
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject u = js.getJSONObject(i);
                            title[i] = u.getString("title");
                            id[i] = u.getString("id");
                            difficulty[i] = u.getString("difficulty");
                            completed[i] = u.getString("completed");
                        }
                        li.setAdapter(new CustomViewTopics(getApplicationContext(), title, id, difficulty, completed));
                    } else {
                        Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("content_type", contentType);
                params.put("uid",sh.getString("uid",""));
                return params;
            }
        };
        int MY_SOCKET_TIMEOUT_MS = 100000;
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(postRequest);

        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String cid = id[i],complete = completed[i];
                final CharSequence[] items1 = {"View more", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(StudyMaterials.this);
                builder.setItems(items1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items1[item].equals("View more")) {
                            SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(StudyMaterials.this);
                            SharedPreferences.Editor ed = sh.edit();
                            ed.putString("cid", cid);
                            ed.apply();
                            Intent j = new Intent(getApplicationContext(), DetailedContent.class);
                            j.putExtra("completed",complete);
                            startActivity(j);
                        } else if (items1[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        // Initialize footer navigation
        initializeFooter();
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



    private void initializeFooter() {
        findViewById(R.id.logical).setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), StudyMaterials.class);
            i.putExtra("content_type", "Logical");
            startActivity(i);
        });

        findViewById(R.id.verbal).setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), StudyMaterials.class);
            i.putExtra("content_type", "Verbal");
            startActivity(i);
        });

        findViewById(R.id.quantitative).setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), StudyMaterials.class);
            i.putExtra("content_type", "Quantitative");
            startActivity(i);
        });

        findViewById(R.id.ic_logical).setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), StudyMaterials.class);
            i.putExtra("content_type", "Logical");
            startActivity(i);
        });

        findViewById(R.id.ic_verbal).setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), StudyMaterials.class);
            i.putExtra("content_type", "Verbal");
            startActivity(i);
        });

        findViewById(R.id.ic_quantitative).setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), StudyMaterials.class);
            i.putExtra("content_type", "Quantitative");
            startActivity(i);
        });
    }
}
