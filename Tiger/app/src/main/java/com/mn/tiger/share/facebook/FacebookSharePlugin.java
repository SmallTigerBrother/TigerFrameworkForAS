package com.mn.tiger.share.facebook;

import android.app.Activity;
import android.content.Context;

import com.mn.tiger.share.TGSharePlugin;

/**
 * Created by peng on 15/10/31.
 */
public class FacebookSharePlugin extends TGSharePlugin<String, FacebookShareResult>
{
    public FacebookSharePlugin(Context context, String appID)
    {
        super(context, appID);
    }

    @Override
    protected void registerApp()
    {

    }

    @Override
    protected void sendShareMsg(Activity activity, String shareMsg)
    {

    }

    @Override
    protected String getMsgIndicator(String shareMsg)
    {
        return null;
    }

    @Override
    protected String getMsgIndicator(FacebookShareResult shareResult)
    {
        return null;
    }

    @Override
    public void onShareSuccess(FacebookShareResult result)
    {

    }

    @Override
    public void onShareFailed(FacebookShareResult result)
    {

    }

    @Override
    public void onShareOver(FacebookShareResult result)
    {

    }
}
