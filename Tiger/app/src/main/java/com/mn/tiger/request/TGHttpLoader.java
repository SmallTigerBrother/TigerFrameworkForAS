package com.mn.tiger.request;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mn.tiger.log.Logger;
import com.mn.tiger.request.async.TGHttpAsyncTask;
import com.mn.tiger.request.async.task.IRequestParser;
import com.mn.tiger.request.method.TGHttpParams;
import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.request.sync.ApacheSyncHttpLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Http请求类（包含异步、同步方法）
 */
public class TGHttpLoader<T> implements IRequestParser
{
	private static final Logger LOG = Logger.getLogger(TGHttpLoader.class);
	
	/**
	 * 执行异步任务的类
	 */
	private TGHttpAsyncTask<T> asyncTask;

	private int httpType = HttpType.REQUEST_UNKNOWN;
	
	public TGHttpLoader()
	{
	}

	/**
	 * 执行Http请求，请在调用该方法之前，调用setHttpType方法设置请求方式
	 * @param context
	 * @param requestUrl
	 * @param clazz
	 * @param callback
	 */
	public void load(Context context,String requestUrl, Class<T> clazz,
					 OnLoadCallback<T> callback)
	{
		if(httpType == HttpType.REQUEST_UNKNOWN)
		{
			LOG.e("[Method:load] you may not set HttpType before use this method, we will try load by HttpType.REQUEST_GET");
			execute(context, HttpType.REQUEST_GET, requestUrl, clazz.getName(), callback);
		}
		else
		{
			execute(context, httpType, requestUrl, clazz.getName(), callback);
		}
	}
	
	/**
	 * 执行get请求
	 * @param requestUrl 请求url
	 * @param clazz 解析结果类名
	 * @param callback 请求回调方法
	 */
	public void loadByGet(Context context, String requestUrl, Class<T> clazz, 
			OnLoadCallback<T> callback)
	{
		execute(context, HttpType.REQUEST_GET, requestUrl, clazz.getName(), callback);
	}
	
	/**
	 * 该方法的作用:
	 * Get请求
	 * @date 2013-12-1
	 * @param context
	 * @param requestUrl
	 * @param parameters
	 * @param properties content-type等请求参数
	 * @return
	 */
	public TGHttpResult loadByGetSync(Context context, String requestUrl, 
			TGHttpParams parameters, Map<String, String> properties)
	{
		return new ApacheSyncHttpLoader().loadByGetSync(
				context, requestUrl, parameters, properties);
	}
	
	/**
	 * 执行post请求
	 * @param requestUrl 请求url
	 * @param clazz 解析结果类名
	 * @param callback 请求回调方法
	 */
	public void loadByPost(Context context, String requestUrl, Class<T> clazz,
			OnLoadCallback<T> callback)
	{
		execute(context, HttpType.REQUEST_POST, requestUrl, clazz.getName(), callback);
	}
	
	/**
	 * 该方法的作用:post请求，请求参数可以自定义设置
	 * @date 2014年5月23日
	 * @param context
	 * @param requestUrl
	 * @param parameters
	 * @param properties content-type等请求参数
	 * @return
	 */
	public TGHttpResult loadByPostSync(Context context, String requestUrl, 
			TGHttpParams parameters, Map<String, String> properties)
	{
		return new ApacheSyncHttpLoader().loadByPostSync(
				context, requestUrl, parameters, properties);
	}
	
	/**
	 * 执行put请求
	 * @param requestUrl 请求url
	 * @param clazz 解析结果类名
	 * @param callback 请求回调方法
	 */
	public void loadByPut(Context context, String requestUrl, Class<T> clazz,
			OnLoadCallback<T> callback)
	{
		execute(context, HttpType.REQUEST_PUT, requestUrl, clazz.getName(), callback);
	}
	
	/**
	 * 该方法的作用: put请求，请求参数可以自定义设置
	 * @date 2014年5月23日
	 * @param context
	 * @param requestUrl
	 * @param parameters
	 * @param properties content-type等请求参数
	 * @return
	 */
	public TGHttpResult loadByPutSync(Context context, String requestUrl, 
			TGHttpParams parameters, Map<String, String> properties)
	{
		return new ApacheSyncHttpLoader().loadByPutSync(
				context, requestUrl, parameters, properties);
	}
	
	/**
	 * 执行delete请求
	 * @param requestUrl 请求url
	 * @param clazz 解析结果类名
	 * @param callback 请求回调方法
	 */
	public void loadByDelete(Context context, String requestUrl, Class<T> clazz,
			OnLoadCallback<T> callback)
	{
		execute(context, HttpType.REQUEST_DELETE, requestUrl, clazz.getName(),  callback);
	}
	
	/**
	 * 该方法的作用: Delete请求
	 * @date 2014年5月19日
	 * @param context
	 * @param requestUrl
	 * @param parameters
	 * @param properties content-type等请求参数
	 * @return
	 */
	public TGHttpResult loadByDeleteSync(Context context, String requestUrl, 
			TGHttpParams parameters, Map<String, String> properties)
	{
		return new ApacheSyncHttpLoader().loadByDeleteSync(
				context, requestUrl, parameters, properties);
	}
	
	/**
	 * 执行异步任务
	 * @param requestType 请求类型
	 * @param requestUrl 请求url
	 * @param resultClsName 解析结果类名
	 * @param callback 请求回调方法
	 */
	protected void execute(Context context, int requestType, String requestUrl,
			String resultClsName,  OnLoadCallback<T> callback)
	{
		getAsyncTask().setContext(context);
		getAsyncTask().setRequestType(requestType);
		getAsyncTask().setRequestUrl(requestUrl);
		getAsyncTask().setResultClsName(resultClsName);
		getAsyncTask().setLoadCallback(callback);
		getAsyncTask().setParserClsName(TGHttpLoader.this.getClass().getName());
		
		getAsyncTask().execute();
	}
	
	/**
	 * 解析请求结果
	 */
	@Override
	public Object parseRequestResult(TGHttpResult httpResult, String resultClsName)
	{
		//判断解析结果的className是否为Void
		if(!TextUtils.isEmpty(resultClsName) && !resultClsName.equals(Void.class.getName()))
		{
			String jsonStr = httpResult.getResult();
			//判断结果是否为空
			if (!TextUtils.isEmpty(jsonStr))
			{
				try
				{
					Gson gson = new Gson();
					return gson.fromJson(jsonStr, Class.forName(resultClsName));
				}
				catch (Exception e)
				{
					LOG.e("url : "  + getAsyncTask().getRequestUrl() + "\n params : " + 
				        getAsyncTask().getStringParams() + "\n" + e.getMessage());
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 取消任务
	 */
	public void cancel()
	{
		getAsyncTask().cancel();
	}
	
	/**
	 * 该方法的作用: 批量设置Headers请求参数
	 * @date 2014年5月23日
	 * @param properties
	 */
	public void setProperties(Map<String, String> properties)
	{
		if(null != properties)
		{
			this.getAsyncTask().setProperties(properties);
		}
		else
		{
			this.getAsyncTask().setProperties(new HashMap<String, String>());
		}
	}
	
	/**
	 * 添加Headers请求参数
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, String value)
	{
		if(null != key && null != value)
		{
			this.getAsyncTask().addProperty(key, value);
		}	
		else
		{
			LOG.e("[Method:addProperty] IllegalArguments were found key == " + key + " ; value == " + value);
		}
	}
	
	/**
	 * 添加请求参数
	 * @param key 参数键
	 * @param value 参数值
	 */
	public void addRequestParam(String key, String value)
	{
		if(null != key && null != value)
		{
			this.getAsyncTask().addRequestParam(key, value);
		}	
		else
		{
			LOG.e("[Method:addRequestParam] IllegalArguments were found key == " + key + " ; value == " + value);
		}
	}
	
	/**
	 * 添加请求参数
	 * @param key 参数键
	 * @param filePath 文件路径
	 */
	public void addFileParam(String key, String filePath)
	{
		if(null != key && null != filePath)
		{
			this.getAsyncTask().addFileParam(key, filePath);
		}
		else
		{
			LOG.e("[Method:addFileParam] IllegalArguments were found key == " + key + " ; filePath == " + filePath);
		}
	}
	
	/**
	 * 设置请求参数（会将已添加的参数替换）
	 * @param params
	 */
	public void setRequestParams(Map<String, String> params)
	{
		if(null != params)
		{
			this.getAsyncTask().setRequestParams(params);
		}
		else
		{
			this.getAsyncTask().setRequestParams(new HashMap<String, String>());
		}
	}

	/**
	 * 设置请求方式
	 * @param httpType
	 */
	public void setHttpType(int httpType)
	{
		this.httpType = httpType;
	}

	/**
	 * 获取异步任务
	 * @return
	 */
	protected final TGHttpAsyncTask<T> getAsyncTask()
	{
		if(null == asyncTask)
		{
			asyncTask = initAsyncTask();
		}
		
		return asyncTask;
	}
	
	/**
	 * 初始化异步任务（可Override）
	 * @return
	 */
	protected TGHttpAsyncTask<T> initAsyncTask()
	{
		return new TGHttpAsyncTask<T>("", HttpType.REQUEST_UNKNOWN, null);
	}
	
	/**
	 * 请求结果的回调
	 * 
	 * @date 2014-6-10
	 */
	public static interface OnLoadCallback<T>
	{
		/**
		 * 启动加载时回调
		 */
		public void onLoadStart();
		
		/**
		 * 请求成功时回调
		 * @param result 请求结果
		 */
		void onLoadSuccess(T result, TGHttpResult httpResult);
		
		/**
		 * 请求出现异常时回调
		 * @param code 错误码
		 * @param message 异常信息
		 */
		void onLoadError(int code, String message, TGHttpResult httpResult);
		
		/**
		 * 加载缓存中数据
		 * @param result
		 * @param httpResult
		 */
		void onLoadCache(T result, TGHttpResult httpResult);
		
		/**
		 * 加载结束
		 */
		void onLoadOver();
	}

}
