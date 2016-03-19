package com.mn.tiger.thirdparty.amap;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.lbs.location.ILocationManager;
import com.mn.tiger.lbs.location.TGLocation;
import com.mn.tiger.lbs.location.TGLocationManager;
import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.CR;

/**
 * Created by Dalang on 2015/7/26.
 * 百度定位管理类
 */
public class AMapLocationManager implements ILocationManager
{
    private static final Logger LOG = Logger.getLogger(AMapLocationManager.class);

    private AMapLocationClient locationClient;

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
    };

    /**
     * 初始化
     */
    public AMapLocationManager()
    {
        locationClient = new AMapLocationClient(TGApplicationProxy.getApplication());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        locationClient.setLocationOption(option);
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 请求定位
     */
    @Override
    public void requestLocationUpdates()
    {
        LOG.i("[Method:requestLocationUpdates]");
        if(TGLocationManager.isLocationPermissionDeny())
        {
            LOG.w("[Method:requestLocationUpdates] Location Permission Deny, please check your settings");
            if(null != listener)
            {
                listener.onLocationPermissionDeny();
            }
            return;
        }

        locationClient.startLocation();
    }

    @Override
    public void removeLocationUpdates()
    {
        LOG.i("[Method:removeLocationUpdates]");
        locationClient.stopLocation();
    }

    @Override
    public void onDestroy()
    {
        LOG.i("[Method:onDestroy]");
        locationClient.unRegisterLocationListener(locationListener);
        locationClient.stopLocation();
        locationClient.onDestroy();
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

        Context context = TGApplicationProxy.getApplication();
        String chinaZH = context.getResources().getString(CR.getStringId(context, "tiger_china_zh"));
        String chinaEN = context.getResources().getString(CR.getStringId(context, "tiger_china_en"));

        if(!location.getCountry().equalsIgnoreCase(chinaZH) && !location.getCountry().equalsIgnoreCase(chinaEN))
        {
            return false;
        }

        return true;
    }

    @Override
    public TGLocation getLastLocation()
    {
        return convert2TGLocation(locationClient.getLastKnownLocation());
    }

    /**
     * 转换为TGLocation
     * @param location
     * @return
     */
    public static TGLocation convert2TGLocation(AMapLocation location)
    {
        if(null != location)
        {
            TGLocation tgLocation = new TGLocation();
            tgLocation.setLatitude(location.getLatitude());
            tgLocation.setLongitude(location.getLongitude());
            tgLocation.setCity(location.getCity());
            tgLocation.setCountry(location.getCountry());
            tgLocation.setProvince(location.getProvince());
            tgLocation.setAddress(location.getAddress());
            tgLocation.setStreet(location.getRoad());
            return tgLocation;
        }

        return null;
    }
}
