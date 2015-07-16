package com.mn.tiger.push;

import android.content.Context;

import com.mn.tiger.cache.TGCache;
import com.mn.tiger.datastorage.TGDBManager;
import com.mn.tiger.datastorage.db.exception.DbException;
import com.mn.tiger.datastorage.db.sqlite.Selector;
import com.mn.tiger.datastorage.db.sqlite.WhereBuilder;
import com.mn.tiger.log.Logger;
import com.mn.tiger.push.data.JPushMessage;
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
	public void savePushMessage(Context context, JPushMessage pushMessage)
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
	 * @param deltaCount
	 * @param messageType
	 */
	public synchronized void saveUnReadCount(Context context, int count, int messageType)
	{
		onSaveUnReadCount(context, getCachedPushUnReadCount(context), count, messageType);
	}
	
	protected abstract void onSaveUnReadCount(Context context,TGPushUnReadCount unReadCount, int count, int messageType);
	
	/**
	 * 保存未读消息数
	 * @param context
	 * @param deltaCount
	 * @param messageType
	 */
	public synchronized void saveAllUnReadCount(Context context, TGPushUnReadCount unReadCount)
	{
		pushUnReadCount = unReadCount;
		TGCache.saveCache(context, UNREAD_COUNT_CACHE_KEY, unReadCount);
	}
	
	/**
	 * 获取最后一个消息
	 * @param context
	 * @param messageType 消息类型
	 * @return
	 */
	public JPushMessage getLastMessage(Context context, int messageType)
	{
		try
		{
			return getPushMsgDBManager(context).findFirst(Selector
					.from(JPushMessage.class)
					.where(WhereBuilder.b("type", "=", messageType))
					.orderBy("time", true));
		}
		catch (DbException e)
		{
			LOG.e(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * 获取未读消息数
	 * @param context
	 * @param messageType 消息类型
	 * @return
	 */
	public int getCachedUnReadCount(Context context, int messageType)
	{
		return getCachedUnReadCount(context, getCachedPushUnReadCount(context), messageType);
	}
	
	public abstract int getCachedUnReadCount(Context context,TGPushUnReadCount unReadCount ,int messageType);
	
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
		return null != pushUnReadCount ? pushUnReadCount.getAllUnReadCount() : 0;
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
