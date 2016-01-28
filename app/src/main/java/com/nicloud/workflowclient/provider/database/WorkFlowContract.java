package com.nicloud.workflowclient.provider.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by logicmelody on 2016/1/7.
 */
public class WorkFlowContract {

    public static final String AUTHORITY = "com.nicloud.workflowclient.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);


    public static final class Message implements BaseColumns {

        public static final String TABLE_NAME = "message";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        // DB columns
        public static final String MESSAGE_ID = "message_id";
        public static final String CONTENT = "content";
        public static final String SENDER_ID = "sender_id";
        public static final String RECEIVER_ID = "receiver_id";
        public static final String TIME = "time";
    }

    public static final class Discussion implements BaseColumns {

        public static final class Type {
            public static final String MESSAGE = "text";
            public static final String IMAGE = "image";
            public static final String FILE = "file";
        }

        public static final String TABLE_NAME = "discussion";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        // DB columns
        public static final String DISCUSSION_ID = "discussion_id";
        public static final String CASE_ID = "case_id";
        public static final String WORKER_ID = "worker_id";
        public static final String WORKER_NAME = "worker_name";
        public static final String WORKER_AVATAR_URI = "worker_avatar_uri";
        public static final String CONTENT = "content";
        public static final String FILE_NAME = "file_name";
        public static final String FILE_URI = "file_uri";
        public static final String FILE_THUMB_URI = "file_thumb_uri";
        public static final String TYPE = "type";
        public static final String CREATED_TIME = "created_time";
        public static final String UPDATED_TIME = "updated_time";
    }
}