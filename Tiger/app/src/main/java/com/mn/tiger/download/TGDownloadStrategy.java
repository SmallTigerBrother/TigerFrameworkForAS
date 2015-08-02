package com.mn.tiger.download;

import android.content.Context;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.request.HttpType;
import com.mn.tiger.request.method.ApacheGetMethod;
import com.mn.tiger.request.method.ApacheHttpMethod;
import com.mn.tiger.request.method.ApachePostMethod;
import com.mn.tiger.request.method.TGHttpParams;

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
	
	private DownloadHttpClient downloadHttpClient;
	
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

		downloadHttpClient = new DownloadHttpClient(context, downloader, 
				downloadTask);		
		// 创建请求方法
		ApacheHttpMethod httpMethod = getHttpMethod(downloader);
		
		// 执行下载操作
		downloadHttpClient.execute(httpMethod);
	}

	/**
	 * 该方法的作用:获取请求方法类
	 * @date 2014年1月23日
	 * @param downloader
	 * @return
	 */
	protected ApacheHttpMethod getHttpMethod(TGDownloader downloader)
	{
		LogTools.i(LOG_TAG, "[Method:getHttpMethod] requestUrl:" + downloader.getUrl());
		// 创建post请求的方法
		ApacheHttpMethod httpMethod = null;
		if (downloader.getRequestType() == HttpType.REQUEST_GET)
		{
			httpMethod = new ApacheGetMethod();
		}
		else
		{
			httpMethod = new ApachePostMethod();
		}
		httpMethod.setUrl(downloader.getUrl());
		TGHttpParams httpParams = new TGHttpParams();
		httpParams.setStringParams(downloader.getParams());
		httpMethod.setReqeustParams(httpParams);
		//设置是否支持断点续传
		if(this.downloader.isBreakPoints())
		{
			httpMethod.setProperty("Range", "bytes="+ this.downloader.getCompleteSize() + "-" + (this.downloader.getFileSize() - 1));
		}

		return httpMethod;
	}
	
	@Override
	public void shutdown()
	{
		downloadHttpClient.shutdown();
	}
}
