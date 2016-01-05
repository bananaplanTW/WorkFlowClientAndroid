package com.nicloud.workflowclient.data.data.data;

import android.graphics.drawable.Drawable;

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
}