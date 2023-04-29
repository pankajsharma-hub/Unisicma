package com.example.unisicma.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.unisicma.ContactAdapter;
import com.example.unisicma.Contacts;
import com.example.unisicma.R;
import com.example.unisicma.SQLite_db;
import com.example.unisicma.Workplan_menu;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Due_items_fragment extends Fragment {

    String mother_id, dob, id, name, formattedDob, formatedlvd, facility_id;
    AlertDialog.Builder builder;
    ContactAdapter contactAdapter1;
    public ListView listView1;
    TextView dueCount, Red, defineRed, Yellow, defineYellow, Green, defineGreen;
    Button clear_btn;
    public Activity activity;
    SQLite_db sqLite_db;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workplan_items_fragment, container, false);

        contactAdapter1 = new ContactAdapter(this.getActivity(), R.layout.display_layout2);


        builder = new AlertDialog.Builder(getActivity());

        listView1 = view.findViewById(R.id.listview1);
        // clear_btn = view.findViewById(R.id.clr_btn);
        dueCount = view.findViewById(R.id.due_count);
        Red = view.findViewById(R.id.redColor);
        defineRed = view.findViewById(R.id.defineRed);
        Yellow = view.findViewById(R.id.yellowColor);
        defineYellow = view.findViewById(R.id.defineYellow);
        Green = view.findViewById(R.id.greenColor);
        defineGreen = view.findViewById(R.id.defineGreen);

        //Getting instance of SQLite_db class ----------------------------------------
        sqLite_db = SQLite_db.getInstance(getContext());


        //------------------------sqlite section---------------------------
        sqLite_db.open();
        final ArrayList<String> list = new ArrayList<>();
        final ArrayList<String> listLvd = new ArrayList<>();
        final ArrayList<String> listDob = new ArrayList<>();

        Cursor cursor = sqLite_db.getDueInformation(Objects.requireNonNull(getContext()));

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                mother_id = cursor.getString(0);

                SharedPreferences preferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("save to all activity", Context.MODE_PRIVATE);
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
            dueCount.setText(contactAdapter1.getCount() + " " + getString(R.string.records));
        } else {

            builder.setTitle(getString(R.string.response));
            displayAlert(getString(R.string.no_record));

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

