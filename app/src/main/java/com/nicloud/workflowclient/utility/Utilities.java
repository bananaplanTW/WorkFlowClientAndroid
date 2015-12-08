package com.nicloud.workflowclient.utility;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.tasklog.DownloadFileFromURLCommand;
import com.nicloud.workflowclient.dialog.DisplayDialogFragment;
import com.nicloud.workflowclient.detailedtask.log.DetailedTaskActivity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by logicmelody on 2015/11/18.
 */
public class Utilities {

    public static final String DATE_FORMAT_YMD = "yyyy/MM/dd";
    public static final String DATE_FORMAT_MD = "MM/dd";
    public static final String DATE_FORMAT_YMD_HM_AMPM = "yyyy/MM/dd hh:mm aa";
    public static final String DATE_FORMAT_HM_AMPM = "hh:mm aa";


    public static String timestamp2Date(Date date, String format) {
        if (date == null) return "";

        String r;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);

        cal.setTimeInMillis(date.getTime());
        r = DateFormat.format(format, cal).toString();

        return r;
    }

    public static String milliSeconds2MinsSecs(long millis) {
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        return Integer.toString(hours) + " : " + Integer.toString(minutes);
    }

    public static String pad(int c) {
        StringBuilder s = new StringBuilder();
        int absC = Math.abs(c);

        if (c < 0) {
            s.append("-");
        }

        if (absC >= 10) {
            s.append(String.valueOf(absC));
        } else {
            s.append("0").append(String.valueOf(absC));
        }

        return s.toString();
    }

    /**
     * Convert time(hours and minutes) to milliseconds format.
     *
     * @param hours
     * @param minutes
     * @return Equal time in milliseconds
     */
    public static long timeToMilliseconds(int hours, int minutes) {
        return TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes);
    }

    /**
     * Convert time from milliseconds to hh:mm format.
     *
     * @param milliseconds
     * @return Equal time in xx hours xx minutes
     */
    public static int[] millisecondsToTime(long milliseconds) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long hoursInMinutes = TimeUnit.HOURS.toMinutes(hours);

        return new int[]{(int) hours, (int) (minutes - hoursInMinutes)};
    }

    /**
     * Convert time from milliseconds to hh:mm format.
     *
     * @param milliseconds
     * @return Equal time in hh:mm
     */
    public static String millisecondsToTimeString(long milliseconds) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long hoursInMinutes = TimeUnit.HOURS.toMinutes(hours);

        return pad((int) hours) + ":" + pad((int) (minutes - hoursInMinutes));
    }

    /**
     * Convert time from milliseconds to mm/dd/yyyy format.
     *
     * @param context
     * @param milliseconds
     * @return Equal time in mm/dd/yyyy
     */
    public static String millisecondsToMMDDYYYY(Context context, long milliseconds) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds);

        return String.format(context.getString(R.string.date_format_mm_dd_yyyy),
                pad(c.get(Calendar.MONTH) + 1),
                pad(c.get(Calendar.DAY_OF_MONTH)),
                pad(c.get(Calendar.YEAR)));
    }

    /**
     * Convert time from milliseconds to mm/dd format.
     *
     * Ex: 11月12日
     *
     * @param context
     * @param milliseconds
     * @return Equal time in mm/dd/yyyy
     */
    public static String millisecondsToMMDD(Context context, long milliseconds) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds);

        return String.format(context.getString(R.string.date_format_mm_dd),
                pad(c.get(Calendar.MONTH) + 1),
                pad(c.get(Calendar.DAY_OF_MONTH)));
    }

    /**
     * Convert time from milliseconds to mm/dd/yyyy format.
     *
     * @param milliseconds
     * @return Equal time in mm/dd/yyyy
     */
    public static String millisecondsToHHMMA(long milliseconds) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds);

        SimpleDateFormat df = new SimpleDateFormat("hh:mm a", Locale.US);
        String result = df.format(c.getTime());

        return result;
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            } else if (isDownloadedDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                return getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadedDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isSameId(String id1, String id2) {
        return id1.equals(id2);
    }

    public static void replaceProgressBarWhenLoadingFinished(Context context, View mainView, ProgressBar progressBar) {
        Animation fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);

        mainView.setAnimation(fadeIn);
        progressBar.setAnimation(fadeOut);

        mainView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private static final String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};

    public static boolean isImage(String filePath) {
        File file = new File(filePath);

        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ?
                        (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        return null;
    }

    /**
     * Use this method to show toast in non-UI thread
     *
     * @param context
     * @param text
     */
    public static void showToastInNonUiThread(final Context context, final String text) {
        new Handler().post(new Runnable() {
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    /**
//     * Set the style of TextView according to the status of a task
//     *
//     * @param context
//     * @param status
//     * @param task
//     */
//    public static void setTaskStatusForTextView(Context context, TextView status, Task task) {
//        status.setText(Task.getTaskStatusString(context, task));
//
//        switch (task.status) {
//            case IN_REVIEW:
//                status.setTextColor(context.getResources().getColor(R.color.task_card_status_text_color));
//                status.setBackgroundResource(R.drawable.task_card_status_in_review_background);
//                break;
//
//            case WIP:
//                status.setTextColor(context.getResources().getColor(R.color.task_card_status_text_color));
//                status.setBackgroundResource(R.drawable.task_card_status_wip_background);
//                break;
//
//            case PENDING:
//                status.setTextColor(context.getResources().getColor(R.color.task_card_status_text_color));
//                status.setBackgroundResource(R.drawable.task_card_status_pending_background);
//                break;
//
//            case UNCLAIMED:
//                status.setTextColor(context.getResources().getColor(R.color.task_card_status_text_color));
//                status.setBackgroundResource(R.drawable.task_card_status_unclaimed_background);
//                break;
//
//            case WARNING:
//                status.setTextColor(context.getResources().getColor(R.color.task_card_status_text_color));
//                status.setBackgroundResource(R.drawable.task_card_status_warning_background);
//                break;
//
//            case DONE:
//                status.setTextColor(context.getResources().getColor(R.color.task_card_status_done_text_color));
//                status.setBackground(null);
//                break;
//        }
//    }

    public static void showDialog(FragmentManager fm, int type, String taskId) {
        DisplayDialogFragment fragment =
                (DisplayDialogFragment) fm.findFragmentByTag(DisplayDialogFragment.TAG_DISPLAY_DIALOG_FRAGMENT);
        if (fragment == null) {
            fragment = new DisplayDialogFragment();
        }

        if (fragment.isAdded()) return;

        Bundle bundle = new Bundle();
        switch (type) {
            case DisplayDialogFragment.DialogType.COMPLETE_TASK:
                bundle.putString(DisplayDialogFragment.EXTRA_TASK_ID, taskId);
                bundle.putInt(DisplayDialogFragment.EXTRA_DIALOG_TYPE, DisplayDialogFragment.DialogType.COMPLETE_TASK);
                break;

            case DisplayDialogFragment.DialogType.CHOOSE_TASK:
                bundle.putString(DisplayDialogFragment.EXTRA_TASK_ID, taskId);
                bundle.putInt(DisplayDialogFragment.EXTRA_DIALOG_TYPE, DisplayDialogFragment.DialogType.CHOOSE_TASK);
                break;

            case DisplayDialogFragment.DialogType.CHECK_IN_OUT:
                bundle.putInt(DisplayDialogFragment.EXTRA_DIALOG_TYPE, DisplayDialogFragment.DialogType.CHECK_IN_OUT);
                break;
        }

        fragment.setArguments(bundle);
        fragment.show(fm, DisplayDialogFragment.TAG_DISPLAY_DIALOG_FRAGMENT);
    }

    public static void dismissDialog(FragmentManager fm) {
        DisplayDialogFragment fragment =
                (DisplayDialogFragment) fm.findFragmentByTag(DisplayDialogFragment.TAG_DISPLAY_DIALOG_FRAGMENT);

        if (fragment == null) return;

        fragment.dismiss();
    }

    public static void goToTaskLogActivity(Context context, String taskId) {
        Intent intent = new Intent(context, DetailedTaskActivity.class);
        intent.putExtra(DetailedTaskActivity.EXTRA_TASK_ID, taskId);

        context.startActivity(intent);
    }

    public static Bitmap scaleBitmap(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) return null;

        int targetW = (int) context.getResources().getDimension(R.dimen.photo_thumbnail_max_width);
        int targetH = (int) context.getResources().getDimension(R.dimen.photo_thumbnail_max_height);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor < 1 ? 1 : scaleFactor;

        return BitmapFactory.decodeFile(filePath, bmOptions);
    }

    public static void downloadFile(Context context, String urlString, String fileName) {
        DownloadFileFromURLCommand downloadFileFromURLCommand = new DownloadFileFromURLCommand(context, urlString, fileName);
        downloadFileFromURLCommand.execute();
    }
}
