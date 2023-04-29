package com.example.unisicma;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListItems extends Fragment {


    String mother_id, dob, id, name, formattedDob, formatedlvd, facility_id;
    AlertDialog.Builder builder;
    ContactAdapter contactAdapter1;
    public ListView listView1;
    TextView dueCount, Red, defineRed, Yellow, defineYellow, Green, defineGreen;
    Button clear_btn;
    public Activity activity;
    SQLite_db sqLite_db;
    public static int listSize;

    private ConnectivityManager connectivityManager;


    public ListItems() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_duelist_items, container, false);
        contactAdapter1 = new ContactAdapter(this.getActivity(), R.layout.display_layout2);


        builder = new AlertDialog.Builder(getActivity());

        listView1 = view.findViewById(R.id.listview1);
//        clear_btn = view.findViewById(R.id.clr_btn);
        dueCount = view.findViewById(R.id.due_count);
        Red = view.findViewById(R.id.redColor);
        defineRed = view.findViewById(R.id.defineRed);
        Yellow = view.findViewById(R.id.yellowColor);
        defineYellow = view.findViewById(R.id.defineYellow);
        Green = view.findViewById(R.id.greenColor);
        defineGreen = view.findViewById(R.id.defineGreen);

        //Getting instance of SQLite_db class ----------------------------------------
        sqLite_db = SQLite_db.getInstance(getContext());


        //creating object of Connectivity manager------------------------
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

/*
        //Checking if network is available or not---------------------------
        if (networkInfo != null && networkInfo.isConnected()) {

            Bundle bundle = getArguments();
            final String response = bundle.getString("response");


            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String code = jsonObject.getString("code");
                    if (code.equals("Getting_data")) {
                        String due_count = jsonObject.getString("count");
                        mother_id = jsonObject.getString("mother_Id");
                        id = jsonObject.getString("child_id");
                        name = jsonObject.getString("child_name");
                        dob = jsonObject.getString("child_dob");
                        String Last_Visit_Date = jsonObject.getString("ldv");

                        dueCount.setText(due_count + " " + getString(R.string.records));


                        //-------------Date Format Change-----------------------

                        DateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date date = simpledate.parse(dob);
                        DateFormat simpledatenew = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        formattedDob = simpledatenew.format(date);

                        Date date1 = simpledate.parse(Last_Visit_Date);
                        formatedlvd = simpledatenew.format(date1);


                        final Contacts contacts1 = new Contacts(name, mother_id, id, formattedDob, formatedlvd);
                        listView1.setAdapter(contactAdapter1);
                        contactAdapter1.add(contacts1);

                        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                JSONArray jsonArray1 = null;

                                try {
                                    jsonArray1 = new JSONArray(response);
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
                        builder.setTitle("Response");
                        builder.setMessage(message);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listView1.setAdapter(null);
                                clear_btn.setVisibility(View.GONE);
                                dueCount.setText("");

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {//else if network connection is not available-----------------

*/
//------------------------sqlite section---------------------------

        Bundle bundle1 = getArguments();
        String method = bundle1.getString("method");//Getting arguments from listItems fragment ------------------------

        sqLite_db.open();
        final ArrayList<String> list = new ArrayList<>();
        final ArrayList<String> listLvd = new ArrayList<>();
        final ArrayList<String> listDob = new ArrayList<>();


        //---------------Workplan---------------------------------

        if (method.equals("Workplan")) {
            Cursor cursor = sqLite_db.getInformation(getContext());
            if (cursor == null) {

                builder.setTitle(getString(R.string.response));
                displayAlert(getString(R.string.no_record));

            } else {

                while (cursor.moveToNext()) {
                    mother_id = cursor.getString(0);

                    SharedPreferences preferences = this.getActivity().getSharedPreferences("save to all activity", Context.MODE_PRIVATE);
                    String language = preferences.getString("My_Lang", null);

                    if (language.contains("hi")) {
                        name = cursor.getString(6);
                        if(name.isEmpty()){
                            name = cursor.getString(2);
                        }

                    } else if (language.contains("ta")) {
                        name = cursor.getString(7);
                        if(name.isEmpty()){
                            name = cursor.getString(2);
                        }

                    } else {
                        name = cursor.getString(2);

                    }

                    id = cursor.getString(1);
                    list.add(id);

                    facility_id = cursor.getString(3);
                    dob = cursor.getString(4);
                    listDob.add(dob);
                    String lvd = cursor.getString(5);
                    listLvd.add(lvd);


                    //Changing date format to dd-mm-yyyy------------------------------------
                    try {
                        DateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date date = null;
                        date = simpledate.parse(dob);
                        DateFormat simpledatenew = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        formattedDob = simpledatenew.format(date);


                        Date date1 = simpledate.parse(lvd);
                        formatedlvd = simpledatenew.format(date1);


                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }


                    final Contacts contacts1 = new Contacts(name, mother_id, id, formattedDob, formatedlvd);
                    listView1.setAdapter(contactAdapter1);
                    contactAdapter1.add(contacts1);

                    //dueCount.setText(contactAdapter1.getCount());
                }
                dueCount.setText(contactAdapter1.getCount() + " " + "Records Found");
            }

//------------------------------------------DueList--------------------------------


        } else if (method.equals("DueList")) {

            Cursor cursor = sqLite_db.getDueInformation(getContext());
            if (cursor == null) {

                builder.setTitle(getString(R.string.response));
                displayAlert(getString(R.string.no_record));

            } else {

                while (cursor.moveToNext()) {
                    mother_id = cursor.getString(0);

                    SharedPreferences preferences = this.getActivity().getSharedPreferences("save to all activity", Context.MODE_PRIVATE);
                    String language = preferences.getString("My_Lang", null);

                    if (language.contains("hi")) {
                        name = cursor.getString(6);
                        if(name.isEmpty()){
                            name = cursor.getString(2);
                        }

                    } else if (language.contains("ta")) {
                        name = cursor.getString(7);
                        if(name.isEmpty()){
                            name = cursor.getString(2);
                        }

                    } else {
                        name = cursor.getString(2);

                    }

                    id = cursor.getString(1);
                    list.add(id);

                    facility_id = cursor.getString(3);
                    dob = cursor.getString(4);
                    listDob.add(dob);
                    String lvd = cursor.getString(5);
                    listLvd.add(lvd);

                    try {
                        DateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date date = null;
                        date = simpledate.parse(dob);
                        DateFormat simpledatenew = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        formattedDob = simpledatenew.format(date);


                        Date date1 = simpledate.parse(lvd);
                        formatedlvd = simpledatenew.format(date1);


                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }


                    //--------------------Adding to list view---------------------

                    final Contacts contacts1 = new Contacts(name, mother_id, id, formattedDob, formatedlvd);
                    listView1.setAdapter(contactAdapter1);
                    contactAdapter1.add(contacts1);
//
                }
                dueCount.setText(contactAdapter1.getCount() + " " + "Records");

            }
        }
        //----------------------------------Onclick list events----------------------------------

        sqLite_db.close();
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {


                Intent intent = new Intent(view.getContext(), Workplan_menu.class);
                Bundle bundle1 = new Bundle();


                bundle1.putString("id", list.get(position));
                bundle1.putString("DOB", listDob.get(position));
                bundle1.putString("LVD", listLvd.get(position));
                bundle1.putString("facility_id", facility_id);
                intent.putExtras(bundle1);
                startActivity(intent);

            }
        });


        // }
//---------------------------clear button-------------------------------
        /**
         *

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView1.setAdapter(null);
                dueCount.setText("");
                clear_btn.setVisibility(View.GONE);
                Red.setVisibility(View.GONE);
                defineRed.setVisibility(View.GONE);
                Yellow.setVisibility(View.GONE);
                defineYellow.setVisibility(View.GONE);
                Green.setVisibility(View.GONE);
                defineGreen.setVisibility(View.GONE);

            }
        });

         */

        //closing else part of network check----------------

        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    public void displayAlert(String message) {
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
