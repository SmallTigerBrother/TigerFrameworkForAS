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

        if(indexOfSeparator > -1)
        {
            TGUri leKuUri = new TGUri();
            leKuUri.string = string;
            //解析scheme
            leKuUri.scheme = string.substring(0, indexOfSeparator);

            if(indexOfSeparator < string.length())
            {
                //解析host
                if(indexOfQuestionMark > -1)
                {
                    leKuUri.host = string.substring(indexOfSeparator + 3, indexOfQuestionMark);
                    if(indexOfQuestionMark < string.length())
                    {
                        leKuUri.paramString = string.substring(indexOfQuestionMark + 1);
                        //解析params键值对
                        if(!TextUtils.isEmpty(leKuUri.paramString))
                        {
                            leKuUri.params = new HashMap<String, String>();
                            String[] keyValues = leKuUri.paramString.split("&");
                            int count = keyValues.length;
                            String[] keyAndValue;
                            for (int i = 0; i < count; i++)
                            {
                                if (keyValues[i].indexOf("=") > -1)
                                {
                                    keyAndValue = keyValues[i].split("=");
                                    if(keyAndValue.length > 1)
                                    {
                                        leKuUri.params.put(keyAndValue[0], keyAndValue[1]);
                                    }
                                    else
                                    {
                                        leKuUri.params.put(keyAndValue[0], "");
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    leKuUri.host = string.substring(indexOfSeparator + 2);
                }
            }

            return leKuUri;
        }
        else
        {
            LOG.e("[Method:parse] can not parse to uri with String " + string);
        }

        return null;
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
