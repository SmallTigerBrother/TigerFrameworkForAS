package com.mn.tiger.widget.adpter;

import butterknife.ButterKnife;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public abstract class TGExpandableChildViewHolder<T>
{
	private Activity activity;
	
	/**
	 * 搭配使用的Adapter
	 */
	@SuppressWarnings("rawtypes")
	private TGExpandableListAdapter adapter;
	
	public TGExpandableChildViewHolder()
	{
	}
	
	/**
	 * 初始化列表行视图
	 * @param convertView
	 * @return 需要返回convertView
	 */
	public View initView(View convertView)
	{
		ButterKnife.bind(this, convertView);
		return convertView;
	}
	
	/**
	 * 更新列表行的尺寸
	 * @param itemData
	 * @param position
	 */
	public void updateViewDimension(T itemData, int groupPosition,
			int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		
	}
	
	/**
	 * 填充数据
	 * @param itemData
	 * @param position
	 */
	public abstract void fillData(T itemData, int groupPosition,
			int childPosition, boolean isLastChild);

	public Activity getActivity()
	{
		return activity;
	}

	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}

	@SuppressWarnings("rawtypes")
	public TGExpandableListAdapter getAdapter()
	{
		return adapter;
	}

	@SuppressWarnings("rawtypes")
	void setAdapter(TGExpandableListAdapter adapter)
	{
		this.adapter = adapter;
	}
}
