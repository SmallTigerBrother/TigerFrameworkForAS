package com.medialab.dimension.policy;

/**
 * 以iphone6为基准的计算策略
 * @author peng
 *
 */
public class CaculatePolicyBaseOnIphone6 extends CaculatePolicy
{
	public CaculatePolicyBaseOnIphone6(double inchOfScreen)
	{
		super(inchOfScreen);
	}

	@Override
	public double getInchOfReferenceDevice()
	{
		return 4.7d;
	}

	@Override
	public double getDensityOfReferenceDevice()
	{
		return getDensity(750, 1334, 4.7d);
	}
}
