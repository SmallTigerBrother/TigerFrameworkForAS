package com.mn.tiger.widget.tab;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.CR;
import com.mn.tiger.utility.ImageLoaderUtils;

/**
 * 自定义Tab视图
 */
public class TGTabView extends LinearLayout
{
	private static final Logger LOG = Logger.getLogger(TGTabView.class);
	
	/**
	 * 所有TabItem选项
	 */
	private ArrayList<TabItem> tabItems;

	/**
	 * Tab切换事件监听器
	 */
	private OnTabChangeListener onTabChangeListener;

	/**
	 * 当前选中的Tab项的索引值
	 */
	private int currentTabIndex = -1;

	/**
	 * 适配器
	 */
	private BaseAdapter adapter;

	/**
	 * 数据观察者对象，用于更新Tabview
	 */
	private DataSetObserver dataSetObserver = new DataSetObserver()
	{
		@Override
		public void onChanged()
		{
			bindViews();
		}

		@Override
		public void onInvalidated()
		{
			bindViews();
		}
	};

	public TGTabView(Context context)
	{
		super(context);

		tabItems = new ArrayList<TGTabView.TabItem>();
	}

	public TGTabView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		tabItems = new ArrayList<TGTabView.TabItem>();
	}

	/**
	 * 获取当前选中的Tab的索引值
	 * @return
	 */
	public int getCurrentTab()
	{
		return currentTabIndex;
	}

	/**
	 * 获取TabItem
	 * @param index
	 *            TabItem的索引值
	 * @return
	 */
	public TabItem getTabItem(int index)
	{
		return tabItems.get(index);
	}

	/**
	 * 添加TabItem
	 * @param tabItem
	 */
	private void addTabItem(TabItem tabItem)
	{
		tabItems.add(tabItem.getIndex(), tabItem);
		View itemView = tabItem.getConvertView();
		this.addView(itemView);
	}

	/**
	 * 设置Tab切换事件监听器
	 * @param onTabChangeListener
	 */
	public void setOnTabChangeListener(OnTabChangeListener onTabChangeListener)
	{
		this.onTabChangeListener = onTabChangeListener;
		// 设置各个Tab的点击事件
		setTabListeners();
	}

	/**
	 * 设置所有Tab项的事件监听器
	 */
	private void setTabListeners()
	{
		// 设置各个Tab的点击事件
		for (TabItem tabItem : tabItems)
		{
			final int tabIndex = tabItem.getIndex();
			tabItem.getConvertView().setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					setSelection(tabIndex);
				}
			});
		}
	}

	/**
	 * 设置选中项
	 * @param index
	 */
	public void setSelection(int index)
	{
		if (index < 0 || getTabCount() <= 0)
		{
			return;
		}

		if (currentTabIndex == index)
		{
			return;
		}

		// 执行Tab切换事件
		if (null != onTabChangeListener)
		{
			int lastTabIndex = currentTabIndex;
			currentTabIndex = index;
			onTabChangeListener.onTabChanged(this, lastTabIndex, currentTabIndex);
		}
	}

	/**
	 * 设置适配器
	 * @param adapter
	 */
	public void setAdapter(BaseAdapter adapter)
	{
		try
		{
			// 注册数据观察者
			if (null != this.adapter && null != dataSetObserver)
			{
				this.adapter.unregisterDataSetObserver(dataSetObserver);
			}
		}
		catch (Exception e)
		{
			LOG.e(e);
		}

		this.adapter = adapter;
		this.adapter.registerDataSetObserver(dataSetObserver);
		this.adapter.notifyDataSetChanged();
	}

	private void bindViews()
	{
		if(null != adapter)
		{
			this.tabItems.clear();
			this.removeAllViews();
			TabItem tabItem = null;
			for (int i = 0; i < adapter.getCount(); i++)
			{
				tabItem = new TabItem();
				tabItem.setIndex(i);
				tabItem.setConvertView(adapter.getView(i, null, this));
				this.addTabItem(tabItem);
			}
		}

		// 设置选中项
		if (currentTabIndex != -1)
		{
			setSelection(currentTabIndex);
		}
	}

	/**
	 * 该方法的作用:获取Tabview中的所有的Item的view
	 * 
	 * @date 2014-3-10
	 * @return
	 */
	public ArrayList<TabItem> getTabItems()
	{
		return tabItems;
	}
	
	/**
	 * 获取Tab个数
	 * @return
	 */
	public int getTabCount()
	{
		return tabItems.size();
	}
	
	public BaseAdapter getAdapter()
	{
		return adapter;
	}
	
	/**
	 * Tab选中项
	 */
	public static class TabItem
	{
		/**
		 * 内容视图
		 */
		private View convertView;

		/**
		 * 索引值
		 */
		private int index;

		/**
		 * 获取内容视图
		 * @return 内容视图
		 */
		public View getConvertView()
		{
			return convertView;
		}

		/**
		 * 设置内容视图
		 * @param convertView
		 *            内容视图
		 */
		public void setConvertView(View convertView)
		{
			this.convertView = convertView;
		}

		/**
		 * 获取索引值
		 * @return 索引值
		 */
		public int getIndex()
		{
			return index;
		}

		/**
		 * 设置索引值
		 * @param index
		 *            索引值
		 */
		public void setIndex(int index)
		{
			this.index = index;
		}
	}
	
	/**
	 * 显示图片资源
	 * @param imageName image的名称，支持http，file，和资源文件名称
	 * @param imageView
	 */
	public static void displayImage(String imageName, ImageView imageView)
	{
		if(!TextUtils.isEmpty(imageName))
		{
			if(imageName.startsWith("http"))
			{
				//如果是在线文件，使用ImageLoader加载
				ImageLoaderUtils.displayImage(imageName, imageView);
			}
			else if(imageName.startsWith("file"))
			{
				//如果是本地存储中的文件，直接加载
				Drawable drawable = BitmapDrawable.createFromPath(imageName);
				if(null != drawable)
				{
					imageView.setImageDrawable(drawable);
				}
			}
			else
			{
				//其他默认为资源文件，直接填充
				int resId = CR.getDrawableId(imageView.getContext(),imageName);
				if(resId != 0)
				{
					imageView.setImageResource(resId);
				}
				else
				{
					resId = CR.getMipmapId(imageView.getContext(), imageName);
					if(resId != 0)
					{
						imageView.setImageResource(resId);
					}
					else
					{
						LOG.e("[Method:diplayImage] no resource found of name " + imageName);
					}
				}
			}
		}
	}
	
	/**
	 * Tab切换事件监听器接口
	 */
	public static interface OnTabChangeListener
	{
		/**
		 * tab切换回调方法
		 * @param tabView
		 * @param lastTabIndex 上次选中的tab的索引，若之前不存在已选中的tab，lastTabIndex 为 -1
		 * @param currentTabIndex 当前选中的tab的索引
		 */
		void onTabChanged(TGTabView tabView, int lastTabIndex, int currentTabIndex);
	}
	
	/**
	 * 布局参数
	 */
	public static class LayoutParams extends LinearLayout.LayoutParams
	{
		public LayoutParams(int width, int height)
		{
			super(width, height);
		}
		
		public LayoutParams(int width, int height, float weight)
		{
			super(width, height, weight);
		}
	}
}
