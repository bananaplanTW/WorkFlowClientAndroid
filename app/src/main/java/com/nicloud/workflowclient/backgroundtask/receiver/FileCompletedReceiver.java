package com.nicloud.workflowclient.backgroundtask.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by logicmelody on 2016/1/18.
 */
public class FileCompletedReceiver extends BroadcastReceiver {

    public static final String ACTION_LOAD_FILES_COMPLETED = "action_load_files_completed";

    public interface OnLoadFileCompletedListener {
        void onLoadFileCompleted(Intent intent);
    }

    private OnLoadFileCompletedListener mOnLoadFileCompletedListener;


    public FileCompletedReceiver(OnLoadFileCompletedListener listener) {
        mOnLoadFileCompletedListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mOnLoadFileCompletedListener.onLoadFileCompleted(intent);
    }
}
