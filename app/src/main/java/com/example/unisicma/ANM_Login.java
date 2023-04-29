package com.example.unisicma;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Build;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
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

public class ANM_Login extends AppCompatActivity {

    //----------------------Declaration of Layout Elements ------------
    EditText Anm_id, Password;
    TextInputLayout inputRCH, inputPass;
    public String ANM_ID, ANM_PASS, ANM_NAME, ANM_MOBILE;
    Button login_button;

    AlertDialog.Builder builder;

    // -----------------SQLite  Database Object -------------------
    SQLite_db sqLite_db;

    // ----------------- Assigning Shared Preference keys -----------------
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCE = "mypref";
    public static final String AN_id = "id_k";
    public static final String AN_name = "name_k";
    public static final String AN_mobile = "mobile_k";

    HashConvertor hashConvertor;
    String login_url = "http://192.168.20.56/login.php";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anm__login);

        // ---------------------------Toolbar section ---------------
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // ------------------------End Toolbar ------------------------------

        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // ------------------ Getting Instance of DB object ----------------
        sqLite_db = SQLite_db.getInstance(this);

        builder = new AlertDialog.Builder(ANM_Login.this);
        Anm_id = findViewById(R.id.anm_Id);
        Password = findViewById(R.id.anm_Password);
        inputRCH = findViewById(R.id.inputRCH);
        inputPass = findViewById(R.id.inputPassword);
        login_button = findViewById(R.id.anm_log);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ANM_ID = Anm_id.getText().toString();
                ANM_PASS = Password.getText().toString();// Top-level build file where you can add configuration options common to all sub-projects/modules.

                if (ANM_ID.isEmpty() || ANM_PASS.isEmpty()) {
                    inputRCH.setError(getString(R.string.anm_id_validate));
                    inputPass.setError(getString(R.string.anm_pass_validate));
                } else {

                    if (ANM_ID.equals(" ") || ANM_PASS.equals(" ")) {
                        builder.setTitle(getString(R.string.response));
                        displayAlert(getString(R.string.authenticate));
                    } else {
                        // if (networkInfo != null && networkInfo.isConnected()) {

                           /* StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url,
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
                                                    String id = jsonObject.getString("id");
                                                    String anm_name = jsonObject.getString("anm_name");
                                                    String anm_mobile = jsonObject.getString("anm_mobile");
                                                    sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCE, Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString(AN_id, id);
                                                    editor.putString(AN_name, anm_name);
                                                    editor.putString(AN_mobile, anm_mobile);

                                                    editor.apply();
                                                    startActivity(new Intent(ANM_Login.this, Dashboard.class));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(ANM_Login.this, "Error", Toast.LENGTH_LONG).show();
                                    error.printStackTrace();

                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("ANM_ID", ANM_ID);
                                    params.put("ANM_PASS", ANM_PASS);
                                    return params;
                                }
                            };
                            MySingleton.getInstance(ANM_Login.this).addToRequestQueue(stringRequest);

                            */
                        //   } else {
                        //--------------------SQLite Section-------------------------------------
                        sqLite_db.open();


                        //----------------------------Getting ANM details from anm sign up table ----------------
                        String Hashed_password = HashConvertor.getMd5(ANM_PASS);
                        Cursor cursor = sqLite_db.getANMLogin(ANM_ID, Hashed_password);
                        if (cursor != null && cursor.getCount()!= 0) {

                            cursor.moveToFirst();

                            String id = cursor.getString(0);
                            String mobile = cursor.getString(1);


                            //----------------------------------ANM Details and Facility Details----------------------------------------------

                            String ANM_url = "http://192.168.50.144:8081/api/anm/login";

                            //int int_id = Integer.parseInt(id);


                            //----------------------Instantiating second object of DB -------------------
                            final SQLiteInsert sqLiteInsert = SQLiteInsert.getInstance(getApplicationContext());
                            sqLiteInsert.open();

                            //-----------------Getting Login details from ANM master table ----------------
                            Cursor cursor1 = sqLite_db.getLoginDetails(id);
                            if (cursor1 != null) {
                                String anm_id = cursor1.getString(0);
                                String anm_name = cursor1.getString(1);
                                String anm_mobile = cursor1.getString(2);

                                // --------------------------Inserting Anm login details in shared preference -----------------
                                sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCE, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(AN_id, anm_id);
                                editor.putString(AN_name, anm_name);
                                editor.putString(AN_mobile, anm_mobile);
                                editor.apply();

                                // -----------------------Navigating to Dashboard after successful login --------------------
                                startActivity(new Intent(ANM_Login.this, Dashboard.class));
                                sqLite_db.close();

                            } else {

                                StringRequest stringRequest1 = new StringRequest(Request.Method.GET, ANM_url + "?anm_id=" + id,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONArray jsonArray = new JSONArray(response);
                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                                        String anm_id = jsonObject.getString("anm_id");
                                                        String anm_address = jsonObject.getString("anm_address");
                                                        String anm_name = jsonObject.getString("anm_name");
                                                        String anm_mobile = jsonObject.getString("anm_mobile");
                                                        String facility = jsonObject.getString("current_facility_id");
                                                        String join_date = jsonObject.getString("join_date");


                                                        // ----------------------- Inserting anm data into SQLite table from cassandra --------------------
                                                        boolean anm_inserted = sqLiteInsert.insertANM_master(anm_id, anm_name, "NA", anm_mobile, facility);
                                                        if (anm_inserted) {
                                                            Toast.makeText(getApplicationContext(), "Anm inserted", Toast.LENGTH_LONG).show();
                                                            sqLite_db.open();
                                                            Cursor anmLoginCursor = sqLite_db.getLoginDetails(anm_id);
                                                            if (anmLoginCursor != null) {
                                                                String an_id = anmLoginCursor.getString(0);
                                                                String an_name = anmLoginCursor.getString(1);
                                                                String an_mobile = anmLoginCursor.getString(2);

                                                                sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCE, Context.MODE_PRIVATE);
                                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                editor.putString(AN_id, an_id);
                                                                editor.putString(AN_name, an_name);
                                                                editor.putString(AN_mobile, an_mobile);
                                                                editor.apply();

                                                                startActivity(new Intent(ANM_Login.this, Dashboard.class));
                                                                sqLite_db.close();
                                                            }
                                                            boolean anmMap_inserted = sqLiteInsert.insertANM_mapping(anm_id, facility, join_date);
                                                            if (anmMap_inserted) {
                                                                Toast.makeText(getApplicationContext(), "Anm Mapping inserted", Toast.LENGTH_LONG).show();
                                                            }
                                                        }

                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                                        error.printStackTrace();

                /*Map<String, String> params = new HashMap<String, String>();
                params.put("anm_id",String.valueOf(33001));

                 */

                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        return super.getParams();
                                    }

                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {

                                        HashMap<String, String> headers = new HashMap<String, String>();
                                        headers.put("Content-Type", "application/json; charset=utf-8");
                                        return headers;
                                    }
                                };

                                MySingleton.getInstance(ANM_Login.this).addToRequestQueue(stringRequest1);

                                startActivity(new Intent(ANM_Login.this, Dashboard.class));
                                sqLiteInsert.close();
                            }

                        } else {
                            builder.setTitle(getString(R.string.response));
                            displayAlert(getString(R.string.invalidInput));

                        }
                        //   }
                        sqLite_db.close();


                    }
                }
            }
        });


        TextView textView = findViewById(R.id.New_Password);
        TextView textView2 = findViewById(R.id.new_registration);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ANM_Login.this, Dashboard.class);
                startActivity(intent);
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), Register.class));


            }
        });


        Button clear = findViewById(R.id.anm_clear);


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Text = Anm_id.getText().toString();
                String Text1 = Password.getText().toString();


                if (Text.isEmpty() && Text1.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Already Empty !", Toast.LENGTH_SHORT).show();
                } else {
                    Anm_id.setText("");
                    Password.setText("");
                }
            }
        });

    }

    public void displayAlert(String message) {
        builder.setMessage(message);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Anm_id.setText("");
                Password.setText("");

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


   /* public void go_login(View view) {


        final EditText editText = findViewById(R.id.anm_id);
        final EditText editText1 = findViewById(R.id.anm_password);


        anm_id = editText.getText().toString();
        password = editText1.getText().toString();

        String method = "anm_login";
        login_backend loginBackend = new login_backend(this);
        loginBackend.execute(method, anm_id, password);


    }*/


}
