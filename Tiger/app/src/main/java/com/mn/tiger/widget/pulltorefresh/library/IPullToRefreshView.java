package com.mn.tiger.widget.pulltorefresh.library;

import com.mn.tiger.widget.pulltorefresh.OnScrollListener;
import com.mn.tiger.widget.pulltorefresh.library.PullToRefreshBase.Mode;
import com.mn.tiger.widget.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

public interface IPullToRefreshView
{
	Context getContext();
	
	void addHeaderView(View headerView);
	
	void addFooterView(View footerView);

	ViewTreeObserver getViewTreeObserver();
	
	void invalidate();
	
	void setSuperOnSrcollListener(OnScrollListener onScrollListener);
	
	void setMode(Mode mode);
	
	boolean superOnTouchEvent(MotionEvent ev);
	
	void postInvalidate();
	
	void superComputeScroll();
	
	void setSelection(int index);
	
	void setOnRefreshListener(OnRefreshListener listener);
	
	void onRefreshComplete();
	
	int getScrollY();
	
	int getHeight();
	
	View getChildAt(int index);

}
