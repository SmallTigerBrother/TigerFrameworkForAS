package com.mn.tiger.upload;

import android.os.Bundle;

import com.mn.tiger.log.Logger;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.task.TaskType;
import com.mn.tiger.upload.observe.TGUploadObserveController;

/**
 *
 * 该类作用及功能说明: 上传任务类
 *
 * @date 2014年6月28日
 */
public class TGUploadTask extends TGTask
{
	private static final Logger LOG = Logger.getLogger(TGUploadTask.class);
	/**
	 * 上传信息
	 */
	protected TGUploader uploader;

	/**
	 * 上传信息 
	 */
	protected TGUploadParams uploadParams;

	/**
	 * 构造函数
	 * @date 2014年6月28日
	 */
	public TGUploadTask()
	{
		super();
		this.setType(TaskType.TASK_TYPE_UPLOAD);
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
		LOG.d("[Method:uploadInBackground]" + "; taskid: " + this.getTaskID());
		uploadParams = getUploadParams();

		executeUpload();
	}

	/**
	 *
	 * 该方法的作用: 执行上传任务
	 * @date 2014年7月23日
	 */
	protected void executeUpload()
	{
		//TODO 上传
		// 获取上传参数
		uploader = getUploader(uploadParams);
	}

	/**
	 *
	 * 该方法的作用: 获取上传信息
	 * @date 2014年8月18日
	 * @param uploadParams
	 * @return
	 */
	protected TGUploader getUploader(TGUploadParams uploadParams)
	{
		TGUploader uploader = TGUploadDBHelper.getInstance(getContext()).getUploader(uploadParams.getFileParams());

		if(uploader == null)
		{
			uploader = createNewUploader(uploadParams);
		}
		uploader.setUploadStatus(TGUploadManager.UPLOAD_WAITING);

		return uploader;
	}

	protected TGUploader createNewUploader(TGUploadParams uploadParams)
	{
		TGUploader uploader = new TGUploader();
		uploader.setId(getTaskID().toString());
		uploader.setServiceURL(uploadParams.getServiceURL());
		uploader.setType(uploadParams.getUploadType());
		uploader.setFileParams(uploadParams.getFileParams());
		uploader.setTaskClsName(uploadParams.getTaskClsName());
		uploader.setFileSize(uploadParams.getContentLength());
		uploader.setParamsClsName(uploadParams.getClass().getName());

		return uploader;
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

	void onUploadStart(TGUploader uploader)
	{
		LOG.d("[Method:onUploadStart]");
		sendTaskResult(uploader);
	}

	/**
	 *
	 * 该方法的作用: 上传过程中
	 * @date 2014年8月19日
	 * @param uploader
	 */
	void onUploading(TGUploader uploader, int progress)
	{
		LOG.d("[Method:onUploading] progress == " + progress);

		sendTaskResult(uploader);
		onTaskChanged(progress);
	}

	/**
	 *
	 * 该方法的作用: 上传文件完成，删除数据库记录
	 * @date 2014年8月19日
	 * @param uploader
	 */
	void onUploadFinish(TGUploader uploader)
	{
		LOG.d("[Method:onUploadFinish]");
		sendTaskResult(uploader);
		onTaskFinished();
	}

	/**
	 * 该方法的作用: 上传文件过程中出现异常，如果不是断点上传，删除本地文件
	 * @date 2014年8月19日
	 * @param uploader
	 */
	void onUploadFailed(TGUploader uploader)
	{
		LOG.d("[Method:onUploadFailed]");
		sendTaskResult(uploader);
		onTaskFinished();
	}

	/**
	 *
	 * 该方法的作用: 停止上传，如果不是断点上传，删除本地文件
	 * @date 2014年8月19日
	 * @param uploader
	 */
	void onUploadStop(TGUploader uploader)
	{
		LOG.d("[Method:onUploadStop]");
		sendTaskResult(uploader);
		onTaskFinished();
	}

	/**
	 *
	 * 该方法的作用: 取消上传，直接删除本地文件和数据库记录
	 * @date 2014年8月19日
	 * @param uploader
	 */
	void onUploadCancel(TGUploader uploader)
	{
		LOG.d("[Method:onUploadCancel]");
		sendTaskResult(uploader);
	}
}
