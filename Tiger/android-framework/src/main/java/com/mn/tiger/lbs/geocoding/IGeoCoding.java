package com.mn.tiger.lbs.geocoding;

import com.mn.tiger.lbs.location.TGLocation;

/**
 * Created by peng on 15/11/18.
 */
public interface IGeoCoding
{
     void geoCoding(final double latitude, final double longitude, final GeoCodeListener listener);

    /**
     * 地址回调接口
     */
    interface GeoCodeListener
    {
        /**
         * 地址解析成功
         * @param location
         */
        void onGeoCodingSuccess(TGLocation location);

        /**
         * 地址解析失败
         * @param code
         * @param message
         */
        void onGeoCodingError(int code, String message);
    }
}
