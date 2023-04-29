package com.example.unisicma;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class vaccine_scan extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;
    Context context;

    String vaccine_url = "http://192.168.42.176/vaccine_scan.php";
    private ConnectivityManager connectivityManager;

    public static String scan_result, expiryDate, vaccinationDate,manFac,manDate,batchNo;
    public static String vID, vName, vDosage;

    SQLite_db sqLite_db;
    SQLiteInsert sqLiteInsert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_scan);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);


        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        sqLite_db = SQLite_db.getInstance(getApplicationContext());
        sqLiteInsert = SQLiteInsert.getInstance(getApplicationContext());

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void handleResult(Result rawResult) {
        scan_result = rawResult.getText();

        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
/*
        if (networkInfo != null && networkInfo.isConnected()) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, vaccine_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(response);

                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String status = jsonObject.getString("status");
                                if (status.equals("available")) {

                                    vID = jsonObject.getString("v_id");
                                    vName = jsonObject.getString("v_name");
                                    vDosage = jsonObject.getString("v_dosage");


                                } else if (status.equals("not_available")) {

                                    vID = jsonObject.getString("message");
                                    // Vaccine_details.showResult.setText(vID);


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
                       /*SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
                        String anm_id = sharedPreferences.getString(AN_id, null);

                    params.put("scan_result", scan_result);
                    return params;
                }
            };
            MySingleton.getInstance(vaccine_scan.this).addToRequestQueue(stringRequest);

        } else {
            */

        //------------------------------SQLite Scan Section----------------------
       /* sqLite_db.open();
        Cursor cursor = sqLite_db.VaccineScan(scan_result);
        while (cursor.moveToNext()) {
            vID = cursor.getString(0);
            vName = cursor.getString(1);
            vDosage = cursor.getString(2);
            Log.d("Vaccines", vID);
            Log.d("Vaccines", vName);

            Log.d("Vaccines", vDosage);

        }

        */

        // - --------------------------------------- New Barcode Scan code ---------------------------------------

        sqLite_db.open();
        Cursor BarcodeCursor = sqLite_db.VaccineBarcode(scan_result);
        while (BarcodeCursor.moveToNext()) {
            vID = BarcodeCursor.getString(0);
            vName = BarcodeCursor.getString(1);
            vDosage = BarcodeCursor.getString(2);
            expiryDate = BarcodeCursor.getString(3);
            vaccinationDate = BarcodeCursor.getString(4);
            manDate = BarcodeCursor.getString(5);
            manFac = BarcodeCursor.getString(6);
            batchNo = BarcodeCursor.getString(7);

            Log.d("Vaccines", vID);
            Log.d("Vaccines", vName);
            Log.d("Vaccines", vDosage);

        }



        if (!expiryDate.isEmpty()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date expire_date = simpleDateFormat.parse(expiryDate);
                assert expire_date != null;
                Calendar calendar = Calendar.getInstance();
                String current_date = simpleDateFormat.format(calendar.getTime());
                Date currentDate = simpleDateFormat.parse(current_date);
                if (!vaccinationDate.equals("null") && !vaccinationDate.isEmpty()) {
                    Date vaccination_date = simpleDateFormat.parse(vaccinationDate);
                    assert vaccination_date != null;
                    if (expire_date.compareTo(vaccination_date) > 0) {
                        Vaccine_details.hiddenText.setText(vID);
                        sqLite_db.close();
                        if (Vaccine_details.hiddenText.getText().toString().length() != 0) {
                            if(sqLiteInsert.insertVaccineBarcode(vID,vName,scan_result,current_date,expiryDate,manDate,batchNo,manFac)){
                                Log.d("Vaccines", "Inserted for "+current_date);
                            }else{
                                Log.d("Vaccines", "Already inserted for current date");
                            }
                            sqLiteInsert.close();
                            onBackPressed();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Vaccine has been expired", Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (expire_date.compareTo(currentDate) > 0) {
                        Vaccine_details.hiddenText.setText(vID);
                        sqLite_db.close();
                        if (Vaccine_details.hiddenText.getText().toString().length() != 0) {
                            //sqLiteInsert.updateVaccineBarcode(current_date, scan_result);
                            if(sqLiteInsert.insertVaccineBarcode(vID,vName,scan_result,current_date,expiryDate,manDate,batchNo,manFac)){
                                Log.d("Vaccines", "Inserted for "+current_date);
                            }else{
                                Log.d("Vaccines", "Already inserted for current date");
                            }
                            sqLiteInsert.close();
                            onBackPressed();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Vaccine has been expired", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        //  }
       // Vaccine_details.hiddenText.setText(vID);
      //  sqLite_db.close();
       // if (Vaccine_details.hiddenText.getText().toString().length() != 0) {
        //    onBackPressed();
      //  }
    }


    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();

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


            scannerView.setResultHandler(this);
            scannerView.startCamera();

        }

    }
}
