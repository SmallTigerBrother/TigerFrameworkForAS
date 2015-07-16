package com.mn.tiger.download;

import android.os.Bundle;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.request.error.TGHttpError;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.utility.NetworkUtils;

/**
 * 
 * 该类作用及功能说明: 下载任务类
 * 
 * @date 2014年8月18日
 */
public class TGDownloadTask extends TGTask
{
	/**
	 * 下载策略
	 */
	private IDownloadStrategy downloadStrategy = null;
	
	/**
	 * 下载信息
	 */
	private TGDownloadParams mDownloadParams;
	
	/**
	 * 下载进度
	 */
	private int progress = -1; 
	
	/**
	 * 构造函数
	 * @date 2014年6月28日
	 */
	public TGDownloadTask()
	{
		super();
		this.setType(TASK_TYPE_DOWNLOAD);
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
			TGDownloader downloader = TGDownloader.getInstance(getContext(), mDownloadParams, this.getTaskID());
			onDownloadPause(downloader);
		}
		super.onTaskPause();
	}
	
	@Override
	protected void onTaskCancel()
	{
		if(getTaskState() == TGTaskState.WAITING)
		{
			TGDownloader downloader = TGDownloader.getInstance(getContext(), mDownloadParams, this.getTaskID());
			onDownloadCancel(downloader);
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
		LogTools.p(LOG_TAG, "[Metohd:downloadInBackground]" + "; taskid: " + this.getTaskID());
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
		if(NetworkUtils.isConnectivityAvailable(getContext()))
		{
			downloadStrategy = new TGDownloadStrategy(getContext(), this);
			downloadStrategy.download(mDownloadParams);
		}
		else
		{
			TGDownloader downloader = TGDownloader.getInstance(getContext(), mDownloadParams, getTaskID());
			downloader.setErrorCode(TGHttpError.NO_NETWORK);
			downloader.setErrorMsg(TGHttpError.getDefaultErrorMsg(getContext(), TGHttpError.NO_NETWORK));
			onDownloadFailed(downloader);
		}
	}
	
	@Override
	protected void onDestory()
	{
		progress = -1;
		super.onDestory();
	}
	
	/**
	 * 下载回调方法——开始
	 * @param downloader
	 */
	void onDownloadStart(TGDownloader downloader) 
	{
		LogTools.p(LOG_TAG, "[Metohd:downloadStart]" + "; taskid: " + TGDownloadTask.this.getTaskID());
		sendTaskResult(downloader);
	}
	
	/**
	 * 下载回调方法——成功
	 * @param downloader
	 */
	void onDownloadSuccess(TGDownloader downloader)
	{
		LogTools.p(LOG_TAG, "[Metohd:downloadSucceed]" + "; taskid: " + TGDownloadTask.this.getTaskID());
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
			LogTools.d(LOG_TAG, "[Metohd:downloadProgress]" + "; taskid: " + TGDownloadTask.this.getTaskID() + 
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
		LogTools.p(LOG_TAG, "[Metohd:downloadFailed]" + "; taskid: " + TGDownloadTask.this.getTaskID());
		sendTaskResult(downloader);
		onTaskError(((TGDownloader)downloader).getErrorCode(), ((TGDownloader)downloader).getErrorMsg());
	}
	
	/**
	 * 下载回调方法——暂停
	 * @param downloader
	 */
	void onDownloadPause(TGDownloader downloader)
	{
		LogTools.p(LOG_TAG, "[Metohd:downloadPause]" + "; taskid: " + TGDownloadTask.this.getTaskID());
		sendTaskResult(downloader);
	}
	
	/**
	 * 下载回调方法——取消
	 * @param downloader
	 */
	void onDownloadCancel(TGDownloader downloader)
	{
		LogTools.p(LOG_TAG, "[Metohd:downloadCanceled]" + "; taskid: " + TGDownloadTask.this.getTaskID());
		sendTaskResult(downloader);
	}
	
	public void setDownloadStrategy(IDownloadStrategy downloadStrategy)
	{
		this.downloadStrategy = downloadStrategy;
	}
}
