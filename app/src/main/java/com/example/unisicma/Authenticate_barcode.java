package com.example.unisicma;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Authenticate_barcode extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView scannedView;
    SQLite_db sqLite_db;
    String facility_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannedView = new ZXingScannerView(this);
        setContentView(scannedView);
        sqLite_db = SQLite_db.getInstance(getApplicationContext());


    }


    @Override
    public void handleResult(Result result) {


        String get_barCode = result.toString();
        Bundle bundle = getIntent().getExtras();
        String Child_id = bundle.getString("child_id");
        String LVD = bundle.getString("lvd");
        String facility_id = bundle.getString("facility_id");


        String method = "Identify";
        // Background_Actions loginBackend = new Background_Actions(this);
        //loginBackend.execute(method, get_barCode, new_vaccines, M_id);

        Cursor cursor = sqLite_db.getBarCodeValue(get_barCode);

        if (cursor == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.barcode_match), Toast.LENGTH_LONG).show();
        } else {

            while (cursor.moveToNext()) {
               String mother_id = cursor.getString(0);
                String mother_name = cursor.getString(1);
                String child_id = cursor.getString(2);
                String child_name = cursor.getString(3);


            }
            Toast.makeText(getApplicationContext(), "QR code Authenticated  ",Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Authenticate_barcode.this, Vaccine_details.class);
            Bundle bundle1 = new Bundle();
            bundle1.putString("child_id", Child_id);
            bundle1.putString("lvd", LVD);
            bundle1.putString("facility_id", facility_id);
            intent.putExtras(bundle1);
            startActivity(intent);
        }
        onBackPressed();

    }


    @Override
    protected void onPause() {
        super.onPause();
        scannedView.stopCamera();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            scannedView.setResultHandler(this);
            scannedView.startCamera();

        }

    }
}





