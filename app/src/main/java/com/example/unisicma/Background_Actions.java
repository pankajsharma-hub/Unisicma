package com.example.unisicma;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import androidx.appcompat.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Background_Actions extends AsyncTask<String, Void, String> {
    Context ctx;


    public String new_vaccines,M_id;
    String message, status, facility, child_id;
    androidx.appcompat.app.AlertDialog.Builder builder;

    Background_Actions(Context ctx) {
        this.ctx = ctx;
    }


    @Override
    protected String doInBackground(String... params) {
        String method = params[0];
        //String insert = "http://192.168.42.220/insertion.php";
        String match = "http://192.168.42.176/matched_code.php";

       /* if (method.equals("insert")) {


            try {

                String bar_code = params[1];

                URL url = new URL(insert);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("bar_code", "UTF-8") + "=" + URLEncoder.encode(bar_code, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                return "Registration Success";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else

            */
        if (method.equals("Identify")) {

            try {

                String get_code = params[1];
                new_vaccines = params[2];
                M_id = params[3];

                URL url = new URL(match);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("get_code", "UTF-8") + "=" + URLEncoder.encode(get_code, "UTF-8")+"&"+
                        URLEncoder.encode("M_id","UTF-8")+"="+URLEncoder.encode(M_id,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                StringBuilder stringBuilder = new StringBuilder();

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuilder.append(line);
                }


                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


                return stringBuilder.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return null;
    }


    @Override
    protected void onPostExecute(String result) {
        builder = new AlertDialog.Builder(ctx);

//        if (result.equals("Registration Success")) {
//            Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
//
//        } else {
        try {
            JSONArray jsonArray = new JSONArray(result);
            final JSONObject jsonObject = jsonArray.getJSONObject(0);
            status = jsonObject.getString("status");


            if (status.equals("Authenticated")) {

                String mID = jsonObject.getString("mother_id");

                message = jsonObject.getString("mother_name");
                facility = jsonObject.getString("facility");
                child_id = jsonObject.getString("child_id");

                Intent intent = new Intent(ctx, Vaccine_details.class);
                Bundle bundle = new Bundle();
                bundle.putString("facility", facility);
                bundle.putString("child_id", child_id);
                bundle.putString("new_vaccines",new_vaccines);
                intent.putExtras(bundle);

                ctx.startActivity(intent);

              //  Dashboard.resultview2.setText(status);
               // Dashboard.resultview.setText(message);
                //Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();


            } else if (status.equals("No_Record_found")) {

                message = jsonObject.getString("message");
                //Dashboard.resultview2.setText(status);
              //  Dashboard.resultview.setText(message);
                //Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();


            }



                /*if (status.equals("Authenticated")) {
                    String mID = jsonObject.getString("mother_id");
                    message = jsonObject.getString("mother_name");
                    builder.setTitle("Server Response..");
                    builder.setMessage(message);

                } else if (status.equals("No_Record_found")) {
                    message = jsonObject.getString("message");
                    builder.setTitle("Server Response..");
                    builder.setMessage(message);


                }*/


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onPreExecute() {


    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public void displayAlert(final String status) {

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (status.equals("Authenticated")) {
                    builder.setMessage(message);
                    Intent intent = new Intent(ctx, Vaccine_details.class);
                    ctx.startActivity(intent);
                } else if (status.equals("No_Record_found")) {
                    builder.setMessage(message);
                }


            }
        });
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}