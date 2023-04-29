package com.example.unisicma;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Intermediate extends Fragment implements SearchView.OnQueryTextListener {

    private Spinner spinner;
    android.widget.SearchView searchView;
    String anm_id, userInput, dueDate, part1;
    Button search_Btn;
    public String childId, query_submit;
    String[] part;

    String vaccine_search_url = "http://192.168.42.176/vaccine_search.php";

    public static final String AN_id = "id_k";

    private ConnectivityManager connectivityManager;


    public Intermediate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_intermediate, container, false);

        search_Btn = view.findViewById(R.id.search_btn);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        spinner = view.findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.vaccines_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Choose Vaccine ID")) {

                } else {
                    userInput = parent.getItemAtPosition(position).toString();
                    part = userInput.split(" ");
                    part1 = part[0];


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Toast.makeText(parent.getContext(), "No Option Selected.!", Toast.LENGTH_SHORT).show();

            }
        });


        search_Btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
/*
                if (networkInfo != null && networkInfo.isConnected()) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, vaccine_search_url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Intent intent = new Intent(getContext(), DueList.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("result", response);
                                    intent.putExtras(bundle);
                                    startActivity(intent);


                                    Toast.makeText(getContext(), userInput, Toast.LENGTH_LONG).show();
                                    Toast.makeText(getContext(), anm_id, Toast.LENGTH_LONG).show();


                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> params = new HashMap<String, String>();


                            params.put("anm_id", anm_id);
                            params.put("value", part1);
                            return params;
                        }
                    };
                    MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
                } else {
                    */

                    Intent intent = new Intent(getContext(), DueList.class);
                    Bundle bundle = new Bundle();
                    String code = "VaccineWise";
                    bundle.putString("vaccine_input", part1);
                    bundle.putString("code", code);
                    intent.putExtras(bundle);
                    startActivity(intent);
              //  }
            }
        });


        return view;


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
        anm_id = sharedPreferences.getString(AN_id, null);


    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
