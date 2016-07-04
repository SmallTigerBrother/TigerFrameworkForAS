package com.mn.tiger.thirdparty.google;

import com.mn.tiger.lbs.geocoding.IGeoCoding;
import com.mn.tiger.lbs.location.TGLocation;
import com.mn.tiger.log.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Google地址解析功能
 */
public class GoogleGeoCoding implements IGeoCoding
{
    private static final Logger LOG = Logger.getLogger(GoogleGeoCoding.class);

    private static Retrofit retrofit;

    private static Retrofit getRetrofit()
    {
        if(null == retrofit)
        {
            retrofit = new Retrofit.Builder().baseUrl("http://maps.googleapis.com/maps/api/").build();
        }
        return retrofit;
    }

	/**
	 * 执行地址解析
	 * @param latitude
	 * @param longitude
	 * @param listener
	 */
	public void geoCoding(final double latitude, final double longitude,
			final IGeoCodeListener listener)
	{
        LOG.i("[Method:geoCoding] latitde == " + latitude + " longitude == " + longitude);
        getRetrofit().create(GeoCodingService.class).geoCoding(latitude, longitude).enqueue(new Callback<GoogleGeoCodeResult>()
        {
            @Override
            public void onResponse(Call<GoogleGeoCodeResult> call, Response<GoogleGeoCodeResult> response)
            {
                LOG.i("[Method:geoCoding:onResponse] " + response.body());
                if(null != response.body() && null != response.body().getResults() && response.body().getResults().length > 0)
                {
                    GoogleAddressResult addressResult = response.body().getResults()[0];
                    addressResult.setLatitude(latitude);
                    addressResult.setLongitude(longitude);

                    TGLocation location = addressResult.convert2Location();
                    location.setTime(System.currentTimeMillis());

                    listener.onGeoCodingSuccess(location);
                }
            }

            @Override
            public void onFailure(Call<GoogleGeoCodeResult> call, Throwable t)
            {
                if (null != listener)
                {
                    listener.onGeoCodingError(0, t.getMessage());
                }
            }
        });
	}
}
