package com.nicloud.workflowclient.detailedtask.filelog;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingPhotoDataCommand;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.data.data.activity.FileData;
import com.nicloud.workflowclient.data.data.activity.PhotoData;
import com.nicloud.workflowclient.utility.Utilities;

import java.util.Date;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/12.
 */
public class FileLogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<BaseData> mFileLogData;


    private class FileLogItemViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ImageView fileImage;
        public TextView fileName;
        public TextView fileInformation;


        public FileLogItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            fileImage = (ImageView) itemView.findViewById(R.id.file_image);
            fileName = (TextView) itemView.findViewById(R.id.file_name);
            fileInformation = (TextView) itemView.findViewById(R.id.log_information);
        }
    }

    public FileLogListAdapter(Context context, List<BaseData> data) {
        mContext = context;
        mFileLogData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileLogItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.file_log_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (mFileLogData.get(position).type) {
            case PHOTO:
                onBindPhotoLog((FileLogItemViewHolder) holder, position);
                break;

            case FILE:
                onBindFileLog((FileLogItemViewHolder) holder, position);
                break;
        }
    }

    private void onBindPhotoLog(FileLogItemViewHolder holder, int position) {
        final PhotoData photoData = (PhotoData) mFileLogData.get(position);
        String fileInformation = photoData.uploaderName + " " +
                Utilities.timestamp2Date(new Date(photoData.time), Utilities.DATE_FORMAT_YMD_HM_AMPM);

        holder.fileName.setText(photoData.fileName);
        holder.fileInformation.setText(fileInformation);

        if (photoData.photo != null) {
            holder.fileImage.setImageBitmap(photoData.photo);

        } else {
            holder.fileImage.setImageResource(R.drawable.ic_file);

            LoadingPhotoDataCommand loadingPhotoDataCommand
                    = new LoadingPhotoDataCommand(mContext, photoData.photoUri, photoData, holder.fileImage);
            loadingPhotoDataCommand.execute();
        }

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

    private void onBindFileLog(FileLogItemViewHolder holder, int position) {
        final FileData fileData = (FileData) mFileLogData.get(position);
        String fileInformation = fileData.uploaderName + " " +
                Utilities.timestamp2Date(new Date(fileData.time), Utilities.DATE_FORMAT_YMD_HM_AMPM);

        holder.fileImage.setImageResource(R.drawable.ic_file);
        holder.fileName.setText(fileData.fileName);
        holder.fileInformation.setText(fileInformation);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.downloadFile(mContext, fileData.filePath.toString(), fileData.fileName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFileLogData.size();
    }
}
