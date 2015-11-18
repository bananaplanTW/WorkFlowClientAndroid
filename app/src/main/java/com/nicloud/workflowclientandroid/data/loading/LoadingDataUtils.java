package com.nicloud.workflowclientandroid.data.loading;

import android.content.Context;
import android.util.Log;

import com.nicloud.workflowclientandroid.data.data.Task;
import com.nicloud.workflowclientandroid.data.data.WorkingData;
import com.nicloud.workflowclientandroid.data.utility.RestfulUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public static final class WorkingDataUrl {

        //public static final String BASE_URL = "http://128.199.198.169:3000";
        public static final String BASE_URL = "http://188.166.248.171";
        // public static final String BASE_URL = "http://188.166.235.141";  // Released version
        public static final String DEBUG_BASE_URL = "http://128.199.198.169:3000";

        public static final String WORKERS = BASE_URL + "/api/employees";
        public static final String CASES = BASE_URL + "/api/cases";
        public static final String FACTORIES = BASE_URL + "/api/groups";
        public static final String EQUIPMENTS = BASE_URL + "/api/resources";
        public static final String TASKS_BY_CASE = BASE_URL + "/api/tasks?caseId=";
        public static final String TASKS_BY_WORKER = BASE_URL + "/api/employee/tasks?employeeId=";
        public static final String WORKERS_BY_FACTORY = BASE_URL + "/api/group/employees?groupId=";
        public static final String TIME_CARD_BY_CASE = BASE_URL + "/api/case/task-timecards?caseId=%s&startDate=%d&endDate=%d";
        public static final String TIME_CARD_BY_WORKER = BASE_URL + "/api/employee/timecards?employeeId=%s&startDate=%d&endDate=%d";
        public static final String LEAVE_WORKERS = BASE_URL + "/api/leaves?startDate=%s&endDate=%s";
        public static final String WORKER_ATTENDANCE = BASE_URL + "/api/employee/leaves?employeeId=%s&startDate=%d&endDate=%d";

        public static final class EndPoints {
            public static final String EMPLOYEES = "/api/employees";
            public static final String WARNINGS = "/api/exceptions";
            public static final String TASK_WARNINGS = "/api/task-exceptions";

            public static final String WORKER_ACTIVITIES = "/api/employee/activities";
            public static final String TASK_ACTIVITIES = "/api/task/activities";
            public static final String TASK_WARNING_ACTIVITIES = "/api/task-exception/activities";
            public static final String DISPATCH = "/api/dispatch";
            public static final String COMPLETE_TASK = "/api/complete-task";
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

            public static final String COMMENT_IMAGE_ACTIVITY_TO_TASK_WARNING = "/api/add-task-exception-activity/image";
            public static final String COMMENT_FILE_ACTIVITY_TO_TASK_WARNING = "/api/add-task-exception-activity/file";
            public static final String COMMENT_TEXT_ACTIVITY_TO_TASK_WARNING = "/api/add-task-exception-activity/text";

            public static final String LOGIN_STATUS = "/api/login-status";
            public static final String LOGIN = "/api/login";

            public static final String TIME_CARD_BY_RESOURCE = "/api/resource/timecards";

            public static final String CHECKIN_OUT = "/api/checkin-out/employee";
        }
    }

//    /**
//     * Load all cases data from server, not include tasks data.
//     * We only load task id of each task in the case.
//     *
//     * @param context
//     */
//    public static void loadCases(Context context) {
//        try {
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String caseJsonListString = RestfulUtils.restfulGetRequest(WorkingDataUrl.CASES, headers);
//            JSONArray caseJsonList = new JSONObject(caseJsonListString).getJSONArray("result");
//
//            for (int i = 0; i < caseJsonList.length(); i++) {
//                JSONObject caseJson = caseJsonList.getJSONObject(i);
//                addCaseToWorkingData(context, caseJson);
//            }
//
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in loadCases()");
//            e.printStackTrace();
//        }
//    }
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
//    public static void loadWorkersByFactory(Context context, String factoryId) {
//        if (!WorkingData.getInstance(context).hasFactory(factoryId)) return;
//
//        try {
//            HashMap<String, String> headers = new HashMap<>();
//            headers.put("x-user-id", WorkingData.getUserId());
//            headers.put("x-auth-token", WorkingData.getAuthToken());
//
//            String workerJsonString = RestfulUtils.restfulGetRequest(getWorkersByFactoryUrl(factoryId), headers);
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
//            WorkingData.getInstance(context).getFactoryById(factoryId).workers = newWorkers;
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
            JSONObject wipTaskJson = taskJson.getJSONObject("WIPTask");
            JSONArray scheduledTaskJsonList = taskJson.getJSONArray("scheduledTasks");

            WorkingData.getInstance(context).setWipTask(retrieveTaskFromJson(context, wipTaskJson));

            WorkingData.getInstance(context).clearScheduledTasks();
            for (int i = 0 ; i < scheduledTaskJsonList.length() ; i++) {
                JSONObject scheduledTaskJson = scheduledTaskJsonList.getJSONObject(i);
                WorkingData.getInstance(context).addScheduledTask(retrieveTaskFromJson(context, scheduledTaskJson));
            }

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
//    private static void addCaseToWorkingData(Context context, JSONObject caseJson) {
//        try {
//            String caseId = caseJson.getString("_id");
//            long lastUpdatedTime = caseJson.getLong("updatedAt");
//            boolean hasCase = WorkingData.getInstance(context).hasCase(caseId);
//
//            if (hasCase &&
//                    WorkingData.getInstance(context).getCaseById(caseId).lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            if (hasCase) {
//                WorkingData.getInstance(context).updateCase(caseId, retrieveCaseFromJson(context, caseJson));
//            } else {
//                WorkingData.getInstance(context).addCase(retrieveCaseFromJson(context, caseJson));
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addCaseToWorkingData()");
//            e.printStackTrace();
//        }
//    }
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
//            String factoryId = factoryJson.getString("_id");
//            long lastUpdatedTime = factoryJson.getLong("updatedAt");
//            boolean hasFactory = WorkingData.getInstance(context).hasFactory(factoryId);
//
//            if (hasFactory &&
//                    WorkingData.getInstance(context).getFactoryById(factoryId).lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            if (hasFactory) {
//                WorkingData.getInstance(context).updateFactory(factoryId, retrieveFactoryFromJson(context, factoryJson));
//            } else {
//                WorkingData.getInstance(context).addFactory(retrieveFactoryFromJson(context, factoryJson));
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addFactoryToWorkingData()");
//            e.printStackTrace();
//        }
//    }
//    private static void addWorkerToWorkingData(Context context, JSONObject workerJson) {
//        try {
//            String workerId = workerJson.getString("_id");
//            long lastUpdatedTime = workerJson.getLong("updatedAt");
//            boolean workingDataHasWorker = WorkingData.getInstance(context).hasWorker(workerId);
//
//            if (workingDataHasWorker &&
//                    WorkingData.getInstance(context).getWorkerById(workerId).lastUpdatedTime >= lastUpdatedTime) {
//                return;
//            }
//
//            if (workingDataHasWorker) {
//                WorkingData.getInstance(context).updateWorker(workerId, retrieveWorkerFromJson(context, workerJson));
//            } else {
//                WorkingData.getInstance(context).addWorker(retrieveWorkerFromJson(context, workerJson));
//            }
//
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in addWorkerToWorkingData()");
//            e.printStackTrace();
//        }
//    }
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
//    private static Case retrieveCaseFromJson(Context context, JSONObject caseJson) {
//        try {
//            JSONObject caseIndustrialForm = caseJson.getJSONObject("industrialForm");
//            JSONObject caseVendor = caseJson.getJSONObject("client");
//            JSONObject caseManager = caseJson.getJSONObject("lead");
//            JSONArray caseMovableMoldSize = caseIndustrialForm.getJSONArray("movableMoldSize");
//            JSONArray caseFixedMoldSize = caseIndustrialForm.getJSONArray("fixedMoldSize");
//            JSONArray caseSupportBlockMoldSize = caseIndustrialForm.getJSONArray("supportBlockMoldSize");
//            JSONArray caseTagListJson = caseJson.getJSONArray("tags");
//            JSONArray caseWorkerIds = caseJson.getJSONArray("employeeIdList");
//
//            String id = caseJson.getString("_id");
//            String name = caseJson.getString("name");
//            String description = getStringFromJson(caseJson, "details");
//            double cost = caseJson.getDouble("cost");
//
//            addVendorToWorkingData(context, caseVendor);
//            String vendorId = caseVendor.getString("_id");
//
//            addManagerToWorkingData(context, caseManager);
//            String managerId = caseManager.getString("_id");
//
//            int plateCount = caseIndustrialForm.getInt("plateCount");
//            int supportBlockCount = caseIndustrialForm.getInt("supportBlockCount");
//            long lastUpdatedTime = caseJson.getLong("updatedAt");
//
//            double[] movableMoldSize = {
//                    caseMovableMoldSize.getDouble(0),
//                    caseMovableMoldSize.getDouble(1),
//                    caseMovableMoldSize.getDouble(2),
//                    caseIndustrialForm.getDouble("movableMoldWeight")
//            };
//            double[] fixedMoldSize = {
//                    caseFixedMoldSize.getDouble(0),
//                    caseFixedMoldSize.getDouble(1),
//                    caseFixedMoldSize.getDouble(2),
//                    caseIndustrialForm.getDouble("fixedMoldWeight")
//            };
//            double[] supportBlockMoldSize = {
//                    caseSupportBlockMoldSize.getDouble(0),
//                    caseSupportBlockMoldSize.getDouble(1),
//                    caseSupportBlockMoldSize.getDouble(2),
//                    caseIndustrialForm.getDouble("supportBlockMoldWeight")
//            };
//
//            Date deliveredDate = getDateFromJson(caseJson, "willDeliverAt");
//            Date materialPurchasedDate = getDateFromJson(caseIndustrialForm, "materialPurchasedAt");
//            Date layoutDeliveredDate = getDateFromJson(caseIndustrialForm, "layoutDeliveredAt");
//
//            List<Tag> tags = new ArrayList<>();
//            for (int j = 0 ; j < caseTagListJson.length() ; j++) {
//                JSONObject caseTag = caseTagListJson.getJSONObject(j);
//                String tagId = caseTag.getString("_id");
//
//                addTagToWorkingData(context, caseTag);
//                tags.add(WorkingData.getInstance(context).getTagById(tagId));
//            }
//
//            // TODO: employeeIdList is empty
//            List<String> workerIds = new ArrayList<>();
//            for (int w = 0 ; w < caseWorkerIds.length() ; w++) {
//                workerIds.add(caseWorkerIds.getString(w));
//            }
//
////            Log.d(TAG, "Case id = " + id);
////            Log.d(TAG, "Case name = " + name);
////            Log.d(TAG, "Case description = " + description);
////            Log.d(TAG, "Case vendorId = " + vendorId);
////            Log.d(TAG, "Case managerId = " + managerId);
////            Log.d(TAG, "Case deliveredDate = " + deliveredDate.getTime());
////            Log.d(TAG, "Case materialPurchasedDate = " + materialPurchasedDate.getTime());
////            Log.d(TAG, "Case layoutDeliveredDate = " + layoutDeliveredDate.getTime());
////            Log.d(TAG, "Case movableMoldSize = " + description);
////            Log.d(TAG, "Case movableMoldSize = "
////                    + movableMoldSize[0] + " " + movableMoldSize[1] + " " + movableMoldSize[2] + " " + movableMoldSize[3]);
////            Log.d(TAG, "Case fixedMoldSize = "
////                    + fixedMoldSize[0] + " " + fixedMoldSize[1] + " " + fixedMoldSize[2] + " " + fixedMoldSize[3]);
////            Log.d(TAG, "Case supportBlockMoldSize = "
////                    + supportBlockMoldSize[0] + " " + supportBlockMoldSize[1] + " "
////                    + supportBlockMoldSize[2] + " " + supportBlockMoldSize[3]);
////            Log.d(TAG, "Case plateCount = " + plateCount);
////            Log.d(TAG, "Case supportBlockCount = " + supportBlockCount);
////            Log.d(TAG, "Case tags = " + tags);
////            Log.d(TAG, "Case involvedWorkerIds = " + involvedWorkerIds);
////            Log.d(TAG, "Case lastUpdatedTime = " + lastUpdatedTime);
//
//
//            return new Case(
//                    id,
//                    name,
//                    description,
//                    vendorId,
//                    managerId,
//                    cost,
//                    deliveredDate,
//                    materialPurchasedDate,
//                    layoutDeliveredDate,
//                    new Case.Size(movableMoldSize),
//                    new Case.Size(fixedMoldSize),
//                    new Case.Size(supportBlockMoldSize),
//                    plateCount,
//                    supportBlockCount,
//                    tags,
//                    workerIds,
//                    lastUpdatedTime);
//
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in retrieveCaseFromJson()");
//            e.printStackTrace();
//        }
//
//        return null;
//    }
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
//    private static Worker retrieveWorkerFromJson(Context context, JSONObject workerJson) {
//        try {
//            JSONObject paymentJson = workerJson.getJSONObject("paymentClassification");
//            JSONObject equipmentJson = getJsonObjectFromJson(workerJson, "resource");
//            JSONArray scheduledTaskJsonList = workerJson.getJSONArray("scheduledTaskIds");
//
//            String id = workerJson.getString("_id");
//            String name = workerJson.getJSONObject("profile").getString("name");
//            String factoryId = getStringFromJson(workerJson, "groupId");
//            String equipmentId = "";
//            String wipTaskId = getStringFromJson(workerJson, "WIPTaskId");
//            String address = getStringFromJson(workerJson, "address");
//            String phone = getStringFromJson(workerJson, "phone");
//
//            if (equipmentJson != null) {
//                equipmentId = equipmentJson.getString("_id");
//                addEquipmentToWorkingData(context, equipmentJson);
//            }
//
//            int score = workerJson.getInt("score");
//            long lastUpdatedTime = workerJson.getLong("updatedAt");
//            boolean isOvertime = workerJson.getBoolean("overwork");
//
//            Worker.Status status = Worker.convertStringToStatus(workerJson.getString("status"));
//
//            Worker.PaymentClassification payment =
//                    new Worker.PaymentClassification(paymentJson.getString("type"),
//                                                     paymentJson.getDouble("base"),
//                                                     paymentJson.getDouble("hourlyPayment"),
//                                                     paymentJson.getDouble("overtimeBase"));
//
//            List<String> scheduledTaskIds = new ArrayList<>();
//            for (int st = 0 ; st < scheduledTaskJsonList.length() ; st++) {
//                scheduledTaskIds.add(scheduledTaskJsonList.getString(st));
//            }
//
//            return new Worker(
//                    context,
//                    id,
//                    name,
//                    factoryId,
//                    wipTaskId,
//                    address,
//                    phone,score,
//                    isOvertime,
//                    status,
//                    payment,
//                    scheduledTaskIds,
//                    lastUpdatedTime);
//
//        } catch (JSONException e) {
//            Log.e(TAG, "Exception in retrieveWorkerFromJson()");
//            e.printStackTrace();
//        }
//
//        return null;
//    }
    private static Task retrieveTaskFromJson(Context context, JSONObject taskJson) {
        try {
            JSONArray warningJsonList = taskJson.getJSONArray("taskExceptions");
            JSONObject equipmentJson = getJsonObjectFromJson(taskJson, "resource");
            JSONObject taskTimecardJson = getJsonObjectFromJson(taskJson, "taskTimecard");

            String id = taskJson.getString("_id");
            String name = taskJson.getString("name");
            String caseId = taskJson.getString("caseId");
            String workerId = getStringFromJson(taskJson, "employeeId");
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

            Date startDate = getDateFromJson(taskJson, "startDate");
            Date endDate = getDateFromJson(taskJson, "endDate");
            Date assignDate = getDateFromJson(taskJson, "dispatchedDate");
//
//            List<TaskWarning> taskWarnings = new ArrayList<>();
//            for (int w = 0 ; w < warningJsonList.length() ; w++) {
//                JSONObject warningJson = warningJsonList.getJSONObject(w);
//                String warningId = warningJson.getString("_id");
//
//                addTaskWarningToWorkingData(context, warningJson);
//                taskWarnings.add(WorkingData.getInstance(context).getTaskWarningById(warningId));
//            }

            boolean isDelayed = getBooleanFromJson(taskJson, "delay");

            // TODO: Sub task

            Task task = new Task(
                    id,
                    name,
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
                    isDelayed);

            JSONObject scheduledTaskAlert = getJsonObjectFromJson(taskJson, "scheduledTaskAlert");
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
//            String factoryId = equipmentJson.getString("workingGroupId");
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
//                    factoryId,
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
//    private static String getWorkersByFactoryUrl(String factoryId) {
//        return WorkingDataUrl.WORKERS_BY_FACTORY + factoryId;
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
    private static Date getDateFromJson(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.has(key) ? new Date(jsonObject.getLong(key)) : null;
    }
    private static String getStringFromJson(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.has(key) ? jsonObject.getString(key) : "";
    }
    private static long getLongFromJson(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.has(key) ? jsonObject.getLong(key) : 0L;
    }
    private static boolean getBooleanFromJson(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.has(key) ? jsonObject.getBoolean(key) : false;
    }
    private static JSONObject getJsonObjectFromJson(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.has(key) ? jsonObject.getJSONObject(key) : null;
    }
}
