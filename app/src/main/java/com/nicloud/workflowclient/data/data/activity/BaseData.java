package com.nicloud.workflowclient.data.data.activity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Ben on 2015/8/29.
 */
public class BaseData implements Parcelable {

    public enum TYPE {
        RECORD, FILE, PHOTO, HISTORY
    }

    public enum CATEGORY {
        WORKER, TASK, WARNING
    }

    public long id;
    public String workerId;

    public Bitmap avatar;
    public long time;
    public TYPE type;
    public CATEGORY category;
    public String tag;


    public BaseData() {}

    public BaseData(TYPE type) {
        this.type = type;
    }

    protected BaseData(Parcel in) {
        id = in.readLong();
        workerId = in.readString();
        avatar = in.readParcelable(Bitmap.class.getClassLoader());
        time = in.readLong();

        try {
            type = TYPE.valueOf(in.readString());
        } catch (IllegalArgumentException x) {
            type = null;
        }

        try {
            category = CATEGORY.valueOf(in.readString());
        } catch (IllegalArgumentException x) {
            category = null;
        }

        tag = in.readString();
    }

    public static final Creator<BaseData> CREATOR = new Creator<BaseData>() {
        @Override
        public BaseData createFromParcel(Parcel in) {
            return new BaseData(in);
        }

        @Override
        public BaseData[] newArray(int size) {
            return new BaseData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(workerId);
        dest.writeParcelable(avatar, flags);
        dest.writeLong(time);
        dest.writeString((type == null) ? "" : type.name());
        dest.writeString((category == null) ? "" : category.name());
        dest.writeString(tag);
    }
}
