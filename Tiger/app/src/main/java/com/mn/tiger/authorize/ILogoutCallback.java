package com.mn.tiger.authorize;

public interface ILogoutCallback
{
	/**
	 * 注销成功
	 */
	void onSuccess();
	
	/**
	 * 注销出错
	 * @param code
	 * @param message
	 * @param detail
	 */
	void onError(int code, String message, String detail);
	
	/**
	 * 注销取消
	 */
	void onCancel();
}
