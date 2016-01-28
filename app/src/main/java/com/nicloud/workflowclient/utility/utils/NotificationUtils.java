package com.nicloud.workflowclient.utility.utils;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.nicloud.workflowclient.R;

/**
 * Created by logicmelody on 2016/1/28.
 */
public class NotificationUtils {

    public static NotificationCompat.Builder generateUploadNotificationBuilder(Context context,
                                                                               String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_nicloud_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setProgress(0, 0, true);

        return builder;
    }

    public static void uploadSuccessfully(NotificationCompat.Builder builder, String successfulText) {
        builder.setSmallIcon(R.drawable.ic_notification_done)
                .setContentText(successfulText)
                .setProgress(0, 0, false);
    }

    public static void uploadFailed(NotificationCompat.Builder builder, String failedText) {
        builder.setSmallIcon(R.drawable.ic_notification_failed)
                .setContentText(failedText)
                .setProgress(0, 0, false);
    }
}
