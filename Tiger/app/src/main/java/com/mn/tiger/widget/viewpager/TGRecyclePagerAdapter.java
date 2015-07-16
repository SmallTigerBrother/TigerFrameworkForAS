package com.mn.tiger.widget.viewpager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.mn.tiger.log.Logger;

/**
 * 支持视图重用的PagerAdapter
 * @param <T> 每页的数据类型
 */
public class TGRecyclePagerAdapter<T> extends PagerAdapter
{
	private static final Logger LOG = Logger.getLogger(TGRecyclePagerAdapter.class);
	
	/**
	 * 忽略类型
	 */
	public static final int IGNORE_ITEM_VIEW_TYPE = AdapterView.ITEM_VIEW_TYPE_IGNORE;

	private Activity activity;

	/**
	 * 保存重用视图的数组
	 */
	private RecyleArray recyleArray = null;
	
	/**
	 * 分页数据
	 */
	private List<T> pagerData;
	
	/**
	 * 分页视图ViewHolder类
	 */
	private Class<? extends TGPagerViewHolder<T>> viewHolderClazz;
	
	/**
	 * 默认的ViewHolder，用来获取ViewType
	 */
	private TGPagerViewHolder<T> pagerViewHolder;

	public TGRecyclePagerAdapter(Activity activity, List<T> pagerData, 
			Class<? extends TGPagerViewHolder<T>> viewHolderClazz)
	{
		this.activity = activity;
		
		this.pagerData = new ArrayList<T>();
		if(null != pagerData)
		{
			this.pagerData.addAll(pagerData);
		}
		
		this.pagerData = pagerData;
		this.viewHolderClazz = viewHolderClazz;
		recyleArray = new RecyleArray();
		
		if(null != viewHolderClazz)
		{
			try
			{
				pagerViewHolder = viewHolderClazz.newInstance();
			}
			catch (Exception e)
			{
				LOG.e(e);
			}
		}
		else
		{
			LOG.w("viewHolderClazz is null, please check your code if you do not want that");
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		int viewType = getViewType(position);
		if(null != pagerViewHolder)
		{
			viewType = pagerViewHolder.getItemViewType(pagerData, position);
		}
		
		//查找ViewPager中是否已存在子视图
		View view = recyleArray.getScrapView(viewType);
		try
		{
			if(null == view)
			{
				view = initPageView(viewType);
			}
			//填充数据
			fillPageData(position, viewType, pagerData, view);
			
			container.addView(view);
		}
		catch (Exception e)
		{
			LOG.e(e);
		}
		
		return view;
	}

	/**
	 * 获取视图类型
	 * @param position
	 * @return
	 */
	public int getViewType(int position)
	{
		return IGNORE_ITEM_VIEW_TYPE;
	}
	
	/**
	 * 初始化PageView
	 * @param viewType
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected View initPageView(int viewType) throws InstantiationException, IllegalAccessException
	{
		//初始化viewholder
		TGPagerViewHolder<T> viewHolder = viewHolderClazz.newInstance();
		viewHolder.setActivity(activity);
		viewHolder.setPagerAdapter(this);
		//初始化子视图
		View view = viewHolder.initPage(viewType);
		view.setTag(viewHolder);
		return view;
	}
	
	/**
	 * 填充分页数据
	 * @param position
	 * @param viewType
	 * @param pageData
	 * @param viewOfPage
	 */
	@SuppressWarnings("unchecked")
	protected void fillPageData(int position, int viewType, List<T> pageData, View viewOfPage)
	{
		TGPagerViewHolder<T> viewHolder = (TGPagerViewHolder<T>) viewOfPage.getTag();
		if(null != pagerData && pagerData.size() > position)
		{
			viewHolder.fillData(pagerData.get(position), position,viewType);
		}
		else
		{
			viewHolder.fillData(null, position, viewType);
		}
	}

	@Override
	public final void destroyItem(ViewGroup container, int position, Object object)
	{
		View view = (View) object;
		
		//从container中移除
		container.removeView(view);
		
		//加入到已遗弃视图数组中
		int viewType = IGNORE_ITEM_VIEW_TYPE;
		if(null != pagerViewHolder)
		{
			viewType = pagerViewHolder.getItemViewType(pagerData, position);
		}
		recyleArray.addScrapView(view, viewType);
	}

	@Override
	public final boolean isViewFromObject(View view, Object object)
	{
		return view == object;
	}

	protected Activity getActivity()
	{
		return activity;
	}
	
	@Override
	public int getCount()
	{
		if(null != pagerData)
		{
			return pagerData.size();
		}
		return 0;
	}
	
	/**
	 * 获取分页数据
	 * @return
	 */
	public List<T> getPagerData()
	{
		return pagerData;
	}
	
	/**
	 * 保存重用视图的数组
	 */
	private class RecyleArray
	{
		/**
		 * 保存多种ViewType重用视图的数组
		 */
		private SparseArray<ArrayList<View>> allScrapViewArrays;
		
		public RecyleArray()
		{
			allScrapViewArrays = new SparseArray<ArrayList<View>>();
		}
		
		/**
		 * 添加弃用的视图
		 * @param view
		 * @param viewType
		 */
		public void addScrapView(View view, int viewType)
		{
			//根据ViewType获取对应的数组
			ArrayList<View> scrapArray = allScrapViewArrays.get(viewType);
			//初始化对应类型的view数组
			if(null == scrapArray)
			{
				scrapArray = new ArrayList<View>();
				allScrapViewArrays.put(viewType, scrapArray);
			}
			scrapArray.add(view);
		}
		
		/**
		 * 获取弃用的视图
		 * @param viewType
		 * @return
		 */
		public View getScrapView(int viewType)
		{
			//根据viewType获取view列表
			ArrayList<View> viewArray = getScrapViewArrayByType(viewType);
			//查找该类型下的未使用的子视图
			if(viewArray.size() == 0)
			{
				return null;
			}
			else
			{
				View view;
				for(int i = 0; i < viewArray.size(); i++)
				{
					//若view不为null，返回
					view = viewArray.get(i);
					if(null != view)
					{
						viewArray.remove(view);
						return view;
					}
					else
					{
						//清楚null对象
						viewArray.remove(i);
					}
				}
				
				return null;
			}
		}
		
		/**
		 * 根据ViewType获取弃用View的数组
		 */
		private ArrayList<View> getScrapViewArrayByType(int viewType)
		{
			ArrayList<View> scrapArray = allScrapViewArrays.get(viewType);
			if(null == scrapArray)
			{
				scrapArray = new ArrayList<View>();
				allScrapViewArrays.put(viewType, scrapArray);
			}
			return scrapArray;
		}
	}
}
