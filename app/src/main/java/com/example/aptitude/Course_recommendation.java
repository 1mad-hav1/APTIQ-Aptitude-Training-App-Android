package com.example.aptitude;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class Course_recommendation extends AppCompatActivity {

    EditText inputOpenness, inputConscientiousness, inputExtraversion, inputAgreeableness,
            inputNeuroticism, inputNumericalAptitude, inputSpatialAptitude,
            inputPerceptualAptitude, inputAbstractReasoning, inputVerbalReasoning;
    Button btnPredict;
    TextView textResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_recommendation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inputOpenness = findViewById(R.id.input_openness);
        inputConscientiousness = findViewById(R.id.input_conscientiousness);
        inputExtraversion = findViewById(R.id.input_extraversion);
        inputAgreeableness = findViewById(R.id.input_agreeableness);
        inputNeuroticism = findViewById(R.id.input_neuroticism);
        inputNumericalAptitude = findViewById(R.id.input_numerical_aptitude);
        inputSpatialAptitude = findViewById(R.id.input_spatial_aptitude);
        inputPerceptualAptitude = findViewById(R.id.input_perceptual_aptitude);
        inputAbstractReasoning = findViewById(R.id.input_abstract_reasoning);
        inputVerbalReasoning = findViewById(R.id.input_verbal_reasoning);

        // Initializing button and text view
        btnPredict = findViewById(R.id.btn_predict);
        textResult = findViewById(R.id.text_result);

        // Set button click listener
        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predictCourse();
            }
        });



    }
    private void predictCourse() {
        // Collect input values
        String openness = inputOpenness.getText().toString().trim();

        String conscientiousness = inputConscientiousness.getText().toString().trim();
        String extraversion = inputExtraversion.getText().toString().trim();
        String agreeableness = inputAgreeableness.getText().toString().trim();
        String neuroticism = inputNeuroticism.getText().toString().trim();
        String numericalAptitude = inputNumericalAptitude.getText().toString().trim();
        String spatialAptitude = inputSpatialAptitude.getText().toString().trim();
        String perceptualAptitude = inputPerceptualAptitude.getText().toString().trim();
        String abstractReasoning = inputAbstractReasoning.getText().toString().trim();
        String verbalReasoning = inputVerbalReasoning.getText().toString().trim();
        SharedPreferences sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String url=sh.getString("url","")+"d";// user_login is function name
        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>(){
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {

                            JSONObject obj = new JSONObject(new String(response.data));

                            if(obj.getString("status").equals("ok")){
//                                Toast.makeText(getApplicationContext(), "Login success",Toast.LENGTH_SHORT).show();
//                                SharedPreferences sh= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                                SharedPreferences.Editor ed=sh.edit();
//                                ed.putString("lid",obj.getString("lid"));
//                                ed.putString("uid",obj.getString("uid"));
//                                ed.commit();
//                                Intent i = new Intent(getApplicationContext(),homenew.class);
//                                startActivity(i);
                                textResult.setVisibility(View.VISIBLE);
                                textResult.setText(obj.getString("result"));
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Invalid User" ,Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"----" +e.getMessage().toString(),Toast.LENGTH_SHORT).show();

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
                params.put("openness", openness);//passing to python
                params.put("conscientiousness", conscientiousness);//passing to python
                params.put("extraversion", extraversion);//passing to python
                params.put("agreeableness", agreeableness);//passing to python
                params.put("neuroticism", neuroticism);//passing to python
                params.put("numericalAptitude", numericalAptitude);//passing to python
                params.put("spatialAptitude", spatialAptitude);//passing to python

                params.put("perceptualAptitude", perceptualAptitude);//passing to python
                params.put("abstractReasoning", abstractReasoning);//passing to python

                params.put("verbalReasoning", verbalReasoning);//passing to python

                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                long imagename = System.currentTimeMillis();
//                        params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
        // You can process the input here and integrate with the trained model
        // For now, we are displaying a dummy result

    }
}