package com.example.unisicma;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class barcode_scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    public static TextView resultview;

    SQLite_db sqLite_db;
    public static String child_id, child_name, mother_name, child_dob, Facility_id;
    AlertDialog.Builder builder;
    Toolbar toolbar;
    public static final String AN_id = "id_k";
    public static final String AN_name = "name_k";
    public static final String AN_mobile = "mobile_k";
    ZXingScannerView scannerView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        sqLite_db = SQLite_db.getInstance(getApplicationContext());
        builder = new AlertDialog.Builder(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public void handleResult(Result rawResult) {

        String get_result = rawResult.toString();
        Cursor cursor = sqLite_db.getBarCodeValue(get_result);

        //------------------------------------Getting Bar code value after scanning rch card------------------

        if (cursor.getCount() == 0) {

            builder.setTitle(getString(R.string.status));
            builder.setMessage(getString(R.string.barcode_match));
            builder.setPositiveButton(getString(R.string.scan_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton(getString(R.string.biometric), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), RI_Immunization.class);
                    startActivity(intent);
                }
            });
            androidx.appcompat.app.AlertDialog mDialog = builder.create();
            mDialog.show();

        }
        //----------------------------After matching successfully, saving child details in Shared Preference----------------
        else {
            while (cursor.moveToNext()) {
                String mother_id = cursor.getString(0);
                mother_name = cursor.getString(1);
                child_id = cursor.getString(2);
                child_name = cursor.getString(3);
                Facility_id = cursor.getString(4);
                child_dob = cursor.getString(5);
            }

            builder.setTitle(getString(R.string.barcode_matches));
            builder.setMessage(
                    getString(R.string.c_id)+": " + child_id + " \n" +
                    getString(R.string.c_name)+": " + child_name);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    sharedPreferences = getApplicationContext().getSharedPreferences("RI_preference", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("CHILD_ID", child_id);
                    editor.putString("CHILD_NAME", child_name);
                    editor.putString("CHILD_DOB", child_dob);
                    editor.putString("MOTHER_NAME", mother_name);
                    editor.putString("FACILITY", Facility_id);

                    editor.apply();


                    Intent intent = new Intent(getApplicationContext(), RI_Immunization.class);
                    /*Bundle bundle1 = new Bundle();
                    bundle1.putString("child_id", child_id);
                    bundle1.putString("child_name", child_name);
                    bundle1.putString("mother_name", mother_name);
                    bundle1.putString("child_dob", child_dob);
                    bundle1.putString("facility_id",Facility_id);
                    intent.putExtras(bundle1);

                     */
                    startActivity(intent);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();


            // String method = "insert";
            // Background_Actions loginBackend = new Background_Actions(this);
            // loginBackend.execute(method, bar_code_result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            scannerView.setResultHandler(this);
            scannerView.startCamera();

        }

    }
}





