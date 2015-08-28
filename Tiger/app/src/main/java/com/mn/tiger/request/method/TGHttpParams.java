package com.mn.tiger.request.method;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.request.method.ProgressEntiryWrapper.ProgressListener;

/**
 * 网络请求参数
 */
public class TGHttpParams extends ConcurrentHashMap<String, HashMap<String, String>>
{
    private static final String LOG_TAG = TGHttpParams.class.getSimpleName();

    private static final long serialVersionUID = 1L;

    private static String ENCODING = "UTF-8";

    private ProgressListener progressListener;

    private long contentLength = 0;

    /**
     * 设置字符串参数
     * @param params
     */
    public void setStringParams(HashMap<String, String> params)
    {
        this.put("string_param", params);
    }

    /**
     * 获取字符串参数
     * @return
     */
    public HashMap<String, String> getStringParams()
    {
        return this.get("string_param");
    }

    /**
     * 设置文件参数
     * @param params
     */
    public void setFileParams(HashMap<String, String> params)
    {
        this.put("file_param", params);
    }

    /**
     * 获取文件参数
     * @return
     */
    public HashMap<String, String> getFileParams()
    {
        return this.get("file_param");
    }

    /**
     * 将字符串参数合并成键值对
     * @return
     * @throws UnsupportedEncodingException
     */
    public String mergeStringParams2KeyValuePair() throws UnsupportedEncodingException
    {
        StringBuffer urlBuffer = new StringBuffer();
        Map<String, String> StrParams = getStringParams();

        if(null != StrParams)
        {
            Object[] paraKeys = StrParams.keySet().toArray();
            int parameterLength = paraKeys.length;

            for (int i = 0; i < parameterLength; i++)
            {
                urlBuffer.append(URLEncoder.encode((String) paraKeys[i], ENCODING));
                urlBuffer.append("=");
                urlBuffer.append(URLEncoder.encode(StrParams.get(paraKeys[i]), ENCODING));
                if (i != (parameterLength - 1))
                {
                    urlBuffer.append("&");
                }
            }
        }

        return urlBuffer.toString();
    }

    /**
     * Returns an HttpEntity containing all request parameters
     */
    public HttpEntity getEntity()
    {
        HttpEntity entity = null;

        HashMap<String, String> stringParams = getStringParams();
        HashMap<String, String> fileParams = getFileParams();

        if (null != fileParams && !fileParams.isEmpty())
        {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.setCharset(Charset.forName(HTTP.UTF_8));
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            // Add string params
            for (ConcurrentHashMap.Entry<String, String> entry : stringParams.entrySet())
            {
                multipartEntityBuilder.addTextBody(entry.getKey(), entry.getValue());
            }

            // Add file params
            try
            {
                for (HashMap.Entry<String, String> entry : fileParams.entrySet())
                {
                    multipartEntityBuilder.addBinaryBody(entry.getKey(),new File(entry.getValue()));
                }
            }
            catch (Exception e)
            {
                LogTools.d(LOG_TAG, e.getMessage(), e);
            }

            entity = multipartEntityBuilder.build();

            if(null != progressListener)
            {
                entity = new ProgressEntiryWrapper(entity,this.progressListener);
            }
        }
        else
        {
            try
            {
                entity = new UrlEncodedFormEntity(getParamsList(), ENCODING);
            }
            catch (UnsupportedEncodingException e)
            {
                LogTools.d(LOG_TAG, e.getMessage(), e);
            }
        }

        contentLength = entity.getContentLength();
        return entity;
    }

    protected List<BasicNameValuePair> getParamsList()
    {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for (ConcurrentHashMap.Entry<String, String> entry : getStringParams().entrySet())
        {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return lparams;
    }

    public void setProgressListener(ProgressListener progressListener)
    {
        this.progressListener = progressListener;
    }

    public long getContentLength()
    {
        return contentLength;
    }

    public static String appendParams2Url(String url, TGHttpParams params)
    {
        String paramsString = null;
        if (params instanceof Map<?, ?>)
        {
            try
            {
                paramsString = ((TGHttpParams)params).mergeStringParams2KeyValuePair();
            }
            catch (UnsupportedEncodingException e)
            {
                LogTools.e(LOG_TAG, e);
            }
        }

        if (!TextUtils.isEmpty(paramsString))
        {
            if (url.indexOf("?") < 0)
            {
                url = url + "?";
            }
            url = url + paramsString;;
        }
        Log.i("url:", url);
        return url;
    }
}

