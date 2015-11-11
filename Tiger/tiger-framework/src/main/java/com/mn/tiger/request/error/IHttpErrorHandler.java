package com.mn.tiger.request.error;


import com.mn.tiger.request.receiver.TGHttpResult;

/**
 * 该类作用及功能说明
 * Http异常处理接口
 * @version V2.0
 * @see JDK1.6,android-8
 * @date 2014年2月10日
 */
public interface IHttpErrorHandler
{
	/**
	 * 该方法的作用:
	 * 处理服务返回的错误信息
	 * @date 2014年4月26日
	 * @param httpResult
	 * @return
	 */
	public boolean handleErrorInfo(TGHttpResult httpResult);
}
