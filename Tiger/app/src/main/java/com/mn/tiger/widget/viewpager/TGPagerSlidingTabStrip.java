/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mn.tiger.widget.viewpager;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TGPagerSlidingTabStrip extends HorizontalScrollView
{
    public interface IconTabProvider
    {
        public int getPageIconResId(int position);
    }

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private int indicatorColor = 0xFF666666;
    private int underlineColor = 0x1A000000;
    private int dividerColor = 0x1A000000;

    private boolean shouldExpand = false;
    private boolean textAllCaps = false;

    private int scrollOffset = 52;
    private int indicatorHeight = 8;
    private int underlineHeight = 2;
    private int dividerPadding = 0;
    private int tabPaddingLeftRight = 24;
    private int tabPaddingTop = 8;
    private int tabPaddingBottom = 8;
    private int dividerWidth = 2;

    private int tabTextSize = 12;
    private int tabTextColor = 0xFF666666;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;

    private int lastScrollX = 0;

    private int tabBackgroundResId = -1;

    private Locale locale;

    /**
     * 控制自动均分tab，当所有tab都不能填充满可见部分时，将自动拉伸各个View，充满可见区域，并均分视图；
     * 若所有tab超出可见部分时，自适应大小
     */
    private boolean averageTabAuto = false;

    private int highLightTextColor = -1;

    private int selectedPage = 0;

    public TGPagerSlidingTabStrip(Context context)
    {
        this(context, null);
    }

    public TGPagerSlidingTabStrip(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TGPagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPaddingLeftRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPaddingLeftRight, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null)
        {
            locale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager pager)
    {
        this.pager = pager;

        if (pager.getAdapter() == null)
        {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener)
    {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged()
    {
        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++)
        {
            if (pager.getAdapter() instanceof IconTabProvider)
            {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            }
            else
            {
                addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
            }

        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
        {
            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout()
            {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                else
                {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });

    }

    private void addTextTab(final int position, String title)
    {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();

        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId)
    {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);

        addTab(position, tab);
    }

    private void addTab(final int position, View tab)
    {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pager.setCurrentItem(position);
            }
        });

        tab.setPadding(tabPaddingLeftRight, tabPaddingTop, tabPaddingLeftRight, tabPaddingBottom);
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    private void updateTabStyles()
    {
        for (int i = 0; i < tabCount; i++)
        {
            View v = tabsContainer.getChildAt(i);

            if(tabBackgroundResId > 0)
            {
                v.setBackgroundResource(tabBackgroundResId);
            }

            v.setPadding(tabPaddingLeftRight, tabPaddingTop, tabPaddingLeftRight, tabPaddingBottom);

            if (v instanceof TextView)
            {
                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);

                if(selectedPage == i && highLightTextColor != -1)
                {
                    tab.setTextColor(highLightTextColor);
                }
                else
                {
                    tab.setTextColor(tabTextColor);
                }

                // setAllCaps() is only available from API 14, so the upper case
                // is made manually if we are on a
                // pre-ICS-build
                if (textAllCaps)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    {
                        tab.setAllCaps(true);
                    }
                    else
                    {
                        tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }
            }
        }
    }

    private void scrollToChild(int position, int offset)
    {
        if (tabCount == 0)
        {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0)
        {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX)
        {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(averageTabAuto)
        {
            //测量内嵌的LinearLayout的宽度
            tabsContainer.measure(MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST), 0);

            int width = this.getMeasuredWidth();
            if(tabsContainer.getMeasuredWidth() <= width)
            {
                //均分各个子视图
                for(int i = 0; i < tabsContainer.getChildCount(); i++)
                {
                    tabsContainer.getChildAt(i).getLayoutParams().width = width / tabsContainer.getChildCount();
                }
            }
            else
            {
                //自适应宽度
                for(int i = 0; i < tabsContainer.getChildCount(); i++)
                {
                    tabsContainer.getChildAt(i).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0)
        {
            return;
        }

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates
        // between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1)
        {
            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        onDrawIndicator(canvas, rectPaint, lineLeft, lineRight);

        onDrawUnderLine(canvas, rectPaint);

        onDrawDivider(canvas, dividerPaint);
    }

    /**
     * 绘制指示器
     * @param canvas
     */
    protected void onDrawIndicator(Canvas canvas, Paint rectPaint, float lineLeft, float lineRight)
    {
        // draw indicator line
        rectPaint.setColor(indicatorColor);
        canvas.drawRect(lineLeft, getHeight() - indicatorHeight, lineRight, getHeight(), rectPaint);
    }

    /**
     * 绘制底部下划线
     * @param canvas
     * @param rectPaint
     */
    protected void onDrawUnderLine(Canvas canvas, Paint rectPaint)
    {
        // draw underline
        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, getHeight() - underlineHeight, tabsContainer.getWidth(),
                getHeight(), rectPaint);
    }

    /**
     * 绘制分割线
     * @param canvas
     * @param dividerPaint
     */
    protected void onDrawDivider(Canvas canvas, Paint dividerPaint)
    {
        // draw divider
        dividerPaint.setColor(dividerColor);
        for (int i = 0; i < tabCount - 1; i++)
        {
            View tab = tabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(),
                    getHeight() - dividerPadding, dividerPaint);
        }
    }

    public View getTab(int position)
    {
        return tabsContainer.getChildAt(position);
    }

    private class PageListener implements OnPageChangeListener
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

            invalidate();

            if (delegatePageListener != null)
            {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            if (state == ViewPager.SCROLL_STATE_IDLE)
            {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if (delegatePageListener != null)
            {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position)
        {
            ((TextView)tabsContainer.getChildAt(selectedPage)).setTextColor(tabTextColor);
            if(highLightTextColor != -1)
            {
                ((TextView)tabsContainer.getChildAt(position)).setTextColor(highLightTextColor);
            }
            selectedPage = position;

            if (delegatePageListener != null)
            {
                delegatePageListener.onPageSelected(position);
            }
        }
    }

    /**
     * 控制自动均分tab，当所有tab都不能填充满可见部分时，将自动拉伸各个View，充满可见区域，并均分视图；
     * 若所有tab超出可见部分时，自适应大小
     * 默认为True
     * @param averageTabAuto
     */
    public void setAverageTabAuto(boolean averageTabAuto)
    {
        this.averageTabAuto = averageTabAuto;
    }

    public void setIndicatorColor(int indicatorColor)
    {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorHeight(int indicatorLineHeightPx)
    {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public void setUnderlineColor(int underlineColor)
    {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setDividerColor(int dividerColor)
    {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setUnderlineHeight(int underlineHeightPx)
    {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public void setDividerPadding(int dividerPaddingPx)
    {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public void setDividerWidth(int dividerWidth)
    {
        this.dividerWidth = dividerWidth;
        this.dividerPaint.setStrokeWidth(dividerWidth);
        updateTabStyles();
    }

    public void setScrollOffset(int scrollOffsetPx)
    {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public void setShouldExpand(boolean shouldExpand)
    {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean isTextAllCaps()
    {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps)
    {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx)
    {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize()
    {
        return tabTextSize;
    }

    public void setTextColor(int textColor)
    {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public int getTextColor()
    {
        return tabTextColor;
    }

    public void setHighLightTextColor(int highLightTextColor)
    {
        this.highLightTextColor = highLightTextColor;
    }

    public void setTypeface(Typeface typeface, int style)
    {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId)
    {
        this.tabBackgroundResId = resId;
    }

    public void setTabPaddingLeftRight(int paddingPx)
    {
        this.tabPaddingLeftRight = paddingPx;
        updateTabStyles();
    }

    public void setTabPaddingTop(int paddingPx)
    {
        this.tabPaddingTop = paddingPx;
    }

    public void setTabPaddingBottom(int paddingPx)
    {
        this.tabPaddingBottom = paddingPx;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState
    {
        int currentPosition;

        public SavedState(Parcelable superState)
        {
            super(superState);
        }

        private SavedState(Parcel in)
        {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>()
        {
            @Override
            public SavedState createFromParcel(Parcel in)
            {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size)
            {
                return new SavedState[size];
            }
        };
    }

}
