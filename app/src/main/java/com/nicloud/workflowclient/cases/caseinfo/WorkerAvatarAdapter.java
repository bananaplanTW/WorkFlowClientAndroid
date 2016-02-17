package com.nicloud.workflowclient.cases.caseinfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.Worker;
import com.nicloud.workflowclient.dialog.activity.AddWorkerToCaseActivity;
import com.nicloud.workflowclient.utility.utils.Utils;
import com.pkmmte.view.CircularImageView;

import java.util.List;

/**
 * Created by logicmelody on 2016/2/14.
 */
public class WorkerAvatarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<Worker> mDataSet;


    private class WorkerAvatarViewHolder extends RecyclerView.ViewHolder {

        public CircularImageView avatar;

        public WorkerAvatarViewHolder(View itemView) {
            super(itemView);
            avatar = (CircularImageView) itemView.findViewById(R.id.worker_avatar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(AddWorkerToCaseActivity.generateAddWorkerToCaseDialogIntent(mContext));
                }
            });
        }
    }


    public WorkerAvatarAdapter(Context context, List<Worker> dataSet) {
        mContext = context;
        mDataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WorkerAvatarViewHolder(LayoutInflater.from(mContext).inflate(R.layout.worker_avatar, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WorkerAvatarViewHolder vh = (WorkerAvatarViewHolder) holder;

        Utils.setWorkerAvatarImage(mContext, mDataSet.get(position), vh.avatar, R.drawable.ic_worker_black);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
