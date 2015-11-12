package com.mn.tiger.lbs.location;

import com.mn.tiger.lbs.map.TGLocation;
import com.mn.tiger.log.Logger;

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
        LOG.d("[Method:init] provider == " + provider.toString());
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
        LOG.d("[Method:initAppropriateLocationManager]");
        //请求一次定位，判断是不是在中国
        curLocationManager.setLocationListener(new ILocationListener()
        {
            @Override
            public void onReceiveLocation(TGLocation location)
            {
                removeLocationUpdates();

                isLocationInChina = curLocationManager.isLocationInChina(location);
                LOG.d("[Method:initAppropriateLocationManager] isLocationInChina == " + isLocationInChina);
                if (!isLocationInChina)
                {
                    if (!(isGoogleLocationManager(curLocationManager)))
                    {
                        curLocationManager = newGoogleLocationManager();
                    }
                    currentProvider = Provider.Google;
                    LOG.d("[Method:initAppropriateLocationManager] use GoogleLocationManager");
                }
                curLocationManager.setLocationListener(listener);
            }
        });

        requestLocationUpdates();
    }

    @Override
    public void requestLocationUpdates()
    {
        if(null != curLocationManager)
        {
            LOG.d("[Method:requestLocationUpdates] " + currentProvider);
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
        return curLocationManager.isLocationInChina(location);
    }

    @Override
    public void removeLocationUpdates()
    {
        LOG.d("[Method:removeLocationUpdates]");
        curLocationManager.removeLocationUpdates();
    }

    @Override
    public void destroy()
    {
        curLocationManager.destroy();
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
}
