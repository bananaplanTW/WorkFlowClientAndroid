package com.nicloud.workflowclient.data.data.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ben on 2015/8/29.
 */
public class PhotoData extends BaseData implements Parcelable {

    public String uploader;
    public String uploaderName;

    public Bitmap photo;
    public Uri photoUri;

    public String fileName;
    public Uri filePath = Uri.EMPTY;


    public PhotoData(BaseData.TYPE type) {
        super(type);
    }

    protected PhotoData(Parcel in) {
        super(in);
        uploader = in.readString();
        uploaderName = in.readString();
        photo = in.readParcelable(Bitmap.class.getClassLoader());
        fileName = in.readString();
        filePath = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(uploader);
        dest.writeString(uploaderName);
        dest.writeParcelable(photo, flags);
        dest.writeString(fileName);
        dest.writeParcelable(filePath, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoData> CREATOR = new Creator<PhotoData>() {
        @Override
        public PhotoData createFromParcel(Parcel in) {
            return new PhotoData(in);
        }

        @Override
        public PhotoData[] newArray(int size) {
            return new PhotoData[size];
        }
    };
}
