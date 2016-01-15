package com.nicloud.workflowclient.serveraction.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by logicmelody on 2016/1/18.
 */
public class MessageCompletedReceiver extends BroadcastReceiver {

    public static final String ACTION_LOAD_BEFORE_MESSAGE_COMPLETED = "action_load_before_message_completed";

    public interface OnMessageCompletedListener {
        void onMessageCompleted(Intent intent);
    }

    private OnMessageCompletedListener mOnMessageCompletedListener;


    public MessageCompletedReceiver(OnMessageCompletedListener listener) {
        mOnMessageCompletedListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnMessageCompletedListener.onMessageCompleted(intent);
    }
}
