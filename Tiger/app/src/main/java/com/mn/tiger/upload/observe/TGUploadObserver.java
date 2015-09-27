package com.mn.tiger.upload.observe;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.upload.TGUploader;

/**
 * 该类作用及功能说明 ：数据观察者
 * 
 * @date 2014年3月31日
 */
public class TGUploadObserver
{
	/**
	 * log tag
	 */
	public static final String TAG = TGUploadObserveController.class.getSimpleName();
	
	/**
	 * 注册observer时使用得key
	 */
	public String key;
	
	/**
	 * 构造函数
	 * 
	 * @date 2014年6月29日
	 */
	public TGUploadObserver()
	{
	}

	/**
	 * 
	 * 该方法的作用:获取键值
	 * 
	 * @date 2014年6月29日
	 * @return
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * 
	 * 该方法的作用:设置键值
	 * 
	 * @date 2014年6月29日
	 * @param key
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	/**
	 * 
	 * 该方法的作用: 任务开始回调
	 * 
	 * @date 2014年6月26日
	 * @param uploader
	 */
	public void onStart(TGUploader uploader)
	{
		LogTools.d(TAG, "[Method:onStart]");
	}
	
	/**
	 * 
	 * 该方法的作用: 任务进度变化回调
	 * 
	 * @date 2014年6月26日
	 * @param progress
	 */
	public void onProgress(TGUploader uploader, int progress)
	{
		LogTools.d(TAG, "[Method:onProgress] progress: " + progress);
	}

	/**
	 * 
	 * 该方法的作用: 任务完成回调
	 * 
	 * @date 2014年6月26日
	 */
	public void onSuccess(TGUploader uploader)
	{
		LogTools.p(TAG, "[Method:onSuccess]");
	}

	/**
	 * 
	 * 该方法的作用: 任务失败回调
	 * 
	 * @date 2014年6月27日
	 * @param errorCode
	 * @param errorMessage
	 */
	public void onFailed(TGUploader uploader, int errorCode, String errorMessage)
	{
		LogTools.p(TAG, "[Method:onFailed] errorCode: " + errorCode + "; errorMessage: "
				+ errorMessage);
	}

	/**
	 * 
	 * 该方法的作用: 任务停止回调
	 * 
	 * @date 2014年6月27日
	 */
	public void onPause(TGUploader uploader)
	{
		LogTools.p(TAG, "[Method:onPause]");
	}
}
