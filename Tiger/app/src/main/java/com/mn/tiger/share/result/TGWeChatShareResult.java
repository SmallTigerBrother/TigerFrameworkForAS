package com.mn.tiger.share.result;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;

/**
 * 微信分享结果
 */
public class TGWeChatShareResult extends TGShareResult
{
	/**
	 * 错误码 —— 分享成功
	 */
	public static final int SUCCESS = BaseResp.ErrCode.ERR_OK;
	
	/**
	 * 错误码 —— 用户取消
	 */
	public static final int USER_CANCEL = BaseResp.ErrCode.ERR_USER_CANCEL;
	
	/**
	 * 错误码 —— 认证被拒绝
	 */
	public static final int AUTH_DENIEND = BaseResp.ErrCode.ERR_AUTH_DENIED;
	
	/**
	 * 错误码 —— 
	 */
	public static final int ERROR_COMM = BaseResp.ErrCode.ERR_COMM;
	
	/**
	 * 错误码 —— 发送失败
	 */
	public static final int SENT_FAILED = BaseResp.ErrCode.ERR_SENT_FAILED;
	
	/**
	 * 错误码 —— 不支持的消息
	 */
	public static final int UNSUPPORT = BaseResp.ErrCode.ERR_UNSUPPORT;
	
	/**
	 * 结果Code
	 */
	private int resultCode = SUCCESS;
	
	/**
	 * 分享信息标识
	 */
	private String transaction;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
	
	public TGWeChatShareResult(BaseReq req)
	{
		this.resultCode = req.getType();
		this.transaction = req.transaction;
	}
	
	public TGWeChatShareResult(BaseResp req)
	{
		this.resultCode = req.errCode;
		this.transaction = req.transaction;
		this.errorMsg = req.errStr;
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
	 * 获取分享信息标识
	 * @return
	 */
	public String getTransaction()
	{
		return transaction;
	}
	
	/**
	 * 获取错误信息
	 * @return
	 */
	public String getErrorMsg()
	{
		return errorMsg;
	}

	@Override
	public boolean isSuccess()
	{
		return resultCode == SUCCESS;
	}
	
	@Override
	public String toString()
	{
		return "transaction == " + transaction + " ; resultCode == " + resultCode + 
				" ; errorMsg == " + errorMsg + " ; shareType == " + getShareType();
	}
}
