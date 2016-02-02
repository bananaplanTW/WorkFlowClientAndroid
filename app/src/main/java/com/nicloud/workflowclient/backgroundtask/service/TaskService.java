package com.nicloud.workflowclient.backgroundtask.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.receiver.TaskCompletedReceiver;
import com.nicloud.workflowclient.utility.utils.DbUtils;
import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.tasklist.main.Task;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.utility.utils.RestfulUtils;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.utils.JsonUtils;
import com.nicloud.workflowclient.utility.utils.URLUtils;
import com.nicloud.workflowclient.utility.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
        public static final String LOAD_TASK_BY_ID = "task_service_load_task_by_id";
        public static final String LOAD_CASE_TASKS = "task_service_load_case_tasks";
    }

    public static final class ExtraKey {
        public static final String BOOLEAN_FIRST_LOAD_TASKS = "extra_first_load_my_tasks";
        public static final String STRING_TASK_ID = "extra_task_id";
        public static final String STRING_CASE_ID = "extra_case_id";
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
        intent.putExtra(ExtraKey.BOOLEAN_FIRST_LOAD_TASKS, isFirstLoad);

        return intent;
    }

    public static Intent generateLoadTaskByIdIntent(Context context, String taskId) {
        Intent intent = new Intent(context, TaskService.class);
        intent.setAction(Action.LOAD_TASK_BY_ID);
        intent.putExtra(ExtraKey.STRING_TASK_ID, taskId);

        return intent;
    }

    public static Intent generateLoadCaseTasksIntent(Context context, String caseId, boolean isFirstLoad) {
        Intent intent = new Intent(context, TaskService.class);
        intent.setAction(Action.LOAD_CASE_TASKS);
        intent.putExtra(ExtraKey.STRING_CASE_ID, caseId);
        intent.putExtra(ExtraKey.BOOLEAN_FIRST_LOAD_TASKS, isFirstLoad);

        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (Action.LOAD_MY_TASKS.equals(action)) {
            loadMyTasks(intent);

        } else if (Action.LOAD_TASK_BY_ID.equals(action)) {
            loadTaskById(intent);

        } else if (Action.LOAD_CASE_TASKS.equals(action)) {
            loadCaseTasks(intent);
        }
    }

    private void loadMyTasks(Intent intent) {
        Intent broadcastIntent = new Intent(TaskCompletedReceiver.ACTION_LOAD_TASKS_COMPLETED);
        boolean isFirstLoadTasks = intent.getBooleanExtra(ExtraKey.BOOLEAN_FIRST_LOAD_TASKS, true);
        if (isFirstLoadTasks) {
            broadcastIntent.putExtra(TaskCompletedReceiver.EXTRA_FROM, TaskCompletedReceiver.From.LOAD_FIRST);

        } else {
            broadcastIntent.putExtra(TaskCompletedReceiver.EXTRA_FROM, TaskCompletedReceiver.From.LOAD_NORMAL);
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

                insertAndUpdateTasksToDb(tasksFromDbMap, tasksFromServer);
                deleteTasksFromDb(tasksFromServerMap, tasksFromDb);

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

    private void loadTaskById(Intent intent) {
        String taskId = intent.getStringExtra(ExtraKey.STRING_TASK_ID);

        Intent broadcastIntent = new Intent(TaskCompletedReceiver.ACTION_LOAD_TASKS_COMPLETED);
        broadcastIntent.putExtra(TaskCompletedReceiver.EXTRA_FROM, TaskCompletedReceiver.From.DETAILED_TASK);
        boolean isFirstLoadMyTasks = intent.getBooleanExtra(ExtraKey.BOOLEAN_FIRST_LOAD_TASKS, true);
        if (isFirstLoadMyTasks) {
            broadcastIntent.putExtra(TaskCompletedReceiver.EXTRA_FROM, TaskCompletedReceiver.From.LOAD_FIRST);

        } else {
            broadcastIntent.putExtra(TaskCompletedReceiver.EXTRA_FROM, TaskCompletedReceiver.From.LOAD_NORMAL);
        }

        if (RestfulUtils.isConnectToInternet(this)) {
            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                String taskJsonString =
                        RestfulUtils.restfulGetRequest(getTaskByIdUrl(taskId), headers);

                JSONObject taskJson = new JSONObject(taskJsonString).getJSONObject("result");

                if (taskJson == null) return;

                Task task = Task.retrieveTaskFromJson(taskJson);

                DbUtils.updateTaskToDb(this, task);
                DbUtils.updateCheckListToDb(this, task.id, task.checkList);

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

    private void loadCaseTasks(Intent intent) {
        String caseId = intent.getStringExtra(ExtraKey.STRING_CASE_ID);

        Intent broadcastIntent = new Intent(TaskCompletedReceiver.ACTION_LOAD_TASKS_COMPLETED);
        boolean isFirstLoadTasks = intent.getBooleanExtra(ExtraKey.BOOLEAN_FIRST_LOAD_TASKS, true);
        if (isFirstLoadTasks) {
            broadcastIntent.putExtra(TaskCompletedReceiver.EXTRA_FROM, TaskCompletedReceiver.From.LOAD_FIRST);

        } else {
            broadcastIntent.putExtra(TaskCompletedReceiver.EXTRA_FROM, TaskCompletedReceiver.From.LOAD_NORMAL);
        }

        if (RestfulUtils.isConnectToInternet(this)) {
            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                String taskJsonString =
                        RestfulUtils.restfulGetRequest(getCaseTasksUrl(caseId), headers);

                JSONArray taskJsonList = JsonUtils.getJsonArrayFromJson(new JSONObject(taskJsonString), "result");

                if (taskJsonList != null) {
                    ArrayList<TaskInDb> tasksFromDb = new ArrayList<>();
                    ArrayList<Task> tasksFromServer = new ArrayList<>();
                    Map<String, TaskInDb> tasksFromDbMap = new HashMap<>();
                    Map<String, Task> tasksFromServerMap = new HashMap<>();

                    getCaseTasksFromDb(caseId, tasksFromDb, tasksFromDbMap);
                    getTasksFromServer(taskJsonList, tasksFromServer, tasksFromServerMap);

                    insertAndUpdateTasksToDb(tasksFromDbMap, tasksFromServer);
                    deleteTasksFromDb(tasksFromServerMap, tasksFromDb);
                }

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
        String selection =  WorkFlowContract.Task.WORKER_ID + " = ? AND " + WorkFlowContract.Task.STATUS + " != ?";
        String[] selectionArgs = new String[] {workerId, WorkFlowContract.Task.Status.DONE};
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

    private void getCaseTasksFromDb(String caseId, ArrayList<TaskInDb> taskList, Map<String, TaskInDb> taskMap) {
        String selection =  WorkFlowContract.Task.CASE_ID + " = ?";
        String[] selectionArgs = new String[] {caseId};
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
                Task task = Task.retrieveTaskFromJson(jsonObject);

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

    private String getTaskByIdUrl(String taskId) {
        return LoadingDataUtils.WorkingDataUrl.TASK_BY_ID + taskId;
    }

    private String getCaseTasksUrl(String caseId) {
        HashMap<String, String> queries = new HashMap<>();
        queries.put("caseId", caseId);

        return URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                LoadingDataUtils.WorkingDataUrl.EndPoints.TASKS, queries);
    }

    private void insertAndUpdateTasksToDb(Map<String, TaskInDb> tasksFromDbMap, List<Task> tasksFromServer) {
        for (Task task : tasksFromServer) {
            if (!tasksFromDbMap.containsKey(task.id)) {
                DbUtils.insertTaskToDb(this, task);
                DbUtils.insertCheckListToDb(this, task.checkList);

            } else {
                if (task.lastUpdatedTime <= tasksFromDbMap.get(task.id).lastUpdatedTime) continue;

                DbUtils.updateTaskToDb(this, task);
                DbUtils.updateCheckListToDb(this, task.id, task.checkList);
            }
        }
    }

    private void deleteTasksFromDb(Map<String, Task> tasksFromServerMap, ArrayList<TaskInDb> tasksFromDb) {
        for (TaskInDb taskInDb : tasksFromDb) {
            if (!tasksFromServerMap.containsKey(taskInDb.taskId)) {
                DbUtils.deleteTaskFromDb(this, taskInDb.taskId);
                DbUtils.deleteCheckListFromDb(this, taskInDb.taskId);
            }
        }
    }
}
