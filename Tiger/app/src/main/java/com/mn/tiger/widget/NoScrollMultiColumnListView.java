package com.mn.tiger.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;

import com.mn.tiger.widget.viewflow.MultiColumnListView;

/**
 * 不支持滚动的瀑布流
 *
 */
public class NoScrollMultiColumnListView extends MultiColumnListView
{
	/**
	 * 多列的高度数组
	 */
	private int[] columnHeights;

	public NoScrollMultiColumnListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		//每次测量时重置高度数据
		columnHeights = new int[mColumnNumber];
		//测量所有子视图的高度
		int height = measureHeightOfChildren(widthMeasureSpec, 0, NO_POSITION, 0, -1);
		
		//测量列表行
		super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
	}
	
	@Override
	protected int measureHeightOfChildren(int widthMeasureSpec, int startPosition, int endPosition,
			int maxHeight, int disallowPartialChildPosition)
	{
		final ListAdapter adapter = mAdapter;
		if (adapter == null)
		{
			return mListPadding.top + mListPadding.bottom;
		}

		// The previous height value that was less than maxHeight and contained
		// no partial children
		int i;
		View child;

		// mItemCount - 1 since endPosition parameter is inclusive
		endPosition = (endPosition == NO_POSITION) ? adapter.getCount() - 1 : endPosition;
		final boolean[] isScrap = mIsScrap;

		for (i = startPosition; i <= endPosition; ++i)
		{
			child = obtainView(i, isScrap);

			measureScrapChild(child, i, widthMeasureSpec);

			//将新的子视图添加到列高度最小的一列中
			int shortestColumnIndex = getShortestColumn();
			columnHeights[shortestColumnIndex] = child.getMeasuredHeight() + columnHeights[shortestColumnIndex];
		}

		//返回最高的一列的高度
		return getLongestColumnHeight();
	}
	
	/**
	 * 获取高度最小的一列的索引值
	 * @return
	 */
	private int getShortestColumn()
	{
		int index = 0;
		int columnHeight = columnHeights[0];
		for (int i = 1; i < mColumnNumber; i++)
		{
			if(columnHeight > columnHeights[i])
			{
				columnHeight = columnHeights[i];
				index = i;
			}
		}
		
		return index;
	}
	
	/**
	 * 获取高度最高的一列的高度
	 * @return
	 */
	private int getLongestColumnHeight()
	{
		int columnHeight = columnHeights[0];
		for (int i = 1; i < mColumnNumber; i++)
		{
			columnHeight = columnHeight >= columnHeights[i] ? columnHeight : columnHeights[i];
		}
		
		return columnHeight;
	}

	@Override
	protected boolean recycleOnMeasure()
	{
		//不允许测量时进行视图重用，防止出现首行显示错误
		return false;
	}
}
