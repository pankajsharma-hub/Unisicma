package com.example.unisicma;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RI_selection extends Fragment {
    Spinner spinner;
    TextView workplan, dueList;
    public static String choosen_item;
    public static String anm_id;

    private ConnectivityManager connectivityManager;

    private LinearLayout linearLayout;

    public static final String AN_id = "id_k";
    String RI_url = "http://192.168.20.56/RI_workplan.php";
    String RI_due_url = "http://192.168.20.56/RI_newDue.php";


    ContactAdapter contactAdapter1;


    public RI_selection() {
        // Required empty public constructor
    }

    //-------------------interface---------------------------
    OnSharingData onSharingdata;

    public interface OnSharingData {
        void onShareMethod(String response
        );

         void onSqliteShared(String method);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ri_selection, container, false);
        linearLayout =  view.findViewById(R.id.rootLayout);
        spinner = view.findViewById(R.id.spinner_days);
        workplan = view.findViewById(R.id.txt_workplan);
        dueList = view.findViewById(R.id.txt_dueList);
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(getContext().CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        List<String> items = new ArrayList<>();
        items.add(0, getString(R.string.choose_day));
        items.add("Monday");
        items.add("Wednesday");
        items.add("Thursday");


        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals(getString(R.string.choose_day))) {
                    return;
                } else {
                    choosen_item = parent.getItemAtPosition(position).toString();

                    String[] ItemsArray = {"Monday", "Wednesday", "Thursday"};
                    if (Arrays.asList(ItemsArray).contains(choosen_item)) {


                        workplan.setOnClickListener(new View.OnClickListener() {


                            @Override
                            public void onClick(View v) {
/*
                                if (networkInfo != null && networkInfo.isConnected()) {
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, RI_url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {

                                                    onSharingdata.onShareMethod(response);
                                                    String method = "insert_data";
                                                    //SQLiBackend sqLiBackend = new SQLiBackend(getContext());
                                                    //sqLiBackend.execute(method, response);


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
                                            params.put("day", choosen_item);
                                            return params;
                                        }
                                    };
                                    MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
                                } else {
                                    */
                                    String method = "Workplan";
                                    onSharingdata.onSqliteShared(method);
//                                  String method = "Select_data";
//                                 SQLiBackend sqLiBackend = new SQLiBackend(getContext());
//                                  sqLiBackend.execute(method, null);
                                    Snackbar.make(linearLayout, "Internet Connection Not Available.!", Snackbar.LENGTH_LONG).show();
                                //}
                            }
                        });


                        dueList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
/*
                                if (networkInfo != null && networkInfo.isConnected()) {

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, RI_due_url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {

                                                    onSharingdata.onShareMethod(response);


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
                                            params.put("day", choosen_item);
                                            return params;
                                        }
                                    };
                                    MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);


                                } else {
                                    */
                                    String method = "DueList";
                                    onSharingdata.onSqliteShared(method);
                              //  }
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Select any Day", Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(ANM_Login.MyPREFERENCE, Context.MODE_PRIVATE);
        anm_id = sharedPreferences.getString(AN_id, null);

        Activity activity = (Activity) context;
        try {
            onSharingdata = (OnSharingData) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " Must implement Interface onSharingData");
        }


    }


}
