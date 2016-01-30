package com.nicloud.workflowclient.tasklist.main;

/**
 * Created by logicmelody on 2016/1/29.
 */
public class TaskListItem {

    public Task task;
    public boolean showDueDate;
    public boolean showDueDateUnderline;


    public TaskListItem(Task task, boolean showDueDate, boolean showDueDateUnderline) {
        this.task = task;
        this.showDueDate = showDueDate;
        this.showDueDateUnderline = showDueDateUnderline;
    }
}
