package com.nicloud.workflowclient.data.data;

import com.nicloud.workflowclient.utility.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/4.
 */
public class Case extends IdData {

    public boolean isCompleted = false;
    public String ownerId;
    public String description;
    public List<String> workerIdList;


    public Case(String id, String name, String ownerId, List<String> workerIdList,
                String description, boolean isCompleted, long updatedAt) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.workerIdList = workerIdList;
        this.description = description;
        this.isCompleted = isCompleted;
        this.lastUpdatedTime = updatedAt;
    }

    public static Case retrieveCaseFromJson(JSONObject caseJson) throws JSONException {
        JSONArray workerIdListJson = JsonUtils.getJsonArrayFromJson(caseJson, "employeeIdList");
        List<String> workerIdList = new ArrayList<>();

        String id = caseJson.getString("_id");
        String name = caseJson.getString("name");
        String ownerId = caseJson.getString("leadId");
        String description = JsonUtils.getStringFromJson(caseJson, "description");
        boolean isCompleted = caseJson.getBoolean("completed");
        long updatedAt = caseJson.getLong("updatedAt");

        if (workerIdListJson != null) {
            for (int i = 0 ; i < workerIdListJson.length() ; i++) {
                workerIdList.add(workerIdListJson.getString(i));
            }
        }

        return new Case(id, name, ownerId, workerIdList, description, isCompleted, updatedAt);
    }
}
