package com.mn.tiger.thirdparty.google;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.lbs.geocoding.IGeoCoding;
import com.mn.tiger.lbs.location.ILocationManager;
import com.mn.tiger.lbs.location.TGLocation;
import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.CR;

/**
 * Created by Dalang on 2015/7/26.
 */
public class GoogleLocationManager implements ILocationManager
{
    private static final Logger LOG = Logger.getLogger(GoogleLocationManager.class);

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * 上一次定位的地址
     */
    private Location lastLocation;

    /**
     * 系统的位置管理器
     */
    private LocationManager locationManager;

    private ILocationListener listener;

    private TGLocation lastTGLocation;

    private GoogleGeoCoding geoCoding;

    public GoogleLocationManager()
    {
        locationManager = (LocationManager) TGApplicationProxy.getApplication().getSystemService(Context.LOCATION_SERVICE);
        geoCoding = new GoogleGeoCoding();
    }

    /**
     * 请求地址更新
     */
    @Override
    public void requestLocationUpdates()
    {
        requestGPSLocationUpdates();
        requestNetworkLocationUpdates();
    }

    @Override
    public void setLocationListener(ILocationListener listener)
    {
        this.listener = listener;
    }

    /**
     * 请求GPS定位更新
     */
    private void requestGPSLocationUpdates()
    {
        if(PackageManager.PERMISSION_GRANTED == TGApplicationProxy.getApplication().checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION")
                && PackageManager.PERMISSION_GRANTED == TGApplicationProxy.getApplication().checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION"))
        {
            try
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 1000, 20, gpsLocationListener);
            }
            catch (Exception e)
            {
                LOG.e("[Method:requestGPSLocationUpdates]" + e.getMessage());
            }
        }
    }

    /**
     * 请求网络定位地址更新
     */
    private void requestNetworkLocationUpdates()
    {
        if(PackageManager.PERMISSION_GRANTED == TGApplicationProxy.getApplication().checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION")
                && PackageManager.PERMISSION_GRANTED == TGApplicationProxy.getApplication().checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION"))
        {
            try
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2 * 1000, 20, networkLocationListener);
            }
            catch (Exception e)
            {
                LOG.e("[Method:requestNetworkLocationUpdates]" + e.getMessage());
            }
        }
    }

    /**
     * 删除网络定位监听器
     */
    private void removeNetworkLocationUpdates()
    {
        if(PackageManager.PERMISSION_GRANTED == TGApplicationProxy.getApplication().checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION")
                && PackageManager.PERMISSION_GRANTED == TGApplicationProxy.getApplication().checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION"))
        {
            locationManager.removeUpdates(networkLocationListener);
        }
    }

    @Override
    public void removeLocationUpdates()
    {
        if(PackageManager.PERMISSION_GRANTED == TGApplicationProxy.getApplication().checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION")
                && PackageManager.PERMISSION_GRANTED == TGApplicationProxy.getApplication().checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION"))
        {
            locationManager.removeUpdates(networkLocationListener);
            locationManager.removeUpdates(gpsLocationListener);
        }
    }

    @Override
    public void onDestroy()
    {

    }

    /**
     * gps定位监听对象
     */
    private LocationListener gpsLocationListener = new LocationListener()
    {
        /**
         * 是否已删除网络定位监听器的控制参数
         */
        private boolean isRemoveNetworkListener = false;

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            LOG.i("[Method:onStatusChanged] provider == " + provider + "status == " + status);

            //若GPS定位不可用，则启动网络定位
            if (LocationProvider.OUT_OF_SERVICE == status)
            {
                requestNetworkLocationUpdates();
                isRemoveNetworkListener = false;
            }
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

        @Override
        public void onProviderDisabled(String provider)
        {
        }

        @Override
        public void onLocationChanged(Location location)
        {
            LOG.i("[Method:onLocationChanged] provider == " + location.getProvider());
            //判断当前地址和上一次定位的结果哪个更加精确，若当前定位的地址更加精确，通知更新地址
            if (isBetterLocation(location, lastLocation))
            {
                updateLocation(location);
            }

            //删除网络定位监听接口
            if (location != null && !isRemoveNetworkListener)
            {
                removeNetworkLocationUpdates();
                isRemoveNetworkListener = true;
            }
        }
    };

    /**
     * 网络定位监听类
     */
    private LocationListener networkLocationListener = new LocationListener()
    {
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

        @Override
        public void onLocationChanged(Location location)
        {
            LOG.i("[Method:onLocationChanged] provider == " + location.getProvider());
            //判断当前地址和上一次定位的结果哪个更加精确，若当前定位的地址更加精确，通知更新地址
            if (isBetterLocation(location, lastLocation))
            {
                updateLocation(location);
            }
        }
    };

    /**
     * 更新地址
     * @param location
     */
    private void updateLocation(final Location location)
    {
        this.lastLocation = location;
        geoCoding.geoCoding(location.getLatitude(), location.getLongitude(), new IGeoCoding.IGeoCodeListener()
        {
            @Override
            public void onGeoCodingSuccess(TGLocation location)
            {
                LOG.i("[Method:onGeoCodingSuccess]");
                //发通知界面处理
                if (null != listener)
                {
                    GoogleLocationManager.this.lastTGLocation = location;
                    listener.onReceiveLocation(location);
                }
            }

            @Override
            public void onGeoCodingError(int code, String message)
            {
                LOG.e("[Method:onGeoCodingError]");
                if (null != listener)
                {
                    GoogleLocationManager.this.lastTGLocation = convert2TGLocation(location);
                    GoogleLocationManager.this.lastTGLocation.setTime(System.currentTimeMillis());
                    listener.onReceiveLocation(lastTGLocation);
                }
            }
        });
    }

    /**
     * Determines whether one Location reading is better than the current
     * Location fix
     *(该方法由Google提供)
     * @param location
     *            The new Location that you want to evaluate
     * @param currentBestLocation
     *            The current Location fix, to which you want to compare the new
     *            one
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation)
    {
        if (currentBestLocation == null)
        {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer)
        {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        }
        else if (isSignificantlyOlder)
        {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate)
        {
            return true;
        }
        else if (isNewer && !isLessAccurate)
        {
            return true;
        }
        else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
        {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2)
    {
        if (provider1 == null)
        {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * 判断位置是否在中国
     * @param location
     * @return
     */
    @Override
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
    public TGLocation getLastLocation()
    {
        return lastTGLocation;
    }

    public static TGLocation convert2TGLocation(Location location)
    {
        if(null != location)
        {
            TGLocation tgLocation = new TGLocation();
            tgLocation.setLatitude(location.getLatitude());
            tgLocation.setLongitude(location.getLongitude());
            tgLocation.setLocation(location);
            return tgLocation;
        }
        return null;
    }
}
