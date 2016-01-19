package com.nicloud.workflowclient.messagechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by logicmelody on 2016/1/19.
 */
public class LoadMessageReceiver extends BroadcastReceiver {

    public static final String ACTION_LOAD_MESSAGE = "action_load_message";

    public static final String EXTRA_SENDER_ID = "extra_sender_id";

    public interface OnLoadMessageListener {
        void onLoadMessage(Intent intent);
    }

    private OnLoadMessageListener mOnLoadMessageListener;


    public LoadMessageReceiver(OnLoadMessageListener listener) {
        mOnLoadMessageListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnLoadMessageListener.onLoadMessage(intent);
    }
}
