package com.example.unisicma;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class SearchAnm extends AppCompatActivity {

    RequestReceiver requestReceiver = new RequestReceiver();

    EditText editText;
    String SearchValue;
    SQLite_db sqLite_db;
    SQLiteInsert sqLiteInsert;
    ListView requestList;
    AnmListAdapter anmListAdapter;

    Calendar calendar = Calendar.getInstance();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String startDate = dateFormat.format(calendar.getTime());


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_anm);

        IntentFilter filter = new IntentFilter("com.example.unisicma.REQUEST_ACTION");
        registerReceiver(requestReceiver, filter);

        loadlocale();

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        editText = findViewById(R.id.anm_search);
        requestList = findViewById(R.id.request_id);
        anmListAdapter = new AnmListAdapter(this, R.layout.anm_list);
        sqLiteInsert = SQLiteInsert.getInstance(this);

        Cursor cursor1 = sqLiteInsert.showRequest(startDate);
        if (cursor1 != null && cursor1.getCount() > 0) {
            while (cursor1.moveToNext()) {
                String request_id = cursor1.getString(0);

                AnmList anmList = new AnmList("ANM request received:", request_id);
                requestList.setAdapter(anmListAdapter);
                anmListAdapter.add(anmList);

            }
        } else {
            AnmList anmList = new AnmList("No Request Received", null);
            requestList.setAdapter(anmListAdapter);
            anmListAdapter.add(anmList);

        }
        sqLite_db = SQLite_db.getInstance(getApplicationContext());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(requestReceiver);
    }

    public void AnmSearch(View view) {
        SearchValue = editText.getText().toString();
        if (!SearchValue.equals("")) {
            Cursor cursor = sqLite_db.getLoginDetails(SearchValue);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                String anm_name = cursor.getString(1);

                Intent intent = new Intent(SearchAnm.this, LangInterfaceChild.class);
                Bundle bundle = new Bundle();
                bundle.putString("anm_id", SearchValue);
                bundle.putString("anm_name", anm_name);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
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

                SharedPreferences sharedPreferences = getSharedPreferences(ADMIN_LOGIN.AdminPREFERENCE, Context.MODE_PRIVATE);
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
        final String[] listItems = {"English", "हिंदी", "اردو", "தமிழ்"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchAnm.this);
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
                    setLocal("ur");
                    recreate();
                } else if (i == 3) {
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


}
