package com.mn.tiger.task.service;

import android.os.RemoteException;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.task.invoke.TGRemoteTaskInvoker;
import com.mn.tiger.task.invoke.TGTaskParams;

/**
 * 
 * 该类作用及功能说明 远程服务的aidl接口,是数据的通道 在绑定到service时,具体返回的对象
 * 
 * @date 2014年5月15日
 */
public class TGRemoteBinder extends TGRemoteService.Stub
{
	/**
	 * 日志标签
	 */
	private final String LOG_TAG = this.getClass().getSimpleName();
	/**
	 * 上下文
	 */
	private TGTaskService context;
	
	/**
	 * 构造函数
	 * @date 2014年6月28日
	 * @param context
	 */
	public TGRemoteBinder(TGTaskService context)
	{
		this.context = context;
	}

	@Override
	public void invoke(TGTaskParams taskParams) throws RemoteException
	{
		LogTools.d(LOG_TAG, "[Method:invoke]");
		TGRemoteTaskInvoker.invokeTask(context, taskParams);
	}
	
	public TGTaskService getContext()
	{
		return context;
	}
}
