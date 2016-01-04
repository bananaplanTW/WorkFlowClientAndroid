package com.nicloud.workflowclient.mainmenu;

import com.nicloud.workflowclient.data.data.data.Case;

/**
 * Created by logicmelody on 2015/12/23.
 */
public class MainMenuItem {

    public int mId;
    public String mName;
    public Case mCase;
    public int mViewType;
    public boolean mIsSelected = false;


    public MainMenuItem(int id, String name, Case aCase, int viewType, boolean isSelected) {
        mId = id;
        mName = name;
        mCase = aCase;
        mViewType = viewType;
        mIsSelected = isSelected;
    }
}
