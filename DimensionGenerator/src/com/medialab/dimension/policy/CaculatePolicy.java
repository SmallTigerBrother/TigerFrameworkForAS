package com.medialab.dimension.policy;

/**
 * 计算策略基类
 */
public abstract class CaculatePolicy
{
	/**
	 * 参考设备的屏幕大小（英寸）
	 */
	private double inchOfReferenceDevice = 4d;
	
	/**
	 * 参考设备的屏幕像素点密度
	 */
	private double densityOfReferenceDevice = 1d;
	
	/**
	 * 目标屏幕缩放比例
	 */
	private double scale = 1;
	
	public CaculatePolicy(double inchOfScreen)
	{
		this.inchOfReferenceDevice = getInchOfReferenceDevice();
		this.densityOfReferenceDevice = getDensityOfReferenceDevice();
		this.scale = caculateScale(inchOfScreen);
	}
	
	/**
	 * 计算缩放比例
	 * @param inchOfScreen
	 * @return
	 */
	public double caculateScale(double inchOfScreen)
	{
		return Math.sqrt(inchOfScreen/inchOfReferenceDevice);
	}
		
	/**
	 * 计算屏幕像素点密度
	 * @param width 屏幕宽度（px）
	 * @param height 屏幕高度（px）
	 * @param inch 屏幕大小（英寸）
	 * @return
	 */
	public static double getDensity(int width, int height, double inch)
	{
		return Math.sqrt(((width * width + height * height)))/ (inch * 160);
	}
	
	/**
	 * px转换为sp
	 * @param pxValue
	 * @return
	 */
	public final int px2sp(int pxValue)
	{
		return (int)((pxValue * scale / densityOfReferenceDevice) + 0.5f);
	}
	
	/**
	 * px转换为dp
	 * @param pxValue
	 * @return
	 */
	public final int px2dp(int pxValue)
	{
		return (int)((pxValue * scale) / densityOfReferenceDevice);
	}
	
	/**
	 * 获取参考设备屏幕大小（英寸）
	 * @return
	 */
	public abstract double getInchOfReferenceDevice();
	
	/**
	 * 获取参考设备屏幕像素点密度
	 * @return
	 */
	public abstract double getDensityOfReferenceDevice();
	
}
