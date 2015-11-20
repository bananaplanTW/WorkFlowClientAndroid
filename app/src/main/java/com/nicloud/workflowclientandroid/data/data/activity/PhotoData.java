package com.nicloud.workflowclientandroid.data.data.activity;

import android.graphics.drawable.Drawable;
import android.net.Uri;

/**
 * Created by Ben on 2015/8/29.
 */
public class PhotoData extends BaseData {
    public String uploader;

    public Drawable photo;
    public String fileName;
    public Uri filePath = Uri.EMPTY;

    public PhotoData(BaseData.TYPE type) {
        super(type);
    }
}
