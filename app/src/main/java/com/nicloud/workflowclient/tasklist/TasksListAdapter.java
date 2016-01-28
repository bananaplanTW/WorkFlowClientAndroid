package com.nicloud.workflowclient.tasklist;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.data.Task;
import com.nicloud.workflowclient.detailedtask.main.DetailedTaskActivity;
import com.nicloud.workflowclient.dialog.DisplayDialogFragment;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.List;

/**
 * Created by logicmelody on 2015/11/11.
 */
public class TasksListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private FragmentManager mFragmentManager;
    private List<Task> mDataSet;

    private class TaskViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView taskName;
        public TextView caseName;
        public ImageView completeButton;

        public TaskViewHolder(View view) {
            super(view);
            findViews(view);
            setupViews();
        }

        private void findViews(View view) {
            this.view = view;
            taskName = (TextView) view.findViewById(R.id.task_card_name);
            caseName = (TextView) view.findViewById(R.id.task_card_case_name);
            completeButton = (ImageView) view.findViewById(R.id.complete_task_button);
        }

        private void setupViews() {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(
                            DetailedTaskActivity.generateActivityIntent(mContext, mDataSet.get(getAdapterPosition()).id));
                }
            });

            completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showDialog(mFragmentManager,
                            DisplayDialogFragment.DialogType.COMPLETE_TASK,
                            mDataSet.get(getAdapterPosition()).id);
                }
            });
        }
    }


    public TasksListAdapter(Context context, FragmentManager fm, List<Task> dataSet) {
        mContext = context;
        mFragmentManager = fm;
        mDataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(mContext).inflate(R.layout.task_card, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null) return;

        TaskViewHolder taskVH = (TaskViewHolder) holder;

        if (position == 0) {
            taskVH.view.setBackgroundResource(R.drawable.scheduled_task_first_card_background);
        }

        taskVH.taskName.setText(mDataSet.get(position).name);
        taskVH.caseName.setText(mDataSet.get(position).caseName);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
