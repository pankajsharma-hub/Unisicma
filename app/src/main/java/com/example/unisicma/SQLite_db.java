package com.example.unisicma;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class SQLite_db {
    String days;
    String Lst_vaccine;
    String max_date;
    int count42, count70, count98, count270, count480, count730;

    Context context;
    /*   public String QUERY1 = "CREATE TABLE " + TableStructure.TableDetails.TABLE1 + " (" + TableStructure.TableDetails.RCH_CHILD_ID + " Text," +
               TableStructure.TableDetails.VACCINE_ID + " Text," + TableStructure.TableDetails.ANM_ID +
               " Text," + TableStructure.TableDetails.ASHA_ID + " Text," + TableStructure.TableDetails.FACILITY_ID
               + " Text," + TableStructure.TableDetails.WEIGHT_AT_VACCINATION + " float," + TableStructure.TableDetails.DATE_OF_VACCINATION +
               " date,Primary key(" + TableStructure.TableDetails.RCH_CHILD_ID + "," + TableStructure.TableDetails.VACCINE_ID + "));";


     */
    private SQLiteOpenHelper openHelper;
    private static SQLite_db instance;
    private SQLiteDatabase database;
    Cursor cursor;

    private SQLite_db(Context context) {
        this.openHelper = new DatabaseAccess(context);
    }

    public static SQLite_db getInstance(Context context) {
        if (instance == null) {
            instance = new SQLite_db(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }


    public boolean insertData(String child_id, String vaccine_id, String anm_id,
                              String facility_id, String weight_at_vaccination,String barcode_no) {
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TableStructure.TableDetails.COL1, child_id);
        contentValues.put(TableStructure.TableDetails.COL2, vaccine_id);
        contentValues.put(TableStructure.TableDetails.COL3, anm_id);
        contentValues.put(TableStructure.TableDetails.COL4, "Not Assigned");
        contentValues.put(TableStructure.TableDetails.COL5, facility_id);
        contentValues.put(TableStructure.TableDetails.COL6, weight_at_vaccination);
        contentValues.put(TableStructure.TableDetails.COL8,barcode_no);
        //contentValues.put(Count, count);

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date_of_vaccination = "";
        date_of_vaccination = dateFormat.format(calendar.getTime());
        contentValues.put(TableStructure.TableDetails.COL7, date_of_vaccination);

        sqLiteDatabase.insert(TableStructure.TableDetails.TABLE1, null, contentValues);
        Log.d("Database Operations", "One Row Inserted");


        return true;


    }


//------------------ANM Login----------------------------

    public Cursor getANMLogin(String anm_id, String anm_pass) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT anm_signup.anm_id,mobile FROM anm_signup  WHERE anm_signup.anm_id = '" + anm_id + "' AND password = '" + anm_pass + "'", null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }

    }


    public Cursor getLoginDetails(String anm_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT anm_id,anm_name,anm_mobile,anm_address FROM NRP_anm_master  WHERE anm_id = '" + anm_id + "'", null);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }
    }


    //----------------------ASHA LOGIN --------------------------------------

    public Cursor getASHALogin(String asha_id, String asha_pass) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("select asha_id,asha_name,asha_mobile from NRP_asha_master where asha_id = '" + asha_id + "'", null);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }

    //---------------------------ANM Register------------------------------
    public boolean ANMRegister(String anm_id, String password, String mobile) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("select anm_id from anm_signup where anm_id = '" + anm_id + "'", null);
        if (cursor != null && cursor.moveToFirst()) {

            return false;

        } else {

            SQLiteDatabase sqLiteDatabase1 = openHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(TableStructure.TableDetails.ANM_ID, anm_id);
            contentValues.put(TableStructure.TableDetails.ANM_PASS, password);
            contentValues.put(TableStructure.TableDetails.ANM_MOBILE, mobile);
            sqLiteDatabase1.insert(TableStructure.TableDetails.TABLE2, null, contentValues);
            Log.d("Database Operations", "Registered Successfully");
            return true;

        }


    }

    //------------------------------------------Days Logic---------------------------------------------------------
    public int DateValue() {

        Calendar calendar = Calendar.getInstance();
        int week_day = calendar.get(Calendar.DAY_OF_WEEK);

        if (week_day == 7) { // Saturday
            int Inc_day = week_day - 6;
            return Inc_day;
        } else if (week_day == 6) {  // Friday
            int Inc_day = week_day - 4;
            return Inc_day;
        } else if (week_day == 5) { // Thursday
            int Inc_day = week_day - 2;
            return Inc_day;
        } else if (week_day == 4) { // Wednesday
            int Inc_day = week_day;
            return Inc_day;
        } else if (week_day == 3) { // Tuesday
            int Inc_day = week_day + 2;
            return Inc_day;
        } else if (week_day == 2) { // Monday
            int Inc_day = week_day + 4;
            return Inc_day;
        } else if (week_day == 1) {  // Sunday
            int Inc_day = week_day + 6;
            return Inc_day;
        }
        return 0;
    }
//-----------------------------------------------Getting RI Workplan--------------------------------------------

    public Cursor getInformation(Context context) {

        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

//        String[] columns = {TableStructure.TableDetails.RCH_CHILD_ID, TableStructure.TableDetails.VACCINE_ID,
//                TableStructure.TableDetails.DATE_OF_VACCINATION};

        if (Tabbed.choosen_item.equals(context.getString(R.string.monday))) {

            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_master.rch_mother_id, NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.resident_facility_id,\n" +
                            "                                             child_dob,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_master join NRP_anm_master join NRP_child_immunization_fact_table join NRP_vaccine_master\n" +
                            "                                             on NRP_child_master.resident_facility_id = NRP_anm_master.anm_address\n" +
                            "                                              and \n" +
                            "                                             NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id \n" +
                            "                                             where \n" +
                            "                                             NRP_child_immunization_fact_table.date_of_vaccination<> date('now')\n" +
                            "                                             AND\n" +
                            "                                             NRP_anm_master.anm_id =  '" + Tabbed.anm_id + "'\n" +
                            "                                             GROUP BY  NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                             HAVING \n" +
                            "                                             date(max(date_of_vaccination),'+42 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                            "                                              AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V5')\n" +
                            "                              \n" +
                            "                            OR    date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V5')\n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t\t   OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V5')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42')\n" +
                            "                             OR \n" +
                            "\t\t\t\t\t\t\t  date(max(date_of_vaccination),'+42 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                            "                                              AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V6')\n" +
                            "                              \n" +
                            "                            OR    date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V6')\n" +
                            "\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V6')\n" +
                            "                             AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42')\n" +
                            "\t\t\t\t\t\t    OR \n" +
                            "\t\t\t\t\t\t\t date(max(date_of_vaccination),'+42 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                            "                                              AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V7')\n" +
                            "                              \n" +
                            "                            OR    date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V7') \n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t\t   OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V7')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42')\n" +
                            "\t\t\t\t\t\t\t  OR \n" +
                            "\t\t\t\t\t\t\tdate(max(date_of_vaccination),'+42 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                            "                                              AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V8')\n" +
                            "                              \n" +
                            "                            OR    date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V8')\n" +
                            "\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V8')\n" +
                            "                             AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42')\n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\tOR\n" +
                            "\t\t\t                            date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                            "                                             AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t NRP_child_immunization_fact_table.vaccine_id = 'V9')\n" +
                            "                                           OR\n" +
                            "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V9')\n" +
                            "\t\t\t\t\t\t\t\t\t\tOR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V9')\n" +
                            "                                AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' ))\n" +
                            "                               OR\n" +
                            "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                            "                                             AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t NRP_child_immunization_fact_table.vaccine_id = 'V10')\n" +
                            "                                           OR\n" +
                            "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V10')\n" +
                            "\t\t\t\t\t\t\t\t\t\tOR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\tmax(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V10')\n" +
                            "                             AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' ))\n" +
                            "\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t     date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                            "                                             AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t NRP_child_immunization_fact_table.vaccine_id = 'V11')\n" +
                            "                                           OR\n" +
                            "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V11')\n" +
                            "\t\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V11')\n" +
                            "                                    AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' ))\n" +
                            "                               OR\n" +
                            "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                            "                                             AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t NRP_child_immunization_fact_table.vaccine_id = 'V12')\n" +
                            "                                           OR\n" +
                            "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V12')\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V12')\n" +
                            "                               AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' ))ORDER BY NRP_child_immunization_fact_table.rch_child_id",
                    null);
            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }


        } else if (Tabbed.choosen_item.equals(context.getString(R.string.wednesday))) {

            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_master.rch_mother_id, NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.resident_facility_id,\n" +
                            "                                                                                                 child_dob,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_master join NRP_anm_master join NRP_child_immunization_fact_table join NRP_vaccine_master\n" +
                            "                                                                                                  on NRP_child_master.resident_facility_id = NRP_anm_master.anm_address\n" +
                            "                                                                                                   and \n" +
                            "                                                                                                   NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id \n" +
                            "                                                                                                 where \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.date_of_vaccination<> date('now')\n" +
                            "                                                                                                 AND\n" +
                            "                                                                                                   NRP_anm_master.anm_id =  '" + Tabbed.anm_id + "' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   GROUP BY  NRP_child_immunization_fact_table.rch_child_id \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   Having\n" +
                            "                                                            (NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                 WHERE  when_to_use_days = '42' \n" +
                            "                                                AND  NRP_child_immunization_fact_table.rch_child_id IN( \n" +
                            "                                                SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                            "                                               JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                            "                                                AND\n" +
                            "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 70 AND\n" +
                            "                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V13'))\n" +
                            "                                             OR\n" +
                            "                                  (NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                              FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE  when_to_use_days = '42' \n" +
                            "                                               AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(\n" +
                            "                                               SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                            "                                                JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                            "                                                AND\n" +
                            "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 70 AND\n" +
                            "                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V13')) \n" +
                            "\t\t\t\t\t\t\t\t\tOR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-27 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V13')\n" +
                            "                                   AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70'  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98')))\n" +
                            "                                  OR\n" +
                            "\t\t\t\t\t\t\t   (NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                 WHERE  when_to_use_days = '42' \n" +
                            "                                                AND  NRP_child_immunization_fact_table.rch_child_id IN( \n" +
                            "                                                SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                            "                                               JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                            "                                                AND\n" +
                            "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 70 AND\n" +
                            "                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V17'))\n" +
                            "                                            OR\n" +
                            "\t\t\t\t\t\t\t\t\t (NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                              FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE  when_to_use_days = '42' \n" +
                            "                                               AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(\n" +
                            "                                               SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                            "                                                JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                            "                                                AND\n" +
                            "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 70 AND\n" +
                            "                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V17')) \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-27 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V17')\n" +
                            "                              AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70'  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98')))\n" +
                            "          OR\n" +
                            "\t\t\t\t\tdate(max(date_of_vaccination),'+172 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V14')\n" +
                            "                  AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                            "\t\t\t\t\t\t\t\t OR\n" +
                            "                    date(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V14' )\n" +
                            "                              AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                            "\t\t\t\t\t\t  OR\n" +
                            "\t\t\t\t\t\t   max(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V14')\n" +
                            "                        AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' ))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "                       OR\n" +
                            "\t\t\t\t\t\t\t date(max(date_of_vaccination),'+172 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V15')\n" +
                            "                      AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                            "\t\t\t\t\t\t\t\t OR\n" +
                            "                                  date(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V15' )\n" +
                            "                                AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                            "\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\tmax(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V15')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' ))))\n" +
                            "\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\tdate(max(date_of_vaccination),'+172 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V16')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "                                OR\n" +
                            "                   date(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V16' )\n" +
                            "                 \n" +
                            "                                           AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\tOR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V16')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' ))))ORDER BY NRP_child_immunization_fact_table.rch_child_id",
                    null);
            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        } else if (Tabbed.choosen_item.equals(context.getResources().getString(R.string.thursday))) {

            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_master.rch_mother_id, NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.resident_facility_id,\n" +
                            "                                                                                                 child_dob,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_master join NRP_anm_master join NRP_child_immunization_fact_table join NRP_vaccine_master\n" +
                            "                                                                                                  on NRP_child_master.resident_facility_id = NRP_anm_master.anm_address\n" +
                            "                                                                                                   and \n" +
                            "                                                                                                   NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id \n" +
                            "                                                                                                 where \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.date_of_vaccination<> date('now')\n" +
                            "                                                                                                 AND\n" +
                            "                                                                                                   NRP_anm_master.anm_id =  '" + Tabbed.anm_id + "' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   GROUP BY  NRP_child_immunization_fact_table.rch_child_id \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   Having\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+210 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                            "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V18')\n" +
                            "\t\t\t\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t\t OR\n" +
                            "                                date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                            "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V18')\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V18')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "                               OR\n" +
                            "\t\t\t\t\t\t\t  date(max(date_of_vaccination),'+210 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                            "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V19')\n" +
                            "\t\t\t\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t OR\n" +
                            "                                date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                            "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V19')\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V19')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                            "\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t  date(max(date_of_vaccination),'+210 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                            "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V20')\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t OR\n" +
                            "                                date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                            "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V20')\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V20')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                            "\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+210 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                            "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V3')\n" +
                            "\t\t\t\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t\tOR\n" +
                            "                                date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                            "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V3')\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V3')\n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                            "\t\tOR\n" +
                            "\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t      date(max(date_of_vaccination),'+240 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') AND julianday('now') - julianday(child_dob) > 486\n" +
                            "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                  on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V21')\n" +
                            "                                OR\n" +
                            "                                 date(max(date_of_vaccination),'+240 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 486\n" +
                            "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                  on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V21')\n" +
                            "                                                            ORDER BY NRP_child_immunization_fact_table.rch_child_id",
                    null);
            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        }
        return null;

    }
//-------------------------------------------Getting RI Due data-------------------------------

    public Cursor getDueInformation(Context context) {

        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

//        String[] columns = {TableStructure.TableDetails.RCH_CHILD_ID, TableStructure.TableDetails.VACCINE_ID,
//                TableStructure.TableDetails.DATE_OF_VACCINATION};

        if (Tabbed.choosen_item.equals(context.getString(R.string.monday))) {
            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_master.rch_mother_id, NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.resident_facility_id,\n" +
                            "                                             child_dob,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_master join NRP_anm_master  \n" +
                            "                            join NRP_child_immunization_fact_table join NRP_vaccine_master\n" +
                            "                                             on NRP_child_master.resident_facility_id = NRP_anm_master.anm_address\n" +
                            "                                              and \n" +
                            "                                             NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id \n" +
                            "                                             where \n" +
                            "                                             NRP_child_immunization_fact_table.date_of_vaccination<> date('now')\n" +
                            "                                             AND\n" +
                            "                                             NRP_anm_master.anm_id =   '" + Tabbed.anm_id + "' \n" +
                            "                                             GROUP BY  NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                             HAVING \n" +
                            "                                            date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V5')\n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t\t   OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 day')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V5')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42')\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\tOR\n" +
                            "\t\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t\tdate(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V6')\n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t\t   OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V6')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42')\n" +
                            "\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t \n" +
                            "\t\t\t\t\t\t\tOR \n" +
                            "\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V7') \n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t\t   OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V7')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42')\n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t\t   OR \n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V8')\n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t  OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V8')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42')\n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\tOR\n" +
                            "\t\n" +
                            "\t                                   date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                              AND julianday('now') - julianday(child_dob) >= 43 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V9')\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\tOR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V9')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' ))\n" +
                            "                               OR\n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                              AND julianday('now') - julianday(child_dob) >= 43 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V10')\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t   OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V10')\n" +
                            "                 \n" +
                            "                                       AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' ))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t\n" +
                            "                               OR\n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                              AND julianday('now') - julianday(child_dob) >= 43 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V11')\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t   OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V11')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' ))\n" +
                            "                               OR\n" +
                            "\t\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                            "                                              AND julianday('now') - julianday(child_dob) >= 43 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V12')\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V12')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' )) ORDER BY NRP_child_immunization_fact_table.rch_child_id",
                    null);

            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        } else if (Tabbed.choosen_item.equals(context.getString(R.string.wednesday))) {
            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_master.rch_mother_id, NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.resident_facility_id,\n" +
                            "                                             child_dob,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_master join NRP_anm_master  \n" +
                            "                            join NRP_child_immunization_fact_table join NRP_vaccine_master\n" +
                            "                                             on NRP_child_master.resident_facility_id = NRP_anm_master.anm_address\n" +
                            "                                              and \n" +
                            "                                             NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id \n" +
                            "                                             where \n" +
                            "                                             NRP_child_immunization_fact_table.date_of_vaccination<> date('now')\n" +
                            "                                             AND\n" +
                            "                                             NRP_anm_master.anm_id =   '" + Tabbed.anm_id + "' \n" +
                            "                                             GROUP BY  NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                             HAVING \n" +
                            "                                            (NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                              FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE  when_to_use_days = '42' \n" +
                            "                                               AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(\n" +
                            "                                               SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                            "                                                JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                            "                                                AND\n" +
                            "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) >= 71 AND\n" +
                            "                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V13')) \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t \n" +
                            "\t\t\t\t\t\t\t\t\t\tOR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-27 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V13')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70'  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98')))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\tOR\n" +
                            "\t\t\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t\t\t(NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                              FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE  when_to_use_days = '42' \n" +
                            "                                               AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(\n" +
                            "                                               SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                            "                                                JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                            "                                                AND\n" +
                            "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) >= 71 AND\n" +
                            "                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V17')) \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-27 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V17')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70'  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98')))\n" +
                            "                                \n" +
                            "\t\t\t\t\t\t\t\t\n" +
                            "         OR\n" +
                            "\t\t \n" +
                            "\t\t                          date(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) >= 99 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V14' )\n" +
                            "                 \n" +
                            "                                           AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "                           OR\n" +
                            "\t\t\t\t\t\t   \n" +
                            "\t\t\t\t\t\t   max(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V14')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' ))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "                             \n" +
                            "\t\t\t\t\t\t   \n" +
                            "\t                  OR\n" +
                            "\t\t\t\t\t  \n" +
                            "\t\t\t\t\t   date(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) >= 99 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V15' )\n" +
                            "                 \n" +
                            "                                           AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "                                OR\n" +
                            "\t\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V15')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' ))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "                                OR\n" +
                            "\t\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t\tdate(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) >= 99 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V16' )\n" +
                            "                 \n" +
                            "                                           AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\tOR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t   max(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V16')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' )))) ORDER BY NRP_child_immunization_fact_table.rch_child_id",
                    null);

            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        } else if (Tabbed.choosen_item.equals(context.getString(R.string.thursday))) {

            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_master.rch_mother_id, NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.resident_facility_id,\n" +
                            "                                             child_dob,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_master join NRP_anm_master  \n" +
                            "                            join NRP_child_immunization_fact_table join NRP_vaccine_master\n" +
                            "                                             on NRP_child_master.resident_facility_id = NRP_anm_master.anm_address\n" +
                            "                                              and \n" +
                            "                                             NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id \n" +
                            "                                             where \n" +
                            "                                             NRP_child_immunization_fact_table.date_of_vaccination<> date('now')\n" +
                            "                                             AND\n" +
                            "                                             NRP_anm_master.anm_id =   '" + Tabbed.anm_id + "' \n" +
                            "                                             GROUP BY  NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                             HAVING \n" +
                            "                                            date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) >= 274 \n" +
                            "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V18')\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V18')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "                               \n" +
                            "                              OR\n" +
                            "\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) >= 274 \n" +
                            "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V19')\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V19')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\tOR\n" +
                            "\t\t\t\t\t\t\t\t\t\n" +
                            "\t\t\t\t\t\t\t\t\tdate(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) >= 274 \n" +
                            "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V20')\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V20')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "                               \n" +
                            "                              OR\n" +
                            "\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) >= 274 \n" +
                            "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V3')\n" +
                            "\t\t\t\t\t\t\t\t\t\t AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t OR\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date(date('now'),'-1 DAY')\n" +
                            "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                            "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V3')\n" +
                            "                 \n" +
                            "                                     AND\n" +
                            "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                            "                                                  WHERE   when_to_use_days = '42'\n" +
                            "                                                             AND\n" +
                            "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                            "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                            "                                                WHERE   when_to_use_days = '70' \n" +
                            "                                                                                                  AND\n" +
                            "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                            "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                            "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                            "                               \n" +
                            "       OR\n" +
                            "\t   \n" +
                            "\t                             date(max(date_of_vaccination),'+240 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) >= 487\n" +
                            "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                            "                                                  on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V21') ORDER BY NRP_child_immunization_fact_table.rch_child_id",
                    null);
            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        }
        return null;
    }
    /*
     *----Getting Due details---------------------
     */

    public Cursor DueDetails(String child_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

//        String[] columns = {TableStructure.TableDetails.RCH_CHILD_ID, TableStructure.TableDetails.VACCINE_ID,
//                TableStructure.TableDetails.DATE_OF_VACCINATION};
        cursor = sqLiteDatabase.rawQuery(" SELECT distinct NRP_mother_master1.rch_mother_id,mother_name,NRP_child_master.rch_child_id,mother_mobile,husband_name,husband_mobile,NRP_facility_master.facility_id,NRP_child_master.child_dob,NRP_child_master.child_name," +
                        "mother_name_hi,father_name_hi,mother_name_tm,father_name_tm,child_name_hi,child_name_tm " +
                        "from NRP_mother_master1 join NRP_child_master join NRP_facility_master ON NRP_child_master.resident_facility_id = NRP_facility_master.facility_id AND  NRP_mother_master1.rch_mother_id = NRP_child_master.rch_mother_id " +
                        "WHERE NRP_child_master.rch_child_id = " + child_id + "\n",
                null);
        return cursor;
    }
//---------------Getting received vaccines-------------------

    public Cursor vaccinesGiven(String child_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

//        String[] columns = {TableStructure.TableDetails.RCH_CHILD_ID, TableStructure.TableDetails.VACCINE_ID,
//                TableStructure.TableDetails.DATE_OF_VACCINATION};
        Cursor cursor = sqLiteDatabase.rawQuery(" select DISTINCT NRP_vaccine_master.vaccine_name,NRP_child_immunization_fact_table.date_of_vaccination from NRP_vaccine_master " +
                        "join NRP_child_immunization_fact_table on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id where rch_child_id = " + child_id + " ORDER BY when_to_use_days",
                null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    //-------------------Getting Due on date-----------------------
    public Cursor DueOnDate(String child_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

        final ArrayList<String> Last_vaccine = new ArrayList<>();

        Cursor cursor2 = sqLiteDatabase.rawQuery("select max(date_of_vaccination) FROM NRP_child_immunization_fact_table WHERE rch_child_id = '" + child_id + "'", null);
        while (cursor2.moveToNext()) {
            max_date = cursor2.getString(0);
        }

        Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_immunization_fact_table.vaccine_id FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master ON \n" +
                        "                        NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id  where rch_child_id = '" + child_id + "' and date_of_vaccination = '" + max_date + "' GROUP BY NRP_child_immunization_fact_table.vaccine_id \n" +
                        "                        ",
                null);
        int vaccineCount = cursor1.getCount();
        while (cursor1.moveToNext()) {
            Lst_vaccine = cursor1.getString(0);
            Last_vaccine.add(Lst_vaccine);
        }

        Cursor cursor42 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '42' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor42 != null) {
            count42 = cursor42.getCount();
        }
        Cursor cursor70 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '70' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor70 != null) {
            count70 = cursor70.getCount();
        }
        Cursor cursor98 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '98' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor98 != null) {
            count98 = cursor98.getCount();
        }
        Cursor cursor273 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '273' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor273 != null) {
            count270 = cursor273.getCount();
        }
        Cursor cursor486 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '486' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor486 != null) {
            count480 = cursor486.getCount();
        }
        Cursor cursor730 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '730' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor730 != null) {
            count730 = cursor730.getCount();
        }


        if ((vaccineCount == 3) && Last_vaccine.contains("V1") && Last_vaccine.contains("V2") && Last_vaccine.contains("V4")) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+42 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id,
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }

        } else if ((vaccineCount < 3) && (Last_vaccine.contains("V1") || Last_vaccine.contains("V2") || Last_vaccine.contains("V4"))) {
            cursor = sqLiteDatabase.rawQuery("Select max(date_of_vaccination) as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id,
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount == 4) && (Last_vaccine.contains("V5") && Last_vaccine.contains("V6") && Last_vaccine.contains("V7") && Last_vaccine.contains("V8"))) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+28 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id,
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount < 4) && (Last_vaccine.contains("V5") || Last_vaccine.contains("V6") || Last_vaccine.contains("V7") || Last_vaccine.contains("V8")) && count42 != 4) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+42 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id + " and vaccine_id = 'V4'",
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }

        } else if ((vaccineCount < 4) && (Last_vaccine.contains("V5") || Last_vaccine.contains("V6") || Last_vaccine.contains("V7") || Last_vaccine.contains("V8")) && count42 == 4) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+28 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id + "",
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }

        } else if ((vaccineCount == 4) && (Last_vaccine.contains("V9") && Last_vaccine.contains("V10") && Last_vaccine.contains("V11") && Last_vaccine.contains("V12"))) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+28 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id,
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount < 4) && (Last_vaccine.contains("V9") || Last_vaccine.contains("V10") || Last_vaccine.contains("V11") || Last_vaccine.contains("V12")) && count70 != 4) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+28 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id + " and vaccine_id = 'V8'",
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount < 4) && (Last_vaccine.contains("V9") || Last_vaccine.contains("V10") || Last_vaccine.contains("V11") || Last_vaccine.contains("V12")) && count70 == 4) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+28 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id + "",
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount == 2) && (Last_vaccine.contains("V13") && Last_vaccine.contains("V17"))) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+172 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id,
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount < 2) && (Last_vaccine.contains("V13") || Last_vaccine.contains("V17")) && count98 != 2) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+28 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id + " and vaccine_id = 'V12'",
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount < 2) && (Last_vaccine.contains("V13") || Last_vaccine.contains("V17")) && count98 == 2) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+172 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id + "",
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount == 3) && (Last_vaccine.contains("V14") && Last_vaccine.contains("V15") && Last_vaccine.contains("V16"))) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+210 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id,
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount < 3) && (Last_vaccine.contains("V14") || Last_vaccine.contains("V15") || Last_vaccine.contains("V16")) && count270 != 3) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+172 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id + " and vaccine_id = 'V17'",
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount < 3) && (Last_vaccine.contains("V14") || Last_vaccine.contains("V15") || Last_vaccine.contains("V16")) && count270 == 3) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+210 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id + "",
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount == 4) && (Last_vaccine.contains("V18") && Last_vaccine.contains("V19") && Last_vaccine.contains("V20") && Last_vaccine.contains("V3"))) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+240 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id,
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount < 4) && (Last_vaccine.contains("V18") || Last_vaccine.contains("V19") || Last_vaccine.contains("V20") || Last_vaccine.contains("V3")) && count480 != 4) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+210 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id + " and vaccine_id = 'V16'",
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        } else if ((vaccineCount < 4) && (Last_vaccine.contains("V18") || Last_vaccine.contains("V19") || Last_vaccine.contains("V20") || Last_vaccine.contains("V3")) && count480 == 4) {
            cursor = sqLiteDatabase.rawQuery("Select date(max(date_of_vaccination),'+240 day') as dueDate from NRP_child_master \n" +
                            "join NRP_child_immunization_fact_table on NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id\n" +
                            " where NRP_child_immunization_fact_table.rch_child_id = " + child_id + "",
                    null);
            if (cursor == null) {
                return null;
            } else {
                return cursor;
            }
        }


//        String[] columns = {TableStructure.TableDetails.RCH_CHILD_ID, TableStructure.TableDetails.VACCINE_ID,
//                TableStructure.TableDetails.DATE_OF_VACCINATION};
        return null;
    }

    public Cursor vaccinesRemained(String child_id, String Last_vaccine_date) {

        final ArrayList<String> Last_vaccine = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

        Cursor cursor2 = sqLiteDatabase.rawQuery("select max(date_of_vaccination) FROM NRP_child_immunization_fact_table WHERE rch_child_id = '" + child_id + "'", null);
        while (cursor2.moveToNext()) {
            max_date = cursor2.getString(0);
        }

        Cursor cursor1 = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_immunization_fact_table.vaccine_id FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master ON \n" +
                        "NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE date_of_vaccination = '" + Last_vaccine_date +
                        "' and rch_child_id = " + child_id + " GROUP BY NRP_child_immunization_fact_table.vaccine_id ",
                null);
        int vaccineCount = cursor1.getCount();
        while (cursor1.moveToNext()) {
            Lst_vaccine = cursor1.getString(0);
            Last_vaccine.add(Lst_vaccine);
            Last_vaccine.add(Lst_vaccine);
        }

        Cursor cursor42 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '42' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor42 != null) {
            count42 = cursor42.getCount();
        }
        Cursor cursor70 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '70' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor70 != null) {
            count70 = cursor70.getCount();
        }
        Cursor cursor98 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '98' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor98 != null) {
            count98 = cursor98.getCount();
        }
        Cursor cursor273 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '273' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor273 != null) {
            count270 = cursor273.getCount();
        }
        Cursor cursor486 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '486' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor486 != null) {
            count480 = cursor486.getCount();
        }
        Cursor cursor730 = sqLiteDatabase.rawQuery("SELECT NRP_child_immunization_fact_table.vaccine_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master\n" +
                "on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE when_to_use_days = '730' AND\n" +
                "rch_child_id = '" + child_id + "'", null);
        if (cursor730 != null) {
            count730 = cursor730.getCount();
        }

        if (vaccineCount == 3 && Last_vaccine.contains("V1") && Last_vaccine.contains("V2") && Last_vaccine.contains("V4")) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '42' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount < 3 && (Last_vaccine.contains("V1") || Last_vaccine.contains("V2") || Last_vaccine.contains("V4"))) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '0' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount == 4 && Last_vaccine.contains("V5") && Last_vaccine.contains("V6") && Last_vaccine.contains("V7") && Last_vaccine.contains("V8")) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '70' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount < 4 && (Last_vaccine.contains("V5") || Last_vaccine.contains("V6") || Last_vaccine.contains("V7") || Last_vaccine.contains("V8")) && count42 != 4) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '42' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount < 4 && (Last_vaccine.contains("V5") || Last_vaccine.contains("V6") || Last_vaccine.contains("V7") || Last_vaccine.contains("V8")) && count42 == 4) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '70' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount == 4 && Last_vaccine.contains("V9") && Last_vaccine.contains("V10") && Last_vaccine.contains("V11") && Last_vaccine.contains("V12")) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '98' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount < 4 && (Last_vaccine.contains("V9") || Last_vaccine.contains("V10") || Last_vaccine.contains("V11") || Last_vaccine.contains("V12")) && count70 != 4) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '70' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount < 4 && (Last_vaccine.contains("V9") || Last_vaccine.contains("V10") || Last_vaccine.contains("V11") || Last_vaccine.contains("V12")) && count70 == 4) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '98' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount == 2 && Last_vaccine.contains("V13") && Last_vaccine.contains("V17")) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '273' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount < 2 && (Last_vaccine.contains("V13") || Last_vaccine.contains("V17")) && count98 != 2) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '98' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount < 2 && (Last_vaccine.contains("V13") || Last_vaccine.contains("V17")) && count98 == 2) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '273' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount == 3 && Last_vaccine.contains("V14") && Last_vaccine.contains("V15") && Last_vaccine.contains("V16")) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '486' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if ((vaccineCount < 3) && (Last_vaccine.contains("V14") || Last_vaccine.contains("V15") || Last_vaccine.contains("V16")) && count270 != 3) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '273' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if ((vaccineCount < 3) && (Last_vaccine.contains("V14") || Last_vaccine.contains("V15") || Last_vaccine.contains("V16")) && count270 == 3) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '486' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount == 4 && Last_vaccine.contains("V18") && Last_vaccine.contains("V19") && Last_vaccine.contains("V20") && Last_vaccine.contains("V3")) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '730' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount < 4 && (Last_vaccine.contains("V18") || Last_vaccine.contains("V19") || Last_vaccine.contains("V20") || Last_vaccine.contains("V3")) && count480 != 4) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '486' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        } else if (vaccineCount < 4 && (Last_vaccine.contains("V18") || Last_vaccine.contains("V19") || Last_vaccine.contains("V20") || Last_vaccine.contains("V3")) && count480 == 4) {
            cursor = sqLiteDatabase.rawQuery("Select distinct NRP_vaccine_master.vaccine_id,vaccine_name from NRP_vaccine_master  \n" +
                            "WHERE NRP_vaccine_master.vaccine_id NOT IN(SELECT vaccine_id from NRP_child_immunization_fact_table \n" +
                            "WHERE rch_child_id = " + child_id + ") and when_to_use_days = '730' ORDER BY when_to_use_days ",
                    null);
            return cursor;
        }

        if (cursor == null) {
            return null;
        } else {
            return cursor;
        }
    }
//       String[] columns = {TableStructure.TableDetails.RCH_CHILD_ID, TableStructure.TableDetails.VACCINE_ID,
//                TableStructure.TableDetails.DATE_OF_VACCINATION};


    public Cursor getworkplan(String anm_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

        String str1 = "SELECT  DISTINCT NRP_child_master.rch_mother_id, NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.resident_facility_id,\n" +
                "                                               child_dob,max(date_of_vaccination) as LVD, child_name_hi,child_name_tm from NRP_child_master join \n" +
                "                                             NRP_anm_master \n" +
                "                                              join \n" +
                "                                              NRP_child_immunization_fact_table join NRP_vaccine_master on NRP_child_master.resident_facility_id = NRP_anm_master.anm_address\n" +
                "                                              and \n" +
                "                                               NRP_child_immunization_fact_table.rch_child_id = NRP_child_master.rch_child_id \n" +
                "                                            where \n" +
                "                                             NRP_child_immunization_fact_table.date_of_vaccination<>date('now') \n" +
                "\t\t\t\t\t\t\t\t\t          AND\n" +
                "                                              NRP_anm_master.anm_id = '" + anm_id + "' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t  GROUP BY  \n" +
                "                                            NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 HAVING\n" +
                "                                       date(max(date_of_vaccination),'+42 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                "                                              AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V5')\n" +
                "                              \n" +
                "                            OR    date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V5')\n" +
                "\t\t\t\t\t\t\t   \n" +
                "\t\t\t\t\t\t\t   OR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V5')\n" +
                "                 \n" +
                "                                     AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42')\n" +
                "                             OR \n" +
                "\t\t\t\t\t\t\t  date(max(date_of_vaccination),'+42 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                "                                              AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V6')\n" +
                "                              \n" +
                "                            OR    date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V6')\n" +
                "\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V6')";


        String str2 = " AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42')\n" +
                "\t\t\t\t\t\t    OR \n" +
                "\t\t\t\t\t\t\t date(max(date_of_vaccination),'+42 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                "                                              AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V7')\n" +
                "                              \n" +
                "                            OR    date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V7') \n" +
                "\t\t\t\t\t\t\t   \n" +
                "\t\t\t\t\t\t\t   OR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V7')\n" +
                "                 \n" +
                "                                     AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42')\n" +
                "\t\t\t\t\t\t\t  OR \n" +
                "\t\t\t\t\t\t\tdate(max(date_of_vaccination),'+42 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                "                                              AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V8')\n" +
                "                              \n" +
                "                            OR    date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t   NRP_child_immunization_fact_table.vaccine_id = 'V8')\n" +
                "\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V8')\n" +
                "                             AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42')\n" +
                "\t\t\t\t\t\t\t   \n" +
                "\tOR\n" +
                "\t\t\t                            date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                "                                             AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t\t\t\t\t NRP_child_immunization_fact_table.vaccine_id = 'V9')\n" +
                "                                           OR\n" +
                "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V9')\n" +
                "\t\t\t\t\t\t\t\t\t\tOR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V9')\n" +
                "                                AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' ))\n" +
                "                               OR\n" +
                "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                "                                             AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t\t\t\t\t NRP_child_immunization_fact_table.vaccine_id = 'V10')\n" +
                "                                           OR\n" +
                "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V10')\n" +
                "\t\t\t\t\t\t\t\t\t\tOR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V10')\n" +
                "                             AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' ))\n" +
                "\t\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t     date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                "                                             AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t\t\t\t\t NRP_child_immunization_fact_table.vaccine_id = 'V11')\n" +
                "                                           OR\n" +
                "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V11')\n" +
                "\t\t\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V11')\n" +
                "                                    AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'";


        String str3 = "AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' ))\n" +
                "                               OR\n" +
                "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') \n" +
                "                                             AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t\t\t\t\t NRP_child_immunization_fact_table.vaccine_id = 'V12')\n" +
                "                                           OR\n" +
                "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "\t\t\t\t\t\t\t\t\t\t\t  NRP_child_immunization_fact_table.vaccine_id = 'V12')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V12')\n" +
                "                               AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' ))\n" +
                "\tOR\n" +
                "                               (NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                 WHERE  when_to_use_days = '42' \n" +
                "                                                AND  NRP_child_immunization_fact_table.rch_child_id IN( \n" +
                "                                                SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                "                                               JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                "                                                AND\n" +
                "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 70 AND\n" +
                "                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V13'))\n" +
                "                                             OR\n" +
                "                                  (NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                              FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE  when_to_use_days = '42' \n" +
                "                                               AND \n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(\n" +
                "                                               SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                "                                                JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                "                                                AND\n" +
                "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 70 AND\n" +
                "                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V13')) \n" +
                "\t\t\t\t\t\t\t\t\tOR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-27 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 70 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V13')\n" +
                "                                   AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70'  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98')))\n" +
                "                                  OR\n" +
                "\t\t\t\t\t\t\t   (NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                 WHERE  when_to_use_days = '42' \n" +
                "                                                AND  NRP_child_immunization_fact_table.rch_child_id IN( \n" +
                "                                                SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                "                                               JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                "                                                AND\n" +
                "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 70 AND\n" +
                "                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V17'))\n" +
                "                                            OR\n" +
                "\t\t\t\t\t\t\t\t\t (NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                              FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE  when_to_use_days = '42' \n" +
                "                                               AND \n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(\n" +
                "                                               SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                "                                                JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                "                                                AND\n" +
                "                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 70 AND\n" +
                "                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V17')) \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-27 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 70 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V17')";

        String str4 = "AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70'  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98')))\n" +
                "          OR\n" +
                "\t\t\t\t\tdate(max(date_of_vaccination),'+172 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V14')\n" +
                "                  AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                "\t\t\t\t\t\t\t\t OR\n" +
                "                    date(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V14' )\n" +
                "                              AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                "\t\t\t\t\t\t  OR\n" +
                "\t\t\t\t\t\t   max(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V14')\n" +
                "                        AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' ))))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "                       OR\n" +
                "\t\t\t\t\t\t\t date(max(date_of_vaccination),'+172 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V15')\n" +
                "                      AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                "\t\t\t\t\t\t\t\t OR\n" +
                "                                  date(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V15' )\n" +
                "                                AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                "\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t\tmax(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V15')\n" +
                "                 \n" +
                "                                     AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'";


        String str5 = "AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' ))))\n" +
                "\t\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t\tdate(max(date_of_vaccination),'+172 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V16')\n" +
                "                 \n" +
                "                                     AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "                                OR\n" +
                "                   date(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V16' )\n" +
                "                 \n" +
                "                                           AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\t\t\t\t\t\t\t\tOR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V16')\n" +
                "                 \n" +
                "                                     AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' ))))\n" +
                "\t\t\t  OR\n" +
                "\t\t\t\t\t\t\t\tdate(max(date_of_vaccination),'+210 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V18')";

        String str6 = "AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "\t\t\t\t\t\t\t\t\t OR\n" +
                "                                date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V18')\n" +
                "\t\t\t\t\t\t\t\t\t\t AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V18')\n" +
                "                 \n" +
                "                                     AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "                               OR\n" +
                "\t\t\t\t\t\t\t  date(max(date_of_vaccination),'+210 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V19')\n" +
                "\t\t\t\t\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t\t\t\t\t AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "\t\t\t\t\t\t\t\t OR\n" +
                "                                date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V19')\n" +
                "\t\t\t\t\t\t\t\t\t\t AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "\t\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t  max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V19')\n" +
                "                 \n" +
                "                                     AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                "\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t  date(max(date_of_vaccination),'+210 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V20')\n" +
                "\t\t\t\t\t\t\t\t\t\t AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "\t\t\t\t\t\t\t\t OR\n" +
                "                                date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V20')\n" +
                "\t\t\t\t\t\t\t\t\t\t AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "\t\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V20')\n" +
                "                 \n" +
                "                                     AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                "\t\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t   date(max(date_of_vaccination),'+210 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V3')\n" +
                "\t\t\t\t\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t\t\t\t\t AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "\t\t\t\t\t\t\t\t\tOR\n" +
                "                                date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V3')\n" +
                "\t\t\t\t\t\t\t\t\t\t AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t  AND \n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "\t\t\t\t\t\t\t\t\t OR\n" +
                "\t\t\t\t\t\t\t\t\t\t\t max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date('now')\n" +
                "                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V3')\n" +
                "                                     AND\n" +
                "                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                             AND\n" +
                "                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                  AND\n" +
                "                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                "\t\tOR\n" +
                "\t\t\t\t\t\t\t  \n" +
                "\t\t\t\t\t\t\t      date(max(date_of_vaccination),'+240 DAY') BETWEEN date('now') AND date(date('now'),'" + DateValue() + " DAY') AND julianday('now') - julianday(child_dob) > 486\n" +
                "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                  on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V21')\n" +
                "                                OR\n" +
                "                                 date(max(date_of_vaccination),'+240 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 486\n" +
                "                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                  on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V21') ORDER BY NRP_child_immunization_fact_table.rch_child_id";

        cursor = sqLiteDatabase.rawQuery(str1 + str2 + str3 + str4 + str5 + str6, null);

        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }


    public Cursor VaccineWiseDue(String vaccine, String anm_id) {

        String[] grp1 = {"V1", "V2", "V4"};//0
        String[] grp2 = {"V5", "V6", "V7", "V8"};//42
        String[] grp3 = {"V9", "V10", "V11", "V12"};//70
        String[] grp4 = {"V13", "V17"};//98
        String[] grp5 = {"V14", "V15", "V16"};//270
        String[] grp6 = {"V3", "V18", "V19", "V20"};//480
        String[] grp7 = {"V21"};//720

        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        if (Arrays.asList(grp1).contains(vaccine)) {
            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.rch_mother_id,\n" +
                    "child_dob,\n" +
                    "NRP_child_master.resident_facility_id,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_immunization_fact_table JOIN NRP_child_master JOIN NRP_anm_master ON\n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE NRP_child_immunization_fact_table.rch_child_id NOT IN (\n" +
                    "select\n" +
                    "DISTINCT\n" +
                    "NRP_child_immunization_fact_table.rch_child_id  from \n" +
                    "NRP_child_master join NRP_child_immunization_fact_table \n" +
                    "join NRP_anm_master\n" +
                    "on \n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE \n" +
                    "NRP_child_immunization_fact_table.rch_child_id IN(select rch_child_id FROM NRP_child_immunization_fact_table where vaccine_id = '" + vaccine + "') AND \n" +
                    "NRP_anm_master.anm_id = " + anm_id + ") And NRP_anm_master.anm_id = " + anm_id + "\n" +
                    "AND date(child_dob,'+7 DAY') BETWEEN date(date('now'),'-1825 DAY') AND date(date('now'),'-1 day') GROUP BY NRP_child_immunization_fact_table.rch_child_id ORDER BY NRP_child_immunization_fact_table.rch_child_id", null);
            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        } else if (Arrays.asList(grp2).contains(vaccine)) {
            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.rch_mother_id,\n" +
                    "child_dob,\n" +
                    "NRP_child_master.resident_facility_id,max(date_of_vaccination)as LVD,child_name_hi,child_name_tm from NRP_child_immunization_fact_table JOIN NRP_child_master JOIN NRP_anm_master ON\n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE NRP_child_immunization_fact_table.rch_child_id NOT IN (\n" +
                    "select\n" +
                    "DISTINCT\n" +
                    "NRP_child_immunization_fact_table.rch_child_id  from \n" +
                    "NRP_child_master join NRP_child_immunization_fact_table \n" +
                    "join NRP_anm_master\n" +
                    "on \n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE \n" +
                    "NRP_child_immunization_fact_table.rch_child_id IN(select rch_child_id FROM NRP_child_immunization_fact_table where vaccine_id = '" + vaccine + "') AND \n" +
                    "NRP_anm_master.anm_id = " + anm_id + ") And NRP_anm_master.anm_id = " + anm_id + "\n" +
                    "AND date(child_dob,'+42 DAY') BETWEEN date(date('now'),'-1825 DAY') AND date(date('now'),'-1 day') GROUP BY NRP_child_immunization_fact_table.rch_child_id ORDER BY NRP_child_immunization_fact_table.rch_child_id", null);
            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        } else if (Arrays.asList(grp3).contains(vaccine)) {
            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.rch_mother_id,\n" +
                    "child_dob,\n" +
                    "NRP_child_master.resident_facility_id,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_immunization_fact_table JOIN NRP_child_master JOIN NRP_anm_master ON\n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE NRP_child_immunization_fact_table.rch_child_id NOT IN (\n" +
                    "select\n" +
                    "DISTINCT\n" +
                    "NRP_child_immunization_fact_table.rch_child_id  from \n" +
                    "NRP_child_master join NRP_child_immunization_fact_table \n" +
                    "join NRP_anm_master\n" +
                    "on \n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE \n" +
                    "NRP_child_immunization_fact_table.rch_child_id IN(select rch_child_id FROM NRP_child_immunization_fact_table where vaccine_id = '" + vaccine + "') AND \n" +
                    "NRP_anm_master.anm_id = " + anm_id + ") And NRP_anm_master.anm_id = " + anm_id + "\n" +
                    "AND date(child_dob,'+70 DAY') BETWEEN date(date('now'),'-1825 DAY') AND date(date('now'),'-1 day') GROUP BY NRP_child_immunization_fact_table.rch_child_id ORDER BY NRP_child_immunization_fact_table.rch_child_id", null);
            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        } else if (Arrays.asList(grp4).contains(vaccine)) {
            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.rch_mother_id,\n" +
                    "child_dob,\n" +
                    "NRP_child_master.resident_facility_id,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_immunization_fact_table JOIN NRP_child_master JOIN NRP_anm_master ON\n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE NRP_child_immunization_fact_table.rch_child_id NOT IN (\n" +
                    "select\n" +
                    "DISTINCT\n" +
                    "NRP_child_immunization_fact_table.rch_child_id  from \n" +
                    "NRP_child_master join NRP_child_immunization_fact_table \n" +
                    "join NRP_anm_master\n" +
                    "on \n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE \n" +
                    "NRP_child_immunization_fact_table.rch_child_id IN(select rch_child_id FROM NRP_child_immunization_fact_table where vaccine_id = '" + vaccine + "') AND \n" +
                    "NRP_anm_master.anm_id = " + anm_id + ") And NRP_anm_master.anm_id = " + anm_id + "\n" +
                    "AND date(child_dob,'+98 DAY') BETWEEN date(date('now'),'-1825 DAY') AND date(date('now'),'-1 day') GROUP BY NRP_child_immunization_fact_table.rch_child_id ORDER BY NRP_child_immunization_fact_table.rch_child_id", null);
            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        } else if (Arrays.asList(grp5).contains(vaccine)) {
            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.rch_mother_id,\n" +
                    "child_dob,\n" +
                    "NRP_child_master.resident_facility_id,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_immunization_fact_table JOIN NRP_child_master JOIN NRP_anm_master ON\n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE NRP_child_immunization_fact_table.rch_child_id NOT IN (\n" +
                    "select\n" +
                    "DISTINCT\n" +
                    "NRP_child_immunization_fact_table.rch_child_id  from \n" +
                    "NRP_child_master join NRP_child_immunization_fact_table \n" +
                    "join NRP_anm_master\n" +
                    "on \n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE \n" +
                    "NRP_child_immunization_fact_table.rch_child_id IN(select rch_child_id FROM NRP_child_immunization_fact_table where vaccine_id = '" + vaccine + "') AND \n" +
                    "NRP_anm_master.anm_id = " + anm_id + ") And NRP_anm_master.anm_id = " + anm_id + "\n" +
                    "AND date(child_dob,'+270 DAY') BETWEEN date(date('now'),'-1825 DAY') AND date(date('now'),'-1 day') GROUP BY NRP_child_immunization_fact_table.rch_child_id ORDER BY NRP_child_immunization_fact_table.rch_child_id", null);
            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        } else if (Arrays.asList(grp6).contains(vaccine)) {
            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.rch_mother_id,\n" +
                    "child_dob,\n" +
                    "NRP_child_master.resident_facility_id,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_immunization_fact_table JOIN NRP_child_master JOIN NRP_anm_master ON\n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE NRP_child_immunization_fact_table.rch_child_id NOT IN (\n" +
                    "select\n" +
                    "DISTINCT\n" +
                    "NRP_child_immunization_fact_table.rch_child_id  from \n" +
                    "NRP_child_master join NRP_child_immunization_fact_table \n" +
                    "join NRP_anm_master\n" +
                    "on \n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE \n" +
                    "NRP_child_immunization_fact_table.rch_child_id IN(select rch_child_id FROM NRP_child_immunization_fact_table where vaccine_id = '" + vaccine + "') AND \n" +
                    "NRP_anm_master.anm_id = " + anm_id + ") And NRP_anm_master.anm_id = " + anm_id + "\n" +
                    "AND date(child_dob,'+480 DAY') BETWEEN date(date('now'),'-1825 DAY') AND date(date('now'),'-1 day') GROUP BY NRP_child_immunization_fact_table.rch_child_id ORDER BY NRP_child_immunization_fact_table.rch_child_id", null);
            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        } else if (Arrays.asList(grp7).contains(vaccine)) {
            cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id,NRP_child_master.child_name,NRP_child_master.rch_mother_id,\n" +
                    "child_dob,\n" +
                    "NRP_child_master.resident_facility_id,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from NRP_child_immunization_fact_table JOIN NRP_child_master JOIN NRP_anm_master ON\n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE NRP_child_immunization_fact_table.rch_child_id NOT IN (\n" +
                    "select\n" +
                    "DISTINCT\n" +
                    "NRP_child_immunization_fact_table.rch_child_id  from \n" +
                    "NRP_child_master join NRP_child_immunization_fact_table \n" +
                    "join NRP_anm_master\n" +
                    "on \n" +
                    "NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                    "AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                    "WHERE \n" +
                    "NRP_child_immunization_fact_table.rch_child_id IN(select rch_child_id FROM NRP_child_immunization_fact_table where vaccine_id = '" + vaccine + "') AND \n" +
                    "NRP_anm_master.anm_id = " + anm_id + ") And NRP_anm_master.anm_id = " + anm_id + "\n" +
                    "AND date(child_dob,'+720 DAY') BETWEEN date(date('now'),'-1825 DAY') AND date(date('now'),'-1 day') GROUP BY NRP_child_immunization_fact_table.rch_child_id ORDER BY NRP_child_immunization_fact_table.rch_child_id", null);
            if (cursor != null) {
                return cursor;
            } else {
                return null;
            }
        }


        return null;
    }

    public Cursor dateWiseDue(String anm_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

        String part1 = "select distinct NRP_child_master.rch_mother_id,NRP_child_immunization_fact_table.rch_child_id,\n" +
                "                                NRP_child_master.child_name,NRP_child_master.resident_facility_id,child_dob,max(date_of_vaccination) as LVD,child_name_hi,child_name_tm from \n" +
                "                                NRP_child_master join NRP_child_immunization_fact_table \n" +
                "                                join NRP_anm_master\n" +
                "                                on \n" +
                "                                NRP_child_master.resident_facility_id = NRP_anm_master.anm_address \n" +
                "                                AND NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                WHERE \n" +
                "                                NRP_child_immunization_fact_table.date_of_vaccination<>date('now') \n" +
                "                          AND\n" +
                "                                                              NRP_anm_master.anm_id = '" + anm_id + "' \n" +
                "                  GROUP BY  \n" +
                "                                                             NRP_child_immunization_fact_table.rch_child_id\n" +
                "                  HAVING\n" +
                "                 date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 day') \n" +
                "                                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "                   NRP_child_immunization_fact_table.vaccine_id = 'V5')\n" +
                "                   \n" +
                "                   OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 day')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V5')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  OR\n" +
                "                \n" +
                "                 date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 day') \n" +
                "                                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "                   NRP_child_immunization_fact_table.vaccine_id = 'V6')\n" +
                "                   \n" +
                "                   OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 day')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V6')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  OR \n" +
                "                \n" +
                "                 date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 day') \n" +
                "                                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "                   NRP_child_immunization_fact_table.vaccine_id = 'V7') \n" +
                "                   \n" +
                "                   OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 day')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V7')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  OR \n" +
                "                   \n" +
                "                   date(max(date_of_vaccination),'+42 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 day') \n" +
                "                                                             AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "                   NRP_child_immunization_fact_table.vaccine_id = 'V8')\n" +
                "                   \n" +
                "                  OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 day')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V8')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42')\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   OR\n" +
                "                \n" +
                "                                                   date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "                  NRP_child_immunization_fact_table.vaccine_id = 'V9')\n" +
                "\t\t\t\t                         AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id  NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' ))\n" +
                "                  \n" +
                "                OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V9')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' ))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tOR\n" +
                "                   \n" +
                "                   date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "                  NRP_child_immunization_fact_table.vaccine_id = 'V10')\n" +
                "\t\t\t\t                                                AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' ))\n" +
                "                  \n" +
                "                   OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V10')\n" +
                "                                 \n" +
                "                                                       AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' ))";

        String part2 = "OR\n" +
                "                   \n" +
                "                   date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "                  NRP_child_immunization_fact_table.vaccine_id = 'V11')\n" +
                "\t\t\t\t                        AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' ))\n" +
                "                  \n" +
                "                   OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V11')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' ))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tOR\n" +
                "                   \n" +
                "                   date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') \n" +
                "                                                              AND julianday('now') - julianday(child_dob) > 42 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE\n" +
                "                  NRP_child_immunization_fact_table.vaccine_id = 'V12') AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' ))\n" +
                "                  \n" +
                "                 OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-20 day') AND date('now')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V12')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' ))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t OR\n" +
                "                  \n" +
                "                                               (NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                              FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE  when_to_use_days = '42' \n" +
                "                                                               AND \n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(\n" +
                "                                                               SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                "                                                                JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                "                                                                AND\n" +
                "                                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 70 AND\n" +
                "                                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V13')) \n" +
                "                 \n" +
                "                OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-27 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V13')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70'  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98')))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  OR\n" +
                "                \n" +
                "                (NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                              FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE  when_to_use_days = '42' \n" +
                "                                                               AND \n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(\n" +
                "                                                               SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id FROM NRP_child_immunization_fact_table \n" +
                "                                                                JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE  when_to_use_days = '70'))\n" +
                "                                                                AND\n" +
                "                                                                date(max(date_of_vaccination),'+28 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 70 AND\n" +
                "                                                                 NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V17')) \n" +
                "                 \n" +
                "                \n" +
                "                 OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-27 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V17')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70'  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98')))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  OR\n" +
                "                 \n" +
                "                                          (date(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY'))\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V14' )\n" +
                "                                 \n" +
                "                                                           AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                "                  \n" +
                "                                           OR\n" +
                "                   \n" +
                "                   max(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V14')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "                 AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' ))))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  OR\n" +
                "                  \n" +
                "                   date(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V15' )\n" +
                "                                 \n" +
                "                                                           AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                "                  \n" +
                "                                                OR\n" +
                "                \n" +
                "                \n" +
                "                \n" +
                "                 max(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V15')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "                 AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' ))))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  OR\n" +
                "                \n" +
                "                date(max(date_of_vaccination),'+172 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id \n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V16' )\n" +
                "                                 \n" +
                "                                                           AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' )))\n" +
                "                  \n" +
                "                OR\n" +
                "                  \n" +
                "                   max(date_of_vaccination) BETWEEN date(date('now'),'-28 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V16')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "                 AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' ))))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   OR\n" +
                "                 \n" +
                "                                            date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V18')\n" +
                "                 AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "                  AND \n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "                  \n" +
                "                 OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V18')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "                 AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                "                   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  OR\n" +
                "                  \n" +
                "                   date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V19')\n" +
                "                 AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "                  AND \n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "                  \n" +
                "                 OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V19')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "                 AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                "                   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  OR\n" +
                "                \n" +
                "                date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V20')\n" +
                "                 AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "                  AND \n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "                  \n" +
                "                 OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V20')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "                 AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                "                   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  \n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t   OR\n" +
                "                  \n" +
                "                   date(max(date_of_vaccination),'+210 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 273 \n" +
                "                                                                 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                                 on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V3')\n" +
                "                 AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98' \n" +
                "                  AND \n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273'))))\n" +
                "                  \n" +
                "                 OR\n" +
                "                  \n" +
                "                  max(date_of_vaccination) BETWEEN date(date('now'),'-60 day') AND date(date('now'),'-1 DAY')\n" +
                "                                                                AND julianday('now') - julianday(child_dob) > 98 AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                 from NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id\n" +
                "                                                                 WHERE NRP_child_immunization_fact_table.vaccine_id = 'V3')\n" +
                "                                 \n" +
                "                                                     AND\n" +
                "                                                                      NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id\n" +
                "                                                                  WHERE   when_to_use_days = '42'\n" +
                "                                                                             AND\n" +
                "                                                                NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id\n" +
                "                                                                FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id \n" +
                "                                                                WHERE   when_to_use_days = '70' \n" +
                "                                                                                                                  AND\n" +
                "                                                                                                                  NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '98'\n" +
                "                 AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '273' \n" +
                "                   AND NRP_child_immunization_fact_table.rch_child_id IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id \n" +
                "                                                                  FROM NRP_child_immunization_fact_table JOIN NRP_vaccine_master on NRP_child_immunization_fact_table.vaccine_id = NRP_vaccine_master.vaccine_id WHERE   when_to_use_days = '486')))))\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t  OR\n" +
                "                   \n" +
                "                                             date(max(date_of_vaccination),'+240 DAY') BETWEEN date(date('now'),'-1825 day') AND date(date('now'),'-1 DAY') AND julianday('now') - julianday(child_dob) > 486\n" +
                "                                                                  AND NRP_child_immunization_fact_table.rch_child_id NOT IN(SELECT DISTINCT NRP_child_immunization_fact_table.rch_child_id from NRP_child_immunization_fact_table JOIN NRP_vaccine_master \n" +
                "                                                                  on NRP_vaccine_master.vaccine_id = NRP_child_immunization_fact_table.vaccine_id WHERE NRP_child_immunization_fact_table.vaccine_id = 'V21') ORDER BY NRP_child_immunization_fact_table.rch_child_id ";

        cursor = sqLiteDatabase.rawQuery(part1 + part2, null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor getFacilityInfo(String anm_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT block,phc,sc,village,NRP_facility_master.facility_id from NRP_facility_master JOIN NRP_anm_facility_mapping_fact_table on NRP_anm_facility_mapping_fact_table.facility_id = NRP_facility_master.facility_id\n" +
                "WHERE NRP_anm_facility_mapping_fact_table.anm_id = " + anm_id, null);

        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }

    }

    public Cursor getBarCodeValue(String scanned_value) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("select NRP_mother_master1.rch_mother_id,mother_name,rch_child_id,child_name,resident_facility_id,NRP_child_master.child_dob,NRP_mother_master1.mother_name_hi,NRP_mother_master1.mother_name_tm,NRP_child_master.child_name_hi," +
                "NRP_child_master.child_name_tm" +
                " from NRP_mother_master1 join NRP_child_master on \n" +
                "                NRP_mother_master1.rch_mother_id = NRP_child_master.rch_mother_id where NRP_child_master.rch_child_id = '" + scanned_value + "' ", null);

        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }

    }

    public Cursor VaccineScan(String scan_result) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("select vaccine_id,vaccine_name,vaccine_dosage from NRP_vaccine_master where vaccine_id = '" + scan_result + "'", null);

        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor VaccineBarcode(String scan_result) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("select NRP_vaccine_master.vaccine_id,NRP_vaccines_barcode.vaccine_name,vaccine_dosage,expiry_date,vaccination_date," +
                "manufacturing_date,manufacturer,batch_no \n" +
                " from NRP_vaccine_master JOIN NRP_vaccines_barcode  on NRP_vaccine_master.vaccine_id = NRP_vaccines_barcode.vaccine_id \n" +
                " WHERE NRP_vaccines_barcode.barcode_no = '" + scan_result + "'", null);

        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor getLVD(String Child_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT max(date_of_vaccination) from NRP_child_immunization_fact_table WHERE rch_child_id = '" + Child_id + "'", null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }

    }

}
