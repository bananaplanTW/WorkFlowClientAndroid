package com.nicloud.workflowclient.dialog.chooseworker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.dialog.chooseworker.ChooseWorkerActivity.ChooseWorkerItem;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.List;

/**
 * Created by logicmelody on 2016/2/8.
 */
public class WorkerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<ChooseWorkerItem> mDataSet;


    private class WorkerViewHolder extends RecyclerView.ViewHolder {

        public ImageView workerAvatar;
        public TextView workerName;
        public ImageView isChosenIndicator;


        public WorkerViewHolder(View itemView) {
            super(itemView);
            workerAvatar = (ImageView) itemView.findViewById(R.id.worker_avatar);
            workerName = (TextView) itemView.findViewById(R.id.worker_name);
            isChosenIndicator = (ImageView) itemView.findViewById(R.id.is_chosen_indicator);
        }
    }

    public WorkerListAdapter(Context context, List<ChooseWorkerItem> dataSet) {
        mContext = context;
        mDataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WorkerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.worker_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WorkerViewHolder workerVH = (WorkerViewHolder) holder;

        Utils.setWorkerAvatarImage(mContext, mDataSet.get(position).worker,
                                   workerVH.workerAvatar, R.drawable.ic_worker_black);
        workerVH.workerName.setText(mDataSet.get(position).worker.name);
        workerVH.isChosenIndicator.setVisibility(mDataSet.get(position).isChosen ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
