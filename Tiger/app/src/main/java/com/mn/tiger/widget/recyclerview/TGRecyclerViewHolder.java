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
     * 列表航layoutID
     */
    private int layoutId;

    /**
     * 搭配使用的Adapter
     */
    private TGRecyclerViewAdapter<T> adapter;

    private int position;

    private RecyclerView recyclerView;

    private TGRecyclerViewAdapter.InternalRecyclerViewHolder<T> holder;

    /**
     * 列表行点击事件
     */
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

        return convertView;
    }

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

    public void setContext(Context context)
    {
        this.context = context;
    }

    public void setLayoutId(int layoutId)
    {
        this.layoutId = layoutId;
    }

    protected int getLayoutId()
    {
        return layoutId;
    }

    public void setOnItemClickListener(TGRecyclerView.OnItemClickListener onItemClickListener)
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

    public void setRecyclerView(RecyclerView recyclerView)
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

    public boolean recycleAble()
    {
        return true;
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
