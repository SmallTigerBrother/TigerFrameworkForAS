package com.mn.tiger.thirdparty.qq;

import com.mn.tiger.log.Logger;
import com.mn.tiger.share.TGShareResult;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * QQ/QQZone分享结果类
 */
public class QQShareResult extends TGShareResult
{
	private static final Logger LOG = Logger.getLogger(QQShareResult.class);
	
	// 返回码参考文档，0表示成功
	// http://wiki.connect.qq.com/com-tencent-tauth-tencent-sharetoqq
	// http://wiki.connect.qq.com/%E5%85%AC%E5%85%B1%E8%BF%94%E5%9B%9E%E7%A0%81%E8%AF%B4%E6%98%8E
	public static final int SUCCESS = 0;
	
	/**
	 * 错误码 —— 网络异常
	 */
	public static final int NETWORK_ERROR = -30001;
	
	/**
	 * 错误码 —— 用户取消
	 */
	public static final int USER_CANCEL = -1;
	
	/**
	 * 错误码 —— 应用未认证
	 */
	public static final int UNAUTHORIZED = 100030;
	
	/**
	 * 错误码 —— Token过期
	 */
	public static final int ACCESS_TOKEN_EXPIRED = 100014;
	
	/**
	 * 错误码 —— Token无效
	 */
	public static final int ACCESS_TOKEN_INVALID= 100015;
	
	/**
	 * 错误码 —— 认证失败
	 */
	public static final int ACCESS_TOKEN_AUTHORIZE_FAILED= 100016;

	/**
	 * 结果Code
	 */
	private int resultCode = NETWORK_ERROR;
	
	/**
	 * 分享结果信息
	 */
	private JSONObject response;

	/**
	 * UI异常
	 */
	private UiError uiError;

	public QQShareResult(JSONObject response)
	{
		this.response = response;
		if (response != null)
		{
			try
			{
				resultCode = response.getInt("ret");
			}
			catch (JSONException e)
			{
				LOG.e(e);
			}
		}
	}
	
	public QQShareResult(UiError error)
	{
		this.uiError = error;
	}

	/**
	 * 获取结果码
	 * @return
	 */
	public int getResultCode()
	{
		return resultCode;
	}

	@Override
	public boolean isSuccess()
	{
		return resultCode == SUCCESS;
	}

	/**
	 * 获取结果信息
	 * @return
	 */
	public JSONObject getResponse()
	{
		return response;
	}

	/**
	 * 获取UI异常
	 * @return
	 */
	public UiError getUiError()
	{
		return uiError;
	}

	@Override
	public boolean isCanceled()
	{
		return resultCode == USER_CANCEL;
	}

	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		if(null != stringBuilder)
		{
			stringBuilder.append("response == " + response + " ; ");
		}
		
		if(null != uiError)
		{
			stringBuilder.append("UiError == " + uiError.errorMessage + " ; ");
		}
		
		stringBuilder.append("shareType == " + getShareType());
		
		return stringBuilder.toString();
	}
}
