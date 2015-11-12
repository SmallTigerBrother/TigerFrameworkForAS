package com.mn.tiger.share;

/**
 * 分享结果回调接口
 * @param <T> 分享结果
 */
public interface IShareResultHandler<T extends TGShareResult>
{
	/**
	 * 分享成功
	 * @param shareResult
	 */
	void onShareSuccess(T shareResult);

	/**
	 * 分享取消
	 * @param shareResult
	 */
	void onShareCancel(T shareResult);

	/**
	 * 分享失败
	 * @param shareResult
	 */
	void onShareFailed(T shareResult);
}
