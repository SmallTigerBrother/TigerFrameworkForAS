package com.mn.tiger.thirdparty.amap;

import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.mn.tiger.app.TGApplication;
import com.mn.tiger.log.Logger;

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
            LOG.d("[Method:onLocationChanged] Provider == " + aMapLocation.getProvider() + "  lat == " +
                    aMapLocation.getLatitude() + "  lng == " + aMapLocation.getLongitude());

            final TGLocation tgLocation = TGLocation.initWith(aMapLocation);
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
        LOG.d("[Method:requestLocationUpdates]");
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

        if(!location.getCountry().equalsIgnoreCase(TGApplication.getInstance().getResources().getString(R.string.china)))
        {
            return false;
        }

        return true;
    }
	

    public static TGLocation convert2TGLocation(AMapLocation location)
    {
        TGLocation tgLocation = new TGLocation();
        tgLocation.latitude = location.getLatitude();
        tgLocation.longitude = location.getLongitude();
        tgLocation.city = location.getCity();
        tgLocation.country = location.getCountry();
        tgLocation.province = location.getProvince();
        tgLocation.address = location.getAddress();
        tgLocation.street = location.getStreet();
        return tgLocation;
    };
}
