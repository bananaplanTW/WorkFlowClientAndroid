package com.nicloud.workflowclient.serveraction;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
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
        public static final String TEXT = "upload_action_text";
        public static final String PHOTO = "upload_action_photo";
        public static final String FILE = "upload_action_file";
    }

    public static class ExtraKey {
        public static final String TASK_ID = "extra_task_id";
        public static final String TEXT = "extra_text";
    }

    private NotificationManager mNotificationManager;

    private Handler mHandler;


    public static Intent generateUploadTextIntent(Context context, String taskId, String text) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setAction(UploadAction.TEXT);
        intent.putExtra(ExtraKey.TASK_ID, taskId);
        intent.putExtra(ExtraKey.TEXT, text);

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
            uploadPhoto();

        } else if (UploadAction.FILE.equals(action)) {
            uploadFile();
        }
    }

    private void uploadText(Intent intent) {
        int notificationId = Utilities.generateNotificationId();

        String taskId = intent.getStringExtra(ExtraKey.TASK_ID);
        String text = intent.getStringExtra(ExtraKey.TEXT);

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

    private void uploadPhoto() {
    }

    private void uploadFile() {
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
