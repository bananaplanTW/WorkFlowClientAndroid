package com.nicloud.workflowclientandroid.data.connectserver.tasklog;

import android.content.Context;

import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclientandroid.data.connectserver.restful.PostRequestAsyncTask;
import com.nicloud.workflowclientandroid.data.data.data.WorkingData;
import com.nicloud.workflowclientandroid.utility.Utilities;

import java.util.HashMap;

/**
 * Created by daz on 10/17/15.
 */
public class LeaveAPhotoCommentToTaskCommand implements ICreateActivityCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private Context mContext;
    private String mTaskId;
    private String mFilePath;

    private OnLeaveCommentListener mOnLeaveCommentListener;


    public LeaveAPhotoCommentToTaskCommand(Context context, String taskId, String filePath,
                                           OnLeaveCommentListener listener) {
        mContext = context;
        mTaskId = taskId;
        mFilePath = filePath;
        mOnLeaveCommentListener = listener;
    }

    @Override
    public void execute() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());
        headers.put("td", mTaskId);

        UploadingFileStrategy uploadingFileStrategy = new UploadingFileStrategy(mFilePath, LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_IMAGE_ACTIVITY_TO_TASK, headers);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingFileStrategy, this);
        mPostRequestAsyncTask.execute();
    }

    @Override
    public void onFinishPostingData() {
        Utilities.showToastInNonUiThread(mContext, mContext.getString(R.string.add_log_complete_photo));
        mOnLeaveCommentListener.onFinishLeaveComment();
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnLeaveCommentListener.onFailLeaveComment(isFailCausedByInternet);
    }
}
