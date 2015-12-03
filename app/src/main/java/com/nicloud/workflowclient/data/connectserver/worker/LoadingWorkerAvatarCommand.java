package com.nicloud.workflowclient.data.connectserver.worker;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingDrawableAsyncTask;
import com.nicloud.workflowclient.data.data.data.WorkingData;


/**
 * Created by daz on 10/10/15.
 */
public class LoadingWorkerAvatarCommand implements LoadingDrawableAsyncTask.OnFinishLoadingDataListener {

    private Context mContext;
    private Uri mUri;
    private ImageView mAvatar;

    private LoadingDrawableAsyncTask mLoadingDrawableAsyncTask;


    public LoadingWorkerAvatarCommand(Context context, Uri uri, ImageView avatar) {
        mContext = context;
        mUri = uri;
        mAvatar = avatar;
    }

    public void execute () {
        mLoadingDrawableAsyncTask = new LoadingDrawableAsyncTask(mContext, mUri, this);
        mLoadingDrawableAsyncTask.execute();
    }

    @Override
    public void onFinishLoadingData() {
        mAvatar.setImageDrawable(mLoadingDrawableAsyncTask.getResult());
        WorkingData.getInstance(mContext).getLoginWorker().avatar = mAvatar.getDrawable();
    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {
        if (WorkingData.getInstance(mContext).getLoginWorker().avatar == null) {
            mAvatar.setImageResource(R.drawable.ic_worker);
        } else {
            mAvatar.setImageDrawable(WorkingData.getInstance(mContext).getLoginWorker().avatar);
        }
    }
}
