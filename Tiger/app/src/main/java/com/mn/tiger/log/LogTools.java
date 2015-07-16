package com.mn.tiger.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mn.tiger.utility.FileUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;


@SuppressLint("SimpleDateFormat")
public class LogTools
{
	/** 日志写入文件开关(日志不输出到本地，修改为false) */
	private static Boolean LOG_WRITE_TO_FILE = false;

	/** 日志写控制台开关(日志不输出到控制台, 默认为true) */
	private static Boolean LOG_PRINT_TO_CONSOLE = true;

	/** debug级别 (生产环境，务必修改打印级别) */
	private static DebugLevel sDebugLevel = DebugLevel.VERBOSE;

	/** 日志文件在sdcard中的路径 */
	private static String LOG_PATH_SDCARD_DIR = "";

	/** sd卡中日志文件的最多保存天数 */
	private static int SDCARD_LOG_FILE_SAVE_DAYS = 7;

	/** 本类输出的日志文件后缀名称(文件名格式为:yyyy-MM-dd_W3_log.txt) */
	private static String LOG_FILE_PREFIX_NAME = "_W3_log.txt";

	/** 日志的输出格式 */
	private static SimpleDateFormat LogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
	/** 日志文件格式 */
	private static final SimpleDateFormat logfileSf = new SimpleDateFormat("yyyy-MM-dd");

	private static String sTag = "MEAP";

	/** 日志文件目录名称 */
	private static String LOG_FILE_DIR_NAME = "";

	/**
	 * 该方法的作用:设置应用日志文件所在的目录名称
	 * 
	 * @date 2014年1月2日
	 * @param dirName
	 */
	public static void setAppLogFileDir(String dirName)
	{
		LOG_FILE_DIR_NAME = dirName;
	}

	/**
	 * 该方法的作用:根据配置文件判定是否打开打印
	 * 生产环境，PROGRESS和ERROR的级别日志写入文件，但不写控制台；其他环境日志文件打印到控制台，也写入文件。
	 * 
	 * @param context
	 * @date 2013-6-4
	 */
	public static void switchLog(Context context)
	{
		//TODO
		setDebugLevel(DebugLevel.VERBOSE);
		printFileOn();
		printConsoleOn();
	}

	/*** 打开写文件日志开关 */
	public static void printFileOn()
	{
		if (!LOG_WRITE_TO_FILE)
		{
			LOG_WRITE_TO_FILE = true;
		}
	}

	/** 关闭写文件日志开关 */
	public static void printFileOff()
	{
		if (LOG_WRITE_TO_FILE)
		{
			LOG_WRITE_TO_FILE = false;
		}
	}

	/*** 打开控制台日志开关 */
	public static void printConsoleOn()
	{
		if (!LOG_PRINT_TO_CONSOLE)
		{
			LOG_PRINT_TO_CONSOLE = true;
		}
	}

	/** 关闭控制台日志开关 */
	public static void printConsoleOff()
	{
		if (LOG_PRINT_TO_CONSOLE)
		{
			LOG_PRINT_TO_CONSOLE = false;
		}
	}

	public static String getTag()
	{
		return sTag;
	}

	public static void setTag(final String pTag)
	{
		sTag = pTag;
	}

	/**
	 * 该方法的作用:获取打印级别
	 * 
	 * @date 2014年3月14日
	 * @return
	 */
	public static DebugLevel getDebugLevel()
	{
		return sDebugLevel;
	}

	/**
	 * 该方法的作用:设置debug日期级别
	 * 
	 * @date 2014年3月14日
	 * @param pDebugLevel
	 *            级别顺序:NONE, ERROR, PROCESS, WARNING, INFO, DEBUG, VERBOSE
	 *            （定义日志级别为PROGRESS的话,打印包括PROGRESS和ERROR的日志）
	 */
	public static void setDebugLevel(final DebugLevel pDebugLevel)
	{
		if (pDebugLevel == null)
		{
			throw new IllegalArgumentException("pDebugLevel must not be null!");
		}
		sDebugLevel = pDebugLevel;
	}

	public static void v(final String pMessage)
	{
		v(sTag, pMessage, null);
	}

	public static void v(final String pMessage, final Throwable pThrowable)
	{
		v(sTag, pMessage, pThrowable);
	}

	public static void v(final String pTag, final String pMessage)
	{
		v(pTag, pMessage, null);
	}

	/**
	 * 
	 * 该方法的作用:v级别og日志 参数: 返回值: 异常: 在什么情况下调用:
	 * 
	 * @date 2012-6-18
	 * @param pTag
	 * @param pMessage
	 * @param pThrowable
	 */
	public static void v(final String pTag, final String pMessage, final Throwable pThrowable)
	{
		if (sDebugLevel.isSameOrLessThan(DebugLevel.VERBOSE))
		{
			String logContent = pMessage;

			if (LOG_PRINT_TO_CONSOLE)
			{
				if (pThrowable == null)
				{
					Log.v(pTag, pMessage);
				}
				else
				{
					Log.v(pTag, pMessage, pThrowable);
				}
			}

			if (pThrowable != null)
			{
				logContent = Log.getStackTraceString(pThrowable);
			}

			if(LOG_WRITE_TO_FILE)
			{
				writeLogtoFile("V", pTag, logContent);
			}
		}
	}

	public static void d(final String pMessage)
	{
		d(sTag, pMessage, null);
	}

	public static void d(final String pMessage, final Throwable pThrowable)
	{
		d(sTag, pMessage, pThrowable);
	}

	public static void d(final String pTag, final String pMessage)
	{
		d(pTag, pMessage, null);
	}

	/**
	 * 
	 * 该方法的作用:d级别og日志 参数: 返回值: 异常: 在什么情况下调用:
	 * 
	 * @date 2012-6-18
	 * @param pTag
	 * @param pMessage
	 * @param pThrowable
	 */
	public static void d(final String pTag, final String pMessage, final Throwable pThrowable)
	{
		if (sDebugLevel.isSameOrLessThan(DebugLevel.DEBUG))
		{
			String logContent = pMessage;

			if (LOG_PRINT_TO_CONSOLE)
			{
				if (pThrowable == null)
				{
					Log.d(pTag, pMessage);
				}
				else
				{
					Log.d(pTag, pMessage, pThrowable);
				}
			}

			if (pThrowable != null)
			{
				logContent = Log.getStackTraceString(pThrowable);
			}
			
			if(LOG_WRITE_TO_FILE)
			{
				writeLogtoFile("D", pTag, logContent);
			}
		}
	}

	public static void i(final String pMessage)
	{
		i(sTag, pMessage, null);
	}

	public static void i(final String pMessage, final Throwable pThrowable)
	{
		i(sTag, pMessage, pThrowable);
	}

	public static void i(final String pTag, final String pMessage)
	{
		i(pTag, pMessage, null);
	}

	/**
	 * 
	 * 该方法的作用:i级别og日志 参数: 返回值: 异常: 在什么情况下调用:
	 * 
	 * @date 2012-6-18
	 * @param pTag
	 * @param pMessage
	 * @param pThrowable
	 */
	public static void i(final String pTag, final String pMessage, final Throwable pThrowable)
	{
		if (sDebugLevel.isSameOrLessThan(DebugLevel.INFO))
		{
			String logContent = pMessage;

			if (LOG_PRINT_TO_CONSOLE)
			{
				if (pThrowable == null)
				{
					Log.i(pTag, pMessage);
				}
				else
				{
					Log.i(pTag, pMessage, pThrowable);
				}
			}

			if (pThrowable != null)
			{
				logContent = Log.getStackTraceString(pThrowable);
			}
			
			if(LOG_WRITE_TO_FILE)
			{
				writeLogtoFile("I", pTag, logContent);
			}
		}
	}

	public static void w(final String pMessage)
	{
		w(sTag, pMessage, null);
	}

	public static void w(final Throwable pThrowable)
	{
		w(sTag, "", pThrowable);
	}

	public static void w(final String pMessage, final Throwable pThrowable)
	{
		w(sTag, pMessage, pThrowable);
	}

	public static void w(final String pTag, final String pMessage)
	{
		w(pTag, pMessage, null);
	}

	/**
	 * 
	 * 该方法的作用:w级别og日志 参数: 返回值: 异常: 在什么情况下调用:
	 * 
	 * @date 2012-6-18
	 * @param pTag
	 * @param pMessage
	 * @param pThrowable
	 */
	public static void w(final String pTag, final String pMessage, final Throwable pThrowable)
	{
		if (sDebugLevel.isSameOrLessThan(DebugLevel.WARNING))
		{
			String logContent = pMessage;

			if (LOG_PRINT_TO_CONSOLE)
			{
				if (pThrowable == null)
				{
					Log.w(pTag, pMessage);
				}
				else
				{
					Log.w(pTag, pMessage, pThrowable);
				}
			}

			if (pThrowable != null)
			{
				logContent = Log.getStackTraceString(pThrowable);
			}
			if(LOG_WRITE_TO_FILE)
			{
				writeLogtoFile("W", pTag, logContent);
			}
		}
	}

	public static void e(final String pMessage)
	{
		e(sTag, pMessage, null);
	}

	public static void e(final Throwable pThrowable)
	{
		e(sTag, "", pThrowable);
	}

	public static void e(final String pMessage, final Throwable pThrowable)
	{
		e(sTag, pMessage, pThrowable);
	}

	public static void e(final String pTag, final String pMessage)
	{
		e(pTag, pMessage, null);
	}

	/**
	 * 
	 * 该方法的作用:e级别og日志 参数: 返回值: 异常: 在什么情况下调用:
	 * 
	 * @date 2012-6-18
	 * @param pTag
	 * @param pMessage
	 * @param pThrowable
	 */
	public static void e(final String pTag, final String pMessage, final Throwable pThrowable)
	{
		if (sDebugLevel.isSameOrLessThan(DebugLevel.ERROR))
		{
			String logContent = pMessage;

			if (LOG_PRINT_TO_CONSOLE)
			{
				if (pThrowable == null)
				{
					Log.e(pTag, pMessage);
				}
				else
				{
					Log.e(pTag, pMessage, pThrowable);
				}
			}

			if (pThrowable != null)
			{
				logContent = Log.getStackTraceString(pThrowable);
			}
			
			if(LOG_WRITE_TO_FILE)
			{
				writeLogtoFile("E", pTag, logContent);
			}
		}
	}

	public static void p(final String pMessage)
	{
		p(sTag, pMessage, null);
	}

	public static void p(final Throwable pThrowable)
	{
		p(sTag, "", pThrowable);
	}

	public static void p(final String pMessage, final Throwable pThrowable)
	{
		p(sTag, pMessage, pThrowable);
	}

	public static void p(final String pTag, final String pMessage)
	{
		p(pTag, pMessage, null);
	}

	/**
	 * 
	 * 该方法的作用:p级别log,用于打印业务流程中的重要过程日志
	 * 
	 * @date 2012-6-18
	 * @param pTag
	 * @param pMessage
	 * @param pThrowable
	 */
	public static void p(final String pTag, final String pMessage, final Throwable pThrowable)
	{
		if (sDebugLevel.isSameOrLessThan(DebugLevel.PROCESS))
		{
			String logContent = pMessage;

			if (LOG_PRINT_TO_CONSOLE)
			{
				if (pThrowable == null)
				{
					Log.i(pTag, pMessage);
				}
				else
				{
					Log.i(pTag, pMessage, pThrowable);
				}
			}

			if (pThrowable != null)
			{
				logContent = Log.getStackTraceString(pThrowable);
			}
			
			if(LOG_WRITE_TO_FILE)
			{
				writeLogtoFile("P", pTag, logContent);
			}
		}
	}

	/**
	 * 打开日志文件并写入日志
	 * 
	 * @return
	 * **/
	private static synchronized void writeLogtoFile(String logLevel, String tag, String text)
	{// 新建或打开日志文件
		// 设置日志文件路径
		if (LOG_PATH_SDCARD_DIR.equals(""))
		{
			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
			if (sdCardExist)
			{
				LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
						+ LOG_FILE_DIR_NAME + File.separator;
			}
			else
			{
				return;
			}
		}

		if (tag.length() < 30)
		{
			for (int i = tag.length(); i < 30; i++)
			{
				tag += " ";
			}
		}

		Date nowtime = new Date();
		String needWriteFileName = logfileSf.format(nowtime) + LOG_FILE_PREFIX_NAME;

		String needWriteMessage = logLevel + "     [" + LogSdf.format(nowtime) + "]         [" + tag + "]        "
				+ text;

		File file = new File(LOG_PATH_SDCARD_DIR, needWriteFileName);

		File dir = new File(file.getParent());
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		else
		{ // 目录存在时，删除过期的日志
			deleteOutDateLog();
		}

		FileWriter filerWriter = null;
		BufferedWriter bufWriter = null;

		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}

			filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
			bufWriter = new BufferedWriter(filerWriter);
			bufWriter.write(needWriteMessage);
			bufWriter.newLine();
			bufWriter.flush();
			filerWriter.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			printFileOff();
		}
		finally
		{
			try
			{
				if (filerWriter != null)
				{
					filerWriter.close();
				}
				if (bufWriter != null)
				{
					bufWriter.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 该方法的作用:删除过时的日志文件
	 * 
	 * @date 2013-8-7
	 */
	private static void deleteOutDateLog()
	{
		File dir = new File(LOG_PATH_SDCARD_DIR);
		if (!dir.exists())
		{
			return;
		}
		for (File file : dir.listFiles())
		{
			if (file.getName().endsWith(LOG_FILE_PREFIX_NAME))
			{
				Date beforeDate = getDateBefore();

				String logDateStr = file.getName().replace(LOG_FILE_PREFIX_NAME, "");
				Date logCalendar = getDateByStr(logDateStr);

				if (logCalendar.before(beforeDate))
				{
					file.delete();
				}
			}
		}
	}

	/**
	 * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
	 * */
	private static Date getDateBefore()
	{
		Calendar now = Calendar.getInstance();
		now.set(Calendar.DATE, now.get(Calendar.DATE) - SDCARD_LOG_FILE_SAVE_DAYS);
		now.set(Calendar.HOUR, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		return now.getTime();
	}

	/**
	 * 该方法的作用:
	 * 
	 * @date 2013-8-7
	 * @return
	 */
	private static synchronized Date getDateByStr(String dateStr)
	{
		Date date = new Date();
		try
		{
			date = logfileSf.parse(dateStr);
		}
		catch (ParseException e)
		{
			LogTools.e(e);
		}
		return date;
	}

	/**
	 * 该方法的作用:删除所有日志文件
	 * 
	 * @date 2013-8-6
	 */
	public static void deleteAllLogFile()
	{
		File dir = new File(LOG_PATH_SDCARD_DIR);
		if (dir.exists())
		{
			FileUtils.deleteFile(dir.getAbsolutePath());
		}
	}

	/**
	 * 
	 * 该类作用及功能说明:该类作用及功能说明---log级别枚举类
	 * 
	 * @date 2013-8-14
	 */
	public static enum DebugLevel implements Comparable<DebugLevel>
	{
		NONE, ERROR, PROCESS, WARNING, INFO, DEBUG, VERBOSE;

		public static DebugLevel ALL = DebugLevel.VERBOSE;

		public boolean isSameOrLessThan(final DebugLevel pDebugLevel)
		{
			return this.compareTo(pDebugLevel) >= 0;
		}
	}
}
