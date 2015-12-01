package com.nicloud.workflowclient.data.data.activity;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingActivityUserIconCommand;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingPhotoDataCommand;
import com.nicloud.workflowclient.data.connectserver.tasklog.OnLoadImageListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by daz on 10/9/15.
 */
public class ActivityDataFactory {

    public static BaseData genData(JSONObject recordJSON, Context context, OnLoadImageListener listener)
            throws JSONException {

        String type = recordJSON.getString("type");

        switch (type) {
            case "checkIn":
            case "checkOut":
            case "becomeWIP":
            case "becomePause":
            case "becomeResume":
            case "becomeOverwork":
            case "becomeStop":
            case "becomePending":
            case "becomeOff":
                // [TODO] should have record builder
                HistoryData attendance = (HistoryData) DataFactory.genData(recordJSON.getString("receiverId"), BaseData.TYPE.HISTORY);
                attendance.tag = type;
                attendance.category = BaseData.CATEGORY.WORKER;
                attendance.time = new Date(recordJSON.getLong("createdAt"));

                return attendance;

            case "dispatchTask":
            case "startTask":
            case "suspendTask":
            case "completeTask":
            case "unloadTask":
            case "passReviewTask":
            case "failReviewTask":
            case "createTaskException":
            case "completeTaskException":
                HistoryData task = (HistoryData) DataFactory.genData(recordJSON.getString("receiverId"), BaseData.TYPE.HISTORY);
                task.tag = type;
                task.category = BaseData.CATEGORY.WORKER;
                task.time = new Date(recordJSON.getLong("createdAt"));
                task.description = recordJSON.getString("taskName");

                return task;

            case "comment":
                RecordData comment = (RecordData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.RECORD);
                comment.tag = type;
                comment.reporter = recordJSON.getString("ownerId");
                comment.reporterName = recordJSON.getString("ownerName");
                comment.time = new Date(recordJSON.getLong("createdAt"));
                comment.description = recordJSON.getString("content");

                loadUserIcon(context, recordJSON, comment, listener);

                return comment;

            case "attachment":
                if (recordJSON.getString("contentType").equals("image")) {
                    PhotoData photoData = (PhotoData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.PHOTO);
                    photoData.uploader = recordJSON.getString("ownerId");
                    photoData.uploaderName = recordJSON.getString("ownerName");
                    photoData.tag = type;
                    photoData.time = new Date(recordJSON.getLong("createdAt"));
                    photoData.fileName = recordJSON.getString("name");

                    Uri.Builder thumbBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
                    thumbBuilder.path(recordJSON.getString("thumbUrl"));
                    Uri thumbUri = thumbBuilder.build();
                    // TODO: DO NOT load all thumbnails into memory to avoid OOM
                    LoadingPhotoDataCommand loadingPhotoDataCommand
                            = new LoadingPhotoDataCommand(context, thumbUri, photoData, listener);
                    loadingPhotoDataCommand.execute();

                    loadUserIcon(context, recordJSON, photoData, listener);

                    Uri.Builder imageBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
                    imageBuilder.path(recordJSON.getString("imageUrl"));
                    photoData.filePath = imageBuilder.build();

                    return photoData;

                } else if (recordJSON.getString("contentType").equals("file")) {
                    FileData fileData = (FileData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.FILE);
                    fileData.uploader = recordJSON.getString("ownerId");
                    fileData.uploaderName = recordJSON.getString("ownerName");
                    fileData.tag = type;
                    fileData.time = new Date(recordJSON.getLong("createdAt"));
                    fileData.fileName = recordJSON.getString("name");

                    loadUserIcon(context, recordJSON, fileData, listener);

                    Uri.Builder builder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
                    builder.path(recordJSON.getString("fileUrl"));
                    fileData.filePath = builder.build();

                    return fileData;
                }

                // Task specific activities
            case "dispatch":
                HistoryData dispatchTask = (HistoryData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.HISTORY);
                dispatchTask.tag = type;
                dispatchTask.category = BaseData.CATEGORY.TASK;
                dispatchTask.time = new Date(recordJSON.getLong("createdAt"));
                dispatchTask.description = recordJSON.getString("employeeName");

                return dispatchTask;

            case "start":
            case "pause":
            case "resume":
            case "suspend":
            case "complete":
            case "fail":
            case "pass":
                HistoryData taskStatus = (HistoryData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.HISTORY);
                taskStatus.tag = type;
                taskStatus.category = BaseData.CATEGORY.TASK;
                taskStatus.time = new Date(recordJSON.getLong("createdAt"));

                return taskStatus;

            case "create_exception":
            case "complete_exception":
                HistoryData taskException = (HistoryData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.HISTORY);
                taskException.tag = type;
                taskException.category = BaseData.CATEGORY.TASK;
                taskException.time = new Date(recordJSON.getLong("createdAt"));
                taskException.description = recordJSON.getString("exceptionName");

                return taskException;

            // task warining specific activities
            case "open":
            case "close":
                HistoryData taskWarningStatus = (HistoryData) DataFactory.genData(recordJSON.getString("ownerId"), BaseData.TYPE.HISTORY);
                taskWarningStatus.tag = type;
                taskWarningStatus.category = BaseData.CATEGORY.WARNING;
                taskWarningStatus.time = new Date(recordJSON.getLong("createdAt"));

                return taskWarningStatus;

            default:
                return null;
        }
    }

    private static void loadUserIcon(Context context, JSONObject jsonObject, BaseData baseData, OnLoadImageListener listener) {
        String iconThumbUrl = null;

        try {
            iconThumbUrl = LoadingDataUtils.getStringFromJson(jsonObject, "iconThumbUrl");

            if (!TextUtils.isEmpty(iconThumbUrl)) {
                Uri.Builder userIconBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
                userIconBuilder.path(iconThumbUrl);
                Uri userIconUri = userIconBuilder.build();

                // TODO: DO NOT load all thumbnails into memory to avoid OOM
                LoadingActivityUserIconCommand loadingActivityUserIconCommand
                        = new LoadingActivityUserIconCommand(context, userIconUri, baseData, listener);
                loadingActivityUserIconCommand.execute();
            } else {
                baseData.avatar = context.getDrawable(R.drawable.ic_worker);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
