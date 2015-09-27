package com.mn.tiger.share;

import com.mn.tiger.share.result.TGShareResult;

/**
 * 分享结果回调接口
 * @param <T> 分享结果
 */
public interface IShareResultHandler<T extends TGShareResult>
{
	/**
	 * 分享结果回调方法
	 * @param shareResult 分享结果
	 */
	void handleShareResult(T shareResult);
}
