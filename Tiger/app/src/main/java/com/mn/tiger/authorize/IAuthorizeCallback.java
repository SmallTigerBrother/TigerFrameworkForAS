package com.mn.tiger.authorize;

/**
 * 认证结果回调接口
 */
public interface IAuthorizeCallback
{
	/**
	 * 认证成功
	 * @param authorizeResult
	 */
	void onSuccess(TGAuthorizeResult authorizeResult);

	/**
	 * 认证出错
	 * @param code
	 * @param message
	 * @param detail
	 */
	void onError(int code, String message, String detail);

	/**
	 * 认证取消
	 */
	void onCancel();
}
