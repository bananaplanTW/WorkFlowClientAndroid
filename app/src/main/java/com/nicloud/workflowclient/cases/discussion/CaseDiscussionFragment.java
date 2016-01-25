package com.nicloud.workflowclient.cases.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.cases.main.CaseFragment;

/**
 * Created by logicmelody on 2016/1/25.
 */
public class CaseDiscussionFragment extends Fragment {

    private Context mContext;

    private TextView mCaseNameText;

    private String mCaseName;


    public void setCaseName(String caseName) {
        mCaseNameText.setText(caseName);
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
        mCaseName = getArguments().getString(CaseFragment.EXTRA_CASE_NAME);
        findViews();
        setupViews();
    }

    private void findViews() {
        mCaseNameText = (TextView) getView().findViewById(R.id.case_name);
    }

    private void setupViews() {
        mCaseNameText.setText(mCaseName);
    }
}
