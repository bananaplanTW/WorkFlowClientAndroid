package com.nicloud.workflowclient.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.cases.CaseActivity;
import com.nicloud.workflowclient.data.data.data.Worker;
import com.nicloud.workflowclient.dialog.DisplayDialogFragment;
import com.nicloud.workflowclient.mainmenu.MainMenuFragment;
import com.nicloud.workflowclient.mainmenu.MainMenuItem;
import com.nicloud.workflowclient.messagechat.MessageChatActivity;
import com.nicloud.workflowclient.messagemenu.MessageMenuFragment;
import com.nicloud.workflowclient.tasklist.TaskListFragment;
import com.nicloud.workflowclient.utility.Utilities;


/**
 * Main component to control the UI
 *
 * @author Danny Lin
 * @since 2015.05.28
 *
 */
public class UIController implements View.OnClickListener {

    private static final String TAG = "UIController";

    private class FragmentTag {
        public static final String TASK_LIST = "tag_fragment_task_list";
        public static final String CASE = "tag_fragment_case";
        public static final String MAIN_MENU = "tag_fragment_main_menu";
        public static final String MESSAGE_MENU = "tag_fragment_message_menu";
        public static final String MESSAGE_CHAT = "tag_fragment_message_chat";
    }

    private AppCompatActivity mMainActivity;
    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mLeftDrawerView;
    private View mRightDrawerView;

    private MainMenuFragment mMainMenuFragment;
    private MessageMenuFragment mMessageMenuFragment;

    private FragmentManager mFragmentManager;
    private Fragment mCurrentContentFragment;

    private MainMenuItem mClickedMainMenuItem;
    private Worker mClickedWorker;


    public UIController(AppCompatActivity activity) {
        mMainActivity = activity;
    }

    public void onRefreshInTaskList() {
    }

    public void onCreate(Bundle savedInstanceState) {
        initialize();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        mMainActivity.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;

        } else {
            switch(item.getItemId()) {
                case R.id.action_message:
                    openRightDrawer();
                    return true;

                case R.id.action_settings:
                    return true;

                default:
                    return false;
            }
        }
    }

    public void onPostCreate(Bundle savedInstanceState) {
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void onClickMainMenuItem(MainMenuItem item) {
        mClickedMainMenuItem = item;
        closeLeftDrawer();
    }

    public void onClickMessageMenuWorker(Worker worker) {
        closeRightDrawer();
        mClickedWorker = worker;
    }

    public boolean isLeftDrawerOpened() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public boolean isRightDrawerOpened() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.END);
    }

    public void openLeftDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void openRightDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.END);
    }

    public void closeLeftDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void closeRightDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.END);
    }

    private void initialize() {
        mFragmentManager = mMainActivity.getSupportFragmentManager();
        findViews();
        setupViews();
        setupActionbar();
        setupDrawer();
        setupFragments();
    }

    private void findViews() {
        mToolbar = (Toolbar) mMainActivity.findViewById(R.id.tool_bar);
        mDrawerLayout = (DrawerLayout) mMainActivity.findViewById(R.id.drawer_layout);
        mLeftDrawerView = mMainActivity.findViewById(R.id.drawer_menu_left_side_container);
        mRightDrawerView = mMainActivity.findViewById(R.id.drawer_menu_right_side_container);
    }

    private void setupViews() {
    }

    private void setupActionbar() {
        mMainActivity.setSupportActionBar(mToolbar);
        mActionBar = mMainActivity.getSupportActionBar();
        mActionBar.setTitle(mMainActivity.getString(R.string.main_menu_my_tasks));

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowTitleEnabled(true);
        }
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(mMainActivity, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                onOpenDrawer(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                onCloseDrawer(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void onOpenDrawer(View drawerView) {
        if (mLeftDrawerView == drawerView) {

        } else if (mRightDrawerView == drawerView) {
        }
    }

    private void onCloseDrawer(View drawerView) {
        if (mLeftDrawerView == drawerView) {
            onCloseMainMenuAction();

        } else if (mRightDrawerView == drawerView) {
            onCloseMessageMenuAction();
        }
    }

    private void onCloseMainMenuAction() {
        if (mClickedMainMenuItem == null) return;

        mMessageMenuFragment.clearSelectedMessageMenuItem();

        switch (mClickedMainMenuItem.mType) {
            case MainMenuFragment.MainMenuItemType.MY_TASKS:
                if (mCurrentContentFragment instanceof TaskListFragment) break;
                replaceTo(TaskListFragment.class, FragmentTag.TASK_LIST);

                break;

            case MainMenuFragment.MainMenuItemType.CASE:
                Intent intent = new Intent(mMainActivity, CaseActivity.class);
                intent.putExtra(CaseActivity.EXTRA_CASE_ID, mClickedMainMenuItem.mType);
                intent.putExtra(CaseActivity.EXTRA_CASE_NAME, mClickedMainMenuItem.mName);

                mMainActivity.startActivity(intent);

                break;
        }

        mClickedMainMenuItem = null;
    }

    private void onCloseMessageMenuAction() {
        if (mClickedWorker == null) return;

        Intent intent = new Intent(mMainActivity, MessageChatActivity.class);
        intent.putExtra(MessageChatActivity.EXTRA_WORKER_ID, mClickedWorker.id);
        intent.putExtra(MessageChatActivity.EXTRA_WORKER_NAME, mClickedWorker.name);

        mMainActivity.startActivity(intent);

        mClickedWorker = null;
    }

    private void setupFragments() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        // MainMenuFragment in the left side of the drawer menu
        mMainMenuFragment = (MainMenuFragment) mFragmentManager.findFragmentByTag(FragmentTag.MAIN_MENU);
        if (mMainMenuFragment == null) {
            mMainMenuFragment = new MainMenuFragment();
            fragmentTransaction.add(R.id.drawer_menu_left_side_container, mMainMenuFragment, FragmentTag.MAIN_MENU);
        }

        // MessageMenuFragment in the right side of the drawer menu
        mMessageMenuFragment = (MessageMenuFragment) mFragmentManager.findFragmentByTag(FragmentTag.MESSAGE_MENU);
        if (mMessageMenuFragment == null) {
            mMessageMenuFragment = new MessageMenuFragment();
            fragmentTransaction.add(R.id.drawer_menu_right_side_container, mMessageMenuFragment, FragmentTag.MESSAGE_MENU);
        }

        // Default fragment(TaskListFragment) when we launch app
        TaskListFragment taskListFragment = (TaskListFragment) mFragmentManager.findFragmentByTag(FragmentTag.TASK_LIST);
        if (taskListFragment == null) {
            taskListFragment = new TaskListFragment();
            fragmentTransaction.add(R.id.content_container, taskListFragment, FragmentTag.TASK_LIST);
        }

        mCurrentContentFragment = taskListFragment;
        fragmentTransaction.commit();
    }

    private void replaceTo(Class<?> fragmentClass, String fragmentTag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment fragment = mFragmentManager.findFragmentByTag(fragmentTag);
        if (fragment == null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                fragmentTransaction.replace(R.id.content_container, fragment, fragmentTag);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        mCurrentContentFragment = fragment;
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Utilities.showDialog(mFragmentManager, DisplayDialogFragment.DialogType.CHECK_IN_OUT, null);

                break;
        }
    }
}