package com.mn.tiger.utility;

import android.widget.AbsListView;
import android.widget.ImageView;

import com.mn.tiger.app.TGApplication;
import com.mn.tiger.widget.viewflow.internal.PLA_AbsListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Imageloader工具类
 */
public class ImageLoaderUitls
{
	/**
	 * 异步加载图片
	 * @param uri 图片地址
	 * @param imageView
	 */
	public static void displayImage(String uri, ImageView imageView)
	{
		TGApplication.getInstance().getImageLoader().displayImage(uri, imageView);
	}

	/**
	 * 异步加载图片
	 * @param uri 图片地址
	 * @param imageAware
	 */
	public static void displayImage(String uri, ImageAware imageAware)
	{
		TGApplication.getInstance().getImageLoader().displayImage(uri, imageAware);
	}


	/**
	 * 异步加载图片 
	 * @param uri 图片地址
	 * @param imageView
	 * @param listener 图片加载监听接口
	 */
	public static void displayImage(String uri, ImageView imageView, ImageLoadingListener listener)
	{
		TGApplication.getInstance().getImageLoader().displayImage(uri, imageView, listener);
	}

	/**
	 * 异步加载图片
	 * @param uri 图片地址
	 * @param imageView
	 * @param options 图片显示参数
	 */
	public static void displayImage(String uri, ImageView imageView, DisplayImageOptions options)
	{
		TGApplication.getInstance().getImageLoader().displayImage(uri, imageView, options);
	}

	/**
	 * 异步加载图片
	 * @param uri 图片地址
	 * @param imageView
	 * @param options 图片显示参数
	 * @param listener 图片加载监听接口 
	 */
	public static void displayImage(String uri, ImageView imageView, DisplayImageOptions options,
									ImageLoadingListener listener)
	{
		TGApplication.getInstance().getImageLoader().displayImage(uri, imageView,
				options, listener);
	}

	/**
	 * 异步下载图片
	 * @param uri 图片地址
	 * @param listener 图片加载监听接口
	 */
	public static void loadImage(String uri, ImageLoadingListener listener)
	{
		TGApplication.getInstance().getImageLoader().loadImage(uri, listener);
	}

	/**
	 * 异步下载图片
	 * @param uri 图片地址
	 * @param options 图片显示参数
	 * @param listener 图片加载监听接口
	 */
	public static void loadImage(String uri, DisplayImageOptions options, ImageLoadingListener listener)
	{
		TGApplication.getInstance().getImageLoader().loadImage(uri, options, listener);
	}

	/**
	 * 获取磁盘缓存的大小
	 * @return 磁盘缓存大小，单位为byte
	 */
	@SuppressWarnings("deprecation")
	public static double getDiskCacheSize()
	{
		return FileUtils.getDirSize(TGApplication.getInstance().getImageLoader().getDiskCache().getDirectory());
	}

	/**
	 * 清理磁盘缓存
	 */
	public static void clearDiskCache()
	{
		TGApplication.getInstance().getImageLoader().clearDiskCache();
	}

	/**
	 * 清理内存缓存
	 */
	public static void clearMemoryCache()
	{
		TGApplication.getInstance().getImageLoader().clearMemoryCache();
	}

	/**
	 * 暂停ImageLoader
	 */
	public static void pauseImageLoader()
	{
		TGApplication.getInstance().getImageLoader().pause();
	}

	/**
	 * 重启ImageLoader
	 */
	public static void resumeImageLoader()
	{
		TGApplication.getInstance().getImageLoader().resume();
	}

	/**
	 * 创建一个懒加载的OnScrollListener，当屏幕滚动时不加载图片
	 * @return
	 */
	public static AbsListView.OnScrollListener newLazyLoadOnScrollListener()
	{
		return  new AbsListView.OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				switch (scrollState)
				{
					case SCROLL_STATE_IDLE:
					case SCROLL_STATE_TOUCH_SCROLL:
						ImageLoaderUitls.resumeImageLoader();
						break;
					case SCROLL_STATE_FLING:
						ImageLoaderUitls.pauseImageLoader();
						break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
			}
		};
	}

	/**
	 * 创建一个懒加载的PLAOnScrollListener，当屏幕滚动时不加载图片
	 * @return
	 */
	public static PLA_AbsListView.OnScrollListener newLazyloadPLAOnScrollListener()
	{
		return new PLA_AbsListView.OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(PLA_AbsListView view, int scrollState)
			{
				switch (scrollState)
				{
					case SCROLL_STATE_IDLE:
					case SCROLL_STATE_TOUCH_SCROLL:
						ImageLoaderUitls.resumeImageLoader();
						break;
					case SCROLL_STATE_FLING:
						ImageLoaderUitls.pauseImageLoader();
						break;
				}
			}

			@Override
			public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
			}
		};
	}
}
