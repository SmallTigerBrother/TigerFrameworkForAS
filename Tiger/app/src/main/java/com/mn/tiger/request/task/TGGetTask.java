package com.mn.tiger.request.task;

import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.request.sync.AbstractSyncHttpLoader;
import com.mn.tiger.request.sync.OkHttpSyncHttpLoader;

/**
 * 该类作用及功能说明
 * Get请求任务类
 * @date 2014年3月18日
 */
public class TGGetTask extends TGHttpTask
{
	private AbstractSyncHttpLoader syncHttpLoader;

	@Override
	protected TGHttpResult executeHttpRequest()
	{
		syncHttpLoader =  new OkHttpSyncHttpLoader(getTaskID());
		return  syncHttpLoader.loadByGetSync(getContext(), getRequestUrl(),
				getRequestParams(), getRequestProperties());
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
