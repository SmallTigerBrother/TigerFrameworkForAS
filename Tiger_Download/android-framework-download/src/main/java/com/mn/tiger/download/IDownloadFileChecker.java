package com.mn.tiger.download;

import com.mn.tiger.download.db.TGDownloader;

/**
 * 下载文件校验类
 */
public interface IDownloadFileChecker
{
	/**
	 * 判断文件是否已下载
	 * @param downloader
	 * @return
	 */
	boolean isFileAlreadyDownloaded(TGDownloader downloader);
	
	/**
	 * 判断已下载的文件是否正确
	 * @param downloader
	 * @return
	 */
	boolean isFileCorrect(TGDownloader downloader);
}
