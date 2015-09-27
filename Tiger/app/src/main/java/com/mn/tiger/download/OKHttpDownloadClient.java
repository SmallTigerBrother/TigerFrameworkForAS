package com.mn.tiger.download;

import android.content.Context;

import com.mn.tiger.log.Logger;
import com.mn.tiger.request.HttpType;
import com.mn.tiger.request.method.TGHttpParams;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by peng on 15/9/27.
 */
public class OKHttpDownloadClient extends TGDownloadHttpClient
{
    private static final Logger LOG = Logger.getLogger(OKHttpDownloadClient.class);

    private int tag = -1;

    private static OkHttpClient okHttpClient = new OkHttpClient();

    {
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(60, TimeUnit.SECONDS);
    }

    private Response response;

    public OKHttpDownloadClient(Context context, TGDownloader downloader, TGDownloadTask downloadTask)
    {
        super(context, downloader, downloadTask);
        this.tag = downloadTask.getTaskID();
    }

    @Override
    protected void executeHttpConnect()
    {
        try
        {
            LOG.d("[Method:executeHttpConnect] url : " + downloader.getUrl() + "\n" + "params : " + downloader.getParams().toString() + "\n");

            Request request = buildRequest(downloader.getRequestType(), downloader.getUrl(), downloader.getParams(), null);
            response = okHttpClient.newCall(request).execute();
        }
        catch (IOException e)
        {
            LOG.e("[Method:executeHttpConnect]", e);
        }
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
    private Request buildRequest(int httpType, String requestUrl, HashMap<String, String> parameters,
                                 Map<String, String> properties) throws IOException
    {
        Request.Builder builder = new Request.Builder();
        switch (httpType)
        {
            case HttpType.REQUEST_GET:
                builder.url(TGHttpParams.appendParams2Url(requestUrl, parameters)).get();
                break;
            case HttpType.REQUEST_POST:
                RequestBody requestBody = initFormDataRequestBody(parameters);
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
     * 初始化MultiPart请求参数
     * @param parameters
     * @return
     * @throws IOException
     */
    private RequestBody initFormDataRequestBody(HashMap<String, String> parameters) throws IOException
    {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for(Map.Entry<String, String> entry : parameters.entrySet())
        {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    @Override
    protected int getResponseCode()
    {
        return response.code();
    }

    @Override
    protected long getContentLength()
    {
        try
        {
            return response.body().contentLength();
        }
        catch (IOException e)
        {
            LOG.e("[Method:getContentLength]", e);
            return 0;
        }
    }

    @Override
    protected InputStream getDownloadInputStream() throws IOException
    {
        return response.body().byteStream();
    }

    @Override
    protected String getAcceptRangesFromResponseHeaders()
    {
        return response.header("Accept-Ranges", "");
    }

    @Override
    protected String getFileNameFromResponseHeaders()
    {
        String serverFileName = response.header("Content-Disposition", "");
        serverFileName = serverFileName.replace("attachment;filename=", "");
        LOG.d("[Method:getFileNameFromResponseHeaders]  serverFileName:" + serverFileName);

        return serverFileName;
    }

    @Override
    protected void onRetry(TGDownloader downloader)
    {
        executeHttpConnect();
        handleResponse();
    }

    @Override
    public void cancel()
    {
        okHttpClient.cancel(tag);
    }
}
