package com.nicloud.workflowclient.data.data.activity;

import android.graphics.drawable.Drawable;

import java.util.Date;

/**
 * Created by Ben on 2015/8/29.
 */
public class BaseData {

    public enum TYPE {
        RECORD, FILE, PHOTO, HISTORY
    }

    public enum CATEGORY {
        WORKER, TASK, WARNING
    }

    public long id;
    public String workerId;

    public Drawable avatar;
    public Date time;
    public TYPE type;
    public CATEGORY category;
    public String tag;


    public BaseData() {}

    public BaseData(TYPE type) {
        this.type = type;
    }
}
