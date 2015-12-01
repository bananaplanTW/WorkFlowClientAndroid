package com.nicloud.workflowclient.data.connectserver.tasklog;

import android.content.Context;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.connectserver.restful.PostRequestAsyncTask;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.utility.Utilities;

import java.util.HashMap;

/**
 * Created by daz on 10/18/15.
 */
public class LeaveATextCommentToTaskCommand implements ICreateActivityCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private Context mContext;
    private String mTaskId;
    private String mComment;

    private OnLeaveCommentListener mOnLeaveCommentListener;


    public LeaveATextCommentToTaskCommand(Context context, String taskId, String comment,
                                          OnLeaveCommentListener listener) {
        mContext = context;
        mTaskId = taskId;
        mComment = comment;
        mOnLeaveCommentListener = listener;
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
        Utilities.showToastInNonUiThread(mContext, mContext.getString(R.string.add_log_complete_text));
        mOnLeaveCommentListener.onFinishLeaveComment();
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnLeaveCommentListener.onFailLeaveComment(isFailCausedByInternet);
    }
}
