package com.nicloud.workflowclient.data.data.data;

import android.graphics.drawable.Drawable;

import com.nicloud.workflowclient.utility.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by logicmelody on 2016/2/1.
 */
public class File {

    public String fileId;
    public String fileName;
    public String fileType;
    public String fileUrl;
    public String fileThumbUrl;

    public String ownerId;
    public String ownerName;
    public String caseId;
    public String taskId;

    public long createdTime;
    public long updatedTime;

    public Drawable fileThumbnail = null;


    public File(String fileId, String fileName, String fileType, String fileUrl, String fileThumbUrl,
                String ownerId, String ownerName, String caseId, String taskId,
                long createdTime, long updatedTime) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.fileThumbUrl = fileThumbUrl;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.caseId = caseId;
        this.taskId = taskId;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    public static File retrieveCaseFileFromJson(JSONObject jsonObject) throws JSONException {
        String fileId = jsonObject.getString("_id");
        String fileName = jsonObject.getString("name");
        String fileType = jsonObject.getString("attachmentType");
        String fileUrl = JsonUtils.getStringFromJson(jsonObject, "fileUrl");
        String fileThumbUrl = JsonUtils.getStringFromJson(jsonObject, "thumbUrl");
        String ownerId = jsonObject.getString("ownerId");
        String ownerName = jsonObject.getString("ownerName");
        String caseId = JsonUtils.getStringFromJson(jsonObject, "caseId");
        String taskId = JsonUtils.getStringFromJson(jsonObject, "taskId");
        long createdTime = jsonObject.getLong("createdAt");
        long updatedTime = jsonObject.getLong("updatedAt");

        return new File(fileId, fileName, fileType, fileUrl, fileThumbUrl,
                            ownerId, ownerName, caseId, taskId, createdTime, updatedTime);
    }

    public static File retrieveTaskFileFromJson(String td, JSONObject jsonObject) throws JSONException {
        String fileId = jsonObject.getString("_id");
        String fileName = jsonObject.getString("name");
        String fileType = jsonObject.getString("contentType");

        String fileUrl = null;
        if ("image".equals(fileType)) {
            fileUrl = JsonUtils.getStringFromJson(jsonObject, "imageUrl");

        } else if ("file".equals(fileType)) {
            fileUrl = JsonUtils.getStringFromJson(jsonObject, "fileUrl");
        }

        String fileThumbUrl = JsonUtils.getStringFromJson(jsonObject, "thumbUrl");
        String ownerId = jsonObject.getString("ownerId");
        String ownerName = jsonObject.getString("ownerName");
        String caseId = JsonUtils.getStringFromJson(jsonObject, "caseId");
        long createdTime = jsonObject.getLong("createdAt");
        long updatedTime = jsonObject.getLong("updatedAt");

        return new File(fileId, fileName, fileType, fileUrl, fileThumbUrl,
                        ownerId, ownerName, caseId, td, createdTime, updatedTime);
    }
}