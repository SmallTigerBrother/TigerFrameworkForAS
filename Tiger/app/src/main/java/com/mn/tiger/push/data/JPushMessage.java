package com.mn.tiger.push.data;

import java.io.Serializable;

import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.google.gson.Gson;

/**
 * 极光推送消息类
 */
public class JPushMessage implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 消息id
	 */
	private int id;
	
	/**
	 * 消息时间
	 */
	private long time;
	
	/**
	 * 消息类型
	 */
	private int type;
	
	/**
	 * 消息内容
	 */
	private String content;
	
	public JPushMessage()
	{
		
	}
	
	public int getId()
	{
		return id;
	}

	public int getType()
	{
		return type;
	}

	public String getContent()
	{
		return content;
	}
	
	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	public static JPushMessage newInstance(Bundle originalData)
	{
		return new Gson().fromJson(originalData.getString(JPushInterface.EXTRA_MESSAGE), 
				JPushMessage.class);
	}
}
