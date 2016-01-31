package com.mn.tiger.fresco;

import android.content.Context;
import android.util.AttributeSet;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeView;

/**
 * Created by peng on 16/1/31.
 */
public class TGDraweeView extends DraweeView<GenericDraweeHierarchy>
{
    public TGDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TGDraweeView(Context context)
    {
        super(context);
    }

    public TGDraweeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TGDraweeView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
}
