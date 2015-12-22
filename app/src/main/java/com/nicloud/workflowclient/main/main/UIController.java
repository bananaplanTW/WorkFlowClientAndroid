package com.nicloud.workflowclient.main.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.connectserver.LoadingDataUtils;
import com.nicloud.workflowclient.data.connectserver.worker.LoadingLoginWorkerCommand;
import com.nicloud.workflowclient.data.connectserver.worker.LoadingWorkerAvatarCommand;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.dialog.DisplayDialogFragment;
import com.nicloud.workflowclient.mainmenu.MainMenuFragment;
import com.nicloud.workflowclient.login.LoginActivity;
import com.nicloud.workflowclient.messagemenu.MessageMenuFragment;
import com.nicloud.workflowclient.tasklist.TaskListFragment;
import com.nicloud.workflowclient.utility.Utilities;
import com.parse.ParsePush;


/**
 * Main component to control the UI
 *
 * @author Danny Lin
 * @since 2015.05.28
 *
 */
public class UIController implements View.OnClickListener, LoadingLoginWorkerCommand.OnLoadingLoginWorker {

    private static final String TAG = "UIController";

    private class FragmentTag {
        public static final String TASK_LIST = "tag_fragment_task_list";
        public static final String MAIN_MENU = "tag_fragment_main_menu";
        public static final String MESSAGE_MENU = "tag_fragment_message_menu";
    }

    private AppCompatActivity mMainActivity;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private View mActionBarWorkerContainer;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mLeftDrawerView;
    private View mRightDrawerView;

    private MainMenuFragment mMainMenuFragment;
    private MessageMenuFragment mMessageMenuFragment;

    private ImageView mActionBarWorkerAvatar;
    private TextView mActionBarWorkerName;
    private TextView mActionBarWorkerFactoryName;

    private FragmentManager mFragmentManager;


    public UIController(AppCompatActivity activity) {
        mMainActivity = activity;
    }

    public void onRefreshInTaskList() {
        loadingLoginWorker();
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
                case R.id.action_logout:
                    ParsePush.unsubscribeInBackground("user_" + WorkingData.getUserId());
                    //ParseUtils.removeLoginWorkerToParse();

                    WorkingData.getInstance(mMainActivity).resetTasks();
                    WorkingData.resetAccount();

                    SharedPreferences sharedPreferences = mMainActivity.getSharedPreferences(WorkingData.SHARED_PREFERENCE_KEY, 0);
                    sharedPreferences.edit().remove(WorkingData.USER_ID).remove(WorkingData.AUTH_TOKEN).commit();

                    mMainActivity.startActivity(new Intent(mMainActivity, LoginActivity.class));
                    mMainActivity.finish();

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
        loadDataInFirstLaunch();
    }

    private void findViews() {
        mToolbar = (Toolbar) mMainActivity.findViewById(R.id.tool_bar);
        mActionBarWorkerContainer = mMainActivity.findViewById(R.id.action_bar_worker_container);
        mDrawerLayout = (DrawerLayout) mMainActivity.findViewById(R.id.drawer_layout);
        mLeftDrawerView = mMainActivity.findViewById(R.id.drawer_menu_left_side_container);
        mRightDrawerView = mMainActivity.findViewById(R.id.drawer_menu_right_side_container);
        mActionBarWorkerAvatar = (ImageView) mMainActivity.findViewById(R.id.action_bar_worker);
        mActionBarWorkerName = (TextView) mMainActivity.findViewById(R.id.action_bar_worker_name);
        mActionBarWorkerFactoryName = (TextView) mMainActivity.findViewById(R.id.action_bar_worker_factory_name);
    }

    private void setupViews() {
        mActionBarWorkerName.setText(WorkingData.getInstance(mMainActivity).getLoginWorker().name);
        mActionBarWorkerFactoryName.setText(WorkingData.getInstance(mMainActivity).getLoginWorker().factoryName);

        mActionBarWorkerContainer.setOnClickListener(this);
    }

    private void setupActionbar() {
        mMainActivity.setSupportActionBar(mToolbar);
        mActionBar = mMainActivity.getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
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
            Log.d("danny", "onDrawerOpened left side");

        } else if (mRightDrawerView == drawerView) {
            Log.d("danny", "onDrawerOpened right side");
        }
    }

    private void onCloseDrawer(View drawerView) {
        if (mLeftDrawerView == drawerView) {
            Log.d("danny", "onDrawerClosed left side");

        } else if (mRightDrawerView == drawerView) {
            Log.d("danny", "onDrawerClosed right side");
        }
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

        // Default fragment when launch app
        TaskListFragment taskListFragment = (TaskListFragment) mFragmentManager.findFragmentByTag(FragmentTag.TASK_LIST);
        if (taskListFragment == null) {
            taskListFragment = new TaskListFragment();
            fragmentTransaction.add(R.id.content_container, taskListFragment, FragmentTag.TASK_LIST);
        }

        //mCurrentFragment = mainInfoFragment;
        fragmentTransaction.commit();
    }

    private void loadDataInFirstLaunch() {
        loadWorkerAvatar();
    }

    private void loadWorkerAvatar() {
        Drawable avatar = WorkingData.getInstance(mMainActivity).getLoginWorker().avatar;
        String s = WorkingData.getInstance(mMainActivity).getLoginWorker().avatarUrl;

        if (TextUtils.isEmpty(s)) {
            if (avatar == null) {
                mActionBarWorkerAvatar.setImageResource(R.drawable.ic_worker);
            } else {
                mActionBarWorkerAvatar.setImageDrawable(avatar);
            }

            return;
        }

        Uri.Builder avatarBuilder = Uri.parse(LoadingDataUtils.sBaseUrl).buildUpon();
        avatarBuilder.path(s);
        Uri avatarUri = avatarBuilder.build();

        LoadingWorkerAvatarCommand loadingWorkerAvatarCommand
                = new LoadingWorkerAvatarCommand(mMainActivity, avatarUri, mActionBarWorkerAvatar);
        loadingWorkerAvatarCommand.execute();
    }

    private void loadingLoginWorker() {
        LoadingLoginWorkerCommand loadingLoginWorkerCommand = new LoadingLoginWorkerCommand(mMainActivity, this);
        loadingLoginWorkerCommand.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_worker_container:
                if (isLeftDrawerOpened()) break;
                openLeftDrawer();

                break;

            case R.id.fab:
                Utilities.showDialog(mFragmentManager, DisplayDialogFragment.DialogType.CHECK_IN_OUT, null);

                break;
        }
    }

    @Override
    public void onLoadingLoginWorkerSuccessful() {
        loadWorkerAvatar();
        mActionBarWorkerName.setText(WorkingData.getInstance(mMainActivity).getLoginWorker().name);
        mActionBarWorkerFactoryName.setText(WorkingData.getInstance(mMainActivity).getLoginWorker().factoryName);
    }

    @Override
    public void onLoadingLoginWorkerFailed(boolean isFailCausedByInternet) {
        Utilities.showInternetConnectionWeakToast(mMainActivity);
    }
}
