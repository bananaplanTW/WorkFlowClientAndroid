package com.nicloud.workflowclient.data.data.data;

import com.nicloud.workflowclient.detailedtask.checklist.CheckItem;
import com.nicloud.workflowclient.utility.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data in a task item
 *
 * @author Danny Lin
 * @since 2015.06.13
 */
public class Task extends IdData {

    public String description;
    public String caseId;
    public String caseName;
    public String workerId;
    public String status;

    public Date dueDate;

    public List<CheckItem> checkList;


    public Task(String id,
                String name,
                String description,
                String caseName,
                String caseId,
                String workerId,
                String status,
                Date dueDate,
                List<CheckItem> checkList,
                long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.caseName = caseName;
        this.caseId = caseId;
        this.workerId = workerId;
        this.status = status;
        this.dueDate = dueDate;
        this.checkList = checkList;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public static Task retrieveTaskFromJson(JSONObject jsonObject) throws JSONException {
        JSONArray checkListJsonList = JsonUtils.getJsonArrayFromJson(jsonObject, "todos");

        String taskId = jsonObject.getString("_id");
        String name = jsonObject.getString("name");
        String description = JsonUtils.getStringFromJson(jsonObject, "description");
        String caseName = jsonObject.getString("caseName");
        String caseId = jsonObject.getString("caseId");
        String workerId = JsonUtils.getStringFromJson(jsonObject, "employeeId");
        String status = jsonObject.getString("status");
        long dueDate = JsonUtils.getLongFromJson(jsonObject, "dueDate");
        long lastUpdatedTime = jsonObject.getLong("updatedAt");

        List<CheckItem> checkList = new ArrayList<>();

        if (checkListJsonList != null) {
            for (int j = 0; j < checkListJsonList.length(); j++) {
                JSONObject checkItemJson = checkListJsonList.getJSONObject(j);
                checkList.add(new CheckItem(checkItemJson.getString("name"),
                        taskId,
                        checkItemJson.getBoolean("checked"), j));
            }
        }

        return new Task(taskId, name, description, caseName, caseId,
                        workerId, status, new Date(dueDate), checkList, lastUpdatedTime);
    }
}
