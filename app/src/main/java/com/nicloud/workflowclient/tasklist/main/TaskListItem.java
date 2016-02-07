package com.nicloud.workflowclient.tasklist.main;

import com.nicloud.workflowclient.data.data.Task;

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
