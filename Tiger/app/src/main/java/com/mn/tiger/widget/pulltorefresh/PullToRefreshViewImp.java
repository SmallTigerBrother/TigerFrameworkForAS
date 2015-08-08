package com.mn.tiger.widget.pulltorefresh;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.mn.tiger.R;
import com.mn.tiger.log.Logger;
import com.mn.tiger.widget.pulltorefresh.library.IPullToRefreshAdapterView;
import com.mn.tiger.widget.pulltorefresh.library.IPullToRefreshView;
import com.mn.tiger.widget.pulltorefresh.library.PullToRefreshBase.Mode;
import com.mn.tiger.widget.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.mn.tiger.widget.pulltorefresh.loading.ILoadingFooterView;
import com.mn.tiger.widget.pulltorefresh.loading.LoadingFooterView;
import com.mn.tiger.widget.pulltorefresh.loading.LoadingHeaderView;

/**
 * 拖动刷新AdapterView实现类（封装拖动、刷新等功能）
 */
abstract class PullToRefreshViewImp implements OnScrollListener
{
    private static final Logger LOG = Logger.getLogger(PullToRefreshViewImp.class);
    
    private float mLastY = -1; // save event y
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener
    
    // the interface to trigger refresh and load more.
    private OnRefreshListener onRereshListener;
    
    // -- header view
    public LoadingHeaderView mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private RelativeLayout mHeaderViewContent;
    private int mHeaderViewHeight; // header view's height
    private boolean mEnablePullRefresh = true;
    private boolean mPullRefreshing = false; // is refreashing.
    
    // -- footer view
    private ILoadingFooterView mFooterView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;
    private boolean mIsFooterReady = false;
    
    private boolean autoLoadWhileEnd = false;
    
    // total list items, used to detect is at the bottom of listview.
    private int mTotalItemCount;
    
    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;
    
    private final static int SCROLL_DURATION = 400; // scroll back duration
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
    // at bottom, trigger
    // load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
    // feature.
    
    private IPullToRefreshView view;//真正执行拖动刷新的类
    
    public PullToRefreshViewImp(IPullToRefreshView view)
    {
        this.view = view;
        init();
    }
    
    /**
     * 初始化
     */
    private void init()
    {
        mScroller = new Scroller(view.getContext(), new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        view.setSuperOnSrcollListener(this);
        
        // init header view
        mHeaderView = new LoadingHeaderView(view.getContext());
        mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.xlistview_header_content);
        view.addHeaderView(mHeaderView);
        
        // init footer view
        setFooterView(new LoadingFooterView(view.getContext()));
        
        // init header height
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
                                                                    {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout()
            {
                mHeaderViewHeight = mHeaderViewContent.getHeight();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }
    
    public IPullToRefreshView getView()
    {
        return view;
    }
    
    /**
     * 添加footerView
     */
    public void addFooterViewIfNeed()
    {
        // make sure LoadingFooterView is the last footer view, and only add once.
        if (mIsFooterReady == false)
        {
            mIsFooterReady = true;
            view.addFooterView((View) getFooterView());
            getFooterView().setOnClickListener(new OnClickListener()
                                               {
                @Override
                public void onClick(View v)
                {
                    startLoadMore();
                }
            });
        }
    }
    
    public void setMode(Mode mode)
    {
        switch (mode)
        {
            case PULL_FROM_START:
                setPullRefreshEnable(true);
                setPullLoadEnable(false);
                break;
            case PULL_FROM_END:
                setPullRefreshEnable(false);
                setPullLoadEnable(true);
                break;
            case BOTH:
                setPullRefreshEnable(true);
                setPullLoadEnable(true);
                break;
                
            case DISABLED:
                setPullLoadEnable(false);
                setPullRefreshEnable(false);
                break;
                
            default:
                break;
        }
    }
    
    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    private void setPullRefreshEnable(boolean enable)
    {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh)
        { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        }
        else
        {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    private void setPullLoadEnable(boolean enable)
    {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad)
        {
            // mFooterView.hide();
            ((View) getFooterView()).setVisibility(View.GONE);
            ((View) getFooterView()).setOnClickListener(null);
        }
        else
        {
            mPullLoading = false;
            // mFooterView.show();
            ((View) getFooterView()).setVisibility(View.VISIBLE);
            getFooterView().setState(LoadingFooterView.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
        }
    }
    
    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh()
    {
        if (mPullRefreshing == true)
        {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }
    
    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore()
    {
        if (mPullLoading == true)
        {
            mPullLoading = false;
            getFooterView().setState(LoadingFooterView.STATE_NORMAL);
        }
    }
    
    /**
     * 停止刷新和加载
     */
    public void stopRefreshAndLoadMore()
    {
        stopRefresh();
        stopLoadMore();
    }
    
    /**
     * 更新headerView高度
     *
     * @param delta
     */
    private void updateHeaderHeight(float delta)
    {
        if (((int) delta + mHeaderView.getVisiableHeight()) < (mHeaderViewHeight + 30))
        {
            mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
            if (mEnablePullRefresh && !mPullRefreshing)
            { // 未处于刷新状态，更新箭头
                if (mHeaderView.getVisiableHeight() > mHeaderViewHeight)
                {
                    mHeaderView.setState(LoadingHeaderView.STATE_READY);
                }
                else
                {
                    mHeaderView.setState(LoadingHeaderView.STATE_NORMAL);
                }
            }
            view.setSelection(0); // scroll to top each time
        }
    }
    
    /**
     * reset header view's height.
     */
    private void resetHeaderHeight()
    {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight)
        {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight)
        {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        // trigger computeScroll
        view.invalidate();
    }
    
    private void updateFooterHeight(float delta)
    {
        int height = getFooterView().getBottomMargin() + (int) delta;
        
        if (height < PULL_LOAD_MORE_DELTA + 30)
        {
            if (mEnablePullLoad && !mPullLoading)
            {
                if (height > PULL_LOAD_MORE_DELTA)
                { // height enough to invoke load
                    // more.
                    getFooterView().setState(LoadingFooterView.STATE_READY);
                }
                else
                {
                    getFooterView().setState(LoadingFooterView.STATE_NORMAL);
                }
            }
            getFooterView().setBottomMargin(height);
        }
        
        // setSelection(mTotalItemCount - 1); // scroll to bottom
    }
    
    private void resetFooterHeight()
    {
        int bottomMargin = getFooterView().getBottomMargin();
        if (bottomMargin > 0)
        {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            view.invalidate();
        }
    }
    
    private void startLoadMore()
    {
        mPullLoading = true;
        getFooterView().setState(LoadingFooterView.STATE_LOADING);
        if (onRereshListener != null)
        {
            onRereshListener.onPullUpToRefresh();
        }
    }
    
    public boolean onTouchEvent(MotionEvent ev)
    {
        if (mLastY == -1)
        {
            mLastY = ev.getRawY();
        }
        
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isReadPullStart() && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0))
                {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                }
                else if (isReadPullEnd() && (getFooterView().getBottomMargin() > 0 || deltaY < 0))
                {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (isReadPullStart())
                {
                    // invoke refresh
                    if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight)
                    {
                        mPullRefreshing = true;
                        mHeaderView.setState(LoadingHeaderView.STATE_REFRESHING);
                        if (onRereshListener != null)
                        {
                            onRereshListener.onPullDownToRefresh();
                        }
                    }
                    resetHeaderHeight();
                }
                else if (isReadPullEnd())
                {
                    // invoke load more.
                    if (mEnablePullLoad && getFooterView().getBottomMargin() > PULL_LOAD_MORE_DELTA && !autoLoadWhileEnd)
                    {
                        LOG.d("[Method:onTouchEvent] invoke load more");
                        startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
        }
        return view.superOnTouchEvent(ev);
    }
    
    public abstract boolean isReadPullStart();
    
    public abstract boolean isReadPullEnd();
    
    public void computeScroll()
    {
        if (mScroller.computeScrollOffset())
        {
            if (mScrollBack == SCROLLBACK_HEADER)
            {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            }
            else
            {
                getFooterView().setBottomMargin(mScroller.getCurrY());
            }
            view.postInvalidate();
        }
        view.superComputeScroll();
    }
    
    public void setOnScrollListener(OnScrollListener listener)
    {
        this.mScrollListener = listener;
    }
    
    @Override
    public void onScroll(IPullToRefreshAdapterView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount)
    {
        mTotalItemCount = totalItemCount;
        
        if (mScrollListener != null)
        {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        
        if (totalItemCount > visibleItemCount && mEnablePullLoad)
        {
            if (null != getFooterView())
            {
                ((View) getFooterView()).setVisibility(View.VISIBLE);
            }
            
            if(isReadPullEnd() && !mPullLoading && autoLoadWhileEnd)
            {
                LOG.d("[Method:onScroll] invoke load more");
                startLoadMore();
            }
        }
        else
        {
            if (null != getFooterView())
            {
                ((View) getFooterView()).setVisibility(View.GONE);
            }
        }
    }
    
    public int getTotalItemCount()
    {
        return mTotalItemCount;
    }
    
    @Override
    public void onScrollStateChanged(IPullToRefreshAdapterView view, int scrollState)
    {
        if (mScrollListener != null)
        {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }
    
    /**
     * 设置拖动加载监听器
     *
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener)
    {
        this.onRereshListener = listener;
    }
    
    /**
     * 呈现刷新
     *
     * @author liananse 2013-9-2
     */
    public void setRefreshState()
    {
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
                                                                    {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout()
            {
                mHeaderViewHeight = mHeaderViewContent.getHeight();
                updateHeaderHeight(mHeaderViewHeight);
                mPullRefreshing = true;
                mHeaderView.setState(LoadingHeaderView.STATE_REFRESHING);
                resetHeaderHeight();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }
    
    public ILoadingFooterView getFooterView()
    {
        return mFooterView;
    }
    
    public void setFooterView(ILoadingFooterView mFooterView)
    {
        this.mFooterView = mFooterView;
    }
    
    public void setAutoLoadWhileEnd(boolean autoLoadWhileEnd)
    {
        this.autoLoadWhileEnd = autoLoadWhileEnd;
    }
}
