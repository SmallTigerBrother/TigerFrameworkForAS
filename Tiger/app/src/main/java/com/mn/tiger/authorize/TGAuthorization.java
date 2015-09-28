package com.mn.tiger.authorize;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.mn.tiger.core.cache.TGCache;
import com.mn.tiger.utility.Preferences;

import java.io.Serializable;

/**
 * 登录认证类
 */
public abstract class TGAuthorization extends AbsAuthorization
{
	/**
	 * 缓存用户信息的键值
	 */
	private static final String USER_INFO_CACHE_KEY = "Tiger_UserInfo";

	public static final String AUTHORIZER_DATA = "authorizer_data";

	private static final String ACCESS_TOKEN_KEY = "access_token";

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

	public TGAuthorization()
	{
		super(null);
	}

	public void setAccount(String account)
	{
		this.account = account;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	@Override
	public void authorize(Activity activity, final IAuthorizeCallback callback)
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
		executeAuthorize(activity, callback);
	}

	protected abstract void executeAuthorize(Activity activity, IAuthorizeCallback callback);

	@Override
	public void logout(Activity activity, ILogoutCallback callback)
	{
		//检查是否已登录
		if(!checkHasAuthorized(activity))
		{
			if(null != callback)
			{
				callback.onError(LOGOUT_ERROR_NEVER_AUTHORIZED, "", "");
			}
			return;
		}

		executeLogout(activity, callback);

		//将缓存数据置为null
		saveUserInfo(activity, null);
	}

	protected abstract void executeLogout(Activity activity, ILogoutCallback callback);

	@Override
	public void register(Activity activity, String account, String password, IRegisterCallback callback,
						 Object... args)
	{

	}

	/**
	 * 判断是否已登录
	 * @param context
	 * @return
	 */
	public boolean checkHasAuthorized(Context context)
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
		TGAuthorization.userInfo = userInfo;
		if(null != userInfo)
		{
			TGCache.saveCache(context, USER_INFO_CACHE_KEY, userInfo);
		}
		else
		{
			TGCache.removeCache(context, USER_INFO_CACHE_KEY);
		}
	}

	/**
	 * 保存AccessToken
	 */
	public static void saveAccessToken(Context context, String token)
	{
		if (!TextUtils.isEmpty(token))
		{
			Preferences.save(context, AUTHORIZER_DATA, ACCESS_TOKEN_KEY, token);
		}
		else
		{
			Preferences.save(context, AUTHORIZER_DATA, ACCESS_TOKEN_KEY, "");
		}
	}

	/**
	 * 获取AccessToken
	 */
	public static String getAccessToken(Context context)
	{
		String token = Preferences.read(context, AUTHORIZER_DATA, ACCESS_TOKEN_KEY, "");
		if (!TextUtils.isEmpty(token))
		{
			return token;
		}
		return "";
	}

}
