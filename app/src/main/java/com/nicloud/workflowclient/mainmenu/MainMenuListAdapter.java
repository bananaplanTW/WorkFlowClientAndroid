package com.nicloud.workflowclient.mainmenu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.mainmenu.MainMenuFragment.OnClickMainMenuItemListener;

import java.util.List;

/**
 * Created by logicmelody on 2015/12/23.
 */
public class MainMenuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MainMenuListAdapter";

    public static class ItemViewType {
        public static final int ITEM = 0;
        public static final int TITLE = 1;
        public static final int EMPTY = 2;
    }

    private Context mContext;

    private OnClickMainMenuItemListener mOnClickMainMenuItemListener;

    private List<MainMenuItem> mDataSet;

    private MainMenuItem mCurrentSelectedItem;


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
                    onClickMainMenuItem(mDataSet.get(getAdapterPosition()));
                }
            });
        }
    }

    private void onClickMainMenuItem(MainMenuItem clickedItem) {
        if (clickedItem.isSelected) return;

        clickedItem.isSelected = true;
        if (mCurrentSelectedItem != null) {
            mCurrentSelectedItem.isSelected = false;
        }
        mCurrentSelectedItem = clickedItem;

        notifyDataSetChanged();

        mOnClickMainMenuItemListener.onClickMainMenuItem(mCurrentSelectedItem.id);

        Log.d(TAG, "Current selected main menu item: " + mCurrentSelectedItem.name);
    }

    public void clearSelectedMainMenuItem() {
        if (mCurrentSelectedItem == null) return;

        mCurrentSelectedItem.isSelected = false;
        mCurrentSelectedItem = null;

        notifyDataSetChanged();
    }

    public MainMenuListAdapter(Context context, List<MainMenuItem> dataSet, OnClickMainMenuItemListener listener) {
        mContext = context;
        mDataSet = dataSet;
        mOnClickMainMenuItemListener = listener;
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
        MainMenuItem mainMenuItem = mDataSet.get(position);

        switch (mainMenuItem.viewType) {
            case ItemViewType.ITEM:
                onBindMainMenuItem(holder, mainMenuItem);

                break;
        }
    }

    private void onBindMainMenuItem(RecyclerView.ViewHolder holder, MainMenuItem mainMenuItem) {
        ItemViewHolder itemVH = (ItemViewHolder) holder;

        itemVH.name.setText(mainMenuItem.name);
        itemVH.view.setSelected(mainMenuItem.isSelected);

        if (mainMenuItem.isSelected) {
            mCurrentSelectedItem = mainMenuItem;
        }
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
