package com.mn.tiger.download;

/**
 * 
 * 该类作用及功能说明: 下载监听
 * 
 * @date 2014年8月18日
 */
public interface IDownloadListener
{
	public void downloadStart(TGDownloader downloader);
	
	public void downloadSucceed(TGDownloader downloader);
	
	public void downloadProgress(TGDownloader downloader, int progress);
	
	public void downloadFailed(TGDownloader downloader);
	
	public void downloadPause(TGDownloader downloader);
	
	public void downloadCanceled(TGDownloader downloader);
}
