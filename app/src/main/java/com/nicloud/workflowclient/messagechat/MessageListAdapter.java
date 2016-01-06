package com.nicloud.workflowclient.messagechat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;

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

    private List<Message> mMessageData;


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

    public MessageListAdapter(Context context, List<Message> messageData) {
        mContext = context;
        mMessageData = messageData;
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
        Message message = mMessageData.get(position);

        if (message.isMe) {
            onBindMeViewHolder((MeViewHolder) holder, message);

        } else {
            onBindOtherViewHolder((OtherViewHolder) holder, message);
        }
    }

    private void onBindMeViewHolder(MeViewHolder holder, Message message) {
        holder.messageContent.setText(message.content);
    }

    private void onBindOtherViewHolder(OtherViewHolder holder, Message message) {
        holder.messageContent.setText(message.content);
    }

    @Override
    public int getItemViewType(int position) {
        return mMessageData.get(position).isMe ? ItemViewType.ME : ItemViewType.OTHER;
    }

    @Override
    public int getItemCount() {
        return mMessageData.size();
    }
}
