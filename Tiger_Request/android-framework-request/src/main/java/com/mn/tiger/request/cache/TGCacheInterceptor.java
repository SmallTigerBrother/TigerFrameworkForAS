package com.mn.tiger.request.cache;

import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.utility.NetworkUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Tiger on 16/8/29.
 */
public class TGCacheInterceptor implements Interceptor
{
    private static CacheControl FORCE_CACHE;

    private int maxAgeWithNetwork = 0;

    public TGCacheInterceptor()
    {
        FORCE_CACHE = CacheControl.FORCE_CACHE;
    }

    public void setMaxAgeWithNetwork(int maxAgeWithNetwork)
    {
        this.maxAgeWithNetwork = maxAgeWithNetwork;
    }

    public void setMaxStaleNoneNetwork(int maxStaleNoneNetwork)
    {
        FORCE_CACHE = new CacheControl.Builder().onlyIfCached().maxStale(maxStaleNoneNetwork,
                TimeUnit.SECONDS).build();
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        if(!NetworkUtils.isConnectivityAvailable(TGApplicationProxy.getApplication()))
        {
            request = request.newBuilder().cacheControl(FORCE_CACHE).build();
            Response response = chain.proceed(request);
            return response.newBuilder().build();
        }
        else
        {
            Response response = chain.proceed(request);
            return response.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAgeWithNetwork).build();
        }
    }
}
