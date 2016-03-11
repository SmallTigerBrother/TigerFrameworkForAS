package com.mn.tiger.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import java.lang.reflect.Field;

/**
 * Created by peng on 16/3/11.
 */
public class ReverseLinearLayoutManager extends LinearLayoutManager
{
    public ReverseLinearLayoutManager(Context context, int orientation)
    {
        super(context, orientation, true);
    }

    /**
     * 强制修改mLastStackFromEnd的值，防止出现列表回调的bug
     * @param lastStackFromEnd
     */
    public void setLastStackFromEnd(boolean lastStackFromEnd)
    {
        try
        {
            Field mLastStackFromEndField = this.getClass().getSuperclass().getDeclaredField("mLastStackFromEnd");
            mLastStackFromEndField.setAccessible(true);
            mLastStackFromEndField.setBoolean(this, lastStackFromEnd);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
