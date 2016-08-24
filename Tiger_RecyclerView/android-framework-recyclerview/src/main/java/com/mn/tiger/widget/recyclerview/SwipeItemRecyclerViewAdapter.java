package com.mn.tiger.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tiger on 16/8/24.
 */
public class SwipeItemRecyclerViewAdapter<T> extends TGRecyclerViewAdapter<T>
{
    InternalSwipeItemLayoutDelegate delegate;

    public SwipeItemRecyclerViewAdapter(Context context, List<T> items, Class<? extends TGRecyclerViewHolder>... viewHolderClasses)
    {
        super(context, items, viewHolderClasses);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        if(null == delegate)
        {
            delegate = new InternalSwipeItemLayoutDelegate();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState != RecyclerView.SCROLL_STATE_IDLE)
                    {
                        delegate.closeOpenedItems();
                    }
                }
            });
        }
    }

    static class InternalSwipeItemLayoutDelegate implements SwipeItemLayout.SwipeItemLayoutDelegate
    {
        private ArrayList<SwipeItemLayout> openedItems = new ArrayList<>();

        @Override
        public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout)
        {
            closeOpenedItems();
            openedItems.add(swipeItemLayout);
        }

        @Override
        public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout)
        {
            openedItems.remove(swipeItemLayout);
        }

        @Override
        public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout)
        {
            closeOpenedItems();
        }

        public boolean closeOpenedItems()
        {
            boolean result = false;
            for (SwipeItemLayout item : openedItems)
            {
                item.closeWithAnim();
                result = true;
            }
            openedItems.clear();
            return  result;
        }
    }
}
