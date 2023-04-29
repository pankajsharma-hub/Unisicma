package com.example.unisicma;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    // -------------Initializing SharedPreference Resources ------------

    public static final String AN_id = "id_k";
    public static final String AN_name = "name_k";
    public static final String AN_mobile = "mobile_k";

    public static final String AdminPREFERENCE = "Adminpref";
    public static final String ADM_id = "id_adm";
    String preferenceValue;

    //------------------End Section ------------------------------------

    // -------------------- Database Instance Declaration ------------

    SQLite_db sqLite_db;
    SQLiteInsert sqLiteInsert;

    // ------------------End Database Declaration -----------------

    Vaccine_details vaccine_details;
    HashConvertor hashConvertor;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---------------------Calling function for changing language -----------
        loadlocale();

        // ---------------- Assignment of Toolbar Id ------------------
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);


        //------------------Getting database Instances -------------------
        sqLite_db = SQLite_db.getInstance(getApplicationContext());
        sqLiteInsert = SQLiteInsert.getInstance(this);
        hashConvertor = new HashConvertor();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            vaccine_details = new Vaccine_details();
        }


        /*login_backend loginBackend = new login_backend(MainActivity.this);
        if(loginBackend.getName() != " "){
            Intent intent = new Intent(MainActivity.this,Dashboard.class);
            intent.putExtra("name",loginBackend.getName());
            startActivity(intent);
            finish();
        }*/

        // -------------- Assigning button to change Language ------------------
        Button changeLang = findViewById(R.id.changelang);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showChangeLanguageDialog();

            }
        });


    }


    private void showChangeLanguageDialog() {
        final String[] listItems = {"English", "हिंदी", "தமிழ்"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
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

        //---------------------Setting selected language to all activities ----------------------
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

    public void ANM_login(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);

        preferenceValue = sharedPreferences.getString(AN_id, null);
        String show1 = sharedPreferences.getString(AN_mobile, null);
        String show2 = sharedPreferences.getString(AN_name, null);

        if ((preferenceValue != null)) {
            Intent intent = new Intent(MainActivity.this, Dashboard.class);
            startActivity(intent);
        } else {

           // Toast.makeText(getApplicationContext(), preferenceValue, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ANM_Login.class));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void Admin_login(View view) {
      /*  String pankaj = "anku12345";
       String hashValue = HashConvertor.getMd5(pankaj);
        int show = sqLite_db.DateValue();
       Toast.makeText(getApplicationContext(), String.valueOf(show), Toast.LENGTH_LONG).show();
         startActivity(new Intent(this, ADMIN_LOGIN.class));
       long diff = vaccine_details.DateDifference("888881002108");
        Toast.makeText(getApplicationContext(), String.valueOf(diff), Toast.LENGTH_LONG).show();



       */
//sqLiteInsert.requestTable();


/* if(sqLiteInsert.insertAdmin_master()) {
    Toast.makeText(getApplicationContext(), "inserted", Toast.LENGTH_LONG).show();
}

 */

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(AdminPREFERENCE, Context.MODE_PRIVATE);
        preferenceValue = sharedPreferences.getString(ADM_id, null);

        if ((preferenceValue != null)) {
            Intent intent = new Intent(MainActivity.this, SearchAnm.class);
            startActivity(intent);
        } else {

            startActivity(new Intent(this, ADMIN_LOGIN.class));
        }


// Cursor cursor = sqLiteInsert.showVaccines();
/*while (cursor.moveToNext()){
    Log.d("vaccines",cursor.getString(0));
    Log.d("vaccines",cursor.getString(1));
    Log.d("vaccines",cursor.getString(7));
    Log.d("vaccines",cursor.getString(6));
}


 */
/*Cursor cursor = sqLite_db.getFacilityInfo("33049");
        cursor.moveToFirst();
            Log.d("vaccines",cursor.getString(0));
            Log.d("vaccines",cursor.getString(1));
            Log.d("vaccines",cursor.getString(2));
            Log.d("vaccines",cursor.getString(3));

 */

        //sqLiteInsert.DeleteData();
//sqLiteInsert.insertChild_master("001","Dudu ram","111110013211","2020-05-21","Male","2020-05-21","3","3320304152","3320304152");
        //sqLiteInsert.insertChild_vaccination("001","V1","33049","3320304152","3","2020-05-22");
        //sqLiteInsert.insertChild_vaccination("001","V2","33049","3320304152","3","2020-05-22");
        // sqLiteInsert.insertChild_vaccination("001","V4","33049","3320304152","3","2020-05-22");


    }


}

