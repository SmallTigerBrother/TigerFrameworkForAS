package com.mn.tiger.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.mn.tiger.share.result.TGQQShareResult;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;

import java.util.ArrayList;

/**
 * QQ空间分享插件
 */
public class TGQQZoneSharePlugin extends TGQQSharePlugin
{
	public TGQQZoneSharePlugin(Context context, String appID)
	{
		super(context, appID);
	}
	
	@Override
	protected void sendShareMsg(Activity activity, Bundle shareMsg)
	{
		LOG.d("[Method:sendShareMsg]" + shareMsg.toString());
		
		setActivity(activity);
		/*QQ分享必须到指定的Activity执行，因此在这里启动TGQQZoneEntryActivity，
		在TGQQZoneEntryActivity中会调用share2QQ()方法执行分享功能*/
		Intent intent = new Intent(getContext(), TGQQZoneEntryActivity.class);
		activity.startActivity(intent);
	}
	
	/**
	 * 分享到QQ空间
	 */
	@Override
	protected void share2QQ(IUiListener uiListener)
	{
		LOG.d("[Method:share2QQZone]");
		
		getTencent().shareToQzone(getActivity(), getShareMsg(), uiListener);
		//清空actvity，避免内存泄露
		setActivity(null);
	}
	
	@Override
	protected boolean hasSendMessage(TGQQShareResult result)
	{
		return true;
	}
	
	/**
	 * QQZone分享信息建造者
	 */
	public static class TGQQZoneShareMsgBuilder extends TGShareMsgBuilder<Bundle>
	{
		/**
		 * 分享数据
		 */
		private Bundle params;
		
		public TGQQZoneShareMsgBuilder(int shareType)
		{
			super(shareType);
			params = new Bundle();
		}

		@Override
		public Bundle build()
		{
			if(params.getInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, -1) < 0)
			{
				params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
			}
			//检查是否存在参数错误
			if(TextUtils.isEmpty(params.getString(QzoneShare.SHARE_TO_QQ_TARGET_URL)) ||
					TextUtils.isEmpty(params.getString(QzoneShare.SHARE_TO_QQ_TITLE)) ||
					TextUtils.isEmpty(params.getString(QzoneShare.SHARE_TO_QQ_SUMMARY)) ||
					null == params.getStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL))
			{
				throw new IllegalArgumentException("Please make sure all the params include targetUrl/title/summary/imageUrl were set!");
			}
			
			return params;
		}
		
		public void setQZoneKeyType(int qZoneKeyType)
		{
			params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, qZoneKeyType);
		}
		
		public void setQQTargetUrl(String targetUrl)
		{
			params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
		}
		
		public void setQQTitle(String title)
		{
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
		}
		
		public void setQQSummary(String summary)
		{
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
		}
		
		public void setQQImageUrl(ArrayList<String> imageUrls)
		{
			params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		}
	}
}
