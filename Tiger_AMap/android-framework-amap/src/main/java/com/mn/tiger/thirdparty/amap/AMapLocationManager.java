package com.mn.tiger.thirdparty.amap;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.mn.tiger.app.TGApplication;
import com.mn.tiger.lbs.location.ILocationManager;
import com.mn.tiger.lbs.map.TGLocation;
import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.CR;

/**
 * Created by Dalang on 2015/7/26.
 * 百度定位管理类
 */
public class AMapLocationManager implements ILocationManager
{
    private static final Logger LOG = Logger.getLogger(AMapLocationManager.class);

    private LocationManagerProxy locationManagerProxy;

    private ILocationListener listener;

    private AMapLocationListener locationListener = new AMapLocationListener()
    {
        @Override
        public void onLocationChanged(final AMapLocation aMapLocation)
        {
            LOG.i("[Method:onLocationChanged] Provider == " + aMapLocation.getProvider() + "  lat == " +
                    aMapLocation.getLatitude() + "  lng == " + aMapLocation.getLongitude() + " address == " + aMapLocation.getAddress());

            final TGLocation tgLocation = convert2TGLocation(aMapLocation);
            tgLocation.setTime(System.currentTimeMillis());
            listener.onReceiveLocation(tgLocation);
        }

        @Override
        public void onLocationChanged(Location location)
        {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

        @Override
        public void onProviderDisabled(String provider)
        {
        }
    };

    /**
     * 初始化
     */
    AMapLocationManager()
    {
        locationManagerProxy = locationManagerProxy.getInstance(TGApplication.getInstance());
    }

    /**
     * 请求定位
     */
    @Override
    public void requestLocationUpdates()
    {
        locationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1 , 50 , locationListener);
        LOG.i("[Method:requestLocationUpdates]");
    }

    @Override
    public void removeLocationUpdates()
    {
        locationManagerProxy.removeUpdates(locationListener);
    }

    @Override
    public void destroy()
    {
        locationManagerProxy.destroy();
        locationManagerProxy = null;
    }

    @Override
    public void setLocationListener(ILocationListener listener)
    {
        this.listener = listener;
    }

    /**
     * 判断位置是否在中国
     * @param location
     * @return
     */
    public boolean isLocationInChina(TGLocation location)
    {
        if (location.getLongitude() < 72.004 || location.getLongitude() > 137.8347 ||
                location.getLatitude() < 0.8293 || location.getLatitude() > 55.8271)
        {
            return false;
        }

        Context context = TGApplication.getInstance();
        String chinaZH = context.getResources().getString(CR.getStringId(context, "china_zh"));
        String chinaEN = context.getResources().getString(CR.getStringId(context, "china_en"));

        if(!location.getCountry().equalsIgnoreCase(chinaZH) && !location.getCountry().equalsIgnoreCase(chinaEN))
        {
            return false;
        }

        return true;
    }

    /**
     * 转换为TGLocation
     * @param location
     * @return
     */
    public static TGLocation convert2TGLocation(AMapLocation location)
    {
        TGLocation tgLocation = new TGLocation();
        tgLocation.setLatitude(location.getLatitude());
        tgLocation.setLongitude(location.getLongitude());
        tgLocation.setCity(location.getCity());
        tgLocation.setCountry(location.getCountry());
        tgLocation.setProvince(location.getProvince());
        tgLocation.setAddress(location.getAddress());
        tgLocation.setAddress(location.getStreet());
        return tgLocation;
    };
}
