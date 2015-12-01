package com.nicloud.workflowclient.data.connectserver.activity;

import android.content.Context;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.connectserver.restful.PostRequestAsyncTask;
import com.nicloud.workflowclient.data.connectserver.tasklog.ICreateActivityCommand;
import com.nicloud.workflowclient.data.connectserver.tasklog.OnLeaveCommentListener;
import com.nicloud.workflowclient.data.connectserver.tasklog.UploadingFileStrategy;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.utility.Utilities;

import java.util.HashMap;

/**
 * Created by daz on 10/18/15.
 */
public class LeaveAFileCommentToTaskCommand implements ICreateActivityCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;

    private Context mContext;
    private String mTaskId;
    private String mFilePath;

    private OnLeaveCommentListener mOnLeaveCommentListener;


    public LeaveAFileCommentToTaskCommand(Context context, String taskId, String filePath, OnLeaveCommentListener listener) {
        mContext = context;
        mTaskId = taskId;
        mFilePath = filePath;
        mOnLeaveCommentListener = listener;
    }

    @Override
    public void execute() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("td", mTaskId);
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        UploadingFileStrategy uploadingFileStrategy = new UploadingFileStrategy(mFilePath, LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_FILE_ACTIVITY_TO_TASK, headers);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingFileStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {
        mOnLeaveCommentListener.onFinishLeaveComment();
        Utilities.showToastInNonUiThread(mContext, mContext.getString(R.string.add_log_complete_file));
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnLeaveCommentListener.onFailLeaveComment(isFailCausedByInternet);
    }
}
