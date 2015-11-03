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
}
