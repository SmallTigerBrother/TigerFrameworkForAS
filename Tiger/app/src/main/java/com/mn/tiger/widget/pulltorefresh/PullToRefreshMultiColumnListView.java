package com.mn.tiger.widget.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.mn.tiger.log.Logger;
import com.mn.tiger.widget.pulltorefresh.library.IPullToRefreshView;
import com.mn.tiger.widget.recyclerview.TGRecyclerView;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;


/**
 * 拖动刷新瀑布流
 */
public class PullToRefreshMultiColumnListView extends BGARefreshLayout implements IPullToRefreshView
{
	private static Logger LOG = Logger.getLogger(PullToRefreshMultiColumnListView.class);

	private TGRecyclerView recyclerView;

	private PullToRefreshViewImp pullToRefreshViewImp;

	public PullToRefreshMultiColumnListView(Context context)
	{
		super(context);
		initRecyclerView();

		pullToRefreshViewImp = new PullToRefreshViewImp(this);
	}

	public PullToRefreshMultiColumnListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initRecyclerView();

		pullToRefreshViewImp = new PullToRefreshViewImp(this);
	}

	private void initRecyclerView()
	{
		recyclerView = new TGRecyclerView(getContext());
		recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.weight = 1;
		this.addView(recyclerView, layoutParams);
	}

	@Override
	public void setRefreshViewHolder(BGARefreshViewHolder refreshViewHolder)
	{
		pullToRefreshViewImp.setRefreshViewHolder(refreshViewHolder);
		super.setRefreshViewHolder(refreshViewHolder);
	}

	public void setColumnCount(int columnCount)
	{
		((StaggeredGridLayoutManager)recyclerView.getLayoutManager()).setSpanCount(columnCount);
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
		pullToRefreshViewImp.setMode(mode);
	}

	@Override
	public void setOnRefreshListener(OnRefreshListener listener)
	{

	}

	@Override
	public void onRefreshComplete()
	{
		this.endRefreshing();
		this.endLoadingMore();
	}

}
