package com.nicloud.workflowclient.data.connectserver.activity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.nicloud.workflowclient.data.connectserver.tasklog.OnLoadImageListener;
import com.nicloud.workflowclient.data.data.activity.PhotoData;


/**
 * Created by daz on 10/10/15.
 */
public class LoadingPhotoDataCommand implements LoadingDrawableAsyncTask.OnFinishLoadingDataListener {

    private LoadingDrawableAsyncTask mLoadingDrawableAsyncTask;
    private Context mContext;

    private Uri mUri;
    private PhotoData mPhotoData;
    private ImageView mPhotoImageView;


    public LoadingPhotoDataCommand(Context context, Uri uri, PhotoData photoData, ImageView photoImageView) {
        mContext = context;
        mUri = uri;
        mPhotoData = photoData;
        mPhotoImageView = photoImageView;
    }

    public void execute () {
        mLoadingDrawableAsyncTask = new LoadingDrawableAsyncTask(mContext, mUri, this);
        mLoadingDrawableAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onFinishLoadingData() {
        mPhotoData.photo = ((BitmapDrawable) mLoadingDrawableAsyncTask.getResult()).getBitmap();
        mPhotoImageView.setImageBitmap(mPhotoData.photo);
    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {

    }
}
