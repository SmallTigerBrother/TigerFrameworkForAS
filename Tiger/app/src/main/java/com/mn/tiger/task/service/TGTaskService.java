package com.mn.tiger.task.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 该类作用及功能说明 执行后台任务的Service
 * 
 * @date 2014年3月18日
 */
@SuppressLint("HandlerLeak")
public class TGTaskService extends Service
{
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();

	@Override
	public IBinder onBind(Intent intent)
	{
		return new TGRemoteBinder(this);
	}

}
