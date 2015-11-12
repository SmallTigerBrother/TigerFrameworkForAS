package com.mn.tiger.widget.recyclerview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mn.tiger.utility.DisplayUtils;

import butterknife.ButterKnife;

/**
 * 自定义的ViewHolder
 */
public abstract class TGRecyclerViewHolder<T>
{
    private Context context;

    /**
     * 搭配使用的Adapter
     */
    private TGRecyclerViewAdapter<T> adapter;

    /**
     * 当前使用ViewHolder的位置
     */
    private int position;

    private RecyclerView recyclerView;

    /**
     * Adapter真正使用的ViewHolder
     */
    private TGRecyclerViewAdapter.InternalRecyclerViewHolder<T> holder;

    /**
     * 列表行点击事件
     */
    private TGRecyclerView.OnItemClickListener onItemClickListener;

    /**
     * 默认设置的列表行点击事件
     */
    private InternalOnClickListener internalOnClickListener;

    public TGRecyclerViewHolder()
    {
    }

    /**
     * 初始化列表行视图
     * @return 需要返回convertView
     */
    public View initView(ViewGroup parent, int viewType)
    {
        View convertView = LayoutInflater.from(getContext()).inflate(getLayoutId(), parent, false);
        ButterKnife.bind(this, convertView);

        return convertView;
    }

    /**
     * 将OnItemClick事件绑定到View上
     * @param convertView
     */
    void attachOnItemClickListener(View convertView)
    {
        if(null != internalOnClickListener)
        {
            convertView.setOnClickListener(internalOnClickListener);
        }
    }

    /**
     * 获取匹配的Adapter
     * @return
     */
    public TGRecyclerViewAdapter<T> getAdapter()
    {
        return adapter;
    }

    /**
     * 设置匹配的Adapter
     * @param adapter
     */
    public void setAdapter(TGRecyclerViewAdapter<T> adapter)
    {
        this.adapter = adapter;
    }

    /**
     * 更新列表行的尺寸
     * @param itemData
     * @param position
     */
    public void updateViewDimension(ViewGroup parent, View convertView, T itemData, int position, int viewType)
    {

    }

    /**
     * 填充数据
     * @param itemData
     * @param position
     */
    public abstract void fillData(ViewGroup parent, View convertView, T itemData, int position, int viewType);

    /**
     * 获取列表行ViewType
     * @param position
     * @return
     */
    public int getItemViewType(int position)
    {
        return TGRecyclerViewAdapter.NONE_VIEW_TYPE;
    }

    protected Context getContext()
    {
        return context;
    }

    void setContext(Context context)
    {
        this.context = context;
    }

    /**
     * 获取LayoutId
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 设置列表行点击事件
     * @param onItemClickListener
     */
    void setOnItemClickListener(TGRecyclerView.OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
        if(null != onItemClickListener)
        {
            this.internalOnClickListener = new InternalOnClickListener();
        }
    }

    /**
     * 获取onItemClickListener
     * @return
     */
    protected TGRecyclerView.OnItemClickListener getOnItemClickListener()
    {
        return onItemClickListener;
    }

    /**
     * 设置RecyclerView
     * @param recyclerView
     */
    void setRecyclerView(RecyclerView recyclerView)
    {
        this.recyclerView = recyclerView;
    }

    /**
     * 获取RecyclerView
     * @return
     */
    protected RecyclerView getRecyclerView()
    {
        return recyclerView;
    }

    /**
     * 设置内部的ViewHolder
     * @param holder
     */
    void setInternalRecyclerViewHolder(TGRecyclerViewAdapter.InternalRecyclerViewHolder<T> holder)
    {
        this.holder = holder;
    }

    /**
     * 获取内部的ViewHolder
     * @return
     */
    protected TGRecyclerViewAdapter.InternalRecyclerViewHolder<T> getInternalRecyclerViewHolder()
    {
        return holder;
    }

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

    public int getTextSize(int resId)
    {
        return DisplayUtils.px2sp(getContext(), getDimensionPixelSize(resId));
    }

    /**
     * 获取当前填充的列表行的位置
     * @return
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * 设置当前填充的列表行的位置
     * @param position
     */
    void setPosition(int position)
    {
        this.position = position;
    }

    /**
     * 设置ViewHolder是否支持重用
     * @return
     */
    public boolean recycleAble()
    {
        return true;
    }

    /**
     * 列表行点击事件
     */
    private final class InternalOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            if(null != onItemClickListener)
            {
                onItemClickListener.onItemClick(getRecyclerView(), holder.itemView, holder.getAdapterPosition(), holder.getItemId());
            }
        }
    }
}