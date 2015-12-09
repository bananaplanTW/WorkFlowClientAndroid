package com.nicloud.workflowclient.detailedtask.checklist;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.data.CheckItem;

import java.util.List;

/**
 * Created by logicmelody on 2015/12/9.
 */
public class CheckListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<CheckItem> mDataSet;


    private class CheckItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public CheckBox checkBox;


        public CheckItemViewHolder(View itemView) {
            super(itemView);
            findViews();
            setCheckBox();
        }

        private void findViews() {
            name = (TextView) itemView.findViewById(R.id.check_item_name);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_item_check_box);
        }

        private void setCheckBox() {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                }
            });
        }
    }

    public CheckListAdapter(Context context, List<CheckItem> dataSet) {
        mContext = context;
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
