package com.nicloud.workflowclient.data.data;

import com.nicloud.workflowclient.utility.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by logicmelody on 2016/1/4.
 */
public class TaskTextLog {

    public String taskTextLogId;
    public String taskId;
    public String ownerId;
    public String ownerName;
    public String ownerAvatarUrl;
    public long createdTime = 0L;
    public long updatedTime = 0L;
    public String content;


    public TaskTextLog(String taskTextLogId, String taskId, String ownerId, String ownerName,
                       String ownerAvatarUrl, long createdTime, long updatedTime, String content) {
        this.taskTextLogId = taskTextLogId;
        this.taskId = taskId;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerAvatarUrl = ownerAvatarUrl;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.content = content;
    }

    public static TaskTextLog retrieveTaskTextLogFromJson(String td, JSONObject jsonObject) throws JSONException {
        String taskTextLogId = jsonObject.getString("_id");
        String ownerId = jsonObject.getString("ownerId");
        String ownerName = jsonObject.getString("ownerName");
        String ownerAvatarUrl = JsonUtils.getStringFromJson(jsonObject, "iconThumbUrl");
        long createdTime = jsonObject.getLong("createdAt");
        long updatedTime = jsonObject.getLong("updatedAt");
        String content = jsonObject.getString("content");

        return new TaskTextLog(taskTextLogId, td, ownerId, ownerName, ownerAvatarUrl,
                               createdTime, updatedTime, content);
    }
}