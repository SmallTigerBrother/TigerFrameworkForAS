package com.mn.tiger.widget.pulltorefresh;

import com.mn.tiger.widget.pulltorefresh.library.IPullToRefreshAdapterView;

/**
 * Interface definition for a callback to be invoked when the list or grid
 * has been scrolled.
 */
public interface OnScrollListener {

    /**
     * The view is not scrolling. Note navigating the list using the trackball counts as
     * being in the idle state since these transitions are not animated.
     */
    public static int SCROLL_STATE_IDLE = 0;

    /**
     * The user is scrolling using touch, and their finger is still on the screen
     */
    public static int SCROLL_STATE_TOUCH_SCROLL = 1;

    /**
     * The user had previously been scrolling using touch and had performed a fling. The
     * animation is now coasting to a stop
     */
    public static int SCROLL_STATE_FLING = 2;

    /**
     * Callback method to be invoked while the list view or grid view is being scrolled. If the
     * view is being scrolled, this method will be called before the next frame of the scroll is
     * rendered. In particular, it will be called before any calls to
     * {@link android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)}.
     *
     * @param view The view whose scroll state is being reported
     *
     * @param scrollState The current scroll state. One of {@link #SCROLL_STATE_IDLE},
     * {@link #SCROLL_STATE_TOUCH_SCROLL} or {@link #SCROLL_STATE_IDLE}.
     */
    public void onScrollStateChanged(IPullToRefreshAdapterView view, int scrollState);

    /**
     * Callback method to be invoked when the list or grid has been scrolled. This will be
     * called after the scroll has completed
     * @param view The view whose scroll state is being reported
     * @param firstVisibleItem the index of the first visible cell (ignore if
     *        visibleItemCount == 0)
     * @param visibleItemCount the number of visible cells
     * @param totalItemCount the number of items in the list adaptor
     */
    public void onScroll(IPullToRefreshAdapterView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount);
}

