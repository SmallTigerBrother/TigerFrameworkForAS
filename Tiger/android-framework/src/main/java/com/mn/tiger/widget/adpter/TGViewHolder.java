package com.mn.tiger.widget.adpter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import butterknife.ButterKnife;

/**
 * 自定义的ViewHolder
 */
public abstract class TGViewHolder<T>
{
	private Context context;

	/**
	 * 搭配使用的Adapter
	 */
	private TGListAdapter<T> adapter;

	protected ViewGroup parent;

	protected View convertView;

	public TGViewHolder()
	{
	}

	/**
	 * 初始化列表行视图
	 * @return 需要返回convertView
	 */
	public View initView(ViewGroup parent, int viewType)
	{
		View convertView = null;
		if (getLayoutId() > 0)
		{
			try
			{
				convertView = LayoutInflater.from(context).inflate(getLayoutId(), null);
				ButterKnife.bind(this, convertView);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		return convertView;
	}


	/**
	 * 获取匹配的Adapter
	 * @return
	 */
	public TGListAdapter<T> getAdapter()
	{
		return adapter;
	}

	/**
	 * 设置匹配的Adapter
	 * @param adapter
	 */
	void setAdapter(TGListAdapter<T> adapter)
	{
		this.adapter = adapter;
	}

	/**
	 * 更新列表行的尺寸
	 * @param itemData
	 * @param position
	 */
	protected void updateViewDimension(T itemData, int position, int viewType)
	{

	}

	/**
	 * 填充数据
	 * @param itemData
	 * @param position
	 */
	public abstract void fillData(T itemData, int position, int viewType);

	protected int getItemViewType(int position)
	{
		return BaseAdapter.IGNORE_ITEM_VIEW_TYPE;
	}

	protected Context getContext()
	{
		return context;
	}

	void setContext(Context context)
	{
		this.context = context;
	}

	protected abstract int getLayoutId();

	protected int getColor(int resId)
	{
		return getContext().getResources().getColor(resId);
	}

	protected Drawable getDrawable(int resId)
	{
		return getContext().getResources().getDrawable(resId);
	}

	protected int getDimensionPixelSize(int resId)
	{
		return getContext().getResources().getDimensionPixelSize(resId);
	}
}
