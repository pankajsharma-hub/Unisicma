package com.example.unisicma;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class Workplan_menu extends AppCompatActivity {

    private Toolbar toolbar;
    Button Vaccine_proceed;
    String M_id, formattedDate;
    AlertDialog.Builder builder;
    private ConnectivityManager connectivityManager;
    SQLite_db sqLite_db;
    public static String C_ID;
    long diff;

    TextView m_id, c_id, m_name, m_mobile, h_name, h_mobile, f_name, vaccine_received, vaccine_remained, due_on_date, received_date,Child_name,Child_dob;
    public static final String AN_id = "id_k";
    public static final String AN_name = "name_k";
    public static final String AN_mobile = "mobile_k";


    String due_details_url = "http://192.168.20.56/Due_details.php";
    String remained_vaccines_url = "http://192.168.20.56/vaccines_remained.php";
    String due_date_url = "http://192.168.20.56/due_on_date.php";
    String new_vaccines;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workplan_menu);

        loadlocale();

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString(AN_id, null);
        String anmName = sharedPreferences.getString(AN_name, null);
        String anmMobile = sharedPreferences.getString(AN_mobile, null);
        getSupportActionBar().setTitle(anmName);
        toolbar.setLogo(R.drawable.ic_face_4dp);
        toolbar.setSubtitle(getString(R.string.anm_mobile)+": " + anmMobile);

        connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        sqLite_db = SQLite_db.getInstance(this);

        builder = new AlertDialog.Builder(Workplan_menu.this);

       // Vaccine_proceed = findViewById(R.id.Vaccine_proceed);


        c_id = findViewById(R.id.id_value);
        m_name = findViewById(R.id.mName_value);
        m_id = findViewById(R.id.mId_value);
        m_mobile = findViewById(R.id.mMobile_value);
        h_name = findViewById(R.id.fName_value);
        h_mobile = findViewById(R.id.fMobile_value);
        // f_name = findViewById(R.id.textView7);
        vaccine_received = findViewById(R.id.received_value);
        vaccine_remained = findViewById(R.id.remained_value);
        due_on_date = findViewById(R.id.date_value);
        received_date = findViewById(R.id.received_date);
        Child_name = findViewById(R.id.name_value);
        Child_dob =findViewById(R.id.dob_value);


        final Bundle bundle = getIntent().getExtras();
        C_ID = bundle.getString("id");
        final String LVD = bundle.getString("LVD");

        final String DOB = bundle.getString("DOB");
        final String FACILITY_ID = bundle.getString("facility_id");


        //----------------------child details for due list-----------------

        /*
        if (networkInfo != null && networkInfo.isConnected()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, due_details_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String code = jsonObject.getString("code");
                                    if (code.equals("Getting_data")) {
                                        M_id = jsonObject.getString("mother_id");
                                        String M_name = jsonObject.getString("mother_name");
                                        String C_id = jsonObject.getString("child_id");
                                        String M_mobile = jsonObject.getString("mother_mobile");
                                        String H_name = jsonObject.getString("husband_name");
                                        String H_mobile = jsonObject.getString("husband_mobile");
                                        String F_name = jsonObject.getString("facility_name");

                                        String dov = jsonObject.getString("dov");
                                        vaccine_received.append(jsonObject.getString("vaccine_id") + ",On" + " (" +
                                                DateFormation(dov) + ")\n");


                                        m_id.setText(M_id);
                                        m_name.setText(M_name);
                                        c_id.setText(C_id);
                                        m_mobile.setText(M_mobile);
                                        h_name.setText(H_name);
                                        h_mobile.setText(H_mobile);
                                        //  f_name.setText(F_name);


                                    } else if (code.equals("no_data")) {
                                        String message = jsonObject.getString("message");
                                        String M_id = jsonObject.getString("mother_id");
                                        String M_name = jsonObject.getString("mother_name");
                                        String C_id = jsonObject.getString("child_id");
                                        String M_mobile = jsonObject.getString("mother_mobile");
                                        String H_name = jsonObject.getString("husband_name");
                                        String H_mobile = jsonObject.getString("husband_mobile");
                                        String F_name = jsonObject.getString("facility_name");

                                        m_id.setText(M_id);
                                        m_name.setText(M_name);
                                        c_id.setText(C_id);
                                        m_mobile.setText(M_mobile);
                                        h_name.setText(H_name);
                                        h_mobile.setText(H_mobile);
                                        //  f_name.setText(F_name);
                                        vaccine_received.setText(message);
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

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("C_ID", C_ID);
                    return params;
                }
            };
            MySingleton.getInstance(Workplan_menu.this).addToRequestQueue(stringRequest);


            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, due_date_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String due_response) {

                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(due_response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String dueDate = jsonObject.getString("due_date");

                                due_on_date.append(DateFormation(dueDate) + "\n");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


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
                    params.put("C_ID", C_ID);


                    return params;
                }
            };
            MySingleton.getInstance(Workplan_menu.this).addToRequestQueue(stringRequest2);


//-----------------------------------Remained vaccines----------------------------------

            StringRequest stringRequest1 = new StringRequest(Request.Method.POST, remained_vaccines_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);


                                    vaccine_remained.append(jsonObject.getString("remained_vaccines") + "\n");
                                    new_vaccines = response;


                                    Vaccine_proceed.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Workplan_menu.this, Authenticate_barcode.class);
                                            Bundle bundle1 = new Bundle();
                                            bundle1.putString("new_vaccines", new_vaccines);
                                            bundle1.putString("M_id", M_id);
                                            intent.putExtras(bundle1);
                                            startActivity(intent);
                                        }
                                    });

                                }

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }


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
                    params.put("C_ID", C_ID);
                    params.put("lvd", LVD);
                    params.put("DOB", DOB);
                    return params;
                }
            };
            MySingleton.getInstance(Workplan_menu.this).addToRequestQueue(stringRequest1);

        } else {
*/

//------------------------****Getting Details info of listed records*****-----------------------------------
        /*
         *
         * *  SQLite Section----------------------------------
         *
         *
         */
        sqLite_db.open();

        Cursor cursor = sqLite_db.DueDetails(C_ID);//----------Due details------------------

        while (cursor.moveToNext()) {
            String mother_id = cursor.getString(0);
            SharedPreferences preferences = getSharedPreferences("save to all activity", MODE_PRIVATE);
            String language = preferences.getString("My_Lang", null);

            String mother_name = null;
            String father_name = null;
            String child_name = null;
            assert language != null;
            if (language.contains("hi")) {
                 mother_name = cursor.getString(9);
                 father_name = cursor.getString(10);
                 child_name = cursor.getString(13);
                 if(mother_name.isEmpty()||father_name.isEmpty()||child_name.isEmpty()){
                     mother_name = cursor.getString(1);
                     father_name = cursor.getString(4);
                     child_name = cursor.getString(8);
                 }

            } else if (language.contains("ta")) {
                 mother_name = cursor.getString(11);
                father_name = cursor.getString(12);
                child_name = cursor.getString(14);
                if(mother_name.isEmpty()||father_name.isEmpty()||child_name.isEmpty()){
                    mother_name = cursor.getString(1);
                    father_name = cursor.getString(4);
                    child_name = cursor.getString(8);
                }
            } else {

                mother_name = cursor.getString(1);
                father_name = cursor.getString(4);
                child_name = cursor.getString(8);
            }
            id = cursor.getString(2);
            String mother_mobile = cursor.getString(3);

            String father_mobile = cursor.getString(5);
            String facility_id = cursor.getString(6);
            String child_dob = cursor.getString(7);



            m_id.setText(mother_id);
            m_name.setText(mother_name);
            c_id.setText(id);
            m_mobile.setText(mother_mobile);
            h_name.setText(father_name);
            h_mobile.setText(father_mobile);
            Child_dob.setText(DateFormation(child_dob));
            Child_name.setText(child_name);


        }


        Cursor cursor1 = sqLite_db.vaccinesGiven(C_ID);//---*** Getting received vaccines details----------------------

        while (cursor1.moveToNext()) {
            String vaccine_name = cursor1.getString(0);

            String vaccine_date = cursor1.getString(1);
            vaccine_received.append(vaccine_name + " \n");
            received_date.append(DateFormation(vaccine_date) + " \n");


        }


        //------------------------Due on Date - -------------------------

        DateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
        try {
            date = simpledate.parse(LVD);
            Date dob = simpledate.parse(DOB);
            diff = (date.getTime() - dob.getTime()) / (1000 * 60 * 60 * 24);
            //Toast.makeText(getApplicationContext(),String.valueOf(diff),Toast.LENGTH_LONG).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Cursor cursor2 = sqLite_db.DueOnDate(C_ID);
        if (cursor2 == null) {

        } else {
            while (cursor2.moveToNext()) {
                String DueDate = cursor2.getString(0);

                due_on_date.setText(DateFormation(DueDate) + " \n");
            }
        }

        //--------------------------------Getting remained vaccines-----------------


        Cursor cursor3 = sqLite_db.vaccinesRemained(C_ID, LVD);
        if (cursor3.getCount() == 0) {
            vaccine_remained.append("No Due Vaccine");
        } else {
            while (cursor3.moveToNext()) {
                String vaccine_id = cursor3.getString(0);
                new_vaccines = cursor3.getString(1);

                vaccine_remained.append(new_vaccines + " \n");
            }
        }

        //-----------------------------Proceed for Vaccination------------------------------

      /*  Vaccine_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Workplan_menu.this, Authenticate_barcode.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("child_id", C_ID);
                bundle1.putString("lvd", LVD);
                bundle1.putString("facility_id", FACILITY_ID);

                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

       */
        sqLite_db.close();
        // }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }


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
        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Workplan_menu.this);
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


}
