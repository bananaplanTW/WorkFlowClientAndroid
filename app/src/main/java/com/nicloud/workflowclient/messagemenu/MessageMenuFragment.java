package com.nicloud.workflowclient.messagemenu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicloud.workflowclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/12/22.
 */
public class MessageMenuFragment extends Fragment {

    public interface OnClickMessageMenuItemListener {
        void onClickMessageMenuItem(String itemId, String title);
    }

    private Context mContext;

    private RecyclerView mMessageMenuList;
    private LinearLayoutManager mMessageMenuListLayoutManager;
    private MessageMenuListAdapter mMessageMenuListAdapter;
    private List<MessageMenuItem> mDataSet = new ArrayList<>();

    private OnClickMessageMenuItemListener mOnClickMessageMenuItemListener;


    public void clearSelectedMessageMenuItem() {
        mMessageMenuListAdapter.clearSelectedMessageMenuItem();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mOnClickMessageMenuItemListener = (OnClickMessageMenuItemListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_menu, container, false);
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        findViews();
        setupMessageMenuList();
        loadWorkers();
    }

    private void loadWorkers() {

    }

    private void findViews() {
        mMessageMenuList = (RecyclerView) getView().findViewById(R.id.message_menu_list);
    }

    private void setupMessageMenuList() {
        setMessageMenuListData();

        mMessageMenuListLayoutManager = new LinearLayoutManager(mContext);
        mMessageMenuListAdapter = new MessageMenuListAdapter(mContext, mDataSet, mOnClickMessageMenuItemListener);

        mMessageMenuList.setLayoutManager(mMessageMenuListLayoutManager);
        mMessageMenuList.setAdapter(mMessageMenuListAdapter);
    }

    private void setMessageMenuListData() {
        mDataSet.add(new MessageMenuItem("", mContext.getString(R.string.message_menu_messages),
                MessageMenuListAdapter.ItemViewType.TITLE, false));

        mDataSet.add(new MessageMenuItem("wedw", "Paul", MessageMenuListAdapter.ItemViewType.WORKER, false));
        mDataSet.add(new MessageMenuItem("2d2d", "Daz", MessageMenuListAdapter.ItemViewType.WORKER, false));
        mDataSet.add(new MessageMenuItem("2d2d", "Nash", MessageMenuListAdapter.ItemViewType.WORKER, false));
    }
}
