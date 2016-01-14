package com.nicloud.workflowclient.serveraction.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.utility.RestfulUtils;
import com.nicloud.workflowclient.data.utility.URLUtils;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by logicmelody on 2016/1/14.
 */
public class MessageService extends IntentService {

    private static final String TAG = "MessageService";

    private static final int DEFAULT_LIMIT = 30;

    public static final class Action {
        public static final String LOAD_MESSAGE_NORMAL = "message_service_action_load_message_normal";
        public static final String LOAD_MESSAGE_FROM = "message_service_action_load_message_from";
        public static final String LOAD_MESSAGE_BEFORE = "message_service_action_load_message_before";
        public static final String SEND_MESSAGE = "message_service_action_send_message";
    }

    public static final class ExtraKey {
        public static final String WORKER_ID = "message_service_extra_worker_id";
        public static final String FROM_DATE_LONG = "message_service_extra_from_date";
        public static final String BEFORE_DATE_LONG = "message_service_extra_before_date";
    }

    private Handler mHandler;


    public MessageService() {
        super(TAG);
        mHandler = new Handler();
    }

    public static Intent generateLoadMessageNormalIntent(Context context, String workerId) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(Action.LOAD_MESSAGE_NORMAL);
        intent.putExtra(ExtraKey.WORKER_ID, workerId);

        return intent;
    }

    public static Intent generateLoadMessageFromIntent(Context context, String workerId, long fromDateLong) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(Action.LOAD_MESSAGE_FROM);
        intent.putExtra(ExtraKey.WORKER_ID, workerId);
        intent.putExtra(ExtraKey.FROM_DATE_LONG, fromDateLong);

        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (Action.LOAD_MESSAGE_NORMAL.equals(action)) {
            loadMessageNormal(intent);

        } else if (Action.LOAD_MESSAGE_FROM.equals(action)) {
            loadMessageFrom(intent);

        } else if (Action.LOAD_MESSAGE_BEFORE.equals(action)) {
            loadMessageBefore(intent);

        } else if (Action.SEND_MESSAGE.equals(action)) {
            sendMessage(intent);
        }
    }

    private void loadMessageNormal(Intent intent) {
        if (RestfulUtils.isConnectToInternet(this)) {
            String workerId = intent.getStringExtra(ExtraKey.WORKER_ID);

            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                String messagesJsonString = RestfulUtils.restfulGetRequest(getMessagesUrlNormal(workerId), headers);

                JSONObject messagesJson = new JSONObject(messagesJsonString);
                if (messagesJson.getString("status").equals("success")) {
                    JSONArray messageArray = messagesJson.getJSONObject("result").getJSONArray("messages");
                    insertMessagesToDb(messageArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }
    }

    private String getMessagesUrlNormal(String workerId) {
        HashMap<String, String> queries = new HashMap<>();
        queries.put("rud", workerId);
        queries.put("skip", String.valueOf(0));
        queries.put("limit", String.valueOf(DEFAULT_LIMIT));

        return URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                LoadingDataUtils.WorkingDataUrl.EndPoints.MESSAGES, queries);
    }

    private void loadMessageFrom(Intent intent) {
        if (RestfulUtils.isConnectToInternet(this)) {
            String workerId = intent.getStringExtra(ExtraKey.WORKER_ID);
            long fromDateLong = intent.getLongExtra(ExtraKey.FROM_DATE_LONG, -1L);

            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                String messagesJsonString
                        = RestfulUtils.restfulGetRequest(getMessagesUrlFrom(workerId, fromDateLong), headers);

                JSONObject messagesJson = new JSONObject(messagesJsonString);
                if (messagesJson.getString("status").equals("success")) {
                    JSONArray messageArray = messagesJson.getJSONObject("result").getJSONArray("messages");
                    insertMessagesToDb(messageArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }
    }

    private String getMessagesUrlFrom(String workerId, long fromDateLong) {
        HashMap<String, String> queries = new HashMap<>();
        queries.put("rud", workerId);
        queries.put("fromDateLong", String.valueOf(fromDateLong));

        return URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                LoadingDataUtils.WorkingDataUrl.EndPoints.MESSAGES, queries);
    }

    private void loadMessageBefore(Intent intent) {
        if (RestfulUtils.isConnectToInternet(this)) {
            String workerId = intent.getStringExtra(ExtraKey.WORKER_ID);
            long beforeDateLong = intent.getLongExtra(ExtraKey.BEFORE_DATE_LONG, -1L);

            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                String messagesJsonString
                        = RestfulUtils.restfulGetRequest(getMessagesUrlBefore(workerId, beforeDateLong), headers);

                JSONObject messagesJson = new JSONObject(messagesJsonString);
                if (messagesJson.getString("status").equals("success")) {
                    JSONArray messageArray = messagesJson.getJSONObject("result").getJSONArray("messages");
                    insertMessagesToDb(messageArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }
    }

    private String getMessagesUrlBefore(String workerId, long beforeDateLong) {
        HashMap<String, String> queries = new HashMap<>();
        queries.put("rud", workerId);
        queries.put("beforeDateLong", String.valueOf(beforeDateLong));

        return URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                LoadingDataUtils.WorkingDataUrl.EndPoints.MESSAGES, queries);
    }

    private void sendMessage(Intent intent) {

    }

    private void insertMessagesToDb(JSONArray messageArray) {
        for (int i = 0 ; i < messageArray.length() ; i++) {
            JSONObject messageJson = null;
            try {
                messageJson = messageArray.getJSONObject(i);
                String messageId = messageJson.getString("_id");
                String senderId = messageJson.getString("ownerId");
                String receiverId = messageJson.getString("employeeId");
                String content = messageJson.getString("content");
                long time = messageJson.getLong("createdAt");

                ContentValues values = new ContentValues();
                values.put(WorkFlowContract.Message.MESSAGE_ID, messageId);
                values.put(WorkFlowContract.Message.CONTENT, content);
                values.put(WorkFlowContract.Message.SENDER_ID, senderId);
                values.put(WorkFlowContract.Message.RECEIVER_ID, receiverId);
                values.put(WorkFlowContract.Message.TIME, time);

                getContentResolver().insert(WorkFlowContract.Message.CONTENT_URI, values);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
