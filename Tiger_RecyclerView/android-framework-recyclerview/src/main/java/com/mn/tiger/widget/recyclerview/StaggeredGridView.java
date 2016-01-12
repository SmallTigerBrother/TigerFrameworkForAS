package com.mn.tiger.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by peng on 15/9/11.
 */
public class StaggeredGridView extends TGRecyclerView
{
    public StaggeredGridView(Context context)
    {
        super(context);
        setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    public StaggeredGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    public StaggeredGridView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    public void setSpanCount(int spanCount)
    {
        ((StaggeredGridLayoutManager)getLayoutManager()).setSpanCount(2);
    }
}
