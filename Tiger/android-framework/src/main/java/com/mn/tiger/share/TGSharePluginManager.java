package com.mn.tiger.share;

import com.mn.tiger.log.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 分享插件管理器
 */
public class TGSharePluginManager
{
    private static final Logger LOG = Logger.getLogger(TGSharePluginManager.class);

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
     * TAG —— Facebook分享
     */
    public static final int TAG_FACEBOOK = 1005;

    /**
     * TAG —— Twitter分享
     */
    public static final int TAG_TWITTER = 1006;

    /**
     * TAG——邮件分享
     */
    public static final int TAG_EMAIL = 1007;

    /**
     * TAG——短信分享
     */
    public static final int TAG_SMS = 1008;

    /**
     * 插件map
     */
    @SuppressWarnings("rawtypes")
    private ConcurrentHashMap<Integer, TGSharePlugin> pluginMap;

    /**
     * 插件管理器单例
     */
    private static TGSharePluginManager sharePluginManager;

    /**
     * 获取单例对象
     * @return
     */
    public static TGSharePluginManager getInstance()
    {
        if(null == sharePluginManager)
        {
            synchronized (TGSharePluginManager.class)
            {
                if(null == sharePluginManager)
                {
                    sharePluginManager = new TGSharePluginManager();
                }
            }
        }

        return sharePluginManager;
    }

    @SuppressWarnings("rawtypes")
    private TGSharePluginManager()
    {
        pluginMap = new ConcurrentHashMap<>();
    }

    /**
     * 注册插件
     * @param plugin 消息推送插件
     */
    @SuppressWarnings("rawtypes")
    public void registerPlugin(TGSharePlugin plugin)
    {
        //微信朋友圈
        String className = plugin.getClass().getName();
        if(className.equalsIgnoreCase("com.mn.tiger.thirdparty.wechat.WeChatTimeLineSharePlugin"))
        {
            pluginMap.put(TAG_WEI_CHAT_TIME_LINE, plugin);
            return;
        }
        else if(className.equalsIgnoreCase("com.mn.tiger.thirdparty.wechat.WeChatSharePlugin"))
        {
            pluginMap.put(TAG_WEI_CHAT, plugin);
        }
        else if(className.equalsIgnoreCase("com.mn.tiger.thirdparty.weibo.WeiBoSharePlugin"))
        {
            pluginMap.put(TAG_WEI_BO, plugin);
        }
        else if(className.equalsIgnoreCase("com.mn.tiger.thirdparty.qq.QQZoneSharePlugin"))
        {
            pluginMap.put(TAG_QQ_ZONE, plugin);
        }
        else if(className.equalsIgnoreCase("com.mn.tiger.thirdparty.qq.QQSharePlugin"))
        {
            pluginMap.put(TAG_QQ, plugin);
        }
        else if(className.equalsIgnoreCase("com.mn.tiger.thirdparty.facebook.FacebookSharePlugin"))
        {
            pluginMap.put(TAG_FACEBOOK, plugin);
        }
        else if(className.equalsIgnoreCase("com.mn.tiger.thirdparty.twitter.TwitterSharePlugin"))
        {
            pluginMap.put(TAG_TWITTER, plugin);
        }
        else if(plugin instanceof EmailSharePlugin)
        {
            pluginMap.put(TAG_EMAIL, plugin);
        }
        else if(plugin instanceof SMSSharePlugin)
        {
            pluginMap.put(TAG_SMS, plugin);
        }
        else
        {
            LOG.e("[Method:registerPlugin] unknown plugin " + className);
        }
    }

    /**
     * 取消注册插件
     * @param tag 插件的tag，用于查找插件
     */
    public void unregisterPlugin(String tag)
    {
        pluginMap.remove(tag);
    }

    /**
     * 根据tag获取插件
     * @param tag 插件的tag，用于查找插件
     * @return
     */
    @SuppressWarnings("rawtypes")
    public TGSharePlugin getPlugin(Integer tag)
    {
        return pluginMap.get(tag);
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
