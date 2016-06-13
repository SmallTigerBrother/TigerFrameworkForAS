package com.mn.tiger.widget.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

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
	public boolean onTouchEvent(MotionEvent motionEvent)
	{
		if(canScroll)
		{
			return super.onTouchEvent(motionEvent);
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent motionEvent)
	{
		if(canScroll)
		{
			return super.onInterceptTouchEvent(motionEvent);
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

    /**
     * 设置页面滚动时间间隔
     * @param duration
     */
    public void setScrollDuration(int duration)
    {
        try
        {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(this.getContext(),duration,
                    new AccelerateInterpolator());
            field.set(this, scroller);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

	private class FixedSpeedScroller extends Scroller
	{
		private int mDuration = 1500;

		public FixedSpeedScroller(Context context, int duration)
		{
			super(context);
            this.mDuration = duration;
		}

		public FixedSpeedScroller(Context context, int duration, Interpolator interpolator)
		{
			super(context, interpolator);
            this.mDuration = duration;
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration)
		{
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy)
		{
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}
	}
}
