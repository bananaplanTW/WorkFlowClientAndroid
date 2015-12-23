package com.nicloud.workflowclient.mainmenu;

/**
 * Created by logicmelody on 2015/12/23.
 */
public class MainMenuItem {

    public String name;
    public int viewType;
    public boolean isSelected = false;


    public MainMenuItem(String name, int viewType, boolean isSelected) {
        this.name = name;
        this.viewType = viewType;
        this.isSelected = isSelected;
    }
}
