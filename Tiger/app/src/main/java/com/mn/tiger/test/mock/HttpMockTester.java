package com.mn.tiger.test.mock;

import android.text.TextUtils;

import com.mn.tiger.app.TGApplication;
import com.mn.tiger.log.Logger;
import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.request.sync.OkHttpSyncHttpLoader;
import com.mn.tiger.utility.FileUtils;

import java.util.HashMap;

public class HttpMockTester
{
	private static final Logger LOG = Logger.getLogger(HttpMockTester.class);

	public static boolean TEST_ABLE = false;

	public static HashMap<String, String> dataMap = new HashMap<String, String>();

	/**
	 * 添加mock测试数据
	 * @param url
	 * @param params
	 * @param mockFileName
	 */
	public static void addMockTestData(String url, HashMap<String, String> params, String mockFileName)
	{
		if(null != params)
		{
			url = url + "?" + params.toString();
		}
		dataMap.put(url, mockFileName);
	}

	/**
	 * 根据url、params获取测试数据
	 * @param url
	 * @param params
	 * @return
	 */
	public static TGHttpResult getMockTestData(String url, HashMap<String, String> params)
	{
		TGHttpResult httpResult = null;

		if(null != params)
		{
			url = url + "?" + params.toString();
		}

		String dataKey = dataMap.get(url);

		if(dataKey.startsWith("http://") || dataKey.startsWith("https://"))
		{
			httpResult = new OkHttpSyncHttpLoader(0).loadByGetSync(
					TGApplication.getInstance(), dataKey, null, null);
		}
		else
		{
			httpResult = new TGHttpResult();
			String data = FileUtils.readStringFromAsset(TGApplication.getInstance(), dataKey);
			httpResult.setResponseCode(200);
			httpResult.setResult(data);
		}

		LOG.d("[Method:getMockTestData] url : " + url + "\n" + "result : " + httpResult.getResult());
		return httpResult;
	}

	/**
	 * 判断某个接口是不是支持mock测试
	 * @param url
	 * @param params
	 * @return
	 */
	public static boolean isTestAble(String url, HashMap<String, String> params)
	{
		if(null != dataMap && TEST_ABLE)
		{
			if(null != params)
			{
				url = url + "?" + params.toString();
			}

			String dataKey = dataMap.get(url);
			return !TextUtils.isEmpty(dataKey);
		}

		return false;
	}

}
