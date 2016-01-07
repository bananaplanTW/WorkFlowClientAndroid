package com.nicloud.workflowclient.provider.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by logicmelody on 2016/01/07.
 */
public class MessageTable {

    private static final String DB_CREATE = "CREATE TABLE "
            + WorkFlowContract.Message.TABLE_NAME
            + "("
            + WorkFlowContract.Message._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + WorkFlowContract.Message.MESSAGE_ID + " TEXT NOT NULL, "
            + WorkFlowContract.Message.CONTENT + " TEXT NOT NULL, "
            + WorkFlowContract.Message.SENDER_ID + " TEXT NOT NULL, "
            + WorkFlowContract.Message.RECEIVER_ID + " TEXT NOT NULL, "
            + WorkFlowContract.Message.TIME + " INTEGER NOT NULL"
            + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + WorkFlowContract.Message.TABLE_NAME);
        onCreate(database);
    }
}
