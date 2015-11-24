package com.nicloud.workflowclientandroid.data.connectserver.worker;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.data.connectserver.activity.LoadingDrawableAsyncTask;
import com.nicloud.workflowclientandroid.data.connectserver.tasklog.OnLoadImageListener;
import com.nicloud.workflowclientandroid.data.data.activity.PhotoData;


/**
 * Created by daz on 10/10/15.
 */
public class LoadingWorkerAvatarCommand implements LoadingDrawableAsyncTask.OnFinishLoadingDataListener {

    private LoadingDrawableAsyncTask mLoadingDrawableAsyncTask;
    private Context mContext;
    private Uri mUri;
    private ImageView mAvatar;


    public LoadingWorkerAvatarCommand(Context context, Uri uri, ImageView avatar) {
        mContext = context;
        mUri = uri;
        mAvatar = avatar;
    }

    public void execute () {
        mLoadingDrawableAsyncTask = new LoadingDrawableAsyncTask(mContext, mUri, this);
        mLoadingDrawableAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void onFinishLoadingData() {
        mAvatar.setImageDrawable(mLoadingDrawableAsyncTask.getResult());
    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {
        mAvatar.setImageResource(R.drawable.ic_worker);
    }
}
