package com.example.unisicma;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SQLiBackend extends AsyncTask<String, String, String>  {
    Context context;

SQLite_db sqLite_db;
    private ListItems listItems;

    SQLiBackend(Context context) {
        this.context = context;
        //sqLite_db = new SQLite_db(context);

    }


    @Override
    protected String doInBackground(String... params) {

        String method = params[0];
      //  SQLite_db sqLite_db = new SQLite_db(context);

        if (method.equals("insert_data")) {
            String sql_response = params[1];


            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(sql_response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String date = jsonObject.getString("date");
                    String vaccine_id = jsonObject.getString("v_id");
                    String child_id = jsonObject.getString("c_id");
                    String anm = jsonObject.getString("anm");
                    String facility_id = jsonObject.getString("fac");
                    String asha = jsonObject.getString("asha");
                    String weight = jsonObject.getString("weight");
                  //  SQLiteDatabase db = sqLite_db.getWritableDatabase();
                   // sqLite_db.insertData(db, child_id, vaccine_id, anm, asha,
                      //      facility_id, weight, date);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return "one row inserted";
        } else if (method.equals("Select_data")) {

            //sqLite_db.getInformation();
                //RI_selection.onSqliteSend(child_id,vaccine_id,date_of_vaccination);


                // String facility_id = cursor.getString(3);

                //String child_dob = cursor.getString(cursor.getColumnIndex(SQLite_db.Child_dob));

                // String lvd = cursor.getString(cursor.getColumnIndex(SQLite_db.LVD));

                // String day = cursor.getString(cursor.getColumnIndex(SQLite_db.DAY));

return "getting data";


        }

        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
//        if (result.equals("one row inserted")) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        // } else if (result.equals("get_info")) {
        //listView.setAdapter(contactAdapter1);

        // }
    }

    @Override
    protected void onProgressUpdate(String... values) {

    }

   /* public Cursor getInformation(){

        SQLiteDatabase sqLiteDatabase = sqLite_db.getReadableDatabase();
        String[] columns = {TableStructure.TableDetails.RCH_CHILD_ID, TableStructure.TableDetails.VACCINE_ID,
                TableStructure.TableDetails.DATE_OF_VACCINATION};
        Cursor cursor = sqLiteDatabase.query(TableStructure.TableDetails.TABLE1,columns,null,
                null,null,null,null);

        return cursor;
    }

    */

}
