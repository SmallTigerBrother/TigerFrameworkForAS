package com.mn.tiger.widget.recyclerview;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mn.tiger.widget.pulltorefresh.IPullToRefreshView;
import com.mn.tiger.widget.recyclerview.TGRecyclerView.OnItemClickListener;

import java.util.ArrayList;

public class PullToRefreshRecyclerView extends RecyclerView implements IPullToRefreshView
{
    private Context mContext;
    private boolean isLoadingData = false;
    private int mRefreshProgressStyle = ProgressStyle.SysProgress;
    private int mLoadingMoreProgressStyle = ProgressStyle.Pacman;
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFootViews = new ArrayList<>();
    private Adapter mAdapter;
    private HeaderWrapAdapter mWrapAdapter;
    private float mLastY = -1;
    private static final float DRAG_RATE = 3;
    private BaseRefreshHeader mRefreshHeader;
    private boolean pullRefreshEnabled = true;
    private boolean loadingMoreEnabled = true;
    private static final int TYPE_REFRESH_HEADER = -5;
    private static final int TYPE_HEADER = -4;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = -3;
    private int previousTotal = 0;

    private static Handler handler = new Handler();

    private Mode mode = Mode.BOTH;

    private OnRefreshListener onRefreshListener;

    private OnItemClickListener onItemClickListener;

    public PullToRefreshRecyclerView(Context context)
    {
        this(context, null);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        this.setLayoutManager(new LinearLayoutManager(context));
        mContext = context;
        if (pullRefreshEnabled)
        {
            TGRefreshHeader refreshHeader = new TGRefreshHeader(mContext);
            mHeaderViews.add(0, refreshHeader);
            mRefreshHeader = refreshHeader;
            mRefreshHeader.setProgressStyle(mRefreshProgressStyle);
        }
        LoadingMoreFooter footView = new LoadingMoreFooter(mContext);
        footView.setProgressStyle(mLoadingMoreProgressStyle);
        addFootView(footView);
        mFootViews.get(0).setVisibility(GONE);
    }

    public void setLayoutManager(final LayoutManager layoutManager)
    {
        super.setLayoutManager(layoutManager);
        if(null != mWrapAdapter)
        {
            mWrapAdapter.onAttachedToRecyclerView(this);
        }
    }

    protected void addHeaderView(View view)
    {
        if (pullRefreshEnabled && !(mHeaderViews.get(0) instanceof ArrowRefreshHeader))
        {
            ArrowRefreshHeader refreshHeader = new ArrowRefreshHeader(mContext);
            mHeaderViews.add(0, refreshHeader);
            mRefreshHeader = refreshHeader;
            mRefreshHeader.setProgressStyle(mRefreshProgressStyle);
        }
        mHeaderViews.add(view);
        if(null != mWrapAdapter)
        {
            mWrapAdapter.adapter.setViewPositionOffset(mWrapAdapter.getHeadersCount());
        }
    }

    protected void addFootView(final View view)
    {
        mFootViews.clear();
        mFootViews.add(view);
    }

    private void loadMoreComplete()
    {
        isLoadingData = false;
        View footView = mFootViews.get(0);
        if (previousTotal < getLayoutManager().getItemCount())
        {
            if (footView instanceof LoadingMoreFooter)
            {
                ((LoadingMoreFooter) footView).setState(LoadingMoreFooter.STATE_COMPLETE);
            }
            else
            {
                footView.setVisibility(View.GONE);
            }
        }
        else
        {
            if (footView instanceof LoadingMoreFooter)
            {
                ((LoadingMoreFooter) footView).setState(LoadingMoreFooter.STATE_NOMORE);
            }
            else
            {
                footView.setVisibility(View.GONE);
            }
        }
        previousTotal = getLayoutManager().getItemCount();
    }

    private void loadingMoreComplete()
    {
        isLoadingData = false;
        View footView = mFootViews.get(0);
        if (footView instanceof LoadingMoreFooter)
        {
            ((LoadingMoreFooter) footView).setState(LoadingMoreFooter.STATE_COMPLETE);
        }
        else
        {
            footView.setVisibility(View.GONE);
        }
    }

    private void refreshComplete()
    {
        mRefreshHeader.refreshComplete();
    }

    public void setRefreshHeader(BaseRefreshHeader refreshHeader)
    {
        mRefreshHeader = refreshHeader;
    }

    private void setPullRefreshEnabled(boolean enabled)
    {
        pullRefreshEnabled = enabled;
    }

    private void setLoadingMoreEnabled(boolean enabled)
    {
        loadingMoreEnabled = enabled;
        if (!enabled)
        {
            if (mFootViews.size() > 0)
            {
                mFootViews.get(0).setVisibility(GONE);
            }
        }
    }

    public void setRefreshProgressStyle(int style)
    {
        mRefreshProgressStyle = style;
        if (mRefreshHeader != null)
        {
            mRefreshHeader.setProgressStyle(style);
        }
    }

    public void setLoadingMoreProgressStyle(int style)
    {
        mLoadingMoreProgressStyle = style;
        if (mFootViews.size() > 0 && mFootViews.get(0) instanceof LoadingMoreFooter)
        {
            ((LoadingMoreFooter) mFootViews.get(0)).setProgressStyle(style);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
        if (null != mAdapter)
        {
            ((TGRecyclerViewAdapter) mAdapter).setOnItemClickListener(this.onItemClickListener);
        }
    }

    @Override
    public void setAdapter(Adapter adapter)
    {
        mAdapter = adapter;
        if (!(adapter instanceof TGRecyclerViewAdapter<?>))
        {
            throw new RuntimeException("adapter must be an instance of TGRecyclerAdapter");
        }

        if (null != onItemClickListener)
        {
            ((TGRecyclerViewAdapter<?>) adapter).setOnItemClickListener(this.onItemClickListener);
        }

        mWrapAdapter = new HeaderWrapAdapter(mHeaderViews, mFootViews, (TGRecyclerViewAdapter)adapter);
        super.setAdapter(mWrapAdapter);
        mAdapter.registerAdapterDataObserver(mDataObserver);
    }

    @Override
    public void onScrollStateChanged(int state)
    {
        super.onScrollStateChanged(state);

        if (state == RecyclerView.SCROLL_STATE_IDLE && onRefreshListener != null && !isLoadingData && loadingMoreEnabled)
        {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager)
            {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            else if (layoutManager instanceof StaggeredGridLayoutManager)
            {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            }
            else
            {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount() && mRefreshHeader.getState() < ArrowRefreshHeader.STATE_REFRESHING)
            {

                View footView = mFootViews.get(0);
                isLoadingData = true;
                if (footView instanceof LoadingMoreFooter)
                {
                    ((LoadingMoreFooter) footView).setState(LoadingMoreFooter.STATE_LAODING);
                }
                else
                {
                    footView.setVisibility(View.VISIBLE);
                }
                onRefreshListener.onPullUpToRefresh();
            }
        }
    }

    @Override
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
                if (isOnTop() && pullRefreshEnabled)
                {
                    mRefreshHeader.onMove(deltaY / DRAG_RATE);
                    if (mRefreshHeader.getVisibleHeight() > 0 && mRefreshHeader.getState() < ArrowRefreshHeader.STATE_REFRESHING)
                    {
                        Log.i("getVisibleHeight", "getVisibleHeight = " + mRefreshHeader.getVisibleHeight());
                        Log.i("getVisibleHeight", " mRefreshHeader.getState() = " + mRefreshHeader.getState());
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && pullRefreshEnabled)
                {
                    if (mRefreshHeader.releaseAction())
                    {
                        if (onRefreshListener != null)
                        {
                            onRefreshListener.onPullDownToRefresh();
                            previousTotal = 0;
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int findMax(int[] lastPositions)
    {
        int max = lastPositions[0];
        for (int value : lastPositions)
        {
            if (value > max)
            {
                max = value;
            }
        }
        return max;
    }

    private int findMin(int[] firstPositions)
    {
        int min = firstPositions[0];
        for (int value : firstPositions)
        {
            if (value < min)
            {
                min = value;
            }
        }
        return min;
    }

    private boolean isOnTop()
    {
        if (mHeaderViews == null || mHeaderViews.isEmpty())
        {
            return false;
        }

        View view = mHeaderViews.get(0);
        if (view.getParent() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
        //        LayoutManager layoutManager = getLayoutManager();
        //        int firstVisibleItemPosition;
        //        if (layoutManager instanceof GridLayoutManager) {
        //            firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        //        } else if ( layoutManager instanceof StaggeredGridLayoutManager ) {
        //            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
        //            ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(into);
        //            firstVisibleItemPosition = findMin(into);
        //        } else {
        //            firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        //        }
        //        if ( firstVisibleItemPosition <= 1 ) {
        //             return true;
        //        }
        //        return false;
    }

    @Override
    public void setMode(Mode mode)
    {
        this.mode = mode;
        switch (mode)
        {
            case PULL_FROM_START:
                setPullRefreshEnabled(true);
                setLoadingMoreEnabled(false);
                break;
            case PULL_FROM_END:
                setPullRefreshEnabled(false);
                setLoadingMoreEnabled(true);
                break;
            case BOTH:
                setPullRefreshEnabled(true);
                setLoadingMoreEnabled(true);
                break;
            case DISABLED:
                setPullRefreshEnabled(false);
                setLoadingMoreEnabled(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void setOnRefreshListener(OnRefreshListener listener)
    {
        this.onRefreshListener = listener;
    }

    @Override
    public void onRefreshComplete()
    {
        this.refreshComplete();
        this.loadMoreComplete();
    }

    /**
     * 执行数据刷新线程，为防止卡顿掉帧，控制器将选择合适的时机刷新界面
     *
     * @param runnable
     */
    public void postRefreshRunnable(Runnable runnable)
    {
        if (!isLoadingData)
        {
            handler.postDelayed(runnable, mRefreshHeader.getAnimDuration());
        }
        else
        {
            handler.post(runnable);
        }
    }

    private final AdapterDataObserver mDataObserver = new AdapterDataObserver()
    {
        @Override
        public void onChanged()
        {
            mWrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            int offset = mWrapAdapter.getHeadersCount();
            mWrapAdapter.notifyItemRangeInserted(positionStart + offset, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount)
        {
            int offset = mWrapAdapter.getHeadersCount();
            mWrapAdapter.notifyItemRangeChanged(positionStart + offset, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload)
        {
            int offset = mWrapAdapter.getHeadersCount();
            mWrapAdapter.notifyItemRangeChanged(positionStart + offset, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount)
        {
            int offset = mWrapAdapter.getHeadersCount();
            mWrapAdapter.notifyItemRangeRemoved(positionStart + offset, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount)
        {
            int offset = mWrapAdapter.getHeadersCount();
            mWrapAdapter.notifyItemMoved(fromPosition + offset, toPosition);
        }
    };

    public static class HeaderWrapAdapter extends Adapter<ViewHolder>
    {
        private TGRecyclerViewAdapter adapter;

        private ArrayList<View> mHeaderViews;

        private ArrayList<View> mFootViews;

        private int headerPosition = 1;

        public HeaderWrapAdapter(ArrayList<View> headerViews, ArrayList<View> footViews, TGRecyclerViewAdapter adapter)
        {
            this.adapter = adapter;
            this.mHeaderViews = headerViews;
            this.mFootViews = footViews;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView)
        {
            super.onAttachedToRecyclerView(recyclerView);
            final LayoutManager layoutManager = recyclerView.getLayoutManager();
            if(layoutManager instanceof GridLayoutManager)
            {
                ((GridLayoutManager) layoutManager).setSpanSizeLookup(new HeaderSpanSizeLookup(this, (GridLayoutManager) layoutManager,
                        new TGRecyclerViewAdapter.TGSpanSizeLookup(this.adapter)));
            }
            this.adapter.setViewPositionOffset(getHeadersCount());
            this.adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder)
        {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition())))
            {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) lp;
                layoutParams.setFullSpan(true);
            }

            if (holder instanceof TGRecyclerViewAdapter.InternalRecyclerViewHolder)
            {
                adapter.onViewAttachedToWindow((TGRecyclerViewAdapter.InternalRecyclerViewHolder)holder);
            }
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder)
        {
            super.onViewDetachedFromWindow(holder);
            if (holder instanceof TGRecyclerViewAdapter.InternalRecyclerViewHolder)
            {
                TGRecyclerViewAdapter.InternalRecyclerViewHolder viewHolder = (TGRecyclerViewAdapter.InternalRecyclerViewHolder)holder;
                adapter.onViewDetachedFromWindow(viewHolder);
            }
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView)
        {
            super.onDetachedFromRecyclerView(recyclerView);
            this.adapter.onDetachedFromRecyclerView(recyclerView);
        }

        public boolean isHeader(int position)
        {
            return position >= 0 && position < mHeaderViews.size();
        }

        public boolean isFooter(int position)
        {
            return position < getItemCount() && position >= getItemCount() - mFootViews.size();
        }

        public boolean isRefreshHeader(int position)
        {
            return position == 0;
        }

        public int getHeadersCount()
        {
            return mHeaderViews.size();
        }

        public int getFootersCount()
        {
            return mFootViews.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            if (viewType == TYPE_REFRESH_HEADER)
            {
                return new SimpleViewHolder(mHeaderViews.get(0));
            }
            else if (viewType == TYPE_HEADER)
            {
                return new SimpleViewHolder(mHeaderViews.get(headerPosition++));
            }
            else if (viewType == TYPE_FOOTER)
            {
                return new SimpleViewHolder(mFootViews.get(0));
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            if (isHeader(position))
            {
                return;
            }
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (adapter != null)
            {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount)
                {
                    adapter.onBindViewHolder((TGRecyclerViewAdapter.InternalRecyclerViewHolder)holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public void onViewRecycled(ViewHolder holder)
        {
            super.onViewRecycled(holder);
            if (isHeader(holder.getAdapterPosition()))
            {
                return;
            }
            int adjPosition = holder.getAdapterPosition() - getHeadersCount();
            int adapterCount;
            if (adapter != null)
            {
                adapterCount = adapter.getItemCount();
                if (adjPosition >= 0 && adjPosition < adapterCount)
                {
                    adapter.onViewRecycled((TGRecyclerViewAdapter.InternalRecyclerViewHolder) holder);
                    return;
                }
            }
        }

        @Override
        public int getItemCount()
        {
            if (adapter != null)
            {
                return getHeadersCount() + getFootersCount() + adapter.getItemCount();
            }
            else
            {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position)
        {
            if (isRefreshHeader(position))
            {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position))
            {
                return TYPE_HEADER;
            }
            if (isFooter(position))
            {
                return TYPE_FOOTER;
            }
            int adjPosition = position - getHeadersCount();
            ;
            int adapterCount;
            if (adapter != null)
            {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount)
                {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return TYPE_NORMAL;
        }

        @Override
        public long getItemId(int position)
        {
            if (adapter != null && position >= getHeadersCount())
            {
                int adjPosition = position - getHeadersCount();
                int adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount)
                {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -position;
        }

        private class SimpleViewHolder extends ViewHolder
        {
            public SimpleViewHolder(View itemView)
            {
                super(itemView);
            }
        }
    }

    static class HeaderSpanSizeLookup extends GridLayoutManager.SpanSizeLookup
    {
        private HeaderWrapAdapter adapter;

        private GridLayoutManager layoutManager;

        private TGRecyclerViewAdapter.TGSpanSizeLookup internalSpanSizeLookup;

        private int headCount;

        HeaderSpanSizeLookup(HeaderWrapAdapter adapter, GridLayoutManager layoutManager,
                             TGRecyclerViewAdapter.TGSpanSizeLookup internalSpanSizeLookup)
        {
            this.adapter = adapter;
            this.layoutManager = layoutManager;
            this.internalSpanSizeLookup = internalSpanSizeLookup;
            this.headCount = adapter.getHeadersCount();
        }

        @Override
        public int getSpanSize(int position)
        {
            return (adapter.isHeader(position) || adapter.isFooter(position))
                    ? (layoutManager).getSpanCount() : internalSpanSizeLookup.getSpanSize(position - headCount);
        }
    }

}
