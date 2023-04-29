package com.example.unisicma;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.datastax.driver.core.LocalDate;
import com.google.android.material.snackbar.Snackbar;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class
Dashboard extends AppCompatActivity {

    public static String bar_code_result;

    Button Scan_btn;
    Button workplan, RIP, Due_List;
    ProgressBar progressBar;
    LinearLayout progressParent;
    GridLayout gridLayout;
    int responseCode;
    int immunization_records, vCount;

   // String workplan_url = "http://192.168.20.56/workplan.php";
   // String facility_url = "http://192.168.20.56/facility_details.php";
   // String date_url = "http://192.168.20.56/search_date.php";
    AlertDialog.Builder builder;

    public static final String AN_id = "id_k";
    public static final String AN_name = "name_k";
    public static final String AN_mobile = "mobile_k";
    String anm_id, errorResponse, formattedDate;

    Context context;
    private ConnectivityManager connectivityManager;

    SQLite_db sqLite_db;
    SQLiteInsert sqLiteInsert;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedText = intent.getStringExtra("com.example.unisicma.EXTRA_TEXT");
            showAlert(receivedText);
        }
    };

    Calendar calendar = Calendar.getInstance();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String startDate = dateFormat.format(calendar.getTime());

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString(AN_id, null);
        String anmName = sharedPreferences.getString(AN_name, null);
        String anmMobile = sharedPreferences.getString(AN_mobile, null);

        SharedPreferences preferences = getSharedPreferences("save to all activity", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            assert language != null;
            if (language.contains("hi") || language.contains("ta")) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.sarojini));
            } else {
                Objects.requireNonNull(getSupportActionBar()).setTitle(anmName);
            }
        }
        toolbar.setLogo(R.drawable.ic_face_4dp);

        toolbar.setSubtitle(getString(R.string.anm_mobile) + ": " + anmMobile);

        loadlocale();

        connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        sqLite_db = SQLite_db.getInstance(this);

        //subtitle.setText("ANM ID:"+id);

        /* ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

         */
        sqLiteInsert = SQLiteInsert.getInstance(getApplicationContext());
        builder = new AlertDialog.Builder(Dashboard.this);

        workplan = findViewById(R.id.Immunization_Rec);
        RIP = findViewById(R.id.RIP);
        Due_List = findViewById(R.id.Duelist);
        progressBar = findViewById(R.id.progressBar);
        progressParent = findViewById(R.id.progressParent);
        gridLayout = findViewById(R.id.gridLayout);

        errorResponse = "NoError";

        Scan_btn = findViewById(R.id.scan_btn);
        Scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
startActivity(new Intent(Dashboard.this,AboutApp.class));
            }
        });


        Due_List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] listItems = {getString(R.string.date_wise), getString(R.string.vaccine_wise)};
                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Dashboard.this);
                mBuilder.setTitle(getString(R.string.choose));
                mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {

                            Search();

                        } else if (i == 1) {


                            Intent intent = new Intent(Dashboard.this, Show_Due_List.class);
                            startActivity(intent);

                        }
                        dialog.dismiss();
                    }

                });
                androidx.appcompat.app.AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });

        RIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(Dashboard.this, RI_display.class));

                androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Dashboard.this);
                mBuilder.setTitle(getString(R.string.choose));
                mBuilder.setMessage(getString(R.string.card));
                mBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences1 = getSharedPreferences("RI_preference", Context.MODE_PRIVATE);
                        String child_id = sharedPreferences1.getString("CHILD_ID", null);
                        if (child_id != null) {
                            Intent intent = new Intent(Dashboard.this, RI_Immunization.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Dashboard.this, barcode_scanner.class);
                            startActivity(intent);
                        }
                    }
                });
                mBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Dashboard.this, RI_Immunization.class);
                        startActivity(intent);
                    }
                });

                androidx.appcompat.app.AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        IntentFilter filter = new IntentFilter("com.example.unisicma.EXAMPLE_ACTION");
        registerReceiver(broadcastReceiver, filter);

        assert language != null;
        if (language.contains("hi") || language.contains("ta")) {
            Cursor cursor = sqLiteInsert.getBlankChildren(anm_id);
            if (cursor != null) {
                if (cursor.getCount() != 0) {
                    Intent intent = new Intent("com.example.unisicma.EXAMPLE_ACTION");
                    intent.putExtra("com.example.unisicma.EXTRA_TEXT", getString(R.string.blankNames));
                    //sendBroadcast(intent);

                    Cursor cursor1 = sqLiteInsert.getRequest(id, startDate);
                    if (cursor1 != null && cursor1.getCount() > 0) {
                        Toast.makeText(getApplicationContext(), "Request already sent", Toast.LENGTH_LONG).show();
                    } else {
                        showAlert(getString(R.string.blankNames));

                    }

                }
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();

                Toast.makeText(this, "Logout option selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.changelang:
                showChangeLanguageDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"English", "हिंदी", "தமிழ்"};
        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Dashboard.this);
        mBuilder.setTitle("Choose Language..");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    setLocal("en");
                    recreate();

                } else if (i == 1) {
                    setLocal("hi");
                    recreate();
                } else if (i == 2) {
                    setLocal("ta");
                    recreate();
                }
                dialogInterface.dismiss();

            }

        });
        androidx.appcompat.app.AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocal(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("save to all activity", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadlocale() {
        SharedPreferences preferences = getSharedPreferences("save to all activity", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocal(language);


    }

    public void goWorkplan(View view) {

        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Checking if network is available or not---------------------------
        /*
        if (networkInfo != null && networkInfo.isConnected()) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, workplan_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                            Intent intent = new Intent(Dashboard.this, Workplan.class);
                            Bundle bundle = new Bundle();
                            result = response;
                            bundle.putString("result", result);
                            intent.putExtras(bundle);
                            startActivity(intent);


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();
                    SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
                    anm_id = sharedPreferences.getString(AN_id, null);
                    params.put("anm_id", anm_id);
                    return params;
                }
            };
            MySingleton.getInstance(Dashboard.this).addToRequestQueue(stringRequest);
        } else {

         */


        final String[] listItems = {getString(R.string.ww), getString(R.string.daywise)};
        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Dashboard.this);
        mBuilder.setTitle(getString(R.string.choose));
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0) {

                    Intent intent = new Intent(Dashboard.this, Workplan.class);
                    startActivity(intent);
                } else if (i == 1) {

                    Intent intent = new Intent(Dashboard.this, Tabbed.class);
                    startActivity(intent);

                }
                dialog.dismiss();
            }

        });
        androidx.appcompat.app.AlertDialog mDialog = mBuilder.create();
        mDialog.show();


        //}
    }


    public void Search() {


        //  if (userInput.matches("[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])")) {
            /*    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                Date date = null;
                try {
                    date = simpleDateFormat.parse(userInput);
                    date1 = simpleDateFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


             */
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Checking if network is available or not---------------------------

        /*
        if (networkInfo != null && networkInfo.isConnected()) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, date_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Intent intent = new Intent(Dashboard.this, DueList.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("result", response);


                            intent.putExtras(bundle);
                            startActivity(intent);


                            Toast.makeText(getApplicationContext(), anm_id, Toast.LENGTH_LONG).show();


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();
                    SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
                    anm_id = sharedPreferences.getString(AN_id, null);

                    params.put("anm_id", anm_id);
                    // params.put("value", date1);
                    return params;
                }
            };
            MySingleton.getInstance(Dashboard.this).addToRequestQueue(stringRequest);
        } else {
*/
        Intent intent = new Intent(this, DueList.class);
        Bundle bundle = new Bundle();
        String code = "DateWise";
        bundle.putString("code", code);
        intent.putExtras(bundle);
        startActivity(intent);
        //  }
           /*}
          else {
               Toast.makeText(getApplicationContext(), "Invalid Date Format! Date should be in 'yyyy-MM-dd' format", Toast.LENGTH_LONG).show();


          }

            */


    }


    //-------------------------------Upload data to Cassandra DB----------------------
    public boolean SyncData() {

        /*final CassandraConnector client = new CassandraConnector(Dashboard.this);
        final String ipAddress = "10.0.2.2";
        final int port = 9042;
        System.out.println("Connecting to IP Address " + ipAddress + ":" + port + "...");
        client.execute(ipAddress, block, phc, sc, facility);

         */
        String Get_url = "http://192.168.50.144:8081/api/children/allChildren";

        SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
        anm_id = sharedPreferences.getString(AN_id, null);

//int int_anm_id = Integer.parseInt(anm_id);
        final SQLiteInsert sqLiteInsert = SQLiteInsert.getInstance(this);
        sqLiteInsert.open();
        String delete_update = sqLiteInsert.DeleteData();

        Toast.makeText(getApplicationContext(), delete_update, Toast.LENGTH_LONG).show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Get_url + "?anm_id=" + anm_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            String resident_facility = null;
                            String resident_sc = null;
                            String resident_state = null;
                            String resident_district = null;
                            String resident_block = null;
                            String resident_phc = null;
                            String resident_village = null;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String child_id = jsonObject.getString("rch_child_id");
                                String child_name = jsonObject.getString("child_name");
                                String child_dob = jsonObject.getString("child_dob");
                                String birth_weight = jsonObject.getString("birth_weight");
                                String anm_id = jsonObject.getString("anm_id");
                                String child_sex = jsonObject.getString("child_sex");
                                String deilivery_facility = jsonObject.getString("delivery_facility_id");
                                String enroll_date = jsonObject.getString("enrollment_date");
                                String father_mobile = jsonObject.getString("father_mobile");
                                String father_name = jsonObject.getString("father_name");
                                String mother_mobile = jsonObject.getString("mother_mobile");
                                String mother_name = jsonObject.getString("mother_name");
                                String mother_id = jsonObject.getString("rch_mother_id");
                                resident_block = jsonObject.getString("resident_block");
                                resident_district = jsonObject.getString("resident_district");
                                resident_facility = jsonObject.getString("resident_facility_id");
                                resident_phc = jsonObject.getString("resident_phc");
                                resident_sc = jsonObject.getString("resident_sc");
                                resident_state = jsonObject.getString("resident_state");
                                resident_village = jsonObject.getString("resident_village");

                                if ((jsonObject.has("v1") && !jsonObject.isNull("v1")) && (jsonObject.has("v2") && !jsonObject.isNull("v2")) && (jsonObject.has("v4") && !jsonObject.isNull("v4"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v1");


                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");

                                    sqLiteInsert.insertChild_vaccination(child_id, "V1", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                    sqLiteInsert.insertChild_vaccination(child_id, "V2", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                    sqLiteInsert.insertChild_vaccination(child_id, "V4", anm_id_at, facility_id, weight_at, date_at, barcode_no);


                                }
                                if ((jsonObject.has("v5") && !jsonObject.isNull("v5"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v5");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");

                                    sqLiteInsert.insertChild_vaccination(child_id, "V5", anm_id_at, facility_id, weight_at, date_at, barcode_no);

                                }
                                if ((jsonObject.has("v6") && !jsonObject.isNull("v6"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v6");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V6", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }

                                if ((jsonObject.has("v7") && !jsonObject.isNull("v7"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v7");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V7", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }

                                if ((jsonObject.has("v8") && !jsonObject.isNull("v8"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v8");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V8", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }
                                if ((jsonObject.has("v9") && !jsonObject.isNull("v9"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v9");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V9", anm_id_at, facility_id, weight_at, date_at, barcode_no);

                                }

                                if ((jsonObject.has("v10") && !jsonObject.isNull("v10"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v10");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V10", anm_id_at, facility_id, weight_at, date_at, barcode_no);

                                }

                                if ((jsonObject.has("v11") && !jsonObject.isNull("v11"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v11");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V11", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }

                                if ((jsonObject.has("v12") && !jsonObject.isNull("v12"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v12");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V12", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }

                                if ((jsonObject.has("v13") && !jsonObject.isNull("v13"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v13");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V13", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }

                                if ((jsonObject.has("v17") && !jsonObject.isNull("v17"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v17");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V17", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }

                                if ((jsonObject.has("v14") && !jsonObject.isNull("v14"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v14");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V14", anm_id_at, facility_id, weight_at, date_at, barcode_no);


                                }

                                if ((jsonObject.has("v15") && !jsonObject.isNull("v15"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v15");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V15", anm_id_at, facility_id, weight_at, date_at, barcode_no);

                                }

                                if ((jsonObject.has("v16") && !jsonObject.isNull("v16"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v16");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V16", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }

                                if ((jsonObject.has("v18") && !jsonObject.isNull("v18"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v18");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V18", anm_id_at, facility_id, weight_at, date_at, barcode_no);

                                }

                                if ((jsonObject.has("v19") && !jsonObject.isNull("v19"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v19");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V19", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }

                                if ((jsonObject.has("v20") && !jsonObject.isNull("v20"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v20");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V20", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }

                                if ((jsonObject.has("v3") && !jsonObject.isNull("v3"))) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v3");

                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V3", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }


                                if (jsonObject.has("v21") && !jsonObject.isNull("v21")) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("v21");


                                    String date_at = jsonObject1.getString("date_at");
                                    String weight_at = jsonObject1.getString("weight_at_vaccination");
                                    String anm_id_at = jsonObject1.getString("anm_id_at_vaccination");
                                    String facility_id = jsonObject1.getString("facility_id");
                                    String barcode_no = jsonObject1.getString("barcode_no");


                                    sqLiteInsert.insertChild_vaccination(child_id, "V21", anm_id_at, facility_id, weight_at, date_at, barcode_no);
                                }

                                boolean mother_inserted = sqLiteInsert.insertMother_master(mother_id, mother_name, mother_mobile, father_name, father_mobile);
                                if (mother_inserted) {
                                    Log.d("download result", "mother master downloaded.!");
                                }
                                boolean child_inserted = sqLiteInsert.insertChild_master(child_id, child_name, mother_id, enroll_date, child_sex, child_dob, birth_weight, deilivery_facility, resident_facility, null, null);
                                if (child_inserted) {
                                    // Toast.makeText(getApplicationContext(), "Download Complete", Toast.LENGTH_LONG).show();
                                }


                            }

                            //----------------------------Getting Facility Details----------------------------------------------------

                            boolean facility_inserted = sqLiteInsert.insertFacility_master(resident_facility, resident_sc, resident_sc, resident_state, resident_district, resident_block, resident_phc, resident_sc, resident_village);
                            if (facility_inserted) {
                                //  Toast.makeText(getApplicationContext(), "Download Complete", Toast.LENGTH_SHORT).show();
                            }
                            sqLiteInsert.close();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorResponse = "Unable to reach Server";
                 Toast.makeText(getApplicationContext(), errorResponse, Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(Dashboard.this).addToRequestQueue(stringRequest);

        return true;

    }


    //---------------------------------------------Upload Data----------------------------------------
//----------------------------------------------------------------------------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean uploadData() {

//        String result = insertIntoChild("888881000086", "v6", "33001", "5.5", "2020-01-01", "12345");
      Toast.makeText(getApplicationContext(), "Connecting to Server", Toast.LENGTH_LONG).show();


        SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
        anm_id = sharedPreferences.getString(AN_id, null);

        final SQLiteInsert sqLiteInsert = SQLiteInsert.getInstance(this);
        sqLiteInsert.open();

        String Get_child_url = "http://192.168.:8081/api/children/allChildren";

        final JSONObject jsonObject_one = new JSONObject();
        final JSONObject jsonObject_two = new JSONObject();

        final JSONObject jsonObject_three = new JSONObject();

        final JSONObject jsonObject_four = new JSONObject();

        final JSONObject jsonObject_five = new JSONObject();

        final JSONObject jsonObject_six = new JSONObject();
/*
        StringRequest stringRequest3 = new StringRequest(Request.Method.GET, Get_child_url + "?anm_id=" + anm_id,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(String response) {


 */
        try {
                           /* JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String child_id = jsonObject.getString("rch_child_id");
                                Log.d("kuchh bol", "uploading start");
                                String next_vaccine = jsonObject.getString("next_vaccine_id");



                            */
            //  Cursor cursor = sqLiteInsert.getChildImmunization(child_id);
            Cursor cursor = sqLiteInsert.getAllChildImmunization();
            immunization_records = cursor.getCount();

            while (cursor.moveToNext()) {
                String child_id = cursor.getString(0);
                String anm_id = cursor.getString(2);

                Cursor dueDate = sqLite_db.DueOnDate(child_id);

                if (dueDate != null && dueDate.getCount() != 0) {
                    dueDate.moveToFirst();
                    String Due_Date = dueDate.getString(0);

                    //Toast.makeText(getApplicationContext(), sqlite_child_id, Toast.LENGTH_LONG).show();

                    //  if (child_id.equals(sqlite_child_id)) {


                    //-----------------------1st case----------------------------------------------------

                    //  if ((jsonObject.has("v1") && jsonObject.isNull("v1")) && (jsonObject.has("v2") && jsonObject.isNull("v2")) && (jsonObject.has("v4") && jsonObject.isNull("v4"))) {
                    String sqlite_vaccine_id = cursor.getString(1);

                    String[] G1 = {"v1", "v2", "v4"};


                    String new_vaccine = Character.toLowerCase(sqlite_vaccine_id.charAt(0)) + sqlite_vaccine_id.substring(1);
                    Log.d("sqlite", new_vaccine);
                    if (Arrays.asList(G1).contains(new_vaccine)) {

                        // Toast.makeText(getApplicationContext(), new_vaccine, Toast.LENGTH_LONG).show();

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);

                            jsonObject_one.put("barcode_no", sqlite_barcode);
                        }

                        jsonObject_one.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_one.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_one.put("anm_id_at_vaccination", anm_id);
                        jsonObject_one.put("facility_id", sqlite_facility_id);



                                         /*   String result = insertIntoChild(child_name, child_dob, birth_weight, anm_id, anm_mobile, anm_name, child_sex, deilivery_facility,
                                                    enroll_date, father_mobile, father_name, mother_mobile, mother_name, mother_id, resident_block,
                                                    resident_district, resident_facility, resident_phc, resident_sc, resident_state, resident_village,
                                                    sqlite_child_id, new_vaccine, sqlite_anm_id, sqlite_weight, sqlite_date_at, sqlite_facility_id, next_vaccine_session, jsonObject_one, null, null,
                                                    null, null, null, null);


*/

                        String result = insertV1(child_id, jsonObject_one, Due_Date);
                        //  Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();


                    }

                    //   }

                    //---------------------------------------Vaccine 5 -----------------------------------------------------------------

                    //  if ((jsonObject.has("v5") && jsonObject.isNull("v5")) && (jsonObject.has("v6") && jsonObject.isNull("v6")) && (jsonObject.has("v7") && jsonObject.isNull("v7")) && (jsonObject.has("v8") && jsonObject.isNull("v8"))) {
                    //    String sqlite_vaccine_id = cursor.getString(1);

                    String[] G2 = {"v5"};
                    if (Arrays.asList(G2).contains(new_vaccine)) {


                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_two.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_two.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_two.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_two.put("anm_id_at_vaccination", anm_id);
                        jsonObject_two.put("facility_id", sqlite_facility_id);


                                      /*      String result = insertIntoChild(child_name, child_dob, birth_weight, anm_id, anm_mobile, anm_name, child_sex, deilivery_facility,
                                                    enroll_date, father_mobile, father_name, mother_mobile, mother_name, mother_id, resident_block,
                                                    resident_district, resident_facility, resident_phc, resident_sc, resident_state, resident_village,
                                                    sqlite_child_id, new_vaccine, sqlite_anm_id, sqlite_weight, sqlite_date_at, sqlite_facility_id, next_vaccine_session, jsonObject_one,
                                                    jsonObject_two, null, null, null, null, null);

                                                    */

                        String result = insertVaccines(child_id, jsonObject_two, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //   }


//-------------------------------------Vaccine 6--------------------------------------------------------------------------

                    //    if ((jsonObject.has("v9") && jsonObject.isNull("v9")) && (jsonObject.has("v10") && jsonObject.isNull("v10")) && (jsonObject.has("v11") && jsonObject.isNull("v11")) && (jsonObject.has("v12") && jsonObject.isNull("v12"))) {
                    //  String sqlite_vaccine_id = cursor.getString(1);

                    String[] G3 = {"v6"};

                    Log.d("sqlite", new_vaccine);
                    if (Arrays.asList(G3).contains(new_vaccine)) {


                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_three.put("barcode_no", sqlite_barcode);

                        }

                        jsonObject_three.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_three.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_three.put("anm_id_at_vaccination", anm_id);
                        jsonObject_three.put("facility_id", sqlite_facility_id);


                                     /*      String result = insertIntoChild(child_name, child_dob, birth_weight, anm_id, anm_mobile, anm_name, child_sex, deilivery_facility,
                                                    enroll_date, father_mobile, father_name, mother_mobile, mother_name, mother_id, resident_block,
                                                    resident_district, resident_facility, resident_phc, resident_sc, resident_state, resident_village,
                                                    sqlite_child_id, new_vaccine, sqlite_anm_id, sqlite_weight, sqlite_date_at, sqlite_facility_id, next_vaccine_session, jsonObject_one, jsonObject_two,
                                                    jsonObject_three, null, null, null, null);
*/

                        String result = insertVaccines(child_id, jsonObject_three, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //   }

//-------------------------------------------Vaccine 7--------------------------------------------------------------------------

                    //  if ((jsonObject.has("v13") && jsonObject.isNull("v13")) && (jsonObject.has("v17") && jsonObject.isNull("v17"))) {
                    //   String sqlite_vaccine_id = cursor.getString(1);

                    String[] G4 = {"v7"};

                    Log.d("sqlite", new_vaccine);
                    if (Arrays.asList(G4).contains(new_vaccine)) {


                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_four.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_four.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_four.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_four.put("anm_id_at_vaccination", anm_id);
                        jsonObject_four.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_four, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }

                    //  }

                    //-------------------------------------------Vaccine 8--------------------------------------------------------------------------

                    //   if ((jsonObject.has("v14") && jsonObject.isNull("v14")) && (jsonObject.has("v15") && jsonObject.isNull("v15")) && (jsonObject.has("v16") && jsonObject.isNull("v16"))) {
                    //     String sqlite_vaccine_id = cursor.getString(1);

                    String[] G5 = {"v8"};

                    if (Arrays.asList(G5).contains(new_vaccine)) {


                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_five.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_five.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_five.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_five.put("anm_id_at_vaccination", anm_id);
                        jsonObject_five.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_five, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    // }

//------------------------------------------Vaccine 9-------------------------------------------------------------

                    String[] G6 = {"v9"};

                    if (Arrays.asList(G6).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }

                    //------------------------------Vaccine 10 -----------------------------------------

                    String[] G7 = {"v10"};

                    if (Arrays.asList(G7).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }

//--------------------------------------------Vaccine 11  --------------------------------------------------------------------------
                    String[] G8 = {"v11"};

                    if (Arrays.asList(G8).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //-----------------------------------Vaccine 12 ---------------------------------------------

                    String[] G9 = {"v12"};

                    if (Arrays.asList(G9).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //-----------------------------------------------Vaccine 13 -------------------------------------

                    String[] G10 = {"v13"};

                    if (Arrays.asList(G10).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //----------------------------------------------Vaccine 17-----------------------------------

                    String[] G11 = {"v17"};

                    if (Arrays.asList(G11).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //----------------------------------------------Vaccine 14 -----------------------------------

                    String[] G12 = {"v14"};

                    if (Arrays.asList(G12).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //------------------------------------------------Vaccine 15 --------------------------------------

                    String[] G13 = {"v15"};

                    if (Arrays.asList(G13).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }

                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //-------------------------------------------Vaccine 16 -----------------------------------------

                    String[] G14 = {"v16"};

                    if (Arrays.asList(G14).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //--------------------------------------------Vaccine 18 ---------------------------------------

                    String[] G15 = {"v18"};

                    if (Arrays.asList(G15).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }

                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //--------------------------------------------Vaccine 19 -------------------------------------

                    String[] G16 = {"v19"};

                    if (Arrays.asList(G16).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //---------------------------------------Vaccine 20 ---------------------------------------


                    String[] G17 = {"v20"};

                    if (Arrays.asList(G17).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //--------------------------------------Vaccine 3 ------------------------------------------

                    String[] G18 = {"v3"};
                    if (Arrays.asList(G18).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    //-------------------------------------Vaccine 21 ---------------------------------------------

                    String[] G19 = {"v21"};
                    if (Arrays.asList(G19).contains(new_vaccine)) {

                        String sqlite_anm_id = cursor.getString(2);
                        Log.d("sqlite", sqlite_anm_id);
                        String sqlite_date_at = cursor.getString(3);
                        Log.d("sqlite", sqlite_date_at);

                        String sqlite_weight = cursor.getString(4);
                        Log.d("sqlite", sqlite_weight);

                        String sqlite_facility_id = cursor.getString(5);
                        Log.d("sqlite", sqlite_facility_id);

                        String sqlite_barcode = cursor.getString(6);
                        if (sqlite_barcode != null && !sqlite_barcode.isEmpty()) {
                            Log.d("sqlite", sqlite_barcode);
                            jsonObject_six.put("barcode_no", sqlite_barcode);

                        }


                        jsonObject_six.put("date_at", DateFormation(sqlite_date_at));
                        jsonObject_six.put("weight_at_vaccination", sqlite_weight);
                        jsonObject_six.put("anm_id_at_vaccination", anm_id);
                        jsonObject_six.put("facility_id", sqlite_facility_id);


                        String result = insertVaccines(child_id, jsonObject_six, new_vaccine, Due_Date);
                        Log.d("upload result", result);

                    }
                    // }
                    //else if(cursor.getCount() == 0){
                    // SyncData();
                    //}
                }
                if(responseCode != 200){
                    break;
                }
            }


            // }

            sqLiteInsert.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
                   /* }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();


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
        MySingleton.getInstance(Dashboard.this).addToRequestQueue(stringRequest3);



                    */
        return responseCode == 200;
    }

    //---------------------------- Download Data------------------------------------


    public String insertV1(String child_id, JSONObject object1, String next_vaccine_date) {


        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("rch_child_id", child_id);

            jsonObject.put("v1", object1);
            jsonObject.put("v2", object1);
            jsonObject.put("v4", object1);

            DateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date d1 = simpledate.parse(next_vaccine_date);
            assert d1 != null;
            LocalDate next_date = LocalDate.fromMillisSinceEpoch(d1.getTime());

            jsonObject.put("next_vaccine_session1", next_date);

            String URL = "http://192.168.50.144:8081/api/children/saveChild/v1";


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Response", response.toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorResponse = "Unable to reach Server";
                    Toast.makeText(getApplicationContext(),errorResponse,Toast.LENGTH_LONG).show();
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
                    return super.parseNetworkResponse(response);
                }

            };

            MySingleton.getInstance(Dashboard.this).addToRequestQueue(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return "Records are Being uploaded";
    }

    public String insertVaccines(String child_id, JSONObject object6, String vaccine_id, String next_vaccine_date) {


        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("rch_child_id", child_id);

            jsonObject.put(vaccine_id, object6);


            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            Date d1 = formatter.parse(next_vaccine_date);
            assert d1 != null;
            LocalDate next_date = LocalDate.fromMillisSinceEpoch(d1.getTime());

            jsonObject.put("next_vaccine_session1", next_date);


            String URL = "http://192.168.50.144:8081/api/children/saveChild/";


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL + vaccine_id,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    errorResponse = "Unable to reach Server";
                    Toast.makeText(getApplicationContext(),errorResponse,Toast.LENGTH_LONG).show();
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
                    return super.parseNetworkResponse(response);
                }


            };

            MySingleton.getInstance(Dashboard.this).addToRequestQueue(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return "Records are Being Uploaded";
    }


    /*
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public String insertIntoChild(String chid_name, String child_dob, String birth_weight, String anm_id, String anm_mobile, String anm_name,
                                      String child_sex,
                                      String delivery_facility, String enroll_date, String father_mobile, String father_name, String mother_mobile, String mother_name, String mother_id, String resident_block,
                                      String resident_district, String resident_facility, String resident_phc, String resident_sc, String resident_state, String resident_village,
                                      String child_id, String vaccine_id, String sqlite_anm_id, String weight_at, String date_at, String facility_at, String next_vaccine_seesion, JSONObject object1,
                                      JSONObject object2, JSONObject object3, JSONObject object4, JSONObject object5, JSONObject object6
                , JSONObject object7) {


            try {


             String child_name = jsonObject.getString("child_name");
                                            String child_dob = jsonObject.getString("child_dob");
                                            String birth_weight = jsonObject.getString("birth_weight");
                                            String anm_id = jsonObject.getString("anm_id");
                                            String anm_mobile = jsonObject.getString("anm_mobile");
                                            String anm_name = jsonObject.getString("anm_name");
                                            //     String history_facility = jsonObject.getString("history_anm_facility");

                                            String child_sex = jsonObject.getString("child_sex");
                                            String deilivery_facility = jsonObject.getString("delivery_facility_id");
                                            String enroll_date = jsonObject.getString("enrollment_date");
                                            String father_mobile = jsonObject.getString("father_mobile");
                                            String father_name = jsonObject.getString("father_name");
                                            String mother_mobile = jsonObject.getString("mother_mobile");
                                            String mother_name = jsonObject.getString("mother_name");
                                            String mother_id = jsonObject.getString("rch_mother_id");
                                            String next_vaccine_session = jsonObject.getString("next_vaccine_session1");

                                            String resident_block = jsonObject.getString("resident_block");
                                            String resident_district = jsonObject.getString("resident_district");
                                            String resident_facility = jsonObject.getString("resident_facility_id");
                                            String resident_phc = jsonObject.getString("resident_phc");
                                            String resident_sc = jsonObject.getString("resident_sc");
                                            String resident_state = jsonObject.getString("resident_state");
                                            String resident_village = jsonObject.getString("resident_village");



                JSONObject jsonObject = new JSONObject();
                jsonObject.put("rch_child_id", child_id);
                jsonObject.put("anm_id", anm_id);
                jsonObject.put("anm_mobile", anm_mobile);
                jsonObject.put("anm_name", anm_name);
                jsonObject.put("birth_weight", birth_weight);
                jsonObject.put("child_dob", child_dob);
                jsonObject.put("child_name", chid_name);
                jsonObject.put("child_sex", child_sex);
                jsonObject.put("delivery_facility_id", delivery_facility);
                jsonObject.put("enrollment_date", enroll_date);
                jsonObject.put("father_mobile", father_mobile);
                jsonObject.put("father_name", father_name);
                //  jsonObject.put("history_anm_facility",history_facility);
                jsonObject.put("mother_mobile", mother_mobile);
                jsonObject.put("mother_name", mother_name);
                //jsonObject.put("next_vaccine",next_vaccine);
                //jsonObject.put("next_vaccine_session1",next_vaccine_seesion);
                jsonObject.put("rch_mother_id", mother_id);
                jsonObject.put("resident_block", resident_block);
                jsonObject.put("resident_district", resident_district);
                jsonObject.put("resident_facility_id", resident_facility);
                jsonObject.put("resident_phc", resident_phc);
                jsonObject.put("resident_sc", resident_sc);
                jsonObject.put("resident_state", resident_state);
                jsonObject.put("resident_village", resident_village);


                jsonObject.put("v1", object1);
                jsonObject.put("v2", object1);
                jsonObject.put("v4", object1);

                jsonObject.put("v5", object2);
                jsonObject.put("v6", object2);
                jsonObject.put("v7", object2);
                jsonObject.put("v8", object2);
                jsonObject.put("v9", object3);
                jsonObject.put("v10", object3);
                jsonObject.put("v11", object3);
                jsonObject.put("v12", object3);
                jsonObject.put("v13", object4);
                jsonObject.put("v17", object4);
                jsonObject.put("v14", object5);
                jsonObject.put("v15", object5);
                jsonObject.put("v16", object5);
                jsonObject.put("v18", object6);
                jsonObject.put("v19", object6);
                jsonObject.put("v20", object6);
                jsonObject.put("v3", object6);
                jsonObject.put("v21", object7);


                //  JSONArray jsonArray = new JSONArray(jsonObject);


                //Map<String, String> postParam = new HashMap<String, String>();
                //  postParam.put("rch_child_id", child_id);
                //  postParam.put(vaccine_id, jsonObject_one.toString());


                String URL = "http://192.168.42.176:8080/api/children/saveChild";


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,
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


                };

                MySingleton.getInstance(Dashboard.this).addToRequestQueue(jsonObjectRequest);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return "Success";

        }

     */
    public String DateFormation(String input) {

        DateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
        try {
            date = simpledate.parse(input);
            DateFormat simpledatenew = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            formattedDate = simpledatenew.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return formattedDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void SynchronizeData(View view) {
        TransitionManager.beginDelayedTransition(progressParent);

        Cursor cursor = sqLiteInsert.getAllChildImmunization();
        immunization_records = cursor.getCount();

        Cursor cursorVaccine = sqLiteInsert.getUpdatedVaccines();
        vCount = cursorVaccine.getCount();

        if (immunization_records != 0 || vCount != 0) {
            if (uploadData() && uploadVaccines()) {
                if (SyncData()) {
                    getVaccines();
                }
                Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
            }
        } else {
            SyncData();
            getVaccines();
        }


    }

    public void showAlert(String message) {
        builder.setTitle(getString(R.string.status));
        builder.setMessage(message
        );
        builder.setPositiveButton(getString(R.string.contactAdmin), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
                String id = sharedPreferences.getString(AN_id, null);
                Intent admin_intent = new Intent("com.example.unisicma.REQUEST_ACTION");
                admin_intent.setPackage("com.example.unisicma");
                admin_intent.putExtra("com.example.unisicma.REQUEST_TEXT", "Request Received from ANM ID " + id + " ");
                // sendBroadcast(admin_intent);
                if (sqLiteInsert.insertRequest(id, startDate)) {
                    Toast.makeText(getApplicationContext(), "Request sent to Admin", Toast.LENGTH_LONG).show();
                }

            }
        });
        builder.setNegativeButton(getString(R.string.back), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    public void getVaccines() {

        String Get_vaccine_url = "http://192.168.50.144:8081/api/getVaccines";

        StringRequest request = new StringRequest(Request.Method.GET, Get_vaccine_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String barcode = jsonObject.getString("barcode_no");
                                String vaccine_id = jsonObject.getString("vaccine_id");
                                String vaccine_name = jsonObject.getString("vaccine_name");
                                String batch_no = jsonObject.getString("batch_no");
                                String expiry_date = jsonObject.getString("expiry_date");
                                String manufacturing_date = jsonObject.getString("manufacturing_date");
                                String vaccination_date = jsonObject.getString("vaccination_date");
                                String manufacturer = jsonObject.getString("manufacturer");

                                sqLiteInsert.insertVaccine_barcode(vaccine_id, vaccine_name, barcode, batch_no, expiry_date, manufacturing_date, vaccination_date, manufacturer);
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
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        MySingleton.getInstance(Dashboard.this).addToRequestQueue(request);


    }

    public boolean uploadVaccines() {

        SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
        anm_id = sharedPreferences.getString(AN_id, null);

        final SQLiteInsert sqLiteInsert = SQLiteInsert.getInstance(this);
        sqLiteInsert.open();


        Cursor cursor = sqLiteInsert.getUpdatedVaccines();
        vCount = cursor.getCount();

        while (cursor.moveToNext()) {
            String barcode_no = cursor.getString(0);
            String vaccination_date = cursor.getString(1);
            String batch_no = cursor.getString(2);
            String manufacturing_date = cursor.getString(3);
            String expiry_date = cursor.getString(4);
            String vaccine_id = cursor.getString(5);
            String vaccine_name = cursor.getString(6);
            String manufacturer = cursor.getString(7);
            updateVaccines(barcode_no, vaccination_date, batch_no, expiry_date, manufacturing_date, manufacturer, vaccine_id, vaccine_name);
            Log.d("barcode:", barcode_no);
            if (vaccination_date != null && !vaccination_date.isEmpty()) {
                Log.d("v date: ", vaccination_date);
            }

        }


        // }

        sqLiteInsert.close();

        return responseCode == 200;
    }

    public String updateVaccines(String barcode_no, String vaccination_date, String b_no, String expiry_date, String manufacturing_date, String manufacturer, String vaccine_id, String vaccine_name) {


        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("barcode_no", barcode_no);

            jsonObject.put("vaccination_date", vaccination_date);
            jsonObject.put("batch_no", b_no);
            jsonObject.put("expiry_date", expiry_date);
            jsonObject.put("manufacturing_date", manufacturing_date);
            jsonObject.put("manufacturer", manufacturer);
            jsonObject.put("vaccine_id", vaccine_id);
            jsonObject.put("vaccine_name", vaccine_name);


            String URL = "http://192.168.50.144:8081/api/updateVaccine";


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("kuchh bol", "inserting Data");


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
                    return super.parseNetworkResponse(response);
                }
            };

            MySingleton.getInstance(Dashboard.this).addToRequestQueue(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return "Records are Being Uploaded";
    }

    public void SyncVaccines(View view) {
        Cursor cursor = sqLiteInsert.getUpdatedVaccines();
        vCount = cursor.getCount();
        // Toast.makeText(getApplicationContext(),String.valueOf(vCount),Toast.LENGTH_LONG).show();
        if (vCount != 0) {
            //  boolean upload_result = uploadVaccines();
            if (uploadVaccines()) {
                getVaccines();
            }
        } else {
            getVaccines();

        }


    }


    public void callAPI(View view) throws IOException {
        changeLanguage();
    }

    public void changeLanguage() {
        final List<String> names = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {

               translateChildName();

            }
        }).start();
    }

    private void translateChildName() {
        List<String> ch_names_list_hi = new ArrayList<>();
        List<String> ch_id_list = new ArrayList<>();
        List<String> ch_names_list_tn = new ArrayList<>();
        List<String> languageList = new ArrayList<>();
        languageList.add("hi");
        languageList.add("ta");



        try {
            sqLiteInsert.open();
            final Cursor childNames = sqLiteInsert.getChildNames();
            if (childNames.getCount() != 0) {

                while (childNames.moveToNext()) {
                    ch_id_list.add(childNames.getString(0));


                    //---------------------------------------- Child Name Translation to Hindi & Tamil using google script API ----------------------
                    for (int j = 0; j < languageList.size(); j++) {

                        String urlStr = "https://script.google.com/macros/s/AKfycbz-ubFtOU0rjqlDHOhxp_iQsywrAuESMhH9_aiPMblQZ1RhRF0CTqh6oDG_gHOQ1sZPJQ/exec" +
                                "?q=" + URLEncoder.encode(childNames.getString(1), "UTF-8") +
                                "&target=" + languageList.get(j) +
                                "&source=" + "en";
                        URL url = null;

                        url = new URL(urlStr);
                        StringBuilder response = new StringBuilder();
                        HttpURLConnection con = null;
                        try {
                            con = (HttpURLConnection) url.openConnection();

                            assert con != null;
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(languageList.get(j).equals("hi")) {
                            ch_names_list_hi.add(response.toString());
                        }else if(languageList.get(j).equals("ta")){
                            ch_names_list_tn.add(response.toString());
                        }
                    }

                }

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int length = ch_names_list_hi.size();
        for (int i = 0; i < length; i++) {

            sqLiteInsert.updateLanguage(ch_names_list_hi.get(i),ch_names_list_tn.get(i),ch_id_list.get(i));
            Log.d("Names:", ch_names_list_hi.get(i)+ch_id_list.get(i)+ch_names_list_tn.get(i));


        }
        sqLiteInsert.close();


    }

    private void translateMotherMaster() {
        List<String> mother_names_list_hi = new ArrayList<>();
        List<String>  mother_id_list = new ArrayList<>();
        List<String> mother_names_list_tn = new ArrayList<>();
        List<String> father_names_list_hi = new ArrayList<>();
        List<String> father_names_list_tn = new ArrayList<>();
        List<String> languageList = new ArrayList<>();
        languageList.add("hi");
        languageList.add("ta");



        try {
            sqLiteInsert.open();
            final Cursor Names = sqLiteInsert.getChildNames();
            if (Names != null && Names.getCount() != 0) {

                while (Names.moveToNext()) {
                    mother_id_list.add(Names.getString(0));


                    //---------------------------------------- Mother Name Translation to Hindi & Tamil using google script API ----------------------
                    for (int j = 0; j < languageList.size(); j++) {

                        String urlStr = "https://script.google.com/macros/s/AKfycbz-ubFtOU0rjqlDHOhxp_iQsywrAuESMhH9_aiPMblQZ1RhRF0CTqh6oDG_gHOQ1sZPJQ/exec" +
                                "?q=" + URLEncoder.encode(Names.getString(1), "UTF-8") +
                                "&target=" + languageList.get(j) +
                                "&source=" + "en";
                        URL url = null;

                        url = new URL(urlStr);
                        StringBuilder response = new StringBuilder();
                        HttpURLConnection con = null;
                        try {
                            con = (HttpURLConnection) url.openConnection();

                            assert con != null;
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(languageList.get(j).equals("hi")) {
                            mother_names_list_hi.add(response.toString());
                        }else if(languageList.get(j).equals("ta")){
                            mother_names_list_tn.add(response.toString());
                        }
                    }

                    //---------------------------------------- Father Name Translation to Hindi & Tamil using google script API ----------------------
                    for (int j = 0; j < languageList.size(); j++) {

                        String urlStr = "https://script.google.com/macros/s/AKfycbz-ubFtOU0rjqlDHOhxp_iQsywrAuESMhH9_aiPMblQZ1RhRF0CTqh6oDG_gHOQ1sZPJQ/exec" +
                                "?q=" + URLEncoder.encode(Names.getString(2), "UTF-8") +
                                "&target=" + languageList.get(j) +
                                "&source=" + "en";
                        URL url = null;

                        url = new URL(urlStr);
                        StringBuilder response = new StringBuilder();
                        HttpURLConnection con = null;
                        try {
                            con = (HttpURLConnection) url.openConnection();

                            assert con != null;
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(languageList.get(j).equals("hi")) {
                            father_names_list_hi.add(response.toString());
                        }else if(languageList.get(j).equals("ta")){
                            father_names_list_tn.add(response.toString());
                        }
                    }



                }

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int length = mother_names_list_hi.size();
        for (int i = 0; i < length; i++) {

            sqLiteInsert.updateMotherTableLanguage(mother_names_list_hi.get(i),mother_names_list_tn.get(i),mother_id_list.get(i),father_names_list_hi.get(i),father_names_list_tn.get(i));
            Log.d("Names:", mother_names_list_hi.get(i)+mother_id_list.get(i)+mother_names_list_tn.get(i));


        }
        sqLiteInsert.close();


    }
}
