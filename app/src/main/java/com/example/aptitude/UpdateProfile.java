package com.example.aptitude;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity implements View.OnClickListener {
    EditText ed_name, ed_email, ed_phone, ed_place, ed_dob, ed_username;
    RadioButton rb_male,rb_female,rb_others;
    String gender="";
    ImageView profilepic;
    Button bt_submit;
    String path, attach;
    byte[] byteArray = null;
    JSONObject js1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Initialize views
        profilepic = findViewById(R.id.profilepic);
        ed_name = findViewById(R.id.name);
        ed_email = findViewById(R.id.email);
        ed_phone = findViewById(R.id.phone);
        ed_place = findViewById(R.id.place);
        ed_dob = findViewById(R.id.dob);
        ed_username = findViewById(R.id.username);
        rb_male = findViewById(R.id.radioButton5);
        rb_female = findViewById(R.id.radioButton6);
        rb_others = findViewById(R.id.radioButton7);
        bt_submit = findViewById(R.id.button9);
        bt_submit.setOnClickListener(this);
        profilepic.setOnClickListener(this);
        ed_dob.setOnClickListener(v -> {
            // Get the current date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateProfile.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the selected date and set it in the EditText
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        ed_dob.setText(date);
                    },
                    year, month, day);
            datePickerDialog.show();
        });
        // Set default profile picture
        profilepic.setImageResource(R.drawable.user_jpg); // Replace `default_profile` with your default drawable resource

        // Load existing profile picture if available
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
                        js1 = js.getJSONObject(0);
                        ed_name.setText(js1.getString("name"));
                        ed_email.setText(js1.getString("email"));
                        ed_phone.setText(js1.getString("phone"));
                        ed_place.setText(js1.getString("place"));
                        ed_dob.setText(js1.getString("dob"));
                        ed_username.setText(js1.getString("username"));
                        String gender = js1.getString("gender");

                        if (gender.equalsIgnoreCase("male")) {
                            rb_male.setChecked(true);
                        } else if (gender.equalsIgnoreCase("female")) {
                            rb_female.setChecked(true);
                        } else if (gender.equalsIgnoreCase("others")) {
                            rb_others.setChecked(true);
                        }

                        // Load the profile picture
                        String photoUrl = js1.optString("photo");
                        if (photoUrl == null || photoUrl.isEmpty()) {
                            // Set default image if no profile photo is available
                            profilepic.setImageResource(R.drawable.user_jpg);
                        } else {
                            String fullUrl = PreferenceManager.getDefaultSharedPreferences(UpdateProfile.this)
                                    .getString("iurl", "") + photoUrl;

                            Picasso.with(UpdateProfile.this)
                                    .load(fullUrl)
                                    .placeholder(R.drawable.user_jpg) // Show default image while loading
                                    .error(R.drawable.user_jpg) // Show default image if error occurs
                                    .transform(new CircleTransform()) // Optional: Circular transformation
                                    .into(profilepic);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid user", Toast.LENGTH_SHORT).show();
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
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                params.put("uid", sh.getString("uid", "")); // Passing user ID to server
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
        if (v == bt_submit) {
            String name = ed_name.getText().toString();
            String email = ed_email.getText().toString();
            String phone = ed_phone.getText().toString();
            String place = ed_place.getText().toString();
            String dob = ed_dob.getText().toString();
            String username = ed_username.getText().toString();

            if(rb_male.isChecked())
            {
                gender = "Male";
            }else if (rb_female.isChecked()) {
                gender = "Female";
            }
            else if(rb_others.isChecked()) {
                gender = "others";
            }

            if (name.length() < 1) {
                ed_name.setError("Enter Your Name");
            } else if (email.length() < 1) {
                ed_email.setError("Enter Your Email");
            } else if (username.length() < 1) {
                ed_email.setError("Enter Your Username");
            } else if (place.length() < 1) {
                ed_place.setError("Enter the Place");
            } else if (dob.length() < 1) {
                ed_dob.setError("Enter the Date of Birth");
            } else if (phone.length() < 1) {
                ed_phone.setError("Enter the Phone Number");
            }
            else if (gender.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please select your gender", Toast.LENGTH_LONG).show();
            }
            else {
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String url = sh.getString("url", "") + "and_user_update_profile";
                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                try {
                                    JSONObject obj = new JSONObject(new String(response.data));

                                    if (obj.getString("status").equals("ok")) {
                                        Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), MyProfile.class);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Updation failed", Toast.LENGTH_SHORT).show();
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
                        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        params.put("uid", sh.getString("uid", ""));
                        params.put("lid", sh.getString("lid", ""));
                        params.put("name", name);
                        params.put("email", email);
                        params.put("phone", phone);
                        params.put("place", place);
                        params.put("dob", dob);
                        params.put("gender", gender);
                        params.put("username", username);

                        // Only add the "photo" parameter if a new photo has been uploaded
                        if (attach != null && !attach.isEmpty()) {
                            params.put("photo", attach);
                        }

                        return params;
                    }

                };

                Volley.newRequestQueue(this).add(volleyMultipartRequest);
            }
        } else {
            showFileChooser(1);
        }
    }
    void showFileChooser(int string) {         //       function to select image
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), string);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                try {
                    path = FileUtils.getPath(this, uri);

                    File imgFile = new File(path);

                    // Check if the file is a .jpg format
                    if (!path.toLowerCase().endsWith(".jpg") && !path.toLowerCase().endsWith(".jpeg")) {
                        Toast.makeText(this, "Only JPG format is allowed. Please choose a valid image.", Toast.LENGTH_LONG).show();
                        profilepic.setImageResource(R.drawable.user_jpg); // Set default image if invalid file
                        attach = ""; // Reset attach if invalid file
                        return; // Exit without further processing
                    }

                    // Check if the image size exceeds 10 MB
                    if (checkFileSize(imgFile, 10)) {
                        Toast.makeText(this, "Image size exceeds 10 MB. Please choose a smaller file.", Toast.LENGTH_LONG).show();
                        return; // Exit without updating the ImageView
                    }

                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        profilepic.setImageBitmap(myBitmap);
                    }

                    // Convert file to byte array
                    File file = new File(path);
                    byte[] b = new byte[8192];
                    InputStream inputStream = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int bytesRead;
                    while ((bytesRead = inputStream.read(b)) != -1) {
                        bos.write(b, 0, bytesRead);
                    }
                    byteArray = bos.toByteArray();

                    // Base64 encoding of image for uploading as string (Optional)
                    attach = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            // If no photo is selected, set a default image from drawable
            if (profilepic.getDrawable() == null || byteArray == null || attach == null) {
                profilepic.setImageResource(R.drawable.user_jpg); // Replace 'default_profile' with your drawable resource name
                attach = ""; // Set attach to an empty string or a placeholder if required
            }
        }
    }


    // Function to check if the file size exceeds the given limit in MB
    private boolean checkFileSize(File file, int maxSizeInMB) { // Added function
        long fileSizeInBytes = file.length();
        long fileSizeInMB = fileSizeInBytes / (1024 * 1024);
        return fileSizeInMB > maxSizeInMB;
    }


}