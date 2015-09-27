package com.mn.tiger.upload;

import android.os.Bundle;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.upload.observe.TGUploadObserveController;

/**
 * 
 * 该类作用及功能说明: 上传任务类
 * 
 * @date 2014年6月28日
 */
public class TGUploadTask extends TGTask
{
	/**
	 * 日志标签
	 */
	protected static final String LOG_TAG = TGUploadTask.class.getSimpleName();
	
	/**
	 * 上传策略
	 */
	protected IUploadStrategy uploadStrategy = null;
	
	/**
	 * 上传信息 
	 */
	protected TGUploadParams mpUploadParams;
	
	/**
	 * 上传任务监听
	 */
	private IUploadListener uploadListener = new DefaultUploadListener();
	
	/**
	 * 构造函数
	 * @date 2014年6月28日
	 */
	public TGUploadTask()
	{
		super();
		this.setType(TASK_TYPE_UPLOAD);
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
		// 后台执行上传任务
		uploadInBackground();
		// 任务完成，结束任务
		return TGTaskState.FINISHED;
	}
	
	/**
	 * 
	 * 该方法的作用: 后台执行上传任务
	 * @date 2014年6月28日
	 * @return
	 */
	protected void uploadInBackground()
	{
		LogTools.p(LOG_TAG, "[Metohd:uploadInBackground]" + "; taskid: " + this.getTaskID());
		mpUploadParams = getUploadParams();
		
		executeUpload();
	}
	
	/**
	 * 
	 * 该方法的作用: 获取上传任务参数
	 * @date 2014年6月19日
	 * @return
	 */
	protected TGUploadParams getUploadParams()
	{
		if (null == getParams())
		{
			return null;
		}
		
		Bundle params = (Bundle) getParams();
		TGUploadParams uploadParams = (TGUploadParams) params.getSerializable("uploadParams");

		return uploadParams;
	}
	
	/**
	 * 
	 * 该方法的作用: 执行上传任务
	 * @date 2014年7月23日
	 */
	protected void executeUpload()
	{
		// 上传
		uploadStrategy = new TGUploadStrategy(getContext(), this, uploadListener);
		uploadStrategy.upload(mpUploadParams);
	}
	
	@Override
	protected void onTaskCancel() 
	{
		TGUploadObserveController.getInstance().unregisterObserverByKey(String.valueOf(this.getTaskID()));
		super.onTaskCancel();
	}
	
	/**
	 * 上传任务，暂停时的克隆方法，设置新的执行时间，放到上传任务的最后执行
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		TGTask task = (TGTask)super.clone();
		
		return task;
	}
	
	/**
	 * 
	 * 该类作用及功能说明: 默认上传监听
	 * 
	 * @date 2014年8月25日
	 */
	public class DefaultUploadListener implements IUploadListener
	{
		private final String LOG_TAG = this.getClass().getSimpleName();
		
		@Override
		public void uploadStart(TGUploader uploader)
		{
			LogTools.p(LOG_TAG, "[Metohd:uploadStart]");
			sendUploadResult(uploader);
		}
		
		@Override
		public void uploadSucceed(TGUploader uploader)
		{
			LogTools.p(LOG_TAG, "[Metohd:uploadSucceed]" + "; taskid: " + TGUploadTask.this.getTaskID());
			sendUploadResult(uploader);
			onTaskFinished();
		}
		
		@Override
		public void uploadProgress(TGUploader uploader, int progress)
		{
			LogTools.d(LOG_TAG, "[Metohd:uploadProgress]" + "; taskid: " + TGUploadTask.this.getTaskID());
			sendUploadResult(uploader);
			onTaskChanged(progress);
		}
		
		@Override
		public void uploadFailed(TGUploader uploader)
		{
			LogTools.p(LOG_TAG, "[Metohd:uploadFailed]" + "; taskid: " + TGUploadTask.this.getTaskID());
			sendUploadResult(uploader);
			
			onTaskError(uploader.getErrorCode(), uploader.getErrorMsg());
		}
		
		@Override
		public void uploadCanceled(TGUploader uploader)
		{
			LogTools.p(LOG_TAG, "[Metohd:uploadCanceled]" + "; taskid: " + TGUploadTask.this.getTaskID());
			sendUploadResult(uploader);
		}
		
		@Override
		public void uploadStop(TGUploader uploader)
		{
			LogTools.p(LOG_TAG, "[Metohd:uploadStop]" + "; taskid: " + TGUploadTask.this.getTaskID());
			sendUploadResult(uploader);
		}
		
		private void sendUploadResult(TGUploader uploader)
		{
			sendTaskResult(uploader);
		}
	}

	public IUploadStrategy getUploadStrategy()
	{
		return uploadStrategy;
	}

	public void setUploadStrategy(IUploadStrategy uploadStrategy)
	{
		this.uploadStrategy = uploadStrategy;
	}

	public TGUploadParams getMpUploadParams()
	{
		return mpUploadParams;
	}

	public void setMpUploadParams(TGUploadParams mpUploadParams)
	{
		this.mpUploadParams = mpUploadParams;
	}

	public IUploadListener getUploadListener()
	{
		return uploadListener;
	}

	public void setUploadListener(IUploadListener uploadListener)
	{
		this.uploadListener = uploadListener;
	}
}
