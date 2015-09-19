package com.mn.tiger.widget.pulltorefresh;

import android.os.Handler;
import android.view.View;

import com.mn.tiger.log.Logger;

import java.lang.reflect.Field;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

/**
 * Created by Dalang on 2015/9/11.
 */
class PullToRefreshViewImp implements BGARefreshLayout.BGARefreshLayoutDelegate
{
    private static final Logger LOG = Logger.getLogger(PullToRefreshViewImp.class);

    private BGARefreshLayout refreshLayout;

    private BGARefreshViewHolder refreshViewHolder;

    private IPullToRefreshView.OnRefreshListener onRefreshListener;

    private static Handler handler = new Handler();

    private IPullToRefreshView.Mode mode = IPullToRefreshView.Mode.BOTH;

    PullToRefreshViewImp(BGARefreshLayout layout)
    {
        this.refreshLayout = layout;
        this.refreshLayout.setIsShowLoadingMoreView(true);
    }

    public void setMode(IPullToRefreshView.Mode mode)
    {
        if(this.mode != mode)
        {
            this.mode = mode;
            switch (mode)
            {
                case DISABLED:
                    setPullStartDisable();
                    setPullEndDisable();
                    break;

                case BOTH:
                    setPullStartEnable();
                    setPullEndEnable();
                    break;

                case PULL_FROM_END:
                    setPullStartDisable();
                    setPullEndEnable();
                    break;

                case PULL_FROM_START:
                    setPullStartEnable();
                    setPullEndDisable();
                    break;
            }
        }
    }

    public void setRefreshViewHolder(BGARefreshViewHolder refreshViewHolder)
    {
        this.refreshViewHolder = refreshViewHolder;
        setMode(mode);
    }

    private void setPullStartDisable()
    {
        try
        {
            Field mRefreshHeaderViewField = BGARefreshLayout.class.getDeclaredField("mRefreshHeaderView");
            if(null != mRefreshHeaderViewField)
            {
                mRefreshHeaderViewField.setAccessible(true);
                View mRefreshHeaderView = ((View) mRefreshHeaderViewField.get(refreshLayout));
                if(null != mRefreshHeaderView)
                {
                    mRefreshHeaderViewField.set(refreshLayout, null);
                }
            }
        }
        catch (Exception e)
        {
            LOG.e(e);
        }
    }

    private void setPullStartEnable()
    {
        try
        {
            Field mRefreshHeaderViewField = BGARefreshLayout.class.getDeclaredField("mRefreshHeaderView");
            if(null != mRefreshHeaderViewField)
            {
                mRefreshHeaderViewField.setAccessible(true);
                Object value = mRefreshHeaderViewField.get(refreshLayout);
                if(null == value && null != refreshViewHolder)
                {
                    mRefreshHeaderViewField.set(refreshLayout, refreshViewHolder.getRefreshHeaderView());
                }
            }
        }
        catch (Exception e)
        {
            LOG.e(e);
        }
    }

    private void setPullEndDisable()
    {
        try
        {
            final Field mLoadMoreFooterViewField = BGARefreshLayout.class.getDeclaredField("mLoadMoreFooterView");
            if(null != mLoadMoreFooterViewField && null != refreshViewHolder)
            {
                mLoadMoreFooterViewField.setAccessible(true);
                final View mLoadMoreFooterView = ((View) mLoadMoreFooterViewField.get(refreshLayout));
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            if(null != mLoadMoreFooterView)
                            {
                                mLoadMoreFooterViewField.set(refreshLayout, null);
                            }
                        }
                        catch (Exception e)
                        {
                            LOG.e(e);
                        }
                    }
                }, refreshViewHolder.getTopAnimDuration());
            }
        }
        catch (Exception e)
        {
            LOG.e(e);
        }
    }

    private void setPullEndEnable()
    {
        try
        {
            Field mLoadMoreFooterViewField = BGARefreshLayout.class.getDeclaredField("mLoadMoreFooterView");
            if(null != mLoadMoreFooterViewField)
            {
                mLoadMoreFooterViewField.setAccessible(true);
                Object value = mLoadMoreFooterViewField.get(refreshLayout);
                if(null == value && null != refreshViewHolder)
                {
                    mLoadMoreFooterViewField.set(refreshLayout, refreshViewHolder.getLoadMoreFooterView());
                }
            }
        }
        catch (Exception e)
        {
            LOG.e(e);
        }
    }

    public void setOnRefreshListener(IPullToRefreshView.OnRefreshListener onRefreshListener)
    {
        this.onRefreshListener = onRefreshListener;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout)
    {
        if(null != onRefreshListener)
        {
            onRefreshListener.onPullDownToRefresh();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout)
    {
        if(null != onRefreshListener)
        {
            onRefreshListener.onPullUpToRefresh();
        }
        return true;
    }

    public void postRefreshRunnable(Runnable runnable)
    {
        try
        {
            Field mIsLoadingMoreField = BGARefreshLayout.class.getDeclaredField("mIsLoadingMore");
            mIsLoadingMoreField.setAccessible(true);
            Boolean mIsLoadingMore = (Boolean)mIsLoadingMoreField.get(refreshLayout);

            if(!mIsLoadingMore && null != refreshViewHolder)
            {
                handler.postDelayed(runnable, refreshViewHolder.getTopAnimDuration());
            }
            else
            {
                handler.post(runnable);
            }
        }
        catch (Exception e)
        {
            LOG.e(e);
        }
    }
}