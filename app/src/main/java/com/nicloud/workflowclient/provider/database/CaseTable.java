package com.nicloud.workflowclient.provider.database;

import android.database.sqlite.SQLiteDatabase;

import com.nicloud.workflowclient.provider.contentprovider.WorkFlowDatabaseHelper;

/**
 * Created by logicmelody on 2016/01/07.
 */
public class CaseTable {

    private static final String DB_CREATE = "CREATE TABLE "
            + WorkFlowContract.Case.TABLE_NAME
            + "("
            + WorkFlowContract.Case._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WorkFlowContract.Case.CASE_ID + " TEXT NOT NULL, "
            + WorkFlowContract.Case.CASE_NAME + " TEXT NOT NULL, "
            + WorkFlowContract.Case.IS_COMPLETED + " INTEGER NOT NULL, "
            + WorkFlowContract.Case.UPDATED_TIME + " INTEGER NOT NULL"
            + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        int version = oldVersion;

        if (version < WorkFlowDatabaseHelper.DbVersion.VERSION_3) {
            String updateDb = "ALTER TABLE " + WorkFlowContract.Case.TABLE_NAME +
                              " ADD COLUMN " + WorkFlowContract.Case.OWNER_ID + " TEXT NOT NULL DEFAULT '';";
            String updateDb2 = "ALTER TABLE " + WorkFlowContract.Case.TABLE_NAME +
                               " ADD COLUMN " + WorkFlowContract.Case.DESCRIPTION + " TEXT;";

            database.execSQL(updateDb);
            database.execSQL(updateDb2);
        }
    }
}
