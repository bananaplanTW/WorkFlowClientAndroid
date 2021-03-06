package com.nicloud.workflowclient.backgroundtask.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.receiver.GeneralCompletedReceiver;
import com.nicloud.workflowclient.data.data.Case;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.data.data.Task;
import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.utility.utils.RestfulUtils;
import com.nicloud.workflowclient.utility.utils.URLUtils;
import com.nicloud.workflowclient.utility.utils.DbUtils;
import com.nicloud.workflowclient.utility.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by logicmelody on 2015/12/10.
 */
public class GeneralService extends IntentService {

    private static final String TAG = "ActionService";

    public static class Action {
        public static final String CHECK_ITEM = "general_service_action_check_item";
        public static final String COMPLETE_TASK = "general_server_action_complete_task";
        public static final String LOAD_CASES_AND_WORKERS = "general_server_action_load_cases_and_workers";
        public static final String CREATE_CASE = "general_server_action_create_case";
        public static final String CREATE_TASK = "general_server_action_create_task";
        public static final String UPDATE_TASK_DESCRIPTION = "general_server_action_update_task_description";
        public static final String UPDATE_TASK_DUEDATE = "general_server_action_update_task_duedate";
        public static final String UPDATE_CASE_DESCRIPTION = "general_server_action_update_case_description";
        public static final String ADD_WORKER_TO_CASE = "general_server_action_add_worker_to_case";
        public static final String REGISTER_USER = "general_server_action_register_user";
    }

    public static class ExtraKey {
        public static final String TASK_ID = "extra_task_id";
        public static final String TASK_NAME = "extra_task_name";
        public static final String TASK_DESCRIPTION = "extra_task_description";
        public static final String TASK_DUEDATE = "extra_task_duedate";

        public static final String CASE_NAME = "extra_case_name";
        public static final String CASE_ID = "extra_case_id";
        public static final String CASE_DESCRIPTION = "extra_case_description";

        public static final String WORKER_ID = "extra_worker_id";
        public static final String WORKER_EMAIL = "extra_worker_email";
        public static final String BOOLEAN_LOAD_WORKERS = "extra_load_workers";

        public static final String REGISTER_USER_EMAIL = "extra_register_user_email";
        public static final String REGISTER_USER_PASSWORD = "extra_register_user_password";
        public static final String REGISTER_USER_NAME = "extra_register_user_name";

        public static final String ACTION_SUCCESSFUL = "extra_action_successful";

        // Check item
        public static final String CHECK_ITEM_INDEX = "extra_check_item_index";
        public static final String CHECK_ITEM_CHECKED = "extra_check_item_checked";
    }

    private class CaseInDb {

        public String caseId;
        public long updatedTime = 0L;


        public CaseInDb(String caseId, long updatedTime) {
            this.caseId = caseId;
            this.updatedTime = updatedTime;
        }
    }

    private Handler mHandler;


    /**
     * Creates an IntentService.
     * Invoked by your subclass's constructor.
     */
    public GeneralService() {
        super(TAG);
        mHandler = new Handler();
    }

    public static Intent generateLoadCasesAndWorkersIntent(Context context, boolean isLoadWorkers) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(Action.LOAD_CASES_AND_WORKERS);
        intent.putExtra(ExtraKey.BOOLEAN_LOAD_WORKERS, isLoadWorkers);

        return intent;
    }

    public static Intent generateCreateCaseIntent(Context context, String caseName) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(Action.CREATE_CASE);
        intent.putExtra(ExtraKey.CASE_NAME, caseName);

        return intent;
    }

    public static Intent generateCreateTaskIntent(Context context, String taskName,
                                                  String caseId, String workerId, long dueDate) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(Action.CREATE_TASK);
        intent.putExtra(ExtraKey.TASK_NAME, taskName);
        intent.putExtra(ExtraKey.CASE_ID, caseId);
        intent.putExtra(ExtraKey.WORKER_ID, workerId);
        intent.putExtra(ExtraKey.TASK_DUEDATE, dueDate);

        return intent;
    }

    public static Intent generateUpdateTaskDescriptionIntent(Context context, String taskId, String taskDescription) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(Action.UPDATE_TASK_DESCRIPTION);
        intent.putExtra(ExtraKey.TASK_ID, taskId);
        intent.putExtra(ExtraKey.TASK_DESCRIPTION, taskDescription);

        return intent;
    }

    public static Intent generateUpdateTaskDueDateIntent(Context context, String taskId, long taskDueDate) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(Action.UPDATE_TASK_DUEDATE);
        intent.putExtra(ExtraKey.TASK_ID, taskId);
        intent.putExtra(ExtraKey.TASK_DUEDATE, taskDueDate);

        return intent;
    }

    public static Intent generateUpdateCaseDescriptionIntent(Context context, String caseId, String caseDescription) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(Action.UPDATE_CASE_DESCRIPTION);
        intent.putExtra(ExtraKey.CASE_ID, caseId);
        intent.putExtra(ExtraKey.CASE_DESCRIPTION, caseDescription);

        return intent;
    }

    public static Intent generateAddWorkerToCaseIntent(Context context, String caseId, String workerEmail) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(Action.ADD_WORKER_TO_CASE);
        intent.putExtra(ExtraKey.CASE_ID, caseId);
        intent.putExtra(ExtraKey.WORKER_EMAIL, workerEmail);

        return intent;
    }

    public static Intent generateRegisterUserIntent(Context context, String email, String password, String name) {
        Intent intent = new Intent(context, GeneralService.class);
        intent.setAction(Action.REGISTER_USER);
        intent.putExtra(ExtraKey.REGISTER_USER_EMAIL, email);
        intent.putExtra(ExtraKey.REGISTER_USER_PASSWORD, password);
        intent.putExtra(ExtraKey.REGISTER_USER_NAME, name);

        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (Action.CHECK_ITEM.equals(action)) {
            checkItem(intent);

        } else if (Action.COMPLETE_TASK.equals(action)) {
            completeTask(intent);

        } else if (Action.LOAD_CASES_AND_WORKERS.equals(action)) {
            loadCasesAndWorkers(intent);

        } else if (Action.CREATE_CASE.equals(action)) {
            createCase(intent);

        } else if (Action.CREATE_TASK.equals(action)) {
            createTask(intent);

        } else if (Action.UPDATE_TASK_DESCRIPTION.equals(action)) {
            updateTaskDescription(intent);

        } else if (Action.UPDATE_TASK_DUEDATE.equals(action)) {
            updateTaskDueDate(intent);

        } else if (Action.UPDATE_CASE_DESCRIPTION.equals(action)) {
            updateCaseDescription(intent);

        } else if (Action.ADD_WORKER_TO_CASE.equals(action)) {
            addWorkerToCase(intent);

        } else if (Action.REGISTER_USER.equals(action)) {
            registerUser(intent);
        }
    }

    private void checkItem(Intent intent) {
        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        int position = intent.getIntExtra(ExtraKey.CHECK_ITEM_INDEX, 0);
        boolean checked = intent.getBooleanExtra(ExtraKey.CHECK_ITEM_CHECKED, false);

        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            HashMap<String, String> bodies = new HashMap<>();
            bodies.put("td", taskId);
            bodies.put("tx", String.valueOf(position));
            bodies.put("tc", String.valueOf(checked));

            String urlString = URLUtils
                    .buildURLString(LoadingDataUtils.sBaseUrl, LoadingDataUtils.WorkingDataUrl.EndPoints.CHECK_TASK, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    DbUtils.setCheckItem(this, taskId, position, checked);
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in checkitem() in ActionService");
            e.printStackTrace();
        }
    }

    private void completeTask(Intent intent) {
        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String taskName = intent.getStringExtra(ExtraKey.TASK_NAME);

        Intent broadcastIntent = new Intent(Action.COMPLETE_TASK);
        broadcastIntent.putExtra(ExtraKey.TASK_NAME, taskName);

        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            HashMap<String, String> bodies = new HashMap<>();
            bodies.put("td", taskId);

            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl, LoadingDataUtils.WorkingDataUrl.EndPoints.COMPLETE_TASK, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    broadcastIntent.putExtra(ExtraKey.ACTION_SUCCESSFUL, true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                    return;
                }
            }
        }  catch (JSONException e) {
            broadcastIntent.putExtra(ExtraKey.ACTION_SUCCESSFUL, false);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

            Log.e(TAG, "Exception in CompleteTaskForWorkerStrategy() in ActionService");
            e.printStackTrace();

            return;
        }

        broadcastIntent.putExtra(ExtraKey.ACTION_SUCCESSFUL, false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private void loadCasesAndWorkers(Intent intent) {
        loadCases();

        if (intent.getBooleanExtra(ExtraKey.BOOLEAN_LOAD_WORKERS, false)) {
            loadWorkers();
        }
    }

    private void loadCases() {
        if (RestfulUtils.isConnectToInternet(this)) {
            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                        LoadingDataUtils.WorkingDataUrl.EndPoints.LOAD_CASES, null);
                String caseJsonListString =
                        RestfulUtils.restfulGetRequest(urlString, headers);
                if (TextUtils.isEmpty(caseJsonListString)) {
                    Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
                    return;
                }

                JSONArray caseJsonList = new JSONObject(caseJsonListString).getJSONArray("result");

                if (caseJsonList != null) {
                    ArrayList<CaseInDb> casesFromDb = new ArrayList<>();
                    ArrayList<Case> casesFromServer = new ArrayList<>();
                    Map<String, CaseInDb> casesFromDbMap = new HashMap<>();
                    Map<String, Case> casesFromServerMap = new HashMap<>();

                    getCasesFromDb(casesFromDb, casesFromDbMap);
                    getCasesFromServer(caseJsonList, casesFromServer, casesFromServerMap);

                    insertAndUpdateCasesToDb(casesFromDbMap, casesFromServer);
                    deleteCasesFromDb(casesFromServerMap, casesFromDb);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }
    }

    private void loadWorkers() {
        for (Case aCase : DbUtils.getCases(this)) {
            loadWorkerById(aCase.ownerId);

            for (String workerId : aCase.workerIdList) {
                loadWorkerById(workerId);
            }
        }
    }

    private void loadWorkerById(String workerId) {
        if (RestfulUtils.isConnectToInternet(this)) {
            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                HashMap<String, String> queries = new HashMap<>();
                queries.put("employeeId", workerId);

                String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                        LoadingDataUtils.WorkingDataUrl.EndPoints.WORKER, queries);
                String workerJsonString =
                        RestfulUtils.restfulGetRequest(urlString, headers);
                if (TextUtils.isEmpty(workerJsonString)) {
                    Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
                    return;
                }

                JSONObject workerJson = new JSONObject(workerJsonString).getJSONObject("result");

                if (workerJson != null) {
                    if (WorkingData.getInstance(this).hasWorker(workerId)) return;
                    LoadingDataUtils.addWorkerToWorkingData(this, workerJson);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }
    }

    private void getCasesFromDb(ArrayList<CaseInDb> taskList, Map<String, CaseInDb> taskMap) {
        String[] projection = new String[] {
                WorkFlowContract.Case.CASE_ID,
                WorkFlowContract.Case.UPDATED_TIME
        };
        int CASE_ID = 0;
        int UPDATED_TIME = 1;

        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(WorkFlowContract.Case.CONTENT_URI, projection, null, null, null);

            if (cursor != null && cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    CaseInDb caseInDb = new CaseInDb(cursor.getString(CASE_ID), cursor.getLong(UPDATED_TIME));
                    taskList.add(caseInDb);
                    taskMap.put(caseInDb.caseId, caseInDb);
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void getCasesFromServer(JSONArray caseJsonList, ArrayList<Case> caseList, Map<String, Case> caseMap) {
        for (int i = 0 ; i < caseJsonList.length() ; i++) {
            try {
                JSONObject jsonObject = caseJsonList.getJSONObject(i);
                Case aCase = Case.retrieveCaseFromJson(jsonObject);

                caseList.add(aCase);
                caseMap.put(aCase.id, aCase);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertAndUpdateCasesToDb(Map<String, CaseInDb> casesFromDbMap, List<Case> casesFromServer) {
        for (Case aCase : casesFromServer) {
            if (!casesFromDbMap.containsKey(aCase.id)) {
                insertCaseToDb(aCase);

            } else {
                //if (aCase.lastUpdatedTime <= casesFromDbMap.get(aCase.id).updatedTime) continue;

                updateCaseToDb(aCase);
            }
        }
    }

    private void deleteCasesFromDb(Map<String, Case> casesFromServerMap, ArrayList<CaseInDb> casesFromDb) {
        for (CaseInDb caseInDb : casesFromDb) {
            if (!casesFromServerMap.containsKey(caseInDb.caseId)) {
                deleteCaseFromDb(caseInDb.caseId);
            }
        }
    }

    private void insertCaseToDb(Case aCase) {
        getContentResolver().insert(WorkFlowContract.Case.CONTENT_URI, convertCaseToContentValues(aCase));
    }

    private void updateCaseToDb(Case aCase) {
        String selection =  WorkFlowContract.Case.CASE_ID + " = ?";
        String[] selectionArgs = new String[] {aCase.id};

        getContentResolver().update(WorkFlowContract.Case.CONTENT_URI,
                convertCaseToContentValues(aCase), selection, selectionArgs);
    }

    private void deleteCaseFromDb(String caseId) {
        String selection =  WorkFlowContract.Case.CASE_ID + " = ?";
        String[] selectionArgs = new String[] {caseId};

        getContentResolver().delete(WorkFlowContract.Case.CONTENT_URI, selection, selectionArgs);
    }

    private ContentValues convertCaseToContentValues(Case aCase) {
        ContentValues values = new ContentValues();

        values.put(WorkFlowContract.Case.CASE_ID, aCase.id);
        values.put(WorkFlowContract.Case.CASE_NAME, aCase.name);
        values.put(WorkFlowContract.Case.OWNER_ID, aCase.ownerId);
        values.put(WorkFlowContract.Case.WORKER_IDS, Utils.packStrings(aCase.workerIdList));
        values.put(WorkFlowContract.Case.DESCRIPTION, aCase.description);
        values.put(WorkFlowContract.Case.IS_COMPLETED, aCase.isCompleted);
        values.put(WorkFlowContract.Case.UPDATED_TIME, aCase.lastUpdatedTime);

        return values;
    }

    private void createCase(Intent intent) {
        String caseName = intent.getStringExtra(ExtraKey.CASE_NAME);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());
        headers.put("cname", caseName);

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("cname", caseName);

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.CREATE_CASE, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    startService(generateLoadCasesAndWorkersIntent(this, false));
                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createTask(Intent intent) {
        String taskName = intent.getStringExtra(ExtraKey.TASK_NAME);
        String caseId = intent.getStringExtra(ExtraKey.CASE_ID);
        String workerId = intent.getStringExtra(ExtraKey.WORKER_ID);
        long dueDate = intent.getLongExtra(ExtraKey.TASK_DUEDATE, -1L);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("cd", caseId);
        bodies.put("tname", taskName);
        if (!TextUtils.isEmpty(workerId)) {
            bodies.put("ed", workerId);
        }
        if (dueDate != -1L) {
            bodies.put("dueDate", String.valueOf(dueDate));
        }

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.CREATE_TASK, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    DbUtils.insertTaskToDb(this, Task.retrieveTaskFromJson(jsonObject.getJSONObject("result")));
                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateTaskDescription(Intent intent) {
        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String taskDescription = intent.getStringExtra(ExtraKey.TASK_DESCRIPTION);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("td", taskId);
        bodies.put("tdesc", taskDescription);

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.TASK_UPDATE_DESCRIPTION, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    DbUtils.updateTaskDescription(this,  taskId, taskDescription);
                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateTaskDueDate(Intent intent) {
        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        long taskDueDate = intent.getLongExtra(ExtraKey.TASK_DUEDATE, -1L);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("td", taskId);
        bodies.put("ddate", String.valueOf(taskDueDate));

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.TASK_UPDATE_DUEDATE, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    DbUtils.updateTaskDueDate(this,  taskId, taskDueDate);
                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateCaseDescription(Intent intent) {
        String caseId = intent.getStringExtra(ExtraKey.CASE_ID);
        String caseDescription = intent.getStringExtra(ExtraKey.CASE_DESCRIPTION);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("cd", caseId);
        bodies.put("cdesc", caseDescription);

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.CASE_UPDATE_DESCRIPTION, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);

                if (jsonObject.getString("status").equals("success")) {
                    DbUtils.updateCaseDescription(this,  caseId, caseDescription);
                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addWorkerToCase(Intent intent) {
        Intent broadcastIntent = new Intent(GeneralCompletedReceiver.ACTION_GENERAL_COMPLETED);

        String caseId = intent.getStringExtra(ExtraKey.CASE_ID);
        String workerEmail = intent.getStringExtra(ExtraKey.WORKER_EMAIL);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("cd", caseId);
        bodies.put("email", workerEmail);

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.ADD_WORKER_TO_CASE, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);

                if (jsonObject.getString("status").equals("success")) {
                    broadcastIntent.putExtra(ExtraKey.ACTION_SUCCESSFUL, true);

                    String workerId = jsonObject.getJSONObject("result").getString("employeeId");
                    Case aCase = DbUtils.getCaseById(this, caseId);
                    aCase.workerIdList.add(workerId);

                    loadWorkerById(workerId);
                    DbUtils.updateCaseWorkers(this, caseId, Utils.packStrings(aCase.workerIdList));
                    Utils.showToastInNonUiThread(mHandler, this, getString(R.string.add_user_successfully));

                } else {
                    broadcastIntent.putExtra(ExtraKey.ACTION_SUCCESSFUL, false);
                    Utils.showToastInNonUiThread(mHandler, this, getString(R.string.can_not_find_user));
                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
            Utils.showInternetConnectionWeakToast(this);
            broadcastIntent.putExtra(ExtraKey.ACTION_SUCCESSFUL, false);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private void registerUser(Intent intent) {
        Intent broadcastIntent = new Intent(GeneralCompletedReceiver.ACTION_GENERAL_COMPLETED);

        String email = intent.getStringExtra(ExtraKey.REGISTER_USER_EMAIL);
        String password = intent.getStringExtra(ExtraKey.REGISTER_USER_PASSWORD);
        String name = intent.getStringExtra(ExtraKey.REGISTER_USER_NAME);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("name", name);
        bodies.put("email", email);
        bodies.put("password", password);

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.REGISTER_USER, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);

                if (jsonObject.getString("status").equals("success")) {
                    broadcastIntent.putExtra(ExtraKey.ACTION_SUCCESSFUL, true);
                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
            Utils.showInternetConnectionWeakToast(this);
            broadcastIntent.putExtra(ExtraKey.ACTION_SUCCESSFUL, false);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
}
