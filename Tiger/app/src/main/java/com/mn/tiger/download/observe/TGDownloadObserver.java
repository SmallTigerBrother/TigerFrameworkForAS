package com.mn.tiger.download.observe;

import com.mn.tiger.download.TGDownloader;
import com.mn.tiger.log.LogTools;

/**
 * 该类作用及功能说明 ：数据观察者
 * 
 * @date 2014年3月31日
 */
public class TGDownloadObserver
{
	/**
	 * log tag
	 */
	private static final String TAG = TGDownloadObserver.class.getSimpleName();
	
	/**
	 * 注册observer时使用得key
	 */
	private String key;

	/**
	 * 构造函数
	 * 
	 * @date 2014年6月29日
	 */
	public TGDownloadObserver()
	{
	}

	/**
	 * 
	 * 该方法的作用:获取键值
	 * 
	 * @date 2014年6月29日
	 * @return
	 */
	String getKey()
	{
		return key;
	}

	/**
	 * 
	 * 该方法的作用:设置键值
	 * 
	 * @date 2014年6月29日
	 * @param key
	 */
	void setKey(String key)
	{
		this.key = key;
	}

	/**
	 * 
	 * 该方法的作用: 任务开始回调
	 * 
	 * @date 2014年6月26日
	 * @param progress
	 */
	public void onStart(TGDownloader downloader)
	{
		LogTools.d(TAG, "[Method:onStart]");
	}
	
	/**
	 * 
	 * 该方法的作用: 任务进度变化回调
	 * 
	 * @date 2014年6月26日
	 * @param progress
	 */
	public void onProgress(TGDownloader downloader, int progress)
	{
		LogTools.d(TAG, "[Method:onProgress] progress: " + progress);
	}

	/**
	 * 
	 * 该方法的作用: 任务完成回调
	 * 
	 * @date 2014年6月26日
	 */
	public void onSuccess(TGDownloader downloader)
	{
		LogTools.p(TAG, "[Method:onSuccess]");
	}

	/**
	 * 
	 * 该方法的作用: 任务失败回调
	 * 
	 * @date 2014年6月27日
	 * @param errorCode
	 * @param errorMessage
	 */
	public void onFailed(TGDownloader downloader, int errorCode, String errorMessage)
	{
		LogTools.e(TAG, "[Method:onFailed] errorCode: " + errorCode + "; errorMessage: "
				+ errorMessage);
	}

	/**
	 * 
	 * 该方法的作用: 任务停止回调
	 * 
	 * @date 2014年6月27日
	 */
	public void onPause(TGDownloader downloader)
	{
		LogTools.p(TAG, "[Method:onPause]");
	}
	
	/**
	 * 任务取消回调
	 * @param downloader
	 */
	public void onCancel(TGDownloader downloader)
	{
		LogTools.p(TAG, "[Method:onPause]");
	}
}
