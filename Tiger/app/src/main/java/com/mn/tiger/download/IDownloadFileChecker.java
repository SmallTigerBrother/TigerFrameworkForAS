package com.mn.tiger.download;

import org.apache.http.HttpResponse;

/**
 * 下载文件校验类
 */
public interface IDownloadFileChecker
{
	/**
	 * 判断文件是否已下载
	 * @param downloader
	 * @param response
	 * @return
	 */
	boolean isFileAlreadDownloaded(TGDownloader downloader, HttpResponse response);
	
	/**
	 * 判断已下载的文件是否正确
	 * @param downloader
	 * @param response
	 * @return
	 */
	boolean isFileCorrect(TGDownloader downloader, HttpResponse response);
}
