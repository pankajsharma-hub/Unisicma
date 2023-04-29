package com.example.unisicma;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;



public class RI_Immunization extends AppCompatActivity {

    ViewGroup imgContainer;
    ImageView scan_image;
    Button scan_biometric;
    LinearLayout child_details;
    TextView Child_name, Child_id, Mother_name, DOB, Vaccines_due, biometric_status;
    SQLite_db sqLite_db;
    String child_id, LVD, new_vaccines, child_name, dueDate, Facility_id, formattedDate;
    int dayValue;
    AlertDialog.Builder builder;
    Date Current_date, DueDate;

    Intent filePickerIntent;
    public static final int REQUEST_CODE = 10;
    Uri imageUri;
    SQLiteInsert sqLiteInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ri__immunization);

        imgContainer = findViewById(R.id.transition_layout);
        scan_image = findViewById(R.id.scan_image);
        child_details = findViewById(R.id.child_details);
        Child_id = findViewById(R.id.id_value);
        Child_name = findViewById(R.id.name_value);
        Mother_name = findViewById(R.id.mName_value);
        DOB = findViewById(R.id.dob_value);
        Vaccines_due = findViewById(R.id.remained_value);
        biometric_status = findViewById(R.id.biometric_status);
        sqLite_db = SQLite_db.getInstance(getApplicationContext());
        builder = new AlertDialog.Builder(this);
        sqLiteInsert = SQLiteInsert.getInstance(getApplicationContext());

    }


    // ------------------------------------------------Scan Biometric ------------------------------------------


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                assert data != null;
                imageUri = Objects.requireNonNull(data.getData());
                String baseName = FilenameUtils.getBaseName(getRealPathFromURI(imageUri));
                 Log.d("boimetric:",baseName);

               Cursor verify_biometric = sqLite_db.getBarCodeValue(baseName);

                //----------------------------Authenticating biometric------------------

                if (verify_biometric == null || verify_biometric.getCount() == 0) {
                    Log.d("show",baseName);
                    Toast.makeText(getApplicationContext(),baseName,Toast.LENGTH_LONG).show();

                    biometric_status.setText(getString(R.string.not_matches));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        biometric_status.setTextColor(getColor(R.color.colorRed));
                    }

                    boolean visible = false;
                    TransitionManager.beginDelayedTransition(imgContainer);

                    visible = !visible;
                    scan_image.setVisibility(visible ? View.VISIBLE : View.GONE);
                    biometric_status.setVisibility(visible ? View.VISIBLE : View.GONE);


                } else {

                    biometric_status.setText(getString(R.string.matches));

                    while (verify_biometric.moveToNext()) {
                        String mother_id = verify_biometric.getString(0);
                        SharedPreferences preferences = getSharedPreferences("save to all activity", Context.MODE_PRIVATE);
                        String language = preferences.getString("My_Lang", null);

                        String mother_name = null;
                        String child_name = null;
                        assert language != null;
                        if (language.contains("hi")) {
                            mother_name = verify_biometric.getString(6);
                            child_name = verify_biometric.getString(8);
                            if(mother_name.isEmpty()||child_name.isEmpty()){
                                mother_name = verify_biometric.getString(1);
                                child_name = verify_biometric.getString(3);
                            }

                        } else if (language.contains("ta")) {
                            mother_name = verify_biometric.getString(7);
                            child_name = verify_biometric.getString(9);
                            if(mother_name.isEmpty()||child_name.isEmpty()){
                                mother_name = verify_biometric.getString(1);
                                child_name = verify_biometric.getString(3);
                            }
                        } else {

                            mother_name = verify_biometric.getString(1);
                            child_name = verify_biometric.getString(3);
                        }

                        child_id = verify_biometric.getString(2);
                        Facility_id = verify_biometric.getString(4);
                        String child_dob = verify_biometric.getString(5);

                        Child_id.setText(child_id);
                        Child_name.setText(child_name);
                        Mother_name.setText(mother_name);
                        DOB.setText(DateFormation(child_dob));


                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageUri.toString()));
                            scan_image.setImageBitmap(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        Cursor cursor = sqLite_db.getLVD(child_id);
                        if (cursor.getCount() == 0) {

                        } else {
                            while (cursor.moveToNext()) {
                                LVD = cursor.getString(0);
                            }
                        }

                        //--------------------------------Checking if child comes under Due list or not---------------------

                        dayValue = sqLite_db.DateValue();

                        //------------------------------Getting the Difference between Due date and Current date------------

                        //-----------------------If the difference of due date and current date comes under remaining days of this week only then it will be valid for vaccination----------------

                        if (DateDifference(child_id) > dayValue) {
                            builder.setTitle(getString(R.string.status));
                            builder.setMessage(getString(R.string.child_id) + ":" + child_id + "  " + getString(R.string.not_due));
                            builder.setPositiveButton(getString(R.string.next_child), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("RI_preference", Context.MODE_PRIVATE);
                                    sharedPreferences.edit().clear().apply();
                                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                    startActivity(intent);
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        } else {

                            //-------------------------Get Remained Vaccines---------------------------------

                            Cursor cursor3 = sqLite_db.vaccinesRemained(child_id, LVD);
                            if (cursor3.getCount() == 0) {
                                Vaccines_due.append("No Due Vaccine");
                            } else {
                                while (cursor3.moveToNext()) {
                                    String vaccine_id = cursor3.getString(0);
                                    new_vaccines = cursor3.getString(1);

                                    Vaccines_due.append(new_vaccines + " \n");
                                }
                            }

                            //--------------------------Set Views Visible-----------------------------------

                            biometric_status.setText(getString(R.string.matches));

                            boolean visible = false;
                            TransitionManager.beginDelayedTransition(imgContainer);

                            visible = !visible;
                            scan_image.setVisibility(visible ? View.VISIBLE : View.GONE);
                            biometric_status.setVisibility(visible ? View.VISIBLE : View.GONE);
                            child_details.setVisibility(visible ? ViewGroup.VISIBLE : ViewGroup.GONE);

                        }

                    }
                }
            }

        }
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @SuppressLint("IntentReset")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void Scan_Biometric(View view) {

      /*  Bundle bundle = getIntent().getExtras();
        child_id = bundle.getString("child_id");
        String child_name = bundle.getString("child_name");
        String mother_name = bundle.getString("mother_name");
        String child_dob = bundle.getString("child_dob");
        //facility_id = bundle.getString("facility_id");
        Child_id.setText(child_id);
        Child_name.setText(child_name);
        Mother_name.setText(mother_name);
        DOB.setText(child_dob);


       */


        //filePickerIntent = new Intent();
        filePickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        filePickerIntent.setType("image/*");
       // filePickerIntent.setAction(Intent.ACTION_GET_CONTENT);
       // startActivity(new Intent(getApplicationContext(),Vaccine_details.class));
        startActivityForResult(filePickerIntent,REQUEST_CODE);


         SharedPreferences sharedPreferences = getSharedPreferences("RI_preference", Context.MODE_PRIVATE);
         child_id = sharedPreferences.getString("CHILD_ID", null);
         child_name = sharedPreferences.getString("CHILD_NAME", null);
          String mother_name = sharedPreferences.getString("MOTHER_NAME", null);
        String child_dob = sharedPreferences.getString("CHILD_DOB", null);
        // facility_id = sharedPreferences.getString("FACILITY", null);




        //-------------------------Get last vaccine Date ---------------------------
    }

    //------------------------Proceed for vaccination -------------------------------------

    public void Proceed_vaccine(View view) {

        Intent intent = new Intent(RI_Immunization.this, Vaccine_details.class);
        Bundle bundle1 = new Bundle();
        bundle1.putString("child_id", child_id);
        bundle1.putString("lvd", LVD);
        bundle1.putString("facility_id", Facility_id);
        bundle1.putString("child_name", child_name);
        intent.putExtras(bundle1);


        startActivity(intent);
    }

    public long DateDifference(String child_id) {

        //--------------------------------Checking if child comes under Due list or not---------------------

       // Cursor cursor4 = sqLite_db.DueOnDate(child_id, 0, LVD);
        Cursor cursor4 = sqLite_db.DueOnDate(child_id);
        if (cursor4 == null) {

        } else {
            while (cursor4.moveToNext()) {
                dueDate = cursor4.getString(0);
            }
        }
        //------------------------------Getting the Difference between Due date and Current date------------

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String Present_date = "";
        long difference = 0;
        Present_date = dateFormat.format(calendar.getTime());
        try {
            Current_date = dateFormat.parse(Present_date);
            if (dueDate != null) {
                DueDate = dateFormat.parse(dueDate);
                assert DueDate != null;
                difference = (DueDate.getTime() - Current_date.getTime()) / (1000 * 60 * 60 * 24);
            } else {
                builder.setTitle(getString(R.string.status));
                builder.setMessage(getString(R.string.c_id) + " " + child_id + " \n" +
                        getString(R.string.all_given));
                builder.setPositiveButton(getString(R.string.next_child), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences("RI_preference", Context.MODE_PRIVATE);
                        sharedPreferences.edit().clear().apply();
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                    }
                });
                AlertDialog alertDialog1 = builder.create();
                alertDialog1.show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return difference;
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

}
