package com.mn.tiger.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Dalang on 2015/10/29.
 */
public abstract class TGMultiViewTypeRecyclerViewHolder<T> extends TGRecyclerViewHolder<T>
{
    private TGRecyclerViewHolder<T> internalViewHolder;

    @Override
    public View initView(ViewGroup parent, int viewType)
    {
        internalViewHolder = initViewHolderByViewType(parent, viewType);

        internalViewHolder.setContext(getContext());
        internalViewHolder.setAdapter(getAdapter());
        internalViewHolder.setOnItemClickListener(getOnItemClickListener());
        internalViewHolder.setRecyclerView((RecyclerView) parent);

        return internalViewHolder.initView(parent, viewType);
    }

    protected abstract TGRecyclerViewHolder<T> initViewHolderByViewType(ViewGroup parent, int viewType);

    @Override
    public void updateViewDimension(ViewGroup parent, View convertView, T itemData, int position, int viewType)
    {
        internalViewHolder.updateViewDimension(parent, convertView, itemData, position, viewType);
    }

    @Override
    public void fillData(ViewGroup parent, View convertView, T itemData, int position, int viewType)
    {
        internalViewHolder.fillData(parent, convertView, itemData, position, viewType);
    }

    public TGRecyclerViewHolder<T> getCurrentViewHolder()
    {
        return internalViewHolder;
    }

    @Override
    public boolean recycleAble()
    {
        return internalViewHolder.recycleAble();
    }
}
