package com.mn.tiger.share;

import com.mn.tiger.log.Logger;
import com.mn.tiger.share.result.TGQQShareResult;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * QQZone分享启动/回调Activity
 */
public class TGQQZoneEntryActivity extends TGQQEntryActivity
{
	private static final Logger LOG = Logger.getLogger(TGQQEntryActivity.class);
	
	@Override
	public TGQQSharePlugin getPlugin()
	{
		return (TGQQZoneSharePlugin) TGSharePluginManager.getInstance().getPlugin(
				TGSharePluginManager.TAG_QQ_ZONE);
	}
	
	@Override
	public void onCancel()
	{
		JSONObject response = new JSONObject();
		try
		{
			response.put("ret", TGQQShareResult.USER_CANCEL);
		}
		catch (JSONException e)
		{
			LOG.e(e);
		}
		
		TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_QQ_ZONE, 
				new TGQQShareResult(response));
	}

	@Override
	public void onError(UiError error)
	{
		TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_QQ_ZONE, 
				new TGQQShareResult(error));
	}

	@Override
	public void onComplete(Object response)
	{
		TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_QQ_ZONE, 
				new TGQQShareResult((JSONObject)response));
	}
}
