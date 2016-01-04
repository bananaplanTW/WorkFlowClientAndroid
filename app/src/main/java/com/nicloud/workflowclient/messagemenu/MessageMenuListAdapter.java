package com.nicloud.workflowclient.messagemenu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.messagemenu.MessageMenuFragment.OnClickMessageMenuItemListener;

import java.util.List;

/**
 * Created by logicmelody on 2015/12/23.
 */
public class MessageMenuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MessageMenuListAdapter";

    public static class ItemViewType {
        public static final int EMPTY = 0;
        public static final int TITLE = 1;
        public static final int WORKER = 2;
    }

    private Context mContext;

    private List<MessageMenuItem> mDataSet;

    private OnClickMessageMenuItemListener mOnClickMessageMenuItemListener;

    private MessageMenuItem mCurrentSelectedItem;


    private class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder {

        public TextView title;


        public TitleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.menu_title);
        }
    }

    private class WorkerViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ImageView workerAvatar;
        public TextView worker;

        public WorkerViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            workerAvatar = (ImageView) itemView.findViewById(R.id.message_menu_worker_avatar);
            worker = (TextView) itemView.findViewById(R.id.message_menu_worker_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickMessageMenuItem(mDataSet.get(getAdapterPosition()));
                }
            });
        }
    }

    private void onClickMessageMenuItem(MessageMenuItem clickedItem) {
        if (clickedItem.isSelected) return;

        clickedItem.isSelected = true;
        if (mCurrentSelectedItem != null) {
            mCurrentSelectedItem.isSelected = false;
        }
        mCurrentSelectedItem = clickedItem;

        notifyDataSetChanged();

        mOnClickMessageMenuItemListener.onClickMessageMenuItem(clickedItem.id, clickedItem.name);

        Log.d(TAG, "Current selected message menu item: " + mCurrentSelectedItem.name);
    }

    public void clearSelectedMessageMenuItem() {
        if (mCurrentSelectedItem == null) return;

        mCurrentSelectedItem.isSelected = false;
        mCurrentSelectedItem = null;

        notifyDataSetChanged();
    }

    public MessageMenuListAdapter(Context context, List<MessageMenuItem> dataSet, OnClickMessageMenuItemListener listener) {
        mContext = context;
        mDataSet = dataSet;
        mOnClickMessageMenuItemListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemViewType.EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.menu_empty, parent, false));

            case ItemViewType.TITLE:
                return new TitleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.menu_title, parent, false));

            case ItemViewType.WORKER:
                return new WorkerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message_menu_worker, parent, false));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageMenuItem messageMenuItem = mDataSet.get(position);

        switch (messageMenuItem.viewType) {
            case ItemViewType.TITLE:
                onBindTitle((TitleViewHolder) holder, messageMenuItem);
                break;

            case ItemViewType.WORKER:
                onBindWorker((WorkerViewHolder) holder, messageMenuItem);
                break;
        }

        if (messageMenuItem.isSelected) {
            mCurrentSelectedItem = messageMenuItem;
        }
    }

    private void onBindTitle(TitleViewHolder holder, MessageMenuItem messageMenuItem) {
        holder.title.setText(messageMenuItem.name);
    }

    private void onBindWorker(WorkerViewHolder holder, MessageMenuItem messageMenuItem) {
        holder.worker.setText(messageMenuItem.name);
        holder.view.setSelected(messageMenuItem.isSelected);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).viewType;
    }
}
