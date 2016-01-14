package com.nicloud.workflowclient.serveraction.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.data.utility.RestfulUtils;
import com.nicloud.workflowclient.data.utility.URLUtils;
import com.nicloud.workflowclient.utility.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by logicmelody on 2015/12/11.
 */
public class UploadService extends IntentService {

    private static final String TAG = "UploadService";

    public static class UploadAction {
        public static final String CHECK_ITEM = "upload_action_check_item";
        public static final String TEXT = "upload_action_text";
        public static final String PHOTO = "upload_action_photo";
        public static final String FILE = "upload_action_file";
        public static final String UPLOAD_COMPLETED = "upload_action_completed";
    }

    public static class ExtraKey {
        public static final String TASK_ID = "extra_task_id";
        public static final String CHECK_ITEM_NAME = "extra_check_item_name";
        public static final String TEXT = "extra_text";
        public static final String PHOTO_PATH = "extra_photo_path";
        public static final String FILE_PATH = "extra_file_path";
        public static final String UPLOAD_SUCCESSFUL = "extra_upload_successful";
        public static final String FROM_ACTION = "extra_from_action";
    }

    private NotificationManager mNotificationManager;

    private Handler mHandler;


    public static Intent generateUploadCheckItemIntent(Context context, String taskId, String checkItemName) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(UploadAction.CHECK_ITEM);
        intent.putExtra(ExtraKey.TASK_ID, taskId);
        intent.putExtra(ExtraKey.CHECK_ITEM_NAME, checkItemName);

        return intent;
    }

    public static Intent generateUploadTextIntent(Context context, String taskId, String text) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(UploadAction.TEXT);
        intent.putExtra(ExtraKey.TASK_ID, taskId);
        intent.putExtra(ExtraKey.TEXT, text);

        return intent;
    }

    public static Intent generateUploadPhotoIntent(Context context, String taskId, String photoPath) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(UploadAction.PHOTO);
        intent.putExtra(ExtraKey.TASK_ID, taskId);
        intent.putExtra(ExtraKey.PHOTO_PATH, photoPath);

        return intent;
    }

    public static Intent generateUploadFileIntent(Context context, String taskId, String filePath) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(UploadAction.FILE);
        intent.putExtra(ExtraKey.TASK_ID, taskId);
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

        if (UploadAction.TEXT.equals(action)) {
            uploadText(intent);

        } else if (UploadAction.PHOTO.equals(action)) {
            uploadPhoto(intent);

        } else if (UploadAction.FILE.equals(action)) {
            uploadFile(intent);

        } else if (UploadAction.CHECK_ITEM.equals(action)) {
            uploadCheckItem(intent);
        }
    }

    private void uploadText(Intent intent) {
        int notificationId = Utilities.generateNotificationId();

        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String text = intent.getStringExtra(ExtraKey.TEXT);

        Intent broadcastIntent = new Intent(UploadAction.UPLOAD_COMPLETED);
        broadcastIntent.putExtra(ExtraKey.FROM_ACTION, UploadAction.TEXT);

        NotificationCompat.Builder builder
                = generateUploadNotificationBuilder(WorkingData.getInstance(this).getTask(taskId).name,
                                                    getString(R.string.add_log_uploading_text));
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
                    uploadSuccessfully(builder, getString(R.string.add_log_complete_text));
                    mNotificationManager.notify(notificationId, builder.build());

                    Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.add_log_complete_text));

                    broadcastIntent.putExtra(ExtraKey.UPLOAD_SUCCESSFUL, true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                    return;
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in uploadText() in UploadService");
            e.printStackTrace();

            uploadFailed(builder, getString(R.string.add_log_failed));
            mNotificationManager.notify(notificationId, builder.build());

            return;
        }

        uploadFailed(builder, getString(R.string.add_log_failed));
        mNotificationManager.notify(notificationId, builder.build());
    }

    private void uploadPhoto(Intent intent) {
        int notificationId = Utilities.generateNotificationId();

        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String photoPath = intent.getStringExtra(ExtraKey.PHOTO_PATH);

        Intent broadcastIntent = new Intent(UploadAction.UPLOAD_COMPLETED);
        broadcastIntent.putExtra(ExtraKey.FROM_ACTION, UploadAction.FILE);

        NotificationCompat.Builder builder
                = generateUploadNotificationBuilder(WorkingData.getInstance(this).getTask(taskId).name,
                getString(R.string.add_log_uploading_photo));
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
                    uploadSuccessfully(builder, getString(R.string.add_log_complete_photo));
                    mNotificationManager.notify(notificationId, builder.build());

                    Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.add_log_complete_photo));

                    broadcastIntent.putExtra(ExtraKey.UPLOAD_SUCCESSFUL, true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                    return;
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in uploadText() in UploadService");
            e.printStackTrace();

            uploadFailed(builder, getString(R.string.add_log_failed));
            mNotificationManager.notify(notificationId, builder.build());

            return;
        }

        uploadFailed(builder, getString(R.string.add_log_failed));
        mNotificationManager.notify(notificationId, builder.build());
    }

    private void uploadFile(Intent intent) {
        int notificationId = Utilities.generateNotificationId();

        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String filePath = intent.getStringExtra(ExtraKey.FILE_PATH);

        Intent broadcastIntent = new Intent(UploadAction.UPLOAD_COMPLETED);
        broadcastIntent.putExtra(ExtraKey.FROM_ACTION, UploadAction.FILE);

        NotificationCompat.Builder builder
                = generateUploadNotificationBuilder(WorkingData.getInstance(this).getTask(taskId).name,
                getString(R.string.add_log_uploading_file));
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
                    uploadSuccessfully(builder, getString(R.string.add_log_complete_file));
                    mNotificationManager.notify(notificationId, builder.build());

                    Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.add_log_complete_file));

                    broadcastIntent.putExtra(ExtraKey.UPLOAD_SUCCESSFUL, true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

                    return;
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in uploadText() in UploadService");
            e.printStackTrace();

            uploadFailed(builder, getString(R.string.add_log_failed));
            mNotificationManager.notify(notificationId, builder.build());

            return;
        }

        uploadFailed(builder, getString(R.string.add_log_failed));
        mNotificationManager.notify(notificationId, builder.build());
    }

    private void uploadCheckItem(Intent intent) {
        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String checkItemName = intent.getStringExtra(ExtraKey.CHECK_ITEM_NAME);

        Intent broadcastIntent = new Intent(UploadAction.UPLOAD_COMPLETED);
        broadcastIntent.putExtra(ExtraKey.FROM_ACTION, UploadAction.CHECK_ITEM);

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
            Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            Log.e(TAG, "Exception in uploadText() in UploadService");
            e.printStackTrace();

            return;
        }

        Utilities.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
    }

    private NotificationCompat.Builder generateUploadNotificationBuilder(String taskName, String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_nicloud_notification)
                .setContentTitle(taskName)
                .setContentText(contentText)
                .setProgress(0, 0, true);

        return builder;
    }

    private void uploadSuccessfully(NotificationCompat.Builder builder, String successfulText) {
        builder.setSmallIcon(R.drawable.ic_notification_done)
                .setContentText(successfulText)
                .setProgress(0, 0, false);
    }

    private void uploadFailed(NotificationCompat.Builder builder, String failedText) {
        builder.setSmallIcon(R.drawable.ic_notification_failed)
                .setContentText(failedText)
                .setProgress(0, 0, false);
    }
}
