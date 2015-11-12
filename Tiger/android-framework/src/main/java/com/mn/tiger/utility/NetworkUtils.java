package com.mn.tiger.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.mn.tiger.log.Logger;

/**
 * 网络工具类
 */
public class NetworkUtils
{
	private static final Logger LOG = Logger.getLogger(NetworkUtils.class);
	/**
	 * 无网络
	 */
	public final static int NONE = 0;

	/**
	 * Wi-Fi
	 */
	public final static int WIFI = 1;

	/**
	 * 3G,GPRS
	 */
	public final static int MOBILE = 2;

	/**
	 * 该方法的作用: 获取当前网络状态
	 *
	 * @date 2013-8-14
	 * @param context
	 * @return
	 */
	public static int getNetworkState(Context context)
	{
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// 判断是否为Wifi网络
		NetworkInfo wifiNet = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (wifiNet != null
				&& (wifiNet.getState() == State.CONNECTED || wifiNet.getState() == State.CONNECTING))
		{
			return WIFI;
		}

		// 判断是否为手机网络
		NetworkInfo mobileNet = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mobileNet != null
				&& (mobileNet.getState() == State.CONNECTED || mobileNet.getState() == State.CONNECTING))
		{
			return MOBILE;
		}
		return NONE;
	}

	/**
	 * 该方法的作用:判断网络连接是否可用
	 *
	 * @date 2013-3-10
	 * @param context
	 * @return
	 */
	public static boolean isConnectivityAvailable(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (null == connectivityManager)
		{
			return false;
		}
		else
		{
			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			if(networkInfos == null)
			{
				return false;
			}
			else
			{
				for (int i = 0; i < networkInfos.length; i++)
				{
					if (NetworkInfo.State.CONNECTED == networkInfos[i].getState())
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * 该方法的作用: 获取本地WIFI的Mac地址
	 *
	 * @date 2013-9-16
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddress(Context context)
	{
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String wifiAddress = info.getMacAddress();
		if (wifiAddress == null || wifiAddress.trim().equals(""))
		{
			if (!wifi.isWifiEnabled())
			{// 如果没有开wifi,打开wifi
				wifi.setWifiEnabled(true);
				/*
				 * wifiAddress = info.getMacAddress();
				 * wifi.setWifiEnabled(false);
				 */
				wifiAddress = getWifiTimes(50, wifi);
				// 尝试关闭wifi
				if (wifi.isWifiEnabled())
				{
					wifi.setWifiEnabled(false);
				}

			}
		}
		return wifiAddress != null ? wifiAddress : "";
	}

	/**
	 * 该方法的作用: 获取Wifi启动时间
	 *
	 * @date 2014年1月7日
	 * @param times
	 * @param wifi
	 * @return
	 */
	public static String getWifiTimes(int times, WifiManager wifi)
	{
		String macAddress = "";
		// 获取失败，尝试打开wifi获取

		for (int index = 0; index < times; index++)
		{
			// 如果第一次没有成功，第二次做100毫秒的延迟。
			if (index != 0)
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					LOG.e("[Method:getWifiTimes]", e);
				}
			}
			macAddress = wifi.getConnectionInfo().getMacAddress();
			if (macAddress != null && !macAddress.equals(""))
			{
				break;
			}
		}
		return macAddress;
	}
}
