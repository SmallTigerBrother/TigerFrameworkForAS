package com.mn.tiger.download;

import java.io.File;

import org.apache.http.HttpResponse;

import com.mn.tiger.utility.FileUtils;

public class FileSizeDownloadFileChecker implements IDownloadFileChecker
{
	@Override
	public boolean isFileAlreadDownloaded(TGDownloader downloader, HttpResponse response)
	{
		File file = new File(downloader.getSavePath());
		if(file.exists())
		{
			if(downloader.getFileSize() > 0 && downloader.getFileSize() == file.length())
			{
				return true;
			}
			else
			{
				file.delete();
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean isFileCorrect(TGDownloader downloader, HttpResponse response)
	{
		return downloader.getFileSize() == FileUtils.getFileSize(downloader.getSavePath());
	}
}
