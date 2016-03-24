package com.mn.tiger.request;

import com.google.gson.TypeAdapter;
import com.mn.tiger.log.Logger;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T>
{
    private static final Logger LOG = Logger.getLogger(GsonResponseBodyConverter.class);

    private TypeAdapter<T> adapter;

    public GsonResponseBodyConverter()
    {
    }

    void setTypeAdapter(TypeAdapter<T> adapter)
    {
        this.adapter = adapter;
    }

    public TypeAdapter<T> getAdapter()
    {
        return adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException
    {
        try
        {
            String rawData = value.string();
            LOG.i("[Method:convert] result = " + rawData);
            T result = adapter.fromJson(rawData);
            if (result instanceof TGResult)
            {
                ((TGResult) result).setRawData(rawData);
            }
            return result;
        }
        finally
        {
            value.close();
        }
    }
}
