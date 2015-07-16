package com.mn.tiger.request.async.task;

import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.request.sync.ApacheSyncHttpLoader;

/**
 * 该类作用及功能说明
 * Put请求任务类
 * @date 2014年8月22日
 */
public class TGPutTask extends TGHttpTask
{
	protected TGHttpResult executeHttpRequest() 
	{
		return new ApacheSyncHttpLoader().loadByPutSync(getContext(), getRequestUrl(), 
				getRequestParams(), getRequestProperties());
	}
}
