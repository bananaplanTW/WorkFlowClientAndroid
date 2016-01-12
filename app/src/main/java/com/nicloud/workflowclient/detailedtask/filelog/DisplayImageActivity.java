package com.nicloud.workflowclient.detailedtask.filelog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.nicloud.workflowclient.R;


public class DisplayImageActivity extends AppCompatActivity {

    private static final String TAG = "DisplayImageActivity";

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_IMAGE_URL = "extra_image_url";

    private ActionBar mActionBar;

    private WebView mImageWebView;
    private TextView mImageName;

    private String mTitle;
    private String mImageUrl;


    public static Intent launchDisplayImageActivity(Context context, String title, String imageUrl) {
        Intent intent = new Intent(context, DisplayImageActivity.class);
        intent.putExtra(DisplayImageActivity.EXTRA_TITLE, title);
        intent.putExtra(DisplayImageActivity.EXTRA_IMAGE_URL, imageUrl);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        initialize(getIntent());
    }

    private void initialize(Intent intent) {
        mTitle = intent.getStringExtra(EXTRA_TITLE);
        mImageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);
        findViews();
        setupActionBar();
        setupViews();
        setupImageWebView();
    }

    private void findViews() {
        mImageName = (TextView) findViewById(R.id.display_image_name);
        mImageWebView = (WebView) findViewById(R.id.image_web_view);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViews() {
        mImageName.setText(mTitle);
    }

    private void setupImageWebView() {
        mImageWebView.getSettings().setUseWideViewPort(true);
        mImageWebView.getSettings().setLoadWithOverviewMode(true);
        mImageWebView.getSettings().setSupportZoom(true);
        mImageWebView.getSettings().setBuiltInZoomControls(true);
        mImageWebView.setInitialScale(0);

        mImageWebView.loadUrl(mImageUrl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
