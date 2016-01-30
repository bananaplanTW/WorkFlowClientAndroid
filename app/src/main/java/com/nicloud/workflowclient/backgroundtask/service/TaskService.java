package com.nicloud.workflowclient.backgroundtask.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.receiver.MessageCompletedReceiver;
import com.nicloud.workflowclient.backgroundtask.receiver.TaskCompletedReceiver;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.data.data.Task;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.utility.RestfulUtils;
import com.nicloud.workflowclient.data.utility.URLUtils;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.utils.JsonUtils;
import com.nicloud.workflowclient.utility.utils.NotificationUtils;
import com.nicloud.workflowclient.utility.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by logicmelody on 2016/1/14.
 */
public class TaskService extends IntentService {

    private static final String TAG = "TaskService";

    public static final class Action {
        public static final String LOAD_MY_TASKS = "task_service_load_my_tasks";
    }

    public static final class ExtraKey {
        public static final String BOOLEAN_FIRST_LOAD_MY_TASKS = "extra_first_load_my_tasks";
    }

    private static final String[] mProjection = new String[] {
            WorkFlowContract.Task.TASK_ID,
            WorkFlowContract.Task.UPDATED_TIME
    };
    private static final int TASK_ID = 0;
    private static final int UPDATED_TIME = 1;

    private Handler mHandler;


    private class TaskInDb {

        public String taskId;
        public long lastUpdatedTime = 0L;


        public TaskInDb(String taskId, long lastUpdatedTime) {
            this.taskId = taskId;
            this.lastUpdatedTime = lastUpdatedTime;
        }
    }

    public TaskService() {
        super(TAG);
        mHandler = new Handler();
    }

    public static Intent generateLoadMyTasksIntent(Context context, boolean isFirstLoad) {
        Intent intent = new Intent(context, TaskService.class);
        intent.setAction(Action.LOAD_MY_TASKS);
        intent.putExtra(ExtraKey.BOOLEAN_FIRST_LOAD_MY_TASKS, isFirstLoad);

        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (Action.LOAD_MY_TASKS.equals(action)) {
            loadMyTasks(intent);
        }
    }

    private void loadMyTasks(Intent intent) {
        Intent broadcastIntent = new Intent(TaskCompletedReceiver.ACTION_LOAD_TASKS_COMPLETED);
        boolean isFirstLoadMyTasks = intent.getBooleanExtra(ExtraKey.BOOLEAN_FIRST_LOAD_MY_TASKS, true);
        if (isFirstLoadMyTasks) {
            broadcastIntent.putExtra(TaskCompletedReceiver.EXTRA_FROM, TaskCompletedReceiver.From.MY_TASK_FIRST);

        } else {
            broadcastIntent.putExtra(TaskCompletedReceiver.EXTRA_FROM, TaskCompletedReceiver.From.MY_TASK);
        }

        if (RestfulUtils.isConnectToInternet(this)) {
            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                String taskJsonString =
                        RestfulUtils.restfulGetRequest(getTasksByWorkerUrl(WorkingData.getUserId()), headers);

                JSONObject taskJson = new JSONObject(taskJsonString).getJSONObject("result");
                JSONArray taskJsonList = JsonUtils.getJsonArrayFromJson(taskJson, "scheduledTasks");

                ArrayList<TaskInDb> tasksFromDb = new ArrayList<>();
                ArrayList<Task> tasksFromServer = new ArrayList<>();
                Map<String, TaskInDb> tasksFromDbMap = new HashMap<>();
                Map<String, Task> tasksFromServerMap = new HashMap<>();

                getMyTasksFromDb(WorkingData.getUserId(), tasksFromDb, tasksFromDbMap);
                getTasksFromServer(taskJsonList, tasksFromServer, tasksFromServerMap);

                insertAndUpdateTaskToDb(tasksFromDbMap, tasksFromServer);
                deleteTaskFromDb(tasksFromServerMap, tasksFromDb);

            } catch (JSONException e) {
                e.printStackTrace();
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private void getMyTasksFromDb(String workerId, ArrayList<TaskInDb> taskList, Map<String, TaskInDb> taskMap) {
        String selection =  WorkFlowContract.Task.WORKER_ID + " = ?";
        String[] selectionArgs = new String[] {workerId};
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(WorkFlowContract.Task.CONTENT_URI,
                                                mProjection, selection, selectionArgs, null);

            if (cursor != null && cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    TaskInDb taskInDb = new TaskInDb(cursor.getString(TASK_ID), cursor.getLong(UPDATED_TIME));
                    taskList.add(taskInDb);
                    taskMap.put(taskInDb.taskId, taskInDb);
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void getTasksFromServer(JSONArray taskJsonList, ArrayList<Task> taskList, Map<String, Task> taskMap) {
        for (int i = 0 ; i < taskJsonList.length() ; i++) {
            try {
                JSONObject jsonObject = taskJsonList.getJSONObject(i);

                String taskId = jsonObject.getString("_id");
                String name = jsonObject.getString("name");
                String description = JsonUtils.getStringFromJson(jsonObject, "description");
                String caseName = jsonObject.getString("caseName");
                String caseId = jsonObject.getString("caseId");
                String workerId = JsonUtils.getStringFromJson(jsonObject, "employeeId");
                long dueDate = JsonUtils.getLongFromJson(jsonObject, "dueDate");
                long lastUpdatedTime = jsonObject.getLong("updatedAt");

                Task task = new Task(taskId, name, description, caseName, caseId,
                                     workerId, new Date(dueDate), lastUpdatedTime);
                taskList.add(task);
                taskMap.put(task.id, task);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getTasksByWorkerUrl(String workerId) {
        return LoadingDataUtils.WorkingDataUrl.TASKS_BY_WORKER + workerId;
    }

    private void insertAndUpdateTaskToDb(Map<String, TaskInDb> tasksFromDbMap, List<Task> tasksFromServer) {
        for (Task task : tasksFromServer) {
            if (!tasksFromDbMap.containsKey(task.id)) {
                insertTaskToDb(task);

            } else {
                if (task.lastUpdatedTime <= tasksFromDbMap.get(task.id).lastUpdatedTime) continue;

                updateTaskToDb(task.id, task);
            }
        }
    }

    private void deleteTaskFromDb(Map<String, Task> tasksFromServerMap, ArrayList<TaskInDb> tasksFromDb) {
        for (TaskInDb taskInDb : tasksFromDb) {
            if (!tasksFromServerMap.containsKey(taskInDb.taskId)) {
                deleteTaskFromDb(taskInDb.taskId);
            }
        }
    }

    private void insertTaskToDb(Task task) {
        // TODO: Insert check list table
        getContentResolver().insert(WorkFlowContract.Task.CONTENT_URI, convertTaskToContentValues(task));
    }

    private void updateTaskToDb(String taskId, Task task) {
        String selection =  WorkFlowContract.Task.TASK_ID + " = ?";
        String[] selectionArgs = new String[] {taskId};

        getContentResolver().update(WorkFlowContract.Task.CONTENT_URI,
                convertTaskToContentValues(task), selection, selectionArgs);
    }

    private void deleteTaskFromDb(String taskId) {
        String selection =  WorkFlowContract.Task.TASK_ID + " = ?";
        String[] selectionArgs = new String[] {taskId};

        getContentResolver().delete(WorkFlowContract.Task.CONTENT_URI, selection, selectionArgs);
    }

    private ContentValues convertTaskToContentValues(Task task) {
        ContentValues values = new ContentValues();

        values.put(WorkFlowContract.Task.TASK_ID, task.id);
        values.put(WorkFlowContract.Task.TASK_DESCRIPTION, task.description);
        values.put(WorkFlowContract.Task.TASK_NAME, task.name);
        values.put(WorkFlowContract.Task.CASE_ID, task.caseId);
        values.put(WorkFlowContract.Task.CASE_NAME, task.caseName);
        values.put(WorkFlowContract.Task.WORKER_ID, task.workerId);
        values.put(WorkFlowContract.Task.DUE_DATE, task.dueDate.getTime());
        values.put(WorkFlowContract.Task.UPDATED_TIME, task.lastUpdatedTime);

        return values;
    }
}
