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

    public Date dueDate;


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
                long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.caseName = caseName;
        this.caseId = caseId;
        this.workerId = workerId;
        this.dueDate = dueDate;
        this.lastUpdatedTime = lastUpdatedTime;
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
    }
}
