package com.nicloud.workflowclient.data.data;

import com.nicloud.workflowclient.utility.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by logicmelody on 2016/1/4.
 */
public class Case extends IdData {

    public boolean isCompleted = false;
    public String ownerId;
    public String description;


    public Case(String id, String name, String ownerId, String description, boolean isCompleted, long updatedAt) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.description = description;
        this.isCompleted = isCompleted;
        this.lastUpdatedTime = updatedAt;
    }

    public static Case retrieveCaseFromJson(JSONObject caseJson) throws JSONException {
        String id = caseJson.getString("_id");
        String name = caseJson.getString("name");
        String ownerId = caseJson.getString("leadId");
        String description = JsonUtils.getStringFromJson(caseJson, "description");
        boolean isCompleted = caseJson.getBoolean("completed");
        long updatedAt = caseJson.getLong("updatedAt");

        return new Case(id, name, ownerId, description, isCompleted, updatedAt);
    }
}
