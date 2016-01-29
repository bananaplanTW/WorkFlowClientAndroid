package com.nicloud.workflowclient.data.data.data;

import java.util.ArrayList;
import java.util.Date;

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
                Date dueDate,
                long lastUpdatedTime,
                ArrayList<CheckItem> checkList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.caseName = caseName;
        this.caseId = caseId;
        this.workerId = workerId;
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
        this.dueDate = task.dueDate;
        this.lastUpdatedTime = task.lastUpdatedTime;
        this.checkList = task.checkList;
    }
}
