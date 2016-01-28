package com.nicloud.workflowclient.cases.discussion;

import android.graphics.drawable.Drawable;

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
    public String type = WorkFlowContract.Discussion.Type.MESSAGE;

    public String fileName;
    public String fileUri;
    public String fileThumbUri;
    public Drawable fileThumb = null;

    public long createdTime = 0L;


    public DiscussionItem(String discussionId, String caseId,
                          String workerId, String workerName, String workerAvatarUri,
                          String content, String fileName, String fileUri, String fileThumbUri,
                          String type, long createdTime) {
        this.discussionId = discussionId;
        this.caseId = caseId;
        this.workerId = workerId;
        this.workerName = workerName;
        this.workerAvatarUri = workerAvatarUri;
        this.content = content;
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.fileThumbUri = fileThumbUri;
        this.type = type;
        this.createdTime = createdTime;
    }
}
