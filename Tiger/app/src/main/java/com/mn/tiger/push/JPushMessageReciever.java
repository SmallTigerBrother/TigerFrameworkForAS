package com.mn.tiger.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.mn.tiger.log.Logger;
import com.mn.tiger.push.data.JPushMessage;

/**
 * 极光推送消息接收类
 */
public abstract class JPushMessageReciever extends BroadcastReceiver 
{
	private static final Logger LOG = Logger.getLogger(JPushMessageReciever.class);
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		LOG.d("[onReceive] new push message");
		
		String action = intent.getAction();
		//若为消息，发出通知
		if(JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action))
		{
			Bundle extras = intent.getExtras();
			if(null != extras)
			{
				JPushMessage pushMessage = JPushMessage.newInstance(extras);
				pushMessage.setTime(System.currentTimeMillis());
				onPushMessageRecieved(context, pushMessage);
			}
		}
	}
	
	protected abstract void onPushMessageRecieved(Context context, JPushMessage pushMessage);
}
