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
            + WorkFlowContract.Discussion.WORKER_NAME + " TEXT NOT NULL, "
            + WorkFlowContract.Discussion.WORKER_AVATAR_URI + " TEXT NOT NULL, "
            + WorkFlowContract.Discussion.CONTENT + " TEXT NOT NULL, "
            + WorkFlowContract.Discussion.FILE_NAME + " TEXT, "
            + WorkFlowContract.Discussion.FILE_URI + " TEXT, "
            + WorkFlowContract.Discussion.FILE_THUMB_URI + " TEXT, "
            + WorkFlowContract.Discussion.TYPE + " TEXT NOT NULL, "
            + WorkFlowContract.Discussion.CREATED_TIME + " INTEGER NOT NULL, "
            + WorkFlowContract.Discussion.UPDATED_TIME + " INTEGER NOT NULL"
            + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + WorkFlowContract.Discussion.TABLE_NAME);
        onCreate(database);
    }
}
