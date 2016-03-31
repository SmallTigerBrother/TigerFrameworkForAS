package com.mn.tiger.widget.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

/**
 * Created by peng on 16/3/31.
 */
public class GridVerticalDividerItemDecoration extends VerticalDividerItemDecoration
{
    public GridVerticalDividerItemDecoration(VerticalDividerItemDecoration.Builder builder)
    {
        super(builder);
    }

    @Override
    protected Rect getDividerBound(int position, RecyclerView parent, View child)
    {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.top = child.getTop();
        bounds.bottom = child.getBottom();

        bounds.left = child.getRight() + params.leftMargin + transitionX;
        bounds.right = bounds.left;

        return bounds;
    }

    public static class Builder extends VerticalDividerItemDecoration.Builder
    {
        public Builder(Context context)
        {
            super(context);
        }

        public VerticalDividerItemDecoration build()
        {
            checkBuilderParams();
            return new GridVerticalDividerItemDecoration(this);
        }
    }
}
