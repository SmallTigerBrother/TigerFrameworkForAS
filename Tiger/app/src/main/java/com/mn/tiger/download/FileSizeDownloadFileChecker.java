package com.mn.tiger.download;

import java.io.File;

import org.apache.http.HttpResponse;

import com.mn.tiger.utility.FileUtils;

public class FileSizeDownloadFileChecker implements IDownloadFileChecker
{
	@Override
	public boolean isFileAlreadyDownloaded(TGDownloader downloader)
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
				if(!downloader.isBreakPoints())
				{
					file.delete();
				}
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean isFileCorrect(TGDownloader downloader)
	{
		return downloader.getFileSize() == FileUtils.getFileSize(downloader.getSavePath());
	}
}
