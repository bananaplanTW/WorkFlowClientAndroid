package com.nicloud.workflowclient.cases.file;

import android.graphics.drawable.Drawable;

import com.nicloud.workflowclient.detailedtask.checklist.CheckItem;
import com.nicloud.workflowclient.utility.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by logicmelody on 2016/2/1.
 */
public class FileItem {

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


    public FileItem(String fileId, String fileName, String fileType, String fileUrl, String fileThumbUrl,
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

    public static FileItem retrieveFileItemFromJson(JSONObject jsonObject) throws JSONException {
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

        return new FileItem(fileId, fileName, fileType, fileUrl, fileThumbUrl,
                            ownerId, ownerName, caseId, taskId, createdTime, updatedTime);
    }
}