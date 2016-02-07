package com.nicloud.workflowclient.backgroundtask.asyntask.tasklog;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.asyntask.restful.GetRequestAsyncTask;

import java.io.File;
import java.net.URLConnection;


/**
 * Created by daz on 10/30/15.
 */
public class DownloadFileFromURLCommand implements IDownloadCommand,
        DownloadFileFromURLStrategy.DownloadProgressListener,
        GetRequestAsyncTask.OnFinishGettingDataListener {

    private GetRequestAsyncTask mGetRequestAsyncTask;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;

    private Context mContext;
    private String mUrlString;
    private String mFilePath;
    private String mFileName;

    private int mNotificationId = 0;


    public DownloadFileFromURLCommand(Context context, String urlString, String fileName) {
        mContext = context;
        mUrlString = urlString;
        mFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName;
        mFileName = fileName;
    }

    @Override
    public void execute() {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(mContext);
        mNotificationId = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext,
                0,
                getOpenFileIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        String downloadFileNotificationTitle = String.format(mContext.getString(R.string.download_file), mFileName);
        mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(downloadFileNotificationTitle)
                .setContentText(mContext.getString(R.string.downloading))
                .setAutoCancel(true)
                .setTicker(downloadFileNotificationTitle)
                .setContentIntent(pendingIntent);

        DownloadFileFromURLStrategy downloadFileFromURLStrategy = new DownloadFileFromURLStrategy(mUrlString, mFilePath, this);
        mGetRequestAsyncTask = new GetRequestAsyncTask(mContext, downloadFileFromURLStrategy, this);
        mGetRequestAsyncTask.execute();

        Toast.makeText(mContext, downloadFileNotificationTitle, Toast.LENGTH_SHORT).show();
    }

    private Intent getOpenFileIntent() {
        Uri fileUri = Uri.fromFile(new File(mFilePath));
        String mimeType = URLConnection.guessContentTypeFromName(fileUri.toString());

        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.setDataAndType(fileUri, mimeType);

        return resultIntent;
    }

    @Override
    public void onFinishGettingData() {

    }
    @Override
    public void onFailGettingData(boolean isFailCausedByInternet) {

    }

    @Override
    public void updateProgress(int progress) {
        mNotificationBuilder.setContentText(mContext.getString(R.string.downloading) + ": " + progress + "%");
        mNotificationBuilder.setProgress(100, progress, false);

        mNotificationManager.notify(mNotificationId, mNotificationBuilder.build());
    }
    @Override
    public void downloadCompleted() {
        mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentText(mContext.getString(R.string.download_completed)).setProgress(0, 0, false)
                .setTicker(mContext.getString(R.string.download_completed));

        mNotificationManager.notify(mNotificationId, mNotificationBuilder.build());
    }
}
