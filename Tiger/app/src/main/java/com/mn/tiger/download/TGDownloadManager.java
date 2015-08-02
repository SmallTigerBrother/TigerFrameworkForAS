package com.mn.tiger.download;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.mn.tiger.datastorage.db.sqlite.Selector;
import com.mn.tiger.download.observe.TGDownloadObserveController;
import com.mn.tiger.download.observe.TGDownloadObserver;
import com.mn.tiger.log.LogTools;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.task.TGTaskManager;
import com.mn.tiger.task.invoke.TGTaskParams;
import com.mn.tiger.task.result.TGTaskResult;
import com.mn.tiger.task.result.TGTaskResultHandler;
import com.mn.tiger.utility.FileUtils;

/**
 * 
 * 该类作用及功能说明: 下载管理类
 * 
 * @date 2014年8月18日
 */
public class TGDownloadManager
{
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();

	/**
	 * 上下文信息
	 */
	private Context mContext;

	/**
	 * 构造方法
	 * 
	 * @date 2014年6月24日
	 * @param context
	 */
	public TGDownloadManager(Context context)
	{
		mContext = context;
	}

	/**
	 * 
	 * 该方法的作用: 开始下载
	 * 
	 * @date 2014年6月20日
	 * @param downloadParams
	 */
	public int start(TGDownloadParams downloadParams)
	{
		return enqueue(downloadParams);
	}

	/**
	 * 
	 * 该方法的作用: 取消下载
	 * 
	 * @date 2014年6月20日
	 * @param taskId
	 */
	public void cancel(int taskId)
	{
		TGTaskManager.getInstance().cancelTask(taskId, TGTask.TASK_TYPE_DOWNLOAD);
	}

	/**
	 * 
	 * 该方法的作用: 停止下载
	 * 
	 * @date 2014年6月20日
	 * @param taskId
	 */
	public void pause(int taskId)
	{
		TGTaskManager.getInstance().pauseTask(taskId, TGTask.TASK_TYPE_DOWNLOAD);
	}

	/**
	 * 
	 * 该方法的作用: 启动传入类型所有下载任务
	 * 
	 * @date 2014年8月26日
	 */
	public void startAll(String type)
	{
		List<TGDownloader> downloaders = TGDownloadDBHelper.getInstance(mContext).getDownloader(type);

		if (null != downloaders)
		{
			TGDownloadParams downloadParams = null;
			try
			{
				for (TGDownloader downloader : downloaders)
				{
					// 获取下载参数
					downloadParams = (TGDownloadParams) Class.forName(downloader
							.getParamsClsName()).newInstance();
					downloadParams.setRequestType(downloader.getRequestType());
					downloadParams.setSavePath(downloader.getSavePath());
					downloadParams.setUrl(downloader.getUrl());
					downloadParams.setParams(downloader.getParams());
					if (!TextUtils.isEmpty(downloader.getTaskClsName()))
					{
						downloadParams.setTaskClsName(downloader.getTaskClsName());
					}
					start(downloadParams);
				}
			}
			catch(Exception e)
			{
				LogTools.e(LOG_TAG, e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 * 该方法的作用: 取消传入类型所有下载任务
	 * 
	 * @date 2014年8月26日
	 */
	public void cancelAll(String type)
	{
		List<TGDownloader> downloaders = TGDownloadDBHelper.getInstance(mContext).getDownloader(type);

		if (null != downloaders)
		{
			for (TGDownloader downloader : downloaders)
			{
				cancel(Integer.valueOf(downloader.getId()));
			}
		}
	}

	/**
	 * 
	 * 该方法的作用: 停止传入类型所有下载任务
	 * 
	 * @date 2014年8月26日
	 */
	public void pauseAll(String type)
	{
		List<TGDownloader> downloaders = TGDownloadDBHelper.getInstance(mContext).getDownloader(type);

		if (null != downloaders)
		{
			for (TGDownloader downloader : downloaders)
			{
				pause(Integer.valueOf(downloader.getId()));
			}
		}
	}
	
	/**
	 * 
	 * 该方法的作用: 启动所有下载任务
	 * 
	 * @date 2014年8月26日
	 */
	public void startAll()
	{
		List<TGDownloader> downloaders = TGDownloadDBHelper.getInstance(mContext).getAllDownloader();

		if (null != downloaders)
		{
			TGDownloadParams downloadParams = null;
			try
			{
				for (TGDownloader downloader : downloaders)
				{
					// 获取下载参数
					downloadParams = (TGDownloadParams) Class.forName(downloader
							.getParamsClsName()).newInstance();
					downloadParams.setRequestType(downloader.getRequestType());
					downloadParams.setSavePath(downloader.getSavePath());
					downloadParams.setUrl(downloader.getUrl());
					downloadParams.setParams(downloader.getParams());
					if (!TextUtils.isEmpty(downloader.getTaskClsName()))
					{
						downloadParams.setTaskClsName(downloader.getTaskClsName());
					}
					start(downloadParams);
				}
			}
			catch(Exception e)
			{
				LogTools.e(LOG_TAG, e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 * 该方法的作用: 取消所有下载任务
	 * 
	 * @date 2014年8月26日
	 */
	public void cancelAll()
	{
		List<TGDownloader> downloaders = TGDownloadDBHelper.getInstance(mContext).getAllDownloader();
		if (null != downloaders)
		{
			for (TGDownloader downloader : downloaders)
			{
				cancel(Integer.valueOf(downloader.getId()));
			}
		}
	}

	/**
	 * 
	 * 该方法的作用: 停止所有下载任务
	 * 
	 * @date 2014年8月26日
	 */
	public void pauseAll()
	{
		List<TGDownloader> downloaders = TGDownloadDBHelper.getInstance(mContext).getAllDownloader();

		if (null != downloaders)
		{
			for (TGDownloader downloader : downloaders)
			{
				pause(Integer.valueOf(downloader.getId()));
			}
		}
	}

	/**
	 * 
	 * 该方法的作用: 把下载任务添加到下载队列，返回任务id
	 * 
	 * @date 2014年6月18日
	 * @param downloadParams
	 * @return
	 */
	private int enqueue(TGDownloadParams downloadParams)
	{
		LogTools.p(LOG_TAG, "[Method:enqueue] start");
		
		final Bundle params = new Bundle();
		params.putSerializable("downloadParams", downloadParams);

		if (TextUtils.isEmpty(downloadParams.getTaskClsName()))
		{
			downloadParams.setTaskClsName(TGDownloadTask.class.getName());
		}
		
		// 获取本地是否有相同的下载任务
		TGDownloader downloader = getLocalDownloader(downloadParams);
		// 检查本地下载任务和下载文件
		downloader = checkLocalDownloader(downloader);
		
		// 构建任务参数，当没有taskID传入时，直接构建新任务下载。
		// 当有taskID传入时，需要在下载队列中查看是否已经有该下载任务，如果有，则不需要新加入任务。
		TGTaskParams taskParams = null;
		if(downloader != null)
		{
			//如果当前任务正在下载中，直接返回当前任务的id
			if(downloader.getDownloadStatus() == TGDownloader.DOWNLOAD_DOWNLOADING)
			{
				return Integer.parseInt(downloader.getId());
			}
			else
			{
				taskParams = TGTaskManager.createTaskParams(params,
						downloadParams.getTaskClsName(), resultHandler, Integer.parseInt(downloader.getId()));
			}
		}
		else
		{
			taskParams = TGTaskManager.createTaskParams(params,
				downloadParams.getTaskClsName(), resultHandler);
		}

		taskParams.setBundleParams(params);
		taskParams.setTaskType(TGTask.TASK_TYPE_DOWNLOAD);

		// 启动任务
		return TGTaskManager.getInstance().startTask(mContext, taskParams);
	}

	/**
	 * 该方法的作用: 根据下载参数从本地数据库中查询对应的downloader，查询策略由使用者自行决定。
	 *              如果没有重复的下载任务，返回null
	 * @date 2014年9月3日
	 * @return
	 */
	public TGDownloader getLocalDownloader(TGDownloadParams downloadParams)
	{
		return getDownloadInfo(downloadParams.getUrl(), downloadParams.getParams(), downloadParams.getSavePath());
	}
	
	/**
	 * 该方法的作用: 检测本地文件和本地下载记录是否一致
	 * @date 2014年9月3日
	 * @param downloader
	 */
	private TGDownloader checkLocalDownloader(TGDownloader downloader)
	{
		if(downloader == null)
		{
			return null;
		}
		
		// 如果数据库中有记录，但本地文件不存在，删除数据库记录
		String savePath = downloader.getSavePath();
		File file = FileUtils.getFile(savePath);
		if(file == null || !file.exists())
		{
			delDownloader(downloader);
			return null;
		}
		
		return downloader;
	}
	
	/**
	 * 
	 * 该方法的作用: 根据传入的key，注册数据观察者
	 * 
	 * @date 2014年3月31日
	 * @param entityType
	 * @param observer
	 */
	public void registerDownloadObserver(int taskId, TGDownloadObserver observer)
	{
		TGDownloadObserveController.getInstance().registerDataSetObserver(String.valueOf(taskId),
				observer);
	}

	/**
	 * 该方法的作用: 取消注册observer
	 * 
	 * @date 2014年3月31日
	 * @param observer
	 */
	public void unregisterDownloadObserver(TGDownloadObserver observer)
	{
		TGDownloadObserveController.getInstance().unregisterObserver(observer);
	}

	/**
	 * 
	 * 该方法的作用:获取文件下载信息
	 * 
	 * @date 2014年8月19日
	 * @param urlstr
	 * @param params
	 * @return
	 */
	public TGDownloader getDownloadInfo(String urlstr, HashMap<String, String> params, String savePath)
	{
		TGDownloader downloader = null;
		downloader = TGDownloadDBHelper.getInstance(mContext).getDownloader(urlstr, params, savePath);

		return downloader;
	}

	/**
	 * 
	 * 该方法的作用: 根据下载类型查询下载任务信息
	 * 
	 * @date 2014年8月24日
	 * @param downloadType
	 * @return
	 */
	public List<TGDownloader> getDownloadInfoByType(String downloadType)
	{
		return TGDownloadDBHelper.getInstance(mContext).getDownloader(downloadType);
	}

	/**
	 * 
	 * 该方法的作用: 根据传入sql查询下载任务信息
	 * 
	 * @date 2014年8月24日
	 * @param selector
	 * @return
	 */
	public List<TGDownloader> getDownloadInfoBySQL(Selector selector)
	{
		return TGDownloadDBHelper.getInstance(mContext).getDownloaderBySql(selector);
	}
	
	/**
	 * 
	 * 该方法的作用: 删除下载任务
	 * 
	 * @date 2014年8月24日
	 * @param downloader
	 * @return
	 */
	public void delDownloader(TGDownloader downloader)
	{
		TGDownloadDBHelper.getInstance(mContext).deleteDownloader(downloader);
	}

	/**
	 * 下载任务回传handler
	 */
	private TGTaskResultHandler resultHandler = new TGTaskResultHandler()
	{
		@Override
		public void handleTaskResult(TGTaskResult result)
		{
			TGDownloadObserveController.getInstance().notifyChange(result);
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
}
