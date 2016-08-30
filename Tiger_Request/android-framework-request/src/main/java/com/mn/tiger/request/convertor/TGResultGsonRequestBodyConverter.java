package com.mn.tiger.request.convertor;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

public class TGResultGsonRequestBodyConverter<T> implements Converter<T, RequestBody>
{
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private Gson gson;
    private TypeAdapter<T> adapter;
    private Type type;

    public TGResultGsonRequestBodyConverter()
    {
    }

    void setGson(Gson gson)
    {
        this.gson = gson;
    }

    void setTypeAdapter(TypeAdapter<T> adapter)
    {
        this.adapter = adapter;
    }

    void setType(Type type)
    {
        this.type = type;
    }

    public Gson getGson()
    {
        return gson;
    }

    public TypeAdapter<T> getTypeAdapter()
    {
        return adapter;
    }

    public Type getType()
    {
        return type;
    }

    @Override
    public RequestBody convert(T value) throws IOException
    {
        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        JsonWriter jsonWriter = gson.newJsonWriter(writer);
        adapter.write(jsonWriter, value);
        jsonWriter.close();
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }
}
