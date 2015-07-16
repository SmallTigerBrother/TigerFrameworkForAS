package com.mn.tiger.authorize;

import android.app.Activity;
import android.content.Intent;

/**
 * 认证类
 */
public abstract class AbsAuthorizer
{
	/**
	 * 启动认证的Activity
	 */
	private Activity activity;
	
	/**
	 * 第三方认证的appID（独立认证系统由业务而定）
	 */
	private String appID;
	
	/**
	 * @param activity 启动认证的Activity
	 * @param appID 第三方认证的appID（独立认证系统由业务而定）
	 */
	public AbsAuthorizer(Activity activity, String appID)
	{
		this.activity = activity;
		this.appID = appID;
	}
	
	/**
	 * 启动认证（在主线程调用）
	 * @param callback 认证结果回调接口
	 */
	public abstract void authorize(IAuthorizeCallback callback);
	
	/**
	 * 认证注销
	 */
	public abstract void logout(ILogoutCallback callback);
	
	public abstract void register(String account, String password, 
			IRegisterCallback callback, Object... args);
	
	/**
	 * 获取启动认证的Activity
	 * @return
	 */
	protected Activity getActivity()
	{
		return activity;
	}
	
	/**
	 * 获取第三方认证的appID（独立认证系统由业务而定）
	 * @return
	 */
	protected String getAppID()
	{
		return appID;
	}
	
	/**
	 * 处理认证界面返回的结果
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	}
}
