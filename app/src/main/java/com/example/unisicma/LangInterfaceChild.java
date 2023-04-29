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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class LangInterfaceChild extends AppCompatActivity {

    ListView child_list;
    ChildListAdapter childListAdapter;

    SQLiteInsert sqLiteInsert;
    TextView Anm_name;
    AlertDialog.Builder builder;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang_interface_child);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        loadlocale();

        child_list = findViewById(R.id.listview3);
        childListAdapter = new ChildListAdapter(this, R.layout.child_list);
        Anm_name = findViewById(R.id.anm_name);
        sqLiteInsert = SQLiteInsert.getInstance(getApplicationContext());
        builder = new AlertDialog.Builder(LangInterfaceChild.this);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String anm_id = bundle.getString("anm_id");
        String anm_name = bundle.getString("anm_name");
        Anm_name.append(anm_name);

        Cursor cursor = sqLiteInsert.getBlankChildren(anm_id);
        final ArrayList<String> id_list = new ArrayList<>();
        final ArrayList<String> name_list = new ArrayList<>();

        if (cursor != null && cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String child_id = cursor.getString(0);
                id_list.add(child_id);
                String child_name = cursor.getString(1);
                name_list.add(child_name);
                ChildList childList = new ChildList(child_name, child_id);
                child_list.setAdapter(childListAdapter);
                childListAdapter.add(childList);
            }

            child_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle bundle1 = new Bundle();
                    Intent intent = new Intent(getApplicationContext(), AddLanguage.class);
                    bundle1.putString("child_id", id_list.get(i));
                    bundle1.putString("child_name", name_list.get(i));
                    intent.putExtras(bundle1);
                    startActivity(intent);
                }
            });
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Workplan workplan = new Workplan();
                builder.setTitle(getString(R.string.status));
                workplan.displayAlert("No Blank Record Found");
            } else {

                Toast.makeText(getApplicationContext(), "No Blank Record Found", Toast.LENGTH_LONG).show();
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LangInterfaceChild.this);
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
