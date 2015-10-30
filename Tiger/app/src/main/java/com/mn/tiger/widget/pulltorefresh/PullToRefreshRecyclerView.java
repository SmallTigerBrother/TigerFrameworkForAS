package com.mn.tiger.widget.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.mn.tiger.widget.recyclerview.TGRecyclerView;

/**
 * 拖动刷新列表
 * Created by Dalang on 2015/9/10.
 */
public class PullToRefreshRecyclerView extends BGARefreshLayout implements IPullToRefreshView
{
    private TGRecyclerView recyclerView;

    private PullToRefreshViewImp pullToRefreshViewImp;

    public PullToRefreshRecyclerView(Context context)
    {
        super(context);
        initRecyclerView();

        pullToRefreshViewImp = new PullToRefreshViewImp(this);
        this.setDelegate(pullToRefreshViewImp);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initRecyclerView();

        pullToRefreshViewImp = new PullToRefreshViewImp(this);
        this.setDelegate(pullToRefreshViewImp);
    }

    private void initRecyclerView()
    {
        recyclerView = new TGRecyclerView(getContext());
        recyclerView.setLayoutManager(new InternalLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        this.addView(recyclerView, layoutParams);
    }

    /**
     * 设置列表行动画
     * @param animator
     */
    public void setItemAnimator(RecyclerView.ItemAnimator animator)
    {
        recyclerView.setItemAnimator(animator);
    }

    /**
     * 设置adapter，请使用TGRecyclerViewAdapter
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter)
    {
        recyclerView.setAdapter(adapter);
    }

    /**
     * 获取Adapter，Adapter是TGRecyclerViewAdapter的实例
     * @return
     */
    public RecyclerView.Adapter getAdapter()
    {
        return recyclerView.getAdapter();
    }

    /**
     * 设置列表行点击事件
     * @param onItemClickListener
     */
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
        endRefreshing();
        endLoadingMore();
    }

    /**
     * 添加滚动监听器
     * @param onScrollListener
     */
    public void addOnScrollListener(RecyclerView.OnScrollListener onScrollListener)
    {
        recyclerView.addOnScrollListener(onScrollListener);
    }

    /**
     * 执行数据刷新线程，为防止卡顿掉帧，控制器将选择合适的时机刷新界面
     * @param runnable
     */
    public void postRefreshRunnable(Runnable runnable)
    {
        pullToRefreshViewImp.postRefreshRunnable(runnable);
    }

    /**
     * 滚动到指定位置
     * @param position
     */
    public void scrollToPosition(int position)
    {
        recyclerView.scrollToPosition(position);
    }

    /**
     * 内部使用的LinearLayoutManager，修改findFirstCompletelyVisibleItemPosition方法，原返回值为-1时，返回0
     */
    private class InternalLinearLayoutManager extends LinearLayoutManager
    {
        public InternalLinearLayoutManager(Context context, int orientation, boolean reverseLayout)
        {
            super(context, orientation, reverseLayout);
        }

        @Override
        public int findFirstCompletelyVisibleItemPosition()
        {
            int firstCompletelyVisibleItemPosition = super.findFirstCompletelyVisibleItemPosition();
            int firstVisibleItemPosition = super.findFirstVisibleItemPosition();
            if(firstCompletelyVisibleItemPosition != RecyclerView.NO_POSITION)
            {
                return firstCompletelyVisibleItemPosition;
            }
            else
            {
                return firstVisibleItemPosition;
            }
        }

        @Override
        public int findLastCompletelyVisibleItemPosition()
        {
            int lastCompletelyVisibleItemPosition =  super.findLastCompletelyVisibleItemPosition();
            int lastVisibleItemPosition = super.findLastVisibleItemPosition();
            if(lastCompletelyVisibleItemPosition != RecyclerView.NO_POSITION)
            {
                return lastCompletelyVisibleItemPosition;
            }
            else
            {
                return lastVisibleItemPosition;
            }
        }
    }

}