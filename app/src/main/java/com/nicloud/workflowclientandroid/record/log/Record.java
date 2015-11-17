package com.nicloud.workflowclientandroid.record.log;

/**
 * Created by logicmelody on 2015/11/17.
 */
public class Record {

    public String userName;
    public String content;
    public String timeStamp;

    public Record(String userName, String content, String timeStamp) {
        this.userName = userName;
        this.content = content;
        this.timeStamp = timeStamp;
    }
}
