package com.mn.tiger.utility;

import android.text.TextUtils;
import android.widget.TextView;

import com.mn.tiger.log.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 该类作用及功能说明: 与String相关的一些转换等操作
 *
 * @date 2014-2-11
 */
public class StringUtils
{
	private static final Logger LOG = Logger.getLogger(StringUtils.class);

	/**
	 * 判断TextView中的字符串是否为null或者""
	 * @param textView
	 * @return
	 */
	public static boolean isTextEmpty(TextView textView)
	{
		if(null == textView)
		{
			throw new IllegalArgumentException("the textview can not be null");
		}

		return TextUtils.isEmpty(textView.getText().toString().trim());
	}

	/**
	 * 该方法的作用:判断字符串是否为null 或者为""
	 *
	 * @date 2014-1-23
	 * @param content
	 * @return true表示字符串为null或者""
	 */
	public static boolean isEmptyOrNull(String content)
	{
		return content == null || content.equals("");
	}

	/**
	 * 该方法的作用: 判断字符串是否为IP地址
	 *
	 * @date 2014-01-23
	 * @param ipString
	 * @return
	 */
	public static boolean isIPAddress(String ipString)
	{
		if (ipString != null)
		{
			// 将字符串通过.分割
			String[] singleArray = ipString.split("\\.");
			if (singleArray == null)
			{
				return false;
			}
			// 遍历分割后的所有字符串，转换成int型再进行判断
			for (String numString : singleArray)
			{
				if (isEmptyOrNull(numString.trim()))
				{
					return false;
				}
				try
				{
					int num = Integer.parseInt(numString.trim());
					if (num < 0 || num > 255)
					{
						return false;
					}
				}
				catch (NumberFormatException e)
				{
					LOG.e("[Method:isIPAddress]", e);
					return false;
				}

			}
			return true;
		}
		return false;
	}

	/**
	 * 该方法的作用:通过正则表达式判断是否为email地址
	 *
	 * @date 2014-1-23
	 * @param email
	 * @return
	 */
	public static boolean isEmailAddress(String email)
	{
		boolean tag = true;
		final String pattern1 = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		final Pattern pattern = Pattern.compile(pattern1);
		final Matcher mat = pattern.matcher(email);
		if (!mat.find())
		{
			tag = false;
		}
		return tag;
	}

	/**
	 * 该方法的作用:通过正则表达式判断是否为数字
	 *
	 * @date 2014-1-23
	 * @param digitString
	 * @return
	 */
	public static boolean isDigit(String digitString)
	{
		if (!isEmptyOrNull(digitString))
		{
			String regex = "[0-9]*";
			return isMatch(regex, digitString);
		}
		return false;
	}

	/**
	 * 该方法的作用: 通过正则表达式判断是否为手机号
	 *
	 * @date 2014-01-23
	 * @param phoneString
	 * @return
	 */
	public static boolean isPhoneNumber(String phoneString)
	{
		String format = "^((12[0-9])|(13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";
		return isMatch(format, phoneString);
	}

	/**
	 * 该方法的作用: 字符串正则校验
	 *
	 * @date 2014-01-23
	 * @param regex
	 *            正则表达式
	 * @param string
	 *            需要检验的字符串
	 * @return
	 */
	public static boolean isMatch(String regex, String string)
	{
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(string);
		return matcher.matches();
	}

	/**
	 * 该方法的作用: 通过正则表达式判断是否为URL地址
	 *
	 * @date 2014-01-23
	 * @param strIp
	 * @return
	 */
	public static boolean isUrl(String strIp)
	{
		if(!TextUtils.isEmpty(strIp))
		{
			String strPattern = "^((https?)|(ftp))://(?:(\\s+?)(?::(\\s+?))?@)?([a-zA-Z0-9\\-.]+)"
					+ "(?::(\\d+))?((?:/[a-zA-Z0-9\\-._?,'+\\&%$=~*!():@\\\\]*)+)?$";
			return isMatch(strPattern, strIp);
		}
		return false;
	}

	/**
	 * 字符串转换unicode
	 */
	public static String string2Unicode(String string) {

		StringBuffer unicode = new StringBuffer();

		for (int i = 0; i < string.length(); i++) {

			// 取出每一个字符
			char c = string.charAt(i);

			// 转换为unicode
			unicode.append("\\u" + Integer.toHexString(c));
		}

		return unicode.toString();
	}

	/**
	 * unicode 转字符串
	 */
	public static String unicode2String(String unicode) {

		StringBuffer string = new StringBuffer();

		String[] hex = unicode.split("\\\\u");

		for (int i = 1; i < hex.length; i++) {

			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);

			// 追加成string
			string.append((char) data);
		}

		return string.toString();
	}


	/**
	 * 该方法的作用:获取url中某参数的值 参数:传入需要获取的参数的名 返回:该参数的值(字符串型) 异常:
	 * 在什么情况下调用:需要获取url中某参数的值
	 *
	 * @date 2012-12-24
	 * @param url
	 * @param paramName
	 * @return
	 */
	public static String getParamValueOfUrl(String url, String paramName)
	{
		try
		{
			String urls[] = url.split("[?]");
			if (urls.length > 1)
			{
				String param = urls[1];
				String params[] = param.split("[&]");
				String key = null;
				String value = null;
				for (String string : params)
				{
					String keyAndValue[] = string.split("[=]");
					if (keyAndValue.length > 1)
					{
						key = keyAndValue[0];
						value = keyAndValue[1];
						if (key.equalsIgnoreCase(paramName))
						{
							return value;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			return "";
		}
		return "";
	}

	/**
	 * 方法作用: 通过正则验证身份证号格式
	 * @param id
	 * @return
	 */
	public static boolean isIdCardNumber(String id)
	{
		Pattern pattern = Pattern.compile("/^(\\d{15}$|^\\d{18}$|^\\d{17}(\\d|X|x))$/");
		Matcher matcher = pattern.matcher(id);
		if (matcher.find())
		{
			return true;
		}

		return false;
	}

	/**
	 * 修复字符串的换行符
	 * @param original
	 * @return
	 */
	public static String repairBreakLine(String original)
	{
		String result = original;
		int indexOfN = original.indexOf("n");
		if(indexOfN > -1)
		{
			StringBuilder resultBuilder = new StringBuilder();
			int length = original.length();
			for (int index = 0; index < length; index++)
			{
				if((original.charAt(index) == '\\' && index < length - 1 && original.charAt(index + 1) == 'n'))
				{
					resultBuilder.append('\n');
					index += 1;
				}
				else if(original.charAt(index) == '\n')
				{
					resultBuilder.append('\n');
				}
				else
				{
					resultBuilder.append(original.charAt(index));
				}
			}

			result = resultBuilder.toString();
		}

		LOG.i("[Method:repairBreakLine] original == " + original + "\n result == " + result);

		return result;
	}
}
