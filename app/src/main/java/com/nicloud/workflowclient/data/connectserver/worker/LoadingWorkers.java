package com.nicloud.workflowclient.data.connectserver.worker;

import android.content.Context;
import android.os.AsyncTask;

import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.utility.RestfulUtils;


/**
 * Async task to load tasks data from server
 *
 * @author Danny Lin
 * @since 2015/11/18.
 */
public class LoadingWorkers extends AsyncTask<Void, Void, Void> {

    public interface OnFinishLoadingWorkersListener {
        void onFinishLoadingWorkers();
        void onFailLoadingWorkers(boolean isFailCausedByInternet);
    }

    private Context mContext;
    private OnFinishLoadingWorkersListener mOnFinishLoadingWorkersListener;

    private boolean isFailCausedByInternet = false;


    public LoadingWorkers(Context context, OnFinishLoadingWorkersListener listener) {
        mContext = context;
        mOnFinishLoadingWorkersListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (RestfulUtils.isConnectToInternet(mContext)) {
            try {
                LoadingDataUtils.loadWorkers(mContext);

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
        mOnFinishLoadingWorkersListener.onFailLoadingWorkers(isFailCausedByInternet);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mOnFinishLoadingWorkersListener.onFinishLoadingWorkers();
    }
}
