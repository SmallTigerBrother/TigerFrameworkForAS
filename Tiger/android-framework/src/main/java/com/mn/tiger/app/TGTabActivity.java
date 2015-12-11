package com.mn.tiger.app;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mn.tiger.R;
import com.mn.tiger.log.Logger;
import com.mn.tiger.widget.TGBadgeView;
import com.mn.tiger.widget.adpter.TGListAdapter;
import com.mn.tiger.widget.adpter.TGViewHolder;
import com.mn.tiger.widget.tab.TGTabView;
import com.mn.tiger.widget.tab.TGTabView.LayoutParams;
import com.mn.tiger.widget.tab.TGTabView.OnTabChangeListener;
import com.mn.tiger.widget.viewpager.TGFragmentPagerAdapter;
import com.mn.tiger.widget.viewpager.TGViewPager;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 带底部Tab的FragmentActivity
 * @author Dalang
 */
public abstract class TGTabActivity extends TGActionBarActivity implements
        OnPageChangeListener, OnTabChangeListener
{
    private Logger LOG = Logger.getLogger(TGTabActivity.class);

    /**
     * 底部Tab
     */
    private TGTabView tabView;

    /**
     * ViewPager
     */
    private TGViewPager viewPager;

    /**
     * tab数据
     */
    private TabModel[] tabModels;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setNavigationBarVisible(false);
        setContentView(R.layout.tiger_tab_activity);

        tabView = (TGTabView) findViewById(R.id.tiger_tab_bar);
        viewPager = (TGViewPager) findViewById(R.id.tiger_view_pager);

        setTabs(onInitTabs());
    }

    /**
     * 设置也页面是否可以滑动
     * @param canScroll
     */
    protected void setPageCanScroll(boolean canScroll)
    {
        viewPager.setCanScroll(canScroll);
    }

    /**
     * 设置Tab和Fragment
     * @param tabModels 所有的tab
     */
    public void setTabs(TabModel[] tabModels)
    {
        this.tabModels = tabModels;

        if(null != tabModels && tabModels.length > 0)
        {
            TGFragmentPagerAdapter pagerAdapter = new TGFragmentPagerAdapter(
                    getFragmentManager(), getFragmentsFromTabs(tabModels));
            viewPager.setAdapter(pagerAdapter);
            viewPager.setOnPageChangeListener(this);
            viewPager.setOffscreenPageLimit(tabModels.length);

            tabView.setAdapter(new TGListAdapter<TabModel>(this, Arrays.asList(tabModels),
                    R.layout.tiger_fragment_tab_item, TabViewHolder.class));
            tabView.setOnTabChangeListener(this);
            tabView.setSelection(0);
        }
    }

    /**
     * 从tabmodels读取Fragment数组
     * @param tabModels
     * @return
     */
    private ArrayList<Fragment> getFragmentsFromTabs(TabModel[] tabModels)
    {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        for (int i = 0; i < tabModels.length; i++)
        {
            fragments.add(tabModels[i].getFragment());
        }
        return fragments;
    }

    /**
     * 初始化tabs
     * @return
     */
    protected abstract TabModel[] onInitTabs();

    public TabModel[] getTabModels()
    {
        return tabModels;
    }

    @Override
    public void onTabChanged(TGTabView tabView, int lastTabIndex, int currentTabIndex)
    {
        //高亮显示当前Tab
        highLightCurrentTab(tabView, currentTabIndex);

        //重置上一个选中的tab，如果从来没有设置过选中项，lastTabIndex== -1
        if(lastTabIndex >= 0)
        {
            resetLastTab(tabView, lastTabIndex);
        }

        //设置ViewPager显示的页面
        viewPager.setCurrentItem(currentTabIndex, false);
    }

    /**
     * 重置上一个tab的资源
     * @param tabView
     * @param lastTabIndex 上一个tab的索引
     */
    protected void resetLastTab(TGTabView tabView, int lastTabIndex)
    {
        LOG.d("resetLastTab(tabview, int)   lastTabIndex:"+lastTabIndex);
        TabViewHolder holder = (TabViewHolder) tabView.getTabItem(lastTabIndex).getConvertView().getTag();
        TabModel tabModel = (TabModel) tabView.getAdapter().getItem(lastTabIndex);
        if(tabModel.getDefaultRes() != 0)
        {
            holder.getImageView().setImageResource(tabModel.getDefaultRes());
        }

        holder.getTextView().setTextColor(tabModel.getDefaultTextColor());
        holder.getTextView().setTextSize(tabModel.getDefaultTextSize());
    }

    /**
     * 高亮当前选中的的tab
     * @param tabView
     * @param currentTabIndex 当前tab的索引
     */
    protected void highLightCurrentTab(TGTabView tabView, int currentTabIndex)
    {
        LOG.d("highLightCurrentTab(tabview,int)   currentTabIndex:"+currentTabIndex);
        TabViewHolder holder = (TabViewHolder) tabView.getTabItem(currentTabIndex).getConvertView().getTag();
        TabModel tabModel = (TabModel) tabView.getAdapter().getItem(currentTabIndex);
        if(tabModel.getHighlightRes() != 0)
        {
            holder.getImageView().setImageResource(tabModel.getHighlightRes());
        }

        holder.getTextView().setTextColor(tabModel.getHighlightTextColor());
        holder.getTextView().setTextSize(tabModel.getHighlightTextSize());
    }

    /**
     * 获取tabView
     * @return
     */
    protected TGTabView getTabView()
    {
        return tabView;
    }

    /**
     * 获取当前选中的tab的索引
     * @return
     */
    protected int getCurrentTab()
    {
        return tabView.getCurrentTab();
    }

    /**
     * 获取ViewPager
     * @return
     */
    protected ViewPager getViewPager()
    {
        return viewPager;
    }

    @Override
    public void onPageSelected(int page)
    {
        //页码切换时，修改tab选中项
        tabView.setSelection(page);
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
    }

    @Override
    public void onPageScrolled(int page, float arg1, int arg2)
    {
    }

    /**
     * 显示徽标
     * @param tabIndex tab索引
     * @param text 文本内容
     */
    public void showBadge(int tabIndex, String text)
    {
        LOG.i("[Method:showBadge] tabIndex : " + tabIndex + "  text : " + text);
        TGBadgeView badgeView =
                ((TabViewHolder) tabView.getTabItem(tabIndex).getConvertView().getTag()).getBadgeView();

        badgeView.setText(text);
        badgeView.show();
    }

    /**
     * 显示徽标
     * @param tabIndex tab索引
     */
    public void showBadge(int tabIndex)
    {
        LOG.i("[Method:showBadge] tabIndex : "+tabIndex);
        ((TabViewHolder) tabView.getTabItem(tabIndex).getConvertView().getTag()).getBadgeView().show();
    }

    /**
     * 隐藏徽标
     * @param tabIndex tab索引
     */
    public void hideBadge(int tabIndex)
    {
        LOG.i("[Method:hideBadge] tabIndex : "+tabIndex);
        ((TabViewHolder) tabView.getTabItem(tabIndex).getConvertView().getTag()).getBadgeView().hide();
    }

    /**
     * 自定义的TabViewHolder
     * @author Dalang
     */
    public static class TabViewHolder extends TGViewHolder<TabModel>
    {
        private ImageView imageView;

        private TextView textView;

        private TGBadgeView badgeView;

        @Override
        public View initView(ViewGroup parent, int viewType)
        {
            View view = super.initView(parent, viewType);
            //设置tab均分
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT, 1);
            view.setLayoutParams(layoutParams);

            imageView = (ImageView) view.findViewById(R.id.tab_item_image);

            textView = (TextView) view.findViewById(R.id.tab_item_name);
            badgeView = new TGBadgeView(getContext(), imageView);
            badgeView.setBadgePosition(TGBadgeView.POSITION_TOP_RIGHT);
            badgeView.setBadgeMargin(0, 0, 0, 0);

            return view;
        }

        @Override
        public void fillData(ViewGroup parent, View convertView, TabModel itemData, int position, int viewType)
        {
            imageView.setImageResource(itemData.getDefaultRes());

            ViewGroup.MarginLayoutParams imageLayoutParams = (ViewGroup.MarginLayoutParams)imageView.getLayoutParams();
            imageLayoutParams.width = itemData.getImageWidth();
            imageLayoutParams.height = itemData.getImageHeight();
            imageLayoutParams.bottomMargin = itemData.getImageMarginText();
            imageView.setLayoutParams(imageLayoutParams);

            if(TextUtils.isEmpty(itemData.getTabName()))
            {
                textView.setVisibility(View.GONE);
            }
            else
            {
                textView.setVisibility(View.VISIBLE);
                textView.setText(itemData.getTabName());
                textView.setTextColor(itemData.getDefaultTextColor());
                textView.setTextSize(itemData.getDefaultTextSize());
            }

            if(itemData.getBadgeBackgroundResId() != 0)
            {
                badgeView.setBackgroundResource(itemData.getBadgeBackgroundResId());
                badgeView.setTextSize(itemData.getBadgeTextSize());
                badgeView.setTextColor(itemData.getBadgeTextColor());
            }
        }

        public ImageView getImageView()
        {
            return imageView;
        }

        public TextView getTextView()
        {
            return textView;
        }

        public TGBadgeView getBadgeView()
        {
            return badgeView;
        }
    }

    /**
     * 底部Tab数据模型
     * @author Dalang
     */
    public static class TabModel
    {
        /**
         * 默认图片资源id
         */
        private int defaultRes;

        /**
         *高亮图片资源id
         */
        private int highlightRes;

        /**
         * tab名称
         */
        private String tabName;

        /**
         * 默认的文本颜色
         */
        private int defaultTextColor = Color.BLACK;

        /**
         * 选中时高亮显示的文本颜色
         */
        private int highlightTextColor = Color.BLACK;

        /**
         * 默认的文字大小
         */
        private float defaultTextSize = 16f;

        /**
         * 选中时的文字大小
         */
        private float highlightTextSize = 16f;

        /**
         * 图片和文字之间的间距
         */
        private int imageMarginText = 0;

        /**
         * 图片高度
         */
        private int imageHeight = LinearLayout.LayoutParams.WRAP_CONTENT;

        /**
         * 图片宽度
         */
        private int imageWidth = LinearLayout.LayoutParams.WRAP_CONTENT;

        /**
         * 徽标背景资源
         */
        private int badgeBackgroundResId = 0;

        /**
         * 徽标字体大小
         */
        private float badgeTextSize = 14f;

        /**
         * 徽标颜色
         */
        private int badgeTextColor =  Color.WHITE;

        /**
         * tab对应的Fragmengt
         */
        private Fragment fragment;

        public TabModel()
        {
        }

        public int getDefaultRes()
        {
            return defaultRes;
        }

        public void setDefaultRes(int defaultRes)
        {
            this.defaultRes = defaultRes;
        }

        public String getTabName()
        {
            return tabName;
        }

        public void setTabName(String tabName)
        {
            this.tabName = tabName;
        }

        public int getHighlightRes()
        {
            return highlightRes;
        }

        public void setHighlightRes(int highlightRes)
        {
            this.highlightRes = highlightRes;
        }

        public int getDefaultTextColor()
        {
            return defaultTextColor;
        }

        public void setDefaultTextColor(int defaultTextColor)
        {
            this.defaultTextColor = defaultTextColor;
        }

        public int getHighlightTextColor()
        {
            return highlightTextColor;
        }

        public void setHighlightTextColor(int highlightTextColor)
        {
            this.highlightTextColor = highlightTextColor;
        }

        public float getDefaultTextSize()
        {
            return defaultTextSize;
        }

        public void setDefaultTextSize(float defaultTextSize)
        {
            this.defaultTextSize = defaultTextSize;
        }

        public float getHighlightTextSize()
        {
            return highlightTextSize;
        }

        public void setHighlightTextSize(float highlightTextSize)
        {
            this.highlightTextSize = highlightTextSize;
        }

        public void setBadgeBackgroundResId(int badgeBackgroundResId)
        {
            this.badgeBackgroundResId = badgeBackgroundResId;
        }

        public int getBadgeBackgroundResId()
        {
            return badgeBackgroundResId;
        }

        public void setImageMarginText(int imageMarginText)
        {
            this.imageMarginText = imageMarginText;
        }

        public int getImageMarginText()
        {
            return imageMarginText;
        }

        public void setImageHeight(int imageHeight)
        {
            this.imageHeight = imageHeight;
        }

        public int getImageHeight()
        {
            return imageHeight;
        }

        public void setImageWidth(int imageWidth)
        {
            this.imageWidth = imageWidth;
        }

        public int getImageWidth()
        {
            return imageWidth;
        }

        public void bindFragment(Fragment fragment)
        {
            this.fragment = fragment;
        }

        public Fragment getFragment()
        {
            return fragment;
        }

        public float getBadgeTextSize()
        {
            return badgeTextSize;
        }

        public int getBadgeTextColor()
        {
            return badgeTextColor;
        }

        public void setBadgeTextColor(int badgeTextColor)
        {
            this.badgeTextColor = badgeTextColor;
        }

        public void setBadgeTextSize(float badgeTextSize)
        {
            this.badgeTextSize = badgeTextSize;
        }
    }

}
