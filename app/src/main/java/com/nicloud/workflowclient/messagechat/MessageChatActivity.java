package com.nicloud.workflowclient.messagechat;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.nicloud.workflowclient.R;

import java.util.ArrayList;
import java.util.List;

public class MessageChatActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_WORKER_ID = "extra_worker_id";
    public static final String EXTRA_WORKER_NAME = "extra_worker_name";

    private String mWorkerId;
    private String mWorkerName;

    private RecyclerView mMessageList;
    private LinearLayoutManager mMessageListLayoutManager;

    private EditText mMessageBox;
    private ImageView mSendButton;

    private List<String> mMessageListData = new ArrayList<>();

    private TextWatcher mMessageBoxWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String message = mMessageBox.getText().toString();

            if (TextUtils.isEmpty(message)) {
                mSendButton.setImageResource(R.drawable.ic_send_disabled);
                mSendButton.setBackground(null);
            } else {
                mSendButton.setImageResource(R.drawable.ic_send_enabled);
                mSendButton.setBackgroundResource(R.drawable.send_button_enabled_background);
            }

            mSendButton.setAnimation(
                    AnimationUtils.loadAnimation(MessageChatActivity.this, R.anim.message_send_button_reveal));
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);
        initialize();
    }

    private void initialize() {
        mWorkerId = getIntent().getStringExtra(EXTRA_WORKER_ID);
        mWorkerName = getIntent().getStringExtra(EXTRA_WORKER_NAME);
        findViews();
        setupViews();
        setupActionBar();
        setupMessageList();
    }

    private void findViews() {
        mMessageList = (RecyclerView) findViewById(R.id.message_list);
        mMessageBox = (EditText) findViewById(R.id.message_box);
        mSendButton = (ImageView) findViewById(R.id.send_button);
    }

    private void setupViews() {
        mMessageBox.addTextChangedListener(mMessageBoxWatcher);
        mSendButton.setOnClickListener(this);
    }

    private void setupActionBar() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mWorkerName);
    }

    private void setupMessageList() {
        mMessageListLayoutManager = new LinearLayoutManager(this);

        mMessageList.setLayoutManager(mMessageListLayoutManager);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:

                break;
        }
    }
}
