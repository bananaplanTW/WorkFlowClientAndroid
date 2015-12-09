package com.nicloud.workflowclient.data.connectserver.task;

import android.content.Context;
import android.os.AsyncTask;

import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.utility.RestfulUtils;


/**
 * Async task to load a task data from server
 *
 * @author Danny Lin
 * @since 2015/11/18.
 */
public class LoadingTaskById extends AsyncTask<Void, Void, Void> {

    public interface OnFinishLoadingTaskByIdListener {
        void onFinishLoadingTaskById();
        void onFailLoadingTaskById(boolean isFailCausedByInternet);
    }

    private Context mContext;
    private String mTaskId;
    private OnFinishLoadingTaskByIdListener mOnFinishLoadingTaskByIdListener;

    private boolean isFailCausedByInternet = false;


    public LoadingTaskById(Context context, String taskId, OnFinishLoadingTaskByIdListener listener) {
        mContext = context;
        mTaskId = taskId;
        mOnFinishLoadingTaskByIdListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (RestfulUtils.isConnectToInternet(mContext)) {
            try {
                LoadingDataUtils.loadTaskById(mContext, mTaskId);

            } catch (Exception e) {
                e.printStackTrace();
                cancel(true);
            }

        } else {
            isFailCausedByInternet = true;
            cancel(true);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mOnFinishLoadingTaskByIdListener.onFinishLoadingTaskById();
    }

    @Override
    protected void onCancelled(Void aVoid) {
        //super.onCancelled(aVoid);
        mOnFinishLoadingTaskByIdListener.onFailLoadingTaskById(isFailCausedByInternet);
    }
}
