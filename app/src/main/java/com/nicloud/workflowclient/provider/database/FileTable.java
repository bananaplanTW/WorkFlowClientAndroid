package com.nicloud.workflowclient.provider.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by logicmelody on 2016/01/07.
 */
public class FileTable {

    private static final String DB_CREATE = "CREATE TABLE "
            + WorkFlowContract.File.TABLE_NAME
            + "("
            + WorkFlowContract.File._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WorkFlowContract.File.FILE_ID + " TEXT NOT NULL, "
            + WorkFlowContract.File.FILE_NAME + " TEXT NOT NULL, "
            + WorkFlowContract.File.FILE_TYPE + " TEXT NOT NULL, "
            + WorkFlowContract.File.FILE_URL + " TEXT NOT NULL, "
            + WorkFlowContract.File.FILE_THUMB_URL + " TEXT, "
            + WorkFlowContract.File.OWNER_ID + " TEXT NOT NULL, "
            + WorkFlowContract.File.OWNER_NAME + " TEXT, "
            + WorkFlowContract.File.CASE_ID + " TEXT, "
            + WorkFlowContract.File.TASK_ID + " TEXT, "
            + WorkFlowContract.File.CREATED_TIME + " INTEGER NOT NULL, "
            + WorkFlowContract.File.UPDATED_TIME + " INTEGER NOT NULL"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + WorkFlowContract.File.TABLE_NAME);
        onCreate(database);
    }
}
