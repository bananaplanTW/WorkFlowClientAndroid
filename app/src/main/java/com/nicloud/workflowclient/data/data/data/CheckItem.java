package com.nicloud.workflowclient.data.data.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by logicmelody on 2015/12/9.
 */
public class CheckItem implements Parcelable {

    public String name;
    public boolean isChecked = false;


    public CheckItem(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    protected CheckItem(Parcel in) {
        name = in.readString();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<CheckItem> CREATOR = new Creator<CheckItem>() {
        @Override
        public CheckItem createFromParcel(Parcel in) {
            return new CheckItem(in);
        }

        @Override
        public CheckItem[] newArray(int size) {
            return new CheckItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
}
