package com.mn.tiger.upload.observe;

import com.mn.tiger.log.Logger;
import com.mn.tiger.upload.TGUploader;

/**
 * 该类作用及功能说明 ：数据观察者
 *
 * @date 2014年3月31日
 */
public class TGUploadObserver
{
	private static final Logger LOG = Logger.getLogger(TGUploadObserver.class);

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
		LOG.d("[Method:onStart]");
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
		LOG.d("[Method:onProgress] progress: " + progress);
	}

	/**
	 *
	 * 该方法的作用: 任务完成回调
	 *
	 * @date 2014年6月26日
	 */
	public void onSuccess(TGUploader uploader)
	{
		LOG.i("[Method:onSuccess]");
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
		LOG.w("[Method:onFailed] errorCode: " + errorCode + "; errorMessage: "
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
		LOG.w("[Method:onPause]");
	}

	/**
	 * 取消上传回调
	 * @param uploader
	 */
	public void onCancel(TGUploader uploader)
	{
		LOG.w("[Method:onCancel]");
	}
}
