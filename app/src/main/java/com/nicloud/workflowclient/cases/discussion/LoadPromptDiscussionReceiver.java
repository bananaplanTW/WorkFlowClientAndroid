package com.nicloud.workflowclient.cases.discussion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by logicmelody on 2016/1/19.
 */
public class LoadPromptDiscussionReceiver extends BroadcastReceiver {

    public static final String ACTION_LOAD_PROMPT_DISCUSSION = "action_load_prompt_discussion";

    public static final String EXTRA_CASE_ID = "extra_case_id";

    public interface OnLoadPromptDiscussionListener {
        void onLoadPromptDiscussion(Intent intent);
    }

    private OnLoadPromptDiscussionListener mOnLoadPromptDiscussionListener;


    public LoadPromptDiscussionReceiver(OnLoadPromptDiscussionListener listener) {
        mOnLoadPromptDiscussionListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnLoadPromptDiscussionListener.onLoadPromptDiscussion(intent);
    }
}
