package com.mn.tiger.thirdparty.google;

import com.mn.tiger.app.TGApplication;
import com.mn.tiger.request.TGHttpLoader;
import com.mn.tiger.request.receiver.TGHttpResult;

/**
 * Google地址解析功能
 */
public class GoogleGeoCoding
{
	/**
	 * 执行地址解析
	 * @param latitude
	 * @param longitude
	 * @param listener
	 */
	public static void geoCoding(final double latitude, final double longitude,
			final GeoCodeListener listener)
	{
        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude
                + "," + longitude + "&sensor=false";

        TGHttpLoader<GoogleGeoCodeResult> httpLoader = new TGHttpLoader<GoogleGeoCodeResult>();
        httpLoader.loadByGet(TGApplication.getInstance(), url, GoogleGeoCodeResult.class,
                new TGHttpLoader.OnLoadCallback<GoogleGeoCodeResult>()
                {
                    @Override
                    public void onLoadStart()
                    {
                    }

                    @Override
                    public void onLoadSuccess(GoogleGeoCodeResult geoCodeResult, TGHttpResult tgHttpResult)
                    {
                        listener.onGeoCodingSuccess(geoCodeResult);
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

	/**
	 * 地址回调接口
	 */
	public static interface GeoCodeListener
	{
		/**
		 * 地址解析成功
		 * @param result
		 */
		void onGeoCodingSuccess(GoogleGeoCodeResult result);

		/**
		 * 地址解析失败
		 * @param code
		 * @param message
		 */
		void onGeoCodingError(int code, String message);
	}

}
