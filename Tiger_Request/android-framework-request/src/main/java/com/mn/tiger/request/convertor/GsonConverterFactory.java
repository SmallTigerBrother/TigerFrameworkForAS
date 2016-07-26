package com.mn.tiger.request.convertor;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * A {@linkplain Converter.Factory converter} which uses Gson for JSON.
 * <p/>
 * Because Gson is so flexible in the types it supports, this converter assumes that it can handle
 * all types. If you are mixing JSON serialization with something else (such as protocol buffers),
 * you must {@linkplain Retrofit.Builder#addConverterFactory(Converter.Factory) add this instance}
 * last to allow the other converters a chance to see their types.
 */
public class GsonConverterFactory extends Converter.Factory
{
    private final Gson gson;

    private Class gsonResponseBodyConverterClass;

    private Class gsonRequestBodyConverterClass;

    public GsonConverterFactory(Gson gson)
    {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    public  <T extends GsonResponseBodyConverter> void setResponseBodyConverter(Class<T> clazz)
    {
        this.gsonResponseBodyConverterClass = clazz;
    }

    public <T extends GsonRequestBodyConverter> void setRequestBodyConverter(Class<T> clazz)
    {
        this.gsonRequestBodyConverterClass = clazz;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit)
    {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        GsonResponseBodyConverter converter = null;
        if(null == gsonResponseBodyConverterClass)
        {
            gsonResponseBodyConverterClass = GsonResponseBodyConverter.class;
        }

        try
        {
            converter = (GsonResponseBodyConverter) gsonResponseBodyConverterClass.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(null != converter)
        {
            converter.setType(type);
            converter.setTypeAdapter(adapter);
        }
        return converter;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit)
    {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        GsonRequestBodyConverter converter = null;
        if(null == gsonRequestBodyConverterClass)
        {
            gsonRequestBodyConverterClass = GsonRequestBodyConverter.class;
        }

        try
        {
            converter = (GsonRequestBodyConverter) gsonRequestBodyConverterClass.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(null != converter)
        {
            converter.setGson(gson);
            converter.setType(type);
            converter.setTypeAdapter(adapter);
        }
        return converter;
    }
}