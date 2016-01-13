package com.mn.tiger.request.sync;

import android.content.Context;

import com.mn.tiger.log.Logger;
import com.mn.tiger.request.TGHttpParams;
import com.mn.tiger.request.error.TGHttpError;
import com.mn.tiger.request.receiver.TGHttpResult;

import java.util.Map;

public abstract class AbstractSyncHttpLoader
{
	private static final Logger LOG = Logger.getLogger(AbstractSyncHttpLoader.class);

	public abstract TGHttpResult loadByGetSync(Context context, String requestUrl,
											   TGHttpParams parameters, Map<String, String> properties);

	public abstract TGHttpResult loadByPostSync(Context context, String requestUrl,
												TGHttpParams parameters, Map<String, String> properties);

	public abstract TGHttpResult loadByPutSync(Context context, String requestUrl,
											   TGHttpParams parameters, Map<String, String> properties);

	public abstract TGHttpResult loadByDeleteSync(Context context, String requestUrl,
												  TGHttpParams parameters, Map<String, String> properties);

	public abstract void cancel();

	protected TGHttpResult getHttpResultWhileIOException(Context context, String url, String message)
	{
		LOG.e("[Method:getHttpResultWhileIOException] requestUrl == " + url);
		TGHttpResult result = new TGHttpResult();
		result.setResponseCode(TGHttpError.IOEXCEPTION);

		result.setResult(TGHttpError.getDefaultErrorMsg(context, TGHttpError.IOEXCEPTION));
		return result;
	}

	/**
	 * 是否
	 * @param context
	 * @param url
	 * @return
	 */
	protected TGHttpResult getHttpResultWhileUrlInvalid(Context context, String url)
	{
		LOG.e("[Method:getHttpResultWhileUrlInvalid] requestUrl is invalid == " + url);
		TGHttpResult result = new TGHttpResult();
		result.setResponseCode(TGHttpError.ERROR_URL);
		result.setResult(TGHttpError.getDefaultErrorMsg(context, TGHttpError.ERROR_URL));
		return result;
	}
}
