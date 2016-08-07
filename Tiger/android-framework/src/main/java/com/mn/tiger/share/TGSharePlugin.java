package com.mn.tiger.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.mn.tiger.log.Logger;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分享插件
 * @param <Msg> 分享数据类型
 * @param <Result> 分享结果数据类型
 */
public abstract class TGSharePlugin<Msg, Result extends TGShareResult>
{
	protected final Logger LOG = Logger.getLogger(this.getClass());

	private static final String INDICATOR = "indicator";

	/**
	 * 分享信息建造者
	 */
	private TGShareMsgBuilder<Msg> msgBuilder;
	
	/**
	 * 当前分享信息
	 */
	private Msg shareMsg;
	
	private Context context;
	
	/**
	 * 结果回调接口Map
	 */
	private ConcurrentHashMap<String, IShareResultHandler<Result>> resultHandlerMap;
	
	/**
	 * 分享类型Map
	 */
	private ConcurrentHashMap<String, Integer> shareTypeMap;
	
	/**
	 * 分享功能向服务提供商申请的ID
	 */
	private String appID;
	
	/**
	 * @param context
	 * @param appID 向服务提供商申请的ID
	 */
	public TGSharePlugin(Context context, String appID)
	{
		this.context = context;
		this.appID = appID;
		this.resultHandlerMap = new ConcurrentHashMap<String, IShareResultHandler<Result>>();
		this.shareTypeMap = new ConcurrentHashMap<String, Integer>();
		
		registerApp();
	}
	
	/**
	 * 注册应用
	 */
	protected abstract void registerApp();
	
	/**
	 * 分享
	 * @param activity
	 * @param msgBuilder 分享消息建造者
	 * @param handler 结果回调接口
	 */
	public final synchronized <E extends TGShareMsgBuilder<Msg>> void share(Activity activity,
																			E msgBuilder, IShareResultHandler<Result> handler)
	{
		LOG.d("[Method:share]");
		
		this.msgBuilder = msgBuilder;
		this.shareMsg = getShareMsg();

		sendShareMsg(activity, shareMsg);

		// 绑定分享信息与其分享类型
		shareTypeMap.put(getMsgIndicator(getShareMsg()), msgBuilder.getShareType());
		// 绑定分享信息与结果回调接口
		if(null != handler)
		{
			resultHandlerMap.put(getMsgIndicator(getShareMsg()), handler);
		}
	}
	
	/**
	 * 发送分享信息
	 * @param activity
	 * @param shareMsg 分享信息
	 */
	protected abstract void sendShareMsg(Activity activity, Msg shareMsg);
	
	/**
	 * 获取分享信息
	 * @return
	 */
	protected final Msg getShareMsg()
	{
		if(null != msgBuilder)
		{
			shareMsg = msgBuilder.build();
		}
		
		return shareMsg;
	}
	
	/**
	 * 获取分享信息唯一标示，同一条分享信息的内容、结果的标识必需完全一致，这样才能定向匹配对应的handler和type
	 * @param shareMsg 分享信息
	 * @return
	 */
	protected String getMsgIndicator(Msg shareMsg)
    {
        return INDICATOR;
    }
	
	/**
	 * 获取分享结果唯一标示，同一条分享信息的内容、结果的标识必需完全一致，这样才能定向匹配对应的handler和type
	 * @param shareResult 分享结果
	 * @return
	 */
	protected String getResultIndicator(Result shareResult)
    {
        return INDICATOR;
    }

	/**
	 * 接收分享结果
	 * @param result 分享结果
	 */
    public final boolean handleShareResult(Result result)
	{
		LOG.d("[Method:handleShareResult] result == " + result.toString());
		
		if(!hasSendMessage(result))
		{
			LOG.e("[Method:handleShareResult] this plugin had never send the message");
			return false;
		}
		
		//设置信息分享类型
		result.setShareType(shareTypeMap.get(getResultIndicator(result)));
		//获取分享结果回调接口
		IShareResultHandler<Result> handler = resultHandlerMap.get(getResultIndicator(result));

        if(result.isCanceled())
        {
            if(null != handler)
            {
                handler.onShareCancel(result);
            }
            onShareCanceled(result);
        }
        else if(result.isSuccess())
        {
            if(null != handler)
            {
                handler.onShareSuccess(result);
            }
            //执行分享成功方法，仅适用于与界面无关的功能
            onShareSuccess(result);
        }
        else
        {
            if(null != handler)
            {
                handler.onShareFailed(result);
            }
            //执行分享失败方法，仅适用于与界面无关的功能
            onShareFailed(result);
        }

		onShareOver(result);
		
		//移除结果回调接口，防止内存泄露
		onRemoveResultHandler(result);
		
		return true;
	}
	
	/**
	 * 分享成功回调（不可添加操作界面相关的代码）
	 * @param result 分享结果
	 */
	public void onShareSuccess(Result result)
    {

    }
	
	/**
	 * 分享失败回调（不可添加操作界面相关的代码）
	 * @param result 分享结果
	 */
	public void onShareFailed(Result result)
    {

    }
	
	/**
	 * 分享完成，不论分享成功或失败都会回调
	 */
	public void onShareOver(Result result)
    {

    }

    /**
     * 分享取消（不可添加操作界面相关的代码）
     * @param result
     */
    public void onShareCanceled(Result result)
    {

    }
	
	/**
	 * 移除结果回调接口，防止内存泄露
	 * @param result 分享结果
	 */
	protected final void onRemoveResultHandler(Result result)
	{
		//删除handler
		resultHandlerMap.remove(getResultIndicator(result));
	}
	
	protected final boolean hasSendMessage(Result result)
	{
		if(null != shareTypeMap.get(getResultIndicator(result)))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * 移除结果回调接口
	 * @param resultHandler
	 */
	protected final void removeResultHandler(IShareResultHandler<Result> resultHandler)
	{
		Iterator<Entry<String, IShareResultHandler<Result>>> iterator =
				resultHandlerMap.entrySet().iterator();
		while (iterator.hasNext())
		{
			if(resultHandler.equals(iterator.next().getValue()))
			{
				iterator.remove();
				break;
			}
		}
	}
	
	public Context getContext()
	{
		return context;
	}
	
	/**
	 * 获取 向服务提供商申请的ID
	 * @return
	 */
	protected String getAppID()
	{
		return appID;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{

	}
	
	/**
	 * 分享信息建造者
	 * @param <T> 目标分享信息类型
	 */
	public static abstract class TGShareMsgBuilder<T>
	{
		/**
		 * 分享类型
		 */
		private int shareType;
		
		/**
		 * 分享信息工厂
		 * @param shareType 分享类型，具体业务类型，若应用不区分分享类型，填任意数字皆可
		 */
		public TGShareMsgBuilder(int shareType)
		{
			this.shareType = shareType;
		}
		
		/**
		 * 获取分享类型
		 */
		public int getShareType()
		{
			return shareType;
		}
		
		/**
		 * 构造分享信息
		 * @return
		 */
		public abstract T build();
	}
}
