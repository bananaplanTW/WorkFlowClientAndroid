package com.nicloud.workflowclient.messagechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by logicmelody on 2016/1/19.
 */
public class LoadPromptMessageReceiver extends BroadcastReceiver {

    public static final String ACTION_LOAD_PROMPT_MESSAGE = "action_load_prompt_message";

    public static final String EXTRA_SENDER_ID = "extra_sender_id";

    public interface OnLoadPromptMessageListener {
        void onLoadPromptMessage(Intent intent);
    }

    private OnLoadPromptMessageListener mOnLoadPromptMessageListener;


    public LoadPromptMessageReceiver(OnLoadPromptMessageListener listener) {
        mOnLoadPromptMessageListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnLoadPromptMessageListener.onLoadPromptMessage(intent);
    }
}
