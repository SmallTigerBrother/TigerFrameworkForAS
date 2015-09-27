package com.mn.tiger.push.data;

import java.io.Serializable;

/**
 * 推送新未读数量类
 */
public abstract class TGPushUnReadCount implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * 获取全部的未读数量
	 * @return
	 */
	public abstract int getAllUnReadCount();

	public abstract int getUnReadCount(int messageType);

	public abstract int setUnReadCount(int messageType, int count);
}
