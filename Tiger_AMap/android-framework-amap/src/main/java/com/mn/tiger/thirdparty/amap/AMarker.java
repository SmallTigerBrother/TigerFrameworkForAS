package com.mn.tiger.thirdparty.amap;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.mn.tiger.lbs.map.IMarker;

/**
 * Created by peng on 16/1/27.
 */
public class AMarker implements IMarker
{
    private Marker marker;

    AMarker(Marker marker)
    {
        this.marker = marker;
    }

    @Override
    public void setPeriod(int period)
    {
        this.marker.setPeriod(period);
    }

    @Override
    public int getPeriod()
    {
        return this.marker.getPeriod();
    }

    @Override
    public void remove()
    {
        this.marker.remove();
    }

    @Override
    public void destroy()
    {
        this.marker.destroy();
    }

    @Override
    public String getId()
    {
        return this.marker.getId();
    }

    @Override
    public void setTitle(String title)
    {
        this.marker.setTitle(title);
    }

    @Override
    public String getTitle()
    {
        return this.marker.getTitle();
    }

    @Override
    public void setSnippet(String snippet)
    {
        this.marker.setSnippet(snippet);
    }

    @Override
    public String getSnippet()
    {
        return this.marker.getSnippet();
    }

    @Override
    public void setAnchor(float x, float y)
    {
        this.marker.setAnchor(x, y);
    }

    @Override
    public void setDraggable(boolean draggable)
    {
        this.marker.setDraggable(draggable);
    }

    @Override
    public boolean isDraggable()
    {
        return this.marker.isDraggable();
    }

    @Override
    public void showInfoWindow()
    {
        this.marker.showInfoWindow();
    }

    @Override
    public void hideInfoWindow()
    {
        this.marker.hideInfoWindow();
    }

    @Override
    public boolean isInfoWindowShown()
    {
        return this.marker.isInfoWindowShown();
    }

    @Override
    public void setObject(Object object)
    {
        this.marker.setObject(object);
    }

    @Override
    public Object getObject()
    {
        return this.marker.getObject();
    }

    @Override
    public void setPositionByPixels(int x, int y)
    {
        this.marker.setPositionByPixels(x, y);
    }
}
