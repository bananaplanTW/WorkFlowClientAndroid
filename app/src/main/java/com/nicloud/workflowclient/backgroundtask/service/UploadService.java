package com.nicloud.workflowclient.backgroundtask.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.utility.utils.RestfulUtils;
import com.nicloud.workflowclient.utility.utils.URLUtils;
import com.nicloud.workflowclient.utility.utils.DbUtils;
import com.nicloud.workflowclient.utility.utils.NotificationUtils;
import com.nicloud.workflowclient.utility.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by logicmelody on 2015/12/11.
 */
public class UploadService extends IntentService {

    private static final String TAG = "UploadService";

    public static class UploadAction {
        public static final String TASK_CHECK_ITEM = "upload_action_task_check_item";
        public static final String TASK_TEXT = "upload_action_task_text";
        public static final String TASK_PHOTO = "upload_action_task_photo";
        public static final String TASK_FILE = "upload_action_task_file";
        public static final String CASE_PHOTO = "upload_action_case_photo";
        public static final String CASE_FILE = "upload_action_case_file";
        public static final String UPLOAD_COMPLETED = "upload_action_completed";
    }

    public static class ExtraKey {
        public static final String TASK_ID = "extra_task_id";
        public static final String CASE_ID = "extra_case_id";
        public static final String CHECK_ITEM_NAME = "extra_check_item_name";
        public static final String TEXT = "extra_text";
        public static final String PHOTO_PATH = "extra_photo_path";
        public static final String FILE_PATH = "extra_file_path";
        public static final String UPLOAD_SUCCESSFUL = "extra_upload_successful";
        public static final String FROM_ACTION = "extra_from_action";
    }

    private NotificationManager mNotificationManager;

    private Handler mHandler;


    public static Intent generateUploadTaskCheckItemIntent(Context context, String taskId, String checkItemName) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(UploadAction.TASK_CHECK_ITEM);
        intent.putExtra(ExtraKey.TASK_ID, taskId);
        intent.putExtra(ExtraKey.CHECK_ITEM_NAME, checkItemName);

        return intent;
    }

    public static Intent generateUploadTaskTextIntent(Context context, String taskId, String text) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(UploadAction.TASK_TEXT);
        intent.putExtra(ExtraKey.TASK_ID, taskId);
        intent.putExtra(ExtraKey.TEXT, text);

        return intent;
    }

    public static Intent generateUploadTaskPhotoIntent(Context context, String taskId, String photoPath) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(UploadAction.TASK_PHOTO);
        intent.putExtra(ExtraKey.TASK_ID, taskId);
        intent.putExtra(ExtraKey.PHOTO_PATH, photoPath);

        return intent;
    }

    public static Intent generateUploadTaskFileIntent(Context context, String taskId, String filePath) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(UploadAction.TASK_FILE);
        intent.putExtra(ExtraKey.TASK_ID, taskId);
        intent.putExtra(ExtraKey.FILE_PATH, filePath);

        return intent;
    }

    public static Intent generateUploadCasePhotoIntent(Context context, String caseId, String filePath) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(UploadAction.CASE_PHOTO);
        intent.putExtra(ExtraKey.CASE_ID, caseId);
        intent.putExtra(ExtraKey.FILE_PATH, filePath);

        return intent;
    }

    public static Intent generateUploadCaseFileIntent(Context context, String caseId, String filePath) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(UploadAction.CASE_FILE);
        intent.putExtra(ExtraKey.CASE_ID, caseId);
        intent.putExtra(ExtraKey.FILE_PATH, filePath);

        return intent;
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UploadService() {
        super(TAG);
        mHandler = new Handler();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (UploadAction.TASK_TEXT.equals(action)) {
            uploadTaskText(intent);

        } else if (UploadAction.TASK_PHOTO.equals(action)) {
            uploadTaskPhoto(intent);

        } else if (UploadAction.TASK_FILE.equals(action)) {
            uploadTaskFile(intent);

        } else if (UploadAction.TASK_CHECK_ITEM.equals(action)) {
            uploadTaskCheckItem(intent);

        } else if (UploadAction.CASE_PHOTO.equals(action)) {
            uploadCasePhoto(intent);

        } else if (UploadAction.CASE_FILE.equals(action)) {
            uploadCaseFile(intent);
        }
    }

    private void uploadTaskText(Intent intent) {
        int notificationId = Utils.generateNotificationId();

        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String text = intent.getStringExtra(ExtraKey.TEXT);

        Intent broadcastIntent = new Intent(UploadAction.UPLOAD_COMPLETED);
        broadcastIntent.putExtra(ExtraKey.FROM_ACTION, UploadAction.TASK_TEXT);

        NotificationCompat.Builder builder
                = NotificationUtils.
                generateUploadNotificationBuilder(this,
                        DbUtils.getTaskNameById(this, taskId), getString(R.string.add_log_uploading_text));
        mNotificationManager.notify(notificationId, builder.build());

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("td", taskId);
        bodies.put("msg", text);

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_TEXT_ACTIVITY_TO_TASK, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    NotificationUtils.uploadSuccessfully(builder, getString(R.string.add_log_complete_text));
                    mNotificationManager.notify(notificationId, builder.build());

                    Utils.showToastInNonUiThread(mHandler, this, getString(R.string.add_log_complete_text));

                    broadcastIntent.putExtra(ExtraKey.UPLOAD_SUCCESSFUL, true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                    return;
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in uploadTaskText() in UploadService");
            e.printStackTrace();

            NotificationUtils.uploadFailed(builder, getString(R.string.upload_failed));
            mNotificationManager.notify(notificationId, builder.build());

            return;
        }

        NotificationUtils.uploadFailed(builder, getString(R.string.upload_failed));
        mNotificationManager.notify(notificationId, builder.build());
    }

    private void uploadTaskPhoto(Intent intent) {
        int notificationId = Utils.generateNotificationId();

        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String photoPath = intent.getStringExtra(ExtraKey.PHOTO_PATH);

        Intent broadcastIntent = new Intent(UploadAction.UPLOAD_COMPLETED);
        broadcastIntent.putExtra(ExtraKey.FROM_ACTION, UploadAction.TASK_FILE);

        NotificationCompat.Builder builder
                = NotificationUtils.
                generateUploadNotificationBuilder(this, DbUtils.getTaskNameById(this, taskId),
                        getString(R.string.uploading_image));
        mNotificationManager.notify(notificationId, builder.build());

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());
        headers.put("td", taskId);

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_IMAGE_ACTIVITY_TO_TASK, null);
            String responseString = RestfulUtils.restfulPostFileRequest(urlString, headers, photoPath);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    NotificationUtils.uploadSuccessfully(builder, getString(R.string.complete_uploading_image));
                    mNotificationManager.notify(notificationId, builder.build());

                    Utils.showToastInNonUiThread(mHandler, this, getString(R.string.complete_uploading_image));

                    broadcastIntent.putExtra(ExtraKey.UPLOAD_SUCCESSFUL, true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                    return;
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in uploadTaskText() in UploadService");
            e.printStackTrace();

            NotificationUtils.uploadFailed(builder, getString(R.string.upload_failed));
            mNotificationManager.notify(notificationId, builder.build());

            return;
        }

        NotificationUtils.uploadFailed(builder, getString(R.string.upload_failed));
        mNotificationManager.notify(notificationId, builder.build());
    }

    private void uploadTaskFile(Intent intent) {
        int notificationId = Utils.generateNotificationId();

        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String filePath = intent.getStringExtra(ExtraKey.FILE_PATH);

        Intent broadcastIntent = new Intent(UploadAction.UPLOAD_COMPLETED);
        broadcastIntent.putExtra(ExtraKey.FROM_ACTION, UploadAction.TASK_FILE);

        NotificationCompat.Builder builder
                = NotificationUtils.
                generateUploadNotificationBuilder(this,
                        DbUtils.getTaskNameById(this, taskId), getString(R.string.uploading_file));
        mNotificationManager.notify(notificationId, builder.build());

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());
        headers.put("td", taskId);

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_FILE_ACTIVITY_TO_TASK, null);
            String responseString = RestfulUtils.restfulPostFileRequest(urlString, headers, filePath);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    NotificationUtils.uploadSuccessfully(builder, getString(R.string.complete_uploading_file));
                    mNotificationManager.notify(notificationId, builder.build());

                    Utils.showToastInNonUiThread(mHandler, this, getString(R.string.complete_uploading_file));

                    broadcastIntent.putExtra(ExtraKey.UPLOAD_SUCCESSFUL, true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                    return;
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in uploadTaskText() in UploadService");
            e.printStackTrace();

            NotificationUtils.uploadFailed(builder, getString(R.string.upload_failed));
            mNotificationManager.notify(notificationId, builder.build());

            return;
        }

        NotificationUtils.uploadFailed(builder, getString(R.string.upload_failed));
        mNotificationManager.notify(notificationId, builder.build());
    }

    private void uploadTaskCheckItem(Intent intent) {
        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String checkItemName = intent.getStringExtra(ExtraKey.CHECK_ITEM_NAME);

        Intent broadcastIntent = new Intent(UploadAction.UPLOAD_COMPLETED);
        broadcastIntent.putExtra(ExtraKey.FROM_ACTION, UploadAction.TASK_CHECK_ITEM);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("td", taskId);
        bodies.put("todoName", checkItemName);

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.ADD_CHECK_ITEM_TO_TASK, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    broadcastIntent.putExtra(ExtraKey.UPLOAD_SUCCESSFUL, true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                    return;
                }
            }
        }  catch (JSONException e) {
            Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            Log.e(TAG, "Exception in uploadTaskText() in UploadService");
            e.printStackTrace();

            return;
        }

        Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
    }

    private void uploadCasePhoto(Intent intent) {
        int notificationId = Utils.generateNotificationId();

        String caseId = intent.getStringExtra(ExtraKey.CASE_ID);
        String filePath = intent.getStringExtra(ExtraKey.FILE_PATH);

        Intent broadcastIntent = new Intent(UploadAction.UPLOAD_COMPLETED);
        broadcastIntent.putExtra(ExtraKey.FROM_ACTION, UploadAction.CASE_FILE);

        NotificationCompat.Builder builder
                = NotificationUtils.generateUploadNotificationBuilder(this,
                DbUtils.getCaseById(this, caseId).name, getString(R.string.uploading_file));
        mNotificationManager.notify(notificationId, builder.build());

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());
        headers.put("cd", caseId);

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.CASE_UPLOAD_IMAGE, null);
            String responseString = RestfulUtils.restfulPostFileRequest(urlString, headers, filePath);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    NotificationUtils.uploadSuccessfully(builder, getString(R.string.complete_uploading_file));
                    mNotificationManager.notify(notificationId, builder.build());

                    Utils.showToastInNonUiThread(mHandler, this, getString(R.string.complete_uploading_file));

                    broadcastIntent.putExtra(ExtraKey.UPLOAD_SUCCESSFUL, true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                    return;
                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();

            NotificationUtils.uploadFailed(builder, getString(R.string.upload_failed));
            mNotificationManager.notify(notificationId, builder.build());

            return;
        }

        NotificationUtils.uploadFailed(builder, getString(R.string.upload_failed));
        mNotificationManager.notify(notificationId, builder.build());
    }

    private void uploadCaseFile(Intent intent) {
        int notificationId = Utils.generateNotificationId();

        String caseId = intent.getStringExtra(ExtraKey.CASE_ID);
        String filePath = intent.getStringExtra(ExtraKey.FILE_PATH);

        Intent broadcastIntent = new Intent(UploadAction.UPLOAD_COMPLETED);
        broadcastIntent.putExtra(ExtraKey.FROM_ACTION, UploadAction.CASE_FILE);

        NotificationCompat.Builder builder
                = NotificationUtils.generateUploadNotificationBuilder(this,
                DbUtils.getCaseById(this, caseId).name, getString(R.string.uploading_file));
        mNotificationManager.notify(notificationId, builder.build());

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());
        headers.put("cd", caseId);

        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                    LoadingDataUtils.WorkingDataUrl.EndPoints.CASE_UPLOAD_FILE, null);
            String responseString = RestfulUtils.restfulPostFileRequest(urlString, headers, filePath);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    NotificationUtils.uploadSuccessfully(builder, getString(R.string.complete_uploading_file));
                    mNotificationManager.notify(notificationId, builder.build());

                    Utils.showToastInNonUiThread(mHandler, this, getString(R.string.complete_uploading_file));

                    broadcastIntent.putExtra(ExtraKey.UPLOAD_SUCCESSFUL, true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                    return;
                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();

            NotificationUtils.uploadFailed(builder, getString(R.string.upload_failed));
            mNotificationManager.notify(notificationId, builder.build());

            return;
        }

        NotificationUtils.uploadFailed(builder, getString(R.string.upload_failed));
        mNotificationManager.notify(notificationId, builder.build());
    }
}
