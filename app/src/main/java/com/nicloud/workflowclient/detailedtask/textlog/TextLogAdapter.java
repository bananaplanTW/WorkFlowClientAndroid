package com.nicloud.workflowclient.detailedtask.textlog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingActivityUserIconCommand;
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.data.data.activity.RecordData;
import com.nicloud.workflowclient.data.data.data.Worker;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/12.
 */
public class TextLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<BaseData> mTextLogData;


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

    public TextLogAdapter(Context context, List<BaseData> textLogData) {
        mContext = context;
        mTextLogData = textLogData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextLogItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.text_log_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecordData recordData = (RecordData) mTextLogData.get(position);
        Worker reporter = WorkingData.getInstance(mContext).getWorkerById(recordData.reporter);
        TextLogItemViewHolder vh = (TextLogItemViewHolder) holder;

        if (reporter != null && reporter.avatar != null) {
            vh.icon.setImageDrawable(reporter.avatar);

        } else {
            vh.icon.setImageResource(R.drawable.ic_worker_black);

            if (recordData.avatarUri != null) {
                LoadingActivityUserIconCommand loadingActivityUserIconCommand
                        = new LoadingActivityUserIconCommand(mContext, recordData.avatarUri, recordData, vh.icon);
                loadingActivityUserIconCommand.execute();
            }
        }

        vh.userName.setText(recordData.reporterName);
        vh.description.setText(recordData.description);
        vh.timeStamp.setText(Utils.timestamp2Date(new Date(recordData.time), Utils.DATE_FORMAT_YMD_HM_AMPM));
    }

    @Override
    public int getItemCount() {
        return mTextLogData.size();
    }
}
