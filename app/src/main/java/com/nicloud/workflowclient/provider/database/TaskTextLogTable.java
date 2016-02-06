package com.nicloud.workflowclient.provider.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by logicmelody on 2016/01/07.
 */
public class TaskTextLogTable {

    private static final String DB_CREATE = "CREATE TABLE "
            + WorkFlowContract.TaskTextLog.TABLE_NAME
            + "("
            + WorkFlowContract.TaskTextLog._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WorkFlowContract.TaskTextLog.TASK_ID + " TEXT NOT NULL, "
            + WorkFlowContract.TaskTextLog.WORKER_ID + " TEXT NOT NULL, "
            + WorkFlowContract.TaskTextLog.WORKER_NAME + " TEXT NOT NULL, "
            + WorkFlowContract.TaskTextLog.WORKER_AVATAR_URL + " TEXT, "
            + WorkFlowContract.TaskTextLog.CREATED_TIME + " INTEGER NOT NULL, "
            + WorkFlowContract.TaskTextLog.CONTENT + " TEXT NOT NULL"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + WorkFlowContract.Task.TABLE_NAME);
        onCreate(database);
    }
}
