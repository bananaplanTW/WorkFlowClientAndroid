package com.nicloud.workflowclientandroid.main.tasklist;

import com.nicloud.workflowclientandroid.data.data.data.Task;

/**
 * Created by logicmelody on 2015/11/12.
 */
public class TasksListItem {

    public Task task;
    public int itemViewType;

    public TasksListItem(Task task, int itemViewType) {
        this.task = task;
        this.itemViewType = itemViewType;
    }
}
