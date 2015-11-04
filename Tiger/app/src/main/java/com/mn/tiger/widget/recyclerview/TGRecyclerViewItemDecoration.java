package com.mn.tiger.widget.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2015/11/2.
 */
public class TGRecyclerViewItemDecoration extends RecyclerView.ItemDecoration
{
    private int leftSpace;
    private int topSpace;
    private int rightSpace;
    private int bottomSpace;

    /**
     * recyclerview item间距设置
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public TGRecyclerViewItemDecoration(int left, int top, int right, int bottom)
    {
        this.leftSpace = left;
        this.topSpace = top;
        this.rightSpace = right;
        this.bottomSpace = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        if (parent.getChildPosition(view) != 0)
        {
            if (leftSpace >= 0)
            {
                outRect.left = leftSpace;
            }
            if (topSpace >= 0)
            {
                outRect.top = topSpace;
            }
            if (rightSpace >= 0)
            {
                outRect.right = rightSpace;
            }
            if (bottomSpace >= 0)
            {
                outRect.bottom = bottomSpace;
            }
        }
    }
}
