package com.nicloud.workflowclient.data.data.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by logicmelody on 2016/1/4.
 */
public class Case extends IdData {

    public boolean isCompleted = false;


    public Case(String id, String name, boolean isCompleted, long updatedAt) {
        this.id = id;
        this.name = name;
        this.isCompleted = isCompleted;
        this.lastUpdatedTime = updatedAt;
    }

    public static Case retrieveCaseFromJson(JSONObject caseJson) throws JSONException {
        String id = caseJson.getString("_id");
        String name = caseJson.getString("name");
        boolean isCompleted = caseJson.getBoolean("completed");
        long updatedAt = caseJson.getLong("updatedAt");

        return new Case(id, name, isCompleted, updatedAt);
    }
}
