package com.mn.tiger.request.sync;

import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.mn.tiger.request.client.ApacheHttpClient;
import com.mn.tiger.request.method.ApacheGetMethod;
import com.mn.tiger.request.method.ApachePostMethod;
import com.mn.tiger.request.method.TGHttpParams;
import com.mn.tiger.request.receiver.TGHttpResult;


public class ApacheSyncHttpLoader extends AbstractSyncHttpLoader
{
	@Override
	public TGHttpResult loadByGetSync(Context context, String requestUrl, TGHttpParams parameters,
			Map<String, String> properties)
	{
		if (TextUtils.isEmpty(requestUrl))
		{
			return getHttpResultWhileUrlIsNULL(context);
		}
		
		ApacheHttpClient httpClient = new ApacheHttpClient(context);
		ApacheGetMethod getMethod = new ApacheGetMethod();
		getMethod.setUrl(requestUrl);
		getMethod.setReqeustParams(parameters);
		getMethod.setProperties(properties);
		return httpClient.execute(getMethod);
	}

	@Override
	public TGHttpResult loadByPostSync(Context context, String requestUrl, TGHttpParams parameters,
			Map<String, String> properties)
	{
		if (TextUtils.isEmpty(requestUrl))
		{
			return getHttpResultWhileUrlIsNULL(context);
		}
		
		ApacheHttpClient httpClient = new ApacheHttpClient(context);
		ApachePostMethod getMethod = new ApachePostMethod();
		getMethod.setUrl(requestUrl);
		getMethod.setReqeustParams(parameters);
		getMethod.setProperties(properties);
		return httpClient.execute(getMethod);
	}

	@Override
	public TGHttpResult loadByPutSync(Context context, String requestUrl, TGHttpParams parameters,
			Map<String, String> properties)
	{
		if (TextUtils.isEmpty(requestUrl))
		{
			return getHttpResultWhileUrlIsNULL(context);
		}
		return null;
	}

	@Override
	public TGHttpResult loadByDeleteSync(Context context, String requestUrl, TGHttpParams parameters,
			Map<String, String> properties)
	{
		if (TextUtils.isEmpty(requestUrl))
		{
			return getHttpResultWhileUrlIsNULL(context);
		}
		return null;
	}

}
