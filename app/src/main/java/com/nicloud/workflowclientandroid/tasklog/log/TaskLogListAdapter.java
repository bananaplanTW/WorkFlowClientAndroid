package com.nicloud.workflowclientandroid.tasklog.log;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.data.data.activity.BaseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/11/17.
 */
public class TaskLogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final class ItemViewType {
        public static final int RECORD = 0;
        public static final int FILE = 1;
        public static final int PHOTO = 2;
    }

    private Context mContext;
    private List<BaseData> mDataSet = new ArrayList<>();

    private class RecordViewHolder extends RecyclerView.ViewHolder {

        public ImageView userIcon;
        public TextView userName;
        public TextView content;
        public TextView timestamp;

        public RecordViewHolder(View itemView) {
            super(itemView);
            userIcon = (ImageView) itemView.findViewById(R.id.log_user_icon);
            userName = (TextView) itemView.findViewById(R.id.log_user_name);
            content = (TextView) itemView.findViewById(R.id.log_content);
            timestamp = (TextView) itemView.findViewById(R.id.log_timestamp);
        }
    }


    public TaskLogListAdapter(Context context) {
        mContext = context;
    }

    public void swapDataSet(List<BaseData> dataSet) {
        Log.d("danny", "DataSet size = " + dataSet.size());
        mDataSet.clear();
        mDataSet.addAll(dataSet);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordViewHolder(LayoutInflater.from(mContext).inflate(R.layout.task_log_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecordViewHolder recordVH = (RecordViewHolder) holder;

//        recordVH.userName.setText(mDataSet.get(position).userName);
//        recordVH.content.setText(mDataSet.get(position).content);
//        recordVH.timestamp.setText(mDataSet.get(position).timeStamp);
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

            case FILE:
                return ItemViewType.FILE;

            case PHOTO:
                return ItemViewType.PHOTO;
        }

        return super.getItemViewType(position);
    }
}
