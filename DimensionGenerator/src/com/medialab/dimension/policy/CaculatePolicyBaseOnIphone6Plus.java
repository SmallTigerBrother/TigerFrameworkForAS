package com.medialab.dimension.policy;

/**
 * 以iphone6为基准的计算策略
 * @author peng
 *
 */
public class CaculatePolicyBaseOnIphone6Plus extends CaculatePolicy
{
	public CaculatePolicyBaseOnIphone6Plus(int width,int height, double inchOfScreen)
	{
		super(width,height, inchOfScreen);
	}

	@Override
	public int getReferenceDeviceWidth()
	{
		return 1080;
	}
	
	@Override
	public int getReferenceDeviceHeight()
	{
		return 1920;
	}
	
	@Override
	public double getInchOfReferenceDevice()
	{
		return 5.5d;
	}
}
