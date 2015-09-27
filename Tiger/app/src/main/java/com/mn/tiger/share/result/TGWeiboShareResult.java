package com.mn.tiger.share.result;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.constant.WBConstants;

/**
 * 微博分享结果
 */
public class TGWeiboShareResult extends TGShareResult
{
	/**
	 * 错误码 —— 分享成功
	 */
	public static final int SUCCESS = WBConstants.ErrorCode.ERR_OK;
	
	/**
	 * 错误码 —— 用户取消
	 */
	public static final int USER_CANCEL = WBConstants.ErrorCode.ERR_CANCEL;
	
	/**
	 * 错误码 —— 分享失败
	 */
	public static final int SENT_FAILED = WBConstants.ErrorCode.ERR_FAIL;
	
	/**
	 * 分享信息标识
	 */
	private String transaction = "";
	
	/**
	 * 结果Code
	 */
	private int resultCode = SUCCESS;
	
	/**
	 * 请求分享的应用的包名
	 */
	private String reqPackageName = "";
	
	/**
	 * 错误信息
	 */
	private String errorMsg = "";
	
	public TGWeiboShareResult(BaseResponse response)
	{
		this.transaction = response.transaction;
		this.resultCode = response.errCode;
		this.reqPackageName = response.reqPackageName;
		this.errorMsg = response.errMsg;
	}
	
	@Override
	public boolean isSuccess()
	{
		return resultCode == SUCCESS;
	}
	
	/**
	 * 获取错误信息
	 * @return
	 */
	public String getErrorMsg()
	{
		return errorMsg;
	}
	
	/**
	 * 获取结果Code
	 * @return
	 */
	public int getResultCode()
	{
		return resultCode;
	}
	
	/**
	 * 获取请求分享的应用的包名
	 * @return
	 */
	public String getReqPackageName()
	{
		return reqPackageName;
	}
	
	/**
	 * 获取分享信息标识
	 * @return
	 */
	public String getTransaction()
	{
		return transaction;
	}
	
	@Override
	public String toString()
	{
		return "transaction == " + transaction + " ; resultCode == " + resultCode + 
				" ; reqPackageName" + reqPackageName + " ; errorMsg == " + errorMsg +
				" ; shareType == " + getShareType();
	}
}
