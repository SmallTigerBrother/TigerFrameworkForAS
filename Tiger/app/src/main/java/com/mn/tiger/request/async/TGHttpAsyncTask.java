package com.mn.tiger.request.async;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.mn.tiger.app.TGActionBarActivity;
import com.mn.tiger.log.LogTools;
import com.mn.tiger.request.HttpType;
import com.mn.tiger.request.TGHttpLoader.OnLoadCallback;
import com.mn.tiger.request.async.task.TGDeleteTask;
import com.mn.tiger.request.async.task.TGGetTask;
import com.mn.tiger.request.async.task.TGHttpTask;
import com.mn.tiger.request.async.task.TGPostTask;
import com.mn.tiger.request.async.task.TGPutTask;
import com.mn.tiger.request.error.TGHttpErrorHandler;
import com.mn.tiger.request.method.TGHttpParams;
import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.task.TGTaskManager;
import com.mn.tiger.task.invoke.TGTaskParams;

/**
 * 该类作用及功能说明
 * tiger框架异步请求基类
 * @version V2.0
 * @see JDK1.6,android-8
 * @date 2014年2月10日
 */
public class TGHttpAsyncTask<T>
{
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();
	
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
	private HashMap<String, String> StringParams = null;
	
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
	
	/**
	 * @param context
	 * @param requestUrl
	 * @param requestType
	 * @param callback
	 */
	public TGHttpAsyncTask(String requestUrl, int requestType, 
			OnLoadCallback<T> callback) 
	{
		this.requestUrl = requestUrl;
		this.requestType = requestType;
		this.loadCallback = callback;
		
		params = new TGHttpParams();
		StringParams = new HashMap<String, String>();
		properties = new HashMap<String, String>();
		
		params.setStringParams(StringParams);
	}
	
	/**
	 * 该方法的作用:
	 * 执行任务
	 * @date 2014年8月22日
	 */
	public int execute()
	{
		if (null == context || (context instanceof Activity && ((Activity) context).isFinishing()))
		{
			return taskID;
		}
		
		if(TextUtils.isEmpty(requestUrl))
		{
			LogTools.e(LOG_TAG, "[Method:execute] the requestUrl is null, please check your code");
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
		LogTools.p(LOG_TAG, "[Method: doInBackground]  " + "start request.");
		
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
		data.putSerializable(TGHttpTask.PARAM_PROPERTIES, properties);
		if(null != params)
		{
			data.putSerializable(TGHttpTask.PARAM_PARAMS, params);
		}
		
		data.putString(TGHttpTask.PARAM_PARSERCLSNAME, parserClsName);
		data.putString(TGHttpTask.PARAM_RESLUTCLSNAME, resultClsName);
		
		TGTaskParams taskParams = TGTaskManager.createTaskParams(data, 
				getTaskClsName(requestType), initHttpResultHandler());
		taskParams.setTaskType(TGTask.TASK_TYPE_HTTP);
		
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
				LogTools.i(LOG_TAG, "[Method:onSuccess]");
				//解析请求结果
				if(!isCancelled() && null != loadCallback)
				{
					loadCallback.onLoadSuccess(parseResult(httpResult.getObjectResult()),
							httpResult);
				}
			}
			
			@Override
			protected void onError(TGHttpResult httpResult) 
			{
				LogTools.i(LOG_TAG, "[Method:onError]");
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
				LogTools.i(LOG_TAG, "[Method:onReturnCachedResult]");
				//解析请求结果
				if(!isCancelled() && null != loadCallback)
				{
					loadCallback.onLoadCache(parseResult(httpResult.getObjectResult()), 
							httpResult);
				}
			}
			
			@Override
			protected void onRequestOver() 
			{
				if(null != loadCallback)
				{
					loadCallback.onLoadOver();
				}
			}
			
			@Override
			protected boolean hasError(TGHttpResult result)
			{
				return TGHttpAsyncTask.this.hasError(result);
			}
		};
		
		return resultHandler;
	}
	
	/**
	 * 判断网络请求结果是否有异常
	 * @param httpResult
	 * @return
	 */
	protected boolean hasError(TGHttpResult httpResult)
	{
		return TGHttpErrorHandler.hasHttpError(httpResult);
	}
	
	/**
	 * 解析请求结果（从已解析成对象的结果中解析出所需要的结果），默认直接返回原始结果
	 * @param originalResultObject  已解析成对象的原始结果
	 * @return 目标对象结果
	 */
	@SuppressWarnings("unchecked")
	protected T parseResult(Object originalResultObject)
	{
		return (T) originalResultObject;
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
	 * 该方法的作用:
	 * 设置执行任务的类名
	 * @date 2014年8月22日
	 * @param taskClsName
	 */
	public void setTaskClsName(String taskClsName)
	{
		this.taskClsName = taskClsName;
	}
	
	/**
	 * 该方法的作用:
	 * 取消任务
	 * @date 2014年8月22日
	 */
	public void cancel()
	{
		this.isCancel = true;
		TGTaskManager.getInstance().cancelTask(taskID, TGTask.TASK_TYPE_HTTP);
		if(null != loadCallback)
		{
			this.loadCallback.onLoadOver();
		}
	}
	
	/**
	 * 该方法的作用:
	 * 打印进度
	 * @date 2014年8月22日
	 * @param value
	 */
	public void publishProgress(Integer value)
	{
		onProgressUpdate(value);
	}
	
	/**
	 * 该方法的作用:
	 * 更新进度
	 * @date 2014年8月22日
	 * @param value
	 */
	protected void onProgressUpdate(Integer value) 
	{
	}

	/**
	 * 是否已取消
	 * @return
	 */
	public boolean isCancelled()
	{
		return isCancel;
	}
	
	public void setContext(Context context)
	{
		this.context = context;
	}
	
	public Context getContext()
	{
		return context;
	}
	
	/**
	 * 该方法的作用: 设置Headers请求参数(会清空原有参数)
	 * @date 2014年5月23日
	 * @param properties
	 */
	public void setProperties(Map<String, String> properties)
	{
		this.properties.clear();
		this.properties.putAll(properties);
	}
	
	/**
	 * 添加Headers请求参数
	 * @param key
	 * @param value
	 */
	public void addProperty(String key, String value)
	{
		this.properties.put(key, value);
	}
	
	/**
	 * 设置字符串参数（会清空原有参数）
	 * @param params
	 */
	public void setRequestParams(Map<String, String> params)
	{
		this.params.clear();
		this.StringParams.putAll(params);
	}
	
	/**
	 * 添加字符串参数
	 * @param key
	 * @param value
	 */
	public void addRequestParam(String key, String value)
	{
		this.StringParams.put(key, value);
	}
	
	/**
	 * 添加文件参数
	 * @param key
	 * @param filePath
	 */
	public void addFileParam(String key, String filePath)
	{
		if(null == fileParams)
		{
			fileParams = new HashMap<String, String>();
			params.put("file_param", fileParams);
		}
		
		fileParams.put(key, filePath);
	}
	
	/**
	 * 设置解析类的类名
	 * @param parserClsName
	 */
	public void setParserClsName(String parserClsName)
	{
		this.parserClsName = parserClsName;
	}
	
	/**
	 * 设置结果类的类名
	 * @param resultClsName
	 */
	public void setResultClsName(String resultClsName)
	{
		this.resultClsName = resultClsName;
	}
	
	/**
	 * 获取请求结果类名
	 * @return
	 */
	protected String getResultClsName()
	{
		return resultClsName;
	}
	
	/**
	 * 设置请求类型
	 * @param requestType
	 */
	public void setRequestType(int requestType)
	{
		this.requestType = requestType;
	}
	
	/**
	 * 设置请求Url
	 * @param requestUrl
	 */
	public void setRequestUrl(String requestUrl)
	{
		this.requestUrl = requestUrl;
	}
	
	public String getRequestUrl()
	{
		return requestUrl;
	}
	
	public HashMap<String, String> getStringParams()
	{
		return StringParams;
	}
	
	/**
	 * 设置请求回调类
	 * @param callback
	 */
	public void setLoadCallback(OnLoadCallback<T> callback)
	{
		this.loadCallback = callback;
	}
}
