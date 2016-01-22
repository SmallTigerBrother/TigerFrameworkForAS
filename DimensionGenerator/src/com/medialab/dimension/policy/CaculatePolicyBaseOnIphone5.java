package com.medialab.dimension.policy;

/**
 * 已iphone5为基准分辨率的计算策略
 */
public class CaculatePolicyBaseOnIphone5 extends CaculatePolicy
{
	public CaculatePolicyBaseOnIphone5(int width,int height, double inchOfScreen)
	{
		super(width, height, inchOfScreen);
	}
	
	@Override
	public int getReferenceDeviceWidth()
	{
		return 640;
	}

	@Override
	public int getReferenceDeviceHeight()
	{
		return 1136;
	}
	
	@Override
	public double getInchOfReferenceDevice()
	{
		return 4d;
	}
}
