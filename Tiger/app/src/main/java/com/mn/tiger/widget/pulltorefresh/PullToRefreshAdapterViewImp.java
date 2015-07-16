package com.mn.tiger.widget.pulltorefresh;

import com.mn.tiger.widget.pulltorefresh.library.IPullToRefreshAdapterView;
import com.mn.tiger.widget.pulltorefresh.library.IPullToRefreshView;


/**
 * 拖动刷新AdapterView实现类（封装拖动、刷新等功能）
 */
class PullToRefreshAdapterViewImp extends PullToRefreshViewImp
{
	public PullToRefreshAdapterViewImp(IPullToRefreshAdapterView view)
	{
		super((IPullToRefreshView) view);
	}

	@Override
	public boolean isReadPullStart()
	{
		return ((IPullToRefreshAdapterView)getView()).getFirstVisiblePosition() == 0;
	}
	
	@Override
	public boolean isReadPullEnd()
	{
		return ((IPullToRefreshAdapterView)getView()).getLastVisiblePosition() == getTotalItemCount() - 1;
	}
	
}
