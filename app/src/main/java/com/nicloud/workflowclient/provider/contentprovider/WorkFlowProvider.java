package com.nicloud.workflowclient.provider.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.nicloud.workflowclient.provider.database.WorkFlowContract;

/**
 * Created by logicmelody on 2016/1/7.
 */
public class WorkFlowProvider extends ContentProvider {

    private static final class UriMatcherIndex {
        public static final int MESSAGE = 0;
        public static final int DISCUSSION = 1;
        public static final int TASK = 2;
        public static final int CHECK_LIST = 3;
        public static final int FILE = 4;
    }

    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(WorkFlowContract.AUTHORITY, WorkFlowContract.Message.TABLE_NAME, UriMatcherIndex.MESSAGE);
        sUriMatcher.addURI(WorkFlowContract.AUTHORITY, WorkFlowContract.Discussion.TABLE_NAME, UriMatcherIndex.DISCUSSION);
        sUriMatcher.addURI(WorkFlowContract.AUTHORITY, WorkFlowContract.Task.TABLE_NAME, UriMatcherIndex.TASK);
        sUriMatcher.addURI(WorkFlowContract.AUTHORITY, WorkFlowContract.CheckList.TABLE_NAME, UriMatcherIndex.CHECK_LIST);
        sUriMatcher.addURI(WorkFlowContract.AUTHORITY, WorkFlowContract.File.TABLE_NAME, UriMatcherIndex.FILE);
    }

    private WorkFlowDatabaseHelper mWorkFlowDatabaseHelper;


    @Override
    public boolean onCreate() {
        mWorkFlowDatabaseHelper = WorkFlowDatabaseHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mWorkFlowDatabaseHelper.getReadableDatabase();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case UriMatcherIndex.MESSAGE:
                queryBuilder.setTables(WorkFlowContract.Message.TABLE_NAME);
                break;

            case UriMatcherIndex.DISCUSSION:
                queryBuilder.setTables(WorkFlowContract.Discussion.TABLE_NAME);
                break;

            case UriMatcherIndex.TASK:
                queryBuilder.setTables(WorkFlowContract.Task.TABLE_NAME);
                break;

            case UriMatcherIndex.CHECK_LIST:
                queryBuilder.setTables(WorkFlowContract.CheckList.TABLE_NAME);
                break;

            case UriMatcherIndex.FILE:
                queryBuilder.setTables(WorkFlowContract.File.TABLE_NAME);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mWorkFlowDatabaseHelper.getWritableDatabase();
        long id = 0;

        switch (sUriMatcher.match(uri)) {
            case UriMatcherIndex.MESSAGE:
                id = db.insert(WorkFlowContract.Message.TABLE_NAME, null, values);
                break;

            case UriMatcherIndex.DISCUSSION:
                id = db.insert(WorkFlowContract.Discussion.TABLE_NAME, null, values);
                break;

            case UriMatcherIndex.TASK:
                id = db.insert(WorkFlowContract.Task.TABLE_NAME, null, values);
                break;

            case UriMatcherIndex.CHECK_LIST:
                id = db.insert(WorkFlowContract.CheckList.TABLE_NAME, null, values);
                break;

            case UriMatcherIndex.FILE:
                id = db.insert(WorkFlowContract.File.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(WorkFlowContract.Message.CONTENT_URI, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mWorkFlowDatabaseHelper.getWritableDatabase();
        int rowsDeleted = 0;

        switch (sUriMatcher.match(uri)) {
            case UriMatcherIndex.MESSAGE:
                rowsDeleted = db.delete(WorkFlowContract.Message.TABLE_NAME, selection, selectionArgs);
                break;

            case UriMatcherIndex.DISCUSSION:
                rowsDeleted = db.delete(WorkFlowContract.Discussion.TABLE_NAME, selection, selectionArgs);
                break;

            case UriMatcherIndex.TASK:
                rowsDeleted = db.delete(WorkFlowContract.Task.TABLE_NAME, selection, selectionArgs);
                break;

            case UriMatcherIndex.CHECK_LIST:
                rowsDeleted = db.delete(WorkFlowContract.CheckList.TABLE_NAME, selection, selectionArgs);
                break;

            case UriMatcherIndex.FILE:
                rowsDeleted = db.delete(WorkFlowContract.File.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mWorkFlowDatabaseHelper.getWritableDatabase();
        int rowsUpdated = 0;

        switch (sUriMatcher.match(uri)) {
            case UriMatcherIndex.MESSAGE:
                rowsUpdated = db.update(WorkFlowContract.Message.TABLE_NAME, values, selection, selectionArgs);
                break;

            case UriMatcherIndex.DISCUSSION:
                rowsUpdated = db.update(WorkFlowContract.Discussion.TABLE_NAME, values, selection, selectionArgs);
                break;

            case UriMatcherIndex.TASK:
                rowsUpdated = db.update(WorkFlowContract.Task.TABLE_NAME, values, selection, selectionArgs);
                break;

            case UriMatcherIndex.CHECK_LIST:
                rowsUpdated = db.update(WorkFlowContract.CheckList.TABLE_NAME, values, selection, selectionArgs);
                break;

            case UriMatcherIndex.FILE:
                rowsUpdated = db.update(WorkFlowContract.File.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}
