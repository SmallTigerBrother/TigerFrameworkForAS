package com.mn.tiger.share;

import com.mn.tiger.log.Logger;
import com.mn.tiger.share.result.TGShareResult;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 分享插件管理器
 */
public class TGSharePluginManager
{
	private static final Logger LOG = Logger.getLogger(WeChatEntryActivity.class);
	
	/**
	 * TAG —— 微信分享
	 */
	public static final int TAG_WEI_CHAT = 1000;
	
	/**
	 * TAG —— 微信朋友圈分享
	 */
	public static final int TAG_WEI_CHAT_TIME_LINE = 1001;
	
	/**
	 * TAG —— QQ分享
	 */
	public static final int TAG_QQ = 1002;
	
	/**
	 * TAG —— QQ空间分享
	 */
	public static final int TAG_QQ_ZONE = 1003;
	
	/**
	 * TAG —— 微博分享
	 */
	public static final int TAG_WEI_BO = 1004;
	
	/**
	 * 插件map
	 */
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<Integer, TGSharePlugin> pushPluginMap;
	
	/**
	 * 插件管理器单例
	 */
	private static TGSharePluginManager sharePluginManager;
	
	/**
	 * 获取单例对象
	 * @return
	 */
	public static synchronized TGSharePluginManager getInstance()
	{
		if(null == sharePluginManager)
		{
			sharePluginManager = new TGSharePluginManager();
		}
		
		return sharePluginManager;
	}
	
	@SuppressWarnings("rawtypes")
	private TGSharePluginManager()
	{
		pushPluginMap = new ConcurrentHashMap<Integer, TGSharePlugin>();
	}
	
	/**
	 * 注册插件
	 * @param tag 插件的tag，用于查找插件
	 * @param plugin 消息推送插件
	 */
	@SuppressWarnings("rawtypes")
	public void registerPlugin(Integer tag, TGSharePlugin plugin)
	{
		pushPluginMap.put(tag, plugin);
	}
	
	/**
	 * 取消注册插件
	 * @param tag 插件的tag，用于查找插件
	 */
	public void unregisterPlugin(String tag)
	{
		pushPluginMap.remove(tag);
	}
	
	/**
	 * 根据tag获取插件 
	 * @param tag 插件的tag，用于查找插件
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public TGSharePlugin getPlugin(Integer tag)
	{
		return pushPluginMap.get(tag);
	}
	
	/**
	 * 发送分享结果
	 * @param tag 插件Tag
	 * @param shareResult 分享结果
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends TGShareResult> boolean postShareResult(int tag, T shareResult)
	{
		LOG.d("[Method:postShareResult]  tag == " + tag);
		
		TGSharePlugin plugin = getPlugin(tag);
		if(null != plugin)
		{
			//调用分享结束方法
			return plugin.handleShareResult(shareResult);
		}
		else
		{
			LOG.e("Your had not register this shareplugin that result type is "+ 
		        shareResult.getClass().getSimpleName() +" ever");
			
			return false;
		}
	}
}
