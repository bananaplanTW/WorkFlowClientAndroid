package com.nicloud.workflowclient.cases.discussion;

/**
 * Created by logicmelody on 2016/1/26.
 */
public class Discussion {

    public String workerId;
    public String content;
    public long time;


    public Discussion(String workerId, String content, long time) {
        this.workerId = workerId;
        this.content = content;
        this.time = time;
    }
}
