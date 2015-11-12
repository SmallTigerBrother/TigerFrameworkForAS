package com.mn.tiger.request.receiver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 该类作用及功能说明 网络请求结果
 * 
 * @version V2.0
 * @see JDK1.6,android-8
 */
public class TGHttpResult extends HashMap<String, Object>  implements Parcelable
{
	/**
	 * @date 2014年4月16日
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @date 2013-12-1 构造函数
	 */
	public TGHttpResult()
	{
		super();
	}
	
	protected TGHttpResult(Parcel source)
	{
		setResponseCode(source.readInt());
		setResult(source.readString());
		super.put("headers", source.readArray(TGHttpResult.class.getClassLoader()));
		this.changeHeaders2Map();
		setObjectResult(source.readSerializable());
	}

	/**
	 * 该方法的作用: 设置ResponseCode
	 * 
	 * @date 2013-12-1
	 * @param responseCode
	 */
	public void setResponseCode(int responseCode)
	{
		super.put("code", Integer.valueOf(responseCode));
	}

	/**
	 * 该方法的作用: 获取ResponseCode
	 * 
	 * @date 2013-12-1
	 * @return
	 */
	public int getResponseCode()
	{
		return (Integer) super.get("code");
	}

	/**
	 * 该方法的作用: 设置请求头
	 * 
	 * @date 2013-12-1
	 * @param headers
	 */
	public void setHeaders(Map<String, List<String>> headers)
	{
		super.put("headers", headers);
	}

	/**
	 * 该方法的作用: 获取请求头
	 * 
	 * @date 2013-12-1
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<String>> getHeaders()
	{
		return (Map<String, List<String>>) super.get("headers");
	}

	/**
	 * 该方法的作用: 设置请求结果
	 * 
	 * @date 2013-12-1
	 * @param result
	 */
	public void setResult(String result)
	{
		super.put("result", result);
	}

	/**
	 * 该方法的作用: 获取请求结果
	 * 
	 * @date 2013-12-1
	 * @return
	 */
	public String getResult()
	{
		return (String) super.get("result");
	}

	/**
	 * 该方法的作用: 将Headers转换为(String[])[]
	 * 
	 * @date 2014年5月5日
	 */
	protected void changeHeaders2Array()
	{
		Map<String, List<String>> map = getHeaders();

		if (map == null || map.isEmpty())
		{ 
			return;
		}
		List<String[]> headers = new ArrayList<String[]>();
		for (Entry<String, List<String>> entry : map.entrySet())
		{
			ArrayList<String> list = new ArrayList<String>(entry.getValue());
			// 把key添加到最后一个;
			list.add(entry.getKey());
			String[] value = list.toArray(new String[list.size()]);

			headers.add(value);
		}
		
		String[][] headersStrArray = headers.toArray(new String[headers.size()][]);
		
		super.put("headers", headersStrArray);
	}

	/**
	 * 该方法的作用: 将Headers转换为Map
	 * 
	 * @date 2014年5月5日
	 */
	protected void changeHeaders2Map()
	{
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		Object[] header = (Object[]) get("headers");// 无法一次强转为String[][]
		if (header == null)
		{
			return;
		}
		for (int i = 0; i < header.length; i++)
		{
			List<String> list = new ArrayList<String>(Arrays.asList((String[]) header[i]));
			// 最后一个作为键值
			String key = list.remove(list.size() - 1);

			map.put(key, list);
		}
		super.put("headers", map);
	}

	/**
	 * 该方法的作用: 设置序列化的结果
	 * 
	 * @date 2014年5月5日
	 * @param result
	 */
	public void setObjectResult(Object result)
	{
		super.put("objectResult", result);
	}

	/**
	 * 该方法的作用: 读取序列化的结果
	 * 
	 * @date 2014年5月5日
	 * @return
	 */
	public Object getObjectResult()
	{
		return super.get("objectResult");
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(getResponseCode());
		dest.writeString(getResult().toString());
		if (get("headers") instanceof Map)
		{
			changeHeaders2Array();
		}
		
		dest.writeArray((String[][]) get("headers"));
		
		if(null != getObjectResult())
		{
			dest.writeSerializable((Serializable) getObjectResult());
		}
	}
	
	public static final Parcelable.Creator<TGHttpResult> CREATOR = new Parcelable.Creator<TGHttpResult>()
	{
		@Override
		public TGHttpResult createFromParcel(Parcel source)
		{
			return new TGHttpResult(source);
		}

		@Override
		public TGHttpResult[] newArray(int size)
		{
			return new TGHttpResult[size];
		}
	};

}
