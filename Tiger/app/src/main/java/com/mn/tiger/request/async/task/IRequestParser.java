package com.mn.tiger.request.async.task;

import com.mn.tiger.request.receiver.TGHttpResult;

/**
 * 请求结果解析接口
 * @author Tiger
 */
public interface IRequestParser
{
	/**
	 * 解析请求结果
	 * @param httpResult 请求结果
	 * @param resultClsName 请求结果类的类名
	 * @return
	 */
	Object parseRequestResult(TGHttpResult httpResult, String resultClsName);
}
