package com.example.unisicma;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SQLiteInsert {
    Context context;
    String formattedDate;

    private SQLiteOpenHelper openHelper;
    private static SQLiteInsert sqLiteInsert;
    private SQLiteDatabase database;
    Cursor cursor;

    private SQLiteInsert(Context context) {
        this.openHelper = new DatabaseAccess(context);
    }

    public static SQLiteInsert getInstance(Context context) {
        if (sqLiteInsert == null) {
            sqLiteInsert = new SQLiteInsert(context);
        }
        return sqLiteInsert;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public String DeleteData() {
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();
        //sqLiteDatabase.execSQL("delete from " + TableStructure.TableDetails.TABLE1);
        //sqLiteDatabase.execSQL("delete from NRP_child_immunization_fact_table where rch_child_id = '001'");
        // sqLiteDatabase.execSQL("update NRP_child_master set child_dob = '2020-05-22' where rch_child_id = '001'");
        // sqLiteDatabase.execSQL("update NRP_child_immunization_fact_table set date_of_vaccination = '2020-05-22' where rch_child_id = '001'");
        // sqLiteDatabase.execSQL("update NRP_child_immunization_fact_table set date_of_vaccination = '2020-05-22' where rch_child_id = '001'");
        // sqLiteDatabase.execSQL("update NRP_child_immunization_fact_table set date_of_vaccination = '2020-05-22' where rch_child_id = '001'");

         sqLiteDatabase.execSQL("delete from " + TableStructure.TableDetails.CHILD_MASTER);

        sqLiteDatabase.execSQL("delete from " + TableStructure.TableDetails.MOTHER_MASTER);

        // sqLiteDatabase.execSQL("delete from " + TableStructure.TableDetails.FACILITY_MASTER);

        //sqLiteDatabase.execSQL("delete from " + TableStructure.TableDetails.ANM_MASTER);

       // sqLiteDatabase.execSQL("delete from " + TableStructure.TableDetails.ANM_MAPPING);


        return "";
    }


    public boolean insertChild_vaccination(String child_id, String vaccine_id, String anm_id,
                                           String facility_id, String weight_at_vaccination, String date_of_vaccination, String barcode) {
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableStructure.TableDetails.COL1, child_id);
        contentValues.put(TableStructure.TableDetails.COL2, vaccine_id);
        contentValues.put(TableStructure.TableDetails.COL3, anm_id);
        contentValues.put(TableStructure.TableDetails.COL4, "Not Assigned");
        contentValues.put(TableStructure.TableDetails.COL5, facility_id);
        contentValues.put(TableStructure.TableDetails.COL6, weight_at_vaccination);
        contentValues.put(TableStructure.TableDetails.COL8, barcode);
        //contentValues.put(Count, count);

       /* Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date_of_vaccination = "";
        date_of_vaccination = dateFormat.format(calendar.getTime());

        */

        contentValues.put(TableStructure.TableDetails.COL7, DateFormation(date_of_vaccination));

        sqLiteDatabase.insert(TableStructure.TableDetails.TABLE1, null, contentValues);
        Log.d("Database Operations", "Child Immunization Table Synced Successfully.!");


        return true;
    }


    public boolean insertChild_master(String child_id, String child_name, String mother_id,
                                      String enroll_date, String child_sex, String child_dob,
                                      String birth_weight, String del_facility_id, String res_facility_id, String child_name_hi, String child_name_tn) {
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableStructure.TableDetails.COL1, child_id);
        contentValues.put(TableStructure.TableDetails.CHILD_NAME, child_name);
        contentValues.put(TableStructure.TableDetails.MOTHER_ID, mother_id);
        contentValues.put(TableStructure.TableDetails.EN_DATE, enroll_date);
        contentValues.put(TableStructure.TableDetails.CHILD_SEX, child_sex);
        contentValues.put(TableStructure.TableDetails.CHILD_DOB, "2020-05-22");
        contentValues.put(TableStructure.TableDetails.BIRTH_WEIGHT, birth_weight);
        contentValues.put(TableStructure.TableDetails.D_FACILITY_ID, del_facility_id);
        contentValues.put(TableStructure.TableDetails.R_FACILITY_ID, res_facility_id);
        contentValues.put(TableStructure.TableDetails.CHILD_NAME_HI, child_name_hi);
        contentValues.put(TableStructure.TableDetails.CHILD_NAME_TN, child_name_tn);
        contentValues.put("Bio_ID", "NA");
        //contentValues.put(Count, count);


        sqLiteDatabase.insert(TableStructure.TableDetails.CHILD_MASTER, null, contentValues);
        Log.d("Database Operations", "Child Immunization Table Synced Successfully.!");


        return true;
    }


    public boolean insertMother_master(String mother_id,
                                       String mother_name, String mother_mobile, String father_name,
                                       String father_mobile) {
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableStructure.TableDetails.MOTHER_ID, mother_id);
        contentValues.put(TableStructure.TableDetails.MOTHER_NAME, mother_name);
        contentValues.put(TableStructure.TableDetails.FATHER_NAME, father_name);
        contentValues.put(TableStructure.TableDetails.M_MOB, mother_mobile);
        contentValues.put(TableStructure.TableDetails.F_MOB, father_mobile);
        contentValues.put(TableStructure.TableDetails.AGE_REGN, "Not Available");
        contentValues.put("mother_education", "Not Available");
        contentValues.put("husband_education", "Not Available");
        contentValues.put("husband_occupation", "Not Available");
        contentValues.put("mother_occupation", "Not Available");
        contentValues.put("family_annual_income", "Not Available");
        contentValues.put("no_of_children", "Not Available");
        contentValues.put("mother_dob", "NA");
        contentValues.put("aadhar_number", "NA");
        contentValues.put("scheme_name", "NA");
        contentValues.put("religion", "NA");
        contentValues.put("ration_number", "NA");
        contentValues.put("caste", "NA");


        //contentValues.put(Count, count);


        sqLiteDatabase.insert(TableStructure.TableDetails.MOTHER_MASTER, null, contentValues);
        Log.d("Database Operations", "Mother Master Table Synced Successfully.!");


        return true;
    }

    public boolean insertFacility_master(String facility_id,
                                         String facility_name, String facility_type, String state,
                                         String district, String block, String phc, String sc, String village) {
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableStructure.TableDetails.FACILITY, facility_id);
        contentValues.put(TableStructure.TableDetails.FAC_NAME, facility_name);
        contentValues.put(TableStructure.TableDetails.F_TYPE, facility_type);
        contentValues.put(TableStructure.TableDetails.DISTRICT, district);
        contentValues.put(TableStructure.TableDetails.BLOCK, block);
        contentValues.put(TableStructure.TableDetails.PHC, phc);
        contentValues.put(TableStructure.TableDetails.SC, sc);
        contentValues.put(TableStructure.TableDetails.STATE, state);
        contentValues.put(TableStructure.TableDetails.VILLAGE, village);

        //contentValues.put(Count, count);


        sqLiteDatabase.insert(TableStructure.TableDetails.FACILITY_MASTER, null, contentValues);
        Log.d("Database Operations", "Mother master Table Synced Successfully.!");


        return true;
    }


    public boolean insertANM_master(String anm_id,
                                    String anm_name, String anm_dob, String anm_mobile,
                                    String anm_address) {
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableStructure.TableDetails.ANM_ID, anm_id);
        contentValues.put(TableStructure.TableDetails.ANM_NAME, anm_name);
        contentValues.put(TableStructure.TableDetails.ANM_DOB, anm_dob);
        contentValues.put(TableStructure.TableDetails.ANM_MOB, anm_mobile);
        contentValues.put(TableStructure.TableDetails.ANM_ADDRESS, anm_address);


        //contentValues.put(Count, count);


        sqLiteDatabase.insert(TableStructure.TableDetails.ANM_MASTER, null, contentValues);
        Log.d("Database Operations", "ANM Master Table Synced Successfully.!");


        return true;
    }

    public boolean insertVaccine_barcode(String v_id, String v_name, String barcode, String batch_no, String expiry_date, String manufacturing_date, String vaccination_date, String manufacturer) {
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableStructure.TableDetails.V_ID, v_id);
        contentValues.put(TableStructure.TableDetails.V_NAME, v_name);
        contentValues.put(TableStructure.TableDetails.BARCODE, barcode);
        contentValues.put(TableStructure.TableDetails.BATCH_NO, batch_no);
        contentValues.put(TableStructure.TableDetails.EX_DATE, expiry_date);
        contentValues.put(TableStructure.TableDetails.MAN_DATE, manufacturing_date);
        contentValues.put(TableStructure.TableDetails.VAC_DATE, vaccination_date);
        contentValues.put(TableStructure.TableDetails.MAN, manufacturer);
        //contentValues.put(Count, count);


        sqLiteDatabase.insert(TableStructure.TableDetails.VACCINE_BARCODE, null, contentValues);
        Log.d("Database Operations", "Vaccine Master Table Synced Successfully.!");


        return true;
    }

    public boolean insertAdmin_master() {
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("asha_id", "ADM0001");
        contentValues.put("asha_name", "admin");
        contentValues.put("asha_dob", "1970-01-01");
        contentValues.put("asha_mobile", "8888899990");
        contentValues.put("asha_address", "Hamirpur");

        long result = sqLiteDatabase.insert("NRP_asha_master", null, contentValues);
        return result != -1;

    }

    public boolean insertANM_mapping(String anm_id,
                                     String facility_id, String join_date) {
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableStructure.TableDetails.ANM_ID, anm_id);
        contentValues.put(TableStructure.TableDetails.FACILITY, facility_id);
        contentValues.put(TableStructure.TableDetails.JOIN_DATE, join_date);

        //contentValues.put(Count, count);


        sqLiteDatabase.insert(TableStructure.TableDetails.ANM_MAPPING, null, contentValues);
        Log.d("Database Operations", "ANM MAPPING Table Synced Successfully.!");


        return true;
    }

    public Cursor showVaccines() {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TableStructure.TableDetails.VACCINE_BARCODE,
                null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor getChildImmunization(String child_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT rch_child_id,vaccine_id,anm_id,date_of_vaccination,weight_at_vaccination," +
                        "facility_id,barcode_no from NRP_child_immunization_fact_table where rch_child_id ='" + child_id + "'\n",
                null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor getAllChildImmunization() {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT rch_child_id,vaccine_id,anm_id,date_of_vaccination,weight_at_vaccination," +
                        "facility_id,barcode_no from NRP_child_immunization_fact_table \n",
                null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor getChildCount() {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select count(*) from NRP_child_master",
                null);
        return cursor;
    }

    public String DateFormation(String input) {

        DateFormat simpledate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = simpledate.parse(input);
            DateFormat simpledatenew = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            formattedDate = simpledatenew.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return formattedDate;
    }

    public Cursor getSingleVaccine(String child_id, String vaccine_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT vaccine_id FROM NRP_child_immunization_fact_table WHERE rch_child_id = '" + child_id + "'  and vaccine_id = '" + vaccine_id + "'",
                null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }

    }

    public boolean updateVaccine() {
        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();
        return true;


    }

    public boolean LanguageInteface(String child_id, String ch_name_hi, String m_name_hi, String f_name_hi, String ch_name_tm, String f_name_tm, String m_name_tm) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

        /*

         sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'माहेश्वरी एफ',mother_name_tm = 'மகேஸ்வரி எஃப்',father_name_hi = 'Isanadi', father_name_tm = 'இசநாடி'\n" +
                "WHERE rch_mother_id = '111110014164'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'Saru',mother_name_tm = 'சாரு',father_name_hi = 'Chalanthalaiyan', father_name_tm = 'சலந்தலையன்'\n" +
                "WHERE rch_mother_id = '111110013553'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'किरुथिका डी',mother_name_tm = 'கிருத்திகா டி',father_name_hi = 'Sayampu', father_name_tm = 'சயம்பு'\n" +
                "WHERE rch_mother_id = '111110013890'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'Saru',mother_name_tm = 'சாரு',father_name_hi = 'Saranan', father_name_tm = 'சரணன்'\n" +
                "WHERE rch_mother_id = '111110013558'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'अनुषा जी',mother_name_tm = 'அனுஷ்ய ஜி',father_name_hi = 'हरि पिल्लै ई', father_name_tm = 'ஹரி பிள்ளை இ'\n" +
                "WHERE rch_mother_id = '111110013570'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'मंजू डी',mother_name_tm = 'மஞ்சு டி',father_name_hi = 'पद्मनाभन एस', father_name_tm = 'padhmanaban எஸ்'\n" +
                "WHERE rch_mother_id = '111110010545'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'किरुथिका डी',mother_name_tm = 'கிருத்திகா டி',father_name_hi = 'Iniyan', father_name_tm = 'இனியன்'\n" +
                "WHERE rch_mother_id = '111110013808'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'जानकी',mother_name_tm = 'ஜனகி',father_name_hi = 'Inbaniingan', father_name_tm = 'இன்பானிங்கன்'\n" +
                "WHERE rch_mother_id = '111110013592'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'विजाया य',mother_name_tm = 'விஜய ஒய்',father_name_hi = 'Chalanthalaiyan', father_name_tm = 'சலந்தலையன்'\n" +
                "WHERE rch_mother_id = '111110013218'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'Vishneshwari',mother_name_tm = 'விஷ்னேஸ்வரி',father_name_hi = 'Iraiyan', father_name_tm = 'இரையன்'\n" +
                "WHERE rch_mother_id = '111110013886'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'विजया के',mother_name_tm = 'விஜயா கே',father_name_hi = 'Araththurainathan', father_name_tm = 'ஆரத்துரைநாதன்'\n" +
                "WHERE rch_mother_id = '111110013086'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'पवनिता सुबाशिनी',mother_name_tm = 'வனிதா சுபாஷினி',father_name_hi = 'Saththan', father_name_tm = 'சாதன் '\n" +
                "WHERE rch_mother_id = '111110013054'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'श्री देवी एस',mother_name_tm = 'ஸ்ரீ தேவி எஸ்',father_name_hi = 'कमल एस', father_name_tm = 'கமல் எஸ்'\n" +
                "WHERE rch_mother_id = '111110013379'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'सरस्वती जी',mother_name_tm = 'சரஸ்வதி ஜி',father_name_hi = 'इराय', father_name_tm = 'இராய் '\n" +
                "WHERE rch_mother_id = '111110013511'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'प्रियंका वी',mother_name_tm = 'பிரியங்கா வி',father_name_hi = 'जानकीरामन', father_name_tm = 'ஜனகிராமன்'\n" +
                "WHERE rch_mother_id = '111110014521'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'भुवन श्री',mother_name_tm = 'புவனா ஸ்ரீ',father_name_hi = 'Saranan', father_name_tm = 'சரணன் '\n" +
                "WHERE rch_mother_id = '111110014114'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'वनिता सुबाशिनी',mother_name_tm = 'வனிதா சுபாஷினி',father_name_hi = 'Sikantan', father_name_tm = 'சிகந்தன்'\n" +
                "WHERE rch_mother_id = '111110013757'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'प्रेमा एम',mother_name_tm = 'பிரேமா எம்',father_name_hi = 'विनयगम एन', father_name_tm = 'விநாயகம் என் '\n" +
                "WHERE rch_mother_id = '111110013827'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'मालती एस',mother_name_tm = 'மாலதி எஸ்',father_name_hi = 'राज कुमार डी', father_name_tm = 'ராஜ்குமார் டி'\n" +
                "WHERE rch_mother_id = '111110014409'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'अनुषा जी',mother_name_tm = 'அனுஷ்ய ஜி',father_name_hi = 'हरि पिल्लै ई', father_name_tm = 'ஹரி பிள்ளை இ '\n" +
                "WHERE rch_mother_id = '111110013537'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'सुता S',mother_name_tm = 'சூதா எஸ்',father_name_hi = 'Sikantan', father_name_tm = 'சிகந்தன்'\n" +
                "WHERE rch_mother_id = '111110013723'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'सुब्बुलक्ष्मी डी',mother_name_tm = 'சுப்புலட்சுமி டி',father_name_hi = 'विचंद्रन सी', father_name_tm = 'சந்திரன் சி '\n" +
                "WHERE rch_mother_id = '111110013214'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'काजोल',mother_name_tm = 'கஜோல்',father_name_hi = 'Ayirchulan', father_name_tm = 'ஆயிர்ச்சுலன்'\n" +
                "WHERE rch_mother_id = '111110013810'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'मीरा माधहुश्री एल',mother_name_tm = 'மிர்ரா மதுஷ்ரீ எல்',father_name_hi = 'अरासु', father_name_tm = 'அராசு '\n" +
                "WHERE rch_mother_id = '111110013026'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'वनिता जी',mother_name_tm = 'வனிதா ஜி',father_name_hi = 'अजय कुमार डी', father_name_tm = 'அஜய் குமார் டி'\n" +
                "WHERE rch_mother_id = '111110013211'; ");
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = 'षणमुग प्रिया',mother_name_tm = 'சண்முகா பிரியா',father_name_hi = 'Araththurainathan', father_name_tm = 'ஆரத்துரைநாதன்'\n" +
                "WHERE rch_mother_id = '111110013335'; ");

         */

        /*
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='प्रेमा एम का बच्चा',child_name_tm = 'பிரேமா எம் குழந்தை' where rch_child_id = '888881002129'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='पशेनबागावल्ली का बच्चा',child_name_tm = 'ஷென்பகவல்லியின் குழந்தை' where rch_child_id = '888881002143'");


        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='मनोरंजनी का बच्चा',child_name_tm = 'மனோரஞ்சனியின் குழந்தை ஏ' where rch_child_id = '888881002142'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='विजाया का बच्चा',child_name_tm = 'விஜயாவின் குழந்தை' where rch_child_id = '888881002116'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='चिन्नपिल्लई का बच्चा',child_name_tm = 'சின்னபில்லையின் குழந்தை' where rch_child_id = '888881002119'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='मधेश्वरी एस का बच्चा',child_name_tm = 'மாதேஸ்வரி எஸ்' where rch_child_id = '888881002115'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='निथ्या एस एम का बच्चाा',child_name_tm = 'குழந்தை நித்யா எஸ் எம்' where rch_child_id = '888881002135'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='चित्रा का बच्चा',child_name_tm = 'சித்ராவின் குழந்தை' where rch_child_id = '888881002113'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='करुणा',child_name_tm = 'கருணா' where rch_child_id = '888881002106'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='भुवन श्री वाई का बच्चाा',child_name_tm = 'புவனாவின் குழந்தை ஸ்ரீ ஒய்' where rch_child_id = '888881002101'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='बेबी ऑफ देवीप्रिया एम',child_name_tm = 'தேவிப்ரியா எம்' where rch_child_id = '888881002100'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='दिव्या की बच्ची',child_name_tm = 'திவ்யாவின் குழந்தை டி' where rch_child_id = '888881002148'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='सरस्वती एच का बच्चा',child_name_tm = 'சரஸ்வதியின் குழந்தை எச்' where rch_child_id = '888881002102'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='काजोल का बच्चा',child_name_tm = 'கஜோலின் குழந்தை' where rch_child_id = '888881002124'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='पोन्नम्माल का बच्चा',child_name_tm = 'பொன்னம்மாலின் குழந்தை' where rch_child_id = '888881002128'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='पूर्णिमा का बच्चा',child_name_tm = 'பூர்ணிமாவின் குழந்தை எம்' where rch_child_id = '888881002146'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='Abhirami',child_name_tm = 'அபிராமி' where rch_child_id = '888881002140'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='गोमती का बच्चा',child_name_tm = 'கோமதியின் குழந்தை' where rch_child_id = '888881002144'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='सारू का बच्चा',child_name_tm = 'சாருவின் குழந்தை' where rch_child_id = '888881002127'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='चित्रा देवी जी का बच्चा',child_name_tm = 'சித்ரா தேவி ஜி' where rch_child_id = '888881002114'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='जानकी का बच्चा',child_name_tm = 'ஜானகியின் குழந்தை' where rch_child_id = '888881002141'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='किर्थिका डी का बच्चा',child_name_tm = 'கிருத்திகாவின் குழந்தை டி' where rch_child_id = '888881002139'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='सारु आर का बच्चा',child_name_tm = 'சாருவின் குழந்தை ஆர்' where rch_child_id = '888881002110'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='नन्थिनी का बच्चा',child_name_tm = 'நந்தினியின் குழந்தை' where rch_child_id = '888881002138'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='देवप्रिया एस का बच्चा',child_name_tm = 'தேவிப்ரியா எஸ்' where rch_child_id = '888881000025'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='माहेश्वरी एफ का बच्चा',child_name_tm = 'மகேஸ்வரி எஃப்' where rch_child_id = '888881002137'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='सारू का बच्चा',child_name_tm = 'சாருவின் குழந்தை' where rch_child_id = '888881002120'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='किर्थिका डी का बच्चा',child_name_tm = 'கிருத்திகாவின் குழந்தை டி' where rch_child_id = '888881002134'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='सारू का बच्चा',child_name_tm = 'சாருவின் குழந்தை' where rch_child_id = '888881002121'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='आशु जी का बच्चा',child_name_tm = 'அனுஷ்யாவின் குழந்தை ஜி' where rch_child_id = '888881002122'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='कन्नन',child_name_tm = 'கண்ணன்' where rch_child_id = '888881002092'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='जानकी का बच्चा',child_name_tm = 'ஜானகியின் குழந்தை' where rch_child_id = '888881002123'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='किर्थिका डी का बच्चा',child_name_tm = 'கிருத்திகாவின் குழந்தை டி' where rch_child_id = '888881002130'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='विजाया वाई का बच्चा',child_name_tm = 'விஜய ஒய் குழந்தை' where rch_child_id = '888881002109'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='विष्णेश्वरी की बच्ची',child_name_tm = 'விஷ்னேஸ்வரியின் குழந்தை' where rch_child_id = '888881002133'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='विजाया का बच्चा',child_name_tm = 'விஜயாவின் குழந்தை கே' where rch_child_id = '888881002105'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='वनिता सुबाशिनी का बच्चा',child_name_tm = 'வனிதா சுபாஷினியின் குழந்தை' where rch_child_id = '888881002104'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='श्री देवी की संतान',child_name_tm = 'ஸ்ரீ தேவி எஸ்' where rch_child_id = '888881002112'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='सरस्वती जी का बच्चा',child_name_tm = 'சரஸ்வதியின் குழந்தை ஜி' where rch_child_id = '888881002117'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='प्रियंका वी का बच्चा',child_name_tm = 'பிரேமா எம் குழந்தை' where rch_child_id = '888881002147'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='भुवन श्री का बच्चा',child_name_tm = 'புவனா ஸ்ரீயின் குழந்தை' where rch_child_id = '888881002136'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='वनिता सुबाशिनी का बच्चा',child_name_tm = 'பிரேமா எம் குழந்தை' where rch_child_id = '888881002126'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='प्रेमा एम का बच्चा',child_name_tm = 'பிரேமா எம் குழந்தை' where rch_child_id = '888881002132'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='मलाथी एस का बच्चा',child_name_tm = 'மாலதி எஸ்' where rch_child_id = '888881002145'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='आशु जी का बच्चा',child_name_tm = 'அனுஷ்யாவின் குழந்தை ஜி' where rch_child_id = '888881002118'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='सुथा एस का बच्चा',child_name_tm = 'சூதா எஸ்' where rch_child_id = '888881002125'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='सुब्बुलक्ष्मी डी का बच्चा',child_name_tm = 'சுபுலட்சுமியின் குழந்தை டி' where rch_child_id = '888881002108'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='काजोल का बच्चा',child_name_tm = 'கஜோலின் குழந்தை' where rch_child_id = '888881002131'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='बेबी ऑफ मीरा मधुश्री एल',child_name_tm = 'மிர்ரா மதுஷ்ரீ எல்' where rch_child_id = '888881002103'");

        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='वनिता जी का बच्चा',child_name_tm = 'குழந்தை வனிதா ஜி' where rch_child_id = '888881002107'");
        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='शन्मुगा प्रिया I की संतान',child_name_tm = 'சண்முக பிரியாவின் குழந்தை I.' where rch_child_id = '888881002111'");



         */
        //  sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='"+ch_name_hi+"',child_name_tm = '"+ch_name_tm+"' where rch_child_id = '888881002111'");
        // sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = '"+m_name_hi+"',mother_name_tm = '"+m_name_tm+"',father_name_hi = '"+f_name_hi+"', father_name_tm = '"+f_name_tm+"'\n" +
        //      "WHERE rch_mother_id = '111110014164'; ");

        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi ='" + ch_name_hi + "',child_name_tm = '" + ch_name_tm + "' where rch_child_id = '" + child_id + "'");
        cursor = sqLiteDatabase.rawQuery("select rch_mother_id from NRP_child_master where rch_child_id = '" + child_id + "'", null);
        String rch_mother_id = null;
        if (cursor != null) {
            cursor.moveToFirst();
            rch_mother_id = cursor.getString(0);
        }
        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = '" + m_name_hi + "',mother_name_tm = '" + m_name_tm + "',father_name_hi = '" + f_name_hi + "', father_name_tm = '" + f_name_tm + "'\n" +
                "WHERE rch_mother_id = '" + rch_mother_id + "'; ");


        return true;
    }

    public Cursor getBlankChildren(String anm_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_master.rch_child_id,child_name FROM NRP_child_master join NRP_mother_master1 join NRP_child_immunization_fact_table on NRP_child_master.rch_mother_id = \n" +
                "                NRP_mother_master1.rch_mother_id and NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id WHERE (mother_name_hi ='' OR mother_name_tm='' OR father_name_hi='' OR father_name_tm='')\n" +
                "                OR (child_name_hi = '' OR child_name_tm = '') AND anm_id = '" + anm_id + "'", null);


        if ((cursor != null)) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor getBlankChildrenhi(String anm_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT NRP_child_master.rch_child_id,child_name FROM NRP_child_master join NRP_mother_master1 join NRP_child_immunization_fact_table on NRP_child_master.rch_mother_id = \n" +
                "                NRP_mother_master1.rch_mother_id and NRP_child_master.rch_child_id = NRP_child_immunization_fact_table.rch_child_id WHERE (mother_name_hi =''OR father_name_hi='' OR child_name_hi='')\n" +
                "                AND anm_id = '" + anm_id + "'", null);


        if ((cursor != null)) {
            return cursor;
        } else {
            return null;
        }
    }

    public void requestTable() {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        sqLiteDatabase.execSQL("Create table Request_tbl(\n" +
                "            request_id varchar(20), request_date text, primary key(request_id));");

    }

    public boolean insertRequest(String req_id, String date) {
        SQLiteDatabase sqLiteDatabase1 = openHelper.getWritableDatabase();

        sqLiteDatabase1.execSQL("Delete from Request_tbl where request_id ='" + req_id + "'");

        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableStructure.TableDetails.REQ_ID, req_id);
        contentValues.put(TableStructure.TableDetails.REQ_DATE, date);
        long result = sqLiteDatabase.insert(TableStructure.TableDetails.REQUEST_TABLE, null, contentValues);

        return result != -1;
    }

    public Cursor getRequest(String req_id, String date) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("Select request_id from Request_tbl where request_id = '" + req_id + "' and request_date = '" + date + "' ", null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor showRequest(String date) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("Select request_id from Request_tbl where request_date = '" + date + "' ", null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public void updateVaccineBarcode(String vaccineDate, String barcode) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        sqLiteDatabase.execSQL("Update NRP_vaccines_barcode set vaccination_date ='" + vaccineDate + "' where barcode_no ='" + barcode + "'");
    }

    public boolean insertVaccineBarcode(String v_id, String v_nm, String barcode, String v_date, String ex_date, String m_date, String b_no, String man) {
        SQLiteDatabase sqLiteDatabase1 = openHelper.getWritableDatabase();


        SQLiteDatabase sqLiteDatabase = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableStructure.TableDetails.V_ID, v_id);
        contentValues.put(TableStructure.TableDetails.V_NAME, v_nm);
        contentValues.put(TableStructure.TableDetails.BARCODE, barcode);
        contentValues.put(TableStructure.TableDetails.VAC_DATE, v_date);
        contentValues.put(TableStructure.TableDetails.EX_DATE, ex_date);
        contentValues.put(TableStructure.TableDetails.MAN_DATE, m_date);
        contentValues.put(TableStructure.TableDetails.BATCH_NO, b_no);
        contentValues.put(TableStructure.TableDetails.MAN, man);

        long result = sqLiteDatabase.insert(TableStructure.TableDetails.VACCINE_BARCODE, null, contentValues);

        return result != -1;
    }

    public void deleteVaccineBarcode() {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        sqLiteDatabase.execSQL("delete from NRP_vaccines_barcode");
        Log.d("operation: ", "vaccine barcode deleted");
    }

    public Cursor getUpdatedVaccines() {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("Select * from NRP_vaccines_barcode", null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor getChildNames() {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("Select rch_child_id, child_name from NRP_child_master", null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public Cursor getMotherFatherNames() {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("Select rch_mother_id, mother_name, father_name from NRP_mother_master1", null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public void updateLanguage(String ch_name_hi,String ch_name_tn,  String ch_id) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

        sqLiteDatabase.execSQL("UPDATE NRP_child_master set child_name_hi = '" + ch_name_hi + "', child_name_tm = '"+ch_name_tn+"' \n" +
                "WHERE rch_child_id = '" + ch_id + "'");

    }

    public void updateMotherTableLanguage(String m_name_hi,String m_name_tn,  String m_id, String f_name_hi, String f_name_tn) {
        SQLiteDatabase sqLiteDatabase = openHelper.getReadableDatabase();

        sqLiteDatabase.execSQL("UPDATE NRP_mother_master1 set mother_name_hi = '" + m_name_hi + "', mother_name_tm = '"+m_name_tn+"', father_name_hi = '"+f_name_hi +"', father_name_tm = '"+ f_name_tn + "' \n"  +
                "WHERE rch_child_id = '" + m_id + "'");
    }


}
