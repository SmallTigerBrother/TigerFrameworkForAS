package com.mn.tiger.upload;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.task.TGTaskManager;
import com.mn.tiger.task.TGTaskParams;
import com.mn.tiger.task.TaskType;
import com.mn.tiger.task.result.TGTaskResult;
import com.mn.tiger.task.result.TGTaskResultHandler;
import com.mn.tiger.upload.observe.TGUploadObserveController;
import com.mn.tiger.upload.observe.TGUploadObserver;

/**
 * 上传管理器
 */
public class TGUploadManager
{
	protected final String LOG_TAG = this.getClass().getSimpleName();

	public static final int UPLOAD_WAITING = -2;

	public static final int UPLOAD_STARTING = -1;

	public static final int UPLOAD_UPLOADING = 0;

	public static final int UPLOAD_SUCCEED = 1;

	public static final int UPLOAD_FAILED = 2;

	public static final int UPLOAD_PAUSE = 3;

	public static final int UPLOAD_CANCEL = 4;

	private Context mContext;

	public TGUploadManager(Context context)
	{
		mContext = context;
	}

	/**
	 * 启动上传
	 * @param uploadParams
	 */
	public int start(TGUploadParams uploadParams)
	{
		return enqueue(uploadParams);
	}

	/**
	 * 取消上传
	 * @param taskId
	 */
	public void cancel(int taskId)
	{
		TGTaskManager.getInstance().cancelTask(taskId, TaskType.TASK_TYPE_UPLOAD);
	}

	/**
	 * 暂停上传
	 * @param taskId
	 */
	public void pause(int taskId)
	{
		TGTaskManager.getInstance().pauseTask(taskId, TaskType.TASK_TYPE_UPLOAD);
	}

	/**
	 * 启动所有上传任务
	 * @param type 任务类型
	 */
	public void startAll(String type)
	{
		List<TGUploader> uploaders = TGUploadDBHelper.getInstance(mContext).getUploaderByType(type);

		if (null != uploaders)
		{
			TGUploadParams uploadParams = null;
			try
			{
				for (TGUploader uploader : uploaders)
				{
					uploadParams = (TGUploadParams) Class.forName(uploader.getParamsClsName()).newInstance();
					uploadParams.setStringParams(uploader.getStringParams());
					uploadParams.setFileParams(uploader.getFileParams());
					uploadParams.setServiceURL(uploader.getServiceURL());
					if (!TextUtils.isEmpty(uploader.getTaskClsName()))
					{
						uploadParams.setTaskClsName(uploader.getTaskClsName());
					}
					start(uploadParams);
				}
			}
			catch (Exception e)
			{
				LogTools.e(LOG_TAG, e.getMessage(), e);
			}
		}
	}

	/**
	 * 取消所有任务
	 * @param type 任务类型
	 */
	public void cancelAll(String type)
	{
		List<TGUploader> uploaders = TGUploadDBHelper.getInstance(mContext).getUploaderByType(type);

		if (null != uploaders)
		{
			for (TGUploader uploader : uploaders)
			{
				cancel(Integer.valueOf(uploader.getId()));
			}
		}
	}

	/**
	 * 暂停所有任务
	 * @param type 任务类型
	 */
	public void pauseAll(String type)
	{
		List<TGUploader> uploaders = TGUploadDBHelper.getInstance(mContext).getUploaderByType(type);

		if (null != uploaders)
		{
			for (TGUploader uploader : uploaders)
			{
				pause(Integer.valueOf(uploader.getId()));
			}
		}
	}

	/**
	 * 启动所有任务
	 */
	public void startAll()
	{
		List<TGUploader> uploaders = TGUploadDBHelper.getInstance(mContext).getAllUploader();

		if (null != uploaders)
		{
			TGUploadParams uploadParams = null;

			try
			{
				for (TGUploader uploader : uploaders)
				{
					uploadParams = (TGUploadParams) Class.forName(uploader.getParamsClsName()).newInstance();
					uploadParams.setStringParams(uploader.getStringParams());
					uploadParams.setFileParams(uploader.getFileParams());
					uploadParams.setServiceURL(uploader.getServiceURL());
					if (!TextUtils.isEmpty(uploader.getTaskClsName()))
					{
						uploadParams.setTaskClsName(uploader.getTaskClsName());
					}
					start(uploadParams);
				}
			}
			catch (Exception e)
			{
				LogTools.e(LOG_TAG, e.getMessage(), e);
			}
		}
	}

	/**
	 * 取消所有任务
	 */
	public void cancelAll()
	{
		List<TGUploader> uploaders = TGUploadDBHelper.getInstance(mContext).getAllUploader();
		if (null != uploaders)
		{
			for (TGUploader uploader : uploaders)
			{
				cancel(Integer.valueOf(uploader.getId()));
			}
		}
	}

	/**
	 * 暂停所有任务
	 */
	public void pauseAll()
	{
		List<TGUploader> uploaders = TGUploadDBHelper.getInstance(mContext).getAllUploader();

		if (null != uploaders)
		{
			for (TGUploader uploader : uploaders)
			{
				pause(Integer.valueOf(uploader.getId()));
			}
		}
	}

	/**
	 * 将上传任务加入队列
	 * @param uploadParams
	 * @return
	 */
	private int enqueue(TGUploadParams uploadParams)
	{
		LogTools.p(LOG_TAG, "[Method:enqueue] start");

		final Bundle params = new Bundle();
		params.putSerializable("uploadParams", uploadParams);

		TGUploader uploader = getUploadInfo(uploadParams.getFileParams());
		TGTaskParams taskParams = null;
		if(uploader != null)
		{
			taskParams = TGTaskManager.createTaskParams(params,
					uploadParams.getTaskClsName(), getResultHandler(), Integer.parseInt(uploader.getId()));
		}
		else
		{
			taskParams = TGTaskManager.createTaskParams(params,
					uploadParams.getTaskClsName(), getResultHandler());
		}

		taskParams.setBundleParams(params);
		taskParams.setTaskType(TaskType.TASK_TYPE_UPLOAD);

		return TGTaskManager.getInstance().startTask(mContext, taskParams);
	}

	/**
	 * 注册观察者
	 * @param taskId 任务id
	 * @param observer
	 */
	public void registerDataSetObserver(int taskId, TGUploadObserver observer)
	{
		TGUploadObserveController.getInstance().registerDataSetObserver(
				String.valueOf(taskId), observer);
	}

	/**
	 * 取消注册观察着
	 * @param observer
	 */
	public void unregisterObserver(TGUploadObserver observer)
	{
		TGUploadObserveController.getInstance().unregisterObserver(observer);
	}

	/**
	 * 根据上传文件的地址获取上传信息实体类
	 * @param filePath
	 * @return
	 */
	public TGUploader getUploadInfo(HashMap<String, String> fileParams)
	{
		TGUploader uploader = null;
		uploader = TGUploadDBHelper.getInstance(mContext).getUploader(fileParams);

		return uploader;
	}

	/**
	 * 根据任务类型获取所有的上传信息
	 * @param uploadType
	 * @return
	 */
	public List<TGUploader> getUploadInfoByType(String uploadType)
	{
		return TGUploadDBHelper.getInstance(mContext).getUploaderByType(uploadType);
	}

	/**
	 * 结果接收Handler
	 */
	private TGTaskResultHandler resultHandler = new TGTaskResultHandler()
	{
		@Override
		public void handleTaskResult(TGTaskResult result)
		{
			TGUploadObserveController.getInstance().notifyChange(result);
		}
	};

	public Context getContext()
	{
		return mContext;
	}

	public void setContext(Context mContext)
	{
		this.mContext = mContext;
	}

	public TGTaskResultHandler getResultHandler()
	{
		return resultHandler;
	}

	public void setResultHandler(TGTaskResultHandler resultHandler)
	{
		this.resultHandler = resultHandler;
	}

}
