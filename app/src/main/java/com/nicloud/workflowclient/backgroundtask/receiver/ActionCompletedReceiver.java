package com.nicloud.workflowclient.backgroundtask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by logicmelody on 2015/12/10.
 */
public class ActionCompletedReceiver extends BroadcastReceiver {

    public interface OnServerActionCompletedListener {
        void onServerActionCompleted(Intent intent);
    }

    private OnServerActionCompletedListener mOnServerActionCompletedListener;


    public ActionCompletedReceiver(OnServerActionCompletedListener listener) {
        mOnServerActionCompletedListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnServerActionCompletedListener.onServerActionCompleted(intent);
    }
}
