package com.mn.tiger.lbs.location;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by Dalang on 2015/7/30.
 */
public class TGLocation implements Serializable
{
    private double latitude;

    private double longitude;

    private String city = "";

    private String province = "";

    private String country = "";

    private String street = "";

    private String address = "";

    private long time;

    private Location location;

    public TGLocation()
    {
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public long getTime()
    {
        return time;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }
}
