package com.mn.tiger.upload;

import android.content.Context;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.utility.FileUtils;

import java.net.HttpURLConnection;

/**
 * 
 * 该类作用及功能说明: 上传具体策略
 * 
 * @date 2014年7月30日
 */
public class TGUploadStrategy implements IUploadStrategy
{
	/**
	 * 日志标识
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();

	/**
	 * 上下文信息
	 */
	protected Context context;

	/**
	 * 上传监听
	 */
	protected IUploadListener uploadListener;

	/**
	 * 上传任务
	 */
	protected TGUploadTask uploadTask = null;
	
	/**
	 * 上传信息
	 */
	protected TGUploader mpUploader;
	

	/**
	 * 构造函数
	 * @date 2014年8月5日
	 * @param context
	 * @param uploadTask
	 * @param listener
	 */
	public TGUploadStrategy(Context context, TGUploadTask uploadTask,
			IUploadListener listener)
	{
		this.context = context;
		this.uploadTask = uploadTask;
		this.uploadListener = listener;
	}

	@Override
	public void upload(TGUploadParams uploadParams)
	{
		LogTools.p(LOG_TAG, "[Method:upload]");

		// 获取上传参数
		mpUploader = getUploader(uploadParams);
		// 通知上传开始
		uploadListener.uploadStart(mpUploader);
		// 执行上传
		executeUpload(context, mpUploader);
	}
	
	/**
	 * 
	 * 该方法的作用: 获取上传信息
	 * @date 2014年8月18日
	 * @param mpUploadParams
	 * @return
	 */
	protected TGUploader getUploader(TGUploadParams mpUploadParams)
	{
		TGUploader uploader = null;
		uploader = TGUploadDBHelper.getInstance(context).getBreakPointUploader(mpUploadParams.getFilePath());
		
		if(uploader == null)
		{
			uploader = createNewUploader(mpUploadParams);
		}
		uploader.setUploadStatus(TGUploadManager.UPLOAD_STARTING);
		
		return uploader;
	}
	
	protected TGUploader createNewUploader(TGUploadParams uploadParams)
	{
		TGUploader uploader = new TGUploader();
		uploader.setId(uploadTask.getTaskID().toString());
		uploader.setServiceURL(uploadParams.getServiceURL());
		uploader.setType(uploadParams.getUploadType());
		uploader.setFilePath(uploadParams.getFilePath());
		uploader.setTaskClsName(uploadParams.getTaskClsName());
		uploader.setFileSize(FileUtils.getFileSize(uploadParams.getFilePath()));
		uploader.setStartPosition(0);
		uploader.setEndPosition(FileUtils.getFileSize(uploadParams.getFilePath()));
		uploader.setParamsClsName(uploadParams.getClass().getName());
		
		return uploader;
	}
	
	/**
	 * 
	 * 该方法的作用: 请求上传
	 * @date 2014年8月5日
	 * @param context
	 * @param uploader
	 */
	protected void executeUpload(Context context, TGUploader uploader)
	{
	}

	/**
	 * 该方法的作用:处理请求结果
	 * 
	 * @date 2014年4月3日
	 * @param uploader
	 * @param httpResult
	 * @return
	 */
	protected void dealRequestResult(TGUploader uploader, TGHttpResult httpResult)
	{
		if (httpResult != null && httpResult.getResponseCode() != HttpURLConnection.HTTP_OK)
		{
			uploader.setErrorCode(httpResult.getResponseCode());
			uploader.setErrorMsg(httpResult.getResult());
			uploadFailed(uploader);
		}
	}

	/**
	 * 
	 * 该方法的作用: 获取上下文信息
	 * @date 2014年7月31日
	 * @return
	 */
	protected Context getContext()
	{
		return context;
	}
	
	/**
	 * 
	 * 该方法的作用: 上传过程中
	 * @date 2014年8月19日
	 * @param uploader
	 */
	private void uploadUploading(TGUploader uploader, int progress)
	{
		// 修改上传状态为正在上传
		uploader.setUploadStatus(TGUploadManager.UPLOAD_UPLOADING);
		
		uploadListener.uploadProgress(uploader, progress);
	}
	
	/**
	 * 
	 * 该方法的作用: 上传文件完成，删除数据库记录
	 * @date 2014年8月19日
	 * @param uploader
	 */
	private void uploadFinish(TGUploader uploader)
	{
		// 上传完每一块, 记录进度到数据库
		uploader.setCompleteSize(uploader.getCompleteSize() + uploader.getEndPosition() - uploader.getStartPosition());
		TGUploadDBHelper.getInstance(context).updateUploader(uploader);
		// 检测上传大小是否大于等于文件大小，如果小于文件大小，说明是分块上传，调用上传中方法
		if(uploader.getCompleteSize() < uploader.getFileSize())
		{
			int currentProgress = (int) (uploader.getCompleteSize() * 100 / uploader.getFileSize());
			uploadUploading(uploader, currentProgress);
			return;
		}

		// 删除本地记录
		uploader.setUploadStatus(TGUploadManager.UPLOAD_SUCCEED);
		TGUploadDBHelper.getInstance(context).deleteUploader(uploader);
		
		if (uploadListener != null)
		{
			uploadListener.uploadSucceed(uploader);
		}
		else
		{
			LogTools.e(LOG_TAG,
					"[Method:failedUpload]  uploadListener is null,Please set uploadListener on Construct..");
		}
	}
	
	/**
	 * 
	 * 该方法的作用: 上传文件过程中出现异常，如果不是断点上传，删除本地文件
	 * @date 2014年8月19日
	 * @param uploader
	 */
	protected void uploadFailed(TGUploader uploader)
	{
		uploader.setUploadStatus(TGUploadManager.UPLOAD_FAILED);
		// 如果不是分块上传，删除数据库记录
    	if(true)
    	{
    		TGUploadDBHelper.getInstance(context).deleteUploader(uploader);
    	}
		
		if (uploadListener != null)
		{
			uploadListener.uploadFailed(uploader);
		}
		else
		{
			LogTools.e(LOG_TAG,
					"[Method:failedUpload]  uploadListener is null,Please set uploadListener on Construct..");
		}
	}
	
	/**
	 * 
	 * 该方法的作用: 停止上传，如果不是断点上传，删除本地文件
	 * @date 2014年8月19日
	 * @param uploader
	 */
	private void uploadStop(TGUploader uploader)
	{
		uploader.setUploadStatus(TGUploadManager.UPLOAD_PAUSE);
		// 如果不是分块上传，删除数据库记录
    	if(true)
    	{
    		TGUploadDBHelper.getInstance(context).deleteUploader(uploader);
    	}
		
		if (uploadListener != null)
		{
			uploadListener.uploadStop(uploader);
		}
		else
		{
			LogTools.e(LOG_TAG,
					"[Method:failedUpload]  uploadListener is null,Please set uploadListener on Construct..");
		}
	}
	
	/**
	 * 
	 * 该方法的作用: 取消上传，直接删除本地文件和数据库记录
	 * @date 2014年8月19日
	 * @param uploader
	 */
	private void uploadCancel(TGUploader uploader)
	{
		uploader.setUploadStatus(TGUploadManager.UPLOAD_PAUSE);
		// 删除数据库记录
		TGUploadDBHelper.getInstance(context).deleteUploader(uploader);
		
		if (uploadListener != null)
		{
			uploadListener.uploadCanceled(uploader);
		}
		else
		{
			LogTools.e(LOG_TAG,
					"[Method:failedUpload]  uploadListener is null,Please set uploadListener on Construct..");
		}
	}
}
