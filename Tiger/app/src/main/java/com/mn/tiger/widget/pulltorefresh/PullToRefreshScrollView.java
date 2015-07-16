/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.mn.tiger.widget.pulltorefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.mn.tiger.R;
import com.mn.tiger.widget.pulltorefresh.library.OverscrollHelper;
import com.mn.tiger.widget.pulltorefresh.library.PullToRefreshBase;

public class PullToRefreshScrollView extends PullToRefreshBase<ScrollView>
{
	/**
	 * 拖动刷新滚动监听器
	 */
	private onPullScrollListener onPullScrollListener;
	
	/**
	 * 已拖动到最顶部
	 */
	private boolean hasScrollToStart = false;
	
	/**
	 * 已拖动到最底部
	 */
	private boolean hasScrollToEnd = false;
	
	public PullToRefreshScrollView(Context context)
	{
		super(context);
	}

	public PullToRefreshScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullToRefreshScrollView(Context context, Mode mode)
	{
		super(context, mode);
	}

	public PullToRefreshScrollView(Context context, Mode mode, AnimationStyle style)
	{
		super(context, mode, style);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection()
	{
		return Orientation.VERTICAL;
	}

	@Override
	protected ScrollView createRefreshableView(Context context, AttributeSet attrs)
	{
		ScrollView scrollView;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD)
		{
			scrollView = new InternalScrollViewSDK9(context, attrs);
		}
		else
		{
			scrollView = new ScrollView(context, attrs);
		}

		scrollView.setId(R.id.scrollview);
		
		return scrollView;
	}

	@Override
	protected boolean isReadyForPullStart()
	{
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullEnd()
	{
		View scrollViewChild = mRefreshableView.getChildAt(0);
		if (null != scrollViewChild)
		{
			return mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
		}
		return false;
	}
	
	/**
	 * 设置拖动滚动监听器
	 * @param onPullScrollListener
	 */
	public void setOnPullScrollListener(onPullScrollListener onPullScrollListener)
	{
		this.onPullScrollListener = onPullScrollListener;
	}
	
	@TargetApi(9)
	final class InternalScrollViewSDK9 extends ScrollView
	{
		public InternalScrollViewSDK9(Context context, AttributeSet attrs)
		{
			super(context, attrs);
		}
		
		@Override
		protected void onScrollChanged(int l, int t, int oldl, int oldt)
		{
			super.onScrollChanged(l, t, oldl, oldt);
			//如果已经拖动到顶部，回调onScrollToStart()方法
			if(getScrollY() == 0)
			{
				//回调监听器接口，返回值为true表示消化事件
				if(null != onPullScrollListener)
				{
					if(!hasScrollToStart)
					{
						hasScrollToStart = onPullScrollListener.onScrollToStart();
					}
				}
			}
			else if(getScrollY() + getHeight() >=  computeVerticalScrollRange())//如果已经拖动到顶部，回调onScrollToEnd()方法
			{
				//回调监听器接口，返回值为true表示消化事件
				if(null != onPullScrollListener)
				{
					if(!hasScrollToEnd)
					{
						hasScrollToEnd = onPullScrollListener.onScrollToEnd();
					}
				}
			}
			else
			{
				//否则设置控制参数为false
				hasScrollToStart = false;
				hasScrollToEnd = false;
			}
		}
		
		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
		{

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshScrollView.this, deltaX, scrollX, deltaY, scrollY,
					getScrollRange(), isTouchEvent);

			return returnValue;
		}

		/**
		 * Taken from the AOSP ScrollView source
		 */
		private int getScrollRange()
		{
			int scrollRange = 0;
			if (getChildCount() > 0)
			{
				View child = getChildAt(0);
				scrollRange = Math.max(0, child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
			}
			return scrollRange;
		}
	}
	
	/**
	 * 拖动滚动监听器
	 */
	public static interface onPullScrollListener
	{
		/**
		 * 拖动到顶部的回调方法
		 * @return 是否处理该事件
		 */
		boolean onScrollToStart();
		
		/**
		 * 拖动到底部的回调方法
		 * @return 是否处理该事件
		 */
		boolean onScrollToEnd();
	}
}
