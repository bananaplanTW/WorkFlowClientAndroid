package com.nicloud.workflowclientandroid.data.data.activity;

import android.net.Uri;

/**
 * Created by Ben on 2015/8/29.
 */
public class FileData extends BaseData {
    public String uploader;

    public String fileName;
    public Uri filePath;

    public FileData(BaseData.TYPE type) {
        super(type);
    }
}
