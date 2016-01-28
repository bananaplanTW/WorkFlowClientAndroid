package com.nicloud.workflowclient.backgroundtask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by logicmelody on 2016/1/18.
 */
public class CaseDiscussionCompletedReceiver extends BroadcastReceiver {

    public static final String ACTION_LOAD_BEFORE_DISCUSSION_COMPLETED = "action_load_before_discussion_completed";

    public interface OnLoadCaseDiscussionCompletedListener {
        void onLoadCaseDiscussionCompleted(Intent intent);
    }

    private OnLoadCaseDiscussionCompletedListener mOnLoadCaseDiscussionCompletedListener;


    public CaseDiscussionCompletedReceiver(OnLoadCaseDiscussionCompletedListener listener) {
        mOnLoadCaseDiscussionCompletedListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnLoadCaseDiscussionCompletedListener.onLoadCaseDiscussionCompleted(intent);
    }
}
