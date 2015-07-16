package com.mn.tiger.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.mn.tiger.log.LogTools;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 该类作用及功能说明:获取手机相关的信息
 * 
 * @date 2014-2-11
 */
public class PhoneUtils
{
	/**
	 * 日志标签
	 */
	protected static final String LOG_TAG = PhoneUtils.class.getSimpleName();

	/** 中国移动 */
	private static final int CHINA_MOBILE = 1;
	/** 中国联通 */
	private static final int UNICOM = 2;
	/** 中国电信 */
	private static final int TELECOMMUNICATIONS = 3;
	/** 获取失败 */
	private static final int FAILURE = 0;

	/**
	 * 该方法的作用:获取手机MAC地址
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context)
	{
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		return info.getMacAddress();
	}
	
	/**
	 * 获取peerid(mac地址移除符号)
	 * @param context
	 * @return
	 */
	public static String getPeerid(Context context)
	{
		String mPeerId = "";
		String mac = getMacAddress(context);
		mac += "004V";
		mPeerId = mac.replaceAll(":", "");
		mPeerId = mPeerId.replaceAll(",", "");
		mPeerId = mPeerId.replaceAll("[.]", "");
		mPeerId = mPeerId.toUpperCase();
		return mPeerId;
	}

	/**
	 * 该方法的作用:获取TelephonyManager对象
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @return
	 */
	private static TelephonyManager getTelManager(Context context)
	{
		return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}

	/**
	 * 该方法的作用:获取DeviceId
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @return
	 */
	public static String getDeviceID(Context context)
	{
		String deviceID = getTelManager(context).getDeviceId();
		if(TextUtils.isEmpty(deviceID))
		{
			deviceID = getPeerid(context);
		}
		
		return deviceID;
	}

	/**
	 * 该方法的作用:获取IMSI号
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context)
	{
		return getTelManager(context).getSubscriberId();
	}

	/**
	 * 该方法的作用: 获取厂商信息
	 * 
	 * @date 2014-1-23
	 * @return
	 */
	public static String getProductModel()
	{
		return android.os.Build.MODEL;
	}

	/**
	 * 该方法的作用:获取release版本
	 * 
	 * @date 2014-1-23
	 * @return
	 */
	public static String getSysReleaseVersion()
	{
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 该方法的作用:获取SDK_INT 版本
	 * 
	 * @date 2014-1-23
	 * @return
	 */
	public static int getSDKINTVersion()
	{
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 该方法的作用:获取手机号码
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context)
	{
		return getTelManager(context).getLine1Number();
	}

	/**
	 * 该方法的作用:获取当前运营商
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @return 返回0 表示获取失败 1表示为中国移动 2为中国联通 3为中国电信
	 */
	public static int getProviderName(Context context)
	{
		String IMSI = getIMSI(context);
		if (IMSI == null)
		{
			return FAILURE;
		}
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002"))
		{
			return CHINA_MOBILE;
		}
		else if (IMSI.startsWith("46001"))
		{
			return UNICOM;
		}
		else if (IMSI.startsWith("46003"))
		{
			return TELECOMMUNICATIONS;
		}
		return FAILURE;
	}

	/**
	 * 该方法的作用:通过传入的voiceType获取不同的音量值
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @param voiceType
	 * @return
	 */
	private static int getVoice(Context context, int voiceType)
	{
		AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int voiceValue = manager.getStreamVolume(voiceType);
		return voiceValue;
	}

	/**
	 * 该方法的作用:获取当前通话音音量
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @return
	 */
	public static int getCallVoice(Context context)
	{
		return getVoice(context, AudioManager.STREAM_VOICE_CALL);
	}

	/**
	 * 该方法的作用:获取当前提示音音量
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @return
	 */
	public static int getAlarmVoice(Context context)
	{
		return getVoice(context, AudioManager.STREAM_ALARM);
	}

	/**
	 * 该方法的作用:获取当前铃声音量
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @return
	 */
	public static int getRingVoice(Context context)
	{
		return getVoice(context, AudioManager.STREAM_RING);
	}

	/**
	 * 该方法的作用:获取当前音乐音量
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @return
	 */
	public static int getMusicVoice(Context context)
	{
		return getVoice(context, AudioManager.STREAM_MUSIC);
	}

	/**
	 * 该方法的作用:获取当前系统音量
	 * 
	 * @date 2014-1-23
	 * @param context
	 * @return
	 */
	public static int getSystemVoice(Context context)
	{
		return getVoice(context, AudioManager.STREAM_SYSTEM);
	}

	/**
	 * 该方法的作用:获取手机CPU名字
	 * 
	 * @date 2014-1-23
	 * @return
	 */
	public static String getCpuName()
	{
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try
		{
			// 读取文件获取CPU信息
			fileReader = new FileReader("/pro/cpuinfo");
			bufferedReader = new BufferedReader(fileReader);
			String string = bufferedReader.readLine();
			String[] strings = string.split(":\\s+", 2);
			return strings[1];
		}
		catch (FileNotFoundException e)
		{
			LogTools.e(LOG_TAG,"", e);
		}
		catch (IOException e)
		{
			LogTools.e(LOG_TAG,"", e);
		}
		finally
		{
			if (bufferedReader != null)
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException e)
				{
					LogTools.e(LOG_TAG,"", e);
				}
			}
			if (fileReader != null)
			{
				try
				{
					fileReader.close();
				}
				catch (IOException e)
				{
					LogTools.e(LOG_TAG,"", e);
				}
			}
		}
		return null;
	}

	/**
	 * 该方法的作用:直接拨打电话
	 * 
	 * @date 2014年3月19日
	 * @param context
	 * @param phoneNum
	 */
	public static void makeCall(Context context, String phoneNum)
	{
		if (phoneNum != null && phoneNum.trim().length() > 0)
		{
			Intent intent = new Intent(Intent.ACTION_CALL);
			Uri uri = Uri.parse("tel:" + phoneNum);
			intent.setData(uri);
			context.startActivity(intent);
		}
	}

	/**
	 * 该方法的作用:跳转到拨号界面
	 * 
	 * @date 2014年3月19日
	 * @param context
	 * @param phoneNum
	 */
	public static void makeCallDial(Context context, String phoneNum)
	{
		Intent intent = new Intent(Intent.ACTION_DIAL);
		Uri uri = Uri.parse("tel:" + phoneNum);
		intent.setData(uri);
		context.startActivity(intent);
	}

	/**
	 * 该方法的作用:跳转到系统的短信编辑界面
	 * 
	 * @date 2014年3月19日
	 * @param context
	 * @param phoneNum
	 * @param content
	 */
	public static void sendMessage(Context context, String phoneNum, String content)
	{
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		Uri uri = Uri.parse("smsto:" + phoneNum);
		intent.setData(uri);
		intent.putExtra("sms_body", content);
		context.startActivity(intent);
	}

	/**
	 * 该方法的作用:直接发送短信，无界面
	 * 
	 * @date 2014年3月19日
	 * @param context
	 * @param phoneNum
	 * @param content
	 */
	public static void sendHideMessage(Context context, String phoneNum, String content)
	{
		if (phoneNum != null && phoneNum.trim().length() > 0)
		{
			SmsManager manager = SmsManager.getDefault();
			// 消息内容大于70就对消息进行拆分
			if (content.length() > 70)
			{
				// 拆分信息内容
				ArrayList<String> arrayList = manager.divideMessage(content);
				for (String message : arrayList)
				{
					manager.sendTextMessage(phoneNum, null, message, null, null);
				}
			}
			else
			{
				manager.sendTextMessage(phoneNum, null, content, null, null);
			}
		}
	}
}
