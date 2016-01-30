package com.nicloud.workflowclient.backgroundtask.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.utility.utils.RestfulUtils;
import com.nicloud.workflowclient.utility.utils.URLUtils;
import com.nicloud.workflowclient.utility.utils.DbUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by logicmelody on 2015/12/10.
 */
public class ActionService extends IntentService {

    private static final String TAG = "ActionService";

    public static class ServerAction {
        public static final String CHECK_ITEM = "server_action_check_item";
        public static final String COMPLETE_TASK = "server_action_complete_task";
    }

    public static class ExtraKey {
        public static final String TASK_ID = "extra_task_id";
        public static final String TASK_NAME = "extra_task_name";
        public static final String WORKER_ID = "extra_worker_id";
        public static final String ACTION_SUCCESSFUL = "extra_action_successful";

        // Check item
        public static final String CHECK_ITEM_INDEX = "extra_check_item_index";
        public static final String CHECK_ITEM_CHECKED = "extra_check_item_checked";
    }


    /**
     * Creates an IntentService.
     * Invoked by your subclass's constructor.
     */
    public ActionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (ServerAction.CHECK_ITEM.equals(action)) {
            checkItem(intent);

        } else if(ServerAction.COMPLETE_TASK.equals(action)) {
            completeTask(intent);
        }
    }

    private void checkItem(Intent intent) {
        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        int position = intent.getIntExtra(ExtraKey.CHECK_ITEM_INDEX, 0);
        boolean checked = intent.getBooleanExtra(ExtraKey.CHECK_ITEM_CHECKED, false);

        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            HashMap<String, String> bodies = new HashMap<>();
            bodies.put("td", taskId);
            bodies.put("tx", String.valueOf(position));
            bodies.put("tc", String.valueOf(checked));

            String urlString = URLUtils
                    .buildURLString(LoadingDataUtils.sBaseUrl, LoadingDataUtils.WorkingDataUrl.EndPoints.CHECK_TASK, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    DbUtils.setCheckItem(this, taskId, position, checked);
                    return;
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in checkitem() in ActionService");
            e.printStackTrace();

            return;
        }
    }

    private void completeTask(Intent intent) {
        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String taskName = intent.getStringExtra(ExtraKey.TASK_NAME);

        Intent broadcastIntent = new Intent(ServerAction.COMPLETE_TASK);
        broadcastIntent.putExtra(ExtraKey.TASK_NAME, taskName);

        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            HashMap<String, String> bodies = new HashMap<>();
            bodies.put("td", taskId);

            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl, LoadingDataUtils.WorkingDataUrl.EndPoints.COMPLETE_TASK, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    broadcastIntent.putExtra(ExtraKey.ACTION_SUCCESSFUL, true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                    return;
                }
            }
        }  catch (JSONException e) {
            broadcastIntent.putExtra(ExtraKey.ACTION_SUCCESSFUL, false);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

            Log.e(TAG, "Exception in CompleteTaskForWorkerStrategy() in ActionService");
            e.printStackTrace();

            return;
        }

        broadcastIntent.putExtra(ExtraKey.ACTION_SUCCESSFUL, false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
}
