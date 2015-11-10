package com.mn.tiger.thirdparty.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mn.tiger.log.Logger;
import com.mn.tiger.share.TGSharePluginManager;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;

/**
 * 微博分享回调Activity
 */
public class WeiBoEntryActivity extends Activity implements IWeiboHandler.Response
{
	private static final Logger LOG = Logger.getLogger(WeiBoEntryActivity.class);
	
	private IWeiboShareAPI weiboShareAPI;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setVisible(false);
		
		weiboShareAPI = getWeiboShareAPI();
		
		if(null != weiboShareAPI)
		{
			weiboShareAPI.handleWeiboResponse(getIntent(), this);
		}
		else
		{
			LOG.e("Your had not register weibo shareplugin ever");
		}
	}
	
	protected IWeiboShareAPI getWeiboShareAPI()
	{
		WeiBoSharePlugin plugin = (WeiBoSharePlugin) TGSharePluginManager.getInstance().getPlugin(
				TGSharePluginManager.TAG_WEI_BO);
		return plugin.getWeiboShareAPI();
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		if(null != weiboShareAPI)
		{
			weiboShareAPI.handleWeiboResponse(intent, this);
		}
		else
		{
			LOG.e("Your had not register weibo shareplugin ever");
		}
	}

	@Override
	public void onResponse(BaseResponse response)
	{
		TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_WEI_BO, 
				new WeiboShareResult(response));
	}
}
