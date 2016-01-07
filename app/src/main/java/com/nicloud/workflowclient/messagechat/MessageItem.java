package com.nicloud.workflowclient.messagechat;

/**
 * Created by logicmelody on 2016/1/6.
 */
public class MessageItem {

    public String messageId;
    public String content;
    public String senderId;
    public String receiverId;
    public long time = 0L;


    public MessageItem(String messageId, String content, String senderId, String receiverId, long time) {
        this.messageId = messageId;
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.time = time;
    }
}
