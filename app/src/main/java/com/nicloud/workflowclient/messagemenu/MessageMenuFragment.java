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
import com.nicloud.workflowclient.data.connectserver.worker.LoadingWorkers;
import com.nicloud.workflowclient.data.data.data.Worker;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.utility.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2015/12/22.
 */
public class MessageMenuFragment extends Fragment implements LoadingWorkers.OnFinishLoadingWorkersListener {

    public interface OnClickMessageMenuWorkerListener {
        void onClickMessageMenuWorker(Worker worker);
    }

    private Context mContext;

    private RecyclerView mMessageMenuList;
    private LinearLayoutManager mMessageMenuListLayoutManager;
    private MessageMenuListAdapter mMessageMenuListAdapter;
    private List<MessageMenuItem> mDataSet = new ArrayList<>();

    private OnClickMessageMenuWorkerListener mOnClickMessageMenuWorkerListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mOnClickMessageMenuWorkerListener = (OnClickMessageMenuWorkerListener) context;
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
        new LoadingWorkers(mContext, this).execute();
    }

    private void findViews() {
        mMessageMenuList = (RecyclerView) getView().findViewById(R.id.message_menu_list);
    }

    private void setupMessageMenuList() {
        mMessageMenuListLayoutManager = new LinearLayoutManager(mContext);
        mMessageMenuListAdapter = new MessageMenuListAdapter(mContext, mDataSet, mOnClickMessageMenuWorkerListener);

        mMessageMenuList.setLayoutManager(mMessageMenuListLayoutManager);
        mMessageMenuList.setAdapter(mMessageMenuListAdapter);
    }

    @Override
    public void onFinishLoadingWorkers() {
        setMessageMenuListData();
    }

    private void setMessageMenuListData() {
        mDataSet.add(new MessageMenuItem(mContext.getString(R.string.message_menu_messages), null,
                MessageMenuListAdapter.ItemViewType.TITLE, false));

        for (Worker worker : WorkingData.getInstance(mContext).getWorkers()) {
            if (worker.id.equals(WorkingData.getInstance(mContext).getLoginWorker().id)) continue;

            mDataSet.add(new MessageMenuItem(worker.name, worker,
                    MessageMenuListAdapter.ItemViewType.WORKER, false));
        }

        mMessageMenuListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailLoadingWorkers(boolean isFailCausedByInternet) {
        Utils.showInternetConnectionWeakToast(mContext);
    }
}
