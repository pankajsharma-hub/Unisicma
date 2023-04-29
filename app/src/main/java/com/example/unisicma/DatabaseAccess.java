package com.example.unisicma;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseAccess extends SQLiteAssetHelper {
    public static final int database_version = 1;
    public DatabaseAccess(Context context) {
        super(context, TableStructure.TableDetails.DATABASE_NAME,null,database_version);
    }

}
