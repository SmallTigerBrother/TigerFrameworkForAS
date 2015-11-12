package com.mn.tiger.request.error;

import android.content.Context;

import com.mn.tiger.utility.CR;

public class TGHttpError
{
	public static final int UNKNOWN_EXCEPTION = 10000;
	
	public static final int NO_NETWORK = 10001;
	
	public static final int SOCKET_TIMEOUT = 10002;
	
	public static final int IOEXCEPTION = 10003;
	
	public static final int ERROR_URL = 10004;
	
	public static final int MOCK_TEST_ERROR = 20000;
	
	public static String getDefaultErrorMsg(Context context, int errorCode)
	{
		switch (errorCode)
		{
			case NO_NETWORK:
				return context.getString(CR.getStringId(context, "http_error_no_network"));
				
			case SOCKET_TIMEOUT:
				return context.getString(CR.getStringId(context, "http_error_socket_timeout"));
				
			case IOEXCEPTION:
				return context.getString(CR.getStringId(context, "http_error_ioexception"));
				
			case UNKNOWN_EXCEPTION:
				return context.getString(CR.getStringId(context, "http_error_unknown_exception"));

			case ERROR_URL:
				return "";
				
			case MOCK_TEST_ERROR:
				return context.getString(CR.getStringId(context, "string.mock_test_error"));
				
			default:
				break;
		}
		
		return "";
	}
}
