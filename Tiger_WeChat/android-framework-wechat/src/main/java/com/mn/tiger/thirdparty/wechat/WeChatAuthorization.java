package com.mn.tiger.thirdparty.wechat;

import android.app.Activity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mn.tiger.app.TGActionBarActivity;
import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.authorize.AbsAuthorization;
import com.mn.tiger.authorize.IAuthorizeCallback;
import com.mn.tiger.authorize.ILogoutCallback;
import com.mn.tiger.authorize.IRegisterCallback;
import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.CR;
import com.mn.tiger.utility.ToastUtils;
import com.squareup.otto.Subscribe;
import com.tencent.mm.sdk.modelmsg.SendAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
     * 负责网络请求的接口
     */
    private WeChatAuthorizeService weChatAuthorizeService;

    /**
     * @param appID  开发者在微信开放平台申请的AppID
     * @param secret 开发者在微信开放平台申请的AppScecret
     * @param state  state参数，用户自行设定
     */
    public WeChatAuthorization(String appID, String secret, String state)
    {
        super(appID);
        TGApplicationProxy.getBus().register(this);

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
        if (!TextUtils.isEmpty(this.state))
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
     *
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
                if (null != authorizeCallback)
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
     *
     * @param code
     */
    private void requestAccessToken(String code)
    {
        Call<WeChatAuthorizeResult> call = getWeChatAuthorizeService().getAccessToken(getAppID(), secret, code, "authorization_code");
        Callback<WeChatAuthorizeResult> callback = new Callback<WeChatAuthorizeResult>()
        {
            @Override
            public void onResponse(Call<WeChatAuthorizeResult> call, Response<WeChatAuthorizeResult> response)
            {
                LOG.i("[Method:requestAccessToken:onRequestSuccess]");
                WeChatAuthorizeResult data = response.body();
                if (null != data)
                {
                    if (data.getErrCode() > 0)
                    {
                        ToastUtils.showToast(activity, data.getErrMsg());
                    }
                    else
                    {
                        WeChatAuthorization.this.authorizeResult = data;
                        //请求用户信息
                        requestUserInfo(data.getUID(), data.getAccessToken());
                    }
                }

                if (activity instanceof TGActionBarActivity)
                {
                    ((TGActionBarActivity) activity).dismissLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<WeChatAuthorizeResult> call, Throwable t)
            {
                LOG.i("[Method:requestAccessToken:onRequestError]");
                ToastUtils.showToast(activity, t.getMessage());
                if (activity instanceof TGActionBarActivity)
                {
                    ((TGActionBarActivity) activity).dismissLoadingDialog();
                }
            }
        };
        //发送请求
        if (activity instanceof TGActionBarActivity)
        {
            ((TGActionBarActivity) activity).enqueue(call, callback);
            ((TGActionBarActivity) activity).showLoadingDialog();
        }
        else
        {
            call.enqueue(callback);
        }
    }

    /**
     * 请求用户信息
     *
     * @param openId
     * @param accessToken
     */
    private void requestUserInfo(String openId, String accessToken)
    {
        Call<WeChatAuthorizeResult.WeChatUser> call = getWeChatAuthorizeService().getUserInfo(accessToken, openId);
        Callback<WeChatAuthorizeResult.WeChatUser> callback =
                new Callback<WeChatAuthorizeResult.WeChatUser>()
                {
                    @Override
                    public void onResponse(Call<WeChatAuthorizeResult.WeChatUser> call, Response<WeChatAuthorizeResult.WeChatUser> response)
                    {
                        LOG.i("[Method:requestUserInfo:onRequestSuccess]");
                        WeChatAuthorizeResult.WeChatUser data = response.body();
                        if (null != data)
                        {
                            if (data.getErrCode() > 0)
                            {
                                ToastUtils.showToast(activity, data.getErrmsg());
                            }
                            else
                            {
                                authorizeResult.setUser(data);
                                authorizeCallback.onAuthorizeSuccess(authorizeResult);
                                LOG.i("[Method:requestUserInfo] onAuthorizeSuccess with userInfo");
                            }
                        }

                        if (activity instanceof TGActionBarActivity)
                        {
                            ((TGActionBarActivity) activity).dismissLoadingDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeChatAuthorizeResult.WeChatUser> call, Throwable t)
                    {
                        LOG.w("[Method:requestUserInfo:onRequestError]");
                        ToastUtils.showToast(activity, t.getMessage());
                        if (activity instanceof TGActionBarActivity)
                        {
                            ((TGActionBarActivity) activity).dismissLoadingDialog();
                        }
                    }
                };

        //发送请求
        if (activity instanceof TGActionBarActivity)
        {
            ((TGActionBarActivity) activity).enqueue(call, callback);
            ((TGActionBarActivity) activity).showLoadingDialog();
        }
        else
        {
            call.enqueue(callback);
        }
    }

    private WeChatAuthorizeService getWeChatAuthorizeService()
    {
        if(null == weChatAuthorizeService)
        {
            weChatAuthorizeService = new Retrofit.Builder().baseUrl("https://api.weixin.qq.com/")
                                                           .addConverterFactory(GsonConverterFactory.create())
                                                           .build()
                                                           .create(WeChatAuthorizeService.class);
        }

        return weChatAuthorizeService;
    }

    public void onDestroy()
    {
        TGApplicationProxy.getBus().unregister(this);
    }
}
