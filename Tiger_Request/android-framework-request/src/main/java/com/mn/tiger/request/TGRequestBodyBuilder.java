package com.mn.tiger.request;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by peng on 16/3/23.
 */
public class TGRequestBodyBuilder
{
    public static RequestBody buildFileBody(String filePath)
    {
        File file = new File(filePath);
        if(file.exists())
        {
            return RequestBody.create(MediaType.parse(TGMediaType.APPLICATION_OCTET_STREAM), file);
        }
        else
        {
            throw new IllegalArgumentException("The file is not exists");
        }
    }

    public static RequestBody buildFileBody(File file)
    {
        if(file.exists())
        {
            return RequestBody.create(MediaType.parse(TGMediaType.APPLICATION_OCTET_STREAM), file);
        }
        else
        {
            throw new IllegalArgumentException("The file is not exists");
        }
    }

    public static RequestBody buildTextPlain(String value)
    {
        return RequestBody.create(MediaType.parse(TGMediaType.TEXT_PLAIN), value);
    }

    public static RequestBody buildTextPlain(int value)
    {
        return buildTextPlain(value + "");
    }

    public static RequestBody buildTextPlain(boolean value)
    {
        return buildTextPlain(value + "");
    }

    public static RequestBody buildTextPlain(long value)
    {
        return buildTextPlain(value + "");
    }

    public static RequestBody buildTextPlain(double value)
    {
        return buildTextPlain(value + "");
    }

    public static RequestBody buildJSON(String value)
    {
        return RequestBody.create(MediaType.parse(TGMediaType.APPLICATION_JSON), value);
    }

}
