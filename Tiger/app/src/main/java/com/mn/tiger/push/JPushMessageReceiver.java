package com.mn.tiger.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mn.tiger.log.Logger;
import com.mn.tiger.push.data.JPushMessage;

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

		//若为消息，发出通知
		if(JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action))
		{
			if(null != extras)
			{
				JPushMessage pushMessage = JPushMessage.newInstance(extras);
				pushMessage.setTime(System.currentTimeMillis());
				onPushMessageReceived(context, pushMessage);
			}
		}
		else if(JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action))
		{
			onPushNotificationOpened(context, extras.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE,""),
					extras.getString(JPushInterface.EXTRA_ALERT,""),extras.getString(JPushInterface.EXTRA_EXTRA));
		}
		else if(JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action))
		{
			onPushNotificationReceived(context, extras.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE,""),
					extras.getString(JPushInterface.EXTRA_ALERT,""),extras.getString(JPushInterface.EXTRA_EXTRA));
		}
	}

	protected abstract void onPushMessageReceived(Context context, JPushMessage pushMessage);

	protected abstract void onPushNotificationOpened(Context context, String title, String alert, String extra);

	protected abstract void onPushNotificationReceived(Context context, String title, String alert, String extra);

}
