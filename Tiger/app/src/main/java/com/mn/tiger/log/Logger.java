package com.mn.tiger.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mn.tiger.utility.FileUtils;

/**
 * @Create at 2013-5-20 下午11:40:17
 * @Version 1.0
 *          <p>
 *          通用Log工具类 可以通过setLogLevel控制log 输出级别
 *          </p>
 */
public class Logger
{
	private String mDefaultTag = "logger";

	private LogLevel mLevel;

	// 当前日志的级别
	private static LogLevel mCurrentLogLevel = LogLevel.LOG_LEVEL_DEBUG;

	private static HashMap<String, SoftReference<Logger>> mCacheLogger = new HashMap<String, SoftReference<Logger>>();

	private OutputStreamWriter mWriter;

	public static final String LOG_FILE_PATH_DEFAULT = "%1$s/%2$s/log/%3$s.log";

	private static SimpleDateFormat mDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	private static SimpleDateFormat mTimeFormatter = new SimpleDateFormat("MM-dd HH:mm:ss");

	private static List<LogConfig> mConfigs = null;
	
	private static String packageName = "tiger";

	static
	{
		initConfig();
	}

	private Logger(String defaultTag, LogLevel level)
	{
		mDefaultTag = defaultTag;
		mLevel = level;
	}

	public String getDefultTag()
	{
		return mDefaultTag;
	}

	public void setLogWriter(OutputStreamWriter writer)
	{
		mWriter = writer;
	}
	
	public static void setPackageName(String packageName)
	{
		Logger.packageName = packageName;
	}

	public static Logger getLogger(Class<?> cls, boolean isLog2File)
	{
		String tag = cls.getSimpleName();
		Package pkg = cls.getPackage();
		String pkgName = "";
		if (pkg != null)
		{
			pkgName = pkg.getName();
		}
		Logger logger = null;
		if (!mCacheLogger.containsKey(tag))
		{
			logger = newInstance2Cache(tag, pkgName);
		}
		else
		{
			SoftReference<Logger> sr = mCacheLogger.get(tag);
			logger = sr.get();
			if (logger == null)
			{
				logger = newInstance2Cache(tag, pkgName);
			}
		}

		if (isLog2File && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		{
			String sdcardPath = Environment.getExternalStorageDirectory().getPath();
			String filePath = String.format(LOG_FILE_PATH_DEFAULT, sdcardPath, packageName,
					mDateFormatter.format(new Date()));
			try
			{
				File logFile = new File(filePath);
				if (!logFile.getParentFile().exists())
				{
					logFile.getParentFile().mkdirs();
				}
				if (logger.mWriter == null)
				{
					logger.mWriter = new FileWriter(filePath, true);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Log.e(Logger.class.getSimpleName(), e.getMessage());
			}
		}

		return logger;
	}

	public static Logger getLogger(Class<?> cls)
	{
		return getLogger(cls, false);
	}

	private static Logger newInstance2Cache(String tag, String pkgName)
	{
		Logger logger;

		LogConfig config = null;
		if (mConfigs != null)
		{
			for (LogConfig cfg : mConfigs)
			{
				if (tag.equals(cfg.filter))
				{
					config = cfg;
					break;
				}
				// 例如 com.jianq.*， 表示com.jianq下面的所有子包， 都是用该配置
				if (!TextUtils.isEmpty(cfg.filter) && cfg.filter.endsWith(".*"))
				{
					String cfgTagPrefix = cfg.filter.substring(0, cfg.filter.lastIndexOf('.'));
					if (pkgName.startsWith(cfgTagPrefix))
					{
						config = cfg;
					}
					break;
				}
			}
		}
		if (config != null)
		{
			logger = new Logger(tag, config.logLevel);
		}
		else
		{
			logger = new Logger(tag, mCurrentLogLevel);
		}

		SoftReference<Logger> srLogger = new SoftReference<Logger>(logger);
		mCacheLogger.put(tag, srLogger);

		return logger;
	}

	private static void initConfig()
	{
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		{
			String logConfigPath = Environment.getExternalStorageDirectory().getPath()
					+ "/jianq.com/logger.json";
			File logConfigFlie = new File(logConfigPath);
			if (logConfigFlie.exists())
			{
				String configStr = FileUtils.readFile(logConfigPath);
				mConfigs = new Gson().fromJson(configStr, new TypeToken<List<LogConfig>>(){}.getType());
			}
		}
	}

	public static void setGlobalLogLevel(LogLevel logLevel)
	{
		mCurrentLogLevel = logLevel;
	}

	public static LogLevel getGlobalLogLevel()
	{
		return mCurrentLogLevel;
	}

	public void e(String msg)
	{
		e(mDefaultTag, msg);
	}

	public void e(Throwable throwable)
	{
		e(mDefaultTag, throwable);
	}

	public void e(String msg, Throwable throwable)
	{
		if (mCurrentLogLevel.getValue() >= LogLevel.LOG_LEVEL_ERROR.getValue())
		{
			Log.e(mDefaultTag, msg, throwable);
			writeFile("E", mDefaultTag, msg + "--->" + throwable.getMessage());
		}
	}

	public void w(String msg)
	{
		w(mDefaultTag, msg);
	}

	public void d(String msg)
	{
		d(mDefaultTag, msg);
	}

	public void i(String msg)
	{
		i(mDefaultTag, msg);
	}

	public void w(String tag, String msg)
	{
		if (mCurrentLogLevel.getValue() >= LogLevel.LOG_LEVEL_WARN.getValue())
		{
			Log.w(tag, msg);
			writeFile("W", tag, msg);
		}
	}

	private void writeFile(String level, String tag, String msg)
	{
		String line = level + ":" + tag + "\t" + mTimeFormatter.format(new Date()) + "\t" + msg;
		if (mWriter != null)
		{
			try
			{
				mWriter.write(line);
				mWriter.flush();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void d(String tag, String msg)
	{
		if (mCurrentLogLevel.getValue() >= LogLevel.LOG_LEVEL_DEBUG.getValue())
		{
			Log.d(tag, msg);
			writeFile("D", tag, msg);
		}
	}

	public void e(String tag, String msg)
	{
		if (mCurrentLogLevel.getValue() >= LogLevel.LOG_LEVEL_ERROR.getValue())
		{
			Log.e(tag, msg);
			writeFile("E", tag, msg);
		}
	}

	public void e(String tag, String msg, Throwable throwable)
	{
		if (mCurrentLogLevel.getValue() >= LogLevel.LOG_LEVEL_ERROR.getValue())
		{
			Log.e(tag, msg, throwable);
			writeFile("E", tag, msg + "--->" + throwable.getMessage());
		}
	}

	public void i(String tag, String msg)
	{
		if (mCurrentLogLevel.getValue() >= LogLevel.LOG_LEVEL_INFO.getValue())
		{
			Log.i(tag, msg);
			writeFile("I", tag, msg);
		}
	}

	/**
	 * alias of report(message), the message will be reported to the specified
	 * server site.
	 * 
	 * @param message
	 */
	public void r(String message)
	{

	}

	public LogLevel getLogLevel()
	{
		return mLevel;
	}

	public void setLogLevel(LogLevel level)
	{
		mLevel = level;
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		if (mWriter != null)
		{
			try
			{
				mWriter.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			mWriter = null;
		}
	}
}
