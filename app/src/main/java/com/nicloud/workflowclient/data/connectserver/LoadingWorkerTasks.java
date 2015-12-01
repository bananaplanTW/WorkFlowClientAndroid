package com.nicloud.workflowclient.data.connectserver;

import android.content.Context;
import android.os.AsyncTask;

import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.utility.RestfulUtils;


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
            try {
                LoadingDataUtils.loadTasksByWorker(mContext, WorkingData.getUserId());

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
