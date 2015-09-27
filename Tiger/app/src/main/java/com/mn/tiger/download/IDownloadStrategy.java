package com.mn.tiger.download;

/**
 * 
 * 该类作用及功能说明: 下载策略接口
 * 
 * @date 2014年8月18日
 */
public interface IDownloadStrategy
{
	void download(TGDownloadParams downloadParams);
	
	void cancel();
}
