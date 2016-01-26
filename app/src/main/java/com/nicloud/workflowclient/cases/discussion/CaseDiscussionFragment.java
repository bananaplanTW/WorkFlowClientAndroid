package com.nicloud.workflowclient.cases.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.cases.main.CaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/25.
 */
public class CaseDiscussionFragment extends Fragment {

    private Context mContext;

    private RecyclerView mDiscussionList;
    private LinearLayoutManager mDiscussionListLayoutManager;
    private DiscussionListAdapter mDiscussionListAdapter;

    private List<Discussion> mDiscussionData = new ArrayList<>();

    public String mCaseId;


    public void setCaseId(String caseId) {
        mCaseId = caseId;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_case_discussion, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        mCaseId = getArguments().getString(CaseFragment.EXTRA_CASE_ID);

        findViews();
        setDiscussionData();
        setupDiscussionList();
    }

    private void findViews() {
        mDiscussionList = (RecyclerView) getView().findViewById(R.id.discussion_list);
    }

    private void setDiscussionData() {
        mDiscussionData.add(new Discussion("123123", "Good day", 1233332131));
        mDiscussionData.add(new Discussion("123123", "Good day", 1233332131));

        for (int i = 0 ; i < 100 ; i ++) {
            mDiscussionData.add(new Discussion("123123", "Good day", 1233332131));
        }
    }

    private void setupDiscussionList() {
        mDiscussionListLayoutManager = new LinearLayoutManager(mContext);
        mDiscussionListAdapter = new DiscussionListAdapter(mContext, mDiscussionData);

        mDiscussionList.setLayoutManager(mDiscussionListLayoutManager);
        mDiscussionList.setAdapter(mDiscussionListAdapter);
    }
}
