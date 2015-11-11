package com.mn.tiger.request.task;

import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.request.sync.AbstractSyncHttpLoader;
import com.mn.tiger.request.sync.OkHttpSyncHttpLoader;

/**
 * 该类作用及功能说明 Delete请求任务
 *
 * @date 2014年8月22日
 */
public class TGDeleteTask extends TGHttpTask
{
	private AbstractSyncHttpLoader syncHttpLoader;

	protected TGHttpResult executeHttpRequest()
	{
		syncHttpLoader = new OkHttpSyncHttpLoader(getTaskID());
		return syncHttpLoader.loadByDeleteSync(getContext(),
				getRequestUrl(), getRequestParams(), getRequestProperties());
	}

	@Override
	protected void onTaskCancel()
	{
		syncHttpLoader.cancel();
		super.onTaskCancel();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		syncHttpLoader = null;
	}
}
