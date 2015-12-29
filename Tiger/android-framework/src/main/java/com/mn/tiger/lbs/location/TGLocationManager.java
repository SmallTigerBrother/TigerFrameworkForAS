package com.mn.tiger.lbs.location;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.*;
import android.os.Process;

import com.mn.tiger.app.TGApplication;
import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.PackageUtils;

/**
 * Created by Dalang on 2015/7/26.
 * 地址管理类
 */
public class TGLocationManager implements ILocationManager
{
    private static final Logger LOG = Logger.getLogger(TGLocationManager.class);

    /**
     * 单例对象
     */
    private static TGLocationManager instance;

    /**
     * 当前使用的位置管理器
     */
    private ILocationManager curLocationManager;

    /**
     * 位置变化监听接口
     */
    private ILocationListener listener;

    /**
     * 位置提供者
     */
    private static Provider currentProvider = Provider.AMap;

    /**
     * 当前位置是否在中国
     */
    private boolean isLocationInChina = true;

    /**
     * 初始化位置管理器
     * @param provider
     */
    public static void init(Provider provider)
    {
        LOG.i("[Method:init] provider == " + provider.toString());
        currentProvider = provider;
    }

    public synchronized static TGLocationManager getInstance()
    {
        if(null == instance)
        {
            instance = new TGLocationManager();
            switch (currentProvider)
            {
                case BaiDu:
                    instance.curLocationManager = newBaiduLocationManager();
                    break;
                case AMap:
                    instance.curLocationManager = newAMapLocationManager();
                    break;
                case Google:
                    instance.curLocationManager = newGoogleLocationManager();
                    break;
                default:
                    break;
            }
        }

        return instance;
    }

    /**
     * 初始化最合适的位置管理器
     */
    public void initAppropriateLocationManager()
    {
        LOG.i("[Method:initAppropriateLocationManager]");
        //请求一次定位，判断是不是在中国
        curLocationManager.setLocationListener(new ILocationListener()
        {
            @Override
            public void onReceiveLocation(TGLocation location)
            {
                removeLocationUpdates();

                isLocationInChina = curLocationManager.isLocationInChina(location);
                LOG.i("[Method:initAppropriateLocationManager] isLocationInChina == " + isLocationInChina);
                if (!isLocationInChina)
                {
                    if (!(isGoogleLocationManager(curLocationManager)))
                    {
                        curLocationManager = newGoogleLocationManager();
                        initGoogleLocationManager();
                    }
                    currentProvider = Provider.Google;
                    LOG.i("[Method:initAppropriateLocationManager] use GoogleLocationManager");
                }
                curLocationManager.setLocationListener(listener);
            }

            @Override
            public void onLocationPermissionDeny()
            {

            }

            @Override
            public void onProviderDisabled(boolean isGPSEnbale, boolean isNetWorkEnable)
            {

            }
        });

        requestLocationUpdates();
    }

    /**
     * 初始化google位置管理器
     */
    private void initGoogleLocationManager()
    {
        curLocationManager.setLocationListener(new ILocationListener()
        {
            @Override
            public void onReceiveLocation(TGLocation location)
            {
                curLocationManager.removeLocationUpdates();
            }

            @Override
            public void onLocationPermissionDeny()
            {

            }

            @Override
            public void onProviderDisabled(boolean isGPSEnbale, boolean isNetWorkEnable)
            {

            }
        });

        curLocationManager.requestLocationUpdates();
    }

    @Override
    public void requestLocationUpdates()
    {
        if(null != curLocationManager)
        {
            LOG.i("[Method:requestLocationUpdates] " + currentProvider);
            curLocationManager.requestLocationUpdates();
        }
    }

    @Override
    public void setLocationListener(ILocationListener listener)
    {
        this.listener = listener;
        if(null != curLocationManager)
        {
            curLocationManager.setLocationListener(listener);
        }
    }

    /**
     * 当前位置是否在中国
     * @return
     */
    public boolean isCurrentLocationInChina()
    {
        return isLocationInChina;
    }

    @Override
    public boolean isLocationInChina(TGLocation location)
    {
        if(null != curLocationManager)
        {
            return curLocationManager.isLocationInChina(location);
        }
        return false;
    }

    @Override
    public void removeLocationUpdates()
    {
        LOG.i("[Method:removeLocationUpdates]");
        if(null != curLocationManager)
        {
            curLocationManager.removeLocationUpdates();
        }
    }

    @Override
    public void onDestroy()
    {
        if(null != curLocationManager)
        {
            curLocationManager.onDestroy();
        }
    }

    public static boolean isLocationPermissionDeny()
    {
        PackageManager packageManager = TGApplicationProxy.getInstance().getApplication().getPackageManager();
        String packageName = TGApplicationProxy.getInstance().getApplication().getPackageName();
        return PackageManager.PERMISSION_GRANTED != packageManager.checkPermission("android.permission.ACCESS_COARSE_LOCATION", packageName)
                && PackageManager.PERMISSION_GRANTED != packageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION",packageName);
    }

    public static boolean isGPSProviderEnabled()
    {
        LocationManager locationManager = (LocationManager)TGApplicationProxy.getInstance().getApplication().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isNetWorkProviderEnabled()
    {
        LocationManager locationManager = (LocationManager)TGApplicationProxy.getInstance().getApplication().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGoogleLocationManager(ILocationManager locationManager)
    {
        try
        {
            Class clazz = Class.forName("com.mn.tiger.thirdparty.google.GoogleLocationManager");
            return clazz.isInstance(locationManager);
        }
        catch (Exception e)
        {
            LOG.e("[Method:isGoogleLocationManager]", e);
            return false;
        }
    }

    private static ILocationManager newGoogleLocationManager()
    {
        try
        {
            Class clazz = Class.forName("com.mn.tiger.thirdparty.google.GoogleLocationManager");
            return (ILocationManager)clazz.newInstance();
        }
        catch (Exception e)
        {
            LOG.e("[Method:newGoogleLocationManager]", e);
            return null;
        }
    }

    private static ILocationManager newBaiduLocationManager()
    {
        try
        {
            Class clazz = Class.forName("com.mn.tiger.thirdparty.baidu.BaiduLocationManager");
            return (ILocationManager)clazz.newInstance();
        }
        catch (Exception e)
        {
            LOG.e("[Method:newBaiduLocationManager]", e);
            return null;
        }
    }

    private static ILocationManager newAMapLocationManager()
    {
        try
        {
            Class clazz = Class.forName("com.mn.tiger.thirdparty.amap.AMapLocationManager");
            return (ILocationManager)clazz.newInstance();
        }
        catch (Exception e)
        {
            LOG.e("[Method:newAMapLocationManager]", e);
            return null;
        }
    }

    @Override
    public TGLocation getLastLocation()
    {
        if(null != curLocationManager)
        {
            return curLocationManager.getLastLocation();
        }
        return null;
    }
}
