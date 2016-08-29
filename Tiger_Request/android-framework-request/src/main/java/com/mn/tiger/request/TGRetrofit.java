package com.mn.tiger.request;

import com.mn.tiger.log.Logger;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.CookieJar;
import okhttp3.Dispatcher;
import okhttp3.Dns;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Dalang on 2016/3/6.
 * Retrofit工具类
 */
public class TGRetrofit
{
    private static final Logger LOG = Logger.getLogger(TGRetrofit.class);

    private Retrofit RETROFIT;

    private TGRetrofit()
    {

    }

    public <T> T create(Class<T> serviceClass)
    {
        return RETROFIT.create(serviceClass);
    }

    public static class Builder
    {
        private Retrofit.Builder retrofitBuilder;

        private OkHttpClient.Builder okHttpClientBuilder;

        public Builder()
        {
            retrofitBuilder = new Retrofit.Builder();
            okHttpClientBuilder = new OkHttpClient.Builder();
        }

        /*********************以下是Retrofit2的配置***************************/

        public Builder baseUrl(String baseUrl)
        {
            retrofitBuilder.baseUrl(baseUrl);
            return this;
        }

        public Builder addConverterFactory(Converter.Factory factory)
        {
            retrofitBuilder.addConverterFactory(factory);
            return this;
        }

        public Builder addCallAdapterFactory(CallAdapter.Factory factory)
        {
            retrofitBuilder.addCallAdapterFactory(factory);
            return this;
        }

        public Builder callFactory(okhttp3.Call.Factory factory)
        {
            retrofitBuilder.callFactory(factory);
            return this;
        }

        public Builder callbackExecutor(Executor executor)
        {
            retrofitBuilder.callbackExecutor(executor);
            return this;
        }

        public Builder validateEagerly(boolean validateEagerly)
        {
            retrofitBuilder.validateEagerly(validateEagerly);
            return this;
        }

        /**********************以下是OKHttp配置*********************/

        public Builder connectTimeout(long timeout, TimeUnit unit)
        {
            okHttpClientBuilder.connectTimeout(timeout, unit);
            return this;
        }

        public Builder readTimeout(long timeout, TimeUnit unit)
        {
            okHttpClientBuilder.readTimeout(timeout, unit);
            return this;
        }

        public Builder writeTimeout(long timeout, TimeUnit unit)
        {
            okHttpClientBuilder.writeTimeout(timeout, unit);
            return this;
        }

        public Builder retryOnConnectionFailure(boolean retryOnConnectionFailure)
        {
            okHttpClientBuilder.retryOnConnectionFailure(retryOnConnectionFailure);
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor)
        {
            okHttpClientBuilder.addInterceptor(interceptor);
            return this;
        }

        public Builder addNetworkInterceptor(Interceptor interceptor)
        {
            okHttpClientBuilder.addNetworkInterceptor(interceptor);
            return this;
        }

        public Builder setCache(Cache cache)
        {
            okHttpClientBuilder.cache(cache);
            return this;
        }

        public Builder proxy(Proxy proxy)
        {
            okHttpClientBuilder.proxy(proxy);
            return this;
        }

        public Builder proxySelector(ProxySelector proxySelector)
        {
            okHttpClientBuilder.proxySelector(proxySelector);
            return this;
        }

        public Builder cookieJar(CookieJar cookieJar)
        {
            okHttpClientBuilder.cookieJar(cookieJar);
            return this;
        }

        public Builder dns(Dns dns)
        {
            okHttpClientBuilder.dns(dns);
            return this;
        }

        public Builder socketFactory(SocketFactory socketFactory)
        {
            okHttpClientBuilder.socketFactory(socketFactory);
            return this;
        }

        public Builder sslSocketFactory(
                SSLSocketFactory sslSocketFactory, X509TrustManager trustManager)
        {
            okHttpClientBuilder.sslSocketFactory(sslSocketFactory, trustManager);
            return this;
        }

        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier)
        {
            okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
            return this;
        }

        public Builder certificatePinner(CertificatePinner certificatePinner)
        {
            okHttpClientBuilder.certificatePinner(certificatePinner);
            return this;
        }

        public Builder authenticator(Authenticator authenticator)
        {
            okHttpClientBuilder.authenticator(authenticator);
            return this;
        }

        public Builder proxyAuthenticator(Authenticator proxyAuthenticator)
        {
            okHttpClientBuilder.proxyAuthenticator(proxyAuthenticator);
            return this;
        }

        public Builder connectionPool(ConnectionPool connectionPool)
        {
            okHttpClientBuilder.connectionPool(connectionPool);
            return this;
        }

        public Builder followSslRedirects(boolean followProtocolRedirects)
        {
            okHttpClientBuilder.followSslRedirects(followProtocolRedirects);
            return this;
        }

        public Builder followRedirects(boolean followRedirects)
        {
            okHttpClientBuilder.followRedirects(followRedirects);
            return this;
        }

        public Builder dispatcher(Dispatcher dispatcher)
        {
            okHttpClientBuilder.dispatcher(dispatcher);
            return this;
        }

        public Builder protocols(List<Protocol> protocols)
        {
            okHttpClientBuilder.protocols(protocols);
            return this;
        }

        public Builder connectionSpecs(List<ConnectionSpec> connectionSpecs)
        {
            okHttpClientBuilder.connectionSpecs(connectionSpecs);
            return this;
        }

        public TGRetrofit build()
        {
            TGRetrofit retrofit = new TGRetrofit();
            retrofit.RETROFIT = retrofitBuilder.client(okHttpClientBuilder.build()).build();
            return retrofit;
        }
    }


    /**
     * 将RequestBody输出为字符串
     * @param requestBody
     * @return
     */
    protected static String stringify(RequestBody requestBody)
    {
        StringBuilder builder = new StringBuilder();
        if(requestBody instanceof FormBody)
        {
            FormBody formBody = (FormBody) requestBody;
            int size = formBody.size();
            for (int i = 0; i < size; i++)
            {
                builder.append(formBody.encodedName(i));
                builder.append(" = ");
                builder.append(formBody.encodedValue(i));
                builder.append(" ; ");
            }
        }
        return builder.toString();
    }

    /**
     * 想Request中添加参数
     * @param originalRequest
     * @param params
     * @return
     */
    protected static Request appendParams(Request originalRequest, Map<String, String> params)
    {
        if(originalRequest.method().equalsIgnoreCase("GET"))
        {
            //添加基础参数
            HttpUrl.Builder urlBuilder = originalRequest.url().newBuilder();
            Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<String,String> entry = iterator.next();
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            HttpUrl url = urlBuilder.build();
            LOG.i("[Method:appendParams] method = " + originalRequest.method() + " ; url = " + url);
            return originalRequest.newBuilder().url(url).build();
        }
        else if(originalRequest.method().equals("POST"))
        {
            RequestBody requestBody = originalRequest.body();
            if(requestBody instanceof FormBody)
            {
                //添加基础数据
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                FormBody formBody = (FormBody)requestBody;
                int size = formBody.size();
                for (int i = 0;i < size; i++)
                {
                    formBodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                }

                Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();
                while (iterator.hasNext())
                {
                    Map.Entry<String,String> entry = iterator.next();
                    formBodyBuilder.add(entry.getKey(), entry.getValue());
                }
                FormBody newFormBody = formBodyBuilder.build();
                LOG.i("[Method:appendParams] method = " + originalRequest.method() + " ; url = " + originalRequest.url() + " ; params : " + stringify(newFormBody));
                return originalRequest.newBuilder().post(newFormBody).build();
            }
            else if(requestBody instanceof MultipartBody)
            {
                //添加基础数据
                MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
                MultipartBody multipartBody = (MultipartBody)requestBody;
                multipartBodyBuilder.setType(multipartBody.type());
                int size = multipartBody.size();
                for (int i = 0; i < size; i++)
                {
                    multipartBodyBuilder.addPart(multipartBody.part(i));
                }
                //处理multipart/form-data类型，其他类型后续处理
                if(multipartBody.type().equals(MultipartBody.FORM))
                {
                    Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();
                    while (iterator.hasNext())
                    {
                        Map.Entry<String,String> entry = iterator.next();
                        multipartBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                    }
                }
                else
                {
                    FormBody.Builder formBodyBuilder = new FormBody.Builder();
                    Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();
                    while (iterator.hasNext())
                    {
                        Map.Entry<String,String> entry = iterator.next();
                        formBodyBuilder.add(entry.getKey(), entry.getValue());
                    }
                    multipartBodyBuilder.addPart(formBodyBuilder.build());
                }
                return originalRequest.newBuilder().post(multipartBodyBuilder.build()).build();
            }
        }
        else if(originalRequest.method().equalsIgnoreCase("PUT"))
        {
            LOG.i("[Method:appendParams] method = " + originalRequest.method() + " ; url = " + originalRequest.url());
        }
        else if(originalRequest.method().equalsIgnoreCase("DELETE"))
        {
            LOG.i("[Method:appendParams] method = " + originalRequest.method() + " ; url = " + originalRequest.url());
        }
        return originalRequest;
    }
}
