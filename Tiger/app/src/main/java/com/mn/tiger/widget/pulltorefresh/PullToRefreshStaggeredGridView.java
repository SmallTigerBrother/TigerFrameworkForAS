package com.mn.tiger.widget.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.mn.tiger.widget.recyclerview.StaggeredGridView;
import com.mn.tiger.widget.recyclerview.TGRecyclerView;

/**
 * Created by Dalang on 2015/9/10.
 */
public class PullToRefreshStaggeredGridView extends BGARefreshLayout implements IPullToRefreshView
{
    private TGRecyclerView recyclerView;

    private PullToRefreshViewImp pullToRefreshViewImp;

    public PullToRefreshStaggeredGridView(Context context)
    {
        super(context);
        initRecyclerView();

        pullToRefreshViewImp = new PullToRefreshViewImp(this);
        this.setDelegate(pullToRefreshViewImp);
    }

    public PullToRefreshStaggeredGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initRecyclerView();

        pullToRefreshViewImp = new PullToRefreshViewImp(this);
        this.setDelegate(pullToRefreshViewImp);
    }

    private void initRecyclerView()
    {
        recyclerView = new StaggeredGridView(getContext());
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
    public void setRefreshViewHolder(BGARefreshViewHolder refreshViewHolder)
    {
        super.setRefreshViewHolder(refreshViewHolder);
        pullToRefreshViewImp.setRefreshViewHolder(refreshViewHolder);
    }

    @Override
    public void setMode(Mode mode)
    {
        pullToRefreshViewImp.setMode(mode);
    }

    @Override
    public void setOnRefreshListener(OnRefreshListener listener)
    {
        pullToRefreshViewImp.setOnRefreshListener(listener);
    }

    @Override
    public void onRefreshComplete()
    {
        endLoadingMore();
        endRefreshing();
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener onScrollListener)
    {
        recyclerView.addOnScrollListener(onScrollListener);
    }

    public void postRefreshRunnable(Runnable runnable)
    {
        pullToRefreshViewImp.postRefreshRunnable(runnable);
    }

}