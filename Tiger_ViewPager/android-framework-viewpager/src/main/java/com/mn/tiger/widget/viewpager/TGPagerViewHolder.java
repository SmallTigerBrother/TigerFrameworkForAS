package com.mn.tiger.widget.viewpager;

import android.app.Activity;
import android.view.View;

import java.util.List;

/**
 * TGRecyclePagerAdapter使用的ViewHolder
 * @param <T> 数据类型
 */
public abstract class TGPagerViewHolder<T>
{
	private Activity activity;
	
	/**
	 * 适配器
	 */
	private TGRecyclePagerAdapter<T> pagerAdapter;
	
	public TGPagerViewHolder()
	{
		
	}
	
	/**
	 * 初始化视图
	 * @param viewType 视图类型
	 * @return
	 */
	public abstract View initPage(int viewType);
	
	/**
	 * 填充数据
	 * @param itemData 数据
	 * @param position 页面位置
	 * @param viewType 视图类型
	 */
	public abstract void fillData(T itemData, int position, int viewType);
	
	/**
	 * 根据位置获取ViewType
	 * @param pagerData 分页数据
	 * @param position 位置
	 * @return 默认返回 IGNORE_ITEM_VIEW_TYPE
	 */
	public int getItemViewType(List<T> pagerData, int position)
	{
		return TGRecyclePagerAdapter.IGNORE_ITEM_VIEW_TYPE;
	}
	
	void setPagerAdapter(TGRecyclePagerAdapter<T> pagerAdapter)
	{
		this.pagerAdapter = pagerAdapter;
	}
	
	protected TGRecyclePagerAdapter<T> getPagerAdapter()
	{
		return pagerAdapter;
	}
	
	void setActivity(Activity activity)
	{
		this.activity = activity;
	}
	
	protected Activity getActivity()
	{
		return activity;
	}

}
