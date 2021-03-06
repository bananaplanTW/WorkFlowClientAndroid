package com.nicloud.workflowclient.main;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.backgroundtask.service.TaskService;
import com.nicloud.workflowclient.cases.main.CaseFragment;
import com.nicloud.workflowclient.backgroundtask.asyntask.worker.LoadingWorkerAvatar;
import com.nicloud.workflowclient.backgroundtask.asyntask.worker.LoadingWorkers;
import com.nicloud.workflowclient.data.data.Worker;
import com.nicloud.workflowclient.dialog.activity.CreateTaskActivity;
import com.nicloud.workflowclient.dialog.fragment.DisplayDialogFragment;
import com.nicloud.workflowclient.mainmenu.MainMenuFragment;
import com.nicloud.workflowclient.mainmenu.MainMenuItem;
import com.nicloud.workflowclient.mainmenu.MainMenuListAdapter;
import com.nicloud.workflowclient.messagechat.MessageChatActivity;
import com.nicloud.workflowclient.messagemenu.MessageMenuFragment;
import com.nicloud.workflowclient.provider.debug.AndroidDatabaseManager;
import com.nicloud.workflowclient.backgroundtask.receiver.GeneralCompletedReceiver;
import com.nicloud.workflowclient.backgroundtask.service.GeneralService;
import com.nicloud.workflowclient.tasklist.my.MyTaskListFragment;
import com.nicloud.workflowclient.utility.utils.DbUtils;
import com.nicloud.workflowclient.utility.utils.LoadingDataUtils;
import com.nicloud.workflowclient.utility.utils.Utils;


/**
 * Main component to control the UI
 *
 * @author Danny Lin
 * @since 2015.05.28
 *
 */
public class UIController implements View.OnClickListener, GeneralCompletedReceiver.OnGeneralCompletedListener,
        LoadingWorkers.OnFinishLoadingWorkersListener {

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
    //private View mRightDrawerView;

    private MainMenuFragment mMainMenuFragment;
    private MessageMenuFragment mMessageMenuFragment;

    private FragmentManager mFragmentManager;
    private Fragment mCurrentContentFragment;

    private MainMenuItem mClickedMainMenuItem;
    private Worker mClickedWorker;

    private GeneralCompletedReceiver mGeneralCompletedReceiver;


    public UIController(AppCompatActivity activity) {
        mMainActivity = activity;
    }

    public void onCompleteTaskOk(String taskId) {
        Intent intent = new Intent(mMainActivity, GeneralService.class);
        intent.setAction(GeneralService.Action.COMPLETE_TASK);
        intent.putExtra(GeneralService.ExtraKey.TASK_ID, taskId);
        intent.putExtra(GeneralService.ExtraKey.TASK_NAME, DbUtils.getTaskNameById(mMainActivity, taskId));

        mMainActivity.startService(intent);
        Utils.dismissDialog(mFragmentManager);
    }

    public void onCompleteTaskCancel() {
        Utils.dismissDialog(mFragmentManager);
    }

    public void onCreate(Bundle savedInstanceState) {
        initialize();
    }

    public void onStart() {
        IntentFilter intentFilter = new IntentFilter(GeneralService.Action.COMPLETE_TASK);
        LocalBroadcastManager.getInstance(mMainActivity).registerReceiver(mGeneralCompletedReceiver, intentFilter);
    }

    public void onStop() {
        LocalBroadcastManager.getInstance(mMainActivity).unregisterReceiver(mGeneralCompletedReceiver);
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

                case R.id.action_database_debug:
                    mMainActivity.startActivity(new Intent(mMainActivity, AndroidDatabaseManager.class));
                    return true;

                case R.id.action_settings:
                    return true;

                case R.id.action_create_task:
                    mMainActivity.startActivity(CreateTaskActivity.generateCreateTaskIntent(mMainActivity));
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
        Utils.hideSoftKeyboard(mMainActivity);
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
        mGeneralCompletedReceiver = new GeneralCompletedReceiver(this);
        mClickedMainMenuItem = new MainMenuItem(MainMenuFragment.MainMenuItemType.MY_TASKS,
                                                mMainActivity.getString(R.string.main_menu_my_tasks),
                                                null, MainMenuListAdapter.ItemViewType.ITEM, true);
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
        //mRightDrawerView = mMainActivity.findViewById(R.id.drawer_menu_right_side_container);
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

        } //else if (mRightDrawerView == drawerView) {
        //}
    }

    private void onCloseDrawer(View drawerView) {
        if (mLeftDrawerView == drawerView) {
            onCloseMainMenuAction();

        } //else if (mRightDrawerView == drawerView) {
            onCloseMessageMenuAction();
        //}
    }

    private void onCloseMainMenuAction() {
        mActionBar.setTitle(mClickedMainMenuItem.mName);

        switch (mClickedMainMenuItem.mType) {
            case MainMenuFragment.MainMenuItemType.MY_TASKS:
                if (mCurrentContentFragment instanceof MyTaskListFragment) break;
                replaceTo(MyTaskListFragment.class, FragmentTag.TASK_LIST);

                break;

            case MainMenuFragment.MainMenuItemType.CASE:
                if (mCurrentContentFragment instanceof CaseFragment) {
                    ((CaseFragment) mCurrentContentFragment).setCaseId(mClickedMainMenuItem.mCase.id);

                    break;
                }
                replaceTo(CaseFragment.class, FragmentTag.CASE);

                break;
        }
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
//        mMessageMenuFragment = (MessageMenuFragment) mFragmentManager.findFragmentByTag(FragmentTag.MESSAGE_MENU);
//        if (mMessageMenuFragment == null) {
//            mMessageMenuFragment = new MessageMenuFragment();
//            fragmentTransaction.add(R.id.drawer_menu_right_side_container, mMessageMenuFragment, FragmentTag.MESSAGE_MENU);
//        }

        // Default fragment(TaskListFragment) when we launch app
        MyTaskListFragment myTaskListFragment = (MyTaskListFragment) mFragmentManager.findFragmentByTag(FragmentTag.TASK_LIST);
        if (myTaskListFragment == null) {
            myTaskListFragment = new MyTaskListFragment();
            fragmentTransaction.add(R.id.content_container, myTaskListFragment, FragmentTag.TASK_LIST);
        }

        mCurrentContentFragment = myTaskListFragment;
        fragmentTransaction.commit();
    }

    private void replaceTo(Class<?> fragmentClass, String fragmentTag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment fragment = mFragmentManager.findFragmentByTag(fragmentTag);
        if (fragment == null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                putFragmentArguments(fragment);

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

    private void putFragmentArguments(Fragment fragment) {
        if (fragment instanceof CaseFragment) {
            Bundle bundle = new Bundle();
            bundle.putString(CaseFragment.EXTRA_CASE_ID, mClickedMainMenuItem.mCase.id);

            fragment.setArguments(bundle);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Utils.showDialog(mFragmentManager, DisplayDialogFragment.DialogType.CHECK_IN_OUT, null);

                break;
        }
    }

    @Override
    public void onGeneralCompleted(Intent intent) {
        String action = intent.getAction();
        String taskName = intent.getStringExtra(GeneralService.ExtraKey.TASK_NAME);
        boolean isActionSuccessful = intent.getBooleanExtra(GeneralService.ExtraKey.ACTION_SUCCESSFUL, false);

        if (action.equals(GeneralService.Action.COMPLETE_TASK)) {
            if (isActionSuccessful) {
                // TODO: Delete task in db
                mMainActivity.startService(TaskService.generateLoadMyTasksIntent(mMainActivity, false));
                Utils.showCompleteTaskToast(mMainActivity, taskName);

            } else {
                Utils.showInternetConnectionWeakToast(mMainActivity);
            }
        }
    }

    @Override
    public void onFinishLoadingWorkers() {
        for (Worker worker : WorkingData.getInstance(mMainActivity).getWorkers()) {
            if (!TextUtils.isEmpty(worker.avatarUrl)) {
                Uri.Builder avatarBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
                avatarBuilder.path(worker.avatarUrl);
                Uri avatarUri = avatarBuilder.build();

                new LoadingWorkerAvatar(mMainActivity, avatarUri, null,
                        worker, R.drawable.selector_message_menu_worker_avatar).execute();
            }
        }
    }

    @Override
    public void onFailLoadingWorkers(boolean isFailCausedByInternet) {

    }
}
