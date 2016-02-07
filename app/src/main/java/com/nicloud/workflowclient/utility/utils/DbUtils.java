package com.nicloud.workflowclient.utility.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.nicloud.workflowclient.data.data.Case;
import com.nicloud.workflowclient.data.data.File;
import com.nicloud.workflowclient.data.data.TaskTextLog;
import com.nicloud.workflowclient.detailedtask.checklist.CheckItem;
import com.nicloud.workflowclient.data.data.Task;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;

import java.util.Date;
import java.util.List;

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
                WorkFlowContract.Task.UPDATED_TIME,
                WorkFlowContract.Task.STATUS
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
        int STATUS = 9;

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
                                cursor.getString(STATUS),
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

    public static int getCaseCount(Context context) {
        String[] projection = new String[] {
                WorkFlowContract.Case._ID,
        };

        Cursor cursor = null;
        int caseCount = 0;

        try {
            cursor = context.getContentResolver().query(WorkFlowContract.Case.CONTENT_URI,
                    projection, null, null, null);

            if (cursor != null && cursor.getCount() != 0) {
                caseCount = cursor.getCount();
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return caseCount;
    }

    public static Case getCaseById(Context context, String caseId) {
        String[] projection = new String[] {
                WorkFlowContract.Case._ID,
                WorkFlowContract.Case.CASE_ID,
                WorkFlowContract.Case.CASE_NAME,
                WorkFlowContract.Case.IS_COMPLETED,
                WorkFlowContract.Case.UPDATED_TIME
        };
        int ID = 0;
        int CASE_ID = 1;
        int CASE_NAME = 2;
        int IS_COMPLETED = 3;
        int UPDATED_TIME = 4;

        String selection = WorkFlowContract.Case.CASE_ID + " = ?";
        String[] selectionArgs = new String[] {caseId};

        Case aCase = null;
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(WorkFlowContract.Case.CONTENT_URI,
                    projection, selection, selectionArgs, null);

            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();

                aCase = new Case(cursor.getString(CASE_ID),
                                 cursor.getString(CASE_NAME),
                                 cursor.getInt(IS_COMPLETED) == 1,
                                 cursor.getLong(UPDATED_TIME));
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return aCase;
    }


    public static void setCheckItem(Context context, String taskId, int position, boolean isChecked) {
        String selection =  WorkFlowContract.CheckList.TASK_ID + " = ? AND " +
                WorkFlowContract.CheckList.POSITION + " = ?";
        String[] selectionArgs = new String[] {taskId, String.valueOf(position)};

        ContentValues values = new ContentValues();
        values.put(WorkFlowContract.CheckList.IS_CHECKED, isChecked);

        context.getContentResolver().update(WorkFlowContract.CheckList.CONTENT_URI, values, selection, selectionArgs);
    }


    // Insert
    public static void insertTaskToDb(Context context, Task task) {
        context.getContentResolver().insert(WorkFlowContract.Task.CONTENT_URI, convertTaskToContentValues(task));
    }

    public static void insertCheckListToDb(Context context, List<CheckItem> checkList) {
        for (CheckItem checkItem : checkList) {
            context.getContentResolver().insert(WorkFlowContract.CheckList.CONTENT_URI,
                    convertCheckItemToContentValues(checkItem));
        }
    }

    public static void insertTaskTextLogToDb(Context context, TaskTextLog taskTextLog) {
        context.getContentResolver().insert(WorkFlowContract.TaskTextLog.CONTENT_URI,
                convertTaskTextLogToContentValues(taskTextLog));
    }

    public static void insertFileToDb(Context context, File file) {
        context.getContentResolver().insert(WorkFlowContract.File.CONTENT_URI, convertFileToContentValues(file));
    }


    // Update
    public static void updateTaskToDb(Context context, Task task) {
        String selection =  WorkFlowContract.Task.TASK_ID + " = ?";
        String[] selectionArgs = new String[] {task.id};

        context.getContentResolver().update(WorkFlowContract.Task.CONTENT_URI,
                convertTaskToContentValues(task), selection, selectionArgs);
    }

    public static void updateCheckListToDb(Context context, String taskId, List<CheckItem> checkList) {
        deleteCheckListFromDb(context, taskId);
        insertCheckListToDb(context, checkList);
    }

    public static void updateFileToDb(Context context, File file) {
        String selection =  WorkFlowContract.File.FILE_ID + " = ?";
        String[] selectionArgs = new String[] {file.fileId};

        context.getContentResolver().update(WorkFlowContract.File.CONTENT_URI,
                convertFileToContentValues(file), selection, selectionArgs);
    }


    // Delete
    public static void deleteTaskFromDb(Context context, String taskId) {
        String selection =  WorkFlowContract.Task.TASK_ID + " = ?";
        String[] selectionArgs = new String[] {taskId};

        context.getContentResolver().delete(WorkFlowContract.Task.CONTENT_URI, selection, selectionArgs);
    }

    public static void deleteCheckListFromDb(Context context, String taskId) {
        String selection =  WorkFlowContract.CheckList.TASK_ID + " = ?";
        String[] selectionArgs = new String[] {taskId};

        context.getContentResolver().delete(WorkFlowContract.CheckList.CONTENT_URI, selection, selectionArgs);
    }

    public static void deleteTaskTextLogFromDb(Context context, String taskId) {
        String selection =  WorkFlowContract.TaskTextLog.TASK_ID + " = ?";
        String[] selectionArgs = new String[] {taskId};

        context.getContentResolver().delete(WorkFlowContract.TaskTextLog.CONTENT_URI, selection, selectionArgs);
    }

    public static void deleteTaskFileFromDb(Context context, String taskId) {
        String selection =  WorkFlowContract.File.TASK_ID + " = ?";
        String[] selectionArgs = new String[] {taskId};

        context.getContentResolver().delete(WorkFlowContract.File.CONTENT_URI, selection, selectionArgs);
    }

    public static void deleteFileFromDb(Context context, String fileId) {
        String selection =  WorkFlowContract.File.FILE_ID + " = ?";
        String[] selectionArgs = new String[] {fileId};

        context.getContentResolver().delete(WorkFlowContract.File.CONTENT_URI, selection, selectionArgs);
    }


    // ContentValues
    public static ContentValues convertFileToContentValues(File file) {
        ContentValues values = new ContentValues();

        values.put(WorkFlowContract.File.FILE_ID, file.fileId);
        values.put(WorkFlowContract.File.FILE_NAME, file.fileName);
        values.put(WorkFlowContract.File.FILE_TYPE, file.fileType);
        values.put(WorkFlowContract.File.FILE_URL, file.fileUrl);
        values.put(WorkFlowContract.File.FILE_THUMB_URL, file.fileThumbUrl);
        values.put(WorkFlowContract.File.OWNER_ID, file.ownerId);
        values.put(WorkFlowContract.File.OWNER_NAME, file.ownerName);
        values.put(WorkFlowContract.File.CASE_ID, file.caseId);
        values.put(WorkFlowContract.File.TASK_ID, file.taskId);
        values.put(WorkFlowContract.File.CREATED_TIME, file.createdTime);
        values.put(WorkFlowContract.File.UPDATED_TIME, file.updatedTime);

        return values;
    }

    public static ContentValues convertTaskToContentValues(Task task) {
        ContentValues values = new ContentValues();

        values.put(WorkFlowContract.Task.TASK_ID, task.id);
        values.put(WorkFlowContract.Task.TASK_DESCRIPTION, task.description);
        values.put(WorkFlowContract.Task.TASK_NAME, task.name);
        values.put(WorkFlowContract.Task.CASE_ID, task.caseId);
        values.put(WorkFlowContract.Task.CASE_NAME, task.caseName);
        values.put(WorkFlowContract.Task.WORKER_ID, task.workerId);
        values.put(WorkFlowContract.Task.DUE_DATE, task.dueDate.getTime());
        values.put(WorkFlowContract.Task.UPDATED_TIME, task.lastUpdatedTime);
        values.put(WorkFlowContract.Task.STATUS, task.status);

        return values;
    }

    public static ContentValues convertCheckItemToContentValues(CheckItem checkItem) {
        ContentValues values = new ContentValues();

        values.put(WorkFlowContract.CheckList.CHECK_NAME, checkItem.name);
        values.put(WorkFlowContract.CheckList.IS_CHECKED, checkItem.isChecked);
        values.put(WorkFlowContract.CheckList.TASK_ID, checkItem.taskId);
        values.put(WorkFlowContract.CheckList.POSITION, checkItem.position);

        return values;
    }

    private static ContentValues convertTaskTextLogToContentValues(TaskTextLog taskTextLog) {
        ContentValues values = new ContentValues();

        values.put(WorkFlowContract.TaskTextLog.TASK_TEXT_LOG_ID, taskTextLog.taskTextLogId);
        values.put(WorkFlowContract.TaskTextLog.TASK_ID, taskTextLog.taskId);
        values.put(WorkFlowContract.TaskTextLog.OWNER_ID, taskTextLog.ownerId);
        values.put(WorkFlowContract.TaskTextLog.OWNER_NAME, taskTextLog.ownerName);
        values.put(WorkFlowContract.TaskTextLog.OWNER_AVATAR_URL, taskTextLog.ownerAvatarUrl);
        values.put(WorkFlowContract.TaskTextLog.CREATED_TIME, taskTextLog.createdTime);
        values.put(WorkFlowContract.TaskTextLog.UPDATED_TIME, taskTextLog.updatedTime);
        values.put(WorkFlowContract.TaskTextLog.CONTENT, taskTextLog.content);

        return values;
    }
}
