package com.mn.tiger.download;

import android.text.TextUtils;

import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.Commons;
import com.mn.tiger.utility.FileUtils;
import com.mn.tiger.utility.MD5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * MD5校验码检查器
 */
public class MD5DownloadFileChecker implements IDownloadFileChecker
{
	private static final Logger LOG = Logger.getLogger(MD5DownloadFileChecker.class);
	
	@Override
	public boolean isFileAlreadyDownloaded(TGDownloader downloader)
	{
		return false;
	}

	@Override
	public boolean isFileCorrect(TGDownloader downloader)
	{
		return equalDownloadFileCheckStr(downloader.getUrl(), downloader.getCheckKey());
	}
	
	/**
	 * 该方法的作用: 校验下载完成后的文件流校验值是否和服务端下发的校验值一致
	 *           返回true, 表示下载文件与服务端文件一致；否则不一致
	 *           默认为MD5校验
	 * @date 2014年7月16日
	 * @param fileUrl
	 * @param serverCheckString
	 * @return
	 */
	private boolean equalDownloadFileCheckStr(String fileUrl, String serverCheckString)
	{
		// 服务端没返回md5时，不做校验
		if(TextUtils.isEmpty(serverCheckString))
		{
			return true;
		}
		
		// 文件不存在，返回校验不通过
		File file = FileUtils.getFile(fileUrl);
		if(file == null || !file.exists())
		{
			LOG.e("[method:equalDownloadFileCheckStr]: " + "file is not exist.");
			return false;
		}
		
		String fileCheckStr = getLocalFileCheckStr(fileUrl);
		LOG.i("[method:equalDownloadFileCheckStr], " + "fileCheckString: " + fileCheckStr
				+ "\r\n ; serverCheckString: " + serverCheckString + "\r\n ; url: " + fileUrl);
		if (null != fileCheckStr)
		{
			if (fileCheckStr.equals(serverCheckString))
			{
				return true;
			}
			else
			{
				// 删除错误文件
				file.delete();
				
				LOG.e("[method:equalDownloadFileCheckStr]: " + "check string is not the same.");
			}
		}

		return false;
	}
	
	/**
	 * 
	 * 该方法的作用: 获取本地文件加密字符串: 默认为MD5加密
	 * @date 2014年8月23日
	 * @param filePath
	 * @return
	 */
	protected String getLocalFileCheckStr(String filePath)
	{
		File file = FileUtils.getFile(filePath);
		InputStream inputStream = null;
		String fileCheckStr = "";
		try
		{
			inputStream = new FileInputStream(file);
			fileCheckStr = MD5.md5sum(inputStream);
		}
		catch (FileNotFoundException e)
		{
			LOG.e("[method:equalDownloadFileCheckStr]: " + e.getMessage(), e);
		}
		finally
		{
			Commons.closeInputStream(inputStream);
		}
		
		return fileCheckStr;
	}


}
