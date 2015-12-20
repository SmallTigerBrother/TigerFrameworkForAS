package com.medialab.dimension.data;

/**
 * 尺寸数据类
 */
public class DimensionVO
{
	/**
	 * 尺寸类型
	 */
	private Type type = Type.DP;
	
	/**
	 * px值
	 */
	private int pxValue;
	
	/**
	 * 在android系统中适配的值
	 */
	private int androidValue;
	
	/**
	 * dimen的name
	 */
	private String name = "";
	
	public DimensionVO(Type type)
	{
		this.type = type;
	}
	
	public int getPxValue()
	{
		return pxValue;
	}
	
	public void setPxValue(int pxValue)
	{
		this.pxValue = pxValue;
	}
	
	public int getAndroidValue()
	{
		return androidValue;
	}
	
	public void setAndroidValue(int androidValue)
	{
		this.androidValue = androidValue;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Type getType()
	{
		return type;
	}
	
	public enum Type
	{
		SP,
		DP
	}
}
