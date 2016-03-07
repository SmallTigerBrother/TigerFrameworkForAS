package com.mn.tiger.request;

import android.app.Activity;
import android.content.Context;

import com.mn.tiger.app.TGActionBarActivity;
import com.mn.tiger.log.Logger;

import java.util.Iterator;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Dalang on 2016/3/6.
 * Retrofit工具类
 */
public class TGRetrofit
{
    private static final Logger LOG = Logger.getLogger(TGRetrofit.class);

    /**
     * 将RequestBody输出为字符串
     * @param requestBody
     * @return
     */
    protected static String stringify(RequestBody requestBody)
    {
        StringBuilder builder = new StringBuilder();
        if(requestBody instanceof FormBody)
        {
            FormBody formBody = (FormBody) requestBody;
            int size = formBody.size();
            for (int i = 0; i < size; i++)
            {
                builder.append(formBody.encodedName(i));
                builder.append(" = ");
                builder.append(formBody.encodedValue(i));
                builder.append(" ; ");
            }
        }
        return builder.toString();
    }

    /**
     * 想Request中添加参数
     * @param originalRequest
     * @param params
     * @return
     */
    protected static Request appendParams(Request originalRequest, Map<String, String> params)
    {
        if(originalRequest.method().equalsIgnoreCase("GET"))
        {
            //添加基础参数
            HttpUrl.Builder urlBuilder = originalRequest.url().newBuilder();
            Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<String,String> entry = iterator.next();
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            HttpUrl url = urlBuilder.build();
            LOG.i("[Method:appendParams] method = " + originalRequest.method() + " ; url = " + url);
            return originalRequest.newBuilder().url(url).build();
        }
        else if(originalRequest.method().equals("POST"))
        {
            RequestBody requestBody = originalRequest.body();
            if(requestBody instanceof FormBody)
            {
                //添加基础数据
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                FormBody formBody = (FormBody)requestBody;
                int size = formBody.size();
                for (int i = 0;i < size; i++)
                {
                    formBodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                }

                Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();
                while (iterator.hasNext())
                {
                    Map.Entry<String,String> entry = iterator.next();
                    formBodyBuilder.add(entry.getKey(), entry.getValue());
                }
                FormBody newFormBody = formBodyBuilder.build();
                LOG.i("[Method:appendParams] method = " + originalRequest.method() + " ; url = " + originalRequest.url());
                LOG.i("[Method:appendParams] params = " + stringify(newFormBody));
                return originalRequest.newBuilder().post(newFormBody).build();
            }
            else if(requestBody instanceof MultipartBody)
            {
                //添加基础数据
                MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
                MultipartBody multipartBody = (MultipartBody)requestBody;
                multipartBodyBuilder.setType(multipartBody.type());
                int size = multipartBody.size();
                for (int i = 0; i < size; i++)
                {
                    multipartBodyBuilder.addPart(multipartBody.part(i));
                }
                //处理multipart/form-data类型，其他类型后续处理
                if(multipartBody.type().equals(MultipartBody.FORM))
                {
                    Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();
                    while (iterator.hasNext())
                    {
                        Map.Entry<String,String> entry = iterator.next();
                        multipartBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                    }
                }
                else
                {
                    FormBody.Builder formBodyBuilder = new FormBody.Builder();
                    Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();
                    while (iterator.hasNext())
                    {
                        Map.Entry<String,String> entry = iterator.next();
                        formBodyBuilder.add(entry.getKey(), entry.getValue());
                    }
                    multipartBodyBuilder.addPart(formBodyBuilder.build());
                }
                return originalRequest.newBuilder().post(multipartBodyBuilder.build()).build();
            }
        }
        else if(originalRequest.method().equalsIgnoreCase("PUT"))
        {
            LOG.i("[Method:appendParams] method = " + originalRequest.method() + " ; url = " + originalRequest.url());
        }
        else if(originalRequest.method().equalsIgnoreCase("DELETE"))
        {
            LOG.i("[Method:appendParams] method = " + originalRequest.method() + " ; url = " + originalRequest.url());
        }
        return originalRequest;
    }

    /**
     * 回调函数
     * @param <T>
     * @param <D>
     */
    public abstract static class Callback<T, D> implements retrofit2.Callback<T>
    {
        /**
         * 是否请求成功
         */
        private boolean success = false;

        private Context context;

        public Callback(Context context)
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
                onRequestSuccess(response, parseOriginalResponse(response));
            }
            else
            {
                if(null != response)
                {
                    //请求失败
                    this.success = false;
                    LOG.e("[Method:onResponse] code = " + response.code() + " message = " + response.message());
                    onRequestError(response.code(), response.message(), response);
                }
                else
                {
                    //请求失败
                    this.success = false;
                    LOG.e("[Method:onResponse] code = -1");
                    onRequestError(-1, "", response);
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
        protected boolean hasError(Call<T> call, Response<T> response)
        {
            return false;
        }

        /**
         * 请求成功回调方法
         * @param response
         * @param data
         */
        public abstract void onRequestSuccess(Response<T> response, D data);

        /**
         * 解析原始结果
         * @param response
         * @return
         */
        protected abstract D parseOriginalResponse(Response<T> response);

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
        protected void onRequestOver(Call<T> call)
        {

        }

        /**
         * 是否请求成功
         * @return
         */
        protected boolean isSuccess()
        {
            return success;
        }
    }
}
