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
			AddressComponent addressComponent = address_components[address_components.length - 1];
			if("country".equalsIgnoreCase(addressComponent.types[0]))
			{
				return addressComponent.long_name;
			}
		}
		return "";
	}

	public String getProvince()
	{
		if(null != address_components && address_components.length > 1)
		{
			AddressComponent addressComponent = address_components[address_components.length - 2];
			if("administrative_area_level_1".equalsIgnoreCase(addressComponent.types[0]))
			{
				return addressComponent.long_name;
			}
		}
		return "";
	}

	public String getCity()
	{
		if(null != address_components && address_components.length > 2)
		{
			AddressComponent addressComponent = address_components[address_components.length - 3];
			if("locality".equalsIgnoreCase(addressComponent.types[0]))
			{
				AddressComponent subAddressComponent = address_components[address_components.length - 4];
				if(address_components.length > 3 && "sublocality_level_1".equalsIgnoreCase(subAddressComponent.types[0]))
				{
					return addressComponent.long_name + subAddressComponent.long_name;
				}
				return addressComponent.long_name;
			}
		}
		return "";
	}

	public String getStreet()
	{
		if(null != address_components && address_components.length > 0)
		{
			AddressComponent addressComponent = address_components[0];
			if("route".equalsIgnoreCase(addressComponent.types[0]))
			{
				return addressComponent.long_name;
			}
		}
		return "";
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
