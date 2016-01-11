package com.mn.tiger.core;

import android.text.TextUtils;

import com.mn.tiger.log.Logger;

import java.util.HashMap;


/**
 * Created by peng on 15/11/3.
 */
public class TGUri
{
    public static final Logger LOG = Logger.getLogger(TGUri.class);

    public static final TGUri URI_HTTP = TGUri.parse("http://");

    public static final TGUri URI_HTTPS =TGUri.parse("https://");

    private String string = "";

    private String scheme = "";

    private String host = "";

    private String paramString = "";

    private HashMap<String,String> params;

    public static TGUri parse(String string)
    {
        int indexOfSeparator = string.indexOf("://");
        int indexOfQuestionMark = string.indexOf("?");

        TGUri uri = new TGUri();
        uri.string = string;
        if(indexOfSeparator > -1)
        {
            //解析scheme
            uri.scheme = string.substring(0, indexOfSeparator);

            //解析host
            if(indexOfQuestionMark > -1)
            {
                uri.host = string.substring(indexOfSeparator + 3, indexOfQuestionMark);
                uri.paramString = string.substring(indexOfQuestionMark + 1);
                //解析params键值对
                if(!TextUtils.isEmpty(uri.paramString))
                {
                    parseParams(uri);
                }
            }
            else
            {
                uri.host = string.substring(indexOfSeparator + 3);
            }

            return uri;
        }
        else
        {
            if(indexOfQuestionMark > -1)
            {
                uri.paramString = string.substring(indexOfQuestionMark + 1);
                //解析params键值对
                if(!TextUtils.isEmpty(uri.paramString))
                {
                    parseParams(uri);
                }
            }
            else
            {
                uri.paramString = string;
                //解析params键值对
                if(!TextUtils.isEmpty(uri.paramString))
                {
                    parseParams(uri);
                }
            }
            LOG.w("[Method:parse] invalidate uri =  " + string);
            return uri;
        }
    }

    private static void parseParams(TGUri uri)
    {
        uri.params = new HashMap<String, String>();
        String[] keyValues = uri.paramString.split("&");
        int count = keyValues.length;
        String[] keyAndValue;
        for (int i = 0; i < count; i++)
        {
            if (keyValues[i].indexOf("=") > -1)
            {
                keyAndValue = keyValues[i].split("=");
                if(keyAndValue.length > 1)
                {
                    uri.params.put(keyAndValue[0], keyAndValue[1]);
                }
                else
                {
                    uri.params.put(keyAndValue[0], "");
                }
            }
        }
    }

    public boolean isPathPrefixMatch(TGUri uri)
    {
        if(this.getScheme().toLowerCase().equals(uri.getScheme().toLowerCase()) &&
                this.getHost().toLowerCase().equals(uri.getHost().toLowerCase()))
        {
            return true;
        }

        return false;
    }

    public String getScheme()
    {
        return scheme;
    }

    public String getHost()
    {
        return host;
    }

    public String getParamString()
    {
        return paramString;
    }

    public String getParamByKey(String key)
    {
        if(null != params)
        {
            return params.get(key);
        }

        return null;
    }

    @Override
    public String toString()
    {
        return this.string;
    }
}
