package com.mn.tiger.widget.recyclerview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * 自定义的ViewHolder
 */
public abstract class TGRecyclerViewHolder<T>
{
    private Context context;

    /**
     * 列表航layoutID
     */
    private int layoutId;

    /**
     * 搭配使用的Adapter
     */
    private TGRecyclerViewAdapter<T> adapter;

    private RecyclerView recyclerView;

    private TGRecyclerViewAdapter.InternalRecyclerViewHolder<T> holder;

    private TGRecyclerView.OnItemClickListener onItemClickListener;

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
        View convertView = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        ButterKnife.bind(this, convertView);
        if(null != internalOnClickListener)
        {
            convertView.setOnClickListener(internalOnClickListener);
        }
        return convertView;
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
    void setAdapter(TGRecyclerViewAdapter<T> adapter)
    {
        this.adapter = adapter;
    }

    /**
     * 更新列表行的尺寸
     * @param itemData
     * @param position
     */
    protected void updateViewDimension(ViewGroup parent, View convertView, T itemData, int position, int viewType)
    {

    }

    /**
     * 填充数据
     * @param itemData
     * @param position
     */
    public abstract void fillData(ViewGroup parent, View convertView, T itemData, int position, int viewType);

    protected Context getContext()
    {
        return context;
    }

    void setContext(Context context)
    {
        this.context = context;
    }

    void setLayoutId(int layoutId)
    {
        this.layoutId = layoutId;
    }

    protected int getLayoutId()
    {
        return layoutId;
    }

    void setOnItemClickListener(TGRecyclerView.OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
        if(null != onItemClickListener)
        {
            this.internalOnClickListener = new InternalOnClickListener();
        }
    }

    protected TGRecyclerView.OnItemClickListener getOnItemClickListener()
    {
        return onItemClickListener;
    }

    void setRecyclerView(RecyclerView recyclerView)
    {
        this.recyclerView = recyclerView;
    }

    protected RecyclerView getRecyclerView()
    {
        return recyclerView;
    }

    void setInternalRecyclerViewHolder(TGRecyclerViewAdapter.InternalRecyclerViewHolder<T> holder)
    {
        this.holder = holder;
    }

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

    private class InternalOnClickListener implements View.OnClickListener
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
