package com.mn.tiger.widget.adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.ButterKnife;


/**
 * 用ListView伪装的GridView使用的ViewHolder
 * @author Dalang
 * @param <T>
 */
public abstract class TGGridListViewHolder<T> extends TGViewHolder<T>
{
	@Override
	public View initView(ViewGroup parent,int viewType)
	{
		int columnNum = ((TGGridListAdapter<T>)getAdapter()).getColumnNum();
		
		//默认使用LinearLayout作为convertView
		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		
		View childGridView;
		for(int i = 0; i < columnNum; i++)
		{
			childGridView = initChildGridView(i, linearLayout, parent);
			linearLayout.addView(childGridView, layoutParams);
		}
		
		return linearLayout;
	}
	
	/**
	 * 初始化子Grid视图
	 * @param columIndex 列索引
	 * @param rowLayout 行Layout
	 * @param parent listview
	 * @return
	 */
	protected View initChildGridView(int columIndex ,LinearLayout rowLayout, ViewGroup parent)
	{
		//初始化子Grid视图
		return LayoutInflater.from(getContext()).inflate(getLayoutId(), null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void fillData(ViewGroup parent, View convertView, T itemData, int position, int viewType)
	{
		TGGridListAdapter<T> listAdapter = (TGGridListAdapter<T>) getAdapter();
 		int columnNum = listAdapter.getColumnNum();
		
		LinearLayout rowLayout = (LinearLayout) convertView;
		View childGridView;
		//遍历各个子视图，并填充数据
		for(int i = 0; i< columnNum; i++)
		{
			childGridView = rowLayout.getChildAt(i);
			T childData = (T) listAdapter.getItem(position, i);
			if(null != childData)
			{
				//若有数据，则显示视图，并设置为enable
				childGridView.setVisibility(View.VISIBLE);
				childGridView.setEnabled(true);
				
				//再需要时进行注入赋值
				ButterKnife.bind(this, childGridView);
				this.fillData(position, i, childData, childGridView, rowLayout, parent);
			}
			else
			{
				//若无数据，则隐藏视图，并设置为disable
				childGridView.setVisibility(View.INVISIBLE);
				childGridView.setEnabled(false);
			}
		}
	}
	
	/**
	 * 填充每个子GridView
	 * @param rowIndex 行索引
	 * @param columnIndex 列索引
	 * @param itemData 数据
	 * @param childGridView 子视图
	 * @param rowLayout 行Layout
	 * @param parent ListView
	 */
	protected abstract void fillData(int rowIndex, int columnIndex, T itemData,
			View childGridView, LinearLayout rowLayout, ViewGroup parent);
	
	@SuppressWarnings("unchecked")
	@Override
	protected void updateViewDimension(ViewGroup parent, View convertView, T itemData,
			int position, int viewType)
	{
		TGGridListAdapter<T> listAdapter = (TGGridListAdapter<T>) getAdapter();
 		int columnNum = listAdapter.getColumnNum();
		LinearLayout rowLayout = (LinearLayout) convertView;
		View childGridView;
		//遍历更新各个子视图
		for(int i = 0; i < columnNum; i++)
		{
			childGridView = rowLayout.getChildAt(i);
			T childData = (T) listAdapter.getItem(position, i);
			if(null != childData)
			{
				updateViewDimension(parent, (LinearLayout) convertView, 
						childGridView, childData, position, i);
			}
		}
	}
	
	/**
	 * 更新各个子视图大小（可能会影响整行的高度）
	 * @param parent listview
	 * @param rowLayout 行Layout
	 * @param childGridView 子视图
	 * @param itemData 数据
	 * @param rowIndex 行索引
	 * @param columnIndex 列索引
	 */
	protected void updateViewDimension(ViewGroup parent, LinearLayout rowLayout, 
			View childGridView, T itemData, int rowIndex, int columnIndex)
	{
		
	}
}
