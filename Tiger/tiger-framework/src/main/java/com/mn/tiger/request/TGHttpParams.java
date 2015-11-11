package com.mn.tiger.request;

import android.text.TextUtils;

import com.mn.tiger.log.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网络请求参数
 */
public class TGHttpParams extends ConcurrentHashMap<String, HashMap<String, String>>
{
    private static final Logger LOG = Logger.getLogger(TGHttpParams.class);

    private static final long serialVersionUID = 1L;

    private static final String ENCODING = "UTF-8";

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
    public static String mergeStringParams2KeyValuePair(Map<String, String> parameters) throws UnsupportedEncodingException
    {
        StringBuffer urlBuffer = new StringBuffer();

        if(null != parameters)
        {
            Object[] paraKeys = parameters.keySet().toArray();
            int parameterLength = paraKeys.length;

            for (int i = 0; i < parameterLength; i++)
            {
                urlBuffer.append(URLEncoder.encode((String) paraKeys[i], ENCODING));
                urlBuffer.append("=");
                urlBuffer.append(URLEncoder.encode(parameters.get(paraKeys[i]), ENCODING));
                if (i != (parameterLength - 1))
                {
                    urlBuffer.append("&");
                }
            }
        }

        return urlBuffer.toString();
    }

    public long getContentLength()
    {
        return contentLength;
    }

    public static String appendParams2Url(String url, Map<String, String> parameters)
    {
        String paramsString = null;
        try
        {
            paramsString = mergeStringParams2KeyValuePair(parameters);
        }
        catch (UnsupportedEncodingException e)
        {
            LOG.e("[Method:appendParams2Url] " + e.getMessage());
        }

        if (!TextUtils.isEmpty(paramsString))
        {
            if (url.indexOf("?") < 0)
            {
                url = url + "?";
            }
            url = url + paramsString;
        }
        LOG.d("[Method:appendParams2Url] url:", url);
        return url;
    }
}

