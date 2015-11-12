package com.mn.tiger.log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.content.Context;

import com.mn.tiger.utility.NetworkUtils;

public class LogReporter
{

	/**
	 * 将输入流转换为字节流
	 * 
	 * @param is
	 *            输入流
	 * @return 转换后得到的字节流
	 * @throws Exception
	 */
	private static byte[] readStream(InputStream is) throws Exception
	{
		byte[] buffer = new byte[1024 * 4];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int len = 0;
		// 循环获取输入流，并转换到字节流当中
		while ((len = is.read(buffer)) != -1)
		{
			bos.write(buffer, 0, len);
		}
		is.close();
		return bos.toByteArray();
	}

	/**
	 * 发送字节流到服务器
	 * 
	 * @param strurl
	 *            服务器地址
	 * @param data
	 *            要发送的字节流数据
	 * @throws Exception
	 */
	private static void sendData(String strurl, byte[] data, Map<String, String> requestParams)
			throws Exception
	{
		try
		{
			URL url = new URL(strurl);
			// 打开连接
			HttpURLConnection conn;
			conn = (HttpURLConnection) url.openConnection();
			// 设置提交方式
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			// post方式不能使用缓存
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			// 设置连接超时时间
			conn.setConnectTimeout(6 * 1000);
			// 配置本次连接的Content-Type
			conn.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
			// 维持长连接

			conn.setRequestProperty("Connection", "Keep-Alive");
			// 设置浏览器编码
			conn.setRequestProperty("Charset", "UTF-8");
			// 应服务器方的要求......
			for (Map.Entry<String, String> entry : requestParams.entrySet())
			{
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			// 将请求参数数据向服务器端发送

			dos.write(data);

			dos.flush();
			dos.close();
			if (conn.getResponseCode() == 200)
			{
				// 获得服务器端输出流
				conn.getInputStream();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 发送崩溃日志的入口方法
	 * 
	 * @param context
	 *            调用者的上下文
	 * @param strurl
	 *            接收崩溃日志的服务器url( Universal Resource Locator )
	 * @param requestParams
	 *            要通过get方式发送到服务器的数据
	 * @param path
	 *            崩溃日志的路径
	 * @return 发送成功返回true,失败返回false
	 */
	public static boolean sendCrash(Context context, String strurl,
			Map<String, String> requestParams, String path)
	{
		if (NetworkUtils.getNetworkState(context) == NetworkUtils.WIFI)
		{
			return false;
		}
		InputStream is = null;
		byte[] data;
		try
		{
			File file = new File(path);
			is = new FileInputStream(file);
			data = readStream(is);
			sendData(strurl, data, requestParams);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if (is != null)
				{
					is.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}
}
