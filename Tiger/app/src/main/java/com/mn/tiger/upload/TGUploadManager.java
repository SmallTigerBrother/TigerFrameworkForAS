package com.mn.tiger.upload;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.task.TGTaskManager;
import com.mn.tiger.task.TGTaskParams;
import com.mn.tiger.task.result.TGTaskResult;
import com.mn.tiger.task.result.TGTaskResultHandler;
import com.mn.tiger.upload.observe.TGUploadObserveController;
import com.mn.tiger.upload.observe.TGUploadObserver;

import java.util.List;

/**
 * 
 * 璇ョ被浣滅敤鍙婂姛鑳借鏄�: 涓婁紶绠＄悊绫�
 * 
 * @date 2014骞�6鏈�18鏃�
 */
public class TGUploadManager
{	
	/**
	 * 鏃ュ織鏍囩
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();
	
	/**
	 * 涓婁紶鐘舵��
	 */
	public static final int UPLOAD_WAITING = -2;
	
	public static final int UPLOAD_STARTING = -1;
	
	public static final int UPLOAD_UPLOADING = 0;

	public static final int UPLOAD_SUCCEED = 1;

	public static final int UPLOAD_FAILED = 2;
	
	public static final int UPLOAD_PAUSE = 3;
	
	/**
	 * 涓婁笅鏂囦俊鎭�
	 */
	private Context mContext;
	
	/**
	 * 鏋勯�犳柟娉�
	 * @date 2014骞�6鏈�24鏃�
	 * @param context
	 */
	public TGUploadManager(Context context)
	{
		mContext = context;
	}
	
	/**
	 * 
	 * 璇ユ柟娉曠殑浣滅敤: 寮�濮嬩笂浼�
	 * @date 2014骞�6鏈�20鏃�
	 * @param uploadParams
	 */
	public int start(TGUploadParams uploadParams)
	{
		return enqueue(uploadParams);
	}
	
	/**
	 * 
	 * 璇ユ柟娉曠殑浣滅敤: 鍙栨秷涓婁紶
	 * @date 2014骞�6鏈�20鏃�
	 * @param taskId
	 */
	public void cancel(int taskId)
	{
		TGTaskManager.getInstance().cancelTask(taskId, TGTask.TASK_TYPE_UPLOAD);
	}
	
	/**
	 * 
	 * 璇ユ柟娉曠殑浣滅敤: 鍋滄涓婁紶
	 * @date 2014骞�6鏈�20鏃�
	 * @param taskId
	 */
	public void pause(int taskId)
	{
		TGTaskManager.getInstance().pauseTask(taskId, TGTask.TASK_TYPE_UPLOAD);
	}
	
	/**
	 * 
	 * 璇ユ柟娉曠殑浣滅敤: 鍚姩浼犲叆绫诲瀷鎵�鏈変笂浼犱换鍔�
	 * 
	 * @date 2014骞�8鏈�26鏃�
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
					// 鑾峰彇涓婁紶鍙傛暟
					uploadParams = (TGUploadParams) Class.forName(uploader.getParamsClsName()).newInstance();
					uploadParams.setFilePath(uploader.getFilePath());
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
	 * 
	 * 璇ユ柟娉曠殑浣滅敤: 鍙栨秷浼犲叆绫诲瀷鎵�鏈変笂浼犱换鍔�
	 * 
	 * @date 2014骞�8鏈�26鏃�
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
	 * 璇ユ柟娉曠殑浣滅敤: 鍋滄浼犲叆绫诲瀷鎵�鏈変笂浼犱换鍔�
	 * 
	 * @date 2014骞�8鏈�26鏃�
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
	 * 
	 * 璇ユ柟娉曠殑浣滅敤: 鍚姩鎵�鏈変笂浼犱换鍔�
	 * 
	 * @date 2014骞�8鏈�26鏃�
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
					// 鑾峰彇涓婁紶鍙傛暟
					uploadParams = (TGUploadParams) Class.forName(uploader.getParamsClsName()).newInstance();
					uploadParams.setFilePath(uploader.getFilePath());
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
	 * 璇ユ柟娉曠殑浣滅敤: 鍙栨秷鎵�鏈変笂浼犱换鍔�
	 * 
	 * @date 2014骞�8鏈�26鏃�
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
	 * 
	 * 璇ユ柟娉曠殑浣滅敤: 鍋滄鎵�鏈変笂浼犱换鍔�
	 * 
	 * @date 2014骞�8鏈�26鏃�
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
	 * 
	 * 璇ユ柟娉曠殑浣滅敤: 鎶奤ploader浠诲姟娣诲姞鍒颁笂浼犻槦鍒楋紝杩斿洖浠诲姟id
	 * @date 2014骞�6鏈�18鏃�
	 * @return
	 */
	private int enqueue(TGUploadParams uploadParams)
	{
		LogTools.p(LOG_TAG, "[Method:enqueue] start");
		
		final Bundle params = new Bundle();
		params.putSerializable("uploadParams", uploadParams);
		
		// 鏌ユ壘鏁版嵁搴撲腑鏄惁瀛樺湪璇ユ潯鏁版嵁
		TGUploader uploader = getUploadInfo(uploadParams.getFilePath());
		TGTaskParams taskParams = null;
		// 鏋勫缓浠诲姟鍙傛暟
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
		taskParams.setTaskType(TGTask.TASK_TYPE_UPLOAD);
		
		// 鍚姩浠诲姟
		return TGTaskManager.getInstance().startTask(mContext, taskParams);
    }
	
	/**
	 * 
	 * 璇ユ柟娉曠殑浣滅敤: 鏍规嵁浼犲叆鐨刱ey锛屾敞鍐屾暟鎹瀵熻��
	 * 
	 * @date 2014骞�3鏈�31鏃�
	 * @param observer
	 */
	public void registerDataSetObserver(int taskId, TGUploadObserver observer)
	{
		TGUploadObserveController.getInstance().registerDataSetObserver(
				String.valueOf(taskId), observer);
	}

	/**
	 * 璇ユ柟娉曠殑浣滅敤: 鍙栨秷娉ㄥ唽observer
	 * 
	 * @date 2014骞�3鏈�31鏃�
	 * @param observer
	 */
	public void unregisterObserver(TGUploadObserver observer)
	{
		TGUploadObserveController.getInstance().unregisterObserver(observer);
	}
	
	/**
	 * 
	 * 璇ユ柟娉曠殑浣滅敤:鑾峰彇鏂囦欢涓婁紶淇℃伅
	 * 
	 * @date 2014骞�8鏈�19鏃�
	 * @param filePath
	 * @return
	 */
	public TGUploader getUploadInfo(String filePath)
	{
		TGUploader uploader = null;
		uploader = TGUploadDBHelper.getInstance(mContext).getUploader(filePath);

		return uploader;
	}

	/**
	 * 
	 * 璇ユ柟娉曠殑浣滅敤: 鏍规嵁涓婁紶绫诲瀷鏌ヨ涓婁紶浠诲姟淇℃伅
	 * 
	 * @date 2014骞�8鏈�24鏃�
	 * @param uploadType
	 * @return
	 */
	public List<TGUploader> getUploadInfoByType(String uploadType)
	{
		return TGUploadDBHelper.getInstance(mContext).getUploaderByType(uploadType);
	}
	
	/**
	 * 涓婁紶浠诲姟鍥炰紶handler
	 */
	private TGTaskResultHandler resultHandler = new TGTaskResultHandler()
	{
		@Override
		public void handleTaskResult(TGTaskResult result)
		{
			TGUploadObserveController.getInstance().notifyChange(result);
		}
	};
	
	public Context getmContext()
	{
		return mContext;
	}

	public void setmContext(Context mContext)
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
