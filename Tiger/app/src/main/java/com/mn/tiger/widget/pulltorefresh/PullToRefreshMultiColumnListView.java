package com.mn.tiger.widget.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;

import com.mn.tiger.widget.pulltorefresh.library.IPullToRefreshAdapterView;
import com.mn.tiger.widget.pulltorefresh.library.IPullToRefreshListenable;
import com.mn.tiger.widget.pulltorefresh.library.PullToRefreshBase.Mode;
import com.mn.tiger.widget.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.mn.tiger.widget.pulltorefresh.loading.ILoadingFooterView;
import com.mn.tiger.widget.viewflow.MultiColumnListView;
import com.mn.tiger.widget.viewflow.internal.PLA_AbsListView;


/**
 * 拖动刷新瀑布流
 */
public class PullToRefreshMultiColumnListView extends MultiColumnListView implements IPullToRefreshAdapterView, IPullToRefreshListenable
{
	private PullToRefreshAdapterViewImp pullToRefreshViewImp;

	public PullToRefreshMultiColumnListView(Context context)
	{
		super(context);
		pullToRefreshViewImp = new PullToRefreshAdapterViewImp(this);
	}

	public PullToRefreshMultiColumnListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		pullToRefreshViewImp = new PullToRefreshAdapterViewImp(this);
	}

	@Override
	public void setAdapter(ListAdapter adapter)
	{
		pullToRefreshViewImp.addFooterViewIfNeed();
		super.setAdapter(adapter);
	}

	@Override
	public void setSuperOnSrcollListener(
			final com.mn.tiger.widget.pulltorefresh.OnScrollListener onScrollListener)
	{
		super.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(PLA_AbsListView view, int scrollState)
			{
				onScrollListener.onScrollStateChanged((IPullToRefreshAdapterView) view, scrollState);
			}

			@Override
			public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount,
								 int totalItemCount)
			{
				onScrollListener.onScroll((IPullToRefreshAdapterView) view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
	}

	@Override
	public void setMode(Mode mode)
	{
		pullToRefreshViewImp.setMode(mode);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		return pullToRefreshViewImp.onTouchEvent(ev);
	}

	@Override
	public boolean superOnTouchEvent(MotionEvent ev)
	{
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll()
	{
		pullToRefreshViewImp.computeScroll();
	}

	@Override
	public void superComputeScroll()
	{
		super.computeScroll();
	}

	@Override
	public void setOnRefreshListener(OnRefreshListener listener)
	{
		pullToRefreshViewImp.setOnRefreshListener(listener);
	}

	@Override
	public void setOnScrollListener(final OnScrollListener listener)
	{
		pullToRefreshViewImp.setOnScrollListener(new com.mn.tiger.widget.pulltorefresh.OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(IPullToRefreshAdapterView view, int scrollState)
			{
				listener.onScrollStateChanged((PLA_AbsListView) view, scrollState);
			}

			@Override
			public void onScroll(IPullToRefreshAdapterView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount)
			{
				listener.onScroll((PLA_AbsListView) view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
	}

	@Override
	public void onRefreshComplete()
	{
		pullToRefreshViewImp.stopRefreshAndLoadMore();
	}

	/**
	 * 设置底部loading视图
	 * @param mFooterView
	 */
	public void setFooterView(ILoadingFooterView mFooterView)
	{
		this.pullToRefreshViewImp.setFooterView(mFooterView);
	}

	/**
	 * 设置到达底部时是否自动加载
	 * @param autoLoadWhileEnd
	 */
	public void setAutoLoadWhileEnd(boolean autoLoadWhileEnd)
	{
		this.pullToRefreshViewImp.setAutoLoadWhileEnd(autoLoadWhileEnd);
	}
}
