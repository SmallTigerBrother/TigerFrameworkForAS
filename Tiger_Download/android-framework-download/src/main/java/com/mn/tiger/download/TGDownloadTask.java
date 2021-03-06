package com.mn.tiger.download;

import android.os.Bundle;

import com.mn.tiger.download.db.TGDownloader;
import com.mn.tiger.log.Logger;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.task.TaskType;
import com.mn.tiger.utility.NetworkUtils;

/**
 * 
 * 该类作用及功能说明: 下载任务类
 * 
 * @date 2014年8月18日
 */
public class TGDownloadTask extends TGTask
{
	private static final Logger LOG = Logger.getLogger(TGDownloadTask.class);

	/**
	 * 下载信息
	 */
	private TGDownloadParams mDownloadParams;
	
	/**
	 * 下载进度
	 */
	private int progress = -1;

	/**
	 * 下载信息
	 */
	protected TGDownloader downloader;

	private TGDownloadHttpClient downloadHttpClient;
	
	/**
	 * 构造函数
	 * @date 2014年6月28日
	 */
	public TGDownloadTask()
	{
		super();
		this.setType(TaskType.TASK_TYPE_DOWNLOAD);
	}
	
	/**
	 * 该方法的作用:
	 * 执行自身（任务真正的执行方法）
	 * @date 2014年3月17日
	 * @return
	 */
	@Override
	protected TGTaskState executeOnSubThread()
	{
		// 后台执行下载任务
		downloadInBackground();
		// 任务完成，结束任务
		return TGTaskState.FINISHED;
	}
	
	@Override
	protected void onTaskPause()
	{
		if(getTaskState() == TGTaskState.WAITING)
		{
			TGDownloader downloader = TGDownloader.fromTGDownloadParams(mDownloadParams, this.getTaskID());
			onDownloadPause(downloader);
		}
		super.onTaskPause();
	}
	
	@Override
	protected void onTaskCancel()
	{
		if(getTaskState() == TGTaskState.WAITING)
		{
			TGDownloader downloader = TGDownloader.fromTGDownloadParams(mDownloadParams, this.getTaskID());
			onDownloadCancel(downloader);
			downloadHttpClient.cancel();
		}
		super.onTaskCancel();
	}
	
	/**
	 * 
	 * 该方法的作用: 后台执行下载任务
	 * @date 2014年6月28日
	 * @return
	 */
	protected void downloadInBackground()
	{
		LOG.d("[Metohd:downloadInBackground]" + "; taskid: " + this.getTaskID());
		mDownloadParams = getDownloadParams();
		
		executeDownload();
	}
	
	/**
	 * 
	 * 该方法的作用: 获取下载任务参数
	 * @date 2014年6月19日
	 * @return
	 */
	protected TGDownloadParams getDownloadParams()
	{
		if (null == getParams())
		{
			return null;
		}
		
		Bundle params = (Bundle) getParams();
		TGDownloadParams downloadParams = (TGDownloadParams) params.getSerializable("downloadParams");

		return downloadParams;
	}
	
	/**
	 * 
	 * 该方法的作用: 执行下载任务
	 * @date 2014年7月23日
	 */
	protected void executeDownload()
	{
		LOG.d("[Method:executeDownload]");
		if(NetworkUtils.isConnectivityAvailable(getContext()))
		{
			// 获取下载参数
			downloader = TGDownloader.fromTGDownloadParams(mDownloadParams, getTaskID());
			downloadHttpClient = new OKHttpDownloadClient(getContext(), downloader, this);
			// 执行下载操作
			downloadHttpClient.execute();
		}
		else
		{
			TGDownloader downloader = TGDownloader.fromTGDownloadParams(mDownloadParams, getTaskID());
			downloader.setErrorCode(TGHttpError.NO_NETWORK);
			downloader.setErrorMsg(TGHttpError.getDefaultErrorMsg(getContext(), TGHttpError.NO_NETWORK));
			onDownloadFailed(downloader);
		}
	}
	
	@Override
	protected void onDestroy()
	{
		progress = -1;
		super.onDestroy();
	}
	
	/**
	 * 下载回调方法——开始
	 * @param downloader
	 */
	void onDownloadStart(TGDownloader downloader) 
	{
		LOG.d("[Method:downloadStart]" + "; taskid: " + TGDownloadTask.this.getTaskID());
		sendTaskResult(downloader);
	}
	
	/**
	 * 下载回调方法——成功
	 * @param downloader
	 */
	void onDownloadSuccess(TGDownloader downloader)
	{
		LOG.d("[Method:downloadSucceed]" + "; taskid: " + TGDownloadTask.this.getTaskID());
		sendTaskResult(downloader);
		onTaskFinished();
	}
	
	/**
	 * 下载回调方法——下载中
	 * @param downloader
	 */
	void onDownloading(TGDownloader downloader)
	{
		// 每1%, 向外推一次进度
		int currentProgress = (int) (downloader.getCompleteSize() * 100 / downloader.getFileSize());
		if (currentProgress != progress)
		{
			LOG.d("[Method:downloadProgress]" + "; taskid: " + TGDownloadTask.this.getTaskID() +
					" ; progress : " + currentProgress);
			
			progress = currentProgress;
			sendTaskResult(downloader);
		}
	}
	
	/**
	 * 下载回调方法——失败
	 * @param downloader
	 */
	void onDownloadFailed(TGDownloader downloader)
	{
		LOG.d("[Method:downloadFailed]" + "; taskid: " + TGDownloadTask.this.getTaskID());
		sendTaskResult(downloader);
		onTaskError((downloader).getErrorCode(), (downloader).getErrorMsg());
	}
	
	/**
	 * 下载回调方法——暂停
	 * @param downloader
	 */
	void onDownloadPause(TGDownloader downloader)
	{
		LOG.d("[Method:downloadPause]" + "; taskid: " + TGDownloadTask.this.getTaskID());
		sendTaskResult(downloader);
	}
	
	/**
	 * 下载回调方法——取消
	 * @param downloader
	 */
	void onDownloadCancel(TGDownloader downloader)
	{
		LOG.d("[Method:downloadCanceled]" + "; taskid: " + TGDownloadTask.this.getTaskID());
		sendTaskResult(downloader);
	}
}
