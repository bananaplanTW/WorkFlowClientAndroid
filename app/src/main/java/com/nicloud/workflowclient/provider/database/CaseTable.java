package com.nicloud.workflowclient.provider.database;

import android.database.sqlite.SQLiteDatabase;

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
        database.execSQL("DROP TABLE IF EXISTS " + WorkFlowContract.Case.TABLE_NAME);
        onCreate(database);
    }
}
