package com.mn.tiger.widget.pulltorefresh;

import com.mn.tiger.log.Logger;
import com.mn.tiger.widget.pulltorefresh.library.IPullToRefreshView;

import java.lang.reflect.Field;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

/**
 * Created by Dalang on 2015/9/11.
 */
class PullToRefreshViewImp
{
    private static final Logger LOG = Logger.getLogger(PullToRefreshViewImp.class);

    private BGARefreshLayout refreshLayout;

    private BGARefreshViewHolder refreshViewHolder;

    private Field mRefreshHeaderViewField;

    PullToRefreshViewImp(BGARefreshLayout layout)
    {
        this.refreshLayout = layout;
    }

    public void setMode(IPullToRefreshView.Mode mode)
    {
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
                setPullStartDisable();
                break;
        }
    }

    public void setRefreshViewHolder(BGARefreshViewHolder refreshViewHolder)
    {
        this.refreshViewHolder = refreshViewHolder;
    }

    private void setPullStartDisable()
    {
        if(null == mRefreshHeaderViewField)
        {
            try
            {
                mRefreshHeaderViewField = BGARefreshLayout.class.getField("mRefreshHeaderViewField");
                mRefreshHeaderViewField.setAccessible(true);
                mRefreshHeaderViewField.set(refreshLayout, null);
            }
            catch (Exception e)
            {
                LOG.e(e);
            }
        }
    }

    private void setPullStartEnable()
    {
        refreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    private void setPullEndDisable()
    {
        if(null != refreshViewHolder)
        {
            try
            {
                Field mIsLoadingMoreEnabledField = BGARefreshViewHolder.class.getField("mIsLoadingMoreEnabled");
                mIsLoadingMoreEnabledField.setAccessible(true);
                mIsLoadingMoreEnabledField.set(refreshViewHolder, true);
            }
            catch (Exception e)
            {
                LOG.e(e);
            }
        }
    }

    private void setPullEndEnable()
    {
        if(null != refreshViewHolder)
        {
            try
            {
                Field mIsLoadingMoreEnabledField = BGARefreshViewHolder.class.getField("mIsLoadingMoreEnabled");
                mIsLoadingMoreEnabledField.setAccessible(true);
                mIsLoadingMoreEnabledField.set(refreshViewHolder, true);
            }
            catch (Exception e)
            {
                LOG.e(e);
            }
        }
    }
}
