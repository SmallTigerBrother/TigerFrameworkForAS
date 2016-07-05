package com.mn.tiger.thirdparty.google;

import com.mn.tiger.lbs.location.TGLocation;

import java.util.Arrays;

public class GoogleAddressResult
{
    private double latitude;

    private double longitude;

	private String[] types;

	private String formatted_address;

	private AddressComponent[] address_components;

	public String getFormatted_address()
	{
		return formatted_address;
	}

	public void setFormatted_address(String formattedAddress)
	{
		formatted_address = formattedAddress;
	}

	public String[] getTypes()
	{
		return types;
	}

	public void setTypes(String[] types)
	{
		this.types = types;
	}

	public String getCountry()
	{
		if(null != address_components && address_components.length > 0)
		{
            for (AddressComponent addressComponent : address_components)
            {
                if(containString(addressComponent.types, "country"))
                {
                    return addressComponent.long_name;
                }
            }
		}
		return "";
	}

	public String getProvince()
	{
		if(null != address_components)
		{
            for (AddressComponent addressComponent : address_components)
            {
                if(containString(addressComponent.types, "administrative_area_level_1"))
                {
                    return addressComponent.long_name;
                }
            }
		}
		return "";
	}

	public String getCity()
	{
		if(null != address_components && address_components.length > 2)
		{
            String city = "";
            for (AddressComponent addressComponent : address_components)
            {
                if(containString(addressComponent.types, "locality"))
                {
                    return addressComponent.long_name;
                }
            }
		}
		return "";
	}

	public String getStreet()
	{
		if(null != address_components)
		{
            for (AddressComponent addressComponent : address_components)
            {
                if(containString(addressComponent.types, "route"))
                {
                    return addressComponent.long_name;
                }
            }
		}
		return "";
	}

	private boolean containString(String[] array, String target)
	{
		for (String string : array)
		{
			if(target.equalsIgnoreCase(string))
			{
				return true;
			}
		}
		return false;
	}

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    @Override
	public String toString()
	{
		return "Result [formatted_address=" + formatted_address + ", types="
				+ Arrays.toString(types) + "]";
	}

    public TGLocation convert2Location()
    {
        TGLocation location = new TGLocation();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setCountry(getCountry());
        location.setProvince(getProvince());
        location.setCity(getCity());
        location.setStreet(getStreet());
        location.setAddress(getFormatted_address());
        return location;
    }

	public static class AddressComponent
	{
		String long_name;
		String short_name;
		String[] types;
	}

}
