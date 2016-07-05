package com.mn.tiger.thirdparty.google;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Tiger on 16/7/4.
 */
public interface GeoCodingService
{
    @GET("geocode/json")
    Call<GoogleGeoCodeResult> geoCoding(@Query("latlng") String latlng, @Query("sensor") boolean sensor);
}
