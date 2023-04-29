package com.example.unisicma;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Build;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ADMIN_LOGIN extends AppCompatActivity {
    private Toolbar toolbar;
    EditText Admin_Id, Password;
    TextInputLayout inputRCH, inputPass;
    public String ADMIN_ID, ADMIN_PASS;
    Button login_button;
    AlertDialog.Builder builder;
    String asha_login_url = "http://192.168.20.56/admin_login.php";

    SharedPreferences sharedPreferences;
    SQLite_db sqLite_db;

    public static final String AdminPREFERENCE = "Adminpref";
    public static final String ADM_id = "id_adm";
    public static final String ADM_name = "name_adm";
    public static final String ADM_mobile = "mobile_adm";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__login);

        sqLite_db = SQLite_db.getInstance(this);

        Admin_Id = findViewById(R.id.ADMIN_id);
        Password = findViewById(R.id.ADMIN_password);

        inputRCH = findViewById(R.id.inputRCH);
        inputPass = findViewById(R.id.inputPassword);


        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        builder = new AlertDialog.Builder(ADMIN_LOGIN.this);

        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        login_button = findViewById(R.id.admin_Login);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ADMIN_ID = Admin_Id.getText().toString();
                ADMIN_PASS = Password.getText().toString();

                if (ADMIN_ID.isEmpty() || ADMIN_PASS.isEmpty()) {
                    inputRCH.setError(getString(R.string.anm_id_validate));
                    inputPass.setError(getString(R.string.anm_pass_validate));


                } else {
                    if (ADMIN_ID.equals(" ") || ADMIN_PASS.equals(" ")) {
                        builder.setTitle("Something went wrong");
                        displayAlert("Enter a Valid ID and Password.!");
                    } else {
                       /* if (networkInfo != null && networkInfo.isConnected()) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, asha_login_url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONArray jsonArray = new JSONArray(response);
                                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                String code = jsonObject.getString("code");
                                                if (code.equals("login_failed")) {
                                                    builder.setTitle("Login Error..");
                                                    displayAlert(jsonObject.getString("message"));
                                                } else {
                                                    startActivity(new Intent(ADMIN_LOGIN.this, Dashboard.class));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(ADMIN_LOGIN.this, "Error", Toast.LENGTH_LONG).show();
                                    error.printStackTrace();

                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("ADMIN_ID", ADMIN_ID);
                                    params.put("ADMIN_PASS", ADMIN_PASS);
                                    return params;
                                }
                            };

                            MySingleton.getInstance(ADMIN_LOGIN.this).addToRequestQueue(stringRequest);
                        } else {
                        *
                        */

                            sqLite_db.open();
                            Cursor cursor = sqLite_db.getASHALogin(ADMIN_ID, ADMIN_PASS);

                            if (cursor != null) {

                                cursor.moveToFirst();

                                String id = cursor.getString(0);
                                String asha_name = cursor.getString(1);
                                String asha_mobile = cursor.getString(2);
                                sharedPreferences = getApplicationContext().getSharedPreferences(AdminPREFERENCE, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(ADM_id, id);
                                editor.putString(ADM_name, asha_name);
                                editor.putString(ADM_mobile, asha_mobile);
                                editor.apply();

                                sqLite_db.close();
                                startActivity(new Intent(ADMIN_LOGIN.this, SearchAnm.class));

                            } else {
                                builder.setTitle(getString(R.string.response));
                                displayAlert(getString(R.string.no_admin));

                            }

                        //}
                    }
                }
            }
        });


        Button clear = findViewById(R.id.admin_clear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Text = Admin_Id.getText().toString();
                String Text1 = Password.getText().toString();


                if (Text.isEmpty() && Text1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Already Empty !", Toast.LENGTH_SHORT).show();
                } else {
                    Admin_Id.setText("");
                    Password.setText("");

                }
            }
        });

        TextView textView2 = findViewById(R.id.new_registration);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), asha_Register.class));


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    public void displayAlert(String message) {
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Admin_Id.setText("");
                Password.setText("");

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

/*
    public void adm_login(View view) {

        final EditText editText = findViewById(R.id.ADMIN_id);
        final EditText editText1 = findViewById(R.id.ADMIN_password);


        String admin_id = editText.getText().toString();
        String admin_pass = editText1.getText().toString();
        String method = "login";
        admin_backend adminBackend = new admin_backend(this);
        adminBackend.execute(method, admin_id, admin_pass);
    }*/
}
