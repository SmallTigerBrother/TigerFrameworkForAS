package com.mn.tiger.thirdparty.wechat;

import com.mn.tiger.app.TGApplicationProxy;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by peng on 15/11/25.
 */
class WeChatAPI
{
    private static WeChatAPI weChatAPI;

    private IWXAPI api;

    public static void init(String appID)
    {
        if(null == weChatAPI)
        {
            weChatAPI = new WeChatAPI(appID);
        }
    }

    private WeChatAPI(String appID)
    {
        this.api = WXAPIFactory.createWXAPI(TGApplicationProxy.getInstance().getApplication(), appID);
        this.api.registerApp(appID);
    }

    public static WeChatAPI getInstance()
    {
        if(null == weChatAPI)
        {
            throw new RuntimeException("you must init WeChatAPI before use this method");
        }
        return weChatAPI;
    }

    public IWXAPI getWXAPI()
    {
        return api;
    }
}
