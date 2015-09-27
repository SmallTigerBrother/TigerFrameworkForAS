package com.mn.tiger.download;

import android.content.Context;

import com.mn.tiger.log.LogTools;

/**
 * 下载策略类
 */
public class TGDownloadStrategy implements IDownloadStrategy
{
	/**
	 * 日志标识
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();

	/**
	 * 上下文信息
	 */
	protected Context context;

	/**
	 * 下载任务
	 */
	protected TGDownloadTask downloadTask = null;
	
	/**
	 * 下载信息
	 */
	protected TGDownloader downloader;
	
	private TGDownloadHttpClient downloadHttpClient;
	
	/**
	 * 构造函数
	 * @date 2014年8月5日
	 * @param context
	 * @param downloadTask
	 */
	public TGDownloadStrategy(Context context, TGDownloadTask downloadTask)
	{
		this.context = context;
		this.downloadTask = downloadTask;
	}

	@Override
	public void download(TGDownloadParams downloadParams)
	{
		LogTools.p(LOG_TAG, "[Method:download]");
		// 获取下载参数
		downloader = TGDownloader.getInstance(context, downloadParams, downloadTask.getTaskID());

		// 执行下载
		executeDownload(context, downloader);
	}
	
	/**
	 * 该方法的作用: 请求下载
	 * @date 2014年8月5日
	 * @param context
	 * @param downloader
	 */
	protected void executeDownload(Context context, TGDownloader downloader)
	{
		LogTools.p(LOG_TAG, "[Method:executeDownload]");

		downloadHttpClient = new OKHttpDownloadClient(context, downloader,
				downloadTask);		

		// 执行下载操作
		downloadHttpClient.execute();
	}

	@Override
	public void cancel()
	{
		downloadHttpClient.cancel();
	}
}
