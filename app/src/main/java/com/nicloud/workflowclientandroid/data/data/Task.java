package com.nicloud.workflowclientandroid.data.data;

/**
 * Created by logicmelody on 2015/11/11.
 */
public class Task {

    public String taskName;
    public String caseName;

    public Task(String title) {
        this.taskName = title;
    }

    public Task(String taskName, String caseName) {
        this.taskName = taskName;
        this.caseName = caseName;
    }
}
