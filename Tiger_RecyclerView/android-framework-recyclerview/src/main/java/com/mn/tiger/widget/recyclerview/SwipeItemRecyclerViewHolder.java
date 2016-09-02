package com.mn.tiger.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by Tiger on 16/8/24.
 */
public abstract class SwipeItemRecyclerViewHolder<T> extends TGRecyclerViewHolder<T>
{
    /**
     * 返回底部视图ID
     * @return
     */
    protected abstract int getBottomLayoutId();

    @Override
    public View initView(ViewGroup parent, int viewType)
    {
        SwipeItemLayout convertView = new SwipeItemLayout(getContext());
        convertView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
        //添加底部View
        convertView.addView(LayoutInflater.from(getContext()).inflate(getBottomLayoutId(), convertView, false));
        //添加上层View
        convertView.addView(LayoutInflater.from(getContext()).inflate(getLayoutId(), convertView, false));
        ButterKnife.bind(this, convertView);

        convertView.setDelegate(((SwipeItemRecyclerViewAdapter)getAdapter()).delegate);

        return convertView;
    }

    @Override
    void attachOnItemClickListener(final View convertView)
    {
        //屏蔽默认方法,设置两个子视图各自的点击事件
        ((SwipeItemLayout)convertView).getChildAt(0).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBottomLayoutClick();
            }
        });

        ((SwipeItemLayout)convertView).getChildAt(1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //如果滑动视图已关闭,响应点击事件
                if(((SwipeItemLayout)convertView).isClosed())
                {
                    //执行OnItemClick
                    performOnItemClick();
                    onAboveLayoutClick();
                }
                //关闭划出层
                close();
            }
        });
    }

    @Override
    public void onViewAttachedToWindow()
    {
        super.onViewAttachedToWindow();
        if(null != convertView)
        {
            //设置侧滑参数
            ((SwipeItemLayout)convertView).setSwipeable(isSwipeable(getPosition()));
            ((SwipeItemLayout)convertView).setSwipeDirection(getSwipeDirection(getPosition()));
            ((SwipeItemLayout)convertView).setBottomMode(getBottomMode(getPosition()));
            ((SwipeItemLayout)convertView).setSpringDistance(getSpringDistance(getPosition()));
        }
    }

    /**
     * 是否可滚动
     * @return
     */
    protected boolean isSwipeable(int position)
    {
        return true;
    }

    /**
     * 获取滑动方向
     * @return
     */
    protected SwipeItemLayout.SwipeDirection getSwipeDirection(int position)
    {
        return SwipeItemLayout.SwipeDirection.Left;
    }

    /**
     * 获取底部显示形式
     * @return
     */
    protected SwipeItemLayout.BottomMode getBottomMode(int position)
    {
        return SwipeItemLayout.BottomMode.PullOut;
    }

    /**
     * 获取拖动的弹簧距离
     * @return
     */
    protected int getSpringDistance(int position)
    {
        return 50;
    }

    /**
     * 底部视图点击回调方法
     */
    protected void onBottomLayoutClick()
    {

    }

    /**
     * 上部视图点击回调方法
     */
    protected void onAboveLayoutClick()
    {

    }

    protected void open()
    {
        if(null != convertView && !((SwipeItemLayout)convertView).isOpened())
        {
            ((SwipeItemLayout)convertView).openWithAnim();
        }
    }

    protected void close()
    {
        if(null != convertView && !((SwipeItemLayout)convertView).isClosed())
        {
            ((SwipeItemLayout)convertView).closeWithAnim();
        }
    }
}
