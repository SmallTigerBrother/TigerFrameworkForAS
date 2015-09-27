package com.mn.tiger.push;

import android.content.Context;

import com.mn.tiger.core.cache.TGCache;
import com.mn.tiger.datastorage.TGDBManager;
import com.mn.tiger.datastorage.db.exception.DbException;
import com.mn.tiger.log.Logger;
import com.mn.tiger.push.data.TGPushUnReadCount;

/**
 * 推送消息管理器
 */
public abstract class JPushMessageManager
{
	public static final String PUSH_MESSAGE_DB_NAME = "tiger_jpush_messages";
	
	/**
	 * 未读消息个数缓存key
	 */
	private static final String UNREAD_COUNT_CACHE_KEY = "push_msg_unread_count_map";
	
	private static final Logger LOG = Logger.getLogger(JPushMessageManager.class);
	
	/**
	 * 未读消息个数
	 */
	private static TGPushUnReadCount pushUnReadCount;

	/**
	 * 未读消息数量类
	 */
	private Class<?> pushUnReadCountClazz;
	
	/**
	 * 获取本地缓存的消息数据库
	 * @param context
	 * @return
	 */
	private static TGDBManager getPushMsgDBManager(Context context)
	{
		return TGDBManager.create(context, PUSH_MESSAGE_DB_NAME, 1, null);
	}
	
	protected JPushMessageManager()
	{
	}
	
	/**
	 * 设置未读消息数类
	 * @param pushUnReadCountClazz
	 */
	public void setPushUnReadCountClazz(Class<?> pushUnReadCountClazz)
	{
		this.pushUnReadCountClazz = pushUnReadCountClazz;
	}
	
	/**
	 * 保存推送消息
	 * @param context
	 * @param pushMessage
	 */
	public void savePushMessage(Context context, Object pushMessage)
	{
		try
		{
			getPushMsgDBManager(context).saveOrUpdate(pushMessage);
		}
		catch (DbException e)
		{
			LOG.e(e.getMessage(), e);
		}
	}

	/**
	 * 保存未读消息数
	 * @param context
	 * @param count
	 * @param messageType
	 */
	public synchronized void saveUnReadCount(Context context, int count, int messageType)
	{
		TGPushUnReadCount pushUnReadCount = getCachedPushUnReadCount(context);
		pushUnReadCount.setUnReadCount(messageType, count);
		saveAllUnReadCount(context, pushUnReadCount);
	}

	/**
	 * 保存未读消息数
	 * @param context
	 * @param unReadCount
	 */
	public synchronized void saveAllUnReadCount(Context context, TGPushUnReadCount unReadCount)
	{
		pushUnReadCount = unReadCount;
		TGCache.saveCache(context, UNREAD_COUNT_CACHE_KEY, unReadCount);
	}
	
	/**
	 * 获取未读消息数
	 * @param context
	 * @param messageType 消息类型
	 * @return
	 */
	public int getCachedUnReadCount(Context context, int messageType)
	{
		TGPushUnReadCount pushUnReadCount = getCachedPushUnReadCount(context);
		return pushUnReadCount.getUnReadCount(messageType);
	}

	/**
	 * 获取所有未读消息个数
	 * @return
	 */
	private TGPushUnReadCount getCachedPushUnReadCount(Context context)
	{
		if(null == pushUnReadCount)
		{
			pushUnReadCount = (TGPushUnReadCount) TGCache.getCache(context, UNREAD_COUNT_CACHE_KEY);
			if(null == pushUnReadCount && null != pushUnReadCountClazz)
			{
				try
				{
					pushUnReadCount = (TGPushUnReadCount) pushUnReadCountClazz.newInstance();
				}
				catch (Exception e)
				{
					LOG.e(e.getMessage());
				}
			}
		}
		
		return pushUnReadCount;
	}
	
	/**
	 * 获取所有未读消息数
	 * @param context
	 * @return
	 */
	public int getAllUnreadCount(Context context)
	{
		TGPushUnReadCount pushUnReadCount = getCachedPushUnReadCount(context);
		return pushUnReadCount.getAllUnReadCount();
	}
	
	/**
	 * 打开消息推送的开关
	 * @param context
	 */
	public abstract void turnOnPushSwitch(final Context context);
	
	/**
	 * 关闭消息推送的开关
	 * @param context
	 */
	public abstract void turnOffPushSwitch(final Context context);
}
