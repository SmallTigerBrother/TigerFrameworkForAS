package com.mn.tiger.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class NoScrollListView extends ListView
{

	public NoScrollListView(Context context)
	{
		super(context);
	}

	public NoScrollListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{

		int expend = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

		super.onMeasure(widthMeasureSpec, expend);
	}

}
