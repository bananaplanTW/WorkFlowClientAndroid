package com.nicloud.workflowclient.provider.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by logicmelody on 2016/01/07.
 */
public class CheckListTable {

    private static final String DB_CREATE = "CREATE TABLE "
            + WorkFlowContract.CheckList.TABLE_NAME
            + "("
            + WorkFlowContract.CheckList._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WorkFlowContract.CheckList.CHECK_NAME + " TEXT NOT NULL, "
            + WorkFlowContract.CheckList.IS_CHECKED + " INTEGER NOT NULL, "
            + WorkFlowContract.CheckList.TASK_ID + " TEXT NOT NULL"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + WorkFlowContract.CheckList.TABLE_NAME);
        onCreate(database);
    }
}
