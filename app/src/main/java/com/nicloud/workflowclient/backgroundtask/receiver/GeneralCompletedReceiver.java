package com.nicloud.workflowclient.backgroundtask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by logicmelody on 2015/12/10.
 */
public class GeneralCompletedReceiver extends BroadcastReceiver {

    public static final String ACTION_GENERAL_COMPLETED = "action_general_completed";

    public interface OnGeneralCompletedListener {
        void onGeneralCompleted(Intent intent);
    }

    private OnGeneralCompletedListener mOnGeneralCompletedListener;


    public GeneralCompletedReceiver(OnGeneralCompletedListener listener) {
        mOnGeneralCompletedListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnGeneralCompletedListener.onGeneralCompleted(intent);
    }
}
