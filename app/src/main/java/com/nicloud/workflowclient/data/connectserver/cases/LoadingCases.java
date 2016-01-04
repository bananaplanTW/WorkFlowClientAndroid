package com.nicloud.workflowclient.data.connectserver.cases;

import android.content.Context;
import android.os.AsyncTask;

import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.utility.RestfulUtils;


/**
 * Async task to load tasks data from server
 *
 * @author Danny Lin
 * @since 2015/11/18.
 */
public class LoadingCases extends AsyncTask<Void, Void, Void> {

    public interface OnFinishLoadingCasesListener {
        void onFinishLoadingCases();
        void onFailLoadingCases(boolean isFailCausedByInternet);
    }

    private Context mContext;
    private OnFinishLoadingCasesListener mOnFinishLoadingCasesListener;

    private boolean isFailCausedByInternet = false;


    public LoadingCases(Context context, OnFinishLoadingCasesListener listener) {
        mContext = context;
        mOnFinishLoadingCasesListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (RestfulUtils.isConnectToInternet(mContext)) {
            try {
                LoadingDataUtils.loadCases(mContext);

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
        mOnFinishLoadingCasesListener.onFailLoadingCases(isFailCausedByInternet);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mOnFinishLoadingCasesListener.onFinishLoadingCases();
    }
}
