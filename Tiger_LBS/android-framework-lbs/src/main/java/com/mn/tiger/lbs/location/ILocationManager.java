package com.mn.tiger.lbs.location;

/**
 * Created by Dalang on 2015/7/30.
 */
public interface ILocationManager
{
    void requestLocationUpdates();

    void removeLocationUpdates();

    void onDestroy();

    void setLocationListener(ILocationListener listener);

    TGLocation getLastLocation();

    boolean isLocationInChina(TGLocation location);

    interface ILocationListener
    {
        void onReceiveLocation(TGLocation location);

        void onLocationPermissionDeny();

        void onProviderDisabled(boolean isGPSDisabled, boolean isNetWorkDisable);
    }

    enum Provider
    {
        BaiDu,
        Google,
        AMap
    }
}
