package com.mn.tiger.thirdparty.google;

import java.util.Arrays;

public class GoogleAddressResult
{
	private String[] types;

	private String formatted_address;

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

	@Override
	public String toString()
	{
		return "Result [formatted_address=" + formatted_address + ", types="
				+ Arrays.toString(types) + "]";
	}

}
