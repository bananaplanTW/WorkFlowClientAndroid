package com.nicloud.workflowclient.provider.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by logicmelody on 2016/01/07.
 */
public class TaskTable {

    private static final String DB_CREATE = "CREATE TABLE "
            + WorkFlowContract.Task.TABLE_NAME
            + "("
            + WorkFlowContract.Task._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WorkFlowContract.Task.TASK_ID + " TEXT NOT NULL, "
            + WorkFlowContract.Task.TASK_NAME + " TEXT NOT NULL, "
            + WorkFlowContract.Task.TASK_DESCRIPTION + " TEXT, "
            + WorkFlowContract.Task.CASE_ID + " TEXT NOT NULL, "
            + WorkFlowContract.Task.CASE_NAME + " TEXT NOT NULL, "
            + WorkFlowContract.Task.WORKER_ID + " TEXT, "
            + WorkFlowContract.Task.DUE_DATE + " INTEGER, "
            + WorkFlowContract.Task.UPDATED_TIME + " INTEGER NOT NULL"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + WorkFlowContract.Task.TABLE_NAME);
        onCreate(database);
    }
}
