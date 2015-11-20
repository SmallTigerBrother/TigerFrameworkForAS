package com.mn.tiger.request;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mn.tiger.app.TGActionBarActivity;
import com.mn.tiger.log.Logger;
import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.request.receiver.TGHttpResultHandler;
import com.mn.tiger.request.sync.OkHttpSyncHttpLoader;
import com.mn.tiger.request.task.TGDeleteTask;
import com.mn.tiger.request.task.TGGetTask;
import com.mn.tiger.request.task.TGHttpTask;
import com.mn.tiger.request.task.TGPostTask;
import com.mn.tiger.request.task.TGPutTask;
import com.mn.tiger.task.TGTaskManager;
import com.mn.tiger.task.TGTaskParams;
import com.mn.tiger.task.TaskType;

import java.util.HashMap;
import java.util.Map;

/**
 * Http请求类（包含异步、同步方法）
 */
public class TGHttpLoader<T> implements IRequestParser
{
	private static final Logger LOG = Logger.getLogger(TGHttpLoader.class);

	/**
	 * 运行环境
	 */
	private Context context;

	/**
	 * 请求URL
	 */
	private String requestUrl;

	/**
	 * 请求类型，默认为Post类型
	 */
	private int requestType = HttpType.REQUEST_GET;

	/**
	 * 网络请求headers参数
	 */
	private HashMap<String, String> properties = null;

	/**
	 * 网络请求参数
	 */
	private TGHttpParams params = null;

	/**
	 * 字符串参数
	 */
	private HashMap<String, String> stringParams = null;

	/**
	 * 文件参数
	 */
	private HashMap<String, String> fileParams = null;

	/**
	 * 任务是否已取消
	 */
	private boolean isCancel = false;

	/**
	 * 任务ID
	 */
	private int taskID = -1;

	/**
	 * 执行任务类名
	 */
	private String taskClsName = null;

	/**
	 * 解析类的类名
	 */
	private String parserClsName = "";

	/**
	 * 结果类的类名
	 */
	private String resultClsName = "";

	/**
	 * 请求结果回调类
	 */
	private OnLoadCallback<T> loadCallback;

	public TGHttpLoader()
	{
		stringParams = new HashMap<String, String>();
		params = new TGHttpParams();
		params.setStringParams(stringParams);
		parserClsName = this.getClass().getName();
	}

	/**
	 * 设置请求方式
	 * @param httpType
	 */
	public void setHttpType(int httpType)
	{
		this.requestType = httpType;
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
		if(requestType == HttpType.REQUEST_UNKNOWN)
		{
			LOG.e("[Method:load] you may not set HttpType before use this method, we will try load by HttpType.REQUEST_GET");
			execute(context, HttpType.REQUEST_GET, requestUrl, clazz.getName(), callback);
		}
		else
		{
			execute(context, requestType, requestUrl, clazz.getName(), callback);
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
	 * @return
	 */
	public TGHttpResult loadByGetSync(Context context, String requestUrl)
	{
        TGHttpParams httpParams = new TGHttpParams();
        httpParams.setStringParams(stringParams);
        httpParams.setFileParams(fileParams);
		return new OkHttpSyncHttpLoader(0).loadByGetSync(context, requestUrl, httpParams, properties);
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
	 * @return
	 */
	public TGHttpResult loadByPostSync(Context context, String requestUrl)
	{
        TGHttpParams httpParams = new TGHttpParams();
        httpParams.setStringParams(stringParams);
        httpParams.setFileParams(fileParams);
		return new OkHttpSyncHttpLoader(0).loadByPostSync(context, requestUrl, httpParams, properties);
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
	 * @return
	 */
	public TGHttpResult loadByPutSync(Context context, String requestUrl)
	{
        TGHttpParams httpParams = new TGHttpParams();
        httpParams.setStringParams(stringParams);
        httpParams.setFileParams(fileParams);
		return new OkHttpSyncHttpLoader(0).loadByPutSync(context, requestUrl, httpParams, properties);
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
		execute(context, HttpType.REQUEST_DELETE, requestUrl, clazz.getName(), callback);
	}

	/**
	 * 该方法的作用: Delete请求
	 * @date 2014年5月19日
	 * @param context
	 * @param requestUrl
	 * @return
	 */
	public TGHttpResult loadByDeleteSync(Context context, String requestUrl)
	{
        TGHttpParams httpParams = new TGHttpParams();
        httpParams.setStringParams(stringParams);
        httpParams.setFileParams(fileParams);
		return new OkHttpSyncHttpLoader(0).loadByDeleteSync(context, requestUrl, httpParams, properties);
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
		this.context = context;
		this.requestType = requestType;
		this.resultClsName = resultClsName;
		this.loadCallback = callback;
		this.requestUrl = requestUrl;
		this.executeLoad();
	}

	/**
	 * 该方法的作用:
	 * 执行任务
	 * @date 2014年8月22日
	 */
	public int executeLoad()
	{
		if (null == context || (context instanceof Activity && ((Activity) context).isFinishing()))
		{
			return taskID;
		}

		if(TextUtils.isEmpty(requestUrl))
		{
			LOG.e("[Method:executeLoad] the requestUrl is null, please check your code");
			return taskID;
		}

		onPreExecute();
		doInBackground(params);

		return taskID;
	}

	/**
	 * 请求之前执行方法（MainThread）
	 */
	protected void onPreExecute()
	{
		if(null != loadCallback)
		{
			loadCallback.onLoadStart();
		}
	}

	/**
	 * 该方法的作用:
	 * 后台执行
	 * @date 2014年8月22日
	 * @param params
	 * @return
	 */
	protected int doInBackground(TGHttpParams params)
	{
		LOG.d("[Method: doInBackground]  " + "start request.");

		if(isCancelled())
		{
			loadCallback.onLoadOver();
			return taskID;
		}

		TGTaskParams taskParams = initHttpParams(params);

		taskID = TGTaskManager.getInstance().startTask(context, taskParams);

		//将taskID注册到Activity
		if(context instanceof TGActionBarActivity)
		{
			((TGActionBarActivity)context).registerHttpLoader(taskID);
		}

		return taskID;
	}

	/**
	 * 该方法的作用:
	 * 初始化Http请求参数（MainTread）
	 * @date 2014年8月22日
	 * @param params
	 * @return
	 */
	protected TGTaskParams initHttpParams(TGHttpParams params)
	{
		if(requestType > HttpType.REQUEST_PUT ||
				requestType < HttpType.REQUEST_POST)
		{
			throw new RuntimeException("Your requestType is invalid!");
		}

		// 设置请求参数
		Bundle data = new Bundle();
		data.putString(TGHttpTask.PARAM_URL, requestUrl);
		if(null != properties && properties.size() > 0)
		{
			data.putSerializable(TGHttpTask.PARAM_PROPERTIES, properties);
		}

		if(null != params)
		{
			data.putSerializable(TGHttpTask.PARAM_PARAMS, params);
		}

		data.putString(TGHttpTask.PARAM_PARSERCLSNAME, parserClsName);
		data.putString(TGHttpTask.PARAM_RESLUTCLSNAME, resultClsName);

		TGTaskParams taskParams = TGTaskManager.createTaskParams(data,
				getTaskClsName(requestType), initHttpResultHandler());
		taskParams.setTaskType(TaskType.TASK_TYPE_HTTP);

		return taskParams;
	}

	/**
	 * 初始化http请求回调接口
	 * @return
	 */
	protected final TGHttpResultHandler initHttpResultHandler()
	{
		TGHttpResultHandler resultHandler = new TGHttpResultHandler(context)
		{
			@Override
			protected void onSuccess(TGHttpResult httpResult)
			{
				LOG.i("[Method:resultHandler:onSuccess]");
				//解析请求结果
				if(!isCancelled() && null != loadCallback)
				{
					loadCallback.onLoadSuccess(parseOriginalResult(httpResult.getObjectResult()),
							httpResult);
				}
			}

			@Override
			protected void onError(TGHttpResult httpResult)
			{
				LOG.i("[Method:resultHandler:onError]");
				//解析请求结果
				if(!isCancelled() && null != loadCallback)
				{
					loadCallback.onLoadError(httpResult.getResponseCode(),
							httpResult.getResult(), httpResult);
				}
			}

			@Override
			protected void onReturnCachedResult(TGHttpResult httpResult)
			{
				LOG.i("[Method:resultHandler:onReturnCachedResult]");
				//解析请求结果
				if(!isCancelled() && null != loadCallback)
				{
					loadCallback.onLoadCache(parseOriginalResult(httpResult.getObjectResult()),
							httpResult);
				}
			}

			@Override
			protected void onRequestOver()
			{
				//请求完毕时，取消注册
				if(context instanceof TGActionBarActivity)
				{
					((TGActionBarActivity)context).unRegisterHttpLoader(taskID);
				}

				if(null != loadCallback)
				{
					loadCallback.onLoadOver();
				}
			}

			@Override
			protected boolean hasError(TGHttpResult result)
			{
                int code = result.getResponseCode();
				return code < 200 || code >= 300 || TGHttpLoader.this.hasError(result);
			}
		};

		return resultHandler;
	}

	/**
	 * 取消任务
	 */
	public void cancel()
	{
		this.isCancel = true;
		TGTaskManager.getInstance().cancelTask(taskID, TaskType.TASK_TYPE_HTTP);
		if(null != loadCallback)
		{
			this.loadCallback.onLoadOver();
		}
	}

	/**
	 * 该方法的作用: 设置Headers请求参数(会清空原有参数)
	 * @date 2014年5月23日
	 * @param properties
	 */
	public TGHttpLoader<T> setProperties(Map<String, String> properties)
	{
		if(null == properties)
		{
			properties = new HashMap<String, String>();
		}

		if(this.properties != properties)
		{
			this.properties.clear();
		}

		if(null != properties)
		{
			this.properties.putAll(properties);
		}

		return this;
	}

	/**
	 * 添加Headers请求参数
	 * @param key
	 * @param value
	 */
	public TGHttpLoader<T> addProperty(String key, String value)
	{
        if(null == properties)
        {
            properties = new HashMap<String, String>();
        }

		this.properties.put(key, value);
		return this;
	}

	/**
	 * 设置字符串参数（会清空原有参数）
	 * @param params
	 */
	public TGHttpLoader<T> setRequestParams(Map<String, String> params)
	{
		if(this.stringParams != params)
		{
			this.stringParams.clear();
		}

		if(null != params)
		{
			this.stringParams.putAll(params);
		}
		return this;
	}

	/**
	 * 添加字符串参数
	 * @param key
	 * @param value
	 */
	public TGHttpLoader<T> addRequestParam(String key, String value)
	{
		if(null != key && null != value)
		{
			this.stringParams.put(key, value);
		}
		else
		{
			LOG.e("[Method:addRequestParam] IllegalArguments were found key == " + key + " ; value == " + value);
		}
		return this;
	}

	/**
	 * 添加int类型的请求参数
	 *
	 * @param key
	 * @param value
	 */
	public TGHttpLoader<T> addRequestParam(String key, int value)
	{
		this.addRequestParam(key, value + "");
		return this;
	}

	/**
	 * 添加long类型的请求参数
	 *
	 * @param key
	 * @param value
	 */
	public TGHttpLoader<T> addRequestParam(String key, long value)
	{
		this.addRequestParam(key, value + "");
		return this;
	}

	/**
	 * 添加double类型那个的请求参数
	 *
	 * @param key
	 * @param value
	 */
	public TGHttpLoader<T> addRequestParam(String key, double value)
	{
		this.addRequestParam(key, value + "");
		return this;
	}

	/**
	 * 添加文件参数
	 * @param key
	 * @param filePath
	 */
	public TGHttpLoader<T> addFileParam(String key, String filePath)
	{
		if(null != key && null != filePath)
		{
			if(null == fileParams)
			{
				fileParams = new HashMap<String, String>();
				params.setFileParams(fileParams);
			}

			fileParams.put(key, filePath);
		}
		else
		{
			LOG.e("[Method:addFileParam] IllegalArguments were found key == " + key + " ; filePath == " + filePath);
		}
		return this;
	}

	/**
	 * 解析请求结果（从已解析成对象的结果中解析出所需要的结果），默认直接返回原始结果
	 * @param originalResultObject  已解析成对象的原始结果
	 * @return 目标对象结果
	 */
	@SuppressWarnings("unchecked")
	protected T parseOriginalResult(Object originalResultObject)
	{
		return (T) originalResultObject;
	}

	/**
	 * 判断网络请求结果是否有异常
	 * @param httpResult
	 * @return
	 */
	protected boolean hasError(TGHttpResult httpResult)
	{
		return false;
	}

	/**
	 * 该方法的作用:
	 * 获取执行任务的类名
	 * @date 2014年8月22日
	 * @param requestType
	 * @return
	 */
	protected String getTaskClsName(int requestType)
	{
		if(TextUtils.isEmpty(taskClsName))
		{
			switch (requestType)
			{
				case HttpType.REQUEST_GET:
					taskClsName = TGGetTask.class.getName();

					break;
				case HttpType.REQUEST_POST:
					taskClsName = TGPostTask.class.getName();

					break;
				case HttpType.REQUEST_PUT:
					taskClsName = TGPutTask.class.getName();

					break;
				case HttpType.REQUEST_DELETE:
					taskClsName = TGDeleteTask.class.getName();

					break;
				default:
					taskClsName = "";

					break;
			}
		}

		return taskClsName;
	}

	/**
	 * 是否已取消
	 * @return
	 */
	public boolean isCancelled()
	{
		return isCancel;
	}

	public Context getContext()
	{
		return context;
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
					LOG.e("[Method:parseRequestResult] url : "  + this.requestUrl + "\n params : " +
							stringParams + "\n" + e.getMessage());
				}
			}
		}

		return null;
	}

	protected String getRequestUrl()
	{
		return requestUrl;
	}

	protected HashMap<String, String> getStringParams()
	{
		return stringParams;
	}

	/**
	 * 请求结果的回调
	 *
	 * @date 2014-6-10
	 */
	public interface OnLoadCallback<T>
	{
		/**
		 * 启动加载时回调
		 */
		void onLoadStart();

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
