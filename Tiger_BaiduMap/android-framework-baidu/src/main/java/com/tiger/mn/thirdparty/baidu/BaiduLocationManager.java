package com.tiger.mn.thirdparty.baidu;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.lbs.location.ILocationManager;
import com.mn.tiger.lbs.location.TGLocation;
import com.mn.tiger.utility.CR;

/**
 * Created by Dalang on 2015/7/26.
 * 百度定位管理类
 */
public class BaiduLocationManager implements ILocationManager
{
    private LocationClient locationClient;

    private ILocationListener listener;

    private BDLocationListener locationListener = new BDLocationListener()
    {
        @Override
        public void onReceiveLocation(BDLocation bdLocation)
        {
            //发通知界面处理
            if(null != listener)
            {
                listener.onReceiveLocation(convert2TGLocation(bdLocation));
            }
        }
    };

    /**
     * 初始化
     */
    BaiduLocationManager()
    {
        locationClient = new LocationClient(TGApplicationProxy.getApplication());
        locationClient.registerLocationListener(locationListener);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setIgnoreKillProcess(true);

        locationClient.setLocOption(option);
    }

    public void checkLocationIsChina(final ILocationListener listener)
    {
        locationClient.registerLocationListener(new BDLocationListener()
        {
            @Override
            public void onReceiveLocation(BDLocation bdLocation)
            {
                locationClient.unRegisterLocationListener(this);
                TGLocation location = convert2TGLocation(bdLocation);
                location.setTime(System.currentTimeMillis());
                listener.onReceiveLocation(location);
            }
        });
    }

    /**
     * 请求定位
     */
    @Override
    public void requestLocationUpdates()
    {
        locationClient.start();
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
        if(location.getLatitude() != 0 && location.getLongitude() != 0)
        {
            if (location.getLongitude() < 72.004 || location.getLongitude() > 137.8347 ||
                    location.getLatitude() < 0.8293 || location.getLatitude() > 55.8271)
            {
                return false;
            }
        }

        Context context = TGApplicationProxy.getApplication();
        String chinaZH = context.getResources().getString(CR.getStringId(context, "china_zh"));
        String chinaEN = context.getResources().getString(CR.getStringId(context, "china_en"));

        if(!location.getCountry().equalsIgnoreCase(chinaZH) && !location.getCountry().equalsIgnoreCase(chinaEN))
        {
            return false;
        }

        return true;
    }

    @Override
    public void removeLocationUpdates()
    {
        locationClient.unRegisterLocationListener(locationListener);
    }

    @Override
    public void onDestroy()
    {
        locationClient.stop();
        locationClient = null;
    }

    @Override
    public TGLocation getLastLocation()
    {
        return convert2TGLocation(locationClient.getLastKnownLocation());
    }

    static TGLocation convert2TGLocation(BDLocation location)
    {
        TGLocation tgLocation = new TGLocation();
        tgLocation.setLatitude(location.getLatitude());
        tgLocation.setLongitude(location.getLongitude());
        tgLocation.setCity(location.getCity());
        tgLocation.setProvince(location.getProvince());
        tgLocation.setCountry(location.getCountry());
        tgLocation.setAddress(location.getAddrStr());
        tgLocation.setStreet(location.getStreet());
        return tgLocation;
    }
}
