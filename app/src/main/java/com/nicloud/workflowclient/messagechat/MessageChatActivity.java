package com.nicloud.workflowclient.messagechat;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.nicloud.workflowclient.R;
import com.nicloud.workflowclient.data.data.data.WorkingData;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;
import com.nicloud.workflowclient.serveraction.service.MessageService;

import java.util.ArrayList;
import java.util.List;

public class MessageChatActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, LoadMessageReceiver.OnLoadMessageListener {

    public static final String EXTRA_WORKER_ID = "extra_worker_id";
    public static final String EXTRA_WORKER_NAME = "extra_worker_name";

    private static final int LOADER_ID = 1;

    private static final String[] mProjection = new String[] {
            WorkFlowContract.Message._ID,
            WorkFlowContract.Message.MESSAGE_ID,
            WorkFlowContract.Message.CONTENT,
            WorkFlowContract.Message.SENDER_ID,
            WorkFlowContract.Message.RECEIVER_ID,
            WorkFlowContract.Message.TIME,
    };
    private static final int ID = 0;
    private static final int MESSAGE_ID = 1;
    private static final int CONTENT = 2;
    private static final int SENDER_ID = 3;
    private static final int RECEIVER_ID = 4;
    private static final int TIME = 5;

    private static final String mSelection
            = "(" + WorkFlowContract.Message.SENDER_ID + " = ?" + " AND " +
              WorkFlowContract.Message.RECEIVER_ID + " = ?) OR " +
              "(" + WorkFlowContract.Message.SENDER_ID + " = ?" + " AND " +
              WorkFlowContract.Message.RECEIVER_ID + " = ?)";
    private static String[] mSelectionArgs;
    private static final String mSortOrder = WorkFlowContract.Message.TIME;

    private String mWorkerId;
    private String mWorkerName;

    private RecyclerView mMessageList;
    private LinearLayoutManager mMessageListLayoutManager;
    private MessageListAdapter mMessageListAdapter;

    private EditText mMessageBox;
    private ImageView mSendButton;

    private LoadMessageReceiver mLoadMessageReceiver;

    private List<MessageItem> mMessageListData = new ArrayList<>();

    private boolean mIsSendButtonBeenChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);
        initialize();
        loadMessagesFirstLaunch();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void initialize() {
        mWorkerId = getIntent().getStringExtra(EXTRA_WORKER_ID);
        mWorkerName = getIntent().getStringExtra(EXTRA_WORKER_NAME);
        mSelectionArgs = new String[] {WorkingData.getUserId(), mWorkerId, mWorkerId, WorkingData.getUserId()};
        mLoadMessageReceiver = new LoadMessageReceiver(this);

        findViews();
        setupViews();
        setupActionBar();
        setupMessageList();
    }

    private void loadMessagesFirstLaunch() {
        if (getMessageCount() == 0) {
            startService(MessageService.generateLoadMessageNormalIntent(this, mWorkerId));

        } else {
            startService(MessageService.generateLoadMessageFromIntent(this, mWorkerId, getLastMessageDate()));
        }
    }

    private int getMessageCount() {
        Cursor cursor = null;
        int messageCount = 0;

        try {
            cursor = getContentResolver().query(WorkFlowContract.Message.CONTENT_URI,
                    mProjection, mSelection, mSelectionArgs, null);
            if (cursor != null) {
                messageCount = cursor.getCount();
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return messageCount;
    }

    private long getLastMessageDate() {
        Cursor cursor = null;
        long lastMessageDate = 0L;

        try {
            cursor = getContentResolver().query(WorkFlowContract.Message.CONTENT_URI,
                    mProjection, mSelection, mSelectionArgs, null);
            if (cursor != null) {
                cursor.moveToLast();
                lastMessageDate = cursor.getLong(TIME);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return lastMessageDate;
    }

    private void findViews() {
        mMessageList = (RecyclerView) findViewById(R.id.message_list);
        mMessageBox = (EditText) findViewById(R.id.message_box);
        mSendButton = (ImageView) findViewById(R.id.send_button);
    }

    private void setupViews() {
        mMessageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    mSendButton.setImageResource(R.drawable.ic_send_disabled);
                    mSendButton.setBackground(null);
                    mSendButton.setAnimation(
                            AnimationUtils.loadAnimation(MessageChatActivity.this, R.anim.message_send_button_reveal));
                    mIsSendButtonBeenChanged = false;

                } else {
                    if (mIsSendButtonBeenChanged) return;

                    mSendButton.setImageResource(R.drawable.ic_send_enabled);
                    mSendButton.setBackgroundResource(R.drawable.send_button_enabled_background);
                    mSendButton.setAnimation(
                            AnimationUtils.loadAnimation(MessageChatActivity.this, R.anim.message_send_button_reveal));
                    mIsSendButtonBeenChanged = true;
                }
            }
        });

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
        mMessageListAdapter = new MessageListAdapter(this, mMessageListData);

        mMessageList.addItemDecoration(new MessageListDecoration(this));
        mMessageList.setLayoutManager(mMessageListLayoutManager);
        mMessageList.setAdapter(mMessageListAdapter);
        mMessageList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isMessageListScrollToTop()) {
                    Log.d("danny", "onScroll top");
                }
            }
        });
    }

    private boolean isMessageListScrollToTop() {
        return mMessageListLayoutManager.findFirstVisibleItemPosition() == 0;
    }

    private void messageListScrollToLast() {
        mMessageList.scrollToPosition(mMessageListData.size() - 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(LoadMessageReceiver.ACTION_LOAD_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLoadMessageReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLoadMessageReceiver);
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
                sendMessage();

                break;
        }
    }

    private void sendMessage() {
        String message = mMessageBox.getText().toString();
        if (TextUtils.isEmpty(message)) return;

        mMessageBox.setText(null);

        ContentValues values = new ContentValues();
        values.put(WorkFlowContract.Message.MESSAGE_ID, "gdthfkdsjie");
        values.put(WorkFlowContract.Message.CONTENT, message);
        values.put(WorkFlowContract.Message.SENDER_ID, WorkingData.getUserId());
        values.put(WorkFlowContract.Message.RECEIVER_ID, mWorkerId);
        values.put(WorkFlowContract.Message.TIME, System.currentTimeMillis());

        getContentResolver().insert(WorkFlowContract.Message.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader
                = new CursorLoader(this, WorkFlowContract.Message.CONTENT_URI,
                                   mProjection, mSelection, mSelectionArgs, mSortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) return;

        setMessageData(data);
    }

    private void setMessageData(Cursor cursor) {
        mMessageListData.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(ID);
            String messageId = cursor.getString(MESSAGE_ID);
            String content = cursor.getString(CONTENT);
            String senderId = cursor.getString(SENDER_ID);
            String receiverId = cursor.getString(RECEIVER_ID);
            long time = cursor.getLong(TIME);

            mMessageListData.add(new MessageItem(messageId, content, senderId, receiverId, time));
        }

        mMessageListAdapter.notifyDataSetChanged();
        messageListScrollToLast();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onLoadMessage(Intent intent) {
        String senderId = intent.getStringExtra(LoadMessageReceiver.EXTRA_SENDER_ID);
        if (!senderId.equals(mWorkerId)) return;

        startService(MessageService.generateLoadMessageFromIntent(this, mWorkerId, getLastMessageDate()));
    }
}