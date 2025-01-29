package com.example.aptitude;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyProfile extends AppCompatActivity implements View.OnClickListener {
    TextView name, email, phone, place, dob, gender, username;
    ImageView profilepic;
    Button change, update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        username = findViewById(R.id.username);
        place = findViewById(R.id.place);
        dob = findViewById(R.id.dob);
        gender = findViewById(R.id.gender);
        profilepic = findViewById(R.id.profilepic);
        change = findViewById(R.id.changepass);
        update = findViewById(R.id.update);

        change.setOnClickListener(this);
        update.setOnClickListener(this);

        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String url = sh.getString("url", "") + "and_user_profile";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("status").equals("ok")) {
                        JSONArray js = obj.getJSONArray("data");
                        JSONObject js1 = js.getJSONObject(0);

                        name.setText(js1.getString("name"));
                        email.setText(js1.getString("email"));
                        phone.setText(js1.getString("phone"));
                        place.setText(js1.getString("place"));
                        dob.setText(js1.getString("dob"));
                        gender.setText(js1.getString("gender"));
                        username.setText(js1.getString("username"));

                        // To show image
                        String photoUrl = js1.getString("photo");
                        if (photoUrl == null || photoUrl.isEmpty() || photoUrl.equals("null")) {
                            // Set default profile picture if no image URL is provided
                            profilepic.setImageResource(R.drawable.user_jpg);
                        } else {
                            String fullUrl = PreferenceManager.getDefaultSharedPreferences(MyProfile.this).getString("iurl", "") + photoUrl;

                            // Load image using Picasso with error handling
                            Picasso.with(MyProfile.this)
                                    .load(fullUrl)
                                    .error(R.drawable.user_jpg) // Fallback to default image on error
                                    .transform(new CircleTransform()) // Optional circle transformation
                                    .into(profilepic, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            // Successfully loaded the image
                                        }

                                        @Override
                                        public void onError() {
                                            // Set default profile picture on loading failure
                                            profilepic.setImageResource(R.drawable.user_jpg);
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid user", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error handling
                        Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                        // Set default profile picture on network error
                        profilepic.setImageResource(R.drawable.user_jpg);
                    }
                }) {
            // Value passing Android to Python
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                params.put("uid", sh.getString("uid", "")); // Passing user ID to Python
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

    @Override
    public void onClick(View v) {
        if (v == change) {
            Intent i = new Intent(getApplicationContext(), ChangePassword.class);
            startActivity(i);
        } else if (v == update) {
            Intent i = new Intent(getApplicationContext(), UpdateProfile.class);
            startActivity(i);
        }
    }
}
