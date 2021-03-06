package com.mn.tiger.thirdparty.google;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mn.tiger.lbs.location.ILocationManager;
import com.mn.tiger.lbs.location.TGLocation;
import com.mn.tiger.lbs.map.IMapManager;
import com.mn.tiger.lbs.map.IMarker;
import com.mn.tiger.log.Logger;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Dalang on 2015/8/23.
 */
public class GoogleMapManager implements IMapManager, LocationSource, LocationListener
{
    private static final Logger LOG = Logger.getLogger(GoogleMapManager.class);

    private Activity activity;

    private MapFragment mapFragment;

    private GoogleMap googleMap;

    private GoogleLocationManager locationManager;

    private OnLocationChangedListener onLocationChangedListener;

    private ArrayList<Runnable> taskList;

    private static final Handler HANDLER = new Handler();

    public GoogleMapManager(Activity activity)
    {
        this.activity = activity;
        taskList = new ArrayList<Runnable>();
    }

    @Override
    public void init(ViewGroup mapContainer, Bundle savedInstanceState)
    {
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL);

        mapFragment = MapFragment.newInstance(options);
        activity.getFragmentManager().beginTransaction().add(mapContainer.getId(),mapFragment).commitAllowingStateLoss();
        HANDLER.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mapFragment.getMapAsync(new OnMapReadyCallback()
                {
                    @Override
                    public void onMapReady(GoogleMap googleMap)
                    {
                        LOG.i("[Method:onMapReady] googleMap == " + googleMap);
                        GoogleMapManager.this.googleMap = googleMap;
                        setUpMap();
                    }
                });
            }
        }, 300);
    }

    @Override
    public void disallowScrollParentInterceptTouchEvent(ViewGroup scrollParent)
    {

    }

    /**
     * 初始化地图
     */
    private void setUpMap()
    {
        if(null != googleMap)
        {
            googleMap.setLocationSource(this);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            try
            {
                googleMap.setMyLocationEnabled(true);
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }

            startNextTask();
        }
    }

    @Override
    public void centerTo(final double latitude, final double longitude)
    {
        taskList.add(new Runnable()
        {
            @Override
            public void run()
            {
                if(null != googleMap)
                {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17);
                    googleMap.animateCamera(cameraUpdate);
                }
            }
        });
        startNextTask();
    }

    @Override
    public void centerZoomTo(double latitude, double longitude, float zoom)
    {

    }

    @Override
    public void zoomTo(float zoom)
    {

    }

    @Override
    public float getZoom()
    {
        return 0;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
    }

    @Override
    public void onDestroy()
    {
    }

    @Override
    public void onResume()
    {
    }

    @Override
    public void onPause()
    {
    }

    @Override
    public IMarker addMarker(final double latitude, final double longitude, final String title)
    {
        taskList.add(new Runnable()
        {
            @Override
            public void run()
            {
                if(null != googleMap)
                {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.draggable(false);
                    markerOptions.position(new LatLng(latitude, longitude));
                    markerOptions.title(title);

                    Marker marker = googleMap.addMarker(markerOptions);
                    marker.showInfoWindow();
                }
            }
        });
        startNextTask();
        return null;
    }

    @Override
    public IMarker addMarker(double latitude, double longitude, String title, String snippet)
    {
        return null;
    }

    @Override
    public IMarker addMarker(double latitude, double longitude, String title, String snippet, int iconRes)
    {
        return null;
    }

    @Override
    public IMarker addMarker(double latitude, double longitude, String title, String snippet, int iconRes, Object params)
    {
        return null;
    }

    @Override
    public void showMyLocation()
    {
        taskList.add(new Runnable()
        {
            @Override
            public void run()
            {
                if(null != googleMap)
                {
                    googleMap.getMyLocation();
                }
            }
        });
        startNextTask();
    }

    @Override
    public void clear()
    {
        taskList.add(new Runnable()
        {
            @Override
            public void run()
            {
                if(null != googleMap)
                {
                    googleMap.clear();
                }
            }
        });
        startNextTask();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        LOG.i("[Method:LocationSource:onLocationChanged] lat == " + location.getLatitude() + " ; long == " + location.getLongitude());
        if(null != onLocationChangedListener)
        {
            onLocationChangedListener.onLocationChanged(location);
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener)
    {
        LOG.i("[Method:LocationSource:activate] ");
        this.onLocationChangedListener = onLocationChangedListener;
        if(null != this.onLocationChangedListener)
        {
            locationManager = new GoogleLocationManager();
            locationManager.setLocationListener(new ILocationManager.ILocationListener()
            {
                @Override
                public void onReceiveLocation(TGLocation location)
                {
                    onLocationChanged(location.getLocation());
                }

                @Override
                public void onLocationPermissionDeny()
                {

                }

                @Override
                public void onProviderDisabled(boolean isGPSDisabled, boolean isNetWorkDisable)
                {

                }
            });
            locationManager.requestLocationUpdates();
        }
    }

    @Override
    public void deactivate()
    {
        LOG.i("[Method:LocationSource:deactivate]");
        onLocationChangedListener = null;
        if(null != locationManager)
        {
            locationManager.removeLocationUpdates();
        }
        locationManager = null;
    }

    private void startNextTask()
    {
        if(null != googleMap)
        {
            Iterator<Runnable> iterator = taskList.iterator();
            while (iterator.hasNext())
            {
                HANDLER.post(iterator.next());
                iterator.remove();
            }
        }
    }

    @Override
    public void setOnMapLongClickListener(final OnMapLongClickListener listener)
    {
        if(null != listener)
        {
            taskList.add(new Runnable()
            {
                @Override
                public void run()
                {
                    if (null != googleMap)
                    {
                        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
                        {
                            @Override
                            public void onMapLongClick(LatLng latLng)
                            {
                                listener.onLongClick(latLng.latitude, latLng.longitude);
                            }
                        });
                    }
                }
            });
        }
        else
        {
            taskList.add(new Runnable()
            {
                @Override
                public void run()
                {
                    if (null != googleMap)
                    {
                        googleMap.setOnMapLongClickListener(null);
                    }
                }
            });
        }

        startNextTask();
    }
}
