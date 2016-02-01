package com.nicloud.workflowclient.utility.utils;

import android.content.Context;
import android.util.Log;

import com.nicloud.workflowclient.data.data.data.Case;
import com.nicloud.workflowclient.data.data.data.Worker;
import com.nicloud.workflowclient.data.data.data.WorkingData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Utility for loading data from server
 *
 * @author Danny Lin
 * @since 2015/9/23.
 */
public class LoadingDataUtils {

    private static final String TAG = "LoadDataUtils";

    public static String sBaseUrl = "http://52.26.71.101";

    public static final class WorkingDataUrl {

        public static final String WORKERS = sBaseUrl + "/api/employees";
        public static final String CASES = sBaseUrl + "/api/cases";
        public static final String TASK_BY_ID = sBaseUrl + "/api/task?taskId=";
        public static final String TASKS_BY_WORKER = sBaseUrl + "/api/employee/tasks?employeeId=";

        public static final class EndPoints {
            public static final String LOGIN_WORKER = "/api/self";
            public static final String TASKS = "/api/tasks";
            public static final String CHECK_TASK = "/api/v2/check-task-todo";
            public static final String MESSAGES = "/api/chatting/employee";

            public static final String CASE_FILES = "/api/case-attachment";
            public static final String CASE_UPLOAD_FILE = "/api/case-attachment/file";
            public static final String CASE_UPLOAD_IMAGE = "/api/case-attachment/image";

            public static final String DISCUSSION = "/api/case-message";
            public static final String SEND_DISCUSSION_MESSAGE = "/api/case-message/text";
            public static final String SEND_DISCUSSION_IMAGE = "/api/case-message/image";
            public static final String SEND_DISCUSSION_FILE = "/api/case-message/file";

            public static final String WORKER_ACTIVITIES = "/api/employee/activities";
            public static final String TASK_ACTIVITIES = "/api/task/activities";
            public static final String TASK_WARNING_ACTIVITIES = "/api/task-exception/activities";
            public static final String COMPLETE_TASK = "/api/v2/complete-task/";

            public static final String COMMENT_IMAGE_ACTIVITY_TO_TASK = "/api/add-task-activity/image";
            public static final String COMMENT_FILE_ACTIVITY_TO_TASK = "/api/add-task-activity/file";
            public static final String COMMENT_TEXT_ACTIVITY_TO_TASK = "/api/add-task-activity/text";
            public static final String ADD_CHECK_ITEM_TO_TASK = "/api/v2/task-todo";

            public static final String LOGIN_STATUS = "/api/login-status";
            public static final String LOGIN = "/api/login";

            public static final String CHECKIN_OUT = "/api/checkin-out/employee";
        }
    }

    /**
     * Load all cases data from server.
     *
     * @param context
     */
    public static void loadCases(Context context) {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            String caseJsonListString = RestfulUtils.restfulGetRequest(WorkingDataUrl.CASES, headers);
            JSONArray caseJsonList = new JSONObject(caseJsonListString).getJSONArray("result");

            for (int i = 0; i < caseJsonList.length(); i++) {
                JSONObject caseJson = caseJsonList.getJSONObject(i);
                addCaseToWorkingData(context, caseJson);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Exception in loadCases()");
            e.printStackTrace();
        }
    }

    /**
     * Load all workers data from server.
     *
     * @param context
     */
    public static void loadWorkers(Context context) {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            String workerJsonListString = RestfulUtils.restfulGetRequest(WorkingDataUrl.WORKERS, headers);
            JSONArray workerJsonList = new JSONObject(workerJsonListString).getJSONArray("result");

            for (int i = 0; i < workerJsonList.length(); i++) {
                JSONObject workerJson = workerJsonList.getJSONObject(i);
                addWorkerToWorkingData(context, workerJson);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Exception in loadWorkers()");
            e.printStackTrace();
        }
    }

    private static void addCaseToWorkingData(Context context, JSONObject caseJson) {
        try {
            String caseId = caseJson.getString("_id");
            long lastUpdatedTime = caseJson.getLong("updatedAt");
            boolean hasCase = WorkingData.getInstance(context).hasCase(caseId);

            if (hasCase &&
                    WorkingData.getInstance(context).getCaseById(caseId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (hasCase) {
                WorkingData.getInstance(context).updateCase(caseId, retrieveCaseFromJson(caseJson));
            } else {
                WorkingData.getInstance(context).addCase(retrieveCaseFromJson(caseJson));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Exception in addCaseToWorkingData()");
            e.printStackTrace();
        }
    }

    private static void addWorkerToWorkingData(Context context, JSONObject workerJson) {
        try {
            String workerId = workerJson.getString("_id");
            long lastUpdatedTime = workerJson.getLong("updatedAt");
            boolean workingDataHasWorker = WorkingData.getInstance(context).hasWorker(workerId);

            if (workingDataHasWorker &&
                    WorkingData.getInstance(context).getWorkerById(workerId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (workingDataHasWorker) {
                WorkingData.getInstance(context).updateWorker(workerId, retrieveWorkerFromJson(context, workerJson));
            } else {
                WorkingData.getInstance(context).addWorker(retrieveWorkerFromJson(context, workerJson));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Exception in addWorkerToWorkingData()");
            e.printStackTrace();
        }
    }

    private static Case retrieveCaseFromJson(JSONObject caseJson) {
        try {
            String id = caseJson.getString("_id");
            String name = caseJson.getString("name");
            boolean isCompleted = caseJson.getBoolean("completed");
            long updatedAt = caseJson.getLong("updatedAt");

            return new Case(
                    id,
                    name,
                    isCompleted,
                    updatedAt);

        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveCaseFromJson()");
            e.printStackTrace();
        }

        return null;
    }

    public static Worker retrieveWorkerFromJson(Context context, JSONObject workerJson) {
        try {
            String id = workerJson.getString("_id");
            String name = workerJson.getJSONObject("profile").getString("name");
            String departmentId = JsonUtils.getStringFromJson(workerJson, "groupId");
            String departmentName = JsonUtils.getStringFromJson(workerJson, "groupName");
            String address = JsonUtils.getStringFromJson(workerJson, "address");
            String phone = JsonUtils.getStringFromJson(workerJson, "phone");
            String avatarUrl = JsonUtils.getStringFromJson(workerJson, "iconThumbUrl");

            long lastUpdatedTime = workerJson.getLong("updatedAt");

            return new Worker(
                    id,
                    name,
                    departmentId,
                    departmentName,
                    address,
                    phone,
                    avatarUrl,
                    lastUpdatedTime);

        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveWorkerFromJson()");
            e.printStackTrace();
        }

        return null;
    }
}
