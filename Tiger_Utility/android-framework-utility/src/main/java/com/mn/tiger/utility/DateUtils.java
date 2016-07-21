package com.mn.tiger.utility;

import android.annotation.SuppressLint;
import android.content.Context;

import com.mn.tiger.log.Logger;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * 该类作用及功能说明:与日期相关的操作
 *
 * @date 2014-2-11
 */
public class DateUtils
{
	private static final Logger LOG = Logger.getLogger(DateUtils.class);

	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	public final static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public final static int ONE_MINUTES = 1000 * 60;
	public final static int ONE_HOURS = ONE_MINUTES * 60;
	public final static int ONE_DAY = ONE_HOURS * 24;

	/**
	 * 通过传入的format格式将时间转换为字符串
	 * @param milliseconds
	 * @param format
	 * @return
	 */
	public static String date2String(long milliseconds, String format)
	{
		if (milliseconds > 0 && format != null)
		{
			return date2String(new Date(milliseconds), format);
		}
		return "";
	}

	/**
	 * 通过传入的format格式将时间转换为字符串
	 * @param milliseconds
	 * @param format
	 * @return
	 */
	public static String date2String(long milliseconds, DateFormat format)
	{
		if (milliseconds > 0 && format != null)
		{
			return date2String(new Date(milliseconds), format);
		}
		return "";
	}

	/**
	 * 该方法的作用:通过传入的format格式将日期转换为字符串
	 *
	 * @date 2014-1-23
	 * @param date
	 * @param format
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String date2String(Date date, String format)
	{
		if (date != null && format != null)
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(date);
		}
		return null;
	}

	/**
	 * 该方法的作用:通过传入的format格式将日期转换为字符串
	 *
	 * @date 2014-1-23
	 * @param date
	 * @param format
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String date2String(Date date, DateFormat format)
	{
		if (date != null && format != null)
		{
			return format.format(date);
		}
		return null;
	}

	/**
	 * 该方法的作用:通过传入的format格式 将日期转换为字符串
	 * @date 2014-1-23
	 * @param dateString
	 * @param format
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date string2Date(String dateString, String format)
	{
		if (!StringUtils.isEmptyOrNull(dateString) && !StringUtils.isEmptyOrNull(format))
		{
			Date date = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			try
			{
				date = dateFormat.parse(dateString);
			}
			catch (ParseException e)
			{
				LOG.e("[Method:string2Date]", e);
			}
			return date;
		}
		return null;
	}

	/**
	 * 该方法的作用:使用compareTo方法比较日期大小
	 *
	 * @date 2014-1-23
	 * @param date1
	 * @param date2
	 * @return 返回 1 date1>date2 -1 date1<date2 0 date1=date2 3 传入的格式有误
	 */
	public static int compareDate(Date date1, Date date2)
	{
		if (date1 != null && date2 != null)
		{
			return date1.compareTo(date2);
		}
		return 3;
	}

	/**
	 * 该方法的作用:该方法的作用:使用compareTo方法比较日期大小
	 *
	 * @date 2014-1-23
	 * @param string1
	 * @param format1
	 * @param string2
	 * @param format2
	 * @return 返回 1 date1>date2 -1 date1<date2 0 date1=date2 3 传入的格式有误
	 */
	public static int compareDate1(String string1, String format1, String string2, String format2)
	{
		Date date1 = string2Date(string1, format1);
		Date date2 = string2Date(string2, format2);
		if (date1 == null || date2 == null)
		{
			return 3;
		}
		int i = date1.compareTo(date2);
		if (i > 0)
		{
			return 1;
		}
		else if (i < 0)
		{
			return -1;
		}
		return i;
	}

	/**
	 * 该方法的作用:获取星期几
	 * @date 2014-1-23
	 * @param date
	 * @return -1为获取失败 0为周日 1为周一 ....6为周六
	 */
	public static int getWeekOfDate(Date date)
	{
		if (date != null)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
			if (day < 0)
			{
				day = 0;
			}
			return day;
		}
		return -1;
	}

	/**
	 * 该方法的作用:将传入的string和format得到Date对象，再获取星期几
	 * @date 2014-1-23
	 * @param dateString
	 * @param format
	 * @return -1为获取失败 0为周日 1为周一 ....6为周六
	 */
	public static int getWeekOfString(String dateString, String format)
	{
		if (!StringUtils.isEmptyOrNull(dateString) && !StringUtils.isEmptyOrNull(format))
		{
			Date date = string2Date(dateString, format);
			if (date == null)
			{
				return -1;
			}
			int day = getWeekOfDate(date);
			return day;
		}
		return -1;
	}

	/**
	 * 如果是一天内的，显示为"XX小时/分钟 前"，超过一天的，直接显示日期
	 *
	 * @param ctx
	 * @param time
	 * @return
	 */
	public static String computeHowLongAgo(Context ctx, long time, String dateFormat)
	{
		long now = System.currentTimeMillis();
		long diff = now - time;
		String timeAgo = "";
		Format format = new SimpleDateFormat(dateFormat);

		if (diff > ONE_DAY)
		{
			timeAgo = format.format(new Date(time));
		}
		else if (diff > ONE_HOURS)
		{
			int hours = (int) (diff / ONE_HOURS);
            if(hours > 1)
            {
                timeAgo = ctx.getString(R.string.hours_ago, hours);
            }
            else
            {
                timeAgo = ctx.getString(R.string.hour_ago, hours);
            }
		}
		else if (diff > ONE_MINUTES)
		{
            int minutes = (int) (diff / ONE_MINUTES);
            if(minutes > 1)
            {
                timeAgo = ctx.getString(R.string.minutes_ago, minutes);
            }
            else
            {
                timeAgo = ctx.getString(R.string.minute_ago, minutes);
            }
		}
		else
		{
			timeAgo = ctx.getString(R.string.just_now);
		}

		return timeAgo;
	}
}
