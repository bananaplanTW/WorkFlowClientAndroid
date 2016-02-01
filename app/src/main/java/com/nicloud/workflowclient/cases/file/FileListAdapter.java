package com.nicloud.workflowclient.cases.file;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.asyntask.LoadImageTask;
import com.nicloud.workflowclient.cases.discussion.DiscussionItem;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingPhotoDataCommand;
import com.nicloud.workflowclient.data.data.activity.FileData;
import com.nicloud.workflowclient.data.data.activity.PhotoData;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.DisplayImageActivity;
import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/12.
 */
public class FileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<FileItem> mFileListData;


    private class FileItemViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ImageView fileImage;
        public TextView fileName;
        public TextView fileInformation;


        public FileItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            fileImage = (ImageView) itemView.findViewById(R.id.file_image);
            fileName = (TextView) itemView.findViewById(R.id.file_name);
            fileInformation = (TextView) itemView.findViewById(R.id.log_information);
        }
    }

    public FileListAdapter(Context context, List<FileItem> data) {
        mContext = context;
        mFileListData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.file_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindFile((FileItemViewHolder) holder, position);
    }

    private void onBindFile(FileItemViewHolder holder, int position) {
        final FileItem fileItem = mFileListData.get(position);
        final Uri.Builder fileBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon().path(fileItem.fileUrl);
        String fileInformation = fileItem.ownerName + " " +
                Utils.timestamp2Date(new Date(fileItem.createdTime), Utils.DATE_FORMAT_YMD_HM_AMPM);

        holder.fileName.setText(fileItem.fileName);
        holder.fileInformation.setText(fileInformation);

        // File
        if (WorkFlowContract.File.Type.FILE.equals(fileItem.fileType)) {
            holder.fileImage.setImageResource(R.drawable.ic_file);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.downloadFile(mContext, fileBuilder.build().toString(), fileItem.fileName);
                }
            });

        // Image
        } else if (WorkFlowContract.File.Type.IMAGE.equals(fileItem.fileType)) {
            if (fileItem.fileThumbnail != null) {
                holder.fileImage.setImageDrawable(fileItem.fileThumbnail);

            } else {
                holder.fileImage.setImageResource(R.drawable.ic_photo);

                Uri.Builder imageBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
                imageBuilder.path(fileItem.fileThumbUrl);

                new LoadImageTask(mContext, imageBuilder.build(), holder.fileImage, fileItem.fileThumbnail).execute();
            }

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(DisplayImageActivity
                            .launchDisplayImageActivity(mContext, fileItem.fileName,
                                                        fileBuilder.build().toString()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFileListData.size();
    }
}
