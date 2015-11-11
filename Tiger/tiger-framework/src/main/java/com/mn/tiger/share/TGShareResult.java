package com.mn.tiger.share;

/**
 * 分享结果基类
 */
public abstract class TGShareResult
{
	/**
	 * 分享类型（具体的业务类型，自行定义）
	 */
	private int shareType = -1;

	public int getShareType()
	{
		return shareType;
	}

	public void setShareType(int shareType)
	{
		this.shareType = shareType;
	}
	
	/**
	 * 是否分享成功，由子类实现
	 * @return
	 */
	public abstract boolean isSuccess();

	public abstract boolean isCanceled();
}
