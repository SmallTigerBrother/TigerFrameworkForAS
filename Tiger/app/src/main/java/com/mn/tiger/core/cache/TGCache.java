package com.mn.tiger.core.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.text.TextUtils;

import com.mn.tiger.log.LogTools;

/**
 * 该类作用及功能说明 缓存文件和读取文件工具类
 * 
 * @version V2.0
 * @see JDK1.6,android-8
 */
public class TGCache
{
	public static final String LOG_TAG = TGCache.class.getSimpleName();

	public static final String SAVE_NORMAL_TYPE = ".tg";

	public static void setDiskCacheSize(int maxSize)
	{

	}

	/**
	 * 
	 * 该方法的作用:保存缓存
	 * 
	 * @date 2014年5月13日
	 * @param context
	 *            上下文
	 * @param key
	 *            文件名称(唯一标识，md5 url)缓存文件名称以此命名
	 * @param value
	 *            需要缓存的内容
	 */
	public static void saveCache(Context context, String key, Object value)
	{
		if (TextUtils.isEmpty(key))
		{
			throw new NullPointerException("key can't be empty!");
		}

		if (null == value)
		{
			throw new NullPointerException("value can't be empty!");
		}

		File normal_file = getNormalCacheFile(context, key);
		if (null != normal_file)
		{
			saveAsDiskCache(normal_file.getAbsolutePath(), value);
		}
		else
		{
			LogTools.e(LOG_TAG, "save cache normal_file is null!");
		}
	}

	/**
	 * 该方法的作用:获取默人文件类型的缓存(默认在cache文件夹下)
	 * 
	 * @date 2014年3月4日
	 * @param context
	 * @param key
	 *            文件名称(唯一标识，md5 url)缓存文件名称以此命名
	 * @return
	 */
	public static Object getCache(Context context, String key)
	{
		File cacheFile = null;
		Object content = null;

		cacheFile = getNormalCacheFile(context, key);
		if (cacheFile.exists())
		{
			content = getDiskCache(cacheFile.getAbsolutePath());
		}

		return content;
	}

	/**
	 * 该方法的作用:获取硬盘缓存内容
	 * 
	 * @date 2014年3月4日
	 * @param fileAbsPath
	 *            文件绝对路径
	 * @return
	 */
	public static Object getDiskCache(String fileAbsPath)
	{
		Object object = null;
		ObjectInputStream objectInputStream = null;
		FileInputStream inputStream = null;
		try
		{
			File file = new File(fileAbsPath);
			if (file == null || !file.exists())
			{
				return null;
			}

			inputStream = new FileInputStream(file);
			objectInputStream = new ObjectInputStream(new BufferedInputStream(inputStream));

			object = objectInputStream.readObject();
		}
		catch (Exception e)
		{
			LogTools.e(e);
		}
		finally
		{
			closeInputStream(inputStream);
			closeInputStream(objectInputStream);
		}

		return object;
	}

	/**
	 * 该方法的作用:缓存到硬盘文件
	 * 
	 * @date 2014年3月4日
	 * @param fileAbsPath
	 *            文件绝对路径
	 * @param content
	 *            文件内容
	 */
	private static void saveAsDiskCache(String fileAbsPath, Object content)
	{
		ObjectOutputStream objectoutputStream = null;
		FileOutputStream outStream = null;
		try
		{
			File cacheFile = new File(fileAbsPath);
			if (!cacheFile.exists())
			{
				cacheFile.createNewFile();
			}

			outStream = new FileOutputStream(cacheFile);
			objectoutputStream = new ObjectOutputStream(new BufferedOutputStream(outStream));
			objectoutputStream.writeObject(content);
			objectoutputStream.flush();
		}
		catch (Exception e)
		{
			LogTools.e(e);
		}
		finally
		{
			closeOutputStream(outStream);
			closeOutputStream(objectoutputStream);
		}
	}

	/**
	 * 
	 * 该方法的作用:获取普通类型缓存文件
	 * 
	 * @date 2014年5月14日
	 * @param context
	 * @param key
	 * @return
	 */
	private static File getNormalCacheFile(Context context, String key)
	{
		File saveFile = new File(getCacheBaseDir(context), key + TGCache.SAVE_NORMAL_TYPE);
		return saveFile;
	}

	/**
	 * 
	 * 该方法的作用:获取缓存的根目录
	 * 
	 * @date 2014年5月14日
	 * @param context
	 * @return
	 */
	private static File getCacheBaseDir(Context context)
	{
		File saveFileDir = context.getCacheDir();
		if (!saveFileDir.exists())
		{
			saveFileDir.mkdirs();
		}
		return saveFileDir;
	}

	/**
	 * 该方法的作用: 将object对象写入outFile文件
	 * 
	 * @date 2013-8-14
	 * @param outFile
	 * @param object
	 * @param context
	 */
	public static void writeObject(String outFile, Object object, Context context)
	{
		File dir = context.getDir("cache", Context.MODE_PRIVATE);
		File file = new File(dir, outFile);
		saveAsDiskCache(file.getAbsolutePath(), object);
	}

	/**
	 * 该方法的作用: 以object的方式读取文件中的内容
	 * 
	 * @date 2013-8-14
	 * @param filePath
	 * @param context
	 */
	public static Object readObject(String filePath, Context context)
	{
		File dir = context.getDir("cache", Context.MODE_PRIVATE);
		File file = new File(dir, filePath);
		return getDiskCache(file.getAbsolutePath());
	}
	
	/**
	 * 移除缓存
	 * @param context
	 * @param key 缓存数据的键
	 */
	public static void removeCache(Context context, String key)
	{
		File cacheFile = getNormalCacheFile(context, key);
		if(cacheFile.exists())
		{
			cacheFile.delete();
		}
	}

	/**
	 * 该方法的作用:关闭输出流
	 * 
	 * @date 2014年5月12日
	 * @param stream
	 */
	public static void closeOutputStream(OutputStream stream)
	{
		if (null != stream)
		{
			try
			{
				stream.close();
			}
			catch (Exception e)
			{
				LogTools.e(e);
			}
		}
	}

	/**
	 * 该方法的作用:关闭输入流
	 * 
	 * @date 2014年5月12日
	 * @param stream
	 */
	public static void closeInputStream(InputStream stream)
	{
		if (null != stream)
		{
			try
			{
				stream.close();
			}
			catch (Exception e)
			{
				LogTools.e(e);
			}
		}
	}

}
