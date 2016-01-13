package com.mn.tiger.widget.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TGViewPager extends ViewPager
{
	private boolean canScroll = true;
	
	public TGViewPager(Context context)
	{
		super(context);
	}

	public TGViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent arg0)
	{
		if(canScroll)
		{
			return super.onTouchEvent(arg0);
		}
		return false;
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if(canScroll)
		{
			return super.onInterceptTouchEvent(ev);
		}
		return false;
	}

	/**
	 * 设置是否支持滚动
	 * @param canScroll
	 */
	public void setCanScroll(boolean canScroll)
	{
		this.canScroll = canScroll;
	}
}
