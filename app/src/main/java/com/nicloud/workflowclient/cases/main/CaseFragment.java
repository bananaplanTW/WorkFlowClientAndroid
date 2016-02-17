package com.nicloud.workflowclient.cases.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.cases.caseinfo.CaseInfoFragment;
import com.nicloud.workflowclient.cases.discussion.CaseDiscussionFragment;
import com.nicloud.workflowclient.cases.file.CaseFileFragment;
import com.nicloud.workflowclient.cases.tasklist.CaseTaskListFragment;
import com.nicloud.workflowclient.utility.MainTabContentFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by logicmelody on 2016/1/25.
 */
public class CaseFragment extends Fragment implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    public static final String EXTRA_CASE_ID = "extra_case_id";

    private static final class FragmentTag {
        public static final String INFO = "fragment_tag_info";
        public static final String TASK = "fragment_tag_task";
        public static final String DISCUSSION = "fragment_tag_discussion";
        public static final String FILE = "fragment_tag_file";
    }

    private static final class TabTag {
        public static final String INFO = "tag_info";
        public static final String TASK = "tag_task";
        public static final String DISCUSSION = "tag_file_discussion";
        public static final String FILE = "tag_tab_file";
    }

    private static final class FragmentPosition {
        public static final int SIZE = 4;

        public static final int INFO = 0;
        public static final int TASK = 1;
        public static final int DISCUSSION = 2;
        public static final int FILE = 3;
    }

    private Context mContext;

    private TabHost mCaseTabHost;

    private ViewPager mCaseViewPager;
    private CasePagerAdapter mCasePagerAdapter;
    private List<Fragment> mCaseFragmentList = new ArrayList<>();

    private String mCaseId;


    public void setCaseId(String caseId) {
        if (caseId.equals(mCaseId)) return;

        mCaseId = caseId;

        ((OnSetCaseId) mCaseFragmentList.get(FragmentPosition.INFO)).setCaseId(mCaseId);
        ((OnSetCaseId) mCaseFragmentList.get(FragmentPosition.TASK)).setCaseId(mCaseId);
        ((OnSetCaseId) mCaseFragmentList.get(FragmentPosition.DISCUSSION)).setCaseId(mCaseId);
        ((OnSetCaseId) mCaseFragmentList.get(FragmentPosition.FILE)).setCaseId(mCaseId);

        mCasePagerAdapter.notifyDataSetChanged();
    }

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
        mCaseId = getArguments().getString(EXTRA_CASE_ID);

        findViews();
        setupTabs();
        setupFragments();
        setupCaseViewPager();
    }

    private void findViews() {
        mCaseTabHost = (TabHost) getView().findViewById(R.id.case_tab_host);
        mCaseViewPager = (ViewPager) getView().findViewById(R.id.case_viewpager);
    }

    private void setupTabs() {
        mCaseTabHost.setup();

        addTab(TabTag.INFO);
        addTab(TabTag.TASK);
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

        if (TabTag.INFO.equals(tag)) {
            text = getString(R.string.case_tab_info);
        } else if (TabTag.TASK.equals(tag)) {
            text = getString(R.string.case_tab_task);
        } else if(TabTag.DISCUSSION.equals(tag)) {
            text = getString(R.string.case_tab_discussion);
        } else if(TabTag.FILE.equals(tag)) {
            text = getString(R.string.case_tab_file);
        }

        tabText.setText(text);

        return tabView;
    }

    private void setupFragments() {
        mCaseFragmentList.add(generateSubCaseFragment(CaseInfoFragment.class, FragmentTag.INFO));
        mCaseFragmentList.add(generateSubCaseFragment(CaseTaskListFragment.class, FragmentTag.TASK));
        mCaseFragmentList.add(generateSubCaseFragment(CaseDiscussionFragment.class, FragmentTag.DISCUSSION));
        mCaseFragmentList.add(generateSubCaseFragment(CaseFileFragment.class, FragmentTag.FILE));
    }

    private Fragment generateSubCaseFragment(Class<? extends Fragment> fragmentClass, String fragmentTag) {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(fragmentTag);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_CASE_ID, mCaseId);

        if (fragment == null) {
            try {
                fragment = fragmentClass.newInstance();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        fragment.setArguments(bundle);

        return fragment;
    }

    private void setupCaseViewPager() {
        mCasePagerAdapter = new CasePagerAdapter(getChildFragmentManager(), mContext, mCaseFragmentList);

        mCaseViewPager.setAdapter(mCasePagerAdapter);
        mCaseViewPager.setOffscreenPageLimit(3);
        mCaseViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // notifying nested fragments (support library bug fix)
        final FragmentManager childFragmentManager = getChildFragmentManager();

        if (childFragmentManager != null) {
            final List<Fragment> nestedFragments = childFragmentManager.getFragments();

            if (nestedFragments == null || nestedFragments.size() == 0) return;

            for (Fragment childFragment : nestedFragments) {
                if (childFragment != null && !childFragment.isDetached() && !childFragment.isRemoving()) {
                    childFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        mCaseViewPager.setCurrentItem(mCaseTabHost.getCurrentTab());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCaseTabHost.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
