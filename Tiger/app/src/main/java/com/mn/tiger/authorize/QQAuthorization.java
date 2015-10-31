package com.mn.tiger.authorize;

import android.app.Activity;
import android.content.Intent;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * QQ登录认证类
 */
public class QQAuthorization extends AbsAuthorization
{
	/**
	 * 认证范围
	 */
	public static final String SCOPE_ALL = "all";
	
	/**
	 * 回调接口
	 */
	private IUiListener uiListener;
	
	/**
	 * QQ认证执行类
	 */
	private Tencent tencent;
	
	/**
	 * 认证结果回调接口
	 */
	private IAuthorizeCallback callback;
	
	public QQAuthorization(String appID)
	{
		super(appID);
		uiListener = new IUiListener()
		{
			@Override
			public void onError(UiError uiError)
			{
				callback.onError(uiError.errorCode, uiError.errorMessage, uiError.errorDetail);
			}
			
			@Override
			public void onComplete(Object reponse)
			{
				try
				{
					//读取认证结果
					TGAuthorizeResult loginResult = new TGAuthorizeResult();
					loginResult.setUID(((JSONObject)reponse).getString("openid"));
					loginResult.setAccessToken(((JSONObject)reponse).getString("access_token"));
					callback.onSuccess(loginResult);
				}
				catch (JSONException e)
				{
					//数据解析出错，登录失败
					callback.onError(0, "认证失败！", "认证失败！");
				}
			}
			
			@Override
			public void onCancel()
			{
				callback.onCancel();
			}
		};
	}
	
	@Override
	public void authorize(Activity activity, IAuthorizeCallback callback)
	{
		tencent = Tencent.createInstance(getAppID(), activity);
		this.callback = callback;
		tencent.login(activity, SCOPE_ALL, uiListener);
	}
	
	@Override
	public void logout(Activity activity, ILogoutCallback logoutCallback)
	{
		tencent.logout(activity);
		logoutCallback.onSuccess();
	}
	
	@Override
	public void register(Activity activity, String account, String password, IRegisterCallback callback,
			Object... args)
	{
		throw new UnsupportedOperationException("a qq account can not be registered in this way");
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		tencent.onActivityResult(requestCode, resultCode, data);
	}
}
