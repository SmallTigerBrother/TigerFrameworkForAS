package com.mn.tiger.thirdparty.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.mn.tiger.share.TGSharePlugin;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

/**
 * QQ分享插件
 */
public class QQSharePlugin extends TGSharePlugin<Bundle, QQShareResult>
{
	/**
	 * 默认的分享信息键值（QQ的分享结果不能区分多个信息）
	 */
	private static final String indicatorKey = "indicator";
	
	/**
	 * Tecent分享类
	 */
	private Tencent tencent;
	
	/**
	 * 使用分享功能的界面
	 */
	private Activity activity;
	
	public QQSharePlugin(Context context, String appID)
	{
		super(context, appID);
	}
	
	@Override
	protected void registerApp()
	{
		tencent = Tencent.createInstance(getAppID(), getContext());
	}
	
	@Override
	protected void sendShareMsg(Activity activity, Bundle shareMsg)
	{
		LOG.d("[Method:sendShareMsg]" + shareMsg.toString());
		
		setActivity(activity);
		//QQ分享必须到指定的Activity执行，因此在这里启动TGQQEntryActivity，在TGQQEntryActivity中会调用share2QQ()方法执行分享功能
		Intent intent = new Intent(getContext(), QQEntryActivity.class);
		activity.startActivity(intent);
	}
	
	/**
	 * 分享到QQ
	 */
	protected void share2QQ(IUiListener uiListener)
	{
		LOG.d("[Method:share2QQ]");
		
		if(null != getShareMsg())
		{
			tencent.shareToQQ(activity, getShareMsg(), uiListener);
		}
		//清空actvity，避免内存泄露
		setActivity(null);
	}

	@Override
	protected String getMsgIndicator(Bundle shareMsg)
	{
		return indicatorKey;
	}

	@Override
	protected String getResultIndicator(QQShareResult shareResult)
	{
		return indicatorKey;
	}
	
	/**
	 * 获取Tencent类
	 * @return
	 */
	protected Tencent getTencent()
	{
		return tencent;
	}
	
	protected void setActivity(Activity activity)
	{
		this.activity = activity;
	}
	
	protected Activity getActivity()
	{
		return activity;
	}

	/**
	 * QQ分享信息建造者
	 * @author Dalang
	 *
	 */
	public static class TGQQShareMsgBuilder extends TGShareMsgBuilder<Bundle>
	{
		/**
		 * 分享数据
		 */
		private Bundle params;
		
		public TGQQShareMsgBuilder(int shareType)
		{
			super(shareType);
			params = new Bundle();
		}

		@Override
		public Bundle build()
		{
			if(params.getInt(QQShare.SHARE_TO_QQ_KEY_TYPE, -1) < 0)
			{
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
			}
			//设置不显示分享到QQZone
			params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
			
			//检测参数是否合法
			if(TextUtils.isEmpty(params.getString(QQShare.SHARE_TO_QQ_TARGET_URL)) || 
					TextUtils.isEmpty(params.getString(QQShare.SHARE_TO_QQ_TITLE)) || 
					TextUtils.isEmpty(params.getString(QQShare.SHARE_TO_QQ_SUMMARY)))
			{
				throw new IllegalArgumentException("Please make sure all the params include targetUrl/title/summary were set!");
			}
			
			return params;
		}
		
		public void setQQKeyType(int qqKeyType)
		{
			params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, qqKeyType);
		}
		
		public void setQQTargetUrl(String targetUrl)
		{
			params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
		}
		
		public void setQQTitle(String title)
		{
			params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		}
		
		public void setQQSummary(String summary)
		{
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
		}
		
		public void setQQImageUrl(String imageUrl)
		{
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
		}
		
		public void setQQImageLocalUrl(String imageLocalUrl)
		{
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageLocalUrl);
		}
		
		public void setQQAppName(String appName)
		{
			params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
		}
		
		public void setQQAudioUrl(String audioUrl)
		{
			params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, audioUrl);
		}
	}
}
