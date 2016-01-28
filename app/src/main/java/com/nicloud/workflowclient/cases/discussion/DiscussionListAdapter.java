package com.nicloud.workflowclient.cases.discussion;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.activity.LoadingActivityUserIconCommand;
import com.nicloud.workflowclient.data.connectserver.worker.LoadingWorkerAvatar;
import com.nicloud.workflowclient.data.data.data.Worker;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.utility.Utilities;

import java.util.Date;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/26.
 */
public class DiscussionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<DiscussionItem> mDiscussionData;


    private class DiscussionItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView workerAvatar;
        public TextView workerName;
        public TextView time;
        public TextView content;

        public DiscussionItemViewHolder(View itemView) {
            super(itemView);
            workerAvatar = (ImageView) itemView.findViewById(R.id.discussion_item_worker_avatar);
            workerName = (TextView) itemView.findViewById(R.id.discussion_item_worker_name);
            time = (TextView) itemView.findViewById(R.id.discussion_item_time);
            content = (TextView) itemView.findViewById(R.id.discussion_item_content);
        }
    }

    public DiscussionListAdapter(Context context, List<DiscussionItem> discussionData) {
        mContext = context;
        mDiscussionData = discussionData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiscussionItemViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.discussion_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DiscussionItemViewHolder itemVH = (DiscussionItemViewHolder) holder;
        Worker worker = WorkingData.getInstance(mContext).getWorkerById(mDiscussionData.get(position).workerId);

        itemVH.workerName.setText(mDiscussionData.get(position).workerName);
        itemVH.time.setText(Utilities.timestamp2Date(
                new Date(mDiscussionData.get(position).createdTime), Utilities.DATE_FORMAT_YMD_HM_AMPM));
        itemVH.content.setText(mDiscussionData.get(position).content);

        if (worker != null && worker.avatar != null) {
            itemVH.workerAvatar.setImageDrawable(worker.avatar);

        } else {
            itemVH.workerAvatar.setImageResource(R.drawable.ic_worker_black);
        }
    }

    @Override
    public int getItemCount() {
        return mDiscussionData.size();
    }
}
