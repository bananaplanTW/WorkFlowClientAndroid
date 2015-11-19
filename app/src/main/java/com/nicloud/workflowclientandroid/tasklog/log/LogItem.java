package com.nicloud.workflowclientandroid.tasklog.log;

/**
 * Created by logicmelody on 2015/11/17.
 */
public class LogItem {

    public String userName;
    public String content;
    public String timeStamp;

    public LogItem(String userName, String content, String timeStamp) {
        this.userName = userName;
        this.content = content;
        this.timeStamp = timeStamp;
    }
}
