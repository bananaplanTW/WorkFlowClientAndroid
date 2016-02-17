package com.nicloud.workflowclient.workerlist;

import com.nicloud.workflowclient.data.data.Worker;

/**
 * Created by logicmelody on 2016/2/17.
 */
public class WorkerListItem {

    public boolean isSelected = false;
    public Worker worker;


    public WorkerListItem(boolean isSelected, Worker worker) {
        this.isSelected = isSelected;
        this.worker = worker;
    }
}
