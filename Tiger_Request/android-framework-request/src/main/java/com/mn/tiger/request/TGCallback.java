package com.mn.tiger.request;

import android.app.Activity;
import android.content.Context;

import com.mn.tiger.app.TGActionBarActivity;
import com.mn.tiger.log.Logger;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dalang on 2016/3/13.
 */
public abstract class TGCallback<T> implements Callback<T>
{
    private static final Logger LOG = Logger.getLogger(TGCallback.class);

    /**
     * 是否请求成功
     */
    private boolean success = false;

    private Context context;

    public TGCallback(Context context)
    {
        this.context = context;
    }

    protected Context getContext()
    {
        return context;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response)
    {
        //如果界面已销毁,取消call
        if(null != context && context instanceof Activity && ((Activity)context).isFinishing())
        {
            if(context instanceof TGActionBarActivity)
            {
                ((TGActionBarActivity)context).cancel(call);
            }
            return;
        }

        //从队列中移除call
        if(null != context && context instanceof TGActionBarActivity)
        {
            ((TGActionBarActivity)context).dequeue(call);
        }

        if(!hasError(call, response))
        {
            //请求成功
            this.success = true;
            if(null != response.body())
            {
                LOG.i("[Method:onResponse] raw = " + response.raw() + "\n" + response.body().toString());
            }
            else
            {
                LOG.i("[Method:onResponse] raw = " + response.raw().toString());
            }

            onRequestSuccess(response, response.body());
        }
        else
        {
            if(null != response)
            {
                //请求失败
                this.success = false;
                if(null != response.body())
                {
                    LOG.e("[Method:onResponse] error , raw = " + response.raw() + "\n" + response.body());
                }
                else if(null != response.errorBody())
                {
                    LOG.e("[Method:onResponse] error , raw = " + response.raw() + "\n" + response.errorBody());
                }
                else
                {
                    LOG.e("[Method:onResponse] error , raw = " + response.raw());
                }

                onRequestError(response.code(), response.message(), response);
            }
            else
            {
                //请求失败
                this.success = false;
                LOG.e("[Method:onResponse] error, url = " + call.request().url() + " code = -1");
                onRequestError(-1, "unknown error, the response is null", response);
            }
        }

        /**
         * 请求结束
         */
        onRequestOver(call);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t)
    {
        LOG.e("[Method:onFailure] url = " + call.request().url() + (null != t ? t.getMessage() : ""));
        this.success = false;
        onRequestError(-1, t.getMessage(), null);
        onRequestOver(call);
    }

    /**
     * 判断是否存在请求异常
     * @param call
     * @param response
     * @return
     */
    public boolean hasError(Call<T> call, Response<T> response)
    {
        return false;
    }

    /**
     * 请求成功回调方法
     * @param response
     * @param result
     */
    public abstract void onRequestSuccess(Response<T> response, T result);

    /**
     * 请求错误回调方法
     * @param code
     * @param message
     * @param response
     */
    public void onRequestError( int code ,String message, Response<T> response)
    {
    }

    /**
     * 请求结束回调方法
     * @param call
     */
    public void onRequestOver(Call<T> call)
    {
    }

    /**
     * 是否请求成功
     * @return
     */
    public boolean isSuccess()
    {
        return success;
    }
}
