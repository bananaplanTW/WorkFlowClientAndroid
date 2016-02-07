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

    public static final class Task implements BaseColumns {

        public static final class Status {
            public static final String DONE = "done";
        }

        public static final String TABLE_NAME = "task";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        // DB columns
        public static final String TASK_ID = "task_id";
        public static final String TASK_NAME = "task_name";
        public static final String TASK_DESCRIPTION = "task_description";
        public static final String CASE_ID = "case_id";
        public static final String CASE_NAME = "case_name";
        public static final String WORKER_ID = "worker_id";
        public static final String DUE_DATE = "due_date";
        public static final String UPDATED_TIME = "updated_time";
        public static final String STATUS = "status";
    }

    public static final class TaskTextLog implements BaseColumns {

        public static final String TABLE_NAME = "tasktextlog";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        // DB columns
        public static final String TASK_TEXT_LOG_ID = "task_text_log_id";
        public static final String TASK_ID = "task_id";
        public static final String OWNER_ID = "owner_id";
        public static final String OWNER_NAME = "owner_name";
        public static final String OWNER_AVATAR_URL = "owner_avatar_url";
        public static final String CREATED_TIME = "created_time";
        public static final String UPDATED_TIME = "updated_time";
        public static final String CONTENT = "content";
    }

    public static final class CheckList implements BaseColumns {

        public static final String TABLE_NAME = "checklist";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        // DB columns
        public static final String CHECK_NAME = "check_name";
        public static final String IS_CHECKED = "is_checked";
        public static final String TASK_ID = "task_id";
        public static final String POSITION = "position";
    }

    public static final class File implements BaseColumns {

        public static final class Type {
            public static final String IMAGE = "image";
            public static final String FILE = "file";
        }

        public static final String TABLE_NAME = "file";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        // DB columns
        public static final String FILE_ID = "file_id";
        public static final String FILE_NAME = "file_name";
        public static final String FILE_TYPE = "file_type";
        public static final String FILE_URL = "file_url";
        public static final String FILE_THUMB_URL = "file_thumb_url";
        public static final String OWNER_ID = "owner_id";
        public static final String OWNER_NAME = "owner_name";
        public static final String CASE_ID = "case_id";
        public static final String TASK_ID = "task_id";
        public static final String CREATED_TIME = "created_time";
        public static final String UPDATED_TIME = "updated_time";
    }

    public static final class Case implements BaseColumns {

        public static final String TABLE_NAME = "cases";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);

        // DB columns
        public static final String CASE_ID = "case_id";
        public static final String CASE_NAME = "case_name";
        public static final String OWNER_ID = "owner_id";
        public static final String DESCRIPTION = "description";
        public static final String IS_COMPLETED = "is_completed";
        public static final String UPDATED_TIME = "updated_time";
    }
}