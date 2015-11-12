package com.mn.tiger.lbs.location;

import com.mn.tiger.lbs.map.TGLocation;

/**
 * Created by Dalang on 2015/7/30.
 */
public interface ILocationManager
{
    void requestLocationUpdates();

    void removeLocationUpdates();

    void destroy();

    void setLocationListener(ILocationListener listener);

    boolean isLocationInChina(TGLocation location);

    interface ILocationListener
    {
        void onReceiveLocation(TGLocation location);
    }

    enum Provider
    {
        BaiDu,
        Google,
        AMap
    }
}
