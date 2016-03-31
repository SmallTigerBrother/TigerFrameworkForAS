package com.mn.tiger.thirdparty.facebook;

import com.mn.tiger.authorize.TGAuthorizeResult;

/**
 * Created by peng on 16/3/30.
 */
public class FacebookAuthorizeResult extends TGAuthorizeResult
{
    private FacebookAuthorization.FacebookUserInfo userInfo;

    public FacebookAuthorization.FacebookUserInfo getUserInfo()
    {
        return userInfo;
    }

    void setUserInfo(FacebookAuthorization.FacebookUserInfo userInfo)
    {
        this.userInfo = userInfo;
    }
}
