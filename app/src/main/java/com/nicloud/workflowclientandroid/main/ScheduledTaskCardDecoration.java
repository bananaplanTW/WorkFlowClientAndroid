package com.nicloud.workflowclientandroid.main;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nicloud.workflowclientandroid.R;


/**
 * @author Danny Lin
 * @since 2015/7/28.
 */
public class ScheduledTaskCardDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;


    public ScheduledTaskCardDecoration(Context context) {
        mContext = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();

        int normalMargin = mContext.getResources().getDimensionPixelSize(R.dimen.scheduled_task_card_margin_top_bottom);

        if (position == 0) {
            outRect.bottom = normalMargin;
        } else if (position == itemCount-1) {
            outRect.top = normalMargin;
        } else {
            outRect.top = normalMargin;
            outRect.bottom = normalMargin;
        }
    }
}
