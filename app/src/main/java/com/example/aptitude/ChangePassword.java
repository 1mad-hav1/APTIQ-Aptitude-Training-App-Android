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

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    EditText ed_current, ed_newpass, ed_confirmpass;
    Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ed_current = findViewById(R.id.editTextCurrentPassword);
        ed_newpass = findViewById(R.id.editTextNewPassword);
        ed_confirmpass = findViewById(R.id.editTextConfirmPassword);
        bt_submit = findViewById(R.id.submit);

        bt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == bt_submit) {
            String current = ed_current.getText().toString();
            String newpass = ed_newpass.getText().toString();
            String confirmpass = ed_confirmpass.getText().toString();

            if (current.length() < 1) {
                ed_current.setError("Enter the Password");
            }
            else if (newpass.length() < 1) {
                ed_newpass.setError("Enter the Password");
            }
            else if (confirmpass.length() < 1) {
                ed_confirmpass.setError("Enter the Password");
            } else if (! confirmpass.equals(newpass)) {
                Toast.makeText(getApplicationContext(), ("New Password and Confirm password does not match"), Toast.LENGTH_LONG).show();
            } else {
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String url = sh.getString("url", "") + "and_user_change_password";
                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                try {
                                    JSONObject obj = new JSONObject(new String(response.data));

                                    if (obj.getString("status").equals("ok")) {
                                        Toast.makeText(getApplicationContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), Home.class);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Password Change Failed", Toast.LENGTH_SHORT).show();
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
                        Map<String, String> params = new HashMap<>();
                        params.put("current",current);
                        params.put("newpass", newpass);
                        params.put("confirmpass",confirmpass);
                        params.put("lid",sh.getString("lid",""));
                        return params;
                    }
                };

                Volley.newRequestQueue(this).add(volleyMultipartRequest);
            }
        }
    }
}

