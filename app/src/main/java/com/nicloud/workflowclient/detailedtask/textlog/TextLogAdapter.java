package com.nicloud.workflowclient.detailedtask.textlog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.TaskTextLog;
import com.nicloud.workflowclient.data.data.Worker;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/12.
 */
public class TextLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<TaskTextLog> mTextLogData;


    private class TextLogItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public TextView userName;
        public TextView description;
        public TextView timeStamp;


        public TextLogItemViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.log_icon);
            userName = (TextView) itemView.findViewById(R.id.log_user_name);
            description = (TextView) itemView.findViewById(R.id.log_description);
            timeStamp = (TextView) itemView.findViewById(R.id.log_timestamp);
        }
    }

    public TextLogAdapter(Context context, List<TaskTextLog> textLogData) {
        mContext = context;
        mTextLogData = textLogData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextLogItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.text_log_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TaskTextLog taskTextLog = mTextLogData.get(position);
        Worker owner = WorkingData.getInstance(mContext).getWorkerById(taskTextLog.ownerId);
        TextLogItemViewHolder vh = (TextLogItemViewHolder) holder;

        Utils.setWorkerAvatarImage(mContext, owner, vh.icon, R.drawable.ic_worker_black);
        vh.userName.setText(taskTextLog.ownerName);
        vh.description.setText(taskTextLog.content);
        vh.timeStamp.setText(Utils.timestamp2Date(new Date(taskTextLog.createdTime), Utils.DATE_FORMAT_YMD_HM_AMPM));
    }

    @Override
    public int getItemCount() {
        return mTextLogData.size();
    }
}
