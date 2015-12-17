package com.mn.tiger.thirdparty.amap;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.mn.tiger.lbs.map.IMapManager;

/**
 * Created by Dalang on 2015/8/23.
 */
public class AMapManager implements IMapManager, AMapLocationListener, LocationSource
{
    private MapView mapView;

    private AMap aMap;

    private Activity activity;

    private AMapLocationClient locationClient;

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
        aMap.setLocationSource(this);
        aMap.getUiSettings().setCompassEnabled(true);
        aMap.getUiSettings().setAllGesturesEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
    }

    public void addMarker(double latitude, double longitude, String title)
    {
        addMarker(latitude, longitude, title, null, null);
    }

    public void addMarker(double latitude, double longitude, String title,String snippet, Object params)
    {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.draggable(false);
        markerOptions.position(new LatLng(latitude, longitude));
        boolean canShowInfoWindow = false;
        if(!TextUtils.isEmpty(title))
        {
            markerOptions.title(title);
            canShowInfoWindow = true;
        }

        if(!TextUtils.isEmpty(snippet))
        {
            markerOptions.snippet(snippet);
            canShowInfoWindow = true;
        }

        Marker marker = aMap.addMarker(markerOptions);
        if(null != params)
        {
            marker.setObject(params);
        }

        if(canShowInfoWindow)
        {
            marker.showInfoWindow();
        }
    }

    /**
     * 设置MarkerInfoWindow显示适配器
     * @param infoWindowAdapter
     */
    public void setInfoWindowAdapter(AMap.InfoWindowAdapter infoWindowAdapter)
    {
        aMap.setInfoWindowAdapter(infoWindowAdapter);
    }

    /**
     * 设置MarkerInfoWindow点击事件监听器
     * @param onInfoWindowClickListener
     */
    public void setOnInfoWindowClickListener(AMap.OnInfoWindowClickListener onInfoWindowClickListener)
    {
        aMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
    }

    public void clear()
    {
        aMap.clear();
    }

    @Override
    public void centerTo(double latitude, double longitude)
    {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17);
        aMap.animateCamera(cameraUpdate);
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
        locationClient.onDestroy();
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
           locationClient = new AMapLocationClient(activity);
           AMapLocationClientOption option = new AMapLocationClientOption();
           option.setOnceLocation(true);
           option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
           locationClient.setLocationOption(option);
           locationClient.setLocationListener(this);
       }
   }

    @Override
    public void deactivate()
    {
        onLocationChangedListener = null;
        if(null != locationClient)
        {
            locationClient.onDestroy();
        }
    }

    @Override
    public void setOnMapLongClickListener(final OnMapLongClickListener listener)
    {
        if(null != listener)
        {
            aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener()
            {
                @Override
                public void onMapLongClick(LatLng latLng)
                {
                    listener.onLongClick(latLng.latitude, latLng.longitude);
                }
            });
        }
        else
        {
            aMap.setOnMapLongClickListener(null);
        }
    }
}
