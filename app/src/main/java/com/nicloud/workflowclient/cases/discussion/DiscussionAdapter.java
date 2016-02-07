package com.nicloud.workflowclient.cases.discussion;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.asyntask.LoadImageTask;
import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.data.data.Worker;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.utility.DisplayImageActivity;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/26.
 */
public class DiscussionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final class ItemViewType {
        public static final int MESSAGE = 0;
        public static final int IMAGE = 1;
        public static final int FILE = 2;
    }

    private Context mContext;

    private List<DiscussionItem> mDiscussionData;

    private class ViewHolderBase extends RecyclerView.ViewHolder {

        public View view;
        public ImageView workerAvatar;
        public TextView workerName;
        public TextView time;
        public TextView content;

        public ViewHolderBase(View itemView) {
            super(itemView);
            view= itemView;
            workerAvatar = (ImageView) itemView.findViewById(R.id.discussion_item_worker_avatar);
            workerName = (TextView) itemView.findViewById(R.id.discussion_item_worker_name);
            time = (TextView) itemView.findViewById(R.id.discussion_item_time);
            content = (TextView) itemView.findViewById(R.id.discussion_item_content);
        }
    }

    private class MessageViewHolder extends ViewHolderBase {

        public MessageViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class ImageViewHolder extends ViewHolderBase {

        public ImageView thumbnail;

        public ImageViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.discussion_thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiscussionItem item = mDiscussionData.get(getAdapterPosition());
                    Uri.Builder imageBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
                    imageBuilder.path(item.fileUri);

                    mContext.startActivity(DisplayImageActivity
                            .launchDisplayImageActivity(mContext, item.fileName, imageBuilder.build().toString()));
                }
            });
        }
    }

    private class FileViewHolder extends ViewHolderBase {

        public FileViewHolder(View itemView) {
            super(itemView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiscussionItem item = mDiscussionData.get(getAdapterPosition());
                    Uri.Builder fileBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
                    fileBuilder.path(item.fileUri);

                    Utils.downloadFile(mContext, fileBuilder.build().toString(), item.fileName);
                }
            });
        }
    }


    public DiscussionAdapter(Context context, List<DiscussionItem> discussionData) {
        mContext = context;
        mDiscussionData = discussionData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemViewType.MESSAGE:
                return new MessageViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.discussion_message, parent, false));

            case ItemViewType.IMAGE:
                return new ImageViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.discussion_image, parent, false));

            case ItemViewType.FILE:
                return new FileViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.discussion_file, parent, false));
        }

        return new MessageViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.discussion_message, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderBase baseVH = (ViewHolderBase) holder;
        Worker worker = WorkingData.getInstance(mContext).getWorkerById(mDiscussionData.get(position).workerId);
        String type = mDiscussionData.get(position).type;

        // Worker avatar
        if (worker != null && worker.avatar != null) {
            baseVH.workerAvatar.setImageDrawable(worker.avatar);

        } else {
            baseVH.workerAvatar.setImageResource(R.drawable.ic_worker_black);
        }

        // Worker name
        baseVH.workerName.setText(mDiscussionData.get(position).workerName);

        // Created time
        baseVH.time.setText(Utils.timestamp2Date(
                new Date(mDiscussionData.get(position).createdTime), Utils.DATE_FORMAT_YMD_HM_AMPM));

        // Others
        if (WorkFlowContract.Discussion.Type.MESSAGE.equals(type)) {
            onBindMessage((MessageViewHolder) baseVH, mDiscussionData.get(position));

        } else if (WorkFlowContract.Discussion.Type.IMAGE.equals(type)) {
            onBindImage((ImageViewHolder) baseVH, mDiscussionData.get(position));

        } else if (WorkFlowContract.Discussion.Type.FILE.equals(type)) {
            onBindFile((FileViewHolder) baseVH, mDiscussionData.get(position));
        }
    }

    private void onBindMessage(MessageViewHolder messageVH, DiscussionItem item) {
        messageVH.content.setText(item.content);
    }

    private void onBindImage(ImageViewHolder imageVH, DiscussionItem item) {
        // Content
        imageVH.content.setText(item.fileName);
        imageVH.content.setPaintFlags(imageVH.content.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Image thumbnail
        if (item.fileThumb != null) {
            imageVH.thumbnail.setImageDrawable(item.fileThumb);

        } else {
            imageVH.thumbnail.setImageDrawable(null);

            Uri.Builder imageBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
            imageBuilder.path(item.fileUri);

            new LoadImageTask(mContext, imageBuilder.build(), imageVH.thumbnail, item.fileThumb).execute();
        }
    }

    private void onBindFile(FileViewHolder fileVH, DiscussionItem item) {
        fileVH.content.setText(item.fileName);
        fileVH.content.setPaintFlags(fileVH.content.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public int getItemCount() {
        return mDiscussionData.size();
    }

    @Override
    public int getItemViewType(int position) {
        String type = mDiscussionData.get(position).type;

        if (WorkFlowContract.Discussion.Type.MESSAGE.equals(type)) {
            return ItemViewType.MESSAGE;

        } else if (WorkFlowContract.Discussion.Type.IMAGE.equals(type)) {
            return ItemViewType.IMAGE;

        } else if (WorkFlowContract.Discussion.Type.FILE.equals(type)) {
            return ItemViewType.FILE;
        }

        return ItemViewType.MESSAGE;
    }
}
