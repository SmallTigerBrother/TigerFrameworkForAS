package com.mn.tiger.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Dalang on 2015/9/9.
 */
public class TGRecyclerView extends RecyclerView
{
    private OnItemClickListener onItemClickListener;

    public TGRecyclerView(Context context)
    {
        super(context);
        setOrientation(VERTICAL);
    }

    public TGRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public TGRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setOrientation(VERTICAL);
    }

    public void setOrientation(int orientation)
    {
        this.setLayoutManager(new LinearLayoutManager(getContext(), orientation, false));
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
        Adapter adapter = getAdapter();
        if(null != adapter)
        {
            ((TGRecyclerViewAdapter<?>)adapter).setOnItemClickListener(this.onItemClickListener);
        }
    }

    @Override
    public void setAdapter(Adapter adapter)
    {
        if(!(adapter instanceof TGRecyclerViewAdapter<?>))
        {
            throw new RuntimeException("adapter must be an instance of TGRecyclerAdapter");
        }

        if(null != onItemClickListener)
        {
            ((TGRecyclerViewAdapter<?>)adapter).setOnItemClickListener(this.onItemClickListener);
        }

        super.setAdapter(adapter);
    }

    public interface OnItemClickListener
    {
        void onItemClick(RecyclerView parent, View view, int position, long id);
    }
}
