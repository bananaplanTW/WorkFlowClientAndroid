package com.nicloud.workflowclient.backgroundtask.asyntask.worker;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.nicloud.workflowclient.backgroundtask.asyntask.activity.LoadingDrawableAsyncTask;
import com.nicloud.workflowclient.data.data.Worker;


/**
 * Created by daz on 10/10/15.
 */
public class LoadingWorkerAvatar implements LoadingDrawableAsyncTask.OnFinishLoadingDataListener {

    private Context mContext;
    private Uri mUri;
    private ImageView mAvatar;

    private Worker mWorker;
    private int mDefaultWorkerIconId;

    private LoadingDrawableAsyncTask mLoadingDrawableAsyncTask;


    public LoadingWorkerAvatar(Context context, Uri uri,
                               ImageView avatar, Worker worker, int defaultWorkerIconId) {
        mContext = context;
        mUri = uri;
        mAvatar = avatar;
        mWorker = worker;
        mDefaultWorkerIconId = defaultWorkerIconId;
    }

    public void execute () {
        mLoadingDrawableAsyncTask = new LoadingDrawableAsyncTask(mContext, mUri, this);
        mLoadingDrawableAsyncTask.execute();
    }

    @Override
    public void onFinishLoadingData() {
        if (mAvatar != null) {
            mAvatar.setImageDrawable(mLoadingDrawableAsyncTask.getResult());
        }

        mWorker.avatar = mLoadingDrawableAsyncTask.getResult();
    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {
        if (mAvatar == null) return;

        if (mWorker.avatar == null) {
            mAvatar.setImageResource(mDefaultWorkerIconId);
        } else {
            mAvatar.setImageDrawable(mWorker.avatar);
        }
    }
}
