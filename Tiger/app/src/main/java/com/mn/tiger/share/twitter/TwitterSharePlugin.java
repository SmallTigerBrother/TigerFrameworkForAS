package com.mn.tiger.share.twitter;

import android.app.Activity;
import android.content.Context;

import com.mn.tiger.share.TGSharePlugin;

/**
 * Created by peng on 15/10/31.
 */
public class TwitterSharePlugin extends TGSharePlugin<Object, TwitterShareResult>
{
    public TwitterSharePlugin(Context context, String appID)
    {
        super(context, appID);
    }

    @Override
    protected void registerApp()
    {

    }

    @Override
    protected void sendShareMsg(Activity activity, Object shareMsg)
    {

    }

    @Override
    protected String getMsgIndicator(Object shareMsg)
    {
        return null;
    }

    @Override
    protected String getMsgIndicator(TwitterShareResult shareResult)
    {
        return null;
    }

    @Override
    public void onShareSuccess(TwitterShareResult result)
    {

    }

    @Override
    public void onShareFailed(TwitterShareResult result)
    {

    }

    @Override
    public void onShareOver(TwitterShareResult result)
    {

    }
}
