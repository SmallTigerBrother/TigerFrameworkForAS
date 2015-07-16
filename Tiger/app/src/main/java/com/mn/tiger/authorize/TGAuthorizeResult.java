package com.mn.tiger.authorize;

/**
 * 认证结果
 */
public class TGAuthorizeResult
{
	/**
	 * 用户Id
	 */
	private String UID;

	/**
	 * 用户授权token
	 */
	private String accessToken;

	public String getUID()
	{
		return UID;
	}

	public void setUID(String UID)
	{
		this.UID = UID;
	}

	public String getAccessToken()
	{
		return accessToken;
	}

	public void setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
	}
}
