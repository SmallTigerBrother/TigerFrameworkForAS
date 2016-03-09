package com.mn.tiger.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mn.tiger.log.Logger;

import cn.jpush.android.api.JPushInterface;

/**
 * 极光推送消息接收类
 */
public abstract class JPushMessageReceiver extends BroadcastReceiver
{
	private static final Logger LOG = Logger.getLogger(JPushMessageReceiver.class);

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();
		Bundle extras = intent.getExtras();
		String title = extras.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE, "");
		String alert = extras.getString(JPushInterface.EXTRA_ALERT, "");
		String message = extras.getString(JPushInterface.EXTRA_MESSAGE,"");
		String pushMsgExtra = extras.getString(JPushInterface.EXTRA_EXTRA, "");
		String registrationId = extras.getString(JPushInterface.EXTRA_REGISTRATION_ID, "");

		//根据当前接收的事件类型，调用不同的处理方法
		if(JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action))
		{
			LOG.d("[Method:onReceive] ACTION_MESSAGE_RECEIVED");
			onPushMessageReceived(context, intent, title, alert, message, pushMsgExtra);
		}
		else if(JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action))
		{
			LOG.d("[Method:onReceive] ACTION_NOTIFICATION_OPENED");
			onPushNotificationOpened(context, intent, title, alert, message, pushMsgExtra);
		}
		else if(JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action))
		{
			LOG.d("[Method:onReceive] ACTION_NOTIFICATION_RECEIVED");
			onPushNotificationReceived(context, intent, title, alert, message, pushMsgExtra);
		}
		else if(JPushInterface.ACTION_REGISTRATION_ID.equals(action))
		{
			LOG.d("[Method:onReceive] ACTION_REGISTRATION_ID registrationId == " + registrationId);
		}
		else
		{
			LOG.d("[Method:onReceive] ");
		}
	}

	/**
	 * 自定义推送消息接收回调方法
	 * @param context
	 * @param title
	 * @param alert
	 * @param extra
	 */
	protected abstract void onPushMessageReceived(Context context, Intent intent, String title, String alert, String message, String extra);

	/**
	 * JPush通知点击时的回调方法
	 * @param context
	 * @param title
	 * @param alert
	 * @param extra
	 */
	protected abstract void onPushNotificationOpened(Context context, Intent intent, String title, String alert, String message, String extra);

	/**
	 * JPush通知收到时的回调方法
	 * @param context
	 * @param title
	 * @param alert
	 * @param extra
	 */
	protected abstract void onPushNotificationReceived(Context context, Intent intent, String title, String alert, String message, String extra);

}
