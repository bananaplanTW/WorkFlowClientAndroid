package com.nicloud.workflowclient.messagemenu;

import android.graphics.Bitmap;

/**
 * Created by logicmelody on 2015/12/23.
 */
public class MessageMenuItem {

    public String id;
    public String name;
    public int viewType;
    public boolean isSelected = false;

    public Bitmap avatar;
    public String avatarUrl;


    public MessageMenuItem(String id, String name, int viewType, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.viewType = viewType;
        this.isSelected = isSelected;
    }
}
