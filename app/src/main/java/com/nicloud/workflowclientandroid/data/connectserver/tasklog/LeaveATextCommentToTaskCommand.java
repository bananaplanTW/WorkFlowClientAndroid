package com.nicloud.workflowclientandroid.data.connectserver.tasklog;

import android.content.Context;

import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclientandroid.data.connectserver.restful.PostRequestAsyncTask;
import com.nicloud.workflowclientandroid.data.data.data.WorkingData;
import com.nicloud.workflowclientandroid.utility.Utilities;

import java.util.HashMap;

/**
 * Created by daz on 10/18/15.
 */
public class LeaveATextCommentToTaskCommand implements ICreateActivityCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    public interface OnLeaveATextCommentListener {
        void onFinishLeaveATextComment();
        void onFailLeaveATextComment(boolean isFailCausedByInternet);
    }

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private Context mContext;
    private String mTaskId;
    private String mComment;

    private OnLeaveATextCommentListener mOnLeaveATextCommentListener;


    public LeaveATextCommentToTaskCommand(Context context, String taskId, String comment,
                                          OnLeaveATextCommentListener listener) {
        mContext = context;
        mTaskId = taskId;
        mComment = comment;
        mOnLeaveATextCommentListener = listener;
    }

    @Override
    public void execute() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("td", mTaskId);
        bodies.put("msg", mComment);

        PostLogStrategy uploadingCommentStrategy = new PostLogStrategy(LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_TEXT_ACTIVITY_TO_TASK, headers, bodies);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingCommentStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {
        mOnLeaveATextCommentListener.onFinishLeaveATextComment();
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnLeaveATextCommentListener.onFailLeaveATextComment(isFailCausedByInternet);
    }
}
