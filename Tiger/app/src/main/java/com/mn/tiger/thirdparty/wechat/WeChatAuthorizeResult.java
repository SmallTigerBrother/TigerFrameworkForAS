package com.mn.tiger.thirdparty.wechat;

import com.mn.tiger.authorize.TGAuthorizeResult;
import com.tencent.mm.sdk.modelmsg.SendAuth;

/**
 * Created by peng on 15/11/10.
 */
public class WeChatAuthorizeResult extends TGAuthorizeResult
{
    private int errCode;

    private String errmsg;

    private String access_token;

    private long expires_in;

    private String refresh_token;

    private String openid;

    private String scope;

    private WeChatUser user;

    @Override
    public String getAccessToken()
    {
        return access_token;
    }

    public long getExpiresIn()
    {
        return expires_in;
    }

    public String getRefreshToken()
    {
        return refresh_token;
    }

    @Override
    public String getUID()
    {
        return openid;
    }

    public String getScope()
    {
        return scope;
    }

    int getErrCode()
    {
        return errCode;
    }

    String getErrMsg()
    {
        return errmsg;
    }

    void setUser(WeChatUser user)
    {
        this.user = user;
    }

    public WeChatUser getUser()
    {
        return user;
    }

    public static class WeChatUser
    {
        private int errCode;

        private String errmsg = "";

        private String nickname; //用户昵称

        private int sex; //1为男性，2为女性

        private String province; //普通用户个人资料填写的省份

        private String city; //普通用户个人资料填写的城市

        private String headimgurl; //用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像）

        private String unionid; //用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。

        public String getNickname()
        {
            return nickname;
        }

        public int getSex()
        {
            return sex;
        }

        public String getProvince()
        {
            return province;
        }

        public String getCity()
        {
            return city;
        }

        public String getHeadimgurl()
        {
            return headimgurl;
        }

        public String getUnionid()
        {
            return unionid;
        }

        int getErrCode()
        {
            return errCode;
        }

        String getErrmsg()
        {
            return errmsg;
        }
    }
}
