package com.mn.tiger.notification;

import java.io.Serializable;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * 通知建造者类
 */
public class TGNotificationBuilder
{
	/**
	 * 广播启动Action
	 */
	public static final String NOTIFICATION_CLICK_ACTION = "com.mn.tiger.notification.click";
	
	/**
	 * 保存NotificationType的Extra键值
	 */
	public static final String NOTIFICATION_TYPE = "notification_type";

	private NotificationCompat.Builder builder;

	private Context context;

	/**
	 * 启动广播的Intent
	 */
	private Intent intent;

	/**
	 * 消息类型
	 */
	private int type;

	public TGNotificationBuilder(Context context)
	{
		this.context = context;
		// 统一设置图标
		builder = new NotificationCompat.Builder(context);
		builder.setAutoCancel(true);

		// 初始化intent
		intent.setAction(NOTIFICATION_CLICK_ACTION);
	}
	
	public void setClass(Class<?> cls)
	{
		intent.setClass(context, cls);
	}
	
	public void setSmallIcon(int resId)
	{
		builder.setSmallIcon(resId);
	}
	
	public void setAutoCancel(boolean autoCancel)
	{
		builder.setAutoCancel(autoCancel);
	}

	/**
	 * 设置通知id
	 * 
	 * @param type
	 */
	void setNotificationType(int type)
	{
		this.type = type;
	}

	/**
	 * 设置滚动文本
	 * 
	 * @param tickerText
	 */
	public void setTickerText(String tickerText)
	{
		this.builder.setTicker(tickerText);
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setContentTitle(String title)
	{
		this.builder.setContentTitle(title);
	}

	/**
	 * 设置内容文本
	 * 
	 * @param text
	 */
	public void setContentText(String text)
	{
		this.builder.setContentText(text);
	}

	/**
	 * 设置extra数据
	 * 
	 * @param key
	 * @param value
	 */
	public void putExtra(String key, int value)
	{
		intent.putExtra(key, value);
	}

	/**
	 * 设置extra数据
	 * 
	 * @param key
	 * @param value
	 */
	public void putExtra(String key, String value)
	{
		intent.putExtra(key, value);
	}

	/**
	 * 设置extra数据
	 * 
	 * @param key
	 * @param value
	 */
	public void putExtra(String key, Serializable value)
	{
		intent.putExtra(key, value);
	}

	/**
	 * 构造通知
	 * 
	 * @return
	 */
	final Notification build()
	{
		intent.putExtra(NOTIFICATION_TYPE, type);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pendingIntent);

		onBuildNotification(context, type);
		
		return builder.build();
	}
	
	protected void onBuildNotification(Context context, int type)
	{
		
	}
	
}

