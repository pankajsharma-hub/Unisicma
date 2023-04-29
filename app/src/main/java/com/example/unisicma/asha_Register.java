package com.example.unisicma;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class asha_Register extends AppCompatActivity {

    EditText rch_id, Mobile, Password, Repassword;
    Button registerbtn, Clr_btn;
    String RCH_ID, MOBILE, PASSWORD, RE_PASS;
    private Toolbar toolbar;

    AlertDialog.Builder builder;
    public String asha_reg = "http://192.168.20.56/asha_sign_up.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asha__register);
        builder = new AlertDialog.Builder(asha_Register.this);
        rch_id = findViewById(R.id.RCH_id);
        Mobile = findViewById(R.id.mobile);
        Password = findViewById(R.id.PASSword);
        Repassword = findViewById(R.id.re_password);
        registerbtn = findViewById(R.id.asha_Register);
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RCH_ID = rch_id.getText().toString();
                MOBILE = Mobile.getText().toString();
                PASSWORD = Password.getText().toString();
                RE_PASS = Repassword.getText().toString();
                if (RCH_ID.equals("") || MOBILE.equals("") || PASSWORD.equals("") || RE_PASS.equals("")) {
                    builder.setTitle("Status");
                    builder.setMessage("Please fill All the Required fields.!");
                    displayAlert("input_error");
                } else {
                    if (!PASSWORD.equals(RE_PASS)) {
                        builder.setTitle("Status");
                        builder.setMessage("Passwords does not match.!");
                        displayAlert("input_error");
                    } else {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, asha_reg,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                String status = jsonObject.getString("Status");
                                                String message = jsonObject.getString("Message");
                                                builder.setTitle("Response..");
                                                builder.setMessage(message);
                                                displayAlert(status);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("RCH_ID", RCH_ID);
                                params.put("MOBILE", MOBILE);
                                params.put("PASSWORD", PASSWORD);
                                params.put("RE_PASS", RE_PASS);
                                return params;
                            }
                        };
                        MySingleton.getInstance(asha_Register.this).addToRequestQueue(stringRequest);
                    }
                }
            }
        });


        Clr_btn = findViewById(R.id.reg_clear);
        Clr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RCH_ID = rch_id.getText().toString();
                MOBILE = Mobile.getText().toString();
                PASSWORD = Password.getText().toString();
                RE_PASS = Repassword.getText().toString();

                if (RCH_ID.isEmpty() && MOBILE.isEmpty() && PASSWORD.isEmpty() && RE_PASS.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Already Empty", Toast.LENGTH_SHORT).show();
                } else {
                    rch_id.setText("");
                    Mobile.setText("");
                    Password.setText("");
                    Repassword.setText("");
                }

            }
        });


        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    public void displayAlert(final String status) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (status.equals("input_error")) {
                    Password.setText("");
                    Repassword.setText("");
                } else if (status.equals(1)) {
                    startActivity(new Intent(getApplicationContext(), ANM_Login.class));
                } else if (status.equals(0)) {
                    Password.setText("");
                    Repassword.setText("");

                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /*public void ashaReg(View view) {
        RCH_ID = rch_id.getText().toString();
        MOBILE = Mobile.getText().toString();
        PASSWORD = Password.getText().toString();
        RE_PASS = Repassword.getText().toString();
        if (!RCH_ID.isEmpty()) {
            if (PASSWORD.equals(RE_PASS) && !PASSWORD.isEmpty()) {


                String method = "asha_register";
                login_backend loginBackend = new login_backend(this);
                loginBackend.execute(method, RCH_ID, MOBILE, PASSWORD, RE_PASS);

            } else {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            }
        } else {
            rch_id.setError("Please Enter RCH ID");
        }

    }
    */

}
