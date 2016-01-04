package com.nicloud.workflowclient.tasklist;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.data.Task;
import com.nicloud.workflowclient.data.data.data.Worker;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.detailedtask.DetailedTaskActivity;
import com.nicloud.workflowclient.utility.Utilities;

import java.util.List;

/**
 * Created by logicmelody on 2015/11/11.
 */
public class TasksListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class ItemViewType {
        public static final int TITLE = 0;
        public static final int WIP_TASK = 1;
        public static final int SCHEDULED_TASK = 2;
    }

    private Context mContext;
    private FragmentManager mFragmentManager;
    private List<TasksListItem> mDataSet;


    private class TitleViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public TitleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.task_list_title);
        }
    }

    private class ScheduledTaskViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView taskIndex;
        public TextView taskName;
        public TextView caseName;
        public TextView status;

        public ScheduledTaskViewHolder(View view) {
            super(view);
            findViews(view);
            setupViews();
        }

        private void findViews(View view) {
            this.view = view;
            taskIndex = (TextView) view.findViewById(R.id.scheduled_task_index);
            taskName = (TextView) view.findViewById(R.id.scheduled_task_task_name);
            caseName = (TextView) view.findViewById(R.id.scheduled_task_case_name);
            status = (TextView) view.findViewById(R.id.scheduled_task_status);
        }

        private void setupViews() {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(
                            DetailedTaskActivity.generateActivityIntent(mContext, mDataSet.get(getAdapterPosition()).task.id));
                }
            });
        }
    }


    public TasksListAdapter(Context context, FragmentManager fm, List<TasksListItem> dataSet) {
        mContext = context;
        mFragmentManager = fm;
        mDataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemViewType.TITLE:
                return new TitleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.task_list_title, parent, false));

            case ItemViewType.SCHEDULED_TASK:
                return new ScheduledTaskViewHolder(LayoutInflater.from(mContext).inflate(R.layout.scheduled_task_card,
                                                   parent, false));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null) return;

        Task task = mDataSet.get(position).task;

        switch (mDataSet.get(position).itemViewType) {
            case ItemViewType.TITLE:
                bindTitleViewHolder((TitleViewHolder) holder, task);
                break;

            case ItemViewType.SCHEDULED_TASK:
                bindScheduledTaskViewHolder((ScheduledTaskViewHolder) holder, task, position);
                break;
        }
    }

    private void bindTitleViewHolder(TitleViewHolder holder, Task task) {
        holder.title.setText(task.name);
    }

    private void bindScheduledTaskViewHolder(ScheduledTaskViewHolder holder, Task task, int position) {
        holder.taskIndex.setText(String.valueOf(position));
        holder.taskName.setText(task.name);
        holder.caseName.setText(task.caseName);

//        if (task.spentTime > 0L) {
//            holder.status.setText(R.string.task_status_pause);
//            holder.status.setBackgroundResource(R.drawable.scheduled_task_status_pause_background);
//        }
    }

    @Override
    public int getItemViewType(int position) {
        int itemViewType = mDataSet.get(position).itemViewType;

        if (ItemViewType.TITLE == itemViewType || ItemViewType.TITLE == itemViewType) {
            return ItemViewType.TITLE;

        } else {
            return ItemViewType.SCHEDULED_TASK;
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
