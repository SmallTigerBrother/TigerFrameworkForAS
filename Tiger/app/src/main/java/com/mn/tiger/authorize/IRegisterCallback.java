package com.mn.tiger.authorize;

/**
 * 注册回调方法
 */
public interface IRegisterCallback
{
	/**
	 * 注册成功
	 */
	void onSuccess();
	
	/**
	 * 注册出错
	 * @param code
	 * @param message
	 */
	void onError(int code, String message);
}
