package com.nicloud.workflowclient.messagechat;

/**
 * Created by logicmelody on 2016/1/6.
 */
public class Message {

    public String id;
    public String content;
    public boolean isMe = false;
    public long time = 0L;


    public Message(String id, String content, boolean isMe, long time) {
        this.id = id;
        this.content = content;
        this.isMe = isMe;
        this.time = time;
    }
}
