package com.mn.tiger.thirdparty.wechat;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by peng on 16/3/22.
 */
public interface WeChatAuthorizeService
{
    /**
     * 获取微信授权的AccessToken
     * @param appId
     * @param secret
     * @param code
     * @param grant_type
     * @return
     */
    @GET("sns/oauth2/access_token")
    Call<WeChatAuthorizeResult> getAccessToken(@Query("appid")String appId,
                                  @Query("secret")String secret,
                                  @Query("code")String code,
                                  @Query("grant_type")String grant_type);

    /**
     * 获取微信用户信息
     * @param accessToken
     * @param openId
     * @return
     */
    @GET("sns/userinfo")
    Call<WeChatAuthorizeResult.WeChatUser> getUserInfo(@Query("access_token") String accessToken,
                                                       @Query("openid")String openId);
}
