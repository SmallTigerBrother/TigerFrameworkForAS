package com.mn.tiger.lbs.map;

/**
 * Created by peng on 16/1/27.
 */
public interface IMarker
{
    public void setPeriod(int period);

    public int getPeriod();

    public void remove();

    public void destroy();

    public String getId();

    public void setTitle(String title);

    public String getTitle();

    public void setSnippet(String snippet);

    public String getSnippet();

    public void setAnchor(float x, float y);

    public void setDraggable(boolean draggable);

    public boolean isDraggable();

    public void showInfoWindow();

    public void hideInfoWindow();

    public boolean isInfoWindowShown();

    public boolean equals(Object object);

    public void setObject(Object object);

    public Object getObject();

    public void setPositionByPixels(int x, int y);
}
