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
	
	public static HashMap<String, String> dataMap = null;
	
	public static void setMockTestData(HashMap<String, String> dataMap)
	{
		HttpMockTester.dataMap = dataMap;
	}
	
	public static TGHttpResult getMockTestData(String url)
	{
		TGHttpResult httpResult = null;

		String dataKey = dataMap.get(url);

		if(dataKey.startsWith("http://") || dataKey.startsWith("https://"))
		{
			httpResult = new OkHttpSyncHttpLoader(-1).loadByGetSync(
					TGApplication.getInstance(), dataKey, null, null);
		}
		else
		{
			httpResult = new TGHttpResult();
			String data = FileUtils.readStringFromAsset(TGApplication.getInstance(), dataKey + ".json");
			httpResult.setResponseCode(200);
			httpResult.setResult(data);
		}

		LOG.d("[Method:getMockTestData] url : " + url + "\n" + "result : " + httpResult.getResult());
		return httpResult;
	}

	public static boolean isTestAble(String url)
	{
		if(null != dataMap && TEST_ABLE)
		{
			String dataKey = dataMap.get(url);
			return !TextUtils.isEmpty(dataKey);
		}

		return false;
	}

}
