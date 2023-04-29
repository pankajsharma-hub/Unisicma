package com.example.unisicma;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    EditText rch_id, Mobile, Password, Repassword, enterOTP;
    Button registerbtn, Clr_btn;
    String RCH_ID, MOBILE, PASSWORD, RE_PASS, codeSent;
    AlertDialog.Builder builder;
    ProgressBar progressBar;

    String reg_url = "http://192.168.42.202/sign_up.php";
    String register_url = "http://192.168.50.144:8081/api/anm/login";

    SQLite_db sqLite_db;
    LinearLayout linearLayout;
    int responseCode;

    Button SendOTP, VerifyOTP;
    FirebaseAuth firebaseAuth;

    boolean otpFlag = true;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rch_id = findViewById(R.id.RCH_id);
        Mobile = findViewById(R.id.mobile);
        Password = findViewById(R.id.PASSword);
        Repassword = findViewById(R.id.re_password);
        registerbtn = findViewById(R.id.Register);
        Clr_btn = findViewById(R.id.reg_clear);
        builder = new AlertDialog.Builder(Register.this);
        linearLayout = findViewById(R.id.linearRoot);

        SendOTP = findViewById(R.id.sendOTP);
        VerifyOTP = findViewById(R.id.verifyOTP);
        enterOTP = findViewById(R.id.enterOTP);
        progressBar = findViewById(R.id.progress_bar);
        firebaseAuth = FirebaseAuth.getInstance();

        ConnectivityManager connectivityManager;
        final NetworkInfo networkInfo;
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        SendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
            }
        });

        VerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode();
            }
        });


        sqLite_db = SQLite_db.getInstance(getApplicationContext());

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

                        // int int_anm_id = Integer.parseInt(RCH_ID);
                        if (networkInfo != null && networkInfo.isConnected()) {

                            if (otpFlag) {
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, register_url + "?anm_id=" + RCH_ID,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONArray jsonArray = new JSONArray(response);
                                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                    //String message = jsonObject.getString("Message");
                                                    if (jsonObject.has("anm_id") && !jsonObject.isNull("anm_id")) {
                                                        if (jsonObject.length() != 0) {

                                                            String anm_id = jsonObject.getString("anm_id");

                                                            String md5_password = HashConvertor.getMd5(PASSWORD);

                                                            if (sqLite_db.ANMRegister(RCH_ID, md5_password, MOBILE)) {
                                                                builder.setTitle("Response..");
                                                                builder.setMessage("You have been registered.!");
                                                                displayAlert("1");
                                                                AnmSignup(anm_id, md5_password, MOBILE);

                                                            } else {
                                                                builder.setTitle("Response..");
                                                                builder.setMessage("ANM ID already Registered.!");
                                                                displayAlert("0");

                                                            }


                                                        } else {

                                                            builder.setTitle("Response..");
                                                            builder.setMessage("ANM ID does not exists.!");
                                                            displayAlert("0");
                                                        }
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
                                        // params.put("RCH_ID", RCH_ID);
                                        // params.put("MOBILE", MOBILE);
                                        // params.put("PASSWORD", PASSWORD);
                                        //  params.put("RE_PASS", RE_PASS);
                                        return params;
                                    }

                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {

                                        HashMap<String, String> headers = new HashMap<String, String>();
                                        headers.put("Content-Type", "application/json; charset=utf-8");
                                        return headers;
                                    }
                                };
                                MySingleton.getInstance(Register.this).addToRequestQueue(stringRequest);
                            } else {
                                Snackbar.make(linearLayout, "Mobile number not verified", Snackbar.LENGTH_LONG).show();

                            }
                        } else {

                            Snackbar.make(linearLayout, "Not Connected to Internet Connection", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

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


        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


    }

    private void sendVerificationCode() {


        String phoneNumber = Mobile.getText().toString();
        firebaseAuth.setLanguageCode("hi");
        if (phoneNumber.isEmpty()) {
            Mobile.setError("Phone no is required");
            Mobile.requestFocus();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                 "+91"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("error", Objects.requireNonNull(e.getMessage()));
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.d( "onCodeSent:",  s);

            codeSent = s;
            SendOTP.setText("Resend OTP");
        }


    };


    private void verifyCode() {
        String enteredOtp = enterOTP.getText().toString();
        if (!enteredOtp.equals("") && enteredOtp.length() > 0) {
            progressBar.setVisibility(View.VISIBLE);

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, enteredOtp);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setVisibility(View.GONE);
                            otpFlag = true;
                            Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(getApplicationContext(), "Incorrect OTP", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void displayAlert(final String status) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (status.equals("input_error")) {
                    Password.setText("");
                    Repassword.setText("");
                } else if (status.equals("1")) {
                    startActivity(new Intent(getApplicationContext(), ANM_Login.class));
                } else if (status.equals("0")) {
                    Password.setText("");
                    Repassword.setText("");

                }
            }
        });
        builder.setNegativeButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(), ANM_Login.class));
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private boolean AnmSignup(String anm_id, String password, String mobile) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("anm_id", anm_id);
            jsonObject.put("password", password);
            jsonObject.put("mobile", mobile);

            String SIGN_UP_URL = "http://192.168.50.144:8081/api/anm/signup";


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SIGN_UP_URL,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Response", response.toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }

            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;

                }

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    responseCode = response.statusCode;
                    Log.e("network response", String.valueOf(response.statusCode));
                    return super.parseNetworkResponse(response);

                }

            };

            MySingleton.getInstance(Register.this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseCode == 200;
    }
}
