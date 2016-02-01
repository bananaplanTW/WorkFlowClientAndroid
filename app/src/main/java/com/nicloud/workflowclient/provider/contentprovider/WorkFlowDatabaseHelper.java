package com.nicloud.workflowclient.provider.contentprovider;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nicloud.workflowclient.provider.database.CheckListTable;
import com.nicloud.workflowclient.provider.database.DiscussionTable;
import com.nicloud.workflowclient.provider.database.FileTable;
import com.nicloud.workflowclient.provider.database.MessageTable;
import com.nicloud.workflowclient.provider.database.TaskTable;
import com.nicloud.workflowclient.provider.database.WorkFlowContract;

import java.util.ArrayList;

/**
 * Created by logicmelody on 2016/1/7.
 */
public class WorkFlowDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "workflow.db";
    private static final int DB_VERSION = 1;

    private static WorkFlowDatabaseHelper sWorkFlowDatabaseHelper;


    private WorkFlowDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static WorkFlowDatabaseHelper getInstance(Context context) {
        if (sWorkFlowDatabaseHelper == null) {
            synchronized (WorkFlowDatabaseHelper.class) {
                if (sWorkFlowDatabaseHelper == null) {
                    sWorkFlowDatabaseHelper = new WorkFlowDatabaseHelper(context);
                }
            }
        }

        return sWorkFlowDatabaseHelper;
    }

    public static void deleteAllTablesData(Context context) {
        SQLiteDatabase db = WorkFlowDatabaseHelper.getInstance(context).getWritableDatabase();
        db.delete(WorkFlowContract.Message.TABLE_NAME, null, null);
        db.delete(WorkFlowContract.Discussion.TABLE_NAME, null, null);
        db.delete(WorkFlowContract.Task.TABLE_NAME, null, null);
        db.delete(WorkFlowContract.CheckList.TABLE_NAME, null, null);
        db.delete(WorkFlowContract.File.TABLE_NAME, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MessageTable.onCreate(db);
        DiscussionTable.onCreate(db);
        TaskTable.onCreate(db);
        CheckListTable.onCreate(db);
        FileTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MessageTable.onUpgrade(db, oldVersion, newVersion);
        DiscussionTable.onUpgrade(db, oldVersion, newVersion);
        TaskTable.onUpgrade(db, oldVersion, newVersion);
        CheckListTable.onUpgrade(db, oldVersion, newVersion);
        FileTable.onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }
}
