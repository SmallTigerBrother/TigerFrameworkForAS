package com.mn.tiger.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.jpush.android.api.JPushInterface;

/**
 * 极光推送消息接收类
 */
public abstract class JPushMessageReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		Bundle extras = intent.getExtras();
		String title = extras.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE, "");
		String alert = extras.getString(JPushInterface.EXTRA_ALERT,"");
		String pushMsgExtra = extras.getString(JPushInterface.EXTRA_EXTRA, "");

		//根据当前接收的事件类型，调用不同的处理方法
		if(JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action))
		{
			onPushMessageReceived(context, title, alert, pushMsgExtra);
		}
		else if(JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action))
		{
			onPushNotificationOpened(context, title, alert, pushMsgExtra);
		}
		else if(JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action))
		{
			onPushNotificationReceived(context, title, alert, pushMsgExtra);
		}
	}

	/**
	 * 自定义推送消息接收回调方法
	 * @param context
	 * @param title
	 * @param alert
	 * @param extra
	 */
	protected abstract void onPushMessageReceived(Context context, String title, String alert, String extra);

	/**
	 * JPush通知点击时的回调方法
	 * @param context
	 * @param title
	 * @param alert
	 * @param extra
	 */
	protected abstract void onPushNotificationOpened(Context context, String title, String alert, String extra);

	/**
	 * JPush通知收到时的回调方法
	 * @param context
	 * @param title
	 * @param alert
	 * @param extra
	 */
	protected abstract void onPushNotificationReceived(Context context, String title, String alert, String extra);

}
