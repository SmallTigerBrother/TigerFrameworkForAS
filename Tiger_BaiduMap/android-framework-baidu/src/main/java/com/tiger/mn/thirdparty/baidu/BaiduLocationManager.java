package com.tiger.mn.thirdparty.baidu;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mn.tiger.app.TGApplication;

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
                listener.onReceiveLocation(TGLocation.initWith(bdLocation));
            }
        }
    };

    /**
     * 初始化
     */
    BaiduLocationManager()
    {
        locationClient = new LocationClient(TGApplication.getInstance());
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
                TGLocation location = TGLocation.initWith(bdLocation);
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

        return true;
    }

    @Override
    public void removeLocationUpdates()
    {
        locationClient.unRegisterLocationListener(locationListener);
    }

    @Override
    public void destroy()
    {
        locationClient.stop();
        locationClient = null;
    }

    static TGLocation convert2TGLocation(BDLocation location)
    {
        TGLocation tgLocation = new TGLocation();
        tgLocation.latitude = location.getLatitude();
        tgLocation.longitude = location.getLongitude();
        tgLocation.city = location.getCity();
        tgLocation.country = location.getCountry();
        tgLocation.province = location.getProvince();
        tgLocation.address = location.getAddrStr();
        tgLocation.street = location.getStreet();
        return tgLocation;
    }
}
