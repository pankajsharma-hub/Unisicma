package com.example.unisicma;

import android.app.Activity;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DueList extends AppCompatActivity {
    private Toolbar toolbar;
    ContactAdapter contactAdapter;
    ListView listView;
    String json_string, anm_id;
    String id, name, dob, mother_id, dueDate, Last_Visit_Date;
    public static final String AN_id = "id_k";
    public static final String AN_name = "name_k";
    public static final String AN_mobile = "mobile_k";
    TextView dueList, Block, SC, Village, Facility, DueDate, dueCount;
    String facility_url = "http://192.168.42.176/facility_details.php";
    String due_date_url = "http://192.168.42.176/due_on_date.php";

    Workplan workplan = new Workplan();
    SQLite_db sqLite_db;
    SQLiteInsert sqLiteInsert;
    String total_Count;

    private ConnectivityManager connectivityManager;

    final ArrayList<String> list = new ArrayList<>();
    final ArrayList<String> listLvd = new ArrayList<>();
    final ArrayList<String> listDob = new ArrayList<>();

    AlertDialog.Builder builder;
    ExpandableListView expandableListView;
    List<String> facility_list;
    Map<String, List<String>> facility_details;
    ExListAdapter exListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due_list);
        contactAdapter = new ContactAdapter(this, R.layout.display_layout);
        listView = findViewById(R.id.listview);
        listView.setAdapter(contactAdapter);
        dueList = findViewById(R.id.dueList);
        // Block = findViewById(R.id.block);
        //SC = findViewById(R.id.SC);
        // Village = findViewById(R.id.village);
        //  Facility = findViewById(R.id.facility);
        DueDate = findViewById(R.id.duedate);
        dueCount = findViewById(R.id.due_count);
        expandableListView = findViewById(R.id.expanded_list);
        facility_list = new ArrayList<>();
        facility_details = new HashMap<>();

        loadlocale();

        FloatingActionButton floating_btn = findViewById(R.id.floating_btn);


        builder = new AlertDialog.Builder(this);
        sqLiteInsert = SQLiteInsert.getInstance(this);

        //-----------------Calender for start and end date of a week-------------


        Calendar calendar = Calendar.getInstance();

        //calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String startDate = "", endDate = "";
        startDate = dateFormat.format(calendar.getTime());
        // calendar.add(Calendar.DATE, 6);
        //endDate = dateFormat.format(calendar.getTime());

        dueList.append("\n( " + getString(R.string.dueUpto) + " " + startDate + " )");

        //-----------------Setting Toolbar----------------------------------


        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
        anm_id = sharedPreferences.getString(AN_id, null);
        String anmName = sharedPreferences.getString(AN_name, null);
        String anmMobile = sharedPreferences.getString(AN_mobile, null);
        getSupportActionBar().setTitle(anmName);
        toolbar.setLogo(R.drawable.ic_face_4dp);
        toolbar.setSubtitle(getString(R.string.anm_mobile)+": " + anmMobile);

        floating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tabbed tabbed = new Tabbed();
                tabbed.showDialog(DueList.this);
            }
        });


        //---------------------Getting Json data from Intermediate Fragment---------------------------

        connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


        Bundle bundle = getIntent().getExtras();
        String code = bundle.getString("code");
        if (code.contains("VaccineWise")) {
            showVaccineWiseList();
        } else {
            showDateWise();
        }
        showFacilityDetails();

/*
        if (networkInfo != null && networkInfo.isConnected()) {//---------------------Checking network status---------------------------

            final Bundle bundle = getIntent().getExtras();
            json_string = bundle.getString("result");
            dueDate = bundle.getString("dueDate");


            try {
                JSONArray jsonArray = new JSONArray(json_string);


                for (int i = 0; i < jsonArray.length(); i++) {

                    final JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String code = jsonObject.getString("code");
                    if (code.equals("Getting_data")) {

                        mother_id = jsonObject.getString("mother_Id");
                        id = jsonObject.getString("child_id");
                        name = jsonObject.getString("child_name");
                        dob = jsonObject.getString("child_dob");
                        Last_Visit_Date = jsonObject.getString("ldv");
                        String due_count = jsonObject.getString("count");

                        //--------------------Date format Change---------------------------

                        dueCount.setText(due_count + " " + getString(R.string.records));


                        Contacts contacts = new Contacts(name, mother_id, id, workplan.DateFormation(dob), workplan.DateFormation(Last_Visit_Date));
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

                                    bundle1.putString("M_ID", jsonObject.getString("mother_Id"));
                                    bundle1.putString("C_ID", jsonObject.getString("child_id"));
                                    bundle1.putString("LVD", jsonObject.getString("ldv"));
                                    bundle1.putString("DOB", jsonObject.getString("child_dob"));

                                    intent.putExtras(bundle1);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });


                    } else if (code.equals("no_data")) {
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //-----------facility details -----------------------------------

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
                    String anm_id = sharedPreferences.getString(AN_id, null);
                    params.put("anm_id", anm_id);
                    return params;
                }
            };
            MySingleton.getInstance(DueList.this).addToRequestQueue(stringRequest);
        } else {
            */


        // }

    }

    public void showVaccineWiseList() {

        contactAdapter = new ContactAdapter(this, R.layout.display_layout);

        sqLiteInsert.open();
        Cursor cursor1 = sqLiteInsert.getChildCount();
        while (cursor1.moveToNext()) {
            total_Count = cursor1.getString(0);
        }

        Bundle bundle = getIntent().getExtras();
        String vaccine_id = bundle.getString("vaccine_input");

        sqLite_db = SQLite_db.getInstance(this);
        sqLite_db.open();
        Cursor cursor = sqLite_db.VaccineWiseDue(vaccine_id, anm_id);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                mother_id = cursor.getString(2);

                SharedPreferences preferences = getSharedPreferences("save to all activity", Context.MODE_PRIVATE);
                String language = preferences.getString("My_Lang", null);

                assert language != null;
                if (language.contains("hi")) {
                    name = cursor.getString(6);
                    if (name.isEmpty()) {
                        name = cursor.getString(1);
                    }

                } else if (language.contains("ta")) {
                    name = cursor.getString(7);
                    if (name.isEmpty()) {
                        name = cursor.getString(1);
                    }
                } else {
                    name = cursor.getString(1);

                }

                id = cursor.getString(0);
                list.add(id);

                String facility_id = cursor.getString(4);
                dob = cursor.getString(3);
                listDob.add(dob);
                String lvd = cursor.getString(5);
                listLvd.add(lvd);


                //Changing date format to dd-mm-yyyy------------------------------------

                final Contacts contacts1 = new Contacts(name, mother_id, id, workplan.DateFormation(dob), workplan.DateFormation(lvd));
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

        onClickList();
        sqLite_db.close();
    }

    public void showDateWise() {

        contactAdapter = new ContactAdapter(this, R.layout.display_layout);

        sqLiteInsert.open();
        Cursor cursor1 = sqLiteInsert.getChildCount();
        while (cursor1.moveToNext()) {
            total_Count = cursor1.getString(0);

        }

        sqLite_db = SQLite_db.getInstance(this);
        sqLite_db.open();
        Cursor cursor = sqLite_db.dateWiseDue(anm_id);


        if (cursor == null) {
            builder.setTitle(getString(R.string.response));

            displayAlert(getString(R.string.no_record));
        } else {

            while (cursor.moveToNext()) {
                mother_id = cursor.getString(0);

                SharedPreferences preferences = getSharedPreferences("save to all activity", Context.MODE_PRIVATE);
                String language = preferences.getString("My_Lang", null);

                assert language != null;
                if (language.contains("hi")) {
                    name = cursor.getString(6);
                    if (name.isEmpty()) {
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
                dob = cursor.getString(4);
                listDob.add(dob);
                String lvd = cursor.getString(5);
                listLvd.add(lvd);


                //Changing date format to dd-mm-yyyy------------------------------------

                final Contacts contacts1 = new Contacts(name, mother_id, id, workplan.DateFormation(dob), workplan.DateFormation(lvd));
                listView.setAdapter(contactAdapter);
                contactAdapter.add(contacts1);


                //dueCount.setText(contactAdapter1.getCount());
            }
        }
        dueCount.setText(contactAdapter.getCount() + " " + getString(R.string.records) + " \n " +
                getString(R.string.total_records) + ": " + total_Count);


        onClickList();
        sqLite_db.close();

    }

    public void onClickList() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {


                Intent intent = new Intent(view.getContext(), Workplan_menu.class);
                Bundle bundle1 = new Bundle();


                bundle1.putString("id", list.get(position));
                bundle1.putString("DOB", listDob.get(position));
                bundle1.putString("LVD", listLvd.get(position));
                intent.putExtras(bundle1);
                startActivity(intent);

            }
        });
    }

    public void showFacilityDetails() {

        exListAdapter = new ExListAdapter(this, facility_list, facility_details);

        facility_list.add(getString(R.string.facility_details));

        sqLite_db = SQLite_db.getInstance(this);
        sqLite_db.open();
        Cursor cursor = sqLite_db.getFacilityInfo(anm_id);


        if (cursor == null) {

            Toast.makeText(getApplicationContext(), getString(R.string.no_record), Toast.LENGTH_LONG).show();
        } else {

            while (cursor.moveToNext()) {

                String block = cursor.getString(0);
                String phc = cursor.getString(1);
                String sc = cursor.getString(2);
                String facility = cursor.getString(4);
                String village = cursor.getString(3);
                // Block.append("\n" + block);
                // SC.append("\n" + sc);

                //  Facility.append("\n" + "(" + facility + ")");
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
        }
        sqLite_db.close();
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DueList.this);
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
        AlertDialog mDialog = mBuilder.create();
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


    public void displayAlert(String message) {
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(DueList.this, Dashboard.class));

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}







