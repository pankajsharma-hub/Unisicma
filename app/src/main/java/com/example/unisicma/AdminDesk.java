package com.example.unisicma;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import jnr.ffi.annotations.In;

public class AdminDesk extends AppCompatActivity {

    ListView anm_list;
    AnmListAdapter anmListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_desk);

        anm_list = findViewById(R.id.listview3);
        anmListAdapter = new AnmListAdapter(this,R.layout.anm_list);

        AnmList anmList = new AnmList("Sunita Devi","177102503");
        anm_list.setAdapter(anmListAdapter);
        anmListAdapter.add(anmList);

        anm_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getApplicationContext(),LangInterfaceChild.class));
            }
        });

    }


}
