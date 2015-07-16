package com.mn.tiger.widget.viewpager;

import com.mn.tiger.utility.DisplayUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ScrollPageView extends ScrollView
{
	public ScrollPageView(Context context)
	{
		super(context);
	}

	public ScrollPageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ScrollPageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canScrollVertically(int direction)
	{
		final int offset = computeVerticalScrollOffset();
		final int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
		if (range < DisplayUtils.dip2px(getContext(), 4))
		{
			return false;
		}

		if (direction < 0)
		{
			return offset > 0;
		}
		else
		{
			return offset < range - 1;
		}
	}

	@Override
	public boolean canScrollHorizontally(int direction)
	{
		final int offset = computeHorizontalScrollOffset();
		final int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
		if (range < DisplayUtils.dip2px(getContext(), 4))
		{
			return false;
		}
			
		if (direction < 0)
		{
			return offset > 0;
		}
		else
		{
			return offset < range - 1;
		}
	}
}
