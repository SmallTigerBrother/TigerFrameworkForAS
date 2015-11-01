package com.mn.tiger.authorize;

/**
 * 注册回调方法
 */
public interface IRegisterCallback
{
	/**
	 * 注册成功
	 */
	void onRegisterSuccess(TGAuthorizeResult authorizeResult);

	/**
	 * 注册出错
	 * @param code
	 * @param message
	 */
	void onRegisterError(int code, String message);
}
