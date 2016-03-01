package com.mn.tiger.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.mn.tiger.log.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

public class Commons
{
	private static final Logger LOG = Logger.getLogger(Commons.class);

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
				LOG.e("[Method:closeInputStream]", e);
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
				LOG.e("[Method:closeOutputStream]", e);
			}
		}
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
		return context != null && (context instanceof Activity)
				&& !((Activity) context).isFinishing();
	}

	/**
	 * 合并两个int数组并去除重复元素
	 * @param array_1
	 * @param array_2
	 * @return
	 */
	public static int[] join(int[] array_1, int[] array_2)
	{
		HashSet<Integer> set = new HashSet<Integer>();
		if(null != array_1)
		{
			for (Integer value : array_1)
			{
				set.add(value);
			}
		}

		if(null != array_2)
		{
			for (Integer value : array_2)
			{
				set.add(value);
			}
		}

		int[] result = new int[set.size()];
		Iterator<Integer> iterator = set.iterator();
		int index = 0;
		while(iterator.hasNext())
		{
			result[index] = iterator.next();
			index++;
		}
		return result;
	}

	/**
	 * 合并一个int数组和一个int值并去除重复元素
	 * @param array_1
	 * @param newValue
	 * @return
	 */
	public static int[] join(int[] array_1, int newValue)
	{
		HashSet<Integer> set = new HashSet<Integer>();
		if(null != array_1)
		{
			for (Integer value : array_1)
			{
				set.add(value);
			}
		}

		set.add(newValue);

		int[] result = new int[set.size()];
		Iterator<Integer> iterator = set.iterator();
		int index = 0;
		while(iterator.hasNext())
		{
			result[index] = iterator.next();
			index++;
		}
		return result.length > 0 ? result : null;
	}


	/**
	 * 合并两个long数组，并去重
	 * @param array_1
	 * @param array_2
	 * @return
	 */
	public static long[] join(long[] array_1, long[] array_2)
	{
		HashSet<Long> set = new HashSet<Long>();
		if(null != array_1)
		{
			for (Long value : array_1)
			{
				set.add(value);
			}
		}

		if(null != array_2)
		{
			for (Long value : array_2)
			{
				set.add(value);
			}
		}

		long[] result = new long[set.size()];
		Iterator<Long> iterator = set.iterator();
		int index = 0;
		while(iterator.hasNext())
		{
			result[index] = iterator.next();
			index++;
		}
		return result;
	}

	/**
	 * 合并两个long数组，并去重
	 * @param array_1
	 * @param newValue
	 * @return
	 */
	public static long[] join(long[] array_1, long newValue)
	{
		HashSet<Long> set = new HashSet<Long>();
		if(null != array_1)
		{
			for (Long value : array_1)
			{
				set.add(value);
			}
		}

		set.add(newValue);

		long[] result = new long[set.size()];
		Iterator<Long> iterator = set.iterator();
		int index = 0;
		while(iterator.hasNext())
		{
			result[index] = iterator.next();
			index++;
		}
		return result;
	}

	/**
	 * 合并两个HashMap，并去重
	 * @param map_1
	 * @param map_2
	 * @return
	 */
	public static <K,V> HashMap<K,V> join(HashMap<K,V> map_1, HashMap<K,V> map_2)
	{
		if(null != map_1 && null != map_2)
		{
			map_1.putAll(map_2);
			return map_1;
		}
		else if(null == map_1 && null != map_2)
		{
			return map_2;
		}
		else if(null != map_1 && null == map_2)
		{
			return map_1;
		}

		return null;
	}


	/**
	 * 转换Integer列表为int数组
	 * @param integerList
	 * @return
	 */
	public static int[] convertIntegerListToIntArray(ArrayList<Integer> integerList)
	{
		if(null != integerList && integerList.size() > 0)
		{
			int length = integerList.size();
			int[] intArray = new int[length];
			for (int i = 0; i < length; i++)
			{
				intArray[i] = integerList.get(i);
			}
			return intArray;
		}
		return null;
	}

	/**
	 * 将long列表转换未long数组
	 * @param longList
	 * @return
	 */
	public static long[] convertLongListToLongArray(ArrayList<Long> longList)
	{
		if(null != longList && longList.size() > 0)
		{
			int length = longList.size();
			long[] longArray = new long[length];
			for (int i = 0; i < length; i++)
			{
				longArray[i] = longList.get(i);
			}
			return longArray;
		}
		return null;
	}

	/**
	 * 向数组中插入数据
	 * @param array
	 * @param data
	 * @param index
	 * @param <T>
	 * @return
	 */
	public static <T> T[] insertData2Array(T[] array, T data, int index)
	{
		ArrayList<T> list = new ArrayList<T>(Arrays.asList((array)));
		list.add(index, data);
		T[] result = (T[])Array.newInstance(data.getClass(), list.size());
		list.toArray(result);
		return result;
	}

	/**
	 * 从数组中删除数据
	 * @param array
	 * @param data
	 * @param <T>
	 * @return
	 */
	public static <T> T[] removeDataFromArray(T[] array, T data)
	{
		ArrayList<T> list = new ArrayList<T>(Arrays.asList((array)));
		list.remove(data);
		T[] result = (T[])Array.newInstance(data.getClass(), list.size());
		list.toArray(result);
		return result;
	}

	public static Class getClassOfGenericType(final Class clazz, final int index)
	{
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType))
		{
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0)
		{
			return Object.class;
		}

		String className = params[index].toString();

		try
		{
			if(className.indexOf("class ") > -1)
			{
				//若为类，去除“class ”(注意class后面有一个空格)
				className = className.replace("class ", "").trim();
				return Class.forName(className);
			}
			else if (className.indexOf("[]") > -1)
			{
				//若为数组类型，去除“[]”符号
				className = className.replace("[]", "");
				return Array.newInstance(Class.forName(className), 0).getClass();
			}
		}
		catch (ClassNotFoundException e)
		{
			LOG.e("[Method:getClassOfGenericType]", e);
		}

		return Object.class;

	}

	public static String getClassNameOfGenericType(final Class clazz, final int index)
	{
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType))
		{
			return Object.class.getName();
		}

		Type[] types = clazz.getGenericInterfaces();

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0)
		{
			return Object.class.getName();
		}

		String className = params[index].toString();
		//若为数组类型，去除“[]”符号
		if(className.indexOf("class ") > -1)
		{
			//若为类，去除“class ”(注意class后面有一个空格)
			className = className.replace("class ", "").replace("$",".").trim();
			return className;
		}
		else if(className.indexOf("<") > -1 && className.indexOf(">") > -1)
		{
			className = className.substring(0, className.indexOf("<"));
			return className;
		}
		else
		{
			return className;
		}
	}

    /**
     * 判断是否在主线程
     * @return
     */
	public static boolean isOnMainThread()
	{
		return Looper.myLooper() == Looper.getMainLooper();
	}
}
