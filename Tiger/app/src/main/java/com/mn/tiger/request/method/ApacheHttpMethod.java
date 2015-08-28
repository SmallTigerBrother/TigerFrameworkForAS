package com.mn.tiger.request.method;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpUriRequest;

public abstract class ApacheHttpMethod
{
	private String url;

	/**
	 * 请求参数
	 */
	private TGHttpParams params = null;

	/**
	 * 消息头里的属性参数
	 */
	private HashMap<String, String> properties = new HashMap<String, String>();

	public ApacheHttpMethod()
	{
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public void setReqeustParams(TGHttpParams params)
	{
		this.params = params;
	}

	public Object getParams()
	{
		return params;
	}

	public HashMap<String, String> getProperties()
	{
		return properties;
	}

	public String getUrl()
	{
		return url;
	}

	public void setProperty(String key, String value)
	{
		this.properties.put(key, value);
	}

	public void setProperties(Map<String, String> properties)
	{
		if(null != properties)
		{
			this.properties.putAll(properties);
		}
	}

	public HttpUriRequest createHttpRequest()
	{
		HttpUriRequest request = initHttpRequest(url, params);

		for (String header : properties.keySet())
		{
			request.addHeader(header, properties.get(header));
		}

		return request;
	}

	/**
	 * 向HttpUrlConnection的输出流中加入参数
	 * @throws BusinessException
	 */
	protected void appendParams2OutputStream(TGHttpParams params)
			throws IOException
	{
	}

	/**
	 * 将参数转换为byte数组
	 * @param parameters
	 * @return
	 * @throws BusinessException
	 */
	protected byte[] convertParams2bytes(Object parameters)
	{
		return null;
	}

	protected abstract HttpUriRequest initHttpRequest(String url, TGHttpParams params);

}
