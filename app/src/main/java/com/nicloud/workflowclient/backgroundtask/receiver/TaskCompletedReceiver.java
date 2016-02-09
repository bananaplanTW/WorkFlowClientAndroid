package com.nicloud.workflowclient.backgroundtask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by logicmelody on 2016/1/18.
 */
public class TaskCompletedReceiver extends BroadcastReceiver {

    public static final String ACTION_LOAD_TASKS_COMPLETED = "action_load_tasks_completed";

    public static final String EXTRA_FROM = "extra_task_from";
    public static final String EXTRA_LOAD_TYPE = "extra_task_load_type";

    public static final class From {
        public static final String LOAD_FIRST = "from_my_task_first";
        public static final String LOAD_NORMAL = "from_my_task";
        public static final String LOAD_MY_TASKS = "from_load_my_tasks";
        public static final String LOAD_CASE_TASKS = "from_load_case_tasks";
        public static final String LOAD_TASK_BY_ID = "from_load_task_by_id";
        public static final String LOAD_TASK_ACTIVITIES = "from_load_task_activities";
        public static final String ASSIGN_TASK_TO_WORKER = "from_assign_task_to_worker";
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
