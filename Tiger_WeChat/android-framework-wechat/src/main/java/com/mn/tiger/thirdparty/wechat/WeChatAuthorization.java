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
import com.mn.tiger.request.GsonConverterFactory;
import com.mn.tiger.request.TGCallback;
import com.mn.tiger.utility.CR;
import com.mn.tiger.utility.ToastUtils;
import com.squareup.otto.Subscribe;
import com.tencent.mm.sdk.modelmsg.SendAuth;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

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
        TGCallback<WeChatAuthorizeResult> callback = new TGCallback<WeChatAuthorizeResult>(activity)
        {
            @Override
            public void onRequestSuccess(Response<WeChatAuthorizeResult> response, WeChatAuthorizeResult data)
            {
                LOG.i("[Method:requestAccessToken:onRequestSuccess]");
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
            }

            @Override
            public boolean hasError(Call<WeChatAuthorizeResult> call, Response<WeChatAuthorizeResult> response)
            {
                return null == response || null == response.body();
            }

            @Override
            public void onRequestOver(Call<WeChatAuthorizeResult> call)
            {
                super.onRequestOver(call);
                if (activity instanceof TGActionBarActivity)
                {
                    ((TGActionBarActivity) activity).dismissLoadingDialog();
                }
            }

            @Override
            public void onRequestError(int code, String message, Response<WeChatAuthorizeResult> response)
            {
                super.onRequestError(code, message, response);
                LOG.i("[Method:requestAccessToken:onRequestError]");
                ToastUtils.showToast(activity, message);
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
        TGCallback<WeChatAuthorizeResult.WeChatUser> callback =
                new TGCallback<WeChatAuthorizeResult.WeChatUser>(activity)
                {
                    @Override
                    public void onRequestSuccess(Response<WeChatAuthorizeResult.WeChatUser> response, WeChatAuthorizeResult.WeChatUser data)
                    {
                        LOG.i("[Method:requestUserInfo:onRequestSuccess]");
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
                    }

                    @Override
                    public boolean hasError(Call<WeChatAuthorizeResult.WeChatUser> call, Response<WeChatAuthorizeResult.WeChatUser> response)
                    {
                        return null == response || null == response.body();
                    }

                    @Override
                    public void onRequestOver(Call<WeChatAuthorizeResult.WeChatUser> call)
                    {
                        super.onRequestOver(call);
                        if (activity instanceof TGActionBarActivity)
                        {
                            ((TGActionBarActivity) activity).dismissLoadingDialog();
                        }
                    }

                    @Override
                    public void onRequestError(int code, String message, Response<WeChatAuthorizeResult.WeChatUser> response)
                    {
                        super.onRequestError(code, message, response);
                        LOG.w("[Method:requestUserInfo:onRequestError]");
                        ToastUtils.showToast(activity, message);
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
                    .addConverterFactory(new GsonConverterFactory(new Gson())).build().create(WeChatAuthorizeService.class);
        }

        return weChatAuthorizeService;
    }

    public void onDestroy()
    {
        TGApplicationProxy.getBus().unregister(this);
    }
}
