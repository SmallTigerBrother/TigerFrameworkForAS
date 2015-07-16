package com.mn.tiger.system;

/**
 * 
 * 该类作用及功能说明:版本信息
 * 
 * @date 2014年4月26日
 */
public class Tiger
{
	/**
	 * 当前使用版本
	 */
	public static final VERSION_CODES SDK_CODE = VERSION_CODES.BENGAL_TIGER;
	
	/**
	 * 该类作用及功能说明:版本代号
	 * 
	 * @date 2014年4月26日
	 */
	public static enum VERSION_CODES
	{
		SOUTH_CHINA_TIGER("1.0.0", 1),
		//升级最低适配版本为API 15
		BENGAL_TIGER("2.0.0", 2);
		
		// release版本号
		public String RELEASE;
		// 版本index
		public int SDK_INT;

		// 构造方法
		private VERSION_CODES(String release, int sdk_int)
		{
			this.RELEASE = release;
			this.SDK_INT = sdk_int;
		}
	}
}
