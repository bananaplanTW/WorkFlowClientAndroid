package com.nicloud.workflowclient.utility.utils;

import android.content.Context;
import android.database.Cursor;

import com.nicloud.workflowclient.provider.database.WorkFlowContract;

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
}
