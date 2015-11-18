package com.mn.tiger.thirdparty.google;

import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.lbs.geocoding.IGeoCoding;
import com.mn.tiger.lbs.location.TGLocation;
import com.mn.tiger.request.TGHttpLoader;
import com.mn.tiger.request.receiver.TGHttpResult;

/**
 * Google地址解析功能
 */
public class GoogleGeoCoding implements IGeoCoding
{
	/**
	 * 执行地址解析
	 * @param latitude
	 * @param longitude
	 * @param listener
	 */
	public void geoCoding(final double latitude, final double longitude,
			final GeoCodeListener listener)
	{
        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude
                + "," + longitude + "&sensor=false";

        TGHttpLoader<GoogleGeoCodeResult> httpLoader = new TGHttpLoader<GoogleGeoCodeResult>();
        httpLoader.loadByGet(TGApplicationProxy.getInstance().getApplication(), url, GoogleGeoCodeResult.class,
                new TGHttpLoader.OnLoadCallback<GoogleGeoCodeResult>()
                {
                    @Override
                    public void onLoadStart()
                    {
                    }

                    @Override
                    public void onLoadSuccess(GoogleGeoCodeResult geoCodeResult, TGHttpResult tgHttpResult)
                    {
                        if(null != geoCodeResult && null != geoCodeResult.getResults() && geoCodeResult.getResults().length > 0)
                        {
                            GoogleAddressResult addressResult = geoCodeResult.getResults()[0];
                            addressResult.setLatitude(latitude);
                            addressResult.setLongitude(longitude);

                            TGLocation location = addressResult.convert2Location();
                            location.setTime(System.currentTimeMillis());

                            listener.onGeoCodingSuccess(location);
                        }
                    }

                    @Override
                    public void onLoadError(int i, String s, TGHttpResult tgHttpResult)
                    {
                        if (null != listener)
                        {
                            listener.onGeoCodingError(i, s);
                        }
                    }

                    @Override
                    public void onLoadCache(GoogleGeoCodeResult geoCodeResult, TGHttpResult tgHttpResult)
                    {
                    }

                    @Override
                    public void onLoadOver()
                    {
                    }
                });
	}



}
