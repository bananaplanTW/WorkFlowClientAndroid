package com.nicloud.workflowclient.data.data.data;

/**
 * Created by logicmelody on 2016/1/4.
 */
public class Case extends IdData {

    public boolean isCompleted = false;
    public long updatedAt = 0L;


    public Case(String id, String name, boolean isCompleted, long updatedAt) {
        this.id = id;
        this.name = name;
        this.isCompleted = isCompleted;
        this.updatedAt = updatedAt;
    }

    public void update(Case aCase) {
        this.name = aCase.name;
        this.isCompleted = aCase.isCompleted;
        this.updatedAt = aCase.updatedAt;
    }
}
