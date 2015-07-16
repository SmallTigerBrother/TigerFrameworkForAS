package com.mn.tiger.utility;

import android.widget.ImageView;

import com.mn.tiger.app.TGApplication;
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
}
