package com.example.unisicma;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class login_backend extends AsyncTask<String, String, String> {

    String JSON_STRING;

    Context context;

    AlertDialog alertDialog;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "myprefs";
    public static final String AN_ID = "id_key";

    private String name;


    login_backend(Context ctx) {
        this.context = ctx;

    }

    @Override
    protected String doInBackground(String... params) {
        String method = params[0];
        String reg_url = "http://192.168.42.220/sign_up.php";
        String login_url = "http://192.168.42.220/login.php";
        String asha_signup_url = "http://192.168.42.220/asha_sign_up.php";

        if (method.equals("anm_login")) {
            try {


                String anm_id = params[1];
                String anm_pass = params[2];

                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("anm_id", "UTF-8") + "=" + URLEncoder.encode(anm_id, "UTF-8") + "&"
                        + URLEncoder.encode("anm_pass", "UTF-8") + "=" + URLEncoder.encode(anm_pass, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {

                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (method.equals("anm_register")) {
            try {
                String RCH_ID = params[1];
                String MOBILE = params[2];
                String PASSWORD = params[3];
                String RE_PASS = params[4];
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("RCH_ID", "UTF-8") + "=" + URLEncoder.encode(RCH_ID, "UTF-8") + "&"
                        + URLEncoder.encode("MOBILE", "UTF-8") + "=" + URLEncoder.encode(MOBILE, "UTF-8") + "&"
                        + URLEncoder.encode("PASSWORD", "UTF-8") + "=" + URLEncoder.encode(PASSWORD, "UTF-8") + "&"
                        + URLEncoder.encode("RE_PASS", "UTF-8") + "=" + URLEncoder.encode(RE_PASS, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {

                    stringBuilder.append(JSON_STRING + "\n");


                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (method.equals("asha_register")) {
            String RCH_ID = params[1];
            String MOBILE = params[2];
            String PASSWORD = params[3];
            String RE_PASS = params[4];
            try {
                URL url = new URL(asha_signup_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("RCH_ID", "UTF-8") + "=" + URLEncoder.encode(RCH_ID, "UTF-8") + "&"
                        + URLEncoder.encode("MOBILE", "UTF-8") + "=" + URLEncoder.encode(MOBILE, "UTF-8") + "&"
                        + URLEncoder.encode("PASSWORD", "UTF-8") + "=" + URLEncoder.encode(PASSWORD, "UTF-8") + "&"
                        + URLEncoder.encode("RE_PASS", "UTF-8") + "=" + URLEncoder.encode(RE_PASS, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {

                    stringBuilder.append(JSON_STRING + "\n");


                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onPreExecute() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);


    }

    @Override
    protected void onPostExecute(String result) {
        // ANM_Login.login.setText(result);
        if (result.contains("server_response")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Status")
                    .setMessage(result)
                    .setNeutralButton("OK", null);

            AlertDialog dialog = builder.create();
            dialog.show();

        } else {


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Message")
                    .setMessage(result)
                    .setNeutralButton("OK", null);

            AlertDialog dialog = builder.create();
            dialog.show();


            if (result != null && !result.contains("No Record found.")) {

                sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AN_ID, result);

                editor.commit();
                //String show =  sharedPreferences.getString(AN_ID,null);

                Intent intent = new Intent(context, Dashboard.class);
                context.startActivity(intent);

            }
        }

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);


    }


}





