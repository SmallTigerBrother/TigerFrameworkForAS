package com.mn.tiger.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.mn.tiger.app.TGApplication;
import com.mn.tiger.log.LogTools;

public class Commons
{
	private static final String LOG_TAG = Commons.class.getSimpleName();

	/**
	 * 该方法的作用:获取系统语言
	 * 
	 * @date 2014年6月10日
	 * @param context
	 * @return
	 */
	public static String getSystemLanguage(Context context)
	{
		Resources resource = context.getResources();
		Configuration configuration = resource.getConfiguration();
		Locale locale = configuration.locale;
		return locale.getLanguage();
	}

	/**
	 * 该方法的作用:获取本地的设备ID
	 * 
	 * @date 2014年2月15日
	 * @param context
	 * @return
	 */
	@SuppressLint("WorldWriteableFiles")
	public static String getLocalDeviceID(Context context)
	{
		String uid = "";
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (null != telephonyManager)
		{
			// 默认取Device_ID
			uid = telephonyManager.getDeviceId();
		}

		if (null == uid)
		{
			// 默认取Android_ID
			uid = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}

		if (null != uid && uid.matches("^[A-Za-z0-9]+$"))
		{
			if ("0".equals(uid) || "000000000000000".equals(uid))
			{
				if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 9)
				{//
					uid = NetworkUtils.getLocalMacAddress(context);
				}
				else
				{// 2.3及以上版本可用
					uid = android.os.Build.SERIAL;
				}
			}
		}
		return uid;
	}

	/**
	 * 该方法的作用:关闭输入流
	 * 
	 * @date 2013-3-10
	 * @param stream
	 */
	public static void closeInputStream(InputStream stream)
	{
		if (stream != null)
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				LogTools.e(LOG_TAG, "", e);
			}
		}
	}

	/**
	 * 该方法的作用:关闭输出流
	 * 
	 * @date 2013-3-10
	 * @param stream
	 */
	public static void closeOutputStream(OutputStream stream)
	{
		if (stream != null)
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				LogTools.e(LOG_TAG, "", e);
			}
		}
	}

	/**
	 * 退出程序
	 */
	public static void exit()
	{
		TGApplication.getInstance().exit();
	}
	
	/**
	 * 该方法的作用: 切换系统语言
	 * 
	 * @date 2014年1月7日
	 * @param context
	 * @param language
	 */
	public static void changeSystemLanguage(Context context, String language)
	{
		Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();
		if (Locale.CHINESE.toString().equals(language))
		{
			config.locale = Locale.CHINESE;
		}
		else
		{
			config.locale = new Locale(language);
		}
		resources.updateConfiguration(config, null);
	}

	/**
	 * 
	 * 该方法的作用:检测当前创建或显示Dialog的Context是否有效
	 * @date 2014年8月12日
	 * @param context
	 * @return true有效可以创建或显示，fasle则相反
	 */
	public static boolean checkContextIsValid(Context context)
	{
		if (context != null && (context instanceof Activity)
				&& !((Activity) context).isFinishing())
		{
			return true;
		}
		return false;
	}
}
