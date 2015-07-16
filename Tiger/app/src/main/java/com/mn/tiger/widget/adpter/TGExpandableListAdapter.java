package com.mn.tiger.widget.adpter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 该类作用及功能说明
 * 基础可展开列表适配器类，GParam参数为Group数据，CParam参数为Child数据
 * @version V2.0
 * @see JDK1.6,android-8
 * @date 2012-12-28
 */
public abstract class TGExpandableListAdapter<GroupParam,ChildParam> extends android.widget.BaseExpandableListAdapter 
{
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();
	
	/**
	 * 运行环境
	 */
	private Activity activity;
	/**
	 * Group数据
	 */
	private List<GroupParam> groups = new ArrayList<GroupParam>();
	/**
	 * Child数据
	 */
	private List<List<ChildParam>> childs = new ArrayList<List<ChildParam>>();
	
	/**
	 * GroupView布局Id
	 */
	private int groupLayoutResId;
	
	/**
	 * GroupViewHolder类
	 */
	private Class<TGExpandableGroupViewHolder<GroupParam>> groupViewHolderClazz;
	
	/**
	 * ChildView布局Id
	 */
	private int childLayoutResId;
	
	/**
	 * GroupViewHolder类
	 */
	private Class<TGExpandableChildViewHolder<ChildParam>> childViewHolderClazz;
	
	/**
	 * @date 2012-12-28
	 * 构造函数
	 * @param context 运行环境
	 * @param groups Group数据
	 * @param childs Child数据
	 */
	public TGExpandableListAdapter(Activity activity, List<GroupParam> groups, 
			List<List<ChildParam>> childs)
	{
		this.setActivity(activity);
		if(null != groups)
		{
			this.groups.addAll(groups);
		}
		
		if(null != childs)
		{
			this.childs.addAll(childs);
		}
	}
	
	/**
	 * @see BaseExpandableListAdapter#getChild(int, int);
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) 
	{
		return childs.get(groupPosition).get(childPosition);
	}

	/**
	 * @see BaseExpandableListAdapter#getChildId(int, int);
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) 
	{
		return childPosition;
	}

	/**
	 * @see BaseExpandableListAdapter#getChildView(int, int, boolean, View, ViewGroup);
	 */
	@SuppressWarnings("unchecked")
	@Override
	public View getChildView(int groupPosition, int childPosition, 
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		TGExpandableChildViewHolder<ChildParam> viewHolder = null;
		if(null == convertView)
		{
			if(childLayoutResId > 0)
			{
				try
				{
					convertView = LayoutInflater.from(activity).inflate(childLayoutResId, null);
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
			
			viewHolder = initChildViewHolder();
			convertView = viewHolder.initView(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (TGExpandableChildViewHolder<ChildParam>) convertView.getTag();
		}
		
		viewHolder.updateViewDimension(childs.get(groupPosition).get(childPosition),
				groupPosition, childPosition, isLastChild, convertView, parent);
		
		viewHolder.fillData(childs.get(groupPosition).get(childPosition), 
				groupPosition, childPosition, isLastChild);
		
		return convertView;
	}
	
	/**
	 * 初始化ChildViewHolder
	 * @return
	 */
	protected TGExpandableChildViewHolder<ChildParam> initChildViewHolder()
	{
		TGExpandableChildViewHolder<ChildParam> viewHolder = null;
		try
		{
			viewHolder = childViewHolderClazz.newInstance();
			viewHolder.setActivity(activity);
			viewHolder.setAdapter(this);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return viewHolder;
	}

	/**
	 * 设置ChildViewHolder
	 * @param childLayoutResId ChildView布局id
	 * @param clazz holder类
	 */
	public void setChildViewHolder(int childLayoutResId, 
			Class<TGExpandableChildViewHolder<ChildParam>> clazz)
	{
		this.childLayoutResId = childLayoutResId;
		this.childViewHolderClazz = clazz;
	}
	
	/**
	 * @see BaseExpandableListAdapter#getChildrenCount(int);
	 */
	@Override
	public int getChildrenCount(int groupPosition) 
	{
		return childs.get(groupPosition).size();
	}

	/**
	 * @see BaseExpandableListAdapter#getGroup(int);
	 */
	@Override
	public Object getGroup(int groupPosition) 
	{
		return groups.get(groupPosition);
	}

	/**
	 * @see BaseExpandableListAdapter#getGroupCount();
	 */
	@Override
	public int getGroupCount() 
	{
		return groups.size();
	}

	/**
	 * @see BaseExpandableListAdapter#getGroupId(int);
	 */
	@Override
	public long getGroupId(int groupPosition) 
	{
		return groupPosition;
	}

	/**
	 * @see BaseExpandableListAdapter#getGroupView(int, boolean, View, ViewGroup);
	 */
	@SuppressWarnings("unchecked")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, 
			View convertView, ViewGroup parent)
	{
		TGExpandableGroupViewHolder<GroupParam> viewHolder = null;
		if(null == convertView)
		{
			if(groupLayoutResId > 0)
			{
				try
				{
					convertView = LayoutInflater.from(activity).inflate(groupLayoutResId, null);
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
			
			viewHolder = initGroupViewHolder();
			convertView = viewHolder.initView(convertView);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (TGExpandableGroupViewHolder<GroupParam>) convertView.getTag();
		}
		
		viewHolder.updateViewDimension(groups.get(groupPosition), groupPosition, 
				isExpanded, convertView, parent);
		viewHolder.fillData(groups.get(groupPosition), groupPosition, isExpanded);
		
		return convertView;
	}

	/**
	 * 初始化GroupViewHolder
	 * @return
	 */
	protected TGExpandableGroupViewHolder<GroupParam> initGroupViewHolder()
	{
		TGExpandableGroupViewHolder<GroupParam> viewHolder = null;
		try
		{
			viewHolder = groupViewHolderClazz.newInstance();
			viewHolder.setActivity(activity);
			viewHolder.setAdapter(this);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return viewHolder;
	}
	
	/**
	 * 设置GroupViewHolder
	 * @param groupLayoutResId GroupView布局Id
	 * @param clazz GroupView类
	 */
	public void setGroupViewHolder(int groupLayoutResId, 
			Class<TGExpandableGroupViewHolder<GroupParam>> clazz)
	{
		this.groupLayoutResId = groupLayoutResId;
		this.groupViewHolderClazz = clazz;
	}
	
	/**
	 * @see BaseExpandableListAdapter#hasStableIds();
	 */
	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	/**
	 * @see BaseExpandableListAdapter#isChildSelectable(int, int);
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}

	public Activity getActivity()
	{
		return activity;
	}

	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}
	
	/**
	 * 获取Group数据
	 * @return
	 */
	public List<GroupParam> getGroups()
	{
		return groups;
	}
	
	/**
	 * 获取Child数据
	 * @return
	 */
	public List<List<ChildParam>> getChilds()
	{
		return childs;
	}
}
