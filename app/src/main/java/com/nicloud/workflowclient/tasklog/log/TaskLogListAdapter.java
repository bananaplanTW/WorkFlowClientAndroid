package com.nicloud.workflowclient.tasklog.log;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.data.data.activity.FileData;
import com.nicloud.workflowclient.data.data.activity.PhotoData;
import com.nicloud.workflowclient.data.data.activity.RecordData;
import com.nicloud.workflowclient.utility.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/11/17.
 */
public class TaskLogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final class ItemViewType {
        public static final int RECORD = 0;
        public static final int PHOTO = 1;
        public static final int FILE = 2;
    }

    private Context mContext;
    private List<BaseData> mDataSet = new ArrayList<>();

    private class BaseLogViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ImageView icon;
        public TextView userName;
        public TextView description;
        public TextView timestamp;


        public BaseLogViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            icon = (ImageView) itemView.findViewById(R.id.log_icon);
            userName = (TextView) itemView.findViewById(R.id.log_user_name);
            description = (TextView) itemView.findViewById(R.id.log_description);
            timestamp = (TextView) itemView.findViewById(R.id.log_timestamp);
        }
    }

    private class TextLogViewHolder extends BaseLogViewHolder {

        public TextLogViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class PhotoLogViewHolder extends BaseLogViewHolder {

        public ImageView photo;

        public PhotoLogViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            photo = (ImageView) itemView.findViewById(R.id.log_photo);
        }
    }

    private class FileLogViewHolder extends BaseLogViewHolder {

        public FileLogViewHolder(View itemView) {
            super(itemView);
        }
    }


    public TaskLogListAdapter(Context context) {
        mContext = context;
    }

    public void swapDataSet(List<BaseData> dataSet) {
        mDataSet.clear();
        mDataSet.addAll(dataSet);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemViewType.RECORD:
                return new TextLogViewHolder(LayoutInflater.from(mContext).inflate(R.layout.task_log_text_item, parent, false));

            case ItemViewType.PHOTO:
                return new PhotoLogViewHolder(LayoutInflater.from(mContext).inflate(R.layout.task_log_photo_item, parent, false));

            case ItemViewType.FILE:
                return new FileLogViewHolder(LayoutInflater.from(mContext).inflate(R.layout.task_log_file_item, parent, false));

            default:
                return new TextLogViewHolder(LayoutInflater.from(mContext).inflate(R.layout.task_log_text_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (mDataSet.get(position).type) {
            case RECORD:
                onBindTextLog((TextLogViewHolder) holder, position);
                break;

            case PHOTO:
                onBindPhotoLog((PhotoLogViewHolder) holder, position);
                break;

            case FILE:
                onBindFileLog((FileLogViewHolder) holder, position);
                break;
        }
    }

    private void onBindTextLog(TextLogViewHolder holder, int position) {
        RecordData recordData = (RecordData) mDataSet.get(position);

        holder.icon.setImageDrawable(recordData.avatar);
        holder.userName.setText(recordData.reporterName);
        holder.description.setText(recordData.description);
        holder.timestamp.setText(Utilities.timestamp2Date(recordData.time, Utilities.DATE_FORMAT_YMD_HM_AMPM));
    }

    private void onBindPhotoLog(PhotoLogViewHolder holder, int position) {
        final PhotoData photoData = (PhotoData) mDataSet.get(position);

        holder.icon.setImageDrawable(photoData.avatar);
        holder.userName.setText(photoData.uploaderName);
        holder.description.setText(String.format(mContext.getString(R.string.task_log_upload_photo), photoData.fileName));
        holder.timestamp.setText(Utilities.timestamp2Date(photoData.time, Utilities.DATE_FORMAT_YMD_HM_AMPM));
        holder.photo.setImageDrawable(photoData.photo);

        if (Uri.EMPTY != photoData.filePath) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(DisplayImageActivity
                            .launchDisplayImageActivity(mContext, photoData.fileName, photoData.filePath.toString()));
                }
            });
        }
    }

    private void onBindFileLog(FileLogViewHolder holder, int position) {
        final FileData fileData = (FileData) mDataSet.get(position);

        holder.icon.setImageDrawable(fileData.avatar);
        holder.userName.setText(fileData.uploaderName);
        holder.description.setText(String.format(mContext.getString(R.string.task_log_upload_file), fileData.fileName));
        holder.timestamp.setText(Utilities.timestamp2Date(fileData.time, Utilities.DATE_FORMAT_YMD_HM_AMPM));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.downloadFile(mContext, fileData.filePath.toString(), fileData.fileName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (mDataSet.get(position).type) {
            case RECORD:
                return ItemViewType.RECORD;

            case PHOTO:
                return ItemViewType.PHOTO;

            case FILE:
                return ItemViewType.FILE;
        }

        return super.getItemViewType(position);
    }
}
