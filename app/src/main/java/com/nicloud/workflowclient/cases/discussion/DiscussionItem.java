package com.nicloud.workflowclient.cases.discussion;

import com.nicloud.workflowclient.provider.database.WorkFlowContract;

/**
 * Created by logicmelody on 2016/1/26.
 */
public class DiscussionItem {

    public String discussionId;
    public String caseId;

    public String workerId;
    public String workerName;
    public String workerAvatarUri;

    public String content;
    public int type = WorkFlowContract.Discussion.Type.MESSAGE;
    public long createdTime = 0L;


    public DiscussionItem(String discussionId, String caseId,
                          String workerId, String workerName, String workerAvatarUri,
                          String content, int type, long createdTime) {
        this.discussionId = discussionId;
        this.caseId = caseId;
        this.workerId = workerId;
        this.workerName = workerName;
        this.workerAvatarUri = workerAvatarUri;
        this.content = content;
        this.type = type;
        this.createdTime = createdTime;
    }
}
