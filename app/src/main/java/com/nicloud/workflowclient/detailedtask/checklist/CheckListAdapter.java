package com.nicloud.workflowclient.detailedtask.checklist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.service.ActionService;

import java.util.List;

/**
 * Created by logicmelody on 2015/12/9.
 */
public class CheckListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<CheckItem> mDataSet;

    private String mTaskId;


    private class CheckItemViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView name;
        public CheckBox checkBox;


        public CheckItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            findViews();

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeCheckItemCheckStatus(checkBox.isChecked());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean currentChecked = checkBox.isChecked();
                    checkBox.setChecked(!currentChecked);

                    changeCheckItemCheckStatus(!currentChecked);
                }
            });
        }

        private void findViews() {
            name = (TextView) itemView.findViewById(R.id.check_item_name);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_item_check_box);
        }

        private void changeCheckItemCheckStatus(boolean isChecked) {
            Intent intent = new Intent(mContext, ActionService.class);
            intent.setAction(ActionService.ServerAction.CHECK_ITEM);
            intent.putExtra(ActionService.ExtraKey.TASK_ID, mTaskId);
            intent.putExtra(ActionService.ExtraKey.CHECK_ITEM_INDEX, getAdapterPosition());
            intent.putExtra(ActionService.ExtraKey.CHECK_ITEM_CHECKED, isChecked);

            mContext.startService(intent);
        }
    }

    public CheckListAdapter(Context context, String taskId, List<CheckItem> dataSet) {
        mContext = context;
        mTaskId = taskId;
        mDataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CheckItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.check_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CheckItemViewHolder checkItemVH = (CheckItemViewHolder) holder;

        checkItemVH.checkBox.setChecked(mDataSet.get(position).isChecked);
        setCheckItemText(checkItemVH.name, position, mDataSet.get(position).isChecked);
    }

    private void setCheckItemText(TextView name, int position, boolean isChecked) {
        name.setText(mDataSet.get(position).name);

        if (isChecked) {
            name.setTextColor(mContext.getResources().getColor(R.color.check_list_checked_text_color));
            Paint paint = name.getPaint();
            paint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            paint.setAntiAlias(true);
        } else {
            name.setTextColor(mContext.getResources().getColor(R.color.check_list_unchecked_text_color));
            Paint paint = name.getPaint();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setAntiAlias(true);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
