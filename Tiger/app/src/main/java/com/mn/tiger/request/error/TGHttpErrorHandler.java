package com.mn.tiger.request.error;

import java.net.HttpURLConnection;

import android.content.Context;

import com.mn.tiger.request.receiver.TGHttpResult;

/**
 * 该类作用及功能说明:自定义错误处理类
 * 
 * @date 2013-11-1
 */
public class TGHttpErrorHandler implements IHttpErrorHandler
{
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();

	private Context context;
	protected static final int EINFO = 0;
	protected static final int ECODE = 1;
	protected static final int POCODE = 7;

	/**
	 * 无数据的错误码
	 */
	protected static final int NODATA = 8;

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

		if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_NO_CONTENT)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	
	protected Context getContext()
	{
		return context;
	}
}
