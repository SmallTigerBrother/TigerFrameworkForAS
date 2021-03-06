package com.mn.tiger.thirdparty.wechat;

import android.content.Context;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;


/**
 * 微信朋友圈分享插件
 */
public class WeChatTimeLineSharePlugin extends WeChatSharePlugin
{
	public WeChatTimeLineSharePlugin(Context context, String appID)
	{
		super(context, appID);
	}
	
	@Override
	protected SendMessageToWX.Req initReq()
	{
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = getShareMsg();
		//设置分享到朋友圈使用scene
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		return req;
	}

}
