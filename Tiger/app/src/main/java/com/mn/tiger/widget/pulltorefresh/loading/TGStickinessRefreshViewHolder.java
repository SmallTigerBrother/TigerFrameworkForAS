package com.mn.tiger.widget.pulltorefresh.loading;

import android.content.Context;
import android.view.View;

import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;

/**
 * Created by Dalang on 2015/9/11.
 */
public class TGStickinessRefreshViewHolder extends BGAStickinessRefreshViewHolder
{
    private TGStickinessRefreshViewHolder(Context context, boolean isLoadingMoreEnabled)
    {
        super(context, isLoadingMoreEnabled);
    }

    public static TGStickinessRefreshViewHolder newDisableModeRefreshViewHolder(Context context)
    {
        return new TGStickinessRefreshViewHolder(context, false)
        {
            @Override
            public View getRefreshHeaderView()
            {
                return null;
            }
        };
    }

    public static TGStickinessRefreshViewHolder newBothModeRefreshViewHolder(Context context)
    {
        return new TGStickinessRefreshViewHolder(context, false);
    }

    public static TGStickinessRefreshViewHolder newPullStartModeRefreshViewHolder(Context context)
    {
        return new TGStickinessRefreshViewHolder(context, false);
    }

    public static TGStickinessRefreshViewHolder newPullEndModeRefreshViewHolder(Context context)
    {
        return new TGStickinessRefreshViewHolder(context, true)
        {
            @Override
            public View getRefreshHeaderView()
            {
                return null;
            }
        };
    }
}
