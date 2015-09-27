package com.mn.tiger.download;

/**
 * 
 * 该类作用及功能说明: 下载监听
 * 
 * @date 2014年8月18日
 */
public interface IDownloadListener
{
	void downloadStart(TGDownloader downloader);
	
	void downloadSucceed(TGDownloader downloader);
	
	void downloadProgress(TGDownloader downloader, int progress);
	
	void downloadFailed(TGDownloader downloader);
	
	void downloadPause(TGDownloader downloader);
	
	void downloadCanceled(TGDownloader downloader);
}
