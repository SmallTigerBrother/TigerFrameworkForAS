package com.mn.tiger.request;

import com.google.gson.TypeAdapter;
import com.mn.tiger.log.Logger;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final Logger LOG = Logger.getLogger(GsonResponseBodyConverter.class);

    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override public T convert(ResponseBody value) throws IOException {
        try {
            String result = value.string();
            LOG.i("[Method:convert] result = " + result);
            return adapter.fromJson(result);
        } finally {
            value.close();
        }
    }
}
