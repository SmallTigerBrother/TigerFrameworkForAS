package com.mn.tiger.request.receiver;

import android.app.Activity;
import android.content.Context;

import com.mn.tiger.task.result.TGTaskResult;
import com.mn.tiger.task.result.TGTaskResultHandler;

/**
 * 该类作用及功能说明
 * Http结果接收类
 * @date 2014年3月18日
 */
public abstract class TGHttpResultHandler extends TGTaskResultHandler
{
	private Context context;
	
	public TGHttpResultHandler(Context context)
	{
		this.context = context;
	}
	
	@Override
	public void handleTaskResult(TGTaskResult result)
	{
		if(null != context)
		{
			if(context instanceof Activity && (context instanceof Activity && 
					((Activity)context).isFinishing()))
			{
				return;
			}
			
			TGHttpResult httpResult = (TGHttpResult)result.getResult();

			if(hasError(httpResult))
			{
				onError(httpResult);
			}
			else
			{
				onSuccess(httpResult);
			}
			
			onRequestOver();
		}
		
	}
	
	/**
	 * 该方法的作用:
	 * 处理Http结果
	 * @date 2014年3月18日
	 * @param httpResult
	 */
	protected abstract void onSuccess(TGHttpResult httpResult);
	
	/**
	 * 出现移除时的回调方法
	 * @param httpResult
	 */
	protected abstract void onError(TGHttpResult httpResult);
	
	/**
	 * 返回缓存结果时的处理方法
	 * @param httpResult
	 */
	protected void onReturnCachedResult(TGHttpResult httpResult)
	{
		
	}
	
	/**
	 * 请求结束后的回调方法（不论请求成功与否都会调用）
	 */
	protected void onRequestOver()
	{
		
	}
	
	/**
	 * 该方法的作用:
	 * 处理Http异常
	 * @date 2014年3月18日
	 * @param result
	 * @return
	 */
	protected boolean hasError(TGHttpResult result)
	{
		return false;
	}
}
