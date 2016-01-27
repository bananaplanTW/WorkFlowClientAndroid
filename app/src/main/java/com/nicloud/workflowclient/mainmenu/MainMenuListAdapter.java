package com.nicloud.workflowclient.mainmenu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        public static final int CASE_TITLE = 1;
        public static final int EMPTY = 2;
        public static final int CASE = 3;
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

    private class CaseTitleViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView createCaseButton;


        public CaseTitleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.menu_case_title);
            createCaseButton = (ImageView) itemView.findViewById(R.id.create_case_button);

            createCaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class CaseViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView caseName;


        public CaseViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            caseName = (TextView) itemView.findViewById(R.id.main_menu_case);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickMainMenuItem(mDataSet.get(getAdapterPosition()));
                }
            });
        }
    }

    private void onClickMainMenuItem(MainMenuItem clickedItem) {
        if (clickedItem.mIsSelected) return;

        clickedItem.mIsSelected = true;
        if (mCurrentSelectedItem != null) {
            mCurrentSelectedItem.mIsSelected = false;
        }
        mCurrentSelectedItem = clickedItem;

        notifyDataSetChanged();

        mOnClickMainMenuItemListener.onClickMainMenuItem(clickedItem);

        Log.d(TAG, "Current selected main menu item: " + clickedItem.mName);
    }

    public void clearSelectedMainMenuItem() {
        if (mCurrentSelectedItem == null) return;

        mCurrentSelectedItem.mIsSelected = false;
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

            case ItemViewType.EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.menu_empty, parent, false));

            case ItemViewType.CASE_TITLE:
                return new CaseTitleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.menu_case_title, parent, false));

            case ItemViewType.CASE:
                return new CaseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.main_menu_case, parent, false));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MainMenuItem mainMenuItem = mDataSet.get(position);

        switch (mainMenuItem.mViewType) {
            case ItemViewType.ITEM:
                onBindItem(holder, mainMenuItem);
                break;

            case ItemViewType.CASE_TITLE:
                onBindCaseTitle(holder, mainMenuItem);
                break;

            case ItemViewType.CASE:
                onBindCase(holder, mainMenuItem);
                break;
        }
    }

    private void onBindItem(RecyclerView.ViewHolder holder, MainMenuItem mainMenuItem) {
        ItemViewHolder itemVH = (ItemViewHolder) holder;

        itemVH.name.setText(mainMenuItem.mName);

        if (MainMenuFragment.MainMenuItemType.MY_TASKS == mainMenuItem.mType) {
            itemVH.name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_my_tasks, 0, 0, 0);
            itemVH.name.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(R.dimen.main_menu_item_drawable_padding));
        }

        if (mainMenuItem.mIsSelected) {
            mCurrentSelectedItem = mainMenuItem;
            itemVH.view.setBackgroundColor(mContext.getResources().getColor(R.color.drawer_menu_item_selected_background_color));
        } else {
            itemVH.view.setBackgroundColor(mContext.getResources().getColor(R.color.drawer_menu_background_color));
        }
    }

    private void onBindCaseTitle(RecyclerView.ViewHolder holder, MainMenuItem mainMenuItem) {
        CaseTitleViewHolder itemVH = (CaseTitleViewHolder) holder;

        itemVH.title.setText(mainMenuItem.mName);

        if (MainMenuFragment.MainMenuItemType.CASE == mainMenuItem.mType) {
            itemVH.title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_case, 0, 0, 0);
            itemVH.title.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(R.dimen.main_menu_item_drawable_padding));
        }
    }

    private void onBindCase(RecyclerView.ViewHolder holder, MainMenuItem mainMenuItem) {
        CaseViewHolder itemVH = (CaseViewHolder) holder;

        itemVH.caseName.setText(mainMenuItem.mName);
        itemVH.caseName.setTextColor(mainMenuItem.mCase.isCompleted ?
                mContext.getResources().getColor(R.color.main_menu_case_completed_text_color) :
                mContext.getResources().getColor(R.color.main_menu_case_uncompleted_text_color));

        if (mainMenuItem.mIsSelected) {
            mCurrentSelectedItem = mainMenuItem;
            itemVH.view.setBackgroundColor(mContext.getResources().getColor(R.color.drawer_menu_item_selected_background_color));
        } else {
            itemVH.view.setBackgroundColor(mContext.getResources().getColor(R.color.drawer_menu_background_color));
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).mViewType;
    }
}
