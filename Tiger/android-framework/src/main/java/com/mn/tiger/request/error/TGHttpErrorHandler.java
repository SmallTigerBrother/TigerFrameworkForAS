package com.mn.tiger.request.error;

import android.content.Context;

import com.mn.tiger.request.receiver.TGHttpResult;

/**
 * 该类作用及功能说明:自定义错误处理类
 * 
 * @date 2013-11-1
 */
public class TGHttpErrorHandler implements IHttpErrorHandler
{
	private Context context;

	public TGHttpErrorHandler(Context dialogContext)
	{
		this.context = dialogContext;
	}

	@Override
	public boolean handleErrorInfo(TGHttpResult httpResult)
	{
		return false;
	}

	public static boolean hasHttpError(TGHttpResult httpResult)
	{
		int code = httpResult.getResponseCode();
		return code < 200 || code >= 300;
	}

	protected Context getContext()
	{
		return context;
	}
}
