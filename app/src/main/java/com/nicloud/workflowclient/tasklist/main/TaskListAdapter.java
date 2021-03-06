package com.nicloud.workflowclient.tasklist.main;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.Task;
import com.nicloud.workflowclient.data.data.Worker;
import com.nicloud.workflowclient.main.WorkingData;
import com.nicloud.workflowclient.detailedtask.main.DetailedTaskActivity;
import com.nicloud.workflowclient.dialog.fragment.DisplayDialogFragment;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.List;

/**
 * Created by logicmelody on 2015/11/11.
 */
public class TaskListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private FragmentManager mFragmentManager;

    private List<TaskListItem> mDataSet;

    private boolean mIsMyTaskList = false;

    private class TaskViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public View dueDateUnderline;
        public View contentUnderline;
        public View taskCardNameContainer;
        public View ownerContainer;
        public TextView taskName;
        public TextView caseName;
        public TextView dueDate;
        public ImageView completeButton;
        public ImageView ownerAvatar;

        public TaskViewHolder(View view) {
            super(view);
            findViews(view);
            setupViews();
        }

        private void findViews(View view) {
            this.view = view;
            dueDateUnderline = view.findViewById(R.id.task_card_due_date_underline);
            contentUnderline = view.findViewById(R.id.task_card_content_underline);
            taskCardNameContainer = view.findViewById(R.id.task_card_name_container);
            ownerContainer = view.findViewById(R.id.task_card_owner_container);
            taskName = (TextView) view.findViewById(R.id.task_card_name);
            caseName = (TextView) view.findViewById(R.id.task_card_case_name);
            dueDate = (TextView) view.findViewById(R.id.task_card_due_date);
            completeButton = (ImageView) view.findViewById(R.id.complete_task_button);
            ownerAvatar = (ImageView) view.findViewById(R.id.owner_avatar);
        }

        private void setupViews() {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(
                            DetailedTaskActivity.generateActivityIntent(mContext,
                                                                        mDataSet.get(getAdapterPosition()).task.id));
                }
            });

            completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.showDialog(mFragmentManager,
                            DisplayDialogFragment.DialogType.COMPLETE_TASK,
                            mDataSet.get(getAdapterPosition()).task.id);
                }
            });
        }
    }


    public TaskListAdapter(Context context, FragmentManager fm, List<TaskListItem> dataSet, boolean isMyTaskList) {
        mContext = context;
        mFragmentManager = fm;
        mDataSet = dataSet;
        mIsMyTaskList = isMyTaskList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(mContext).inflate(R.layout.task_card, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null) return;

        TaskViewHolder taskVH = (TaskViewHolder) holder;
        Task task = mDataSet.get(position).task;
        Worker worker = WorkingData.getInstance(mContext).getWorkerById(task.workerId);

        // Due date underline
        if (mDataSet.get(position).showDueDateUnderline) {
            taskVH.dueDateUnderline.setVisibility(View.VISIBLE);

        } else {
            taskVH.dueDateUnderline.setVisibility(View.GONE);
        }

        // Due date
        if (mDataSet.get(position).showDueDate) {
            taskVH.dueDate.setVisibility(View.VISIBLE);

            if (task.dueDate.getTime() == -1L || task.dueDate == null) {
                taskVH.dueDate.setText(mContext.getString(R.string.task_card_no_due_date));
                taskVH.dueDate.setTextColor(mContext.getResources().getColor(R.color.task_card_due_date_normal_text_color));

            } else {
                taskVH.dueDate.setText(Utils.timestamp2Date(task.dueDate, Utils.DATE_FORMAT_MD));

                if (task.dueDate.getTime() > System.currentTimeMillis()) {
                    taskVH.dueDate.setTextColor(
                            mContext.getResources().getColor(R.color.task_card_due_date_normal_text_color));

                } else {
                    taskVH.dueDate.setTextColor(
                            mContext.getResources().getColor(R.color.task_card_due_date_delay_text_color));
                }
            }

        } else {
            taskVH.dueDate.setVisibility(View.GONE);
        }

        // Task card name container
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) taskVH.taskCardNameContainer.getLayoutParams();
        params.addRule(RelativeLayout.LEFT_OF, mIsMyTaskList ?
                taskVH.completeButton.getId() : taskVH.ownerContainer.getId());

        taskVH.taskName.setText(task.name);
        taskVH.caseName.setText(task.caseName);
        taskVH.taskCardNameContainer.setLayoutParams(params);

        // Completed button
        taskVH.completeButton.setVisibility(mIsMyTaskList ? View.VISIBLE : View.GONE);

        // Owner container
        if (mIsMyTaskList) {
            taskVH.ownerContainer.setVisibility(View.GONE);

        } else {
            taskVH.ownerContainer.setVisibility(View.VISIBLE);
            Utils.setWorkerAvatarImage(mContext, worker, taskVH.ownerAvatar, R.drawable.ic_worker_black);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
