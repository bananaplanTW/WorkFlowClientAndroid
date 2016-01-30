package com.nicloud.workflowclient.backgroundtask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by logicmelody on 2016/1/18.
 */
public class TaskCompletedReceiver extends BroadcastReceiver {

    public static final String ACTION_LOAD_TASKS_COMPLETED = "action_load_tasks_completed";

    public static final String EXTRA_FROM = "extra_task_receive_from";

    public static final class From {
        public static final String MY_TASK_FIRST = "from_my_task_first";
        public static final String MY_TASK = "from_my_task";
        public static final String DETAILED_TASK = "from_detailed_task";
    }

    public interface OnLoadTaskCompletedListener {
        void onLoadTaskCompleted(Intent intent);
    }

    private OnLoadTaskCompletedListener mOnLoadTaskCompletedListener;


    public TaskCompletedReceiver(OnLoadTaskCompletedListener listener) {
        mOnLoadTaskCompletedListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnLoadTaskCompletedListener.onLoadTaskCompleted(intent);
    }
}
