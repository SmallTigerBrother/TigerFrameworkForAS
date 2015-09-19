package com.mn.tiger.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Dalang on 2015/9/9.
 */
public class TGRecyclerViewAdapter<T> extends RecyclerView.Adapter<TGRecyclerViewAdapter.InternalRecyclerViewHolder<T>>
{
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
    private Class<? extends TGRecyclerViewHolder<T>> viewHolderClass;

    private TGRecyclerView.OnItemClickListener onItemClickListener;

    private ViewGroup parent;

    /**
     * 保存extras数据的数组
     */
    private SparseArray<Object> extras;

    /**
     * 保存position - viewHolder的数组
     */
    private SparseArray<TGRecyclerViewHolder<T>> viewHolders;

    public TGRecyclerViewAdapter(Context context, List<T> items, int convertViewLayoutId,
                                 Class<? extends TGRecyclerViewHolder<T>> viewHolderClass)
    {
        this.context = context;
        this.items = new ArrayList<T>();
        if (null != items)
        {
            this.items.addAll(items);
        }

        this.viewHolders = new SparseArray<TGRecyclerViewHolder<T>>();

        this.convertViewLayoutId = convertViewLayoutId;
        this.viewHolderClass = viewHolderClass;
        this.setHasStableIds(true);
    }

    @Override
    public InternalRecyclerViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        this.parent = parent;
        TGRecyclerViewHolder<T> viewHolder = initViewHolder();
        viewHolder.setOnItemClickListener(onItemClickListener);
        viewHolder.setRecyclerView((RecyclerView) parent);
        InternalRecyclerViewHolder<T> internalRecyclerViewHolder = new InternalRecyclerViewHolder<T>(viewHolder.initView(parent,viewType));
        internalRecyclerViewHolder.setTGRecyclerViewHolder(viewHolder);
        return internalRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(InternalRecyclerViewHolder<T> holder, int position)
    {
        holder.getTGRecyclerViewHolder().updateViewDimension(parent, holder.itemView, getItem(position), position, holder.getItemViewType());
        holder.getTGRecyclerViewHolder().fillData(parent, holder.itemView, getItem(position), position, holder.getItemViewType());
    }

    @Override
    public void onViewAttachedToWindow(InternalRecyclerViewHolder<T> holder)
    {
        super.onViewAttachedToWindow(holder);
        //保存ViewHolder
        viewHolders.put(holder.getAdapterPosition(), holder.getTGRecyclerViewHolder());
    }

    @Override
    public void onViewDetachedFromWindow(InternalRecyclerViewHolder<T> holder)
    {
        super.onViewDetachedFromWindow(holder);
        //移除ViewHolder
        viewHolders.remove(holder.getAdapterPosition());
    }

    /**
     * 获取指定位置的ViewHolder
     * @param position
     * @return
     */
    public TGRecyclerViewHolder<T> getViewHolderAtPosition(int position)
    {
        return viewHolders.get(position);
    }

    @Override
    public int getItemViewType(int position)
    {
        return super.getItemViewType(position);
    }

    /**
     * 初始化ViewHolder
     *
     * @return
     */
    protected TGRecyclerViewHolder<T> initViewHolder()
    {
        TGRecyclerViewHolder<T> viewHolder = null;
        try
        {
            viewHolder = viewHolderClass.newInstance();
            viewHolder.setContext(context);
            viewHolder.setAdapter(this);
            viewHolder.setLayoutId(convertViewLayoutId);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return viewHolder;
    }

    public T getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public int getCount()
    {
        return getItemCount();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    void setOnItemClickListener(TGRecyclerView.OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 更新一条数据
     * @param position
     * @param data
     */
    public void updateData(int position, T data)
    {
        this.items.set(position, data);
        notifyItemChanged(position);
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
     * @param data
     */
    public void updatePartData(List<T> data)
    {
        if (null != data)
        {
            if(this.items != data)
            {
                for (int i = 0; i < data.size(); i++)
                {
                    T dataItem = data.get(i);
                    int position = this.items.indexOf(dataItem);
                    if(position > -1)
                    {
                        this.items.set(position, dataItem);
                        notifyItemChanged(position);
                    }
                    else
                    {
                        throw new RuntimeException("The data" + dataItem.toString() + " must be a part of list");
                    }
                }
            }
        }
    }

    /**
     * 局部刷新，data必需是列表中数据的一部分
     * @param data
     */
    public void updatePartData(T[] data)
    {
        if (null != data)
        {
            for (int i = 0; i < data.length; i++)
            {
                T dataItem = data[i];
                int position = this.items.indexOf(dataItem);
                if(position > -1)
                {
                    this.items.set(position, dataItem);
                    notifyItemChanged(position);
                }
                else
                {
                    throw new RuntimeException("The data" + dataItem.toString() + " must be a part of list");
                }
            }
        }
    }

    /**
     * 局部刷新，data必需是列表中数据的一个
     * @param data
     */
    public void updatePartData(T data)
    {
        if (null != data)
        {
            int position = this.items.indexOf(data);
            if(position > -1)
            {
                this.items.set(position, data);
                this.notifyItemChanged(position);
            }
            else
            {
                throw new RuntimeException("The data" + data.toString() + " must be a part of list");
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
            notifyItemRangeInserted(items.size() - data.size(), items.size());
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
            notifyItemRangeInserted(items.size() - data.length, items.size());
        }
    }

    /**
     * 向列表行追加数据
     * @param data
     */
    public void appendData(T data)
    {
        if (null != data)
        {
            this.items.add(data);
            notifyItemInserted(items.size() - 1);
        }
    }

    /**
     * 在指定位置插入一条数据
     * @param position
     * @param data
     */
    public void insertData(int position, T data)
    {
        if(null != data && position >= 0 && position < this.items.size())
        {
            this.items.add(position, data);
            notifyItemInserted(position);

            notifyItemRangeChanged(position, this.items.size() - position);
        }
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
            notifyItemRemoved(position);

            notifyItemRangeChanged(position, this.items.size() - position);
        }
        else
        {
            throw new RuntimeException("invalid position " + position + ", the list size is " + items.size());
        }
    }

    /**
     * 删除列表行
     *
     * @param item 列表行数据
     */
    public void removeItem(T item)
    {
        int position = items.indexOf(item);
        if (position > -1)
        {
            items.remove(item);
            notifyItemRemoved(position);

            notifyItemRangeChanged(position, this.items.size() - position);
        }
        else
        {
            throw new RuntimeException("The data" + item.toString() + " must be a part of list");
        }
    }

    /**
     * 删除多行数据
     * @param data
     */
    public void removeItems(T[] data)
    {
        if(null != data && data.length > 0)
        {
            int minPosition = 0;
            int position = -1;
            for (T item : data)
            {
                position = items.indexOf(item);
                if(minPosition > position && position > -1)
                {
                    minPosition = position;
                }
                items.remove(item);
                notifyItemRemoved(position);
            }

            notifyItemRangeChanged(minPosition, this.items.size() - minPosition);
        }
    }

    /**
     * 删除多行数据
     * @param data
     */
    public void removeItems(List<T> data)
    {
        if(null != data && data.size() > 0)
        {
            int minPosition = 0;
            int position = -1;
            for (T item : data)
            {
                position = items.indexOf(item);
                if(minPosition > position && position > -1)
                {
                    minPosition = position;
                }
                items.remove(item);
                notifyItemRemoved(position);
            }

            notifyItemRangeChanged(minPosition, this.items.size() - minPosition);
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

    public Context getContext()
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

    static class InternalRecyclerViewHolder<T> extends RecyclerView.ViewHolder
    {
        private TGRecyclerViewHolder<T> tgRecyclerViewHolder;

        public InternalRecyclerViewHolder(View itemView)
        {
            super(itemView);
        }

        void setTGRecyclerViewHolder(TGRecyclerViewHolder<T> tgRecyclerViewHolder)
        {
            this.tgRecyclerViewHolder = tgRecyclerViewHolder;
            this.tgRecyclerViewHolder.setInternalRecyclerViewHolder(this);
        }

        public TGRecyclerViewHolder<T> getTGRecyclerViewHolder()
        {
            return tgRecyclerViewHolder;
        }
    }
}
