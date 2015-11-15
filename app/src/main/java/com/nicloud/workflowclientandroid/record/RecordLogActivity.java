package com.nicloud.workflowclientandroid.record;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.nicloud.workflowclientandroid.R;

public class RecordLogActivity extends AppCompatActivity implements TabHost.OnTabChangeListener {

    private static final class TabTag {
        public static final String TEXT = "tag_tab_text";
        public static final String PHOTO = "tag_tab_photo";
        public static final String FILE = "tag_tab_file";
    }

    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private TextView mRecordTaskName;
    private TextView mRecordCaseName;

    private TabHost mRecordLogTabHost;

    private class RecordLogTabContentFactory implements TabHost.TabContentFactory {

        private Context mContext;

        public RecordLogTabContentFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            v.setVisibility(View.GONE);
            return v;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_log);
        initialize();
    }

    private void initialize() {
        findViews();
        setupActionBar();
        setupTabs();
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mRecordTaskName = (TextView) findViewById(R.id.record_log_task_name);
        mRecordCaseName = (TextView) findViewById(R.id.record_log_case_name);
        mRecordLogTabHost = (TabHost) findViewById(R.id.record_log_tab_host);
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecordTaskName.setText("伺服器服務開發");
        mRecordCaseName.setText("流程管理專案");
    }

    private void setupTabs() {
        mRecordLogTabHost.setup();
        addTab(TabTag.TEXT);
        addTab(TabTag.PHOTO);
        addTab(TabTag.FILE);
        mRecordLogTabHost.setOnTabChangedListener(this);
    }

    private void addTab(String tag) {
        View tabView = getTabView(tag);
        mRecordLogTabHost.addTab(mRecordLogTabHost.newTabSpec(tag).setIndicator(getTabView(tag))
                         .setContent(new RecordLogTabContentFactory(this)));
    }

    private View getTabView(String tag) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.record_log_tab, null);
        TextView tabText = (TextView) tabView.findViewById(R.id.record_log_tab_text);

        String text = "";
        if(TabTag.TEXT.equals(tag)) {
            text = getString(R.string.record_log_tab_text);
        } else if(TabTag.PHOTO.equals(tag)) {
            text = getString(R.string.record_log_tab_photo);
        } else if(TabTag.FILE.equals(tag)) {
            text = getString(R.string.record_log_tab_file);
        }

        tabText.setText(text);

        return tabView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_add_record:
                goToAddRecordActivity();
                return true;

            default:
                return false;
        }
    }

    private void goToAddRecordActivity() {
        startActivity(new Intent(this, AddRecordActivity.class));
    }

    @Override
    public void onTabChanged(String tabId) {

    }
}
