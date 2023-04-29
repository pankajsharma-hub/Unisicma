package com.example.unisicma;

import android.provider.BaseColumns;

public class TableStructure {
    public TableStructure() {

    }

    public static abstract class TableDetails implements BaseColumns {
//        public static final String RCH_CHILD_ID = "rch_child_id";
//        public static final String VACCINE_ID = "vaccine_id";
//        public static final String ANM_ID = "anm_id";
//
//        public static final String ASHA_ID = "asha_id";
//
//        public static final String FACILITY_ID = "facility_id";
//
//        public static final String WEIGHT_AT_VACCINATION = "weight_at_vaccination";
//        public static final String DATE_OF_VACCINATION = "date_of_vaccination";

        public static final String DATABASE_NAME = "unisicma.db";


        public static final String TABLE1 = "NRP_child_immunization_fact_table";
        public static final String CHILD_MASTER = "NRP_child_master";
        public static final String COL1 = "rch_child_id";
        public static final String COL2 = "vaccine_id";
        public static final String COL3 = "anm_id";
        public static final String COL4 = "asha_id";
        public static final String COL5 = "facility_id";
        public static final String COL6 = "weight_at_vaccination";
        public static final String COL7 = "date_of_vaccination";
        public static final String COL8 = "barcode_no";

        public static final String ANM_ID = "anm_id";
        public static final String ANM_PASS = "password";
        public static final String ANM_MOBILE = "mobile";
        public static final String TABLE2 = "anm_signup";


        //-----------------child_master--------------------

        public static final String CHILD_NAME = "child_name";
        public static final String MOTHER_ID = "rch_mother_id";
        public static final String EN_DATE = "enrollment_date";
        public static final String CHILD_SEX = "child_sex";
        public static final String CHILD_DOB = "child_dob";
        public static final String BIRTH_WEIGHT = "birth_weight";
        public static final String D_FACILITY_ID = "delivery_facility_id";
        public static final String R_FACILITY_ID = "resident_facility_id";
        public static final String CHILD_NAME_HI = "child_name_hi";
        public static final String CHILD_NAME_TN = "child_name_tm";



        //---------------------mother_master--------------------------
        public static final String MOTHER_MASTER = "NRP_mother_master1";
        public static final String MOTHER_NAME = "mother_name";
        public static final String FATHER_NAME = "husband_name";
        public static final String M_MOB = "mother_mobile";
        public static final String F_MOB = "husband_mobile";
        public static final String AGE_REGN = "age_at_regn";


        //----------------------facility_master---------------

        public static final String FACILITY_MASTER = "NRP_facility_master";
        public static final String FACILITY = "facility_id";
        public static final String FAC_NAME = "facility_name";
        public static final String F_TYPE = "facility_type";
        public static final String STATE = "state";
        public static final String DISTRICT = "district";
        public static final String BLOCK = "block";
        public static final String PHC = "phc";
        public static final String SC = "sc";
        public static final String VILLAGE = "village";


        //----------------------anm_master---------------

        public static final String ANM_MASTER = "NRP_anm_master";

        public static final String ANM_NAME = "anm_name";
        public static final String ANM_DOB = "anm_dob";
        public static final String ANM_MOB = "anm_mobile";
        public static final String ANM_ADDRESS = "anm_address";


        //----------------------anm_mapping_table---------------

        public static final String ANM_MAPPING = "NRP_anm_facility_mapping_fact_table";

        public static final String JOIN_DATE = "join_date";

        //------------------ Request table -------------------------

        public static final String REQUEST_TABLE = "Request_tbl";

        public static final String REQ_ID = "request_id";
        public static final String REQ_DATE = "request_date";

        //----------------Vaccine Table ----------------------

        public static final String VACCINE_BARCODE = "NRP_vaccines_barcode";

        public static final String V_ID = "vaccine_id";
        public static final String V_NAME = "vaccine_name";
        public static final String BARCODE = "barcode_no";
        public static final String VAC_DATE = "vaccination_date";
        public static final String EX_DATE = "expiry_date";
        public static final String MAN_DATE = "manufacturing_date";
        public static final String BATCH_NO = "batch_no";
        public static final String MAN = "manufacturer";

    }
}
