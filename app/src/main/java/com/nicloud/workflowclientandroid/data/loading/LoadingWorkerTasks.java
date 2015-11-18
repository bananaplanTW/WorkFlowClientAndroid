package com.nicloud.workflowclientandroid.data.loading;

import android.content.Context;
import android.os.AsyncTask;

import com.nicloud.workflowclientandroid.data.data.WorkingData;
import com.nicloud.workflowclientandroid.data.utility.RestfulUtils;


/**
 * Async task to load tasks data from server
 *
 * @author Danny Lin
 * @since 2015/11/18.
 */
public class LoadingWorkerTasks extends AsyncTask<Void, Void, Void> {

    // [TODO] should move out to be a identical interface from here
    public interface OnFinishLoadingDataListener {
        void onFinishLoadingData();
        void onFailLoadingData(boolean isFailCausedByInternet);
    }

    private Context mContext;
    private OnFinishLoadingDataListener mOnFinishLoadingDataListener;

    private boolean isFailCausedByInternet = false;


    public LoadingWorkerTasks(Context context, OnFinishLoadingDataListener listener) {
        mContext = context;
        mOnFinishLoadingDataListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (RestfulUtils.isConnectToInternet(mContext)) {
            LoadingDataUtils.loadTasksByWorker(mContext, WorkingData.getUserId());

        } else {
            isFailCausedByInternet = true;
            cancel(true);
        }

        return null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        //super.onCancelled(aVoid);
        mOnFinishLoadingDataListener.onFailLoadingData(isFailCausedByInternet);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mOnFinishLoadingDataListener.onFinishLoadingData();
    }
}
