package com.mn.tiger.request.async.task;

import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.request.sync.OkHttpSyncHttpLoader;

/**
 * 该类作用及功能说明
 * Post请求任务
 * @date 2014年3月18日
 */
public class TGPostTask extends TGHttpTask
{
	@Override
	protected TGHttpResult executeHttpRequest()
	{
		return new OkHttpSyncHttpLoader().loadByPostSync(getContext(), getRequestUrl(),
				getRequestParams(), getRequestProperties());
	}
}
