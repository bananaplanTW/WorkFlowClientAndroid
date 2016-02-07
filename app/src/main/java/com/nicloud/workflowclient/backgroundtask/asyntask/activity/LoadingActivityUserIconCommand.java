package com.nicloud.workflowclient.backgroundtask.asyntask.activity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.nicloud.workflowclient.data.activity.BaseData;


/**
 * Created by daz on 10/10/15.
 */
public class LoadingActivityUserIconCommand implements LoadingDrawableAsyncTask.OnFinishLoadingDataListener {

    private LoadingDrawableAsyncTask mLoadingDrawableAsyncTask;
    private Context mContext;

    private Uri mUri;
    private BaseData mBaseData;
    private ImageView mAvatar;


    public LoadingActivityUserIconCommand(Context context, Uri uri, BaseData baseData, ImageView avatar) {
        mContext = context;
        mUri = uri;
        mBaseData = baseData;
        mAvatar = avatar;
    }

    public void execute () {
        mLoadingDrawableAsyncTask = new LoadingDrawableAsyncTask(mContext, mUri, this);
        mLoadingDrawableAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onFinishLoadingData() {
        mBaseData.avatar = ((BitmapDrawable) mLoadingDrawableAsyncTask.getResult()).getBitmap();
        mAvatar.setImageBitmap(mBaseData.avatar);
    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {

    }
}
