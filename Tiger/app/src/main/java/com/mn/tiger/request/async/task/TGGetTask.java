package com.mn.tiger.request.async.task;

import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.request.sync.ApacheSyncHttpLoader;

/**
 * 该类作用及功能说明
 * Get请求任务类
 * @date 2014年3月18日
 */
public class TGGetTask extends TGHttpTask
{
	@Override
	protected TGHttpResult executeHttpRequest()
	{
		return new ApacheSyncHttpLoader().loadByGetSync(getContext(), getRequestUrl(), 
				getRequestParams(), getRequestProperties());
	}
}
