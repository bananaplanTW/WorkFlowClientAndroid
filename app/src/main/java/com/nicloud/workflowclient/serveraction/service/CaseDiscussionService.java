package com.nicloud.workflowclient.serveraction.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.utility.RestfulUtils;
import com.nicloud.workflowclient.data.utility.URLUtils;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.serveraction.receiver.MessageCompletedReceiver;
import com.nicloud.workflowclient.utility.JsonUtils;
import com.nicloud.workflowclient.utility.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by logicmelody on 2016/1/14.
 */
public class CaseDiscussionService extends IntentService {

    private static final String TAG = "CaseDiscussionService";

    private static final int DEFAULT_LIMIT = 30;

    public static final class Action {
        public static final String LOAD_DISCUSSION_NORMAL = "case_discussion_service_action_load_discussion_normal";
        public static final String LOAD_DISCUSSION_FROM = "case_discussion_service_action_load_discussion_from";
        public static final String LOAD_DISCUSSION_BEFORE = "case_discussion_service_action_load_discussion_before";
        public static final String SEND_DISCUSSION_MESSAGE = "case_discussion_service_action_send_discussion_message";
        public static final String SEND_DISCUSSION_IMAGE = "case_discussion_service_action_send_discussion_image";
        public static final String SEND_DISCUSSION_FILE = "case_discussion_service_action_send_discussion_file";
    }

    public static final class ExtraKey {
        public static final String CASE_ID = "case_discussion_service_extra_case_id";
        public static final String FROM_DATE_LONG = "case_discussion_service_extra_from_date";
        public static final String BEFORE_DATE_LONG = "case_discussion_service_extra_before_date";
        public static final String MESSAGE = "case_discussion_service_extra_message";
        public static final String FILE_PATH = "case_discussion_service_extra_file_path";
    }

    private Handler mHandler;


    public CaseDiscussionService() {
        super(TAG);
        mHandler = new Handler();
    }

    public static Intent generateLoadDiscussionNormalIntent(Context context, String caseId) {
        Intent intent = new Intent(context, CaseDiscussionService.class);
        intent.setAction(Action.LOAD_DISCUSSION_NORMAL);
        intent.putExtra(ExtraKey.CASE_ID, caseId);

        return intent;
    }

    public static Intent generateLoadDiscussionFromIntent(Context context, String caseId, long fromDateLong) {
        Intent intent = new Intent(context, CaseDiscussionService.class);
        intent.setAction(Action.LOAD_DISCUSSION_FROM);
        intent.putExtra(ExtraKey.CASE_ID, caseId);
        intent.putExtra(ExtraKey.FROM_DATE_LONG, fromDateLong);

        return intent;
    }

    public static Intent generateLoadDiscussionBeforeIntent(Context context, String caseId, long beforeDateLong) {
        Intent intent = new Intent(context, CaseDiscussionService.class);
        intent.setAction(Action.LOAD_DISCUSSION_BEFORE);
        intent.putExtra(ExtraKey.CASE_ID, caseId);
        intent.putExtra(ExtraKey.BEFORE_DATE_LONG, beforeDateLong);

        return intent;
    }

    public static Intent generateSendMessageIntent(Context context, String caseId, String message) {
        Intent intent = new Intent(context, CaseDiscussionService.class);
        intent.setAction(Action.SEND_DISCUSSION_MESSAGE);
        intent.putExtra(ExtraKey.CASE_ID, caseId);
        intent.putExtra(ExtraKey.MESSAGE, message);

        return intent;
    }

    public static Intent generateSendImageIntent(Context context, String caseId, String filePath) {
        Intent intent = new Intent(context, CaseDiscussionService.class);
        intent.setAction(Action.SEND_DISCUSSION_IMAGE);
        intent.putExtra(ExtraKey.CASE_ID, caseId);
        intent.putExtra(ExtraKey.FILE_PATH, filePath);

        return intent;
    }

    public static Intent generateSendFileIntent(Context context, String caseId, String filePath) {
        Intent intent = new Intent(context, CaseDiscussionService.class);
        intent.setAction(Action.SEND_DISCUSSION_FILE);
        intent.putExtra(ExtraKey.CASE_ID, caseId);
        intent.putExtra(ExtraKey.FILE_PATH, filePath);

        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (Action.LOAD_DISCUSSION_NORMAL.equals(action)) {
            loadDiscussionNormal(intent);

        } else if (Action.LOAD_DISCUSSION_FROM.equals(action)) {
            loadDiscussionFrom(intent);

        } else if (Action.LOAD_DISCUSSION_BEFORE.equals(action)) {
            loadDiscussionBefore(intent);

        } else if (Action.SEND_DISCUSSION_MESSAGE.equals(action)) {
            sendDiscussionMessage(intent);

        } else if (Action.SEND_DISCUSSION_IMAGE.equals(action)) {
            sendDiscussionImage(intent);

        } else if (Action.SEND_DISCUSSION_FILE.equals(action)) {
            sendDiscussionFile(intent);
        }
    }

    private void loadDiscussionNormal(Intent intent) {
        if (RestfulUtils.isConnectToInternet(this)) {
            String caseId = intent.getStringExtra(ExtraKey.CASE_ID);

            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                String messagesJsonString = RestfulUtils.restfulGetRequest(getDiscussionUrlNormal(caseId), headers);

                JSONObject messagesJson = new JSONObject(messagesJsonString);
                if (messagesJson.getString("status").equals("success")) {
                    JSONArray messageArray = messagesJson.getJSONObject("result").getJSONArray("discussions");
                    insertDiscussionToDb(messageArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }
    }

    private String getDiscussionUrlNormal(String caseId) {
        HashMap<String, String> queries = new HashMap<>();
        queries.put("caseId", caseId);
        queries.put("skip", String.valueOf(0));
        queries.put("limit", String.valueOf(DEFAULT_LIMIT));

        return URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                LoadingDataUtils.WorkingDataUrl.EndPoints.DISCUSSION, queries);
    }

    private void loadDiscussionFrom(Intent intent) {
        if (RestfulUtils.isConnectToInternet(this)) {
            String caseId = intent.getStringExtra(ExtraKey.CASE_ID);
            long fromDateLong = intent.getLongExtra(ExtraKey.FROM_DATE_LONG, -1L);

            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                String messagesJsonString
                        = RestfulUtils.restfulGetRequest(getDiscussionUrlFrom(caseId, fromDateLong), headers);

                JSONObject messagesJson = new JSONObject(messagesJsonString);
                if (messagesJson.getString("status").equals("success")) {
                    JSONArray messageArray = messagesJson.getJSONObject("result").getJSONArray("discussions");
                    insertDiscussionToDb(messageArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }
    }

    private String getDiscussionUrlFrom(String caseId, long fromDateLong) {
        HashMap<String, String> queries = new HashMap<>();
        queries.put("caseId", caseId);
        queries.put("fromDateLong", String.valueOf(fromDateLong));

        return URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                LoadingDataUtils.WorkingDataUrl.EndPoints.DISCUSSION, queries);
    }

    private void loadDiscussionBefore(Intent intent) {
        Intent broadcastIntent = new Intent(MessageCompletedReceiver.ACTION_LOAD_BEFORE_MESSAGE_COMPLETED);

        if (RestfulUtils.isConnectToInternet(this)) {
            String caseId = intent.getStringExtra(ExtraKey.CASE_ID);
            long beforeDateLong = intent.getLongExtra(ExtraKey.BEFORE_DATE_LONG, -1L);

            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                String messagesJsonString
                        = RestfulUtils.restfulGetRequest(getDiscussionUrlBefore(caseId, beforeDateLong), headers);

                JSONObject messagesJson = new JSONObject(messagesJsonString);
                if (messagesJson.getString("status").equals("success")) {
                    JSONArray messageArray = messagesJson.getJSONObject("result").getJSONArray("discussions");
                    insertDiscussionToDb(messageArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private String getDiscussionUrlBefore(String caseId, long beforeDateLong) {
        HashMap<String, String> queries = new HashMap<>();
        queries.put("caseId", caseId);
        queries.put("beforeDateLong", String.valueOf(beforeDateLong));

        return URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                LoadingDataUtils.WorkingDataUrl.EndPoints.DISCUSSION, queries);
    }

    private void sendDiscussionMessage(Intent intent) {
        if (RestfulUtils.isConnectToInternet(this)) {
            String caseId = intent.getStringExtra(ExtraKey.CASE_ID);
            String message = intent.getStringExtra(ExtraKey.MESSAGE);

            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                HashMap<String, String> bodies = new HashMap<>();
                bodies.put("caseId", caseId);
                bodies.put("msg", message);

                String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                        LoadingDataUtils.WorkingDataUrl.EndPoints.DISCUSSION, null);
                String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

                if (responseString != null) {
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.getString("status").equals("success")) {
                        insertDiscussionToDb(jsonObject.getJSONObject("result"));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }
    }

    private void sendDiscussionImage(Intent intent) {
        if (RestfulUtils.isConnectToInternet(this)) {
            String caseId = intent.getStringExtra(ExtraKey.CASE_ID);
            String filePath = intent.getStringExtra(ExtraKey.FILE_PATH);

            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());
                headers.put("cd", caseId);

                String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                        LoadingDataUtils.WorkingDataUrl.EndPoints.SEND_DISCUSSION_IMAGE, null);

                String responseString = RestfulUtils.restfulPostFileRequest(urlString, headers, filePath);

                if (responseString != null) {
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.getString("status").equals("success")) {
                        insertDiscussionToDb(jsonObject.getJSONObject("result"));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }
    }

    private void sendDiscussionFile(Intent intent) {
        if (RestfulUtils.isConnectToInternet(this)) {
            String caseId = intent.getStringExtra(ExtraKey.CASE_ID);
            String filePath = intent.getStringExtra(ExtraKey.FILE_PATH);

            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());
                headers.put("cd", caseId);

                String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                        LoadingDataUtils.WorkingDataUrl.EndPoints.SEND_DISCUSSION_FILE, null);

                String responseString = RestfulUtils.restfulPostFileRequest(urlString, headers, filePath);

                if (responseString != null) {
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.getString("status").equals("success")) {
                        insertDiscussionToDb(jsonObject.getJSONObject("result"));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }
    }

    private void insertDiscussionToDb(JSONArray messageArray) {
        for (int i = 0 ; i < messageArray.length() ; i++) {
            try {
                insertDiscussionToDb(messageArray.getJSONObject(i));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertDiscussionToDb(JSONObject jsonObject) {
        try {
            String discussionId = jsonObject.getString("_id");
            String caseId = jsonObject.getString("caseId");
            String workerId = jsonObject.getString("ownerId");
            String workerName = jsonObject.getString("ownerName");
            String workerAvatarUri = jsonObject.getString("iconThumbUrl");
            String content = jsonObject.getString("content");
            String type = jsonObject.getString("contentType");
            String fileName = JsonUtils.getStringFromJson(jsonObject, "name");
            String fileUri = JsonUtils.getStringFromJson(jsonObject, "fileUrl");
            String fileThumbUri = JsonUtils.getStringFromJson(jsonObject, "thumbUrl");
            long createdTime = jsonObject.getLong("createdAt");
            long updatedTime = jsonObject.getLong("updatedAt");

            ContentValues values = new ContentValues();
            values.put(WorkFlowContract.Discussion.DISCUSSION_ID, discussionId);
            values.put(WorkFlowContract.Discussion.CASE_ID, caseId);
            values.put(WorkFlowContract.Discussion.WORKER_ID, workerId);
            values.put(WorkFlowContract.Discussion.WORKER_NAME, workerName);
            values.put(WorkFlowContract.Discussion.WORKER_AVATAR_URI, workerAvatarUri);
            values.put(WorkFlowContract.Discussion.CONTENT, content);
            values.put(WorkFlowContract.Discussion.TYPE, type);
            values.put(WorkFlowContract.Discussion.FILE_NAME, fileName);
            values.put(WorkFlowContract.Discussion.FILE_URI, fileUri);
            values.put(WorkFlowContract.Discussion.FILE_THUMB_URI, fileThumbUri);
            values.put(WorkFlowContract.Discussion.CREATED_TIME, createdTime);
            values.put(WorkFlowContract.Discussion.UPDATED_TIME, updatedTime);

            getContentResolver().insert(WorkFlowContract.Discussion.CONTENT_URI, values);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
