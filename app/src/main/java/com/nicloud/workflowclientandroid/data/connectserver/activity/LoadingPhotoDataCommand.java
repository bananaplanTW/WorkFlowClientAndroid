package com.nicloud.workflowclientandroid.data.connectserver.activity;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.nicloud.workflowclientandroid.data.data.activity.PhotoData;


/**
 * Created by daz on 10/10/15.
 */
public class LoadingPhotoDataCommand implements LoadingDrawableAsyncTask.OnFinishLoadingDataListener {

    private LoadingDrawableAsyncTask mLoadingDrawableAsyncTask;
    private Context mContext;
    private Uri mUri;
    private PhotoData mPhotoData;

    public LoadingPhotoDataCommand(Context context, Uri uri, PhotoData photoData) {
        mContext = context;
        mUri = uri;
        mPhotoData = photoData;
    }

    public void execute () {
        mLoadingDrawableAsyncTask = new LoadingDrawableAsyncTask(mContext, mUri, this);
        mLoadingDrawableAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void onFinishLoadingData() {
        mPhotoData.photo = mLoadingDrawableAsyncTask.getResult();
    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {

    }
}
