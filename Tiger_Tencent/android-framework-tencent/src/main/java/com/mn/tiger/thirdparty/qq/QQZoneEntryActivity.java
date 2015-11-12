package com.mn.tiger.thirdparty.qq;

import com.mn.tiger.log.Logger;
import com.mn.tiger.share.TGSharePluginManager;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * QQZone分享启动/回调Activity
 */
public class QQZoneEntryActivity extends QQEntryActivity
{
	private static final Logger LOG = Logger.getLogger(QQEntryActivity.class);
	
	@Override
	public QQSharePlugin getPlugin()
	{
		return (QQZoneSharePlugin) TGSharePluginManager.getInstance().getPlugin(
				TGSharePluginManager.TAG_QQ_ZONE);
	}
	
	@Override
	public void onCancel()
	{
		JSONObject response = new JSONObject();
		try
		{
			response.put("ret", QQShareResult.USER_CANCEL);
		}
		catch (JSONException e)
		{
			LOG.e(e);
		}
		
		TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_QQ_ZONE, 
				new QQShareResult(response));
	}

	@Override
	public void onError(UiError error)
	{
		TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_QQ_ZONE, 
				new QQShareResult(error));
	}

	@Override
	public void onComplete(Object response)
	{
		TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_QQ_ZONE, 
				new QQShareResult((JSONObject)response));
	}
}
