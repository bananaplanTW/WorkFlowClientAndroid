package com.nicloud.workflowclientandroid.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclientandroid.R;
import com.nicloud.workflowclientandroid.data.Task;

import java.util.List;

/**
 * Created by logicmelody on 2015/11/11.
 */
public class ScheduledTasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Task> mDataSet;


    private class CardViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView taskIndex;
        public TextView taskName;

        public CardViewHolder(View view) {
            super(view);
            findViews(view);
            setupViews();
        }

        private void findViews(View view) {
            this.view = view;
            taskIndex = (TextView) view.findViewById(R.id.scheduled_task_index);
            taskName = (TextView) view.findViewById(R.id.scheduled_task_name);
        }

        private void setupViews() {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public ScheduledTasksAdapter(Context context, List<Task> dataSet) {
        mContext = context;
        mDataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardViewHolder(LayoutInflater.from(mContext).inflate(R.layout.scheduled_task_card, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CardViewHolder cardVH = (CardViewHolder) holder;
        Task task = mDataSet.get(position);

        cardVH.taskIndex.setText(String.valueOf(position + 1));
        cardVH.taskName.setText(task.name);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
