package com.nicloud.workflowclient.data.connectserver;

import android.content.Context;
import android.util.Log;

import com.nicloud.workflowclient.data.data.data.Case;
import com.nicloud.workflowclient.data.data.data.CheckItem;
import com.nicloud.workflowclient.data.data.data.Task;
import com.nicloud.workflowclient.data.data.data.Worker;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.utility.RestfulUtils;
import com.nicloud.workflowclient.utility.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
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

        //public static final String BASE_URL = "http://128.199.198.169:3000";
        // public static final String BASE_URL = "http://188.166.235.141";  // Released version
        public static final String DEBUG_BASE_URL = "http://128.199.198.169:3000";

        public static final String WORKERS = sBaseUrl + "/api/employees";
        public static final String CASES = sBaseUrl + "/api/cases";
        public static final String FACTORIES = sBaseUrl + "/api/groups";
        public static final String EQUIPMENTS = sBaseUrl + "/api/resources";
        public static final String TASKS_BY_CASE = sBaseUrl + "/api/tasks?caseId=";
        public static final String TASK_BY_ID = sBaseUrl + "/api/task?taskId=";
        public static final String TASKS_BY_WORKER = sBaseUrl + "/api/employee/tasks?employeeId=";
        public static final String WORKERS_BY_FACTORY = sBaseUrl + "/api/group/employees?groupId=";
        public static final String TIME_CARD_BY_CASE = sBaseUrl + "/api/case/task-timecards?caseId=%s&startDate=%d&endDate=%d";
        public static final String TIME_CARD_BY_WORKER = sBaseUrl + "/api/employee/timecards?employeeId=%s&startDate=%d&endDate=%d";
        public static final String LEAVE_WORKERS = sBaseUrl + "/api/leaves?startDate=%s&endDate=%s";
        public static final String WORKER_ATTENDANCE = sBaseUrl + "/api/employee/leaves?employeeId=%s&startDate=%d&endDate=%d";

        public static final class EndPoints {
            public static final String LOGIN_WORKER = "/api/self";
            public static final String SHIFT_TASK = "/api/v2/shift-task";
            public static final String PAUSE_TASK = "/api/v2/suspend-task";
            public static final String CHECK_TASK = "/api/v2/check-task-todo";
            public static final String EMPLOYEES = "/api/employees";
            public static final String WARNINGS = "/api/exceptions";
            public static final String TASK_WARNINGS = "/api/task-exceptions";
            public static final String MESSAGES = "/api/chatting/employee";

            public static final String DISCUSSION = "/api/case-message";
            public static final String SEND_DISCUSSION_MESSAGE = "/api/case-message/text";
            public static final String SEND_DISCUSSION_IMAGE = "/api/case-message/image";
            public static final String SEND_DISCUSSION_FILE = "/api/case-message/file";

            public static final String WORKER_ACTIVITIES = "/api/employee/activities";
            public static final String TASK_ACTIVITIES = "/api/task/activities";
            public static final String TASK_WARNING_ACTIVITIES = "/api/task-exception/activities";
            public static final String DISPATCH = "/api/dispatch";
            public static final String COMPLETE_TASK = "/api/v2/complete-task/";
            public static final String SCORE_EMPLOYEE = "/api/score/employee";
            public static final String INCREMENT_TASK_ALERT = "/api/increment-task-alert";
            public static final String PASS_TASK = "/api/pass-task";
            public static final String FAIL_TASK = "/api/fail-task";

            public static final String CREATE_TASK_EXCEPTION = "/api/add-task-exception";
            public static final String CLOSE_TASK_EXCEPTION = "/api/close-task-exception";

            public static final String COMMENT_IMAGE_ACTIVITY = "/api/add-activity/image";
            public static final String COMMENT_FILE_ACTIVITY = "/api/add-activity/file";
            public static final String COMMENT_TEXT_ACTIVITY = "/api/add-activity/text";

            public static final String COMMENT_IMAGE_ACTIVITY_TO_TASK = "/api/add-task-activity/image";
            public static final String COMMENT_FILE_ACTIVITY_TO_TASK = "/api/add-task-activity/file";
            public static final String COMMENT_TEXT_ACTIVITY_TO_TASK = "/api/add-task-activity/text";
            public static final String ADD_CHECK_ITEM_TO_TASK = "/api/v2/task-todo";

            public static final String COMMENT_IMAGE_ACTIVITY_TO_TASK_WARNING = "/api/add-task-exception-activity/image";
            public static final String COMMENT_FILE_ACTIVITY_TO_TASK_WARNING = "/api/add-task-exception-activity/file";
            public static final String COMMENT_TEXT_ACTIVITY_TO_TASK_WARNING = "/api/add-task-exception-activity/text";

            public static final String LOGIN_STATUS = "/api/login-status";
            public static final String LOGIN = "/api/login";

            public static final String TIME_CARD_BY_RESOURCE = "/api/resource/timecards";

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

//    /**
//     * Load all factories data from server, not include workers data.
//     * We only load worker id of each worker in the factory.
//     *
//     * @param context
//     */
//    public static void loadFactories(Context context) {
//        try {
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String factoryJsonListString = RestfulUtils.restfulGetRequest(WorkingDataUrl.FACTORIES, headers);
//            JSONArray factoryJsonList = new JSONObject(factoryJsonListString).getJSONArray("result");
//
//            for (int i = 0 ; i < factoryJsonList.length() ; i++) {
//                JSONObject factoryJson = factoryJsonList.getJSONObject(i);
//                addFactoryToWorkingData(context, factoryJson);
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in loadFactories()");
//            e.printStackTrace();
//        }
//    }
//    /**
//     * Load all equipments data from server, not include workers data.
//     *
//     * @param context
//     */
//    public static void loadEquipments(Context context) {
//        try {
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String resourceJsonListString = RestfulUtils.restfulGetRequest(WorkingDataUrl.EQUIPMENTS, headers);
//            JSONArray equipmentJsonList = new JSONObject(resourceJsonListString).getJSONArray("result");
//
//            for (int i = 0 ; i < equipmentJsonList.length() ; i++) {
//                JSONObject equipmentJson = equipmentJsonList.getJSONObject(i);
//                addEquipmentToWorkingData(context, equipmentJson);
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in loadFactories()");
//            e.printStackTrace();
//        }
//    }
//    /**
//     * Load all warning data from server
//     *
//     * @param context
//     */
//    public static void loadWarnings(Context context) {
//        try {
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String urlString = URLUtils.buildURLString(WorkingDataUrl.BASE_URL, WorkingDataUrl.EndPoints.WARNINGS, null);
//
//            String resourceJsonListString = RestfulUtils.restfulGetRequest(urlString, headers);
//            JSONArray warningJsonList = new JSONObject(resourceJsonListString).getJSONArray("result");
//
//            for (int i = 0 ; i < warningJsonList.length() ; i++) {
//                JSONObject warningJson = warningJsonList.getJSONObject(i);
//                addWarningToWorkingData(context, warningJson);
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in loadFactories()");
//            e.printStackTrace();
//        }
//    }
//    /**
//     * Load all leave workers data from server.
//     *
//     * @param context
//     */
//    public static void loadLeaveWorkers(Context context, long startDate, long endDate) {
//        try {
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String leaveWorkerJsonListString = RestfulUtils.restfulGetRequest(getLeaveWorkersUrl(startDate, endDate), headers);
//            JSONArray leaveWorkerJsonList = new JSONObject(leaveWorkerJsonListString).getJSONArray("result");
//
//            for (int i = 0 ; i < leaveWorkerJsonList.length() ; i++) {
//                JSONObject leaveWorkerJson = leaveWorkerJsonList.getJSONObject(i);
//                addLeaveWorkerToWorkingData(context, leaveWorkerJson);
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in loadLeaveWorkers()");
//            e.printStackTrace();
//        }
//    }
//
//
//    public static void loadTasksByCase(Context context, String caseId) {
//        if (!WorkingData.getInstance(context).hasCase(caseId)) return;
//
//        try {
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String taskJsonListString = RestfulUtils.restfulGetRequest(getTasksByCaseUrl(caseId), headers);
//            JSONArray taskJsonList = new JSONObject(taskJsonListString).getJSONArray("result");
//            List<Task> newCaseTasks = new ArrayList<>();
//
//            for (int i = 0 ; i < taskJsonList.length() ; i++) {
//                JSONObject taskJson = taskJsonList.getJSONObject(i);
//                String taskId = taskJson.getString("_id");
//
//                addTaskToWorkingData(context, taskJson);
//                if (WorkingData.getInstance(context).hasTask(taskId)) {
//                    newCaseTasks.add(WorkingData.getInstance(context).getTaskById(taskId));
//                }
//            }
//
//            WorkingData.getInstance(context).getCaseById(caseId).tasks = newCaseTasks;
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void loadWorkersByFactory(Context context, String departmentId) {
//        if (!WorkingData.getInstance(context).hasFactory(departmentId)) return;
//
//        try {
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String workerJsonString = RestfulUtils.restfulGetRequest(getWorkersByFactoryUrl(departmentId), headers);
//            JSONArray workerJsonList = new JSONObject(workerJsonString).getJSONArray("result");
//            List<Worker> newWorkers = new ArrayList<>();
//
//            for (int i = 0 ; i < workerJsonList.length() ; i++) {
//                JSONObject workerJson = workerJsonList.getJSONObject(i);
//                String workerId = workerJson.getString("_id");
//
//                addWorkerToWorkingData(context, workerJson);
//                if (WorkingData.getInstance(context).hasWorker(workerId)) {
//                    newWorkers.add(WorkingData.getInstance(context).getWorkerById(workerId));
//                }
//            }
//
//            WorkingData.getInstance(context).getFactoryById(departmentId).workers = newWorkers;
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void loadEmployees(Context context) {
//        try {
//            String urlString = URLUtils.buildURLString(WorkingDataUrl.BASE_URL, WorkingDataUrl.EndPoints.EMPLOYEES, null);
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String employeeJsonString = RestfulUtils.restfulGetRequest(urlString, headers);
//            JSONArray employeeJsonList = new JSONObject(employeeJsonString).getJSONArray("result");
//
//            WorkingData instance = WorkingData.getInstance(context);
//
//            for (int i = 0 ; i < employeeJsonList.length() ; i++) {
//                JSONObject workerJson = employeeJsonList.getJSONObject(i);
//                String workerId = workerJson.getString("_id");
//                String role = workerJson.getString("role");
//
//                if (role.equals("operator")) {
//                    addWorkerToWorkingData(context, workerJson);
//                    // [TODO] should move worker list out from factory, retrieve workers from WorkingData by adding a method:
//                    // getWorkersByFactoryId
//
//                    Factory factory = instance.getFactoryById(workerJson.getString("groupId"));
//                    Worker worker = instance.getWorkerById(workerId);
//                    if (!factory.workers.contains(worker)) {
//                        factory.workers.add(worker);
//                    }
//                } else {
//                    addManagerToWorkingData(context, workerJson);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
    public static void loadTasksByWorker(Context context, String workerId) {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            String taskJsonString = RestfulUtils.restfulGetRequest(getTasksByWorkerUrl(workerId), headers);

            JSONObject taskJson = new JSONObject(taskJsonString).getJSONObject("result");
            JSONArray scheduledTaskJsonList = JsonUtils.getJsonArrayFromJson(taskJson, "scheduledTasks");

            WorkingData.getInstance(context).clearTasks();
            if (scheduledTaskJsonList != null) {
                for (int i = 0; i < scheduledTaskJsonList.length(); i++) {
                    JSONObject scheduledTaskJson = scheduledTaskJsonList.getJSONObject(i);
                    WorkingData.getInstance(context).addTask(retrieveTaskFromJson(context, scheduledTaskJson));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void loadTaskById(Context context, String taskId) {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            String taskJsonString = RestfulUtils.restfulGetRequest(getTaskByIdUrl(taskId), headers);

            JSONObject taskJson = new JSONObject(taskJsonString).getJSONObject("result");

            if (taskJson == null) return;

            WorkingData.getInstance(context).getTask(taskId).update(retrieveTaskFromJson(context, taskJson));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//
//
//    public static void loadTimeCardsByCase(Context context, String caseId, long startDate, long endDate) {
//        if (TextUtils.isEmpty(caseId) || startDate < 0 || endDate < 0 || endDate < startDate)
//            throw new IllegalArgumentException("loadTimeCardsByCase invalid parameter" +
//                    ", caseId: " + caseId +
//                    ", startDate: " + startDate +
//                    ", endDate: " + endDate);
//        if (!WorkingData.getInstance(context).hasCase(caseId)) return;
//
//        try {
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String jsonString = RestfulUtils.restfulGetRequest(getCaseTimeCardUrl(caseId, startDate, endDate), headers);
//            if (!new JSONObject(jsonString).getString("status").equals("success")) return;
//            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("result");
//            Case aCase = WorkingData.getInstance(context).getCaseById(caseId);
//            if (aCase == null) return;
//            for (int i = 0 ; i < jsonArray.length() ; i++) {
//                JSONObject jsonObj = jsonArray.getJSONObject(i);
//                CaseTimeCard timeCard = null;
//                try {
//                    timeCard = new CaseTimeCard(
//                            jsonObj.getString("_id"),
//                            jsonObj.getString("caseId"),
//                            jsonObj.getString("taskId"),
//                            jsonObj.getString("employeeId"),
//                            jsonObj.getLong("startDate"),
//                            jsonObj.getLong("endDate"),
//                            jsonObj.getString("status").equals("close") ?
//                                    CaseTimeCard.STATUS.CLOSE :
//                                    CaseTimeCard.STATUS.OPEN,
//                            jsonObj.getLong("createdAt"),
//                            jsonObj.getLong("updatedAt"));
//                } catch (Exception e) {
//                    Log.e(TAG, "loadTimeCardsByCase parse json string fail.");
//                }
//                if (timeCard == null || !timeCard.caseId.equals(caseId)) continue;
//                synchronized (aCase) {
//                    if (aCase.timeCards.containsKey(timeCard.id)) {
//                        if (aCase.timeCards.get(timeCard.id).updatedDate < timeCard.updatedDate) {
//                            aCase.timeCards.get(timeCard.id).updatedDate = timeCard.updatedDate;
//                            aCase.timeCards.get(timeCard.id).endDate = timeCard.endDate;
//                            aCase.timeCards.get(timeCard.id).status = timeCard.status;
//                        } else {
//                            // ignore
//                        }
//                    } else {
//                        aCase.timeCards.put(timeCard.id, timeCard);
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void loadTimeCardsByWorker(Context context, String workerId, long startDate, long endDate) {
//        if (TextUtils.isEmpty(workerId) || startDate < 0 || endDate < 0 || endDate < startDate)
//            throw new IllegalArgumentException("loadTimeCardsByWorker invalid parameter" +
//                    ", workerId: " + workerId +
//                    ", startDate: " + startDate +
//                    ", endDate: " + endDate);
//        if (!WorkingData.getInstance(context).hasWorker(workerId)) return;
//
//        try {
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String jsonString = RestfulUtils.restfulGetRequest(getWorkerTimeCardUrl(workerId, startDate, endDate), headers);
//            if (!new JSONObject(jsonString).getString("status").equals("success")) return;
//            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("result");
//            for (int i = 0 ; i < jsonArray.length() ; i++) {
//                JSONObject jsonObj = jsonArray.getJSONObject(i);
//                Worker worker = WorkingData.getInstance(context).getWorkerById(jsonObj.getString("employeeId"));
//                if (worker == null) continue;
//                WorkerTimeCard timeCard = null;
//                try {
//                    timeCard = new WorkerTimeCard(
//                            jsonObj.getString("_id"),
//                            jsonObj.getString("employeeId"),
//                            jsonObj.getLong("startDate"),
//                            jsonObj.getLong("endDate"),
//                            jsonObj.getString("status").equals("close") ?
//                                    CaseTimeCard.STATUS.CLOSE :
//                                    CaseTimeCard.STATUS.OPEN,
//                            jsonObj.getLong("createdAt"),
//                            jsonObj.getLong("updatedAt"));
//                } catch (Exception e) {
//                    Log.e(TAG, "loadTimeCardsByCase parse json string fail.");
//                }
//                if (timeCard == null) continue;
//                if (!timeCard.employeeId.equals(worker.id)) continue;
//                synchronized (worker) {
//                    if (worker.timeCards.containsKey(timeCard.id)) {
//                        if (worker.timeCards.get(timeCard.id).updatedDate < timeCard.updatedDate) {
//                            worker.timeCards.get(timeCard.id).updatedDate = timeCard.updatedDate;
//                            worker.timeCards.get(timeCard.id).endDate = timeCard.endDate;
//                            worker.timeCards.get(timeCard.id).status = timeCard.status;
//                        } else {
//                            // ignore
//                        }
//                    } else {
//                        worker.timeCards.put(timeCard.id, timeCard);
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void loadTimeCardsByEquipment(Context context, String equipmentId, long startDate, long endDate) {
//        if (TextUtils.isEmpty(equipmentId) || startDate < 0 || endDate < 0 || endDate < startDate)
//            throw new IllegalArgumentException("loadTimeCardsByEquipment invalid parameter" +
//                    ", equipmentId: " + equipmentId +
//                    ", startDate: " + startDate +
//                    ", endDate: " + endDate);
//        if (!WorkingData.getInstance(context).hasEquipment(equipmentId)) return;
//
//        try {
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String jsonString = RestfulUtils.restfulGetRequest(getEquipmentTimeCardUrl(equipmentId, startDate, endDate), headers);
//            if (!new JSONObject(jsonString).getString("status").equals("success")) return;
//            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("result");
//            for (int i = 0 ; i < jsonArray.length() ; i++) {
//                JSONObject jsonObj = jsonArray.getJSONObject(i);
//                Equipment equipment = WorkingData.getInstance(context).getEquipmentById(jsonObj.getString("resourceId"));
//                if (equipment == null) continue;
//                EquipmentTimeCard timeCard = null;
//                try {
//                    timeCard = new EquipmentTimeCard(
//                            jsonObj.getString("_id"),
//                            jsonObj.getString("resourceId"),
//                            jsonObj.getLong("startDate"),
//                            jsonObj.getLong("endDate"),
//                            jsonObj.getString("status").equals("close") ?
//                                    CaseTimeCard.STATUS.CLOSE :
//                                    CaseTimeCard.STATUS.OPEN,
//                            jsonObj.getLong("createdAt"),
//                            jsonObj.getLong("updatedAt"));
//                } catch (Exception e) {
//                    Log.e(TAG, "loadTimeCardsByEquipment parse json string fail.");
//                }
//                if (timeCard == null) continue;
//                if (!timeCard.equipmentId.equals(equipment.id)) continue;
//                synchronized (equipment) {
//                    if (equipment.timeCards.containsKey(timeCard.id)) {
//                        if (equipment.timeCards.get(timeCard.id).updatedDate < timeCard.updatedDate) {
//                            equipment.timeCards.get(timeCard.id).updatedDate = timeCard.updatedDate;
//                            equipment.timeCards.get(timeCard.id).endDate = timeCard.endDate;
//                            equipment.timeCards.get(timeCard.id).status = timeCard.status;
//                        } else {
//                            // ignore
//                        }
//                    } else {
//                        equipment.timeCards.put(timeCard.id, timeCard);
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
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
//    private static void addWipTaskToWorkingData(Context context, JSONObject wipTaskJson) {
//        try {
//            String taskId = wipTaskJson.getString("_id");
//            long lastUpdatedTime = wipTaskJson.getLong("updatedAt");
//            boolean workingDataHasTask = WorkingData.getInstance(context).hasTask(taskId);
//
//            if (workingDataHasTask &&
//                    WorkingData.getInstance(context).getWipTask().lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            WorkingData.getInstance(context).setWipTask(retrieveTaskFromJson(context, wipTaskJson));
//
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addTaskToWorkingData()");
//            e.printStackTrace();
//        }
//    }
//
//    private static void addScheduledTaskToWorkingData(Context context, JSONObject scheduledTaskJson) {
//        try {
//            String taskId = scheduledTaskJson.getString("_id");
//            long lastUpdatedTime = scheduledTaskJson.getLong("updatedAt");
//            boolean workingDataHasTask = WorkingData.getInstance(context).hasTask(taskId);
//
//            if (workingDataHasTask &&
//                    WorkingData.getInstance(context).get  .lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            WorkingData.getInstance(context).setWipTask(retrieveTaskFromJson(context, scheduledTaskJson));
//
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addTaskToWorkingData()");
//            e.printStackTrace();
//        }
//    }
//    private static void addFactoryToWorkingData(Context context, JSONObject factoryJson) {
//        try {
//            String departmentId = factoryJson.getString("_id");
//            long lastUpdatedTime = factoryJson.getLong("updatedAt");
//            boolean hasFactory = WorkingData.getInstance(context).hasFactory(departmentId);
//
//            if (hasFactory &&
//                    WorkingData.getInstance(context).getFactoryById(departmentId).lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            if (hasFactory) {
//                WorkingData.getInstance(context).updateFactory(departmentId, retrieveFactoryFromJson(context, factoryJson));
//            } else {
//                WorkingData.getInstance(context).addFactory(retrieveFactoryFromJson(context, factoryJson));
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addFactoryToWorkingData()");
//            e.printStackTrace();
//        }
//    }
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
//    private static void addVendorToWorkingData(Context context, JSONObject vendorJson) {
//        try {
//            String vendorId = vendorJson.getString("_id");
//            long lastUpdatedTime = vendorJson.getLong("updatedAt");
//            boolean workingDataHasVendor = WorkingData.getInstance(context).hasVendor(vendorId);
//
//            if (workingDataHasVendor &&
//                    WorkingData.getInstance(context).getVendorById(vendorId).lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            if (workingDataHasVendor) {
//                WorkingData.getInstance(context).updateVendor(vendorId, retrieveVendorFromJson(vendorJson));
//            } else {
//                WorkingData.getInstance(context).addVendor(retrieveVendorFromJson(vendorJson));
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addVendorToWorkingData()");
//            e.printStackTrace();
//        }
//    }
//    private static void addManagerToWorkingData(Context context, JSONObject managerJson) {
//        try {
//            String managerId = managerJson.getString("_id");
//            long lastUpdatedTime = managerJson.getLong("updatedAt");
//            boolean hasManager = WorkingData.getInstance(context).hasManager(managerId);
//
//            if (hasManager &&
//                    WorkingData.getInstance(context).getManagerById(managerId).lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            if (hasManager) {
//                WorkingData.getInstance(context).updateManager(managerId, retrieveManagerFromJson(managerJson));
//            } else {
//                WorkingData.getInstance(context).addManager(retrieveManagerFromJson(managerJson));
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addManagerToWorkingData()");
//            e.printStackTrace();
//        }
//    }
//    private static void addTaskWarningToWorkingData(Context context, JSONObject warningJson) {
//        try {
//            String warningId = warningJson.getString("_id");
//            long lastUpdatedTime = warningJson.getLong("updatedAt");
//            boolean workingDataHasWarning = WorkingData.getInstance(context).hasTaskWarning(warningId);
//
//            if (workingDataHasWarning &&
//                    WorkingData.getInstance(context).getTaskWarningById(warningId).lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            if (workingDataHasWarning) {
//                WorkingData.getInstance(context).updateTaskWarning(warningId, retrieveTaskWarningFromJson(warningJson));
//            } else {
//                WorkingData.getInstance(context).addTaskWarning(retrieveTaskWarningFromJson(warningJson));
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addTaskWarningToWorkingData()");
//            e.printStackTrace();
//        }
//    }
//    private static void addTagToWorkingData(Context context, JSONObject tagJson) {
//        try {
//            String tagId = tagJson.getString("_id");
//            long lastUpdatedTime = tagJson.getLong("updatedAt");
//            boolean workingDataHasTag = WorkingData.getInstance(context).hasTag(tagId);
//
//            if (workingDataHasTag &&
//                    WorkingData.getInstance(context).getTagById(tagId).lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            if (workingDataHasTag) {
//                WorkingData.getInstance(context).updateTag(tagId, retrieveTagFromJson(tagJson));
//            } else {
//                WorkingData.getInstance(context).addTag(retrieveTagFromJson(tagJson));
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addTagToWorkingData()");
//            e.printStackTrace();
//        }
//    }
//    private static void addEquipmentToWorkingData(Context context, JSONObject equipmentJson) {
//        try {
//            String equipmentId = equipmentJson.getString("_id");
//            long lastUpdatedTime = equipmentJson.getLong("updatedAt");
//            boolean hasEquipment = WorkingData.getInstance(context).hasEquipment(equipmentId);
//
//            if (hasEquipment &&
//                    WorkingData.getInstance(context).getEquipmentById(equipmentId).lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            if (hasEquipment) {
//                WorkingData.getInstance(context).updateEquipment(equipmentId, retrieveEquipmentFromJson(equipmentJson));
//            } else {
//                WorkingData.getInstance(context).addEquipment(retrieveEquipmentFromJson(equipmentJson));
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addEquipmentToWorkingData()");
//            e.printStackTrace();
//        }
//    }
//
//    private static void addWarningToWorkingData(Context context, JSONObject warningJson) {
//        WorkingData.getInstance(context).addWarning(retrieveWarningFromJson(warningJson));
//    }
//    private static void addLeaveWorkerToWorkingData(Context context, JSONObject leaveJson) {
//        try {
//            String leaveId = leaveJson.getString("_id");
//            long lastUpdatedTime = leaveJson.getLong("updatedAt");
//            boolean hasLeave = WorkingData.getInstance(context).hasLeave(leaveId);
//
//            if (hasLeave &&
//                    WorkingData.getInstance(context).getLeaveById(leaveId).lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            if (hasLeave) {
//                WorkingData.getInstance(context).updateLeave(leaveId, retrieveLeaveFromJson(leaveJson));
//            } else {
//                WorkingData.getInstance(context).addLeave(retrieveLeaveFromJson(leaveJson));
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addLeaveWorkerToWorkingData()");
//            e.printStackTrace();
//        }
//    }
//
//
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
//    private static Factory retrieveFactoryFromJson(Context context, JSONObject factoryJson) {
//        try {
//            JSONArray managerJsonList = factoryJson.getJSONArray("managerList");
//
//            String id = factoryJson.getString("_id");
//            String name = factoryJson.getString("name");
//            long lastUpdatedTime = factoryJson.getLong("updatedAt");
//
//            List<Manager> managers = new ArrayList<>();
//            for (int m = 0 ; m < managerJsonList.length() ; m++) {
//                JSONObject managerJson = managerJsonList.getJSONObject(m);
//                String managerId = managerJson.getString("_id");
//
//                addManagerToWorkingData(context, managerJson);
//                managers.add(WorkingData.getInstance(context).getManagerById(managerId));
//            }
//
//            return new Factory(id, name, managers, lastUpdatedTime);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
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
    private static Task retrieveTaskFromJson(Context context, JSONObject taskJson) {
        try {
            //JSONArray warningJsonList = taskJson.getJSONArray("taskExceptions");
            JSONArray checkListJsonList = JsonUtils.getJsonArrayFromJson(taskJson, "todos");
            JSONObject equipmentJson = JsonUtils.getJsonObjectFromJson(taskJson, "resource");
            JSONObject taskTimecardJson = JsonUtils.getJsonObjectFromJson(taskJson, "taskTimecard");

            String id = taskJson.getString("_id");
            String description = JsonUtils.getStringFromJson(taskJson, "description");
            String name = taskJson.getString("name");
            String caseName = taskJson.getString("caseName");
            String caseId = taskJson.getString("caseId");
            String workerId = JsonUtils.getStringFromJson(taskJson, "employeeId");
            String equipmentId = "";

            if (equipmentJson != null) {
                equipmentId = equipmentJson.getString("_id");
                //addEquipmentToWorkingData(context, equipmentJson);
            }

            Task.Status status = Task.convertStringToStatus(taskJson.getString("status"));

            long expectedTime = taskJson.getLong("expectedTime");
            long startTime = 0L;
            if (taskTimecardJson != null) {
                startTime = taskTimecardJson.getLong("startDate");
            }
            long spentTime = taskJson.getLong("spentTime");
            long lastUpdatedTime = taskJson.getLong("updatedAt");

            Date startDate = JsonUtils.getDateFromJson(taskJson, "startDate");
            Date endDate = JsonUtils.getDateFromJson(taskJson, "dueDate");
            Date assignDate = JsonUtils.getDateFromJson(taskJson, "dispatchedDate");

//            List<TaskWarning> taskWarnings = new ArrayList<>();
//            for (int w = 0 ; w < warningJsonList.length() ; w++) {
//                JSONObject warningJson = warningJsonList.getJSONObject(w);
//                String warningId = warningJson.getString("_id");
//
//                addTaskWarningToWorkingData(context, warningJson);
//                taskWarnings.add(WorkingData.getInstance(context).getTaskWarningById(warningId));
//            }

            boolean isDelayed = JsonUtils.getBooleanFromJson(taskJson, "delay");

            ArrayList<CheckItem> checkList = new ArrayList<>();

            if (checkListJsonList != null) {
                for (int i = 0 ;  i < checkListJsonList.length() ; i++) {
                    JSONObject checkItemJson = checkListJsonList.getJSONObject(i);
                    checkList.add(new CheckItem(checkItemJson.getString("name"), checkItemJson.getBoolean("checked")));
                }
            }

            Task task = new Task(
                    id,
                    name,
                    description,
                    caseName,
                    caseId,
                    workerId,
                    equipmentId,
                    status,
                    assignDate,
                    startDate,
                    endDate,
                    expectedTime,
                    startTime,
                    spentTime,
                    lastUpdatedTime,
                    isDelayed,
                    checkList);

            JSONObject scheduledTaskAlert = JsonUtils.getJsonObjectFromJson(taskJson, "scheduledTaskAlert");
            if (scheduledTaskAlert != null) {
                task.nextNotifyTime = scheduledTaskAlert.getLong("willAlertAt");
            }

            return task;

        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveTaskFromJson()");
            e.printStackTrace();
        }

        return null;
    }
//    private static Vendor retrieveVendorFromJson(JSONObject vendorJson) {
//        try {
//            JSONArray caseIdsJson = vendorJson.getJSONArray("caseIds");
//
//            String id = vendorJson.getString("_id");
//            String name = vendorJson.getString("name");
//            String address = getStringFromJson(vendorJson, "address");
//            String phone = getStringFromJson(vendorJson, "phone");
//            long lastUpdatedTime = vendorJson.getLong("updatedAt");
//
//            List<String> caseIds = new ArrayList<>();
//            for (int i = 0 ; i < caseIdsJson.length() ; i++) {
//                caseIds.add(caseIdsJson.getString(i));
//            }
//
//            return new Vendor(id, name, address, phone, caseIds, lastUpdatedTime);
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in retrieveVendorFromJson()");
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//    private static Manager retrieveManagerFromJson(JSONObject managerJson) {
//        try {
//            String id = managerJson.getString("_id");
//            String name = managerJson.getJSONObject("profile").getString("name");
//            long lastUpdatedTime = managerJson.getLong("updatedAt");
//
//            return new Manager(id, name, lastUpdatedTime);
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in retrieveManagerFromJson()");
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//    private static Tag retrieveTagFromJson(JSONObject tagJson) {
//        try {
//            String id = tagJson.getString("_id");
//            String name = tagJson.getString("name");
//            long lastUpdatedTime = tagJson.getLong("updatedAt");
//
//            return new Tag(id, name, lastUpdatedTime);
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in retrieveTagFromJson()");
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//    private static TaskWarning retrieveTaskWarningFromJson(JSONObject warningJson) {
//        try {
//            String id = warningJson.getString("_id");
//            String name = getStringFromJson(warningJson, "name");
//            String caseId = warningJson.getString("caseId");
//            String taskId = warningJson.getString("taskId");
//            String workerId = warningJson.getString("employeeId");
//            String managerId = warningJson.getString("managerId");
//
//            long spentTime = warningJson.getLong("spentTime");
//            long lastUpdatedTime = warningJson.getLong("updatedAt");
//
//            TaskWarning.Status status = TaskWarning.convertStringToStatus(warningJson.getString("status"));
//
//            return new TaskWarning(
//                    id,
//                    name,
//                    caseId,
//                    taskId,
//                    workerId,
//                    managerId,
//                    status,
//                    spentTime,
//                    lastUpdatedTime);
//
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in retrieveTagFromJson()");
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//    private static Equipment retrieveEquipmentFromJson(JSONObject equipmentJson) {
//        try {
//            String id = equipmentJson.getString("_id");
//            String name = equipmentJson.getString("name");
//            String description = getStringFromJson(equipmentJson, "details");
//            String departmentId = equipmentJson.getString("workingGroupId");
//
//            long lastUpdatedTime = equipmentJson.getLong("updatedAt");
//            Date purchasedDate = getDateFromJson(equipmentJson, "purchasedDate");
//
//            Equipment.Status status = Equipment.convertStringToStatus(equipmentJson.getString("status"));
//
//            return new Equipment(
//                    id,
//                    name,
//                    description,
//                    departmentId,
//                    status,
//                    purchasedDate,
//                    lastUpdatedTime);
//
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in retrieveTagFromJson()");
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//    private static LeaveInMainInfo retrieveLeaveFromJson(JSONObject leaveJson) {
//        try {
//            String id = leaveJson.getString("_id");
//            String workerId = leaveJson.getString("employeeId");
//            String description = getStringFromJson(leaveJson, "description");
//
//            LeaveInMainInfo.Type type = LeaveInMainInfo.convertStringToType(leaveJson.getString("type"));
//
//            long from = leaveJson.getLong("from");
//            long to = leaveJson.getLong("to");
//            long lastUpdatedTime = leaveJson.getLong("updatedAt");
//
//            return new LeaveInMainInfo(
//                    id,
//                    workerId,
//                    type,
//                    from,
//                    to,
//                    description,
//                    lastUpdatedTime);
//
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in retrieveLeaveFromJson()");
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//    private static Warning retrieveWarningFromJson(JSONObject warningJson) {
//        try {
//            String id = warningJson.getString("_id");
//            String name = warningJson.getString("name");
//
//            return new Warning(id, name);
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in retrieveWarningFromJson()");
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//    public static WorkerAttendance retrieveWorkerAttendance(JSONObject attendanceJson) {
//        try {
//            String id = attendanceJson.getString("_id");
//            String workerId = attendanceJson.getString("employeeId");
//            String description = getStringFromJson(attendanceJson, "description");
//            String timeRange = attendanceJson.getString("range");
//
//            LeaveInMainInfo.Type type = LeaveInMainInfo.convertStringToType(attendanceJson.getString("type"));
//
//            long from = attendanceJson.getLong("from");
//            long to = attendanceJson.getLong("to");
//
//            return new WorkerAttendance(id, workerId, description, timeRange, type, from, to);
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in retrieveWorkerAttendance()");
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//
//    private static String getTasksByCaseUrl(String caseId) {
//        return WorkingDataUrl.TASKS_BY_CASE + caseId;
//    }
    private static String getTasksByWorkerUrl(String workerId) {
        return WorkingDataUrl.TASKS_BY_WORKER + workerId;
    }
    private static String getTaskByIdUrl(String taskId) {
        return WorkingDataUrl.TASK_BY_ID + taskId;
    }
//    private static String getWorkersByFactoryUrl(String departmentId) {
//        return WorkingDataUrl.WORKERS_BY_FACTORY + departmentId;
//    }
//    private static String getCaseTimeCardUrl(String caseId, long startDate, long endDate) {
//        String url = String.format(WorkingDataUrl.TIME_CARD_BY_CASE, caseId, startDate, endDate);
//        Log.d(TAG, "getCaseTimeCardUrl url = " + url);
//        return url;
//    }
//
//    private static String getWorkerTimeCardUrl(String workerId, long startDate, long endDate) {
//        String url = String.format(WorkingDataUrl.TIME_CARD_BY_WORKER, workerId, startDate, endDate);
//        Log.d(TAG, "getWorkerTimeCardUrl url = " + url);
//        return url;
//    }
//    private static String getEquipmentTimeCardUrl(String equipmentId, long startDate, long endDate) {
//        HashMap<String, String> queries = new HashMap<>();
//        queries.put("resourceId", equipmentId);
//        queries.put("startDate", "" + startDate);
//        queries.put("endDate", "" + endDate);
//        String url = URLUtils.buildURLString(WorkingDataUrl.BASE_URL, WorkingDataUrl.EndPoints.TIME_CARD_BY_RESOURCE, queries);
//        Log.d(TAG, "getEquipmentTimeCardUrl url = " + url);
//        return url;
//    }
//
//    private static String getLeaveWorkersUrl(long startDate, long endDate) {
//        String url = String.format(WorkingDataUrl.LEAVE_WORKERS, startDate, endDate);
//        Log.d(TAG, "getLeaveWorkersUrl url = " + url);
//        return url;
//    }
//
//
}
