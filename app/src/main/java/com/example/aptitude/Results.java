
package com.example.aptitude;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class Results extends AppCompatActivity {
    ListView li;
    String[] testname, testdate, marks_scored, pass_fail, id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        li = findViewById(R.id.results_listView);

        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String url = sh.getString("url", "") + "and_get_results";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    if (jsonObj.getString("status").equalsIgnoreCase("ok")) {
                        JSONArray js = jsonObj.getJSONArray("results");
                        testname = new String[js.length()];
                        testdate = new String[js.length()];
                        marks_scored = new String[js.length()];
                        pass_fail = new String[js.length()];
                        id = new String[js.length()];
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject u = js.getJSONObject(i);
                            testname[i] = u.getString("test_name");
                            testdate[i] = u.getString("test_date");
                            marks_scored[i] = u.getString("mark_scored");
                            pass_fail[i] = u.getString("pass_fail");
                            id[i] = u.getString("result_id");
                        }
                        li.setAdapter(new CustomViewResults(getApplicationContext(), id, testname, testdate, marks_scored, pass_fail));
                    } else {
                        // Find the TextView by its ID
                        TextView noResultsTextView = findViewById(R.id.noResultsTextView);

                        // Show the message by setting the visibility to VISIBLE
                        noResultsTextView.setVisibility(View.VISIBLE);
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
                params.put("uid", sh.getString("uid",""));
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
                String rid = id[i];
                final CharSequence[] items1 = {"View more", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(Results.this);
                builder.setItems(items1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items1[item].equals("View more")) {
                            SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(Results.this);
                            SharedPreferences.Editor ed = sh.edit();
                            Intent j = new Intent(getApplicationContext(), TestResult.class);
                            j.putExtra("result_id", Integer.parseInt(rid));
                            startActivity(j);
                        } else if (items1[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
    }
}