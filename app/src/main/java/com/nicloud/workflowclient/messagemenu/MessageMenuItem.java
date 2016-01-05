package com.nicloud.workflowclient.messagemenu;

import com.nicloud.workflowclient.data.data.data.Worker;

/**
 * Created by logicmelody on 2015/12/23.
 */
public class MessageMenuItem {

    public String title;

    public Worker worker;
    public int viewType;
    public boolean isSelected = false;


    public MessageMenuItem(String title, Worker worker, int viewType, boolean isSelected) {
        this.title = title;
        this.worker = worker;
        this.viewType = viewType;
        this.isSelected = isSelected;
    }
}
