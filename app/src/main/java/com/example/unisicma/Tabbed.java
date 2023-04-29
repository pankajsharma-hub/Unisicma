package com.example.unisicma;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.unisicma.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class Tabbed extends AppCompatActivity {

    Spinner spinner;
    public static String choosen_item;
    public static String anm_id;
    public static final String AN_id = "id_k";
    public static final String AN_name = "name_k";
    public static final String AN_mobile = "mobile_k";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        loadlocale();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
        anm_id = sharedPreferences.getString(AN_id, null);
        String anmName = sharedPreferences.getString(AN_name, null);
        String anmMobile = sharedPreferences.getString(AN_mobile, null);
        SharedPreferences preferences = getSharedPreferences("save to all activity", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            assert language != null;
            if (language.contains("hi") || language.contains("ta")) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.sarojini));
            } else {
                Objects.requireNonNull(getSupportActionBar()).setTitle(anmName);
            }
        }
        toolbar.setLogo(R.drawable.ic_face_4dp);

        toolbar.setSubtitle(getString(R.string.anm_mobile)+": " + anmMobile);

        final TabLayout tabs = findViewById(R.id.tabs);
        tabs.setVisibility(View.GONE);

        FloatingActionButton floating_btn = findViewById(R.id.floating_btn);

        spinner = findViewById(R.id.spinner_days);

        List<String> items = new ArrayList<>();
        items.add(0, getString(R.string.choose_day));
        items.add(getString(R.string.monday));
        items.add(getString(R.string.wednesday));
        items.add(getString(R.string.thursday));

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, items);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals(getString(R.string.choose_day))) {
                    return;
                } else {
                    choosen_item = parent.getItemAtPosition(position).toString();

                    String[] ItemsArray = {getString(R.string.monday), getString(R.string.wednesday), getString(R.string.thursday)};
                    if (Arrays.asList(ItemsArray).contains(choosen_item)) {

                        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(Tabbed.this, getSupportFragmentManager());
                        final ViewPager viewPager = findViewById(R.id.view_pager);
                        viewPager.setAdapter(sectionsPagerAdapter);
                        tabs.setupWithViewPager(viewPager);
                        tabs.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), "Select any Day", Toast.LENGTH_LONG).show();
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //----------------Floating color Dialog-----------------------------------

        floating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(Tabbed.this);
            }
        });


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
        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(Tabbed.this);
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
        androidx.appcompat.app.AlertDialog mDialog = mBuilder.create();
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

    public void showDialog(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.color_dialog, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(view).create();

        Button closeBtn = view.findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();

    }

}