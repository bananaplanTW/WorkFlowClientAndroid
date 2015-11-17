package com.nicloud.workflowclientandroid.data.loading;

import android.content.Context;
import android.os.AsyncTask;


/**
 * Async task to load data from server
 *
 * @author Danny Lin
 * @since 2015/10/1.
 */
public class LoadingDataTask extends AsyncTask<Void, Void, Void> {

    // [TODO] should move out to be a identical interface from here
    public interface OnFinishLoadingDataListener {
        void onFinishLoadingData();
        void onFailLoadingData(boolean isFailCausedByInternet);
    }

    private Context mContext;
    private OnFinishLoadingDataListener mOnFinishLoadingDataListener;

    private boolean isFailCausedByInternet = false;


    public LoadingDataTask(Context context, OnFinishLoadingDataListener listener) {
        mContext = context;
        mOnFinishLoadingDataListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        if (RestfulUtils.isConnectToInternet(mContext)) {
//
//        } else {
//            isFailCausedByInternet = true;
//            cancel(true);
//        }

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
