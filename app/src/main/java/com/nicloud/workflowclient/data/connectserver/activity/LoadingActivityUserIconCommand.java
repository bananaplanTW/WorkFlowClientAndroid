package com.nicloud.workflowclient.data.connectserver.activity;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.nicloud.workflowclient.data.connectserver.tasklog.OnLoadImageListener;
import com.nicloud.workflowclient.data.data.activity.BaseData;


/**
 * Created by daz on 10/10/15.
 */
public class LoadingActivityUserIconCommand implements LoadingDrawableAsyncTask.OnFinishLoadingDataListener {

    private LoadingDrawableAsyncTask mLoadingDrawableAsyncTask;
    private Context mContext;
    private Uri mUri;
    private BaseData mBaseData;

    private OnLoadImageListener mOnLoadImageListener;


    public LoadingActivityUserIconCommand(Context context, Uri uri, BaseData baseData, OnLoadImageListener listener) {
        mContext = context;
        mUri = uri;
        mBaseData = baseData;
        mOnLoadImageListener = listener;
    }

    public void execute () {
        mLoadingDrawableAsyncTask = new LoadingDrawableAsyncTask(mContext, mUri, this);
        mLoadingDrawableAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onFinishLoadingData() {
        mBaseData.avatar = mLoadingDrawableAsyncTask.getResult();
        mOnLoadImageListener.onFinishLoadImage();
    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {

    }
}
