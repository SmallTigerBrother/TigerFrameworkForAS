package com.mn.tiger.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import android.content.Context;

import com.mn.tiger.log.Logger;
import com.mn.tiger.request.error.TGHttpError;
import com.mn.tiger.task.TGTask.TGTaskState;
import com.mn.tiger.utility.Commons;
import com.mn.tiger.utility.FileUtils;

/**
 * 下载文件写入类
 */
class DownloadFileWriter
{
	private static final Logger LOG = Logger.getLogger(DownloadHttpClient.class);

	private Context context;

	// 下载任务
	private TGDownloadTask downloadTask;

	// 下载参数
	private TGDownloader downloader;

	/**
	 * 已完成的下载大小
	 */
	private long completeSize;

	private DownloadHttpClient downloadHttpClient;

	public DownloadFileWriter(Context context, TGDownloader downloader,
							  TGDownloadTask downloadTask, DownloadHttpClient httpClient)
	{
		this.downloader = downloader;
		this.downloadTask = downloadTask;
		this.completeSize = downloader.getCompleteSize();
		this.downloadHttpClient = httpClient;
	}

	/**
	 * 该方法的作用:写入文件
	 *
	 * @date 2014年1月14日
	 * @param instream
	 * @param savePath
	 * @throws IOException
	 * @throws NullPointerException
	 */
	@SuppressWarnings("resource")
	protected void writeToLocalFile(InputStream instream, String savePath) throws IOException
	{
		LOG.d("[Method:writeToLocalFile]");
		if (instream == null)
		{
			LOG.e("[Method:writeToLocalFile] instream is empty.");
			throw new IOException("Failed to receive stream, instream is null.");
		}

		completeSize = 0;

		File file = new File(savePath);
		if (!file.exists())
		{ // 文件不存在时，才创建并chmod文件
			file = FileUtils.createFile(file.getAbsolutePath());
		}
		if (null == file)
		{
			throw new IOException("Failed to create file....");
		}

		OutputStream outStream = null;
		try
		{
			outStream = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];
			int length = -1;
			while ((length = (instream.read(buffer))) != -1)
			{
				// 当任务被取消、下载出错或者任务被停止时, 停止写文件
				if (downloadTask == null || downloadTask.getTaskState() == TGTaskState.ERROR
						|| downloadTask.getTaskState() == TGTaskState.PAUSE)
				{
					downloader.setDownloadStatus(TGDownloader.DOWNLOAD_PAUSE);
					downloadHttpClient.onDownloadPause(downloader);
					return;
				}
				else if(downloadTask.getTaskState() == TGTaskState.CANCEL)
				{
					downloadHttpClient.onDownloadCancel(downloader);
					return;
				}
				else
				{
					outStream.write(buffer, 0, length);
					completeSize += length;
					downloader.setCompleteSize(completeSize);
					downloadHttpClient.onDownloading(downloader);
				}
			}

			downloadHttpClient.onDownloadSuccess(downloader);
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			Commons.closeOutputStream(outStream);
		}
	}

	/**
	 * 该方法的作用:将请求返回的流写入本地文件
	 *
	 * @date 2014年1月7日
	 * @param inStream
	 * @param savePath
	 * @param startPos
	 * @param endPos
	 * @throws IOException
	 */
	protected void writeToRandomFile(InputStream inStream, String savePath, long startPos,
									 long endPos) throws IOException
	{
		if (inStream == null)
		{
			LOG.e("[Method:writeToRandomFile] instream is empty.");
			throw new IOException("Failed to receive stream, instream is null.");
		}

		completeSize = startPos;

		RandomAccessFile randomAccessFile = null;
		try
		{
			File file = new File(savePath);
			if(completeSize > 0)
			{
				//若上次下载的本地文件不存在
				if (!file.exists())
				{
					LOG.e("[Method:writeToRandomFile] the last part file can not be found");
					downloadHttpClient.onDownloadFailed(downloader);
					return;
				}
			}
			else
			{
				FileUtils.createFile(savePath);
				FileUtils.chmodFile(file.getAbsolutePath(), "666");
			}

			randomAccessFile = new RandomAccessFile(file, "rwd");
			randomAccessFile.seek(startPos);

			LOG.d("[Method:writeToRandomFile]  seek file position:" + startPos
					+ " start to write...");

			byte[] buffer = new byte[4096];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1)
			{
				// 当任务被取消、下载出错或者任务被停止时, 停止写文件
				if (downloadTask == null || downloadTask.getTaskState() == TGTaskState.ERROR
						|| downloadTask.getTaskState() == TGTaskState.PAUSE)
				{
					downloadHttpClient.onDownloadPause(downloader);
					return;
				}
				else if(downloadTask.getTaskState() == TGTaskState.CANCEL)
				{
					downloadHttpClient.onDownloadCancel(downloader);
					return;
				}
				else
				{
					randomAccessFile.write(buffer, 0, length);
					completeSize += length;
					downloader.setCompleteSize(completeSize);
					downloadHttpClient.onDownloading(downloader);
				}
			}

			downloadHttpClient.onDownloadSuccess(downloader);
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			if (randomAccessFile != null)
			{
				try
				{
					randomAccessFile.close();
				}
				catch (IOException e)
				{
					LOG.e("[Method:writeToRandomFile]" + e.getMessage(), e);
					downloader.setErrorCode(TGHttpError.IOEXCEPTION);
					downloader.setErrorMsg(TGHttpError.getDefaultErrorMsg(context, TGHttpError.IOEXCEPTION));
					downloadHttpClient.onDownloadFailed(downloader);
				}
			}
		}
	}

}
