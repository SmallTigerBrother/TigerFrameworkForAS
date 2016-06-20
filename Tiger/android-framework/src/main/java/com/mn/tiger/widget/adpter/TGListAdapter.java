package com.mn.tiger.widget.adpter;

import android.content.Context;
import android.util.SparseArray;
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
     * viewholder类，用于视图重用，初始化列表行和填充列表行数据
     */
    private Class<? extends TGViewHolder<T>> viewHolderClass;

    /**
     * 保存extras数据的数组
     */
    private SparseArray<Object> extras;

    private TGViewHolder<T> internalViewHolder;

    /**
     * @param context
     * @param items               列表填充数据
     * @param viewHolderClass     ViewHolder类名
     */
    public TGListAdapter(Context context, List<T> items , Class<? extends TGViewHolder<T>> viewHolderClass)
    {
        this.context = context;
        this.items = new ArrayList<T>();
        if (null != items)
        {
            this.items.addAll(items);
        }

        this.viewHolderClass = viewHolderClass;
        this.internalViewHolder = initViewHolder();
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

    @Override
    public int getItemViewType(int position)
    {
        return null != internalViewHolder ? internalViewHolder.getItemViewType(position) : IGNORE_ITEM_VIEW_TYPE;
    }

    /**
     * @see BaseAdapter#getView(int, View, ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (null == convertView)
        {
            convertView = initView(parent, position);
        }
        fillData(position, convertView, parent);

        return convertView;
    }

    /**
     * 初始化convertView（仅在无法重用视图时调用）
     *
     * @return
     */
    protected View initView(ViewGroup parent, int position)
    {
        TGViewHolder<T> viewHolder = initViewHolder();
        View convertView = viewHolder.initView(parent, getItemViewType(position));
        convertView.setTag(viewHolder);
        viewHolder.convertView = convertView;
        viewHolder.parent = parent;

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

        //更新列表行尺寸
        viewHolder.updateViewDimension(items.get(position), position, getItemViewType(position));

        //填充列表行数据
        viewHolder.fillData(items.get(position), position, getItemViewType(position));
    }

    /**
     * 初始化ViewHolder
     *
     * @return
     */
    protected TGViewHolder<T> initViewHolder()
    {
        TGViewHolder<T> viewHolder = null;
        if(null != viewHolderClass)
        {
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
        }
        return viewHolder;
    }

    /**
     * 更新一条数据
     *
     * @param position
     * @param data
     */
    public void updateData(int position, T data)
    {
        if (position >= 0 && position < getCount() && null != data)
        {
            this.items.set(position, data);
            notifyDataSetChanged();
        }
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
            if (this.items != data)
            {
                this.items.clear();
                this.items.addAll(data);
            }
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
            notifyDataSetChanged();
        }
    }

    /**
     * 局部刷新，data必需是列表中数据的一部分
     *
     * @param data
     */
    public void updatePartData(List<T> data)
    {
        if (null != data && this.items != data)
        {
            T dataItem = null;
            int position = -1;
            for (int i = 0; i < data.size(); i++)
            {
                dataItem = data.get(i);
                position = this.items.indexOf(dataItem);
                if (position > -1)
                {
                    this.items.set(position, dataItem);
                }
                else
                {
                    this.items.add(dataItem);
                }
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 局部刷新，data必需是列表中数据的一部分
     *
     * @param data
     */
    public void updatePartData(T[] data)
    {
        if (null != data)
        {
            T dataItem = null;
            int position = -1;
            for (int i = 0; i < data.length; i++)
            {
                dataItem = data[i];
                position = this.items.indexOf(dataItem);
                if (position > -1)
                {
                    this.items.set(position, dataItem);
                }
                else
                {
                    this.items.add(dataItem);
                }
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 局部刷新，data必需是列表中数据的一个
     *
     * @param data
     */
    public void updatePartData(T data)
    {
        if (null != data)
        {
            int position = this.items.indexOf(data);
            if (position > -1)
            {
                updateData(position, data);
            }
            else
            {
                appendData(data);
            }
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
     * 向列表行追加数据
     *
     * @param data
     */
    public void appendData(T data)
    {
        if (null != data)
        {
            this.items.add(data);
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
            notifyDataSetChanged();
        }
    }

    /**
     * 清除所有数据
     */
    public void removeAll()
    {
        items.clear();
        notifyDataSetChanged();
    }

    protected Context getContext()
    {
        return context;
    }

    /**
     * 设置Extra数据
     *
     * @param key
     * @param extra
     */
    public void putExtra(int key, Object extra)
    {
        if (null == extras)
        {
            extras = new SparseArray<Object>();
        }
        extras.put(key, extra);
    }

    /**
     * 获取Extra数据
     *
     * @param key
     * @return
     */
    public Object getExtra(int key)
    {
        if (null == extras)
        {
            return null;
        }
        return extras.get(key);
    }
}
