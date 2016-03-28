package com.mn.tiger.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.mn.tiger.log.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Dalang on 2015/9/9.
 */
public class TGRecyclerViewAdapter<T> extends RecyclerView.Adapter<TGRecyclerViewAdapter.InternalRecyclerViewHolder<T>>
{
    private static final Logger LOG = Logger.getLogger(TGRecyclerViewAdapter.class);

    /**
     * 不区分ViewType
     */
    public static final int NONE_VIEW_TYPE = 0;

    /**
     * 运行环境
     */
    private Context context;

    /**
     * 列表填充数据
     */
    private List<T> items = null;

    /**
     * 列表行点击事件
     */
    private TGRecyclerView.OnItemClickListener onItemClickListener;

    /**
     * 使用Adapter的RecyclerView
     */
    RecyclerView recyclerView;

    /**
     * 保存extras数据的数组
     */
    private SparseArray<Object> extras;

    /**
     * 多种ViewType时，处理Data、ViewHoler、ViewType绑定的管理类
     */
    private TGViewTypeBinder viewTypeBinder;

    /**
     * 是否启用不支持重用的特性
     */
    boolean enableUnRecycleViewHolder = false;

    /**
     * 支持多种数据类型的构造函数
     * （数据类型不支持泛型，例如不支持List<Abc>，如果要使用泛型，请自行写一个类extends ArrayList<Abc>）
     * 使用该构造方法时，请 extends TGRecyclerViewAdapter<Object> 防止出现数据冲突
     * @param context
     * @param items
     * @param viewHolderClasses 多种数据类型的ViewHolder，请注意每种ViewHolder的泛型参数必需都不同，否则无法正常使用
     */
    public TGRecyclerViewAdapter(Context context, List<T> items,
                                 Class<? extends TGRecyclerViewHolder>... viewHolderClasses)
    {
        this.context = context;
        this.items = new ArrayList<T>();
        if (null != items)
        {
            this.items.addAll(items);
        }

        this.setHasStableIds(true);
        viewTypeBinder = new TGViewTypeBinder(this, viewHolderClasses);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        //设置SpanSizeLookup
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager)
        {
            if(!(((GridLayoutManager) layoutManager).getSpanSizeLookup() instanceof PullToRefreshRecyclerView.HeaderSpanSizeLookup))
            {
                ((GridLayoutManager) layoutManager).setSpanSizeLookup(new TGSpanSizeLookup(this));
            }
        }
    }

    /**
     * 设置是否启用不支持重用的特性
     * @param enableUnRecycleViewHolder
     */
    public void setEnableUnRecycleViewHolder(boolean enableUnRecycleViewHolder)
    {
        this.enableUnRecycleViewHolder = enableUnRecycleViewHolder;
    }

    @Override
    public InternalRecyclerViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        TGRecyclerViewHolder<T> viewHolder = createViewHolder(viewType);
        if(viewHolder.recyclable()  || (!enableUnRecycleViewHolder && !viewHolder.recyclable()))
        {
            viewHolder.convertView = viewHolder.initView(parent, viewType);
            viewHolder.attachOnItemClickListener(viewHolder.convertView);
        }

        InternalRecyclerViewHolder<T> internalRecyclerViewHolder = new InternalRecyclerViewHolder<T>(viewHolder.convertView);
        internalRecyclerViewHolder.setTGRecyclerViewHolder(viewHolder);
        return internalRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(InternalRecyclerViewHolder<T> holder, int position)
    {
        TGRecyclerViewHolder tgRecyclerViewHolder = holder.getTGRecyclerViewHolder();
        tgRecyclerViewHolder.setPosition(position);
        if(tgRecyclerViewHolder.recyclable()  || (!enableUnRecycleViewHolder && !tgRecyclerViewHolder.recyclable()))
        {
            tgRecyclerViewHolder.updateViewDimension(recyclerView, holder.itemView, getItem(position), position, holder.getItemViewType());
            tgRecyclerViewHolder.fillData(recyclerView, holder.itemView, getItem(position), position, holder.getItemViewType());
        }
    }

    @Override
    public void onViewAttachedToWindow(InternalRecyclerViewHolder<T> holder)
    {
        super.onViewAttachedToWindow(holder);
        //设置FullSpan参数
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        int position = holder.getTGRecyclerViewHolder().getPosition();
        if(null != layoutParams && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams)
        {
            ((StaggeredGridLayoutManager.LayoutParams)layoutParams).setFullSpan(holder.getTGRecyclerViewHolder().isFullSpan(position));
        }
        //保存ViewHolder
        viewTypeBinder.putViewHolder(holder.getTGRecyclerViewHolder().getPosition(), holder.getTGRecyclerViewHolder());
    }

    @Override
    public void onViewDetachedFromWindow(InternalRecyclerViewHolder<T> holder)
    {
        super.onViewDetachedFromWindow(holder);
        viewTypeBinder.recycleViewHolder(holder.getTGRecyclerViewHolder().getPosition(), holder.getTGRecyclerViewHolder());
    }

    /**
     * 获取指定位置的ViewHolder，如果ViewHolder不在可视范围内，并且属于可重用的ViewHolder，返回值为null，
     * 若想始终拿到ViewHolder，请在ViewHolder的recycleAble()方法中返回false
     * @param position
     * @return
     */
    public final TGRecyclerViewHolder<T> getViewHolderAtPosition(int position)
    {
        return viewTypeBinder.getRecyclerViewHolderAtPosition(position);
    }

    @Override
    public  int getItemViewType(int position)
    {
        return viewTypeBinder.generateItemViewType(position);
    }

    /**
     * 初始化ViewHolder
     *
     * @return
     */
    protected final TGRecyclerViewHolder<T> createViewHolder(int viewType)
    {
        TGRecyclerViewHolder<T> viewHolder = viewTypeBinder.newViewHolderByType(viewType);
        initViewHolder(viewHolder);
        return viewHolder;
    }

    final TGRecyclerViewHolder<T> initViewHolder(TGRecyclerViewHolder<T> viewHolder)
    {
        if(null != viewHolder)
        {
            viewHolder.setContext(context);
            viewHolder.setAdapter(this);
            viewHolder.setOnItemClickListener(onItemClickListener);
            viewHolder.setRecyclerView((RecyclerView) recyclerView);
        }
        return viewHolder;
    }

    /**
     * 生成ViewType
     * @param startPosition
     * @param endPosition
     */
    private void generateViewTypes(int startPosition, int endPosition)
    {
        if(enableUnRecycleViewHolder)
        {
            if(null != viewTypeBinder)
            {
                for (int i = startPosition; i <= endPosition ; i++)
                {
                    viewTypeBinder.generateItemViewType(i);
                }
            }
        }
    }

    /**
     * 回收所有不支持回收的ViewHolder
     */
    public void recycleUnRecyclableViewHolders()
    {
        viewTypeBinder.recycleUnRecyclableViewHolders();
    }

    /**
     * 获取指定位置的数据
     * @param position
     * @return
     */
    public final T getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public final int getItemCount()
    {
        return items.size();
    }

    /**
     * 获取数据总个数
     * @return
     */
    public final int getCount()
    {
        return getItemCount();
    }

    @Override
    public final long getItemId(int position)
    {
        return position;
    }

    /**
     * 设置列表行点击事件
     * @param onItemClickListener
     */
    final void setOnItemClickListener(TGRecyclerView.OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 更新一条数据
     * @param position
     * @param data
     */
    public final void updateData(int position, T data)
    {
        this.items.set(position, data);
        generateViewTypes(position,position);
        notifyItemChanged(position);
    }

    /**
     * 该方法的作用:更新列表数据
     *
     * @param data 列表数据
     * @date 2013-1-17
     */
    public final void updateData(List<T> data)
    {
        if (null != data)
        {
            if(this.items != data)
            {
                this.items.clear();
                this.items.addAll(data);
                generateViewTypes(0, data.size() - 1);
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 更新列表行数据
     *
     * @param data
     */
    public final void updateData(T[] data)
    {
        if (null != data)
        {
            this.items.clear();
            this.items.addAll(Arrays.asList(data));
            generateViewTypes(0, data.length - 1);
            notifyDataSetChanged();
        }
    }

    /**
     * 局部刷新，data必需是列表中数据的一部分
     * @param data
     */
    public final void updatePartData(List<T> data)
    {
        if (null != data)
        {
            if(this.items != data)
            {
                T dataItem = null;
                int position = -1;
                for (int i = 0; i < data.size(); i++)
                {
                    dataItem = data.get(i);
                    position = this.items.indexOf(dataItem);
                    if(position > -1)
                    {
                        this.items.set(position, dataItem);
                        generateViewTypes(position,position);
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
    public final void updatePartData(T[] data)
    {
        if (null != data)
        {
            T dataItem = null;
            int position = -1;
            for (int i = 0; i < data.length; i++)
            {
                dataItem = data[i];
                position = this.items.indexOf(dataItem);
                if(position > -1)
                {
                    this.items.set(position, dataItem);
                    generateViewTypes(position,position);
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
    public final void updatePartData(T data)
    {
        if (null != data)
        {
            int position = this.items.indexOf(data);
            if(position > -1)
            {
                this.items.set(position, data);
                generateViewTypes(position,position);
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
    public final void appendData(List<T> data)
    {
        if (null != data)
        {
            this.items.addAll(data);
            generateViewTypes(items.size() - data.size(), data.size() -1 );
            notifyItemRangeInserted(items.size() - data.size(), data.size());
        }
    }

    /**
     * 向列表行追加数据
     *
     * @param data
     */
    public final void appendData(T[] data)
    {
        if (null != data)
        {
            this.items.addAll(Arrays.asList(data));
            generateViewTypes(items.size() - data.length, data.length - 1);
            notifyItemRangeInserted(items.size() - data.length, data.length);
        }
    }

    /**
     * 向列表行追加数据
     * @param data
     */
    public final void appendData(T data)
    {
        if (null != data)
        {
            this.items.add(data);
            generateViewTypes(items.size() - 1, items.size() - 1);
            notifyItemInserted(items.size() - 1);
        }
    }

    /**
     * 在指定位置插入一条数据
     * @param position
     * @param data
     */
    public final void insertData(int position, T data)
    {
        if(null != data && position >= 0 && position <= this.items.size())
        {
            this.items.add(position, data);
            generateViewTypes(position,position);
            notifyItemInserted(position);
        }
    }

    /**
     * 在指定位置插入部分数据
     * @param position
     * @param data
     */
    public final void insertData(int position, T[] data)
    {
        if(null != data && position >= 0 && position <= this.items.size())
        {
            this.items.addAll(position, Arrays.asList(data));
            generateViewTypes(position, position + data.length - 1);
            notifyItemRangeInserted(position, data.length);
        }
    }


    /**
     * 删除列表行
     *
     * @param position 列表行位置
     */
    public final void removeItem(int position)
    {
        if (items.size() > position && position >= 0)
        {
            items.remove(position);
            notifyDataSetChanged();
//            notifyItemRemoved(position);

//            notifyItemRangeChanged(position, this.items.size() - position);
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
    public final void removeItem(T item)
    {
        int position = items.indexOf(item);
        if (position > -1)
        {
            items.remove(item);
            notifyDataSetChanged();
//            notifyItemRemoved(position);

//            notifyItemRangeChanged(position, this.items.size() - position);
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
    public final void removeItems(T[] data)
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
    public final void removeItems(List<T> data)
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
    public final void removeAll()
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
    public final List<T> getListItems()
    {
        return this.items;
    }

    /**
     * 获取列表第一个元素
     *
     * @return 若列表无数据，返回null
     */
    public final T getFirstItem()
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
    public final T getLastItem()
    {
        if (!items.isEmpty())
        {
            return items.get(items.size() - 1);
        }
        return null;
    }

    public final Context getContext()
    {
        return context;
    }

    /**
     * 设置Extra数据
     *
     * @param key
     * @param extra
     */
    public final void putExtra(int key, Object extra)
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
    public final Object getExtra(int key)
    {
        if (null == extras)
        {
            return null;
        }
        return extras.get(key);
    }

    final static class InternalRecyclerViewHolder<T> extends RecyclerView.ViewHolder
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

    final static class TGSpanSizeLookup extends GridLayoutManager.SpanSizeLookup
    {
        private TGRecyclerViewAdapter adapter;

        public TGSpanSizeLookup(TGRecyclerViewAdapter adapter)
        {
            this.adapter = adapter;
        }

        @Override
        public int getSpanSize(int position)
        {
            TGRecyclerViewHolder viewHolder = this.adapter.viewTypeBinder.getSimpleViewHolderInstanceAtPosition(position);
            if(null != viewHolder)
            {
                return viewHolder.getSpanSize(position);
            }
            return 1;
        }
    }
}
