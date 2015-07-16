package com.mn.tiger.utility;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

import com.mn.tiger.log.LogTools;

/**
 * SharedPreferences工具类
 */
public class Preferences
{
	private static final String LOG_TAG = Preferences.class.getSimpleName();
	
	/**
	 * 保存
	 * @param context
	 * @param name  SharedPreferences名称
	 * @param key
	 * @param value
	 */
	@SuppressLint("InlinedApi")
	public static void save(Context context, String name, String key, String value)
	{
		SharedPreferences setting = getSharedPreferences(context, name);
		if(null != setting)
		{
			Editor editer = setting.edit();
			editer.putString(key, value);
			editer.commit();

			return;
		}
		LogTools.e(LOG_TAG, "[Method:save] ——> the context is null");
	}

	/**
	 * 保存
	 * @param context
	 * @param name SharedPreferences名称
	 * @param key
	 * @param value
	 */
	@SuppressLint("InlinedApi")
	public static void save(Context context, String name, String key, int value)
	{
		SharedPreferences setting = getSharedPreferences(context, name);
		if(null != setting)
		{
			Editor editer = setting.edit();
			editer.putInt(key, value);
			editer.commit();
			
			return;
		}
		
		LogTools.e(LOG_TAG, "[Method:save] ——> the context is null");
	}

	/**
	 * 保存
	 * @param context
	 * @param name SharedPreferences名称
	 * @param key
	 * @param value
	 */
	@SuppressLint("InlinedApi")
	public static void save(Context context, String name, String key, long value)
	{
		SharedPreferences setting = getSharedPreferences(context, name);
		if(null != setting)
		{
			Editor editer = setting.edit();
			editer.putLong(key, value);
			editer.commit();

			return;
		}
		LogTools.e(LOG_TAG, "[Method:save] ——> the context is null");
	}

	/**
	 * 保存
	 * @param context 
	 * @param name SharedPreferences名称
	 * @param key
	 * @param value
	 */
	@SuppressLint("InlinedApi")
	public static void save(Context context, String name, String key, boolean value)
	{
		SharedPreferences setting = getSharedPreferences(context, name);
		if(null != setting)
		{
			Editor editer = setting.edit();
			editer.putBoolean(key, value);
			editer.commit();

			return;
		}
		LogTools.e(LOG_TAG, "[Method:save] ——> the context is null");
	}

	/**
	 * 保存
	 * @param context
	 * @param name SharedPreferences名称
	 * @param key
	 * @param value
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void save(Context context, String name, String key, Float value)
	{
		SharedPreferences setting = getSharedPreferences(context, name);
		if(null != setting)
		{
			Editor editer = setting.edit();
			editer.putFloat(key, value);
			editer.commit();

			return;
		}
		LogTools.e(LOG_TAG, "[Method:save] ——> the context is null");
	}

	/**
	 * 读取
	 * @param context
	 * @param name SharedPreferences名称
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressLint("InlinedApi")
	public static boolean read(Context context, String name, String key, boolean defaultValue)
	{
		SharedPreferences setting = getSharedPreferences(context, name);
		if(null != setting)
		{
			return setting.getBoolean(key, defaultValue);
		}
		
		LogTools.e(LOG_TAG, "[Method:read] ——> the context is null");
		return defaultValue;
	}

	/**
	 * 读取
	 * @param context
	 * @param name SharedPreferences名称
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressLint("InlinedApi")
	public static int read(Context context, String name, String key, int defaultValue)
	{
		SharedPreferences setting = getSharedPreferences(context, name);
		if(null != setting)
		{
			return setting.getInt(key, defaultValue);
		}
		
		LogTools.e(LOG_TAG, "[Method:read] ——> the context is null");
		return defaultValue;
	}

	/**
	 * 读取
	 * @param context
	 * @param name SharedPreferences名称
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressLint("InlinedApi")
	public static float read(Context context, String name, String key, float defaultValue)
	{
		SharedPreferences setting = getSharedPreferences(context, name);
		if(null != setting)
		{
			return setting.getFloat(key, defaultValue);
		}
		
		LogTools.e(LOG_TAG, "[Method:read] ——> the context is null");
		return defaultValue;
	}

	/**
	 * 读取
	 * @param context
	 * @param name SharedPreferences名称
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressLint("InlinedApi")
	public static long read(Context context, String name, String key, long defaultValue)
	{
		SharedPreferences setting = getSharedPreferences(context, name);
		if(null != setting)
		{
			return setting.getLong(key, defaultValue);
		}
		
		LogTools.e(LOG_TAG, "[Method:read] ——> the context is null");
		return defaultValue;
	}

	/**
	 * 读取
	 * @param context
	 * @param name SharedPreferences名称
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressLint("InlinedApi")
	public static String read(Context context, String name, String key, String defaultValue)
	{
		SharedPreferences setting = getSharedPreferences(context, name);
		if(null != setting)
		{
			return setting.getString(key, defaultValue);
		}
		
		LogTools.e(LOG_TAG, "[Method:read] ——> the context is null");
		return defaultValue;
	}
	
	/**
	 * 获取SharedPreferences（3.0以上版本启用多进程读写权限）
	 * @param context
	 * @param name SharedPreferences名称
	 * @return
	 */
	@SuppressLint("InlinedApi")
	public static SharedPreferences getSharedPreferences(Context context, String name)
	{
		if(null != context)
		{
			if(Build.VERSION.SDK_INT >= 11)
			{
				return context.getSharedPreferences(name, Context.MODE_MULTI_PROCESS);
			}
			else
			{
			    return context.getSharedPreferences(name, Context.MODE_PRIVATE);
			}
		}
		
		return null;
	}
}
