package com.mn.tiger.test.mock;

import java.util.HashMap;

import android.text.TextUtils;

import com.mn.tiger.app.TGApplication;
import com.mn.tiger.log.Logger;
import com.mn.tiger.request.error.TGHttpError;
import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.request.sync.ApacheSyncHttpLoader;
import com.mn.tiger.utility.FileUtils;

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
		TGHttpResult httpResult = getDefaultHttpResult();
		if(null != dataMap && TEST_ABLE)
		{
			String dataKey = dataMap.get(url);
			if(!TextUtils.isEmpty(dataKey))
			{
				if(dataKey.startsWith("http://") || dataKey.startsWith("https://"))
				{
					httpResult = new ApacheSyncHttpLoader().loadByGetSync(
							TGApplication.getInstance(), dataKey, null, null);
				}
				else
				{
					String data = FileUtils.readStringFromAsset(TGApplication.getInstance(), dataKey + ".json");
					httpResult.setResponseCode(200);
					httpResult.setResult(data);
				}
				
				LOG.d("[Method:getMockTestData] url : " + url + "\n" + "result : " + httpResult.getResult());
				return httpResult;
			}
		}
		
		return httpResult;
	}
	
	private static TGHttpResult getDefaultHttpResult()
	{
		TGHttpResult httpResult = new TGHttpResult();
		httpResult.setResponseCode(TGHttpError.MOCK_TEST_ERROR);
		httpResult.setResult(TGHttpError.getDefaultErrorMsg(TGApplication.getInstance(), TGHttpError.MOCK_TEST_ERROR));
		return httpResult;
	}
	
	
}
