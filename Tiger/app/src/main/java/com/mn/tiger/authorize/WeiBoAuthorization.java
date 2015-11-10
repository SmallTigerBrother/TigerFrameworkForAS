package com.mn.tiger.authorize;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.utils.LogUtil;

/**
 * 新浪微博认证类
 */
public class WeiBoAuthorization extends AbsAuthorization
{
	/**
	 * 登录认证地址
	 */
	private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	
	 /** 
	  * 注销地址（URL）
	  */
    private static final String REVOKE_OAUTH_URL = "https://api.weibo.com/oauth2/revokeoauth2";
	
    /**
     * 认证范围
     */
	public static final String SCOPE =
			"email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
	
	/**
	 * HTTP 参数 
	 */
	protected static final String KEY_ACCESS_TOKEN = "access_token";
	
	/**
	 * 认证结果accessToken
	 */
	public static Oauth2AccessToken accessToken;
	
	/**
	 * SSO认证工具类
	 */
	private SsoHandler ssoHandler;
	
	/**
	 * 微博认证回调接口
	 */
	private WeiboAuthListener authListener;
	
	/**
	 * 认证结果回调接口
	 */
	private IAuthorizeCallback authorizeCallback;
	
	public WeiBoAuthorization(String appID)
	{
		super(appID);

		authListener = new AuthorListener();
	}

	@Override
	public void authorize(Activity activity, IAuthorizeCallback callback)
	{
		AuthInfo authInfo = new AuthInfo(activity, getAppID(), REDIRECT_URL, SCOPE);
		ssoHandler = new SsoHandler(activity, authInfo);
		this.authorizeCallback = callback;
		ssoHandler.authorize(authListener);
	}
	
	@Override
	public void logout(Activity activity, final ILogoutCallback logoutCallback)
	{
		if (null == accessToken)
		{
			LogUtil.e("TGWeiBoLoginRequest", "Argument error!");
			return;
		}

		WeiboParameters parameters = new WeiboParameters(getAppID());
		parameters.put(KEY_ACCESS_TOKEN, accessToken.getToken());
		new AsyncWeiboRunner(activity).requestAsync(REVOKE_OAUTH_URL, parameters,
				"post", new RequestListener()
				{
					@Override
					public void onWeiboException(WeiboException exception)
					{
						logoutCallback.onLogoutError(-1, exception.getMessage(), "");
					}
					
					@Override
					public void onComplete(String arg0)
					{
						logoutCallback.onLogoutSuccess();
					}
				});
	}
	
	@Override
	public void register(Activity activity, String account, String password, IRegisterCallback callback,
			Object... args)
	{
		throw new UnsupportedOperationException("a weibo account can not be registered by this way");
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	}
	
	private class AuthorListener implements WeiboAuthListener
	{
		@Override
		public void onCancel()
		{
			if(null != authorizeCallback)
			{
				authorizeCallback.onAuthorizeCancel();
			}
		}

		@Override
		public void onComplete(Bundle response)
		{
			if(null != authorizeCallback)
			{
				Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(response);
				if(accessToken.isSessionValid())
				{
					WeiBoAuthorization.accessToken = accessToken;
					TGAuthorizeResult loginResult = new TGAuthorizeResult();
					loginResult.setUID(accessToken.getUid());
					loginResult.setAccessToken(accessToken.getToken());
					authorizeCallback.onAuthorizeSuccess(loginResult);
				}
				else
				{
					authorizeCallback.onAuthorizeError(Integer.valueOf(response.getString("code")), "授权失败！",
							"// 以下几种情况，您会收到 Code：\n" + "// 1. 当您未在平台上注册的应用程序的包名与签名时；\n" +
									"// 2. 当您注册的应用程序包名与签名不正确时；" + "// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。");
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException exception)
		{
			if(null != authorizeCallback)
			{
				authorizeCallback.onAuthorizeError(0, exception.getMessage(), exception.getMessage());
			}
		}
	}

}
