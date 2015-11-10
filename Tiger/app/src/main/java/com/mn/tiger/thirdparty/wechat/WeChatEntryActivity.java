package com.mn.tiger.thirdparty.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mn.tiger.app.TGApplication;
import com.mn.tiger.log.Logger;
import com.mn.tiger.share.TGSharePluginManager;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 *微信分享回调Activity
 */
public class WeChatEntryActivity extends Activity implements IWXAPIEventHandler
{
	private static final Logger LOG = Logger.getLogger(WeChatEntryActivity.class);
	
	private WeChatSharePlugin sharePlugin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setVisible(false);
		
		initSharePlugin();
		if(null != sharePlugin)
		{
			sharePlugin.getIWXApi().handleIntent(getIntent(), this);
		}
	}
	
	private void initSharePlugin()
	{
		if(null != sharePlugin)
		{
			return;
		}
		
		sharePlugin = (WeChatSharePlugin) TGSharePluginManager.getInstance().getPlugin(
				TGSharePluginManager.TAG_WEI_CHAT);
		if(null == sharePlugin)
		{
			sharePlugin = (WeChatSharePlugin) TGSharePluginManager.getInstance().getPlugin(
					TGSharePluginManager.TAG_WEI_CHAT_TIME_LINE);
		}
		
		if(null == sharePlugin)
		{
			LOG.e("You had never register WeChatSharePlugin before");
		}
	}
	
	protected IWXAPI getIWXAPI()
	{
		WeChatSharePlugin plugin = (WeChatSharePlugin) TGSharePluginManager.getInstance().getPlugin(
				TGSharePluginManager.TAG_WEI_CHAT);
		return plugin.getIWXApi();
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		initSharePlugin();
		if(null != sharePlugin)
		{
			sharePlugin.getIWXApi().handleIntent(getIntent(), this);
		}
	}
	
	@Override
	public void onReq(BaseReq req)
	{
        LOG.i("[Method:onReq] share over");

		WeChatShareResult shareResult = new WeChatShareResult(req);
		
		boolean postResult = TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_WEI_CHAT, 
				shareResult);
		if(!postResult)
		{
			TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_WEI_CHAT_TIME_LINE, 
					shareResult);
		}
	}

	@Override
	public void onResp(BaseResp req)
	{
		if(req instanceof SendMessageToWX.Resp)
		{
			LOG.i("[Method:onResp] share over");
			WeChatShareResult shareResult = new WeChatShareResult(req);

			boolean postResult = TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_WEI_CHAT,
					shareResult);
			if(!postResult)
			{
				TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_WEI_CHAT_TIME_LINE,
						shareResult);
			}
		}
		else if(req instanceof SendAuth.Resp)
		{
			LOG.i("[Method:onResp] authorize over");
            TGApplication.getBus().post((SendAuth.Resp)req);
		}
	}
}
