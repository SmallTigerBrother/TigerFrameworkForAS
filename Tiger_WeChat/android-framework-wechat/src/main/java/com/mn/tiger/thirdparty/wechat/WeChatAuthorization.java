package com.mn.tiger.thirdparty.wechat;

import android.app.Activity;
import android.text.TextUtils;

import com.mn.tiger.app.TGActionBarActivity;
import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.authorize.AbsAuthorization;
import com.mn.tiger.authorize.IAuthorizeCallback;
import com.mn.tiger.authorize.ILogoutCallback;
import com.mn.tiger.authorize.IRegisterCallback;
import com.mn.tiger.log.Logger;
import com.mn.tiger.request.TGHttpLoader;
import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.utility.CR;
import com.mn.tiger.utility.ToastUtils;
import com.squareup.otto.Subscribe;
import com.tencent.mm.sdk.modelmsg.SendAuth;

/**
 * Created by peng on 15/11/10.
 */
public class WeChatAuthorization extends AbsAuthorization
{
    private static final Logger LOG = Logger.getLogger(WeChatAuthorization.class);

    /**
     * 允许获取用户信息的Scope
     */
    private static final String AUTH_SCOPE = "snsapi_userinfo";

    private Activity activity;

    /**
     * 认证回调方法
     */
    private IAuthorizeCallback authorizeCallback;

    /**
     * 认证结果
     */
    private WeChatAuthorizeResult authorizeResult;

    /**
     * state参数，用户自行设定
     */
    private String state;

    /**
     * 开发者在微信开放平台申请的AppScecret
     */
    private String secret;

    /**
     * @param appID 开发者在微信开放平台申请的AppID
     * @param secret  开发者在微信开放平台申请的AppScecret
     * @param state  state参数，用户自行设定
     */
    public WeChatAuthorization(String appID, String secret ,String state)
    {
        super(appID);
        TGApplicationProxy.getInstance().getBus().register(this);

        WeChatAPI.init(appID);

        this.secret = secret;
        this.state = state;
    }

    @Override
    public void authorize(Activity activity, IAuthorizeCallback callback)
    {
        LOG.i("[Method:authorize]");
        this.activity = activity;
        this.authorizeCallback = callback;

        final SendAuth.Req req = new SendAuth.Req();
        req.scope = AUTH_SCOPE;
        if(!TextUtils.isEmpty(this.state))
        {
            req.state = this.state;
        }
        WeChatAPI.getInstance().getWXAPI().sendReq(req);
    }

    @Override
    public void logout(Activity activity, ILogoutCallback callback)
    {
        LOG.w("[Method:logout] can not logout with wechat sdk");
    }

    @Override
    public void register(Activity activity, String account, String password, IRegisterCallback callback, Object... args)
    {
        throw new UnsupportedOperationException("a wechat account can not be registered by this way");
    }

    /**
     * 接收请求微信认证的返回值
     * @param resp
     */
    @Subscribe
    public final void handleAuthorizeResp(SendAuth.Resp resp)
    {
        switch (resp.errCode)
        {
            case SendAuth.Resp.ErrCode.ERR_OK:
                LOG.i("[Method:handleAuthorizeResp] errCode == ERR_OK");
                requestAccessToken(resp.code);
                break;
            case SendAuth.Resp.ErrCode.ERR_USER_CANCEL:
                LOG.i("[Method:handleAuthorizeResp] errCode == ERR_USER_CANCEL");
                ToastUtils.showToast(activity, CR.getStringId(activity, "tiger_wechat_auth_user_cancel"));
                if(null != authorizeCallback)
                {
                    authorizeCallback.onAuthorizeCancel();
                }
                break;
            case SendAuth.Resp.ErrCode.ERR_AUTH_DENIED:
                LOG.i("[Method:handleAuthorizeResp] errCode == ERR_AUTH_DENIED");
                ToastUtils.showToast(activity, CR.getStringId(activity, "tiger_wechat_auth_user_denied"));
                if (null != authorizeCallback)
                {
                    authorizeCallback.onAuthorizeError(resp.errCode, "ERR_AUTH_DENIED", "ERR_AUTH_DENIED");
                }
                break;
            default:
                if (null != authorizeCallback)
                {
                    authorizeCallback.onAuthorizeError(resp.errCode, "code == " + resp.errCode, "code == " + resp.errCode);
                }
                break;
        }
    }

    /**
     * 请求AccessToken
     * @param code
     */
    private void requestAccessToken(String code)
    {
        TGHttpLoader<WeChatAuthorizeResult> httpLoader = new TGHttpLoader<WeChatAuthorizeResult>();
        httpLoader.loadByGet(activity, getRequestAccessTokenUrl(code), WeChatAuthorizeResult.class,
                new TGHttpLoader.OnLoadCallback<WeChatAuthorizeResult>()
        {
            @Override
            public void onLoadStart()
            {
                if(activity instanceof TGActionBarActivity)
                {
                    ((TGActionBarActivity)activity).showLoadingDialog();
                }
            }

            @Override
            public void onLoadSuccess(WeChatAuthorizeResult result, TGHttpResult httpResult)
            {
                LOG.i("[Method:requestAccessToken:onLoadSuccess]");
                if(null != result)
                {
                    if (result.getErrCode() > 0)
                    {
                        ToastUtils.showToast(activity, result.getErrMsg());
                    }
                    else
                    {
                        WeChatAuthorization.this.authorizeResult = result;
                        //请求用户信息
                        requestUserInfo(result.getUID(), result.getAccessToken());
                    }
                }
            }

            @Override
            public void onLoadError(int code, String message, TGHttpResult httpResult)
            {
                LOG.i("[Method:requestAccessToken:onLoadError]");
                ToastUtils.showToast(activity, message);
            }

            @Override
            public void onLoadCache(WeChatAuthorizeResult result, TGHttpResult httpResult)
            {}

            @Override
            public void onLoadOver()
            {
                if(activity instanceof TGActionBarActivity)
                {
                    ((TGActionBarActivity)activity).dismissLoadingDialog();
                }
            }
        });
    }

    /**
     *获取请求AccessToken的Url
     * @param code
     * @return
     */
    private String getRequestAccessTokenUrl(String code)
    {
        return "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + getAppID() + "&secret=" + secret +"&code=" +
                code +"&grant_type=authorization_code";
    }

    /**
     * 请求用户信息
     * @param openId
     * @param accessToken
     */
    private void requestUserInfo(String openId, String accessToken)
    {
        TGHttpLoader<WeChatAuthorizeResult.WeChatUser> httpLoader = new TGHttpLoader<WeChatAuthorizeResult.WeChatUser>();
        httpLoader.loadByGet(activity, getRequestUserUrl(openId, accessToken), WeChatAuthorizeResult.WeChatUser.class,
                new TGHttpLoader.OnLoadCallback<WeChatAuthorizeResult.WeChatUser>()
                {
                    @Override
                    public void onLoadStart()
                    {
                        if(activity instanceof TGActionBarActivity)
                        {
                            ((TGActionBarActivity)activity).showLoadingDialog();
                        }
                    }

                    @Override
                    public void onLoadSuccess(WeChatAuthorizeResult.WeChatUser user, TGHttpResult httpResult)
                    {
                        LOG.i("[Method:requestUserInfo:onLoadSuccess]");
                        if(null != user)
                        {
                            if (user.getErrCode() > 0)
                            {
                                ToastUtils.showToast(activity, user.getErrmsg());
                            }
                            else
                            {
                                authorizeResult.setUser(user);
                                authorizeCallback.onAuthorizeSuccess(authorizeResult);
                                LOG.i("[Method:requestUserInfo] onAuthorizeSuccess with userInfo");
                            }
                        }
                    }

                    @Override
                    public void onLoadError(int code, String message, TGHttpResult httpResult)
                    {
                        LOG.i("[Method:requestUserInfo:onLoadError]");
                        ToastUtils.showToast(activity, message);
                    }

                    @Override
                    public void onLoadCache(WeChatAuthorizeResult.WeChatUser result, TGHttpResult httpResult)
                    {}

                    @Override
                    public void onLoadOver()
                    {
                        if(activity instanceof TGActionBarActivity)
                        {
                            ((TGActionBarActivity)activity).dismissLoadingDialog();
                        }
                    }
                });
    }

    /**
     * 获取请求用户信息的Url
     * @param openId
     * @param accessToken
     * @return
     */
    private String getRequestUserUrl(String openId, String accessToken)
    {
        return "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId;
    }

    public void onDestroy()
    {
        TGApplicationProxy.getInstance().getBus().unregister(this);
    }
}
