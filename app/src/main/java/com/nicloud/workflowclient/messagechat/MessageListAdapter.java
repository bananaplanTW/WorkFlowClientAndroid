package com.nicloud.workflowclient.messagechat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.data.WorkingData;

import java.util.List;

/**
 * Created by logicmelody on 2016/1/6.
 */
public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final class ItemViewType {
        public static final int ME = 0;
        public static final int OTHER = 1;
    }

    private Context mContext;

    private List<MessageItem> mMessageItemData;


    private class MeViewHolder extends RecyclerView.ViewHolder {

        public TextView messageContent;


        public MeViewHolder(View itemView) {
            super(itemView);
            messageContent = (TextView) itemView.findViewById(R.id.message_content);
        }
    }

    private class OtherViewHolder extends RecyclerView.ViewHolder {

        public ImageView workerAvatar;
        public TextView messageContent;


        public OtherViewHolder(View itemView) {
            super(itemView);
            workerAvatar = (ImageView) itemView.findViewById(R.id.worker_avatar);
            messageContent = (TextView) itemView.findViewById(R.id.message_content);
        }
    }

    public MessageListAdapter(Context context, List<MessageItem> messageItemData) {
        mContext = context;
        mMessageItemData = messageItemData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemViewType.ME:
                return new MeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message_me_item, parent, false));

            case ItemViewType.OTHER:
                return new OtherViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message_other_item, parent, false));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageItem messageItem = mMessageItemData.get(position);

        if (messageItem.isMe) {
            onBindMeViewHolder((MeViewHolder) holder, messageItem);

        } else {
            onBindOtherViewHolder((OtherViewHolder) holder, messageItem);
        }
    }

    private void onBindMeViewHolder(MeViewHolder holder, MessageItem messageItem) {
        holder.messageContent.setText(messageItem.content);
    }

    private void onBindOtherViewHolder(OtherViewHolder holder, MessageItem messageItem) {
        Drawable avatar = WorkingData.getInstance(mContext).getWorkerById(messageItem.ownerId).avatar;

        holder.messageContent.setText(messageItem.content);

        if (avatar == null) {
            holder.workerAvatar.setImageResource(R.drawable.ic_worker_black);

        } else {
            holder.workerAvatar.setImageDrawable(avatar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mMessageItemData.get(position).isMe ? ItemViewType.ME : ItemViewType.OTHER;
    }

    @Override
    public int getItemCount() {
        return mMessageItemData.size();
    }
}
