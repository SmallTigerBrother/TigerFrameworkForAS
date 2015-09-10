package com.mn.tiger.widget.pulltorefresh.library;

public interface IPullToRefreshView
{
	void setMode(Mode mode);

	void setOnRefreshListener(OnRefreshListener listener);

	void onRefreshComplete();

	/**
	 * An advanced version of the Listener to listen for callbacks to Refresh.
	 * This listener is different as it allows you to differentiate between Pull
	 * Ups, and Pull Downs.
	 *
	 * @author Chris Banes
	 */
	public interface OnRefreshListener
	{
		// TODO These methods need renaming to START/END rather than DOWN/UP

		/**
		 * onPullDownToRefresh will be called only when the user has Pulled from
		 * the start, and released.
		 */
		void onPullDownToRefresh();

		/**
		 * onPullUpToRefresh will be called only when the user has Pulled from
		 * the end, and released.
		 */
		void onPullUpToRefresh();
	}

	public static enum Mode
	{

		/**
		 * Disable all Pull-to-Refresh gesture and Refreshing handling
		 */
		DISABLED,

		/**
		 * Only allow the user to Pull from the start of the Refreshable View to
		 * refresh. The start is either the Top or Left, depending on the
		 * scrolling direction.
		 */
		PULL_FROM_START,

		/**
		 * Only allow the user to Pull from the end of the Refreshable View to
		 * refresh. The start is either the Bottom or Right, depending on the
		 * scrolling direction.
		 */
		PULL_FROM_END,

		/**
		 * Allow the user to both Pull from the start, from the end to refresh.
		 */
		BOTH,
	}
}
