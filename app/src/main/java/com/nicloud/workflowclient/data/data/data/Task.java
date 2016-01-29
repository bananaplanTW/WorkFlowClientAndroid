package com.nicloud.workflowclient.data.data.data;

import android.content.Context;
import android.content.res.Resources;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.utility.utils.Utils;

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

    public Date startDate;  // The starting working date of this task
    public Date dueDate;
    public Date assignDate;

    public long startTime = 0L;  // The starting time of this working section
    public long spentTime = 0L;
    public long nextNotifyTime = 0L;

    public ArrayList<CheckItem> checkList = new ArrayList<>();


    public Task(String title) {
        this.name = title;
    }

    public Task(String id,
                String name,
                String description,
                String caseName,
                String caseId,
                String workerId,
                Date assignDate,
                Date startDate,
                Date dueDate,
                long lastUpdatedTime,
                ArrayList<CheckItem> checkList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.caseName = caseName;
        this.caseId = caseId;
        this.workerId = workerId;
        this.assignDate = assignDate;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.lastUpdatedTime = lastUpdatedTime;
        this.checkList = checkList;
    }

    public Task(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void update(Task task) {
        this.name = task.name;
        this.name = task.description;
        this.caseName = task.caseName;
        this.caseId = task.caseId;
        this.workerId = task.workerId;
        this.startTime = task.startTime;
        this.spentTime = task.spentTime;
        this.assignDate = task.assignDate;
        this.startDate = task.startDate;
        this.dueDate = task.dueDate;
        this.lastUpdatedTime = task.lastUpdatedTime;
        this.checkList = task.checkList;
    }
}
