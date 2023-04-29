package com.example.unisicma;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class Show_Due_List extends AppCompatActivity implements SearchView.OnQueryTextListener {

    Button search_Btn;
    public String childId, query_submit;

    public static final String AN_id = "id_k";
    private Toolbar toolbar;

    public static final String AN_name = "name_k";
    public static final String AN_mobile = "mobile_k";

    public static FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__due__list);


        //------------------Fragment section If fragment already present or not---------

        fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Intermediate intermediate = new Intermediate();
            fragmentTransaction.add(R.id.fragment_container, intermediate, null
            );
            fragmentTransaction.commit();

        }


        //  searchView.setOnQueryTextListener(this);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString(AN_id, null);
        String anmName = sharedPreferences.getString(AN_name, null);
        String anmMobile = sharedPreferences.getString(AN_mobile, null);
        getSupportActionBar().setTitle(anmName);
        toolbar.setLogo(R.drawable.ic_face_4dp);
        toolbar.setSubtitle("Mobile:" + anmMobile);


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
                sharedPreferences.edit().clear().commit();

                Toast.makeText(this, "Logout option selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


  /*public void search_vaccine(View view) {


       // String[] vaccines = {"V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "V10", "V11", "V12", "V13", "V14", "V15", "V16", "V17", "V18", "V19", "V20", "V21"};


       // if (Arrays.asList(vaccines).contains(userInput)) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, vaccine_search_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Intent intent = new Intent(Show_Due_List.this, DueList.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("result", response);
                            intent.putExtras(bundle);
                            startActivity(intent);


                            Toast.makeText(getApplicationContext(), userInput, Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), anm_id, Toast.LENGTH_LONG).show();


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
                    SharedPreferences sharedPreferences = getSharedPreferences(ANM_Login.MyPREFERENCE, getApplicationContext().MODE_PRIVATE);
                    anm_id = sharedPreferences.getString(AN_id, null);

                    params.put("anm_id", anm_id);
                    params.put("value", userInput);
                    return params;
                }
            };
            MySingleton.getInstance(Show_Due_List.this).addToRequestQueue(stringRequest);



    }

   */

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
