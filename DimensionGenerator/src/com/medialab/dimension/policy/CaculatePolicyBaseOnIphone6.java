package com.medialab.dimension.policy;

/**
 * 以iphone6为基准的计算策略
 * @author peng
 *
 */
public class CaculatePolicyBaseOnIphone6 extends CaculatePolicy
{
	public CaculatePolicyBaseOnIphone6(int width,int height, double inchOfScreen)
	{
		super(width, height, inchOfScreen);
	}
	
	@Override
	public int getReferenceDeviceWidth()
	{
		return 750;
	}

	@Override
	public int getReferenceDeviceHeight()
	{
		return 1334;
	}
	
	@Override
	public double getInchOfReferenceDevice()
	{
		return 4.7d;
	}
}
