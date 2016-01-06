package com.nicloud.workflowclient.messagechat;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nicloud.workflowclient.R;


public class MessageListDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;


    public MessageListDecoration(Context context) {
        mContext = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();

        int normalMargin
                = mContext.getResources().getDimensionPixelSize(R.dimen.message_chat_message_list_normal_margin);
        int boundaryMargin
                = mContext.getResources().getDimensionPixelSize(R.dimen.message_chat_message_list_boundary_margin);

        if (position == 0) {
            outRect.top = boundaryMargin;
            outRect.bottom = normalMargin;

        } else if (position == itemCount - 1) {
            outRect.top = normalMargin;
            outRect.bottom = boundaryMargin;

        } else {
            outRect.top = normalMargin;
            outRect.bottom = normalMargin;
        }
    }
}
