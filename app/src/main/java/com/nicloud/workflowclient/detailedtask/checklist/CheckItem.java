package com.nicloud.workflowclient.detailedtask.checklist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by logicmelody on 2015/12/9.
 */
public class CheckItem implements Parcelable {

    public String name;
    public String taskId;
    public boolean isChecked = false;
    public int position;


    public CheckItem(String name, String taskId, boolean isChecked, int position) {
        this.name = name;
        this.taskId = taskId;
        this.isChecked = isChecked;
        this.position = position;
    }

    protected CheckItem(Parcel in) {
        name = in.readString();
        taskId = in.readString();
        isChecked = in.readByte() != 0;
        position = in.readInt();
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
        dest.writeString(taskId);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeInt(position);
    }
}
