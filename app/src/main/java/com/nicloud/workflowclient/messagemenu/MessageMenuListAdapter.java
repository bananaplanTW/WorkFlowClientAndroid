package com.nicloud.workflowclient.messagemenu;

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
 * Created by logicmelody on 2015/12/23.
 */
public class MessageMenuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class ItemViewType {
        public static final int EMPTY = 0;
        public static final int TITLE = 1;
        public static final int CASE = 2;
        public static final int WORKER = 3;
    }

    private Context mContext;

    private List<MessageMenuItem> mDataSet;


    private class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder {

        public TextView title;


        public TitleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.message_menu_title);
        }
    }

    private class CaseViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView caseName;


        public CaseViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            caseName = (TextView) itemView.findViewById(R.id.message_menu_case);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
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

                }
            });
        }
    }

    public MessageMenuListAdapter(Context context, List<MessageMenuItem> dataSet) {
        mContext = context;
        mDataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemViewType.EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message_menu_empty, parent, false));

            case ItemViewType.TITLE:
                return new TitleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message_menu_title, parent, false));

            case ItemViewType.CASE:
                return new CaseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message_menu_case, parent, false));

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

            case ItemViewType.CASE:
                onBindCase((CaseViewHolder) holder, messageMenuItem);
                break;

            case ItemViewType.WORKER:
                onBindWorker((WorkerViewHolder) holder, messageMenuItem);
                break;
        }
    }

    private void onBindTitle(TitleViewHolder holder, MessageMenuItem messageMenuItem) {
        holder.title.setText(messageMenuItem.name);
    }

    private void onBindCase(CaseViewHolder holder, MessageMenuItem messageMenuItem) {
        holder.caseName.setText(messageMenuItem.name);
        holder.view.setSelected(messageMenuItem.isSelected);
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
