package com.nicloud.workflowclient.utility.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.nicloud.workflowclient.tasklist.main.Task;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;

import java.util.Date;

/**
 * Created by logicmelody on 2016/1/30.
 */
public class DbUtils {

    public static String getTaskNameById(Context context, String taskId) {
        String taskName = "";
        Cursor cursor = null;

        String[] projection = new String[] {WorkFlowContract.Task.TASK_NAME};
        String selection =  WorkFlowContract.Task.TASK_ID + " = ?";
        String[] selectionArgs = new String[] {taskId};

        try {
            cursor = context.getContentResolver().query(WorkFlowContract.Task.CONTENT_URI,
                    projection, selection, selectionArgs, null);

            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                taskName = cursor.getString(0);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return taskName;
    }

    public static Task getTaskById(Context context, String taskId) {
        String[] projection = new String[] {
                WorkFlowContract.Task._ID,
                WorkFlowContract.Task.TASK_ID,
                WorkFlowContract.Task.TASK_NAME,
                WorkFlowContract.Task.TASK_DESCRIPTION,
                WorkFlowContract.Task.CASE_ID,
                WorkFlowContract.Task.CASE_NAME,
                WorkFlowContract.Task.WORKER_ID,
                WorkFlowContract.Task.DUE_DATE,
                WorkFlowContract.Task.UPDATED_TIME
        };
        int ID = 0;
        int TASK_ID = 1;
        int TASK_NAME = 2;
        int TASK_DESCRIPTION = 3;
        int CASE_ID = 4;
        int CASE_NAME = 5;
        int WORKER_ID = 6;
        int DUE_DATE = 7;
        int UPDATED_TIME = 8;

        String selection = WorkFlowContract.Task.TASK_ID + " = ?";
        String[] selectionArgs = new String[] {taskId};

        Task task = null;
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(WorkFlowContract.Task.CONTENT_URI,
                    projection, selection, selectionArgs, null);

            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                task = new Task(cursor.getString(TASK_ID),
                                cursor.getString(TASK_NAME),
                                cursor.getString(TASK_DESCRIPTION),
                                cursor.getString(CASE_NAME),
                                cursor.getString(CASE_ID),
                                cursor.getString(WORKER_ID),
                                new Date(cursor.getLong(DUE_DATE)),
                                null,
                                cursor.getLong(UPDATED_TIME));
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return task;
    }

    public static void setCheckItem(Context context, String taskId, int position, boolean isChecked) {
        String selection =  WorkFlowContract.CheckList.TASK_ID + " = ? AND " +
                            WorkFlowContract.CheckList.POSITION + " = ?";
        String[] selectionArgs = new String[] {taskId, String.valueOf(position)};

        ContentValues values = new ContentValues();
        values.put(WorkFlowContract.CheckList.IS_CHECKED, isChecked);

        context.getContentResolver().update(WorkFlowContract.CheckList.CONTENT_URI, values, selection, selectionArgs);
    }
}
