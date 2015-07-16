package com.mn.tiger.request.method;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.text.TextUtils;

import com.mn.tiger.log.LogTools;

public class ApacheGetMethod extends ApacheHttpMethod
{
	private static final String LOG_TAG = ApacheGetMethod.class.getSimpleName();
	
	@Override
	protected HttpUriRequest initHttpRequest(String url, TGHttpParams params)
	{
		return new HttpGet(appendParams2Url(url, params));
	}
	
	protected String appendParams2Url(String url, TGHttpParams params)
	{
		String paramsString = null;
		if (params instanceof Map<?, ?>)
		{
			try
			{
				paramsString = ((TGHttpParams)params).mergeStringParams2KeyValuePair();
			}
			catch (UnsupportedEncodingException e)
			{
				LogTools.e(LOG_TAG, e);
			}
		}
		
		if (!TextUtils.isEmpty(paramsString))
		{
			if (url.indexOf("?") < 0)
			{
				url = url + "?";
			}
			url = url + paramsString;;
		}

		return url;
	}
}
