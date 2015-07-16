package com.mn.tiger.widget.adpter;

import java.util.List;

import android.app.Activity;

/**
 * 用ListView伪装的GridView使用的Adapter
 * @author Dalang
 * @param <T>
 */
public class TGGridListAdapter<T> extends TGListAdapter<T>
{
	/**
	 * 列数
	 */
	private int columnNum = 1;
	
	/**
	 * @param activity
	 * @param items 所有数据
	 * @param convertViewLayoutId 每个子GridView的layout布局
	 * @param viewHolderClass TGGridListViewHolder子类
	 * @param columnNum 列数
	 */
	public TGGridListAdapter(Activity activity, List<T> items, int convertViewLayoutId,
			Class<? extends TGViewHolder<T>> viewHolderClass, int columnNum)
	{
		super(activity, items, convertViewLayoutId, viewHolderClass);
		this.columnNum = columnNum;
	}
	
	@Override
	public int getCount()
	{
		//重新计算行数
		return super.getCount() % columnNum == 0 ?  
				super.getCount() / columnNum : super.getCount() / columnNum + 1;
	}
	
	/**
	 * 设置列表行数
	 * @param columnNum
	 */
	public void setColumnNum(int columnNum)
	{
		this.columnNum = columnNum;
	}
	
	/**
	 * 获取列数
	 * @return
	 */
	public int getColumnNum()
	{
		return columnNum;
	}
	
	/**
	 * 获取行数据
	 * @return
	 */
	public int getRowNum()
	{
		return this.getCount();
	}
	
	/**
	 * 获取Grid数据
	 * @param rowIndex 行索引
	 * @param columnIndex 列索引
	 * @return
	 */
	public Object getItem(int rowIndex, int columnIndex)
	{
		int position = rowIndex * columnNum + columnIndex;
		if(position < super.getCount())
		{
			return super.getItem(position);
		}
		else 
		{
			return null;
		}
	}
}
