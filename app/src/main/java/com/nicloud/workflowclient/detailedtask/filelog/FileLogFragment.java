package com.nicloud.workflowclient.detailedtask.filelog;

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
import com.nicloud.workflowclient.data.data.activity.BaseData;
import com.nicloud.workflowclient.utility.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/12.
 */
public class FileLogFragment extends Fragment {

    public static final String EXTRA_FILE_LOG = "extra_file_log";

    private Context mContext;

    private RecyclerView mFileLogList;
    private LinearLayoutManager mFileLogListLayoutManager;
    private FileLogListAdapter mFileLogListAdapter;
    private List<BaseData> mFileLogData = new ArrayList<>();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_log, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<BaseData> dataSet = getArguments().getParcelableArrayList(EXTRA_FILE_LOG);
        mFileLogData.clear();
        mFileLogData.addAll(dataSet);
        initialize();
    }

    private void initialize() {
        findViews();
        setupFileLogList();
    }

    private void findViews() {
        mFileLogList = (RecyclerView) getView().findViewById(R.id.file_log_list);
    }

    private void setupFileLogList() {
        mFileLogListLayoutManager = new LinearLayoutManager(mContext);
        mFileLogListAdapter = new FileLogListAdapter(mContext, mFileLogData);

        mFileLogList.addItemDecoration(new DividerItemDecoration(
                getResources().getDrawable(R.drawable.list_divider), false, true, false, 0));
        mFileLogList.setLayoutManager(mFileLogListLayoutManager);
        mFileLogList.setAdapter(mFileLogListAdapter);
    }
}
