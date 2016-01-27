package com.nicloud.workflowclient.provider.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by logicmelody on 2016/01/07.
 */
public class DiscussionTable {

    private static final String DB_CREATE = "CREATE TABLE "
            + WorkFlowContract.Discussion.TABLE_NAME
            + "("
            + WorkFlowContract.Discussion._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WorkFlowContract.Discussion.DISCUSSION_ID + " TEXT NOT NULL, "
            + WorkFlowContract.Discussion.CASE_ID + " TEXT NOT NULL, "
            + WorkFlowContract.Discussion.WORKER_ID + " TEXT NOT NULL, "
            + WorkFlowContract.Discussion.CONTENT + " TEXT NOT NULL, "
            + WorkFlowContract.Discussion.TYPE + " INTEGER NOT NULL, "
            + WorkFlowContract.Discussion.TIME + " INTEGER NOT NULL"
            + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + WorkFlowContract.Discussion.TABLE_NAME);
        onCreate(database);
    }
}
