package com.mn.tiger.utility;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;

import com.mn.tiger.log.LogTools;

/**
 * 该类作用及功能说明:对包的操作，安装卸载等
 * @date 2014-2-11
 */
public class PackageUtils
{
	/**
	 * 日志标签
	 */
	protected static final String LOG_TAG = PackageUtils.class.getSimpleName();

	/**
	 * 该方法的作用:安装apk应用
	 * 
	 * @date 2013-3-1
	 * @param context
	 * @param apkFile
	 *            apk文件
	 */
	@SuppressLint("DefaultLocale")
	public static void installPackage(Context context, File apkFile)
	{
		if (apkFile.isFile())
		{
			LogTools.d("installApk", "file-------" + apkFile.getName());
			String fileName = apkFile.getName();
			String postfix = fileName.substring(fileName.length() - 4, fileName.length());
			if (postfix.toLowerCase().equals(".apk"))
			{
				String cmd = "chmod 755 " + apkFile.getAbsolutePath();
				try
				{
					Runtime.getRuntime().exec(cmd);
				}
				catch (Exception e)
				{
					LogTools.e(LOG_TAG,"", e);
				}

				LogTools.d("installApk", "file-------True---" + apkFile.getName());
				Uri uri = Uri.fromFile(apkFile);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(uri, "application/vnd.android.package-archive");
				context.startActivity(intent);
			}
		}
		else if (apkFile.isDirectory())
		{
			LogTools.d("installApk", "directory");
			File[] files = apkFile.listFiles();
			int fileCount = files.length;
			for (int i = 0; i < fileCount; i++)
			{
				LogTools.d("installApk", "file-------" + i + "------" + files[i].getPath());
				installPackage(context, files[i]);
			}
		}
	}

	/**
	 * 该方法的作用:安装apk应用
	 * 
	 * @date 2013-3-1
	 * @param context
	 * @param filePath
	 *            apk文件的路径
	 */
	public static void installPackage(Context context, String filePath)
	{
		File file = new File(filePath);
		installPackage(context, file);
	}

	/**
	 * 该方法的作用:卸载apk文件
	 * 
	 * @date 2013-3-1
	 * @param context
	 * @param packageUri
	 */
	public static void uninstallPackage(Context context, Uri packageUri)
	{
		Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
		context.startActivity(intent);
	}

	/**
	 * 该方法的作用：根据包名对应已安装的应用对象
	 * 
	 * @date 2013-3-1
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static ApplicationInfo getApplicationInfoByName(Context context, String packageName)
	{
		if (null == packageName || "".equals(packageName))
		{
			return null;
		}
		try
		{
			return context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
		}
		catch (NameNotFoundException e)
		{
			LogTools.e("EspanceUtils", packageName + " NameNotFoundException");
			return null;
		}
	}

	/**
	 * 该方法的作用: 通过报名获取包信息
	 * 
	 * @date 2013-12-3
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static PackageInfo getPackageInfoByName(Context context, String packageName)
	{
		if (null == packageName || "".equals(packageName))
		{
			return null;
		}
		try
		{
			return context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
		}
		catch (NameNotFoundException e)
		{
			LogTools.e(LOG_TAG, "",e);
			return null;
		}
	}

	/**
	 * 该方法的作用:判断apk包是否安装
	 * 
	 * @date 2013-3-10
	 * @param context
	 * @param packageName
	 *            apk包名
	 * @return
	 */
	public static boolean isPackageIntalled(Context context, String packageName)
	{
		if (null == getApplicationInfoByName(context, packageName))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * 获取MetaData
	 * @param context
	 * @param key metaData的key
	 * @return
	 */
	public static String getMetaData(Context context, String key)
	{
		Bundle metaData = null;
		String metaDataValue = null;
		try
		{
			ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != applicationInfo)
			{
				metaData = applicationInfo.metaData;
			}
			
			if (null != metaData)
			{
				metaDataValue = metaData.getString(key);
				if ((null == metaDataValue) || metaDataValue.length() != 24)
				{
					metaDataValue = "";
				}
			}
		}
		catch (NameNotFoundException e)
		{
			LogTools.e(LOG_TAG, e);
		}
		return metaDataValue;
	}
}
