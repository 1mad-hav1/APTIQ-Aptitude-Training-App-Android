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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity implements View.OnClickListener {
    EditText ed_name, ed_email, ed_password, ed_username, ed_conpassword, ed_place, ed_dob, ed_phno;
    RadioButton rb_male,rb_female,rb_others;
    String gender="";
    Button bt_signup, bt_reset;
    ImageView bt_profile;
    public static String lid;
    String path, attach;
    byte[] byteArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ed_name = findViewById(R.id.editTextText4);
        ed_email = findViewById(R.id.editTextTextEmailAddress);
        ed_username = findViewById(R.id.editTextText5);
        ed_password = findViewById(R.id.editTextTextPassword3);
        ed_conpassword = findViewById(R.id.editTextNumberPassword);
        ed_place = findViewById(R.id.editTextText6);
        ed_dob = findViewById(R.id.editTextDate);
        ed_phno = findViewById(R.id.editTextPhone);
        rb_male = findViewById(R.id.radioButton5);
        rb_female = findViewById(R.id.radioButton6);
        rb_others= findViewById(R.id.radioButton7);
        bt_signup = findViewById(R.id.button5);
        bt_profile = findViewById(R.id.imageView);

        bt_signup.setOnClickListener(this);
        bt_profile.setOnClickListener(this);
        ed_dob.setOnClickListener(v -> {
            // Get the current date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(Signup.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the selected date and set it in the EditText
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        ed_dob.setText(date);
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    @Override
    public void onClick(View v) {
        if (v == bt_signup) {
            String name = ed_name.getText().toString();
            String email = ed_email.getText().toString();
            String username = ed_username.getText().toString();
            String password = ed_password.getText().toString();
            String conpassword = ed_conpassword.getText().toString();
            String place = ed_place.getText().toString();
            String dob = ed_dob.getText().toString();
            String phno= ed_phno.getText().toString();

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
            }
//            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                ed_email.setError("Enter a valid Email");
//            }
            else if (username.length() < 1) {
                ed_username.setError("Enter Your Username");
            } else if (password.length() < 1) {
                ed_password.setError("Enter the Password");
            }
//            else if (password.length() < 6) {
//                ed_password.setError("Password must be at least 6 characters long");
//            }
            else if (conpassword.length() < 1) {
                ed_conpassword.setError("Enter the Confirm Password");
            } else if (place.length() < 1) {
                ed_place.setError("Enter the Place");
            } else if (dob.length() < 1) {
                ed_dob.setError("Enter the Date of Birth");
            } else if (phno.length() < 1) {
                ed_phno.setError("Enter the Phone Number");
            } else if (gender.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please select your gender", Toast.LENGTH_LONG).show();
            } else if (! password.equals(conpassword)) {
                Toast.makeText(getApplicationContext(), "Password and Confirm Password does match", Toast.LENGTH_LONG).show();
            } else {
                SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String url = sh.getString("url", "") + "and_user_registration";
                VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                try {
                                    JSONObject obj = new JSONObject(new String(response.data));

                                    if (obj.getString("status").equals("ok")) {
                                        Toast.makeText(getApplicationContext(), "Registration success", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), Login.class);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
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
                        params.put("name",name);
                        params.put("email", email);
                        params.put("username",username);
                        params.put("password", password);
                        params.put("conpassword",conpassword);
                        params.put("place", place);
                        params.put("phone", phno);
                        params.put("gender", gender);
                        params.put("dob", dob);
                        params.put("photo", attach);
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
                        bt_profile.setImageResource(R.drawable.user_jpg); // Set default image if invalid file
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
                        bt_profile.setImageBitmap(myBitmap);
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
            if (bt_profile.getDrawable() == null || byteArray == null || attach == null) {
                bt_profile.setImageResource(R.drawable.user_jpg); // Replace 'default_profile' with your drawable resource name
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