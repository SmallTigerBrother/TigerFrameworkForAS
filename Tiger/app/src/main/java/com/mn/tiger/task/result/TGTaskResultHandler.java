package com.mn.tiger.task.result;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.mn.tiger.log.Logger;

/**
 * 该类作用及功能说明 任务结果接收类
 *
 * @date 2014年3月18日
 */
public abstract class TGTaskResultHandler
{
	private static final Logger LOG = Logger.getLogger(TGTaskResultHandler.class);

	/**
	 * 任务结果处理
	 */
	private Handler mHandler = null;

	/**
	 * 接收结果消息
	 */
	private Messenger messenger = null;

	public TGTaskResultHandler()
	{
		mHandler = new Handler()
		{   @Override
			public void handleMessage(Message msg)
			{
				LOG.d("[Method:mHandler:handleMessage]");
				Bundle data = msg.getData();
				data.setClassLoader(TGTaskResult.class.getClassLoader());
				TGTaskResult taskResult = data.getParcelable("result");
				handleTaskResult(taskResult);
			}
		};

		messenger = new Messenger(mHandler);
	}

	/**
	 * 该方法的作用: 获取messenger
	 * @date 2014年7月28日
	 * @return
	 */
	public Messenger getMessenger()
	{
		return messenger;
	}

	/**
	 *
	 * 该方法的作用: 处理任务结果
	 * @date 2014年7月28日
	 * @param result
	 */
	public abstract void handleTaskResult(TGTaskResult result);
}
