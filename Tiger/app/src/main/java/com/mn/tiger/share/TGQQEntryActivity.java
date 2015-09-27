package com.mn.tiger.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mn.tiger.log.Logger;
import com.mn.tiger.share.result.TGQQShareResult;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * QQ分享启动/回调Activity
 */
public class TGQQEntryActivity extends Activity implements IUiListener
{
	private static final Logger LOG = Logger.getLogger(TGQQEntryActivity.class);
	
	/**
	 * tencent接口
	 */
	private Tencent tencent;
	
	/**
	 * 分享插件
	 */
	private TGQQSharePlugin plugin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setVisible(false);
		
		plugin = getPlugin();
		if(null != plugin)
		{
			tencent = plugin.getTencent();
		}
		else
		{
			LOG.e("Your had not register qq shareplugin ever");
			return;
		}
		//实际调用分享方法
		shareToQQ();
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		//实际调用分享方法
		shareToQQ();
	}
	
	public TGQQSharePlugin getPlugin()
	{
		return (TGQQSharePlugin) TGSharePluginManager.getInstance().getPlugin(
				TGSharePluginManager.TAG_QQ);
	}
	
	/**
	 * 分享到QQ/QQZone
	 */
	protected void shareToQQ()
	{
		if(null != plugin)
		{
			plugin.share2QQ(this);
		}
		else
		{
			LOG.e("Your had not register qq shareplugin ever");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(null != tencent)
		{
			//处理分享结果
			tencent.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
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
		
		TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_QQ,
				new TGQQShareResult(response));
	}

	@Override
	public void onError(UiError error)
	{
		TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_QQ,
				new TGQQShareResult(error));
	}

	@Override
	public void onComplete(Object response)
	{
		TGSharePluginManager.getInstance().postShareResult(TGSharePluginManager.TAG_QQ,
				new TGQQShareResult((JSONObject)response));
	}
}
