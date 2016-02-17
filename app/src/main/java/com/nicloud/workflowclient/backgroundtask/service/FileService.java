package com.nicloud.workflowclient.backgroundtask.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.receiver.FileCompletedReceiver;
import com.nicloud.workflowclient.data.data.File;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.utils.DbUtils;
import com.nicloud.workflowclient.utility.utils.JsonUtils;
import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.utility.utils.RestfulUtils;
import com.nicloud.workflowclient.utility.utils.URLUtils;
import com.nicloud.workflowclient.utility.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by logicmelody on 2016/1/14.
 */
public class FileService extends IntentService {

    private static final String TAG = "FileService";

    public static final class Action {
        public static final String LOAD_CASE_FILES = "file_service_load_case_files";
    }

    public static final class ExtraKey {
        public static final String STRING_CASE_ID = "extra_case_id";
    }

    private static final String[] mProjection = new String[] {
            WorkFlowContract.File.FILE_ID,
            WorkFlowContract.File.UPDATED_TIME
    };
    private static final int FILE_ID = 0;
    private static final int UPDATED_TIME = 1;

    private Handler mHandler;


    private class FileInDb {

        public String fileId;
        public long lastUpdatedTime = 0L;


        public FileInDb(String fileId, long lastUpdatedTime) {
            this.fileId = fileId;
            this.lastUpdatedTime = lastUpdatedTime;
        }
    }

    public FileService() {
        super(TAG);
        mHandler = new Handler();
    }


    public static Intent generateLoadCaseFilesIntent(Context context, String caseId) {
        Intent intent = new Intent(context, FileService.class);
        intent.setAction(Action.LOAD_CASE_FILES);
        intent.putExtra(ExtraKey.STRING_CASE_ID, caseId);

        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (Action.LOAD_CASE_FILES.equals(action)) {
            loadCaseFiles(intent);
        }
    }

    private void loadCaseFiles(Intent intent) {
        String caseId = intent.getStringExtra(ExtraKey.STRING_CASE_ID);

        Intent broadcastIntent = new Intent(FileCompletedReceiver.ACTION_LOAD_FILES_COMPLETED);

        if (RestfulUtils.isConnectToInternet(this)) {
            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-user-id", WorkingData.getUserId());
                headers.put("x-auth-token", WorkingData.getAuthToken());

                String fileJsonString =
                        RestfulUtils.restfulGetRequest(getCaseFilesUrl(caseId), headers);
                if (TextUtils.isEmpty(fileJsonString)) {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                    Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
                    return;
                }

                JSONObject fileJson = new JSONObject(fileJsonString);
                JSONArray fileJsonList = JsonUtils.getJsonArrayFromJson(fileJson, "result");

                if (fileJsonList != null) {
                    ArrayList<FileInDb> filesFromDb = new ArrayList<>();
                    ArrayList<File> filesFromServer = new ArrayList<>();
                    Map<String, FileInDb> filesFromDbMap = new HashMap<>();
                    Map<String, File> filesFromServerMap = new HashMap<>();

                    getCaseFilesFromDb(caseId, filesFromDb, filesFromDbMap);
                    getFilesFromServer(fileJsonList, filesFromServer, filesFromServerMap);

                    insertAndUpdateFilesToDb(filesFromDbMap, filesFromServer);
                    deleteFilesFromDb(filesFromServerMap, filesFromDb);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
            }

        } else {
            Utils.showToastInNonUiThread(mHandler, this, getString(R.string.no_internet_connection_information));
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private String getCaseFilesUrl(String caseId) {
        HashMap<String, String> queries = new HashMap<>();
        queries.put("caseId", caseId);

        return URLUtils.buildURLString(LoadingDataUtils.sBaseUrl,
                LoadingDataUtils.WorkingDataUrl.EndPoints.CASE_FILES, queries);
    }

    private void getCaseFilesFromDb(String caseId, ArrayList<FileInDb> fileList, Map<String, FileInDb> fileMap) {
        String selection =  WorkFlowContract.File.CASE_ID + " = ?";
        String[] selectionArgs = new String[] {caseId};
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(WorkFlowContract.File.CONTENT_URI,
                                                mProjection, selection, selectionArgs, null);

            if (cursor != null && cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    FileInDb fileInDb = new FileInDb(cursor.getString(FILE_ID), cursor.getLong(UPDATED_TIME));
                    fileList.add(fileInDb);
                    fileMap.put(fileInDb.fileId, fileInDb);
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void getFilesFromServer(JSONArray jsonList,
                                    ArrayList<File> fileList, Map<String, File> fileMap) {
        for (int i = 0 ; i < jsonList.length() ; i++) {
            try {
                JSONObject jsonObject = jsonList.getJSONObject(i);
                File file = File.retrieveCaseFileFromJson(jsonObject);

                fileList.add(file);
                fileMap.put(file.fileId, file);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertAndUpdateFilesToDb(Map<String, FileInDb> filesFromDbMap, List<File> filesFromServer) {
        for (File file : filesFromServer) {
            if (!filesFromDbMap.containsKey(file.fileId)) {
                DbUtils.insertFileToDb(this, file);

            } else {
                //if (file.updatedTime <= filesFromDbMap.get(file.fileId).lastUpdatedTime) continue;

                DbUtils.updateFileToDb(this, file);
            }
        }
    }

    private void deleteFilesFromDb(Map<String, File> filesFromServerMap, ArrayList<FileInDb> filesFromDb) {
        for (FileInDb fileInDb : filesFromDb) {
            if (!filesFromServerMap.containsKey(fileInDb.fileId)) {
                DbUtils.deleteFileFromDb(this, fileInDb.fileId);
            }
        }
    }
}
