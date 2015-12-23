package com.nicloud.workflowclient.mainmenu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclient.R;

import java.util.List;

/**
 * Created by logicmelody on 2015/12/23.
 */
public class MainMenuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class ItemViewType {
        public static final int ITEM = 0;
        public static final int TITLE = 1;
        public static final int EMPTY = 2;
    }

    private Context mContext;

    private List<MainMenuItem> mDataSet;


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView name;


        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView) itemView.findViewById(R.id.main_menu_item_name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }


    public MainMenuListAdapter(Context context, List<MainMenuItem> dataSet) {
        mContext = context;
        mDataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemViewType.ITEM:
                return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.main_menu_item, parent, false));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = mDataSet.get(position).viewType;
        MainMenuItem mainMenuItem = mDataSet.get(position);

        switch (viewType) {
            case ItemViewType.ITEM:
                onBindMainMenuItem(holder, mainMenuItem);

                break;
        }
    }

    private void onBindMainMenuItem(RecyclerView.ViewHolder holder, MainMenuItem mainMenuItem) {
        ItemViewHolder itemVH = (ItemViewHolder) holder;

        itemVH.name.setText(mainMenuItem.name);
        itemVH.view.setSelected(mainMenuItem.isSelected);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).viewType;
    }
}
