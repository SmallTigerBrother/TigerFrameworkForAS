package com.mn.tiger.request.method;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.text.TextUtils;
import android.util.Log;

import com.mn.tiger.log.LogTools;

public class ApacheGetMethod extends ApacheHttpMethod
{
	private static final String LOG_TAG = ApacheGetMethod.class.getSimpleName();

	@Override
	protected HttpUriRequest initHttpRequest(String url, TGHttpParams params)
	{
		return new HttpGet(TGHttpParams.appendParams2Url(url, params));
	}

}
