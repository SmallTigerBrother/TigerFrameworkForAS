package com.mn.tiger.request;

import com.google.gson.TypeAdapter;
import com.mn.tiger.log.Logger;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T>
{
    private static final Logger LOG = Logger.getLogger(GsonResponseBodyConverter.class);

    private TypeAdapter<T> adapter;

    private Type type;

    public GsonResponseBodyConverter()
    {
    }

    void setTypeAdapter(TypeAdapter<T> adapter)
    {
        this.adapter = adapter;
    }

    void setType(Type type)
    {
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    public TypeAdapter<T> getAdapter()
    {
        return adapter;
    }

    @Override
    public final T convert(ResponseBody value) throws IOException
    {
        try
        {
            return convert(value, value.string(), adapter, type);
        }
        finally
        {
            value.close();
        }
    }

    public T convert(ResponseBody value,String rawData, TypeAdapter<T> adapter, Type type)
    {
        try
        {
            T result = null;
            result = adapter.fromJson(rawData);
            if (result instanceof TGResult)
            {
                ((TGResult) result).setRawData(rawData);
            }
            return result;
        }
        catch (Exception e)
        {
            LOG.e("[Method:convert] " + e.getMessage() + "\n" + rawData);
            return null;
        }
    }
}
