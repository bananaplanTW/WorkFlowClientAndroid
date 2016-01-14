package com.nicloud.workflowclient.serveraction.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by logicmelody on 2015/12/10.
 */
public class UploadCompletedReceiver extends BroadcastReceiver {

    public interface OnUploadCompletedListener {
        void onUploadCompletedListener(Intent intent);
    }

    private OnUploadCompletedListener mOnUploadCompletedListener;


    public UploadCompletedReceiver(OnUploadCompletedListener listener) {
        mOnUploadCompletedListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnUploadCompletedListener.onUploadCompletedListener(intent);
    }
}
