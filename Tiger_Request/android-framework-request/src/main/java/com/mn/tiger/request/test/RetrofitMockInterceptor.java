package com.mn.tiger.request.test;

import android.text.TextUtils;

import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.log.Logger;
import com.mn.tiger.request.TGMediaType;
import com.mn.tiger.utility.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Dalang on 2016/6/7.
 */
public class RetrofitMockInterceptor implements Interceptor
{
    private static final Logger LOG = Logger.getLogger(RetrofitMockInterceptor.class);

    public static boolean TEST_ABLE = false;

    public static HashMap<String, String> dataMap = new HashMap<String, String>();

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();

        Response response = getMockTestData(request);

        return response;
    }

    /**
     * 添加mock测试数据
     * @param url
     * @param mockFileName
     * @param paramKey
     */
    public static void addMockTestData(String url, String mockFileName, String... paramKey)
    {
        url = generateMockDataUrlParamKey(url, paramKey);
        LOG.i("[Method:addMockTestData] url == " + url + " mockFileName == " + mockFileName);
        dataMap.put(url, mockFileName);
    }

    /**
     * 根据url、params获取测试数据
     * @param request
     * @return
     */
    public static Response getMockTestData(Request request)
    {
        String url = urlAppendParamKey(request);
        LOG.i("[Method:getMockTestData] url == " + url);
        String dataKey = dataMap.get(url);
        String data = FileUtils.readStringFromAsset(TGApplicationProxy.getApplication(), dataKey);

        Response response = new Response.Builder()
                .code(200)
                .message("Success")
                .request(request)
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse(TGMediaType.APPLICATION_JSON), data))
                .addHeader("content-type",TGMediaType.APPLICATION_JSON)
                .build();
        return response;
    }

    /**
     * 判断某个接口是不是支持mock测试
     * @param request
     * @return
     */
    public static boolean isMockable(Request request)
    {
        if(null != dataMap && TEST_ABLE)
        {
            String url = urlAppendParamKey(request);

            String dataKey = dataMap.get(url);
            return !TextUtils.isEmpty(dataKey);
        }

        return false;
    }

    private static String urlAppendParamKey(Request request)
    {
        if(request.method().equalsIgnoreCase("GET"))
        {
            return generateGetMethodUrlParamKey(request.url());
        }
        else if (request.method().equalsIgnoreCase("POST"))
        {
            RequestBody requestBody = request.body();
            if (requestBody instanceof FormBody)
            {
                return generateFormEncodedUrlParamKey(request);
            }
        }

        return "";
    }

    private static String generateGetMethodUrlParamKey(HttpUrl httpUrl)
    {
        String url = httpUrl.toString();
        int questionIndex = url.indexOf('?');
        if(questionIndex > -1)
        {
            url = url.substring(0, questionIndex);
        }

        StringBuilder resultBuilder = new StringBuilder(url);

        Set<String> paramNames = httpUrl.queryParameterNames();
        if(null != paramNames)
        {
            for (String paramName : paramNames)
            {
                resultBuilder.append('&');
                resultBuilder.append(paramName);
            }
        }

        return resultBuilder.toString();
    }

    private static String generateFormEncodedUrlParamKey(Request request)
    {
        StringBuilder resultBuilder = new StringBuilder(request.url().toString());
        FormBody formBody = (FormBody) request.body();
        int size = formBody.size();
        for (int i = 0; i < size; i++)
        {
            resultBuilder.append('&');
            resultBuilder.append(formBody.name(i));
        }
        return resultBuilder.toString();
    }

    private static String generateMockDataUrlParamKey(String url, String[] paramKeys)
    {
        StringBuilder resultBuilder = new StringBuilder(url);
        if(null != paramKeys)
        {
            for (String paramName : paramKeys)
            {
                resultBuilder.append('&');
                resultBuilder.append(paramName);
            }
        }
        return resultBuilder.toString();
    }
}
