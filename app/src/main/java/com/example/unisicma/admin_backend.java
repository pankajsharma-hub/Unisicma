package com.example.unisicma;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

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

public class admin_backend extends AsyncTask<String, String, String> {
    AlertDialog alertDialog;
    Context context;

    admin_backend(Context ctx) {
        this.context = ctx;

    }

    @Override
    protected String doInBackground(String... param) {
        String admin_id = param[1];
        String admin_pass = param[2];
        String method = param[0];

        String admin_url = "http://10.0.2.2/admin_login.php";

        try {
            URL url = new URL(admin_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("admin_id", "UTF-8") + "=" + URLEncoder.encode(admin_id, "UTF-8") + "&"
                    + URLEncoder.encode("admin_pass", "UTF-8") + "=" + URLEncoder.encode(admin_pass, "UTF-8");
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

        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Message")
                .setMessage(result)
                .setNeutralButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        super.onPreExecute();
    }
}
