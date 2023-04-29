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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


@RequiresApi(api = Build.VERSION_CODES.O)
public class Workplan extends AppCompatActivity {

    ContactAdapter contactAdapter;
    ListView listView;
    String json_string, dueDate;
    String id, name, dob, mother_id, Last_Visit_Date;
    AlertDialog.Builder builder;
    public static final String AN_id = "id_k";
    public static final String AN_name = "name_k";
    public static final String AN_mobile = "mobile_k";

    TextView workplan, Block, SC, Village, Facility, dueCount;
    String total_Count;

    String facility_url = "http://192.168.20.56/facility_details.php";
    String due_date_url = "http://192.168.20.56/due_on_date.php";
    String anm_id;
    String formattedDate;


    SQLite_db sqLite_db;
    SQLiteInsert sqLiteInsert;
    NetworkInfo networkInfo;

    ExpandableListView expandableListView;
    List<String> facility_list;
    Map<String, List<String>> facility_details;
    ExListAdapter exListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workplan);
        contactAdapter = new ContactAdapter(this, R.layout.display_layout);
        listView = findViewById(R.id.listview);
        listView.setAdapter(contactAdapter);
        workplan = findViewById(R.id.workplan);
        //Block = findViewById(R.id.block);
        //SC = findViewById(R.id.SC);
        // Village = findViewById(R.id.village);
        //Facility = findViewById(R.id.facility);
        dueCount = findViewById(R.id.due_count);

        expandableListView = findViewById(R.id.expanded_list);
        facility_list = new ArrayList<>();
        facility_details = new HashMap<>();

        loadlocale();

        ConnectivityManager connectivityManager;

        builder = new AlertDialog.Builder(Workplan.this);

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();


        sqLite_db = SQLite_db.getInstance(this);
        sqLiteInsert = SQLiteInsert.getInstance(this);

        FloatingActionButton floating_btn = findViewById(R.id.floating_btn);


//------------------Getting Json data from server -------------------------------

        getListData();


        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
        anm_id = sharedPreferences.getString(AN_id, null);
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


        //--- start and end date of a week section--------------------------------------------

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String startDate = "", endDate = "";
        startDate = dateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE, 6);
        endDate = dateFormat.format(calendar.getTime());


        workplan.append("\n(" + startDate + " " + getString(R.string.to) + " " + endDate + ")");


        floating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tabbed tabbed = new Tabbed();
                tabbed.showDialog(Workplan.this);
            }
        });

        //-----------facility details -----------------------------------

      /*  if (networkInfo != null && networkInfo.isConnected()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, facility_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String block = jsonObject.getString("block");
                                String sc = jsonObject.getString("sc");
                                String phc = jsonObject.getString("phc");
                                String code = jsonObject.getString("code");
                                Block.append("\n" + block);
                                SC.append("\n" + sc);
                                String facility = jsonObject.getString("facility");
                                Facility.append("\n" + facility);
                                if (code.equals("No_village")) {
                                    Village.append("\n Not Available");
                                } else if (code.equals("Getting_data")) {
                                    String village = jsonObject.getString("village");
                                    Village.append("\n" + village);


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
                    SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
                    anm_id = sharedPreferences.getString(AN_id, null);
                    params.put("anm_id", anm_id);
                    return params;
                }
            };
            MySingleton.getInstance(Workplan.this).addToRequestQueue(stringRequest);

        } else {
            showFacilityDetails();

        }

       */
        showFacilityDetails();

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
        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Workplan.this);
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


    public void getListData() {

/*
        if (networkInfo != null && networkInfo.isConnected()) {
            final Bundle bundle = getIntent().getExtras();
            json_string = bundle.getString("result");

            try {
                JSONArray jsonArray = new JSONArray(json_string);
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String code = jsonObject.getString("code");
                    if (code.equals("Getting_data")) {


                        mother_id = jsonObject.getString("mother_id");
                        id = jsonObject.getString("child_id");
                        name = jsonObject.getString("child_name");
                        dob = jsonObject.getString("dob");
                        Last_Visit_Date = jsonObject.getString("ldv");
                        String due_count = jsonObject.getString("count");

                        dueCount.setText(due_count + " " + getString(R.string.records));

                        //-------------Date Format Change-----------------------


                        if (!id.equals(" ")) {
                            ///------------------------------Due on Date--------------------------------------------------------------------------


                            StringRequest stringRequest1 = new StringRequest(Request.Method.POST, due_date_url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String due_response) {

                                            JSONArray jsonArray1 = null;
                                            try {
                                                jsonArray1 = new JSONArray(due_response);
                                                JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                                                dueDate = jsonObject1.getString("due_date");


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

                                    params.put("child_id", id);
                                    return params;
                                }
                            };
                            MySingleton.getInstance(Workplan.this).addToRequestQueue(stringRequest1);
                        }


                        final Contacts contacts = new Contacts(name, mother_id, id, DateFormation(dob), DateFormation(Last_Visit_Date));
                        listView.setAdapter(contactAdapter);
                        contactAdapter.add(contacts);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long Id) {
                                try {
                                    JSONArray jsonArray1 = new JSONArray(json_string);
                                    JSONObject jsonObject = jsonArray1.getJSONObject(position);

                                    Intent intent = new Intent(view.getContext(), Workplan_menu.class);
                                    Bundle bundle1 = new Bundle();

                                    bundle1.putString("M_ID", jsonObject.getString("mother_id"));
                                    bundle1.putString("C_ID", jsonObject.getString("child_id"));
                                    bundle1.putString("LVD", jsonObject.getString("ldv"));
                                    bundle1.putString("DOB", jsonObject.getString("dob"));

                                    intent.putExtras(bundle1);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    } else if (code.equals("no_data")) {
                        String message = jsonObject.getString("message");
                        builder.setTitle("Status");
                        displayAlert(getString(R.string.no_record));
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
         else {

 */


        //--------------------------------SQLite section----------------------------------

        //----------------------------------------------Total Count----------------------------------------------
        sqLiteInsert.open();
        Cursor cursor1 = sqLiteInsert.getChildCount();
        while (cursor1.moveToNext()) {
            total_Count = cursor1.getString(0);
        }

        final ArrayList<String> list = new ArrayList<>();
        final ArrayList<String> listLvd = new ArrayList<>();
        final ArrayList<String> listDob = new ArrayList<>();
        final ArrayList<String> listFacility = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
        anm_id = sharedPreferences.getString(AN_id, null);
        sqLite_db.open();


        Cursor cursor = sqLite_db.getworkplan(anm_id);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                mother_id = cursor.getString(0);

                SharedPreferences preferences = getSharedPreferences("save to all activity", Context.MODE_PRIVATE);
                String language = preferences.getString("My_Lang", null);

                assert language != null;
                if (language.contains("hi")) {
                    name = cursor.getString(6);
                    if (name != null && name.isEmpty()) {
                        name = cursor.getString(2);
                    }

                } else if (language.contains("ta")) {
                    name = cursor.getString(7);
                    if (name.isEmpty()) {
                        name = cursor.getString(2);
                    }
                } else {
                    name = cursor.getString(2);

                }
                id = cursor.getString(1);
                list.add(id);

                String facility_id = cursor.getString(3);
                listFacility.add(facility_id);
                dob = cursor.getString(4);
                listDob.add(dob);
                String lvd = cursor.getString(5);
                listLvd.add(lvd);

                //Changing date format to dd-mm-yyyy------------------------------------


                final Contacts contacts1 = new Contacts(name, mother_id, id, DateFormation(dob), DateFormation(lvd));
                listView.setAdapter(contactAdapter);
                contactAdapter.add(contacts1);
                //dueCount.setText(contactAdapter1.getCount());
            }
        } else {

            builder.setTitle(getString(R.string.response));
            displayAlert(getString(R.string.no_record));

        }
        dueCount.setText(contactAdapter.getCount() + " " + getString(R.string.records) + " \n " +
                getString(R.string.total_records) + ": " + total_Count);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {


                Intent intent = new Intent(view.getContext(), Workplan_menu.class);
                Bundle bundle1 = new Bundle();


                bundle1.putString("id", list.get(position));
                bundle1.putString("DOB", listDob.get(position));
                bundle1.putString("LVD", listLvd.get(position));
                bundle1.putString("facility_id", listFacility.get(position));
                intent.putExtras(bundle1);
                startActivity(intent);

            }
        });
        // }
        sqLite_db.close();

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

    public void showFacilityDetails() {
        exListAdapter = new ExListAdapter(this, facility_list, facility_details);
        facility_list.add(getString(R.string.facility_details));

        sqLite_db = SQLite_db.getInstance(this);
        sqLite_db.open();
        Cursor cursor = sqLite_db.getFacilityInfo(anm_id);
        while (cursor.moveToNext()) {

            String block = cursor.getString(0);
            String phc = cursor.getString(1);
            String sc = cursor.getString(2);
            String facility = cursor.getString(4);
            String village = cursor.getString(3);


            // Block.append("\n" + block);
            // SC.append("\n" + sc);
            // Facility.append("\n" + "(" + facility + ")");
            //Village.append("\n" + village);

            List<String> details_list = new ArrayList<>();
            details_list.add(getString(R.string.block) + ":  " + block);
            details_list.add(getString(R.string.sc) + ":  " + sc);
            details_list.add(getString(R.string.facility_id) + ":  " + facility);
            if (!village.isEmpty()) {
                details_list.add(getString(R.string.village) + ":  " + village);
            }
            facility_details.put(facility_list.get(0), details_list);
            expandableListView.setAdapter(exListAdapter);
        }
        sqLite_db.close();
    }


    public void displayAlert(String message) {
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Workplan.this, Dashboard.class));

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
