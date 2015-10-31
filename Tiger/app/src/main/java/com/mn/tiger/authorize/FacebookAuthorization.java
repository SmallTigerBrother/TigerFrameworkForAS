package com.mn.tiger.authorize;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.mn.tiger.log.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by peng on 15/10/31.
 */
public class FacebookAuthorization extends TGAuthorization
{
    private static final Logger LOG = Logger.getLogger(FacebookAuthorization.class);

    public static final String PERMISSION_EMAIL = "email";

    public static final String PERMISSION_USER_BIRTHDAY = "user_birthday";



    public static final String FIELD_ID = "id";

    public static final String FIELD_NAME = "name";

    public static final String FIELD_BIRTHDAY = "birthday";

    public static final String FIELD_EMAIL = "email";

    public static final String FIELD_GENDER = "gender";

    public static final String FIELD_COVER = "cover";



    private LoginButton loginButton;

    private CallbackManager callbackManager;

    private List<String> permissions;

    private IAuthorizeCallback authorizeCallback;

    private ILogoutCallback logoutCallback;

    public FacebookAuthorization(LoginButton loginButton, String... permissions)
    {
        super();
        this.loginButton = loginButton;

        this.permissions = new ArrayList<String>();
        if(null != permissions && permissions.length > 0)
        {
            this.permissions.addAll(Arrays.asList(permissions));
        }

        callbackManager = CallbackManager.Factory.create();
        if(null != loginButton)
        {
            if(null != permissions)
            {
                loginButton.setReadPermissions(permissions);
            }
            loginButton.registerCallback(callbackManager, facebookCallback);
        }
    }

    @Override
    protected void executeAuthorize(Activity activity, IAuthorizeCallback callback)
    {
        this.authorizeCallback = callback;
        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback);
        LoginManager.getInstance().logInWithReadPermissions(activity, permissions);
    }

    @Override
    protected void executeLogout(Activity activity, ILogoutCallback callback)
    {
        this.logoutCallback = logoutCallback;
        LoginManager.getInstance().logOut();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * facebook认证回调接口
     */
    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>()
    {
        @Override
        public void onSuccess(LoginResult loginResult)
        {
            if(null != authorizeCallback)
            {
                TGAuthorizeResult authorizeResult = new TGAuthorizeResult();
                authorizeResult.setAccessToken(loginResult.getAccessToken().getToken());
                authorizeResult.setUID(loginResult.getAccessToken().getUserId());
                authorizeCallback.onSuccess(authorizeResult);
            }
        }

        @Override
        public void onCancel()
        {
            LOG.d("[Method:facebookCallback:onCancel] facebook authorize has canceled");
        }

        @Override
        public void onError(FacebookException exception)
        {
            LOG.e("[Method:facebookCallback:onCancel] facebook authorize has error");
            exception.printStackTrace();
        }
    };

    /**
     * 请求facebook用户信息
     * @param activity
     * @param callback
     * @param paramFields 指定请求用户信息的Fields
     */
    public void requestFacebookUserInfo(final Activity activity, final IFacebookUserInfoCallback callback, String... paramFields)
    {
        Bundle parameters = new Bundle();
        if(null != paramFields && paramFields.length > 0)
        {
            int count = paramFields.length;
            StringBuilder paramBuilder = new StringBuilder();
            for (int i = 0; i < count; i++)
            {
                paramBuilder.append(paramFields[i]);
                if(i < count - 1)
                {
                    paramBuilder.append(",");
                }
            }
            parameters.putString("fields", paramBuilder.toString());
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me", parameters, HttpMethod.GET, new GraphRequest.Callback()
        {
            @Override
            public void onCompleted(final GraphResponse graphResponse)
            {
                if (TextUtils.isEmpty(graphResponse.getRawResponse()))
                {
                    LoginManager.getInstance().logOut();
                    if(null != callback)
                    {
                        callback.onRequestUserInfoError(graphResponse.getError().getErrorCode(),
                                graphResponse.getError().getErrorMessage());
                    }
                }
                else
                {
                    final FacebookUserInfo facebookUserInfo = new Gson().fromJson(graphResponse.getRawResponse(), FacebookUserInfo.class);

                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(null != callback)
                            {
                                callback.onRequestUserInfoSuccess(facebookUserInfo, graphResponse.getRawResponse());
                            }
                        }
                    });
                }
            }
        }).executeAsync();
    }

    /**
     * 请求facebook用户信息时的回调接口
     */
    public interface IFacebookUserInfoCallback
    {
        /**
         * 请求成功，返回值具体的数据与用户设置的请求字段有关系
         * @param facebookUserInfo 用户基本信息
         * @param rawReResponse 原始数据
         */
        void onRequestUserInfoSuccess(FacebookUserInfo facebookUserInfo, String rawReResponse);

        /**
         * 请求出错
         * @param code
         * @param message
         */
        void onRequestUserInfoError(int code, String message);
    }

    public static class FacebookUserInfo implements Serializable
    {
        /**
         * The id of this person's user account. This ID is unique to each app and cannot be used across different apps. Our upgrade guide provides more information about app-specific IDs
         */
        private String id;

        /**
         * The gender selected by this person, male or female. This value will be omitted if the gender is set to a custom value
         */
        private String gender;

        /**
         * The person's primary email address listed on their profile. This field will not be returned if no valid email address is available
         */
        private String email;

        /**
         * The person's full name
         */
        private String name;

        /**
         * The person's birthday. This is a fixed format string, like MM/DD/YYYY. However, people can control who can see the year they were born separately from the month and day so this string can be only the year (YYYY) or the month + day (MM/DD)
         */
        private String birthday;

        /**
         * The person's cover photo
         */
        private FacebookCover cover;

        public String getUserId()
        {
            return id;
        }

        public boolean isMale()
        {
            if (!TextUtils.isEmpty(gender) && gender.equalsIgnoreCase("male"))
            {
                return true;
            }
            return false;
        }

        public String getEmail()
        {
            return TextUtils.isEmpty(email) ? "" : email;
        }

        public String getName()
        {
            return name;
        }

        public String getBirthday()
        {
            return TextUtils.isEmpty(birthday) ? "" : birthday;
        }

        public FacebookCover getCover()
        {
            return cover;
        }
    }

    public static class FacebookCover implements Serializable
    {

        /**
         * The ID of the cover photo
         */
        private String id;

        /**
         * Deprecated. Please use the id field instead
         */
        private String cover_id;

        /**
         * When non-zero, the cover image overflows horizontally. The value indicates the offset percentage of the total image width from the left [0-100]
         */
        private int offset_y;

        /**
         * When non-zero, the cover photo overflows vertically. The value indicates the offset percentage of the total image height from the top [0-100]
         */
        private int offset_x;

        /**
         * Direct URL for the person's cover photo image
         */
        private String source;


        public String getId()
        {
            return id;
        }

        public String getCover_id()
        {
            return cover_id;
        }

        public int getOffset_y()
        {
            return offset_y;
        }

        public int getOffset_x()
        {
            return offset_x;
        }

        public String getSource()
        {
            return source;
        }
    }
}
