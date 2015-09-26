package com.mn.tiger.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

/**
 * Created by peng on 15/9/21.
 */
public class NestRecyclerViewScrollView extends ScrollView
{
    private int downX;
    private int downY;
    private int mTouchSlop;

    public NestRecyclerViewScrollView(Context context)
    {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public NestRecyclerViewScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public NestRecyclerViewScrollView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e)
    {
        if(Build.VERSION.SDK_INT > 19)
        {
            int action = e.getAction();
            switch (action)
            {
                case MotionEvent.ACTION_DOWN:
                    downX = (int) e.getRawX();
                    downY = (int) e.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int) e.getRawY();
                    if (Math.abs(moveY - downY) > mTouchSlop)
                    {
                        return true;
                    }
            }

            return super.onInterceptTouchEvent(e);
        }
        else
        {
            return super.onInterceptTouchEvent(e);
        }
    }
}
