package com.mn.tiger.authorize;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.mn.tiger.core.cache.TGCache;

/**
 * 登录认证类
 */
public abstract class TGAuthorizer extends AbsAuthorizer
{
	/**
	 * 缓存用户信息的键值
	 */
	private static final String USER_INFO_CACHE_KEY = "Tiger_UserInfo";

	/**
	 * 用户名/密码为null的异常
	 */
	public static final int AUTHORIZE_ERROR_ACCOUNT_PASSWORD_NULL = -1;

	/**
	 * 用户密码加密错误的异常
	 */
	public static final int AUTHORIZE_ERROR_PASSWORD_ENCRYPT = -2;

	/**
	 * 未登录调用注销方法的异常
	 */
	public static final int LOGOUT_ERROR_NEVER_AUTHORIZED = -10;

	/**
	 * 验证码异常————手机号不正确
	 */
	public static final int CODE_ERROR_INVALID_MOBILE = -20;

	/**
	 * 验证码异常————验证码格式不正确
	 */
	public static final int CODE_ERROR_INVALID_CODE_FORMAT = -21;

	/**
	 * 手机号（用户名）
	 */
	protected String account;

	/**
	 * 密码
	 */
	protected String password;

	/**
	 * 缓存的用户信息
	 */
	private static Serializable userInfo;

	/**
	 * @param activity
	 * @param account 手机号（用户名）
	 * @param password 密码
	 */
	public TGAuthorizer(Activity activity,String account, String password)
	{
		super(activity, null);

		this.account = account;
		this.password = password;
	}

	@Override
	public void authorize(final IAuthorizeCallback callback)
	{
		//检查账号、密码
		if(TextUtils.isEmpty(account) || TextUtils.isEmpty(password))
		{
			if(null != callback)
			{
				callback.onError(AUTHORIZE_ERROR_ACCOUNT_PASSWORD_NULL, "", "");
			}

			return;
		}
		executeAuthorize(callback);
	}

	protected abstract void executeAuthorize(IAuthorizeCallback callback);

	@Override
	public void logout(ILogoutCallback callback)
	{
		//检查是否已登录
		if(!checkHasAuthorized(getActivity()))
		{
			if(null != callback)
			{
				callback.onError(LOGOUT_ERROR_NEVER_AUTHORIZED, "", "");
			}
			return;
		}

		executeLogout(callback);

		//将缓存数据置为null
		saveUserInfo(getActivity(), null);
	}

	protected abstract void executeLogout(ILogoutCallback callback);

	@Override
	public void register(String account, String password, IRegisterCallback callback,
						 Object... args)
	{

	}

	/**
	 * 判断是否已登录
	 * @param context
	 * @return
	 */
	protected boolean checkHasAuthorized(Context context)
	{
		return null != getUserInfo(context);
	}

	/**
	 * 获取用户信息
	 * @param context
	 * @return
	 */
	public static Serializable getUserInfo(Context context)
	{
		if(null == userInfo)
		{
			userInfo = (Serializable) TGCache.getCache(context, USER_INFO_CACHE_KEY);
		}

		return userInfo;
	}

	/**
	 * 保存用户信息
	 * @param userInfo
	 */
	public static void saveUserInfo(Context context, Serializable userInfo)
	{
		TGAuthorizer.userInfo = userInfo;
		if(null != userInfo)
		{
			TGCache.saveCache(context, USER_INFO_CACHE_KEY, userInfo);
		}
		else
		{
			TGCache.removeCache(context, USER_INFO_CACHE_KEY);
		}
	}
}
