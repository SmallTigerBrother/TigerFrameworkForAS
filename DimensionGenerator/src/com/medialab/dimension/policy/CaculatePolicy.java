package com.medialab.dimension.policy;

/**
 * 计算策略基类
 */
public abstract class CaculatePolicy
{
	private double densityOfScreen;
	
	/**
	 * 目标屏幕缩放比例
	 */
	private double scale = 1;
	
	private int width;
	
	private int height;
	
	private double inchOfScreen;
	
	public CaculatePolicy(int width,int height, double inchOfScreen)
	{
		this.width = width;
		this.height = height;
		this.inchOfScreen = inchOfScreen;
		
		this.scale = caculateScale(inchOfScreen);
		this.densityOfScreen = getDensity(width, height, inchOfScreen);
	}
	
	/**
	 * 计算缩放比例
	 * @param inchOfScreen
	 * @return
	 */
	public double caculateScale(double inchOfScreen)
	{
		return new Double(width) / new Double(getReferenceDeviceWidth()) * getRatio();
		
//		return Math.sqrt((new Double(width) / new Double(getReferenceDeviceWidth()) * inchOfScreen / inchOfReferenceDevice));
		
//		return getDensity(1080, 1920, 5.5) / densityOfReferenceDevice;
//		return Math.sqrt(inchOfScreen/inchOfReferenceDevice);
//		return (inchOfScreen + inchOfReferenceDevice)/(inchOfReferenceDevice * 2);
//		return Math.sqrt((inchOfScreen + inchOfReferenceDevice)/(inchOfReferenceDevice * 2));
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
		return (int)((pxValue * scale / densityOfScreen) + 0.5f);
	}
	
	/**
	 * px转换为dp
	 * @param pxValue
	 * @return
	 */
	public final int px2dp(int pxValue)
	{
		return (int)((pxValue * scale) / densityOfScreen);
	}
	
	/**
	 * PX转换为px
	 * @param pxValue
	 * @return
	 */
	public final int px2px(int pxValue)
	{
		return (int)(pxValue * scale + 0.5f);
	}
	
	protected double getRatio()
	{
		return Math.sqrt(inchOfScreen) * 2 / inchOfScreen;
	}
	
	public abstract int getReferenceDeviceWidth();
	
	public abstract int getReferenceDeviceHeight();
	
	/**
	 * 获取参考设备屏幕大小（英寸）
	 * @return
	 */
	public abstract double getInchOfReferenceDevice();
	
	/**
	 * 获取参考设备屏幕像素点密度
	 * @return
	 */
	public double getDensityOfReferenceDevice()
	{
		return getDensity(getReferenceDeviceWidth(), getReferenceDeviceHeight(), getInchOfReferenceDevice());
	}
	
}
