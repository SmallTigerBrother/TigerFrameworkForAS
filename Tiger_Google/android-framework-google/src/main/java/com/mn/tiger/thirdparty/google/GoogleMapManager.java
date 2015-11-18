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
import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.CR;

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

        mapFragment = (MapFragment)activity.getFragmentManager().findFragmentById(CR.getViewId(activity, "google_map_fragment"));
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

    /**
     * 初始化地图
     */
    private void setUpMap()
    {
        if(null != googleMap)
        {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.setLocationSource(this);

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
                    googleMap.moveCamera(cameraUpdate);
                }
            }
        });
        startNextTask();
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
    public void addMarker(final double latitude, final double longitude, final String title)
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
}
