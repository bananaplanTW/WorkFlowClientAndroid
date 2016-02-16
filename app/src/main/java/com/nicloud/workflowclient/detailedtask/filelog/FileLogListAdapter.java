package com.nicloud.workflowclient.detailedtask.filelog;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.asyntask.LoadImage;
import com.nicloud.workflowclient.data.data.File;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.DisplayImageActivity;
import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/12.
 */
public class FileLogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<File> mFileLogData;


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

    public FileLogListAdapter(Context context, List<File> data) {
        mContext = context;
        mFileLogData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileLogItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.file_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FileLogItemViewHolder vh = (FileLogItemViewHolder) holder;

        String type = mFileLogData.get(position).fileType;
        File file = mFileLogData.get(position);
        Uri.Builder fileBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon().path(file.fileUrl);
        String fileInformation = file.ownerName + " " +
                Utils.timestamp2Date(new Date(file.createdTime), Utils.DATE_FORMAT_YMD_HM_AMPM);

        vh.fileName.setText(file.fileName);
        vh.fileInformation.setText(fileInformation);

        if (WorkFlowContract.File.Type.IMAGE.equals(type)) {
            onBindImageLog((FileLogItemViewHolder) holder, file, fileBuilder.build().toString());

        } else if (WorkFlowContract.File.Type.FILE.equals(type)) {
            onBindFileLog((FileLogItemViewHolder) holder, file, fileBuilder.build().toString());
        }
    }

    private void onBindImageLog(FileLogItemViewHolder holder, final File file, final String fileUrl) {
        if (file.fileThumbnail != null) {
            holder.fileImage.setImageDrawable(file.fileThumbnail);

        } else {
            holder.fileImage.setImageResource(R.drawable.ic_photo);

            if (file.fileThumbUrl != null) {
                new LoadImage(mContext, file.fileThumbUrl, holder.fileImage, file.fileThumbnail).execute();
            }
        }

        if (!TextUtils.isEmpty(file.fileUrl)) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(DisplayImageActivity
                            .launchDisplayImageActivity(mContext, file.fileName, fileUrl));
                }
            });
        }
    }

    private void onBindFileLog(FileLogItemViewHolder holder, final File file, final String fileUrl) {
        holder.fileImage.setImageResource(R.drawable.ic_file);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.downloadFile(mContext, fileUrl, file.fileName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFileLogData.size();
    }
}
