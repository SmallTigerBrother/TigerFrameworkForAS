package com.mn.tiger.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class ListLayout extends LinearLayout
{

	private BaseAdapter mAdapter;
	private DataSetObserver mDataSetObserver = new DataSetObserver()
	{
		public void onChanged()
		{
			bindViews();
		}

		public void onInvalidated()
		{
			bindViews();
		}
	};

	private LinearLayout.LayoutParams mItemLayoutParams;

	public ListLayout(Context context)
	{
		super(context);
		this.setOrientation(LinearLayout.VERTICAL);
	}

	public ListLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.setOrientation(LinearLayout.VERTICAL);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public ListLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.setOrientation(LinearLayout.VERTICAL);
	}

	public void setAdapter(BaseAdapter adpater)
	{
		if (this.mAdapter != null && mDataSetObserver != null)
		{
			this.mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		this.mAdapter = adpater;
		this.mAdapter.registerDataSetObserver(mDataSetObserver);
		this.mAdapter.notifyDataSetChanged();
	}

	public BaseAdapter getAdapter()
	{
		return this.mAdapter;
	}

	public int getCount()
	{
		return this.mAdapter != null ? this.mAdapter.getCount() : 0;
	}

	private void bindViews()
	{
		if (mAdapter != null)
		{
			this.removeAllViews();
			int count = this.mAdapter.getCount();
			for (int i = 0; i < count; i++)
			{
				View itemView = this.mAdapter.getView(i, null, this);
				if (itemView != null)
				{
					if (mItemLayoutParams != null)
					{
						this.addView(itemView, mItemLayoutParams);
					}
					else
					{
						// LinearLayout.LayoutParams params =
						// (LinearLayout.LayoutParams)itemView.getLayoutParams();
						// this.addView(itemView,params);
						this.addView(itemView);
					}
				}
			}
		}
	}

	public void setItemLayoutParams(LayoutParams lp)
	{
		this.mItemLayoutParams = lp;
	}

	/**
	 * Returns the position within the adapter's data set for the first item
	 * displayed on screen.
	 * 
	 * @return The position within the adapter's data set
	 */
	public int getFirstVisiblePosition()
	{
		return 0;
	}

	/**
	 * Returns the position within the adapter's data set for the last item
	 * displayed on screen.
	 * 
	 * @return The position within the adapter's data set
	 */
	public int getLastVisiblePosition()
	{
		return getChildCount() - 1;
	}

}
