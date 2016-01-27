package com.nicloud.workflowclient.cases.discussion;

import com.nicloud.workflowclient.provider.database.WorkFlowContract;

/**
 * Created by logicmelody on 2016/1/26.
 */
public class DiscussionItem {

    public String discussionId;
    public String caseId;
    public String workerId;
    public String content;
    public int type = WorkFlowContract.Discussion.Type.MESSAGE;
    public long time;


    public DiscussionItem(String discussionId, String caseId, String workerId, String content, int type, long time) {
        this.discussionId = discussionId;
        this.caseId = caseId;
        this.workerId = workerId;
        this.content = content;
        this.type = type;
        this.time = time;
    }
}
