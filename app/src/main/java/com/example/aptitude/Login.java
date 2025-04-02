package com.example.aptitude;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText user_name,pass_word;
    Button submit,signup;
    public static String lid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_name = findViewById(R.id.editTextTextUsername);
        pass_word = findViewById(R.id.editTextTextPassword);
        signup = findViewById(R.id.signup);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        signup.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        // Check if the activity is a subpage (not the main entry point)
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

    @Override
    public void onClick(View view) {
        if (view == submit){
            String username = user_name.getText().toString();
            String password = pass_word.getText().toString();

            if (username.length()<1){
                user_name.setError("enter your username");
            }
            else if (password.length()<1){
                pass_word.setError("enter your password");
            }
            else{
                SharedPreferences sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String url= sh.getString("url","")+"and_login";
//                Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                try {


                                    JSONObject obj = new JSONObject(new String(response.data));

                                    if (obj.getString("status").equals("ok")) {
//                                        Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_SHORT).show();
                                        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


                                        String usertype = obj.getString("user_type");
//                                        Toast.makeText(getApplicationContext(), usertype, Toast.LENGTH_SHORT).show();
                                        lid = obj.getString("lid");
                                        if ("user".equals((usertype))) {
                                            Toast.makeText(getApplicationContext(), "User login successfully", Toast.LENGTH_SHORT).show();
                                            SharedPreferences.Editor ed = sh.edit();
                                            ed.putString("lid", obj.getString("lid"));
                                            ed.putString("uid", obj.getString("uid"));
                                            ed.putString("user_name", obj.getString("user_name"));
                                            ed.putString("user_level", obj.getString("user_level"));
                                            ed.putString("progress_value",obj.getString("progress_value"));
                                            ed.commit();
//                                            Intent in=new Intent(getApplicationContext(),gpstracker.class);
//                                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            startService(in);
                                            Intent j = new Intent(getApplicationContext(),Home.class);
                                            startActivity(j);

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Invalid user", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Handle the case where status is not "ok"
                                        Toast.makeText(getApplicationContext(), "Invalid user", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }) {


                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        SharedPreferences o = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        params.put("username", username);//passing to python
                        params.put("password", password);//passing to python

                        return params;
                    }


//                    @Override
//                    protected Map<String, DataPart> getByteData() {
//                        Map<String, DataPart> params = new HashMap<>();
//                        long imagename = System.currentTimeMillis();
////                        params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
//                        return params;
//                    }
                };

                Volley.newRequestQueue(this).add(volleyMultipartRequest);

            }
        }
        else{
            Intent i = new Intent(getApplicationContext(),Signup.class);
            startActivity(i);
        }

    }
}