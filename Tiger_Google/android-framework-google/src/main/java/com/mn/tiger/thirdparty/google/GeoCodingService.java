package com.mn.tiger.thirdparty.google;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Tiger on 16/7/4.
 */
public interface GeoCodingService
{
    @GET("geocode/json?latlng={latitude},{longitude}&sensor=false")
    Call<GoogleGeoCodeResult> geoCoding(@Query("latitude")double latitude, @Query("longitude")double longitude);
}
