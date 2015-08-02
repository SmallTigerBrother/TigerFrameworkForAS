package com.mn.tiger.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import android.content.Context;
import android.text.TextUtils;

import com.mn.tiger.log.Logger;
import com.mn.tiger.request.client.ApacheHttpClient;
import com.mn.tiger.request.error.TGErrorMsgEnum;
import com.mn.tiger.request.error.TGHttpError;
import com.mn.tiger.request.method.ApacheHttpMethod;
import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.utility.FileUtils;

/**
 * 执行下载的HttpClient
 */
class DownloadHttpClient extends ApacheHttpClient
{
	private static final Logger LOG = Logger.getLogger(DownloadHttpClient.class);

	private static final int MAX_RETRY = 2;
	
	// 下载参数
	private TGDownloader downloader;

	/**
	 * 下载文件校验器
	 */
	private IDownloadFileChecker downloadFileChecker;
	
	/**
	 * 请求结果
	 */
	private HttpResponse response;
	
	/**
	 * 下载文件写入器
	 */
	private DownloadFileWriter downloadFileWriter;
	
	private TGDownloadTask downloadTask;
	
	private int retryCount = 0;
	
	public DownloadHttpClient(Context context, TGDownloader downloader,
			TGDownloadTask downloadTask)
	{
		super(context);
		this.downloader = downloader;
		this.downloadTask = downloadTask;
		downloadFileWriter = new DownloadFileWriter(context, downloader, downloadTask, this);
		downloadFileChecker = new FileSizeDownloadFileChecker();
	}

	@Override
	public TGHttpResult execute(ApacheHttpMethod httpMethod)
	{
		onDownloadStart(downloader);
		return super.execute(httpMethod);
	}
	
	@Override
	protected TGHttpResult handleResponse(HttpResponse response, String charset) throws IOException
	{
		this.response = response;
		TGHttpResult httpResult = initHttpResult();
		// 检测请求和接收参数
		if (null == response)
		{
			return httpResult;
		}
		
		httpResult.setResponseCode(response.getStatusLine().getStatusCode());
		
		//从返回值里面读取参数到downloader中
		readParamsFromResponse(response);

		// 检测存储路径是否为空
		if (TextUtils.isEmpty(downloader.getSavePath()))
		{
			httpResult.setResponseCode(TGErrorMsgEnum.DOWNLOAD_SAVE_PATH_IS_NULL.code);
			httpResult.setResult(TGErrorMsgEnum.getErrorMsg(getContext(),
					TGErrorMsgEnum.DOWNLOAD_SAVE_PATH_IS_NULL));
			
			downloader.setErrorCode(httpResult.getResponseCode());
			downloader.setErrorMsg(httpResult.getResult().toString());
			onDownloadFailed(downloader);
			return httpResult;
		}

		if (!isResponseCodeOK(response))
		{
			// 服务返回异常responseCode
			downloader.setErrorCode(httpResult.getResponseCode());
			downloader.setErrorMsg(httpResult.getResult().toString());
			onDownloadFailed(downloader);
			return httpResult;
		}
		
		//检测本地是否已下载，若本地已下载，直接下载完成
		if (hasAlreadyDownloaded(response))
		{
			downloader.setCompleteSize(downloader.getFileSize());
			onDownloadSuccess(downloader);
			return httpResult;
		}

		return dealDownloadResult(response, httpResult);
	}

	/**
	 * 判断返回响应码不为200或204
	 * 
	 * @param response
	 * @return
	 */
	private boolean isResponseCodeOK(HttpResponse response)
	{
		int responseCode = response.getStatusLine().getStatusCode();
		return responseCode == HttpURLConnection.HTTP_OK
				|| responseCode == HttpURLConnection.HTTP_PARTIAL;
	}

	/**
	 * 该方法的作用:处理下载结果
	 * 
	 * @date 2014年4月25日
	 * @param httpResult
	 */
	protected TGHttpResult dealDownloadResult(HttpResponse response, TGHttpResult httpResult)
	{
		InputStream inputStream = null;
		try
		{
			//1、 检测保存空间不足
			if (hasEnoughStorageSpace(downloader.getFileSize()))
			{
				LOG.e("[Method:dealDownloadResult] "
						+ TGErrorMsgEnum.getErrorMsg(getContext(),
								TGErrorMsgEnum.USABLE_SPACE_NOT_ENOUGH));

				downloader.setErrorCode(TGErrorMsgEnum.USABLE_SPACE_NOT_ENOUGH.code);
				downloader.setErrorMsg(TGErrorMsgEnum.getErrorMsg(getContext(), TGErrorMsgEnum.USABLE_SPACE_NOT_ENOUGH));
				onDownloadFailed(downloader);
				return httpResult;
			}

			//2、 若支持断点下载，执行断点下载；若不支持断点下载，使用普通下载
			if (downloader.isBreakPoints())
			{
				LOG.d("[Method:dealDownloadResult]  Server support break point");

				// 写文件流到本地
				inputStream = response.getEntity().getContent();
				downloadFileWriter.writeToRandomFile(inputStream, downloader.getSavePath(), downloader.getCompleteSize(),
						downloader.getFileSize());
			}
			else 
			{
				LOG.d("[Method:dealDownloadResult]  Server do not support break point");

				inputStream = response.getEntity().getContent();
				downloadFileWriter.writeToLocalFile(inputStream, downloader.getSavePath());
			}
		}
		catch (IOException e)
		{
			LOG.e("[Method:dealDownloadResult] " + e.getMessage(), e);
			downloader.setErrorCode(TGHttpError.IOEXCEPTION);
			downloader.setErrorMsg(TGHttpError.getDefaultErrorMsg(getContext(), TGHttpError.IOEXCEPTION));
			onDownloadFailed(downloader);
		}
		catch (NumberFormatException e)
		{
			LOG.e("[Method:dealDownloadResult] " + e.getMessage(), e);
			downloader.setErrorCode(TGErrorMsgEnum.FAILED_GET_FILE_SIZE.code);
			downloader.setErrorMsg(TGErrorMsgEnum.getErrorMsg(getContext(), TGErrorMsgEnum.FAILED_GET_FILE_SIZE));
			onDownloadFailed(downloader);
		}
		finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (IOException e)
				{
					LOG.e("[Method:dealDownloadResult] " + e.getMessage(), e);
					downloader.setErrorCode(TGHttpError.IOEXCEPTION);
					downloader.setErrorMsg(TGHttpError.getDefaultErrorMsg(getContext(), TGHttpError.IOEXCEPTION));
					onDownloadFailed(downloader);
				}
			}
		}
		return httpResult;
	}
	
	/**
	 * 从response中读取下载参数
	 * @param response
	 */
	private void readParamsFromResponse(HttpResponse response)
	{
		long serverFileSize = response.getEntity().getContentLength();
		// 第一次请求，设置文件大小
		if (downloader.getFileSize() <= 0)
		{
			downloader.setFileSize(serverFileSize);
		}

		String acceptRange = getAcceptRanges(response);
		String savePath = getSavePath(response);
		// 设置文件存储路径
		downloader.setSavePath(savePath);
		if(!downloader.isBreakPoints())
		{
			downloader.setBreakPoints(serverFileSize > 0 && acceptRange.equalsIgnoreCase("bytes"));
		}
		
		TGDownloadDBHelper.getInstance(getContext()).saveDownloader(downloader);
	}

	/**
	 * 判断是否有充足的存储空间
	 * @param serverFileSize
	 * @return
	 */
	private boolean hasEnoughStorageSpace(long serverFileSize)
	{
		return serverFileSize > 0
				&& serverFileSize > FileUtils.getFreeBytes(downloader.getSavePath());
	}

	/**
	 * 文件是否已下载完成
	 * @param response
	 * @return
	 */
	private boolean hasAlreadyDownloaded(HttpResponse response)
	{
		if(null != downloadFileChecker)
		{
			return downloadFileChecker.isFileAlreadDownloaded(downloader, response);
		}
		return false;
	}
	
	/**
	 * 下载回调方法——成功
	 * @param downloader
	 */
	void onDownloadSuccess(TGDownloader downloader)
	{
		// 下载完成，校验本地文件校验值与服务端下发的校验值是否一致
		if (!isDownloadFileCorrect())
		{
			downloader.setErrorCode(TGErrorMsgEnum.FAILED_CHECK_FILE_MD5.code);
			downloader.setErrorMsg(TGErrorMsgEnum.getErrorMsg(getContext(), TGErrorMsgEnum.FAILED_CHECK_FILE_MD5));
			onDownloadFailed(downloader);
			return;
		}

		// 如果是断点下载，删除本地记录
		downloader.setDownloadStatus(TGDownloader.DOWNLOAD_SUCCEED);
		TGDownloadDBHelper.getInstance(getContext()).deleteDownloader(downloader);

		downloadTask.onDownloadSuccess(downloader);		
	}
	
	/**
	 * 下载回调方法——开始
	 * @param downloader
	 */
	void onDownloadStart(TGDownloader downloader)
	{
		// 设置下载状态为开始
		downloader.setDownloadStatus(TGDownloader.DOWNLOAD_STARTING);
		
		downloadTask.onDownloadStart(downloader);
	}
	
	/**
	 * 下载回调方法——下载中
	 * @param downloader
	 */
	void onDownloading(TGDownloader downloader)
	{
		// 如果是断点下载，更新本地记录
		downloader.setDownloadStatus(TGDownloader.DOWNLOAD_DOWNLOADING);
		TGDownloadDBHelper.getInstance(getContext()).updateDownloader(downloader);

		// 服务端没返回文件长度时，不返回进度
		if (downloader.getFileSize() <= 0)
		{
			return;
		}

		downloadTask.onDownloading(downloader);
	}
	
	/**
	 * 下载回调方法——暂停
	 * @param downloader
	 */
	void onDownloadPause(TGDownloader downloader)
	{
		downloader.setDownloadStatus(TGDownloader.DOWNLOAD_PAUSE);
		// 如果不是断点下载，删除本地文件和数据库记录; 断点下载，更新本地数据库下载状态
    	if(!downloader.isBreakPoints())
    	{
    		FileUtils.deleteFile(downloader.getSavePath());
    		TGDownloadDBHelper.getInstance(getContext()).deleteDownloader(downloader);
    	}
    	else
    	{
    		TGDownloadDBHelper.getInstance(getContext()).updateDownloader(downloader);
    	}
		
    	downloadTask.onDownloadPause(downloader);
	}
	
	/**
	 * 下载回调方法——失败
	 * @param downloader
	 */
	void onDownloadFailed(TGDownloader downloader)
	{
		// 如果是下载前校验文件流出错，删除出错文件与数据，重新开始下载
		if (downloader.getErrorCode() == TGErrorMsgEnum.FAILED_CHECK_FILE_MD5.code && 
				retryCount <= MAX_RETRY)
		{
			retryCount++;
			retry(downloader);
			return;
		}

		downloader.setDownloadStatus(TGDownloader.DOWNLOAD_FAILED);
		// 如果不是断点下载，删除本地文件和数据库记录; 断点下载，更新本地数据库下载状态
		if (!downloader.isBreakPoints())
		{
			FileUtils.deleteFile(downloader.getSavePath());
			TGDownloadDBHelper.getInstance(getContext()).deleteDownloader(downloader);
		}
		else
		{
			TGDownloadDBHelper.getInstance(getContext()).updateDownloader(downloader);
		}

		downloadTask.onDownloadFailed(downloader);
	}

	/**
	 * 重新尝试下载
	 * @param downloader
	 */
	private void retry(TGDownloader downloader)
	{
		LOG.d("[Method:retry] retryCount == " + retryCount);
		// 删除本地文件
		FileUtils.deleteFile(downloader.getSavePath());
		// 如果是断点下载，删除数据库记录
		if (downloader.isBreakPoints())
		{
			TGDownloadDBHelper.getInstance(getContext()).deleteDownloader(downloader);
		}

		// 重新下载
		downloader.setCompleteSize(0);
		downloader.setCheckKey("");
		downloader.setErrorCode(0);
		downloader.setErrorMsg("");
		
		ApacheHttpMethod httpMethod = getHttpMethod();
		httpMethod.setProperty("Range", "");
		execute(httpMethod);
	}
	
	
	/**
	 * 下载回调方法——取消
	 * @param downloader
	 */
	void onDownloadCancel(TGDownloader downloader)
	{
		FileUtils.deleteFile(downloader.getSavePath());
		TGDownloadDBHelper.getInstance(getContext()).deleteDownloader(downloader);
		downloadTask.onDownloadCancel(downloader);
	}

	/**
	 * 该方法的作用:获取文件长度
	 * 
	 * @date 2014年1月14日
	 * @throws Exception
	 * @return 文件长度
	 */
	protected long getFileSize(HttpResponse response) throws NumberFormatException
	{
		// 获取文件大小
		Header header = response.getFirstHeader("Content-Length");
		if (null != header)
		{
			String fileSize = header.getValue();
			if (!TextUtils.isEmpty(fileSize))
			{
				return Long.valueOf(fileSize);
			}
			else
			{
				return 0;
			}
		}

		return 0;
	}

	private String getAcceptRanges(HttpResponse response)
	{
		// 获取文件大小
		Header header = response.getFirstHeader("Accept-Ranges");
		if (null != header)
		{
			String acceptRanges = header.getValue();
			if (!TextUtils.isEmpty(acceptRanges))
			{
				return acceptRanges;
			}
			else
			{
				return "";
			}
		}

		return "";

	}

	/**
	 * 下载的文件是否正确
	 * @return
	 */
	private boolean isDownloadFileCorrect()
	{
		if(null != downloadFileChecker)
		{
			return downloadFileChecker.isFileCorrect(downloader, response);
		}
		
		return true;
	}

	/**
	 * 
	 * 该方法的作用: 获取文件保存路径
	 * 
	 * @date 2014年8月23日
	 * @return
	 */
	private String getSavePath(HttpResponse response)
	{
		String savePath = downloader.getSavePath();
		if (savePath.endsWith("/"))
		{
			String fileName = getFileName(response);
			savePath = savePath + fileName;
		}

		return savePath;
	}

	/**
	 * 该方法的作用: 获取服务器返回文件名
	 * 
	 * @date 2014年8月23日
	 * @return
	 */
	protected String getFileName(HttpResponse response)
	{
		String serverFileName = "";
		// 服务端返回文件名称Content-Disposition
		Header header = response.getFirstHeader("Content-Disposition");
		if (null != header)
		{
			String contentDisposition = header.getValue();
			serverFileName = contentDisposition.replace("attachment;filename=", "");

			LOG.d("[Method:getFileName]  serverFileName:" + serverFileName);
			if (null == serverFileName)
			{
				serverFileName = "";
			}
		}
		return serverFileName;
	}

	/**
	 * 设置下载文件校验器
	 * @param downloadFileChecker
	 */
	public void setDownloadFileChecker(IDownloadFileChecker downloadFileChecker)
	{
		this.downloadFileChecker = downloadFileChecker;
	}
}
