package com.coolweather.android;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by ZhiQiang on 2017/5/19.
 */

public class SimpleItemTouchHelperCallBack extends ItemTouchHelper.Callback {

    private onMovedAndSwipedListener mMovedAndSwipedListener;

    public SimpleItemTouchHelperCallBack(onMovedAndSwipedListener listener) {
        mMovedAndSwipedListener = listener;
    }

    //用于设置拖动的方向和侧滑的方向
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = 0;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    //拖动item时的回调方法
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    //侧滑item时的回调方法
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
         mMovedAndSwipedListener.onItemDismiss(viewHolder.getAdapterPosition());
    }

    //当状态改变时回调此方法
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (viewHolder instanceof onStateChangeListener) {
                onStateChangeListener listener = (onStateChangeListener) viewHolder;
                listener.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //当拖拽或侧滑完成时回调此方法
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof onStateChangeListener) {
            onStateChangeListener listener = (onStateChangeListener) viewHolder;
            listener.onItemClear();
        }
    }

    //此方法可以判断当前是侧滑还是拖动
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
