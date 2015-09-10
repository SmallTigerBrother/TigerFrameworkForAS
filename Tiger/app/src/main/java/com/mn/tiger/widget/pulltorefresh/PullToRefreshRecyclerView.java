package com.mn.tiger.widget.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.mn.tiger.widget.pulltorefresh.library.IPullToRefreshView;
import com.mn.tiger.widget.recyclerview.TGRecyclerView;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * Created by Dalang on 2015/9/10.
 */
public class PullToRefreshRecyclerView extends BGARefreshLayout implements IPullToRefreshView
{
    private TGRecyclerView recyclerView;

    public PullToRefreshRecyclerView(Context context)
    {
        super(context);
        initRecyclerView();
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initRecyclerView();
    }

    private void initRecyclerView()
    {
        recyclerView = new TGRecyclerView(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        this.addView(recyclerView, layoutParams);
    }

    public void setAdapter(RecyclerView.Adapter adapter)
    {
        recyclerView.setAdapter(adapter);
    }

    public RecyclerView.Adapter getAdapter()
    {
        return recyclerView.getAdapter();
    }

    public void setOnItemClickListener(TGRecyclerView.OnItemClickListener onItemClickListener)
    {
        recyclerView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void setMode(Mode mode)
    {

    }

    @Override
    public void setOnRefreshListener(OnRefreshListener listener)
    {

    }

    @Override
    public void onRefreshComplete()
    {

    }
}
