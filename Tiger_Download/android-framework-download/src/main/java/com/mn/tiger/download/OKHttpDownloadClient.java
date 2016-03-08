package com.mn.tiger.download;

import android.content.Context;

import com.mn.tiger.log.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by peng on 15/9/27.
 */
public class OKHttpDownloadClient extends TGDownloadHttpClient
{
    private static final Logger LOG = Logger.getLogger(OKHttpDownloadClient.class);

    private static OkHttpClient okHttpClient =
            new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS).build();

    private Response response;

    private Call call;

    public OKHttpDownloadClient(Context context, TGDownloader downloader, TGDownloadTask downloadTask)
    {
        super(context, downloader, downloadTask);
    }

    @Override
    protected void executeHttpConnect()
    {
        try
        {
            LOG.d("[Method:executeHttpConnect] url : " + downloader.getUrl() + "\n" + "params : " + downloader.getParams().toString() + "\n");

            Request request = buildRequest(downloader.getRequestType(), downloader.getUrl(), downloader.getParams(), null);
            call = okHttpClient.newCall(request);
            response = call.execute();
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
        FormBody.Builder builder = new FormBody.Builder();
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
        return response.body().contentLength();
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
        call.cancel();
    }
}
