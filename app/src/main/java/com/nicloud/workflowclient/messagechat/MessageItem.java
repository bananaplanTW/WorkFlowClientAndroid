package com.nicloud.workflowclient.messagechat;

import com.nicloud.workflowclient.data.data.data.WorkingData;

/**
 * Created by logicmelody on 2016/1/6.
 */
public class MessageItem {

    public String id;
    public String content;
    public String ownerId;
    public boolean isMe = false;
    public long time = 0L;


    public MessageItem(String id, String content, String ownerId, long time) {
        this.id = id;
        this.content = content;
        this.ownerId = ownerId;
        this.time = time;

        this.isMe = ownerId.equals(WorkingData.getUserId());
    }
}
