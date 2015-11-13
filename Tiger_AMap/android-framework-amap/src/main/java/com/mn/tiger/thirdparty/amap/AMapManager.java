package com.mn.tiger.thirdparty.amap;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.mn.tiger.lbs.location.IMapManager;

/**
 * Created by Dalang on 2015/8/23.
 */
public class AMapManager implements IMapManager, AMapLocationListener, LocationSource
{
    private MapView mapView;

    private AMap aMap;

    private Activity activity;

    private LocationManagerProxy locationManager;

    private OnLocationChangedListener onLocationChangedListener;

    public AMapManager(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void init(ViewGroup mapContainer, Bundle savedInstanceState)
    {
        mapView = new MapView(activity);
        mapView.onCreate(savedInstanceState);

        mapContainer.addView(mapView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        aMap = mapView.getMap();
        setUpMap();
    }

    private void setUpMap()
    {
        aMap.setMyLocationEnabled(true);
        aMap.getUiSettings().setAllGesturesEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setLocationSource(this);
    }

    public void addMarker(double latitude, double langitude, String title)
    {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.draggable(false);
        markerOptions.position(new LatLng(latitude, langitude));
        markerOptions.title(title);
        Marker marker = aMap.addMarker(markerOptions);

        marker.showInfoWindow();
    }

    public void clear()
    {
        aMap.clear();
    }

    @Override
    public void centerTo(double latitude, double longitude)
    {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17);
        aMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume()
    {
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
      mapView.onPause();
    }

    @Override
    public void onDestroy()
    {
        mapView.onDestroy();
    }

    @Override
    public void showMyLocation()
    {
        aMap.getMyLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation)
    {
        if(null != onLocationChangedListener && null != aMapLocation)
        {
            onLocationChangedListener.onLocationChanged(aMapLocation);
        }
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener)
   {
      this.onLocationChangedListener = onLocationChangedListener;
       if(null != this.onLocationChangedListener)
       {
           locationManager = LocationManagerProxy.getInstance(this.activity);
           locationManager.requestLocationData(LocationProviderProxy.AMapNetwork,
                   2000, 10, this);
       }
   }

    @Override
    public void deactivate()
    {
        onLocationChangedListener = null;
        if(null != locationManager)
        {
            locationManager.removeUpdates(this);
            locationManager.destroy();
        }
        locationManager = null;
    }

    @Override
    @Deprecated
    public void onLocationChanged(Location location)
    {

    }

    @Override
    @Deprecated
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    @Deprecated
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    @Deprecated
    public void onProviderDisabled(String provider)
    {

    }
}
