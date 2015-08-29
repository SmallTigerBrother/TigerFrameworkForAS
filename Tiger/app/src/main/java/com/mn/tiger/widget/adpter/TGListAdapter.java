package com.mn.tiger.widget.adpter;

import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 该类作用及功能说明
 * 基础列表适配器类，实现适配器的基本功能
 *
 * @version V2.0
 * @date 2012-12-31
 * @see JDK1.6,android-8
 */
public class TGListAdapter<T> extends BaseAdapter
{
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();

	/**
	 * 运行环境
	 */
	private Context context;

	/**
	 * 列表填充数据
	 */
	private List<T> items = null;

	/**
	 * 列表行视图layoutId
	 */
	private int convertViewLayoutId;

	/**
	 * viewholder类，用于视图重用，初始化列表行和填充列表行数据
	 */
	private Class<? extends TGViewHolder<T>> viewHolderClass;

	private Handler handler;

	/**
	 * 可用于adapter、viewholder之间传值
	 */
	private Object tag;

	private boolean strictlyReuse = false;

	private SparseArray<View> convertViews;

	/**
	 * @param context
	 * @param items               列表填充数据
	 * @param convertViewLayoutId 列表行视图layoutId
	 * @param viewHolderClass     ViewHolder类名
	 */
	public TGListAdapter(Context context, List<T> items, int convertViewLayoutId,
						 Class<? extends TGViewHolder<T>> viewHolderClass)
	{
		this.context = context;
		this.items = new ArrayList<T>();
		if (null != items)
		{
			this.items.addAll(items);
		}

		this.convertViewLayoutId = convertViewLayoutId;
		this.viewHolderClass = viewHolderClass;

		//根据内存容量判断是否启用严格重用
		setStrictlyReuse(false);
	}

	public void setStrictlyReuse(boolean strictlyReuse)
	{
		this.strictlyReuse = strictlyReuse;
		if(!strictlyReuse)
		{
			convertViews = new SparseArray<View>();
		}
		else
		{
			convertViews.clear();
			convertViews = null;
		}
	}

	/**
	 * @see BaseAdapter#getCount()
	 */
	@Override
	public int getCount()
	{
		return items.size();
	}

	/**
	 * @see BaseAdapter#getItem(int)
	 */
	@Override
	public Object getItem(int position)
	{
		return items.get(position);
	}

	/**
	 * @see BaseAdapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	/**
	 * @see BaseAdapter#getView(int, View, ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(!strictlyReuse)
		{
			convertView = convertViews.get(position);
		}

		if (null == convertView)
		{
			convertView = initView(parent, position);

			if(!strictlyReuse)
			{
				convertViews.put(position, convertView);
				fillData(position, convertView, parent);
			}
		}
		else
		{
			if(strictlyReuse)
			{
				fillData(position, convertView, parent);
			}
		}

		return convertView;
	}

	/**
	 * 初始化convertView（仅在无法重用视图时调用）
	 *
	 * @return
	 */
	protected View initView(ViewGroup parent, int position)
	{
		TGViewHolder<T> viewHolder = null;
		View convertView = null;
		if (convertViewLayoutId > 0)
		{
			try
			{
				convertView = LayoutInflater.from(context).inflate(convertViewLayoutId, null);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		viewHolder = initViewHolder();
		viewHolder.setLayoutId(convertViewLayoutId);
		convertView = viewHolder.initView(convertView, parent, position);
		convertView.setTag(viewHolder);

		return convertView;
	}

	/**
	 * 填充列表行数据（每次调用getView时都会调用）
	 *
	 * @param position
	 * @param convertView
	 * @param parent
	 */
	@SuppressWarnings("unchecked")
	protected void fillData(int position, View convertView, ViewGroup parent)
	{
		TGViewHolder<T> viewHolder = (TGViewHolder<T>) convertView.getTag();
		//填充列表行数据
		viewHolder.fillData(parent, convertView, items.get(position), position);

		//更新列表行尺寸
		viewHolder.updateViewDimension(parent, convertView, items.get(position), position);
	}

	/**
	 * 初始化ViewHolder
	 *
	 * @return
	 */
	protected TGViewHolder<T> initViewHolder()
	{
		TGViewHolder<T> viewHolder = null;
		try
		{
			viewHolder = viewHolderClass.newInstance();
			viewHolder.setContext(context);
			viewHolder.setAdapter(this);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		return viewHolder;
	}

	/**
	 * 该方法的作用:更新列表数据
	 *
	 * @param data 列表数据
	 * @date 2013-1-17
	 */
	public void updateData(List<T> data)
	{
		if (null != data)
		{
			if(this.items != data)
			{
				this.items.clear();
				this.items.addAll(data);
			}
			convertViews.clear();
			notifyDataSetChanged();
		}
	}

	/**
	 * 更新列表行数据
	 *
	 * @param data
	 */
	public void updateData(T[] data)
	{
		if (null != data)
		{
			this.items.clear();
			this.items.addAll(Arrays.asList(data));
			convertViews.clear();
			notifyDataSetChanged();
		}
	}

	/**
	 * 向列表行追加数据
	 *
	 * @param data
	 */
	public void appendData(List<T> data)
	{
		if (null != data)
		{
			this.items.addAll(data);
			notifyDataSetChanged();
		}
	}

	/**
	 * 向列表行追加数据
	 *
	 * @param data
	 */
	public void appendData(T[] data)
	{
		if (null != data)
		{
			this.items.addAll(Arrays.asList(data));
			notifyDataSetChanged();
		}
	}

	/**
	 * 该方法的作用:
	 * 获取列表数据
	 *
	 * @return
	 * @date 2014年2月10日
	 */
	public List<T> getListItems()
	{
		return this.items;
	}

	/**
	 * 获取列表第一个元素
	 *
	 * @return 若列表无数据，返回null
	 */
	public T getFirstItem()
	{
		if (!items.isEmpty())
		{
			return items.get(0);
		}
		return null;
	}

	/**
	 * 获取列表最后一个元素
	 *
	 * @return 若列表无数据，返回null
	 */
	public T getLastItem()
	{
		if (!items.isEmpty())
		{
			return items.get(items.size() - 1);
		}
		return null;
	}

	/**
	 * 删除列表行
	 *
	 * @param position 列表行位置
	 */
	public void removeItem(int position)
	{
		if (items.size() > position && position >= 0)
		{
			items.remove(position);
			convertViews.remove(position);
			notifyDataSetChanged();
		}
	}

	/**
	 * 删除列表行
	 *
	 * @param item 列表行数据
	 */
	public void removeItem(T item)
	{
		if (items.contains(item))
		{
			items.remove(item);
			convertViews.remove(items.indexOf(item));
			notifyDataSetChanged();
		}
	}

	/**
	 * 清除所有数据
	 */
	public void removeAll()
	{
		items.clear();
		convertViews.clear();
		notifyDataSetChanged();
	}

	protected Context getContext()
	{
		return context;
	}

	public Handler getHandler()
	{
		return handler;
	}

	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	public Object getTag()
	{
		return tag;
	}

	public void setTag(Object tag)
	{
		this.tag = tag;
	}
}
