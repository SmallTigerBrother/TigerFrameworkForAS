package com.mn.tiger.share;

/**
 * 分享结果回调接口
 * @param <R> 分享结果
 */
public interface IShareResultHandler<R extends TGShareResult>
{
	/**
	 * 分享成功
	 * @param shareResult
	 */
	void onShareSuccess(R shareResult);

	/**
	 * 分享取消
	 * @param shareResult
	 */
	void onShareCancel(R shareResult);

	/**
	 * 分享失败
	 * @param shareResult
	 */
	void onShareFailed(R shareResult);
}
