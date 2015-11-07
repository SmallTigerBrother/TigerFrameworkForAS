package com.mn.tiger.request.sync;

import android.content.Context;

import com.mn.tiger.log.Logger;
import com.mn.tiger.request.HttpType;
import com.mn.tiger.request.TGHttpParams;
import com.mn.tiger.request.error.TGHttpError;
import com.mn.tiger.request.receiver.TGHttpResult;
import com.mn.tiger.utility.StringUtils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OKHttp同步请求封装
 * Created by peng on 15/8/27.
 */
public class OkHttpSyncHttpLoader extends AbstractSyncHttpLoader
{
    private static final Logger LOG = Logger.getLogger(OkHttpSyncHttpLoader.class);

    private int tag = -1;

    private static OkHttpClient okHttpClient = new OkHttpClient();

    {
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(120, TimeUnit.SECONDS);
        okHttpClient.setRetryOnConnectionFailure(true);
    }

    public OkHttpSyncHttpLoader(int tag)
    {
        this.tag = tag;
    }

    @Override
    public TGHttpResult loadByGetSync(Context context, String requestUrl, TGHttpParams parameters, Map<String, String> properties)
    {
        if(StringUtils.isUrl(requestUrl))
        {
            try
            {
                Request request = buildRequest(HttpType.REQUEST_GET, requestUrl, parameters, properties);
                TGHttpResult httpResult = execute(context, request);
                LOG.d("[Method:loadByGetSync] url : " + requestUrl + "\n" + "params : " + parameters.getStringParams().toString() + "\n" +
                        "result : " + httpResult.getResult());
                return httpResult;
            }
            catch (Exception e)
            {
                LOG.e("[Method:loadByGetSync] tag == " + tag + " : " + e);
                return getHttpResultWhileIOException(context, requestUrl, e.getMessage());
            }
        }
        else
        {
            return getHttpResultWhileUrlInvalid(context, requestUrl);
        }
    }

    @Override
    public TGHttpResult loadByPostSync(Context context, String requestUrl, TGHttpParams parameters, Map<String, String> properties)
    {
        if(StringUtils.isUrl(requestUrl))
        {
            try
            {
                Request request = buildRequest(HttpType.REQUEST_POST, requestUrl, parameters, properties);
                TGHttpResult httpResult = execute(context,request);
                LOG.d("[Method:loadByPostSync] url : " + requestUrl + "\n" + "params : " + parameters.getStringParams().toString() + "\n" +
                        "result : " + httpResult.getResult());
                return httpResult;
            }
            catch (Exception e)
            {
                LOG.e("[Method:loadByPostSync] tag == " + tag + " : " + e);
                return getHttpResultWhileIOException(context, requestUrl, e.getMessage());
            }
        }
        else
        {
            return getHttpResultWhileUrlInvalid(context, requestUrl);
        }
    }

    @Override
    public TGHttpResult loadByPutSync(Context context, String requestUrl, TGHttpParams parameters, Map<String, String> properties)
    {
        return null;
    }

    @Override
    public TGHttpResult loadByDeleteSync(Context context, String requestUrl, TGHttpParams parameters, Map<String, String> properties)
    {
        return null;
    }

    /**
     * 取消Http请求
     */
    public void cancel()
    {
        LOG.d("[Method:cancel] tag == " + tag);
        try
        {
            okHttpClient.cancel(tag);
        }
        catch (Exception e)
        {
            LOG.d("[Method:cancel] tag == " + tag + " ; exception == " + e.getMessage());
        }
    }

    /**
     * 执行Http请求
     * @param context
     * @param request
     * @return
     * @throws IOException
     */
    private TGHttpResult execute(Context context, Request request) throws IOException
    {
        Response response = okHttpClient.newCall(request).execute();
        TGHttpResult httpResult = initHttpResult(context);
        httpResult.setResponseCode(response.code());
        if(response.isSuccessful())
        {
            httpResult.setResult(response.body().string());
        }
        else
        {
            httpResult.setResult(response.message());
        }
        return httpResult;
    }

    /**
     * 构造Request请求
     * @param httpType
     * @param requestUrl
     * @param parameters
     * @param properties
     * @return
     * @throws IOException
     */
    private Request buildRequest(int httpType, String requestUrl, TGHttpParams parameters,
                                 Map<String, String> properties) throws IOException
    {
        Request.Builder builder = new Request.Builder();
        switch (httpType)
        {
            case HttpType.REQUEST_GET:
                builder.url(TGHttpParams.appendParams2Url(requestUrl, parameters.getStringParams())).get();
                break;
            case HttpType.REQUEST_POST:
                RequestBody requestBody = initRequestBody(parameters);
                builder.url(requestUrl).post(requestBody);
                break;
            case HttpType.REQUEST_PUT:
                break;
            case HttpType.REQUEST_DELETE:
                break;
        }

        if(null != properties)
        {
            for (Map.Entry<String,String> entry : properties.entrySet())
            {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        builder.tag(tag);
        return builder.build();
    }

    /**
     * 该方法的作用:
     * 初始化网络请求结果
     * @date 2013-12-1
     * @return
     */
    protected TGHttpResult initHttpResult(Context context)
    {
        TGHttpResult httpResult = new TGHttpResult();
        httpResult.setResponseCode(TGHttpError.UNKNOWN_EXCEPTION);
        httpResult.setResult(TGHttpError.getDefaultErrorMsg(context, TGHttpError.UNKNOWN_EXCEPTION));
        return httpResult;
    }

    /**
     * 初始化MultiPart请求参数
     * @param parameters
     * @return
     * @throws IOException
     */
    private RequestBody initRequestBody(TGHttpParams parameters) throws IOException
    {
        if(null == parameters.getFileParams())
        {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for(Map.Entry<String, String> entry :parameters.getStringParams().entrySet())
            {
                builder.add(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }
        else
        {
            MultipartBuilder builder = new MultipartBuilder();
            builder.type(MultipartBuilder.FORM);
            for(Map.Entry<String, String> entry :parameters.getStringParams().entrySet())
            {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }

            File file = null;
            for(Map.Entry<String, String> entry :parameters.getFileParams().entrySet())
            {
                file = new File(entry.getValue());
                builder.addFormDataPart(entry.getKey(), file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file));
            }
            return builder.build();
        }
    }
}
