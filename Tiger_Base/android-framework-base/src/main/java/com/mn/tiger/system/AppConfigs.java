package com.mn.tiger.system;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.Preferences;

/**
 * App配置信息
 */
public class AppConfigs
{
	private static final Logger LOG = Logger.getLogger(AppConfigs.class);

	/**
	 * 产品Id号
	 */
	public static int Product_ID = 10;

	/**
	 * 应用程序的版本号，从AndroidManifest.xml中读取
	 */
	public static String appVersion;

	/**
	 * 应用程序的版本代码，从AndroidManifest.xml中读取
	 */
	public static int appVersionCode;

	/**
	 * 应用设置信息
	 */
	public static final String APP_PREFERENCES = "app_configs";

	/**
	 * 版本号保存键值
	 */
	private static final String VERSION_CODE_KEY = "versionCode";


	public static void initAppConfigs(Context context)
	{
		try
		{
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			appVersion = packageInfo.versionName;
			appVersionCode = packageInfo.versionCode;
		}
		catch (NameNotFoundException e)
		{
			LOG.e(e.getMessage(), e);
		}
	}

	/**
	 * 是否为新安装或者刚升级
	 * @param context
	 * @return
	 */
	public static boolean isNewOrUpgrade(Context context)
	{
		//读取上次保存的版本号
		Integer lastVersionCode = Preferences.read(context, APP_PREFERENCES, VERSION_CODE_KEY, 0);

		PackageInfo packageInfo;
		try
		{
			packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
			Preferences.save(context, APP_PREFERENCES, VERSION_CODE_KEY, packageInfo.versionCode);

			//对比当前应用的版本号与保存的版本号，若应用为新启动，或者刚升级过，返回true
			if(null == lastVersionCode || lastVersionCode < packageInfo.versionCode)
			{
				return true;
			}
		}
		catch (NameNotFoundException e)
		{
			LOG.e("[Method:isNewOrUpgrade]",e);
		}

		return false;
	}

}
