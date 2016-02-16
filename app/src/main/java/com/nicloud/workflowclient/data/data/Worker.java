package com.nicloud.workflowclient.data.data;

import android.graphics.drawable.Drawable;

import com.nicloud.workflowclient.utility.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class Worker extends IdData {

    public String departmentId;
    public String departmentName;
    public String address;
    public String phone;

    public String avatarUrl;
    public Drawable avatar;


    public Worker() {

    }

    public Worker(
            String id,
            String name,
            String departmentId,
            String departmentName,
            String address,
            String phone,
            String avatarUrl,
            long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.address = address;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void update(Worker worker) {
        this.name = worker.name;
        this.departmentId = worker.departmentId;
        this.departmentName = worker.departmentName;
        this.address = worker.address;
        this.phone = worker.phone;
        this.avatarUrl = worker.avatarUrl;
        this.lastUpdatedTime = worker.lastUpdatedTime;
    }

    public static Worker retrieveWorkerFromJson(JSONObject workerJson) {
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
            e.printStackTrace();
        }

        return null;
    }
}