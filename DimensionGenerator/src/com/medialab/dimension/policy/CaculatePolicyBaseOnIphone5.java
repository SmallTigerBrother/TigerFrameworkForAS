package com.medialab.dimension.policy;

/**
 * 已iphone5为基准分辨率的计算策略
 */
public class CaculatePolicyBaseOnIphone5 extends CaculatePolicy
{
	public CaculatePolicyBaseOnIphone5(double inchOfScreen)
	{
		super(inchOfScreen);
	}

	@Override
	public double getInchOfReferenceDevice()
	{
		return 4d;
	}

	@Override
	public double getDensityOfReferenceDevice()
	{
		return getDensity(640, 1136, 4d);
	}
}
