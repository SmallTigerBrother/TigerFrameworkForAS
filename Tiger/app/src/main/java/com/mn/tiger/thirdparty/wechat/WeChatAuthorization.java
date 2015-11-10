package com.mn.tiger.thirdparty.wechat;

import android.app.Activity;
import android.text.TextUtils;

import com.mn.tiger.app.TGApplication;
import com.mn.tiger.authorize.AbsAuthorization;
import com.mn.tiger.authorize.IAuthorizeCallback;
import com.mn.tiger.authorize.ILogoutCallback;
import com.mn.tiger.authorize.IRegisterCallback;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by peng on 15/11/10.
 */
public class WeChatAuthorization extends AbsAuthorization
{
    private static final String AUTH_SCOPE = "snsapi_userinfo";

    private IWXAPI api;

    private String state;

    public WeChatAuthorization(String appID, String state)
    {
        super(appID);
        this.api = WXAPIFactory.createWXAPI(TGApplication.getInstance(), appID);
        this.api.registerApp(appID);
        this.state = state;
    }

    @Override
    public void authorize(Activity activity, IAuthorizeCallback callback)
    {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = AUTH_SCOPE;
        if(!TextUtils.isEmpty(this.state))
        {
            req.state = this.state;
        }
        api.sendReq(req);
    }

    @Override
    public void logout(Activity activity, ILogoutCallback callback)
    {

    }

    @Override
    public void register(Activity activity, String account, String password, IRegisterCallback callback, Object... args)
    {
        throw new UnsupportedOperationException("a wechat account can not be registered by this way");
    }
}
