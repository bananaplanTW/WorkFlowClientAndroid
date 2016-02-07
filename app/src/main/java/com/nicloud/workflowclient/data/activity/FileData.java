package com.nicloud.workflowclient.data.activity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ben on 2015/8/29.
 */
public class FileData extends BaseData implements Parcelable {

    public String uploader;
    public String uploaderName;

    public String fileName;
    public Uri filePath;


    public FileData(BaseData.TYPE type) {
        super(type);
    }

    protected FileData(Parcel in) {
        super(in);
        uploader = in.readString();
        uploaderName = in.readString();
        fileName = in.readString();
        filePath = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(uploader);
        dest.writeString(uploaderName);
        dest.writeString(fileName);
        dest.writeParcelable(filePath, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileData> CREATOR = new Creator<FileData>() {
        @Override
        public FileData createFromParcel(Parcel in) {
            return new FileData(in);
        }

        @Override
        public FileData[] newArray(int size) {
            return new FileData[size];
        }
    };
}
