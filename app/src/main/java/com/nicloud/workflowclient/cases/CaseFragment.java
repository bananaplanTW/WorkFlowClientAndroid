package com.nicloud.workflowclient.cases;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.utility.MainTabContentFactory;

/**
 * Created by logicmelody on 2016/1/25.
 */
public class CaseFragment extends Fragment implements TabHost.OnTabChangeListener {

    private static final class TabTag {
        public static final String DISCUSSION = "tag_file_discussion";
        public static final String FILE = "tag_tab_file";
    }

    private static final class TabPosition {
        public static final int DISCUSSION = 0;
        public static final int FILE = 1;
    }

    private Context mContext;

    private TabHost mCaseTabHost;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_case, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        findViews();
        setupTabs();
    }

    private void findViews() {
        mCaseTabHost = (TabHost) getView().findViewById(R.id.case_tab_host);
    }

    private void setupTabs() {
        mCaseTabHost.setup();

        addTab(TabTag.DISCUSSION);
        addTab(TabTag.FILE);

        mCaseTabHost.setOnTabChangedListener(this);
    }

    private void addTab(String tag) {
        mCaseTabHost.addTab(mCaseTabHost.newTabSpec(tag).setIndicator(getTabView(tag))
                .setContent(new MainTabContentFactory(mContext)));
    }

    private View getTabView(String tag) {
        View tabView = LayoutInflater.from(mContext).inflate(R.layout.tab, null);
        TextView tabText = (TextView) tabView.findViewById(R.id.tab_text);

        String text = "";
        if(TabTag.DISCUSSION.equals(tag)) {
            text = getString(R.string.case_tab_discussion);
        } else if(TabTag.FILE.equals(tag)) {
            text = getString(R.string.case_tab_file);
        }

        tabText.setText(text);

        return tabView;
    }

    @Override
    public void onTabChanged(String tabId) {

    }
}
