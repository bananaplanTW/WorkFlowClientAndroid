package com.nicloud.workflowclient.data.activity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ben on 2015/8/29.
 */
public class RecordData extends BaseData implements Parcelable {

    public String reporter;
    public String reporterName;

    public String description = "";


    public RecordData(BaseData.TYPE type) {
        super(type);
    }

    protected RecordData(Parcel in) {
        super(in);
        reporter = in.readString();
        reporterName = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(reporter);
        dest.writeString(reporterName);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecordData> CREATOR = new Creator<RecordData>() {
        @Override
        public RecordData createFromParcel(Parcel in) {
            return new RecordData(in);
        }

        @Override
        public RecordData[] newArray(int size) {
            return new RecordData[size];
        }
    };
}
