package com.mn.tiger.request.task;

import android.os.Bundle;
import android.text.TextUtils;

import com.mn.tiger.log.Logger;
import com.mn.tiger.request.IRequestParser;
import com.mn.tiger.request.TGHttpParams;
import com.mn.tiger.request.error.TGHttpError;
import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.request.test.HttpMockTester;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.utility.MD5;
import com.mn.tiger.utility.NetworkUtils;

import java.util.HashMap;

/**
 * 该类作用及功能说明 Http请求任务类
 *
 * @date 2014年3月18日
 */
public abstract class TGHttpTask extends TGTask
{
	private static final Logger LOG = Logger.getLogger(TGHttpTask.class);

	/**
	 * 参数名--url
	 */
	public static final String PARAM_URL = "url";

	/**
	 * 参数名--params
	 */
	public static final String PARAM_PARAMS = "params";

	/**
	 * 参数名--properties
	 */
	public static final String PARAM_PROPERTIES = "properties";

	/**
	 * 参数名--parserClsName
	 */
	public static final String PARAM_PARSERCLSNAME = "parserClsName";

	/**
	 * 参数名--resultClsName
	 */
	public static final String PARAM_RESLUTCLSNAME = "resultClsName";

	/**
	 * 参数名--是否支持缓存
	 */
	public static final String PARAM_CACHEABLE = "cacheable";

	/**
	 * 缓存用的Key
	 */
	private String cacheKey = "";

	@Override
	protected TGTaskState executeOnSubThread()
	{
		//先返回Cache中的数据
		sendCachedResult();

		if(NetworkUtils.isConnectivityAvailable(getContext()))
		{
			// 执行网络访问（不带异常处理）；异常处理下放到Activity中执行,
			TGHttpResult result;
			if(!HttpMockTester.TEST_ABLE || !HttpMockTester.isTestAble(getRequestUrl(), getRequestParams().getStringParams()))
			{
                LOG.i("[Method:executeOnSubThread] use http request");
				result = executeHttpRequest();
			}
			else
			{
                LOG.i("[Method:executeOnSubThread] use mock test");
				result = HttpMockTester.getMockTestData(getRequestUrl(), getRequestParams().getStringParams());
			}

			if (getTaskState() == TGTaskState.RUNNING)
			{
				// 返回网络访问结果
				sendTaskResult(parseRequestResult(result));
			}
		}
		else
		{
			TGHttpResult httpResult = new TGHttpResult();
			httpResult.setResponseCode(TGHttpError.NO_NETWORK);
			httpResult.setResult(TGHttpError.getDefaultErrorMsg(getContext(), TGHttpError.NO_NETWORK));
			sendTaskResult(httpResult);
		}

		//设置任务执行状态
		setTaskState(TGTaskState.FINISHED);

		return getTaskState();
	}

	/**
	 * 发送缓存结果
	 */
	private void sendCachedResult()
	{
		LOG.d("[Method:sendCachedResult]");

		if(isCacheable())
		{
			sendTaskResult(getResultFromCache(getCacheKey()));
		}
	}

	/**
	 * 从缓存中获取结果
	 * @param cacheKey 缓存Key
	 * @return
	 */
	protected TGHttpResult getResultFromCache(String cacheKey)
	{
		//TODO 
		return null;
	}

	/**
	 * 该方法的作用: 执行Http请求
	 *
	 * @date 2014年3月18日
	 * @return
	 */
	protected abstract TGHttpResult executeHttpRequest();

	/**
	 * 该方法的作用:
	 * 解析请求结果
	 * @date 2014年8月22日
	 * @param httpResult
	 * @return
	 */
	protected TGHttpResult parseRequestResult(TGHttpResult httpResult)
	{
		//解析结果
		String parserClsName = getParserClsName();
		String resultClsName = getResultClsName();
		if(!TextUtils.isEmpty(parserClsName) && !TextUtils.isEmpty(resultClsName))
		{
			try
			{
				IRequestParser parser = (IRequestParser) Class.forName(parserClsName).newInstance();
				httpResult.setObjectResult(parser.parseRequestResult(httpResult, resultClsName));
			}
			catch (Exception e)
			{
				LOG.e(e.getMessage(), e);
			}
		}

		//将结果存入缓存中
		save2Cache(httpResult);

		return httpResult;
	}

	/**
	 * 将请求结果存入缓存
	 * @param httpResult
	 */
	protected void save2Cache(TGHttpResult httpResult)
	{
		if(isCacheable())
		{
			LOG.d("[Method:save2Cache]");
			//TODO 存入缓存中
		}
	}

	@Override
	protected void sendTaskResult(Object result)
	{
		//若当前任务仍在运行，返回请求结果
		if(getTaskState() == TGTaskState.RUNNING)
		{
			LOG.d("[Method:sendTaskResult]");
			//发送执行结果
			super.sendTaskResult(result);
		}
	}

	/**
	 * 获取缓存Key
	 * @return
	 */
	protected String getCacheKey()
	{
		if(TextUtils.isEmpty(cacheKey))
		{
			//使用网络请求的全部参数作为缓存Key的特征值
			String requestFeature = getRequestUrl() + getRequestParamsStr() + getRequestPropertiesStr() +
					getResultClsNameStr() + getParserClsNameStr();
			cacheKey = MD5.toHexString(requestFeature.getBytes());
		}

		return cacheKey;
	}

	/**
	 * 该方法的作用:
	 * 获取请求参数
	 * @date 2014年8月15日
	 * @return
	 */
	public TGHttpParams getRequestParams()
	{
		Bundle httpParams = (Bundle)getParams();
		return (TGHttpParams) httpParams.getSerializable(PARAM_PARAMS);
	}

	/**
	 * 获取请求参数Str
	 * @return
	 */
	private String getRequestParamsStr()
	{
		Object requestParams = getRequestParams();
		if(null != requestParams)
		{
			return requestParams.toString();
		}

		return "";
	}

	/**
	 * 该方法的作用:
	 * 获取请求Url
	 * @date 2014年8月15日
	 * @return
	 */
	public String getRequestUrl()
	{
		Bundle httpParams = (Bundle)getParams();
		return httpParams.getString(PARAM_URL);
	}

	/**
	 * 该方法的作用:
	 * 获取请求header中的Properties
	 * @date 2014年8月15日
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> getRequestProperties()
	{
		Bundle httpParams = (Bundle)getParams();
		return (HashMap<String, String>)httpParams.get(PARAM_PROPERTIES);
	}

	/**
	 * 获取请求properties的Str
	 * @return
	 */
	private String getRequestPropertiesStr()
	{
		HashMap<String, String> requestProperties = getRequestProperties();
		if(null == requestProperties)
		{
			return "";
		}

		return requestProperties.toString();
	}

	/**
	 * 该方法的作用:
	 * 获取解析类的名字
	 * @date 2014年8月15日
	 * @return
	 */
	public String getParserClsName()
	{
		Bundle httpParams = (Bundle)getParams();
		return httpParams.getString(PARAM_PARSERCLSNAME);
	}

	/**
	 * 获取解析类的类名Str
	 * @return
	 */
	private String getParserClsNameStr()
	{
		String parserClsNameStr = getParserClsName();
		if(!TextUtils.isEmpty(parserClsNameStr))
		{
			return parserClsNameStr;
		}

		return "";
	}

	/**
	 * 该方法的作用:
	 * 获取解析类的名字
	 * @date 2014年8月15日
	 * @return
	 */
	public String getResultClsName()
	{
		Bundle httpParams = (Bundle)getParams();
		return httpParams.getString(PARAM_RESLUTCLSNAME);
	}

	/**
	 * 获取结果类名的Str
	 * @return
	 */
	private String getResultClsNameStr()
	{
		String resultClsNameStr = getResultClsName();
		if(!TextUtils.isEmpty(getResultClsName()))
		{
			return resultClsNameStr;
		}

		return "";
	}

	/**
	 * 判断是否支持缓存
	 * @return
	 */
	private boolean isCacheable()
	{
		Bundle httpParams = (Bundle)getParams();
		if(null != httpParams)
		{
			return httpParams.getBoolean(PARAM_CACHEABLE, false);
		}
		return false;
	}
}
