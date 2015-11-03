package com.mn.tiger.share.weibo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.mn.tiger.share.TGSharePlugin;
import com.mn.tiger.share.weibo.WeiboShareResult;
import com.mn.tiger.utility.BitmapUtils;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;

/**
 * 微博分享插件
 */
public class WeiBoSharePlugin extends TGSharePlugin<WeiboMultiMessage, WeiboShareResult>
{
	/**
	 * 微博分享API
	 */
	private IWeiboShareAPI weiboShareAPI;
	
	/**
	 * MultiMessage请求类
	 */
	private SendMultiMessageToWeiboRequest request;
	
	public WeiBoSharePlugin(Context context, String appID)
	{
		super(context, appID);
	}
	
	@Override
	protected void registerApp()
	{
		weiboShareAPI = WeiboShareSDK.createWeiboAPI(getContext(), getAppID());
		weiboShareAPI.registerApp();
	}

	@Override
	protected void sendShareMsg(Activity activity, WeiboMultiMessage shareMsg)
	{
		LOG.d("[Method:sendShareMsg]" + shareMsg.toString());
		
		request = new SendMultiMessageToWeiboRequest();
		request.multiMessage = shareMsg;
		request.packageName = getContext().getPackageName();
		request.transaction = String.valueOf(System.currentTimeMillis());
		weiboShareAPI.sendRequest(activity, request);
	}

	@Override
	protected String getMsgIndicator(WeiboMultiMessage shareMsg)
	{
		return request.transaction;
	}

	@Override
	protected String getResultIndicator(WeiboShareResult shareResult)
	{
		return shareResult.getTransaction();
	}

	/**
	 * 获取微博分享API
	 * @return
	 */
	protected IWeiboShareAPI getWeiboShareAPI()
	{
		return weiboShareAPI;
	}
	
	/**
	 * 微博分享信息建造者
	 */
	public static class TGWeiBoMsgBuilder extends TGShareMsgBuilder<WeiboMultiMessage>
	{
		private String title = "";
		
		private String description;
		
		private String text = "";
		
		private String actionUrl;
		
		private String defaultText;
		
		private Bitmap thumbBitmap;
		
		public TGWeiBoMsgBuilder(int shareType)
		{
			super(shareType);
		}

		@Override
		public WeiboMultiMessage build()
		{
			WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
			
			TextObject textObject = new TextObject();
			textObject.text = text;
			textObject.title = title;
			weiboMessage.mediaObject = textObject;
			
			if(!TextUtils.isEmpty(actionUrl))
			{
				WebpageObject webpageObject = new WebpageObject();
				webpageObject.actionUrl = actionUrl;
				webpageObject.title = title;
				webpageObject.identify = Utility.generateGUID();
				webpageObject.description = description;
				webpageObject.defaultText = defaultText;
				if (thumbBitmap != null) 
				{
					Bitmap compressed = BitmapUtils.compressBitmapBytes(thumbBitmap, 30 * 1024);
					if (compressed != null) 
					{
						webpageObject.setThumbImage(compressed);
					}
				}
				
				weiboMessage.mediaObject = webpageObject;
			}
			
			return weiboMessage;
		}
		
		public void setTitle(String title)
		{
			this.title = title;
		}
		
		public void setDescription(String description)
		{
			this.description = description;
		}
		
		public void setText(String text)
		{
			this.text = text;
		}
		
		public void setActionUrl(String actionUrl)
		{
			this.actionUrl = actionUrl;
		}
		
		public void setDefaultText(String defaultText)
		{
			this.defaultText = defaultText;
		}
		
		public void setThumbBitmap(Bitmap thumbBitmap)
		{
			this.thumbBitmap = thumbBitmap;
		}
	}
}
