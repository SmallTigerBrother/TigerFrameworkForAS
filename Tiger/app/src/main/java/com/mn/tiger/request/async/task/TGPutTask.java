package com.mn.tiger.request.async.task;

import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.request.sync.AbstractSyncHttpLoader;
import com.mn.tiger.request.sync.OkHttpSyncHttpLoader;

/**
 * 该类作用及功能说明
 * Put请求任务类
 * @date 2014年8月22日
 */
public class TGPutTask extends TGHttpTask
{
	private AbstractSyncHttpLoader syncHttpLoader;

	protected TGHttpResult executeHttpRequest() 
	{
		syncHttpLoader = new OkHttpSyncHttpLoader(getTaskID());
		return syncHttpLoader.loadByPutSync(getContext(), getRequestUrl(),
				getRequestParams(), getRequestProperties());
	}

	@Override
	protected void onTaskCancel()
	{
		super.onTaskCancel();
		syncHttpLoader.cancel();
	}
}
