package com.mn.tiger.request.sync;

import android.content.Context;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.request.error.TGHttpError;
import com.mn.tiger.request.method.TGHttpParams;
import com.mn.tiger.request.receiver.TGHttpResult;

import java.util.Map;

public abstract class AbstractSyncHttpLoader
{
	private static final String LOG_TAG = AbstractSyncHttpLoader.class.getSimpleName();
	
	public abstract TGHttpResult loadByGetSync(Context context, String requestUrl, 
			TGHttpParams parameters, Map<String, String> properties);
	
	public abstract TGHttpResult loadByPostSync(Context context, String requestUrl, 
			TGHttpParams parameters, Map<String, String> properties);
	
	public abstract TGHttpResult loadByPutSync(Context context, String requestUrl, 
			TGHttpParams parameters, Map<String, String> properties);
	
	public abstract TGHttpResult loadByDeleteSync(Context context, String requestUrl, 
			TGHttpParams parameters, Map<String, String> properties);

	public abstract void cancel();
	
	
	protected TGHttpResult getHttpResultWhileUrlIsNULL(Context context)
	{
		LogTools.e(LOG_TAG, "[Method:getHttpResultWhileUrlIsNULL] requestUrl can not be null or \"\" !");
		TGHttpResult result = new TGHttpResult();
		result.setResponseCode(TGHttpError.ERROR_URL);
		result.setResult(TGHttpError.getDefaultErrorMsg(context, TGHttpError.ERROR_URL));
		return result;
	}
}
