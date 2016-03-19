package com.mn.tiger.download;

import com.mn.tiger.download.db.TGDownloader;
import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.FileUtils;

import java.io.File;

public class FileSizeDownloadFileChecker implements IDownloadFileChecker
{
	private static final Logger LOG = Logger.getLogger(FileSizeDownloadFileChecker.class);

	@Override
	public boolean isFileAlreadyDownloaded(TGDownloader downloader)
	{
		File file = new File(downloader.getSavePath());
		if(file.exists())
		{
			if(downloader.getFileSize() > 0 && downloader.getFileSize() == file.length())
			{
				LOG.d("[Method:isFileAlreadyDownloaded] true");
				return true;
			}
			else
			{
				if(!downloader.getAccessRanges())
				{
					file.delete();
				}
				LOG.d("[Method:isFileAlreadyDownloaded] false");
				return false;
			}
		}
		else
		{
			LOG.d("[Method:isFileAlreadyDownloaded] false");
			return false;
		}
	}

	@Override
	public boolean isFileCorrect(TGDownloader downloader)
	{
		boolean fileCorrect =  downloader.getFileSize() == FileUtils.getFileSize(downloader.getSavePath());
		LOG.d("[Method:isFileCorrect] " + fileCorrect);
		return fileCorrect;
	}
}
