package com.tiger.mn.thirdparty.baidu;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mn.tiger.lbs.map.IMapManager;
import com.mn.tiger.lbs.map.IMarker;

/**
 * Created by peng on 16/1/28.
 */
public class BDMapManager implements IMapManager
{
    @Override
    public void init(ViewGroup mapContainer, Bundle savedInstanceState)
    {

    }

    @Override
    public void disallowScrollParentInterceptTouchEvent(final ViewGroup scrollParent)
    {
//        (ViewGroup)mapView.getChildAt(0).setOnTouchListener(new View.OnTouchListener()
//        {
//            @Override
//            public boolean onTouch(View v, MotionEvent event)
//            {
//                if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
//                {
//                    scrollParent.requestDisallowInterceptTouchEvent(false);
//                }
//                else
//                {
//                    scrollParent.requestDisallowInterceptTouchEvent(true);
//                }
//                return false;
//            }
//        });
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
    public IMarker addMarker(double latitude, double langitude, String title)
    {
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
    public void centerTo(double latitude, double longitude)
    {

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
    public void showMyLocation()
    {

    }

    @Override
    public void clear()
    {

    }

    @Override
    public void setOnMapLongClickListener(OnMapLongClickListener listener)
    {

    }
}
