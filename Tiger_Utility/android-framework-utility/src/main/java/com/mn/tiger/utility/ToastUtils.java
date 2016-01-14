package com.mn.tiger.utility;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Toast工具类（可自定仪全局显示样式）
 */
public class ToastUtils
{
	/**
	 * 自定义布局ID
	 */
	public static int CUSTOM_LAYOUT_RES_ID = 0;
	
	/**
	 * 自定义布局TextView的ID
	 */
	public static int CUSTOM_LAYOUT_TEXT_VIEW_ID = 0;
	
	/**
	 * 显示时间
	 */
	public static int DURATION = Toast.LENGTH_SHORT;
	
	/**
	 * 上一次显示的toast
	 */
	private static Toast mLastToast;

	/**
	 * 显示Toast
	 * @param ctx
	 * @param message 信息
	 */
	public static void showToast(Context ctx, String message)
	{
		showToast(ctx, message, Toast.LENGTH_LONG);
	}

	/**
	 * 显示Toast
	 * @param ctx
	 * @param msgResId 信息资源Id
	 */
	public static void showToast(Context ctx, int msgResId)
	{
		if (ctx != null)
		{
			String message = ctx.getString(msgResId);
			showToast(ctx, message, DURATION);
		}
	}

	/**
	 * 获取自定义的Toast
	 * @param ctx
	 * @param layoutResId 自定义布局id
	 * @param textViewId 自定义TextView的ID
	 * @param msg 信息
	 * @return
	 */
	private static Toast getCustomToast(Context ctx, int layoutResId, int textViewId, String msg)
	{
		View customView = LayoutInflater.from(ctx).inflate(layoutResId, null);
		TextView textView = (TextView) customView.findViewById(textViewId);
		Toast toast = new Toast(ctx);
		if (textView != null && customView != null)
		{
			textView.setText(msg);
			toast.setView(customView);
		}
		return toast;
	}

	/**
	 * 显示Toast
	 * @param ctx
	 * @param msgResId 信息资源ID
	 * @param msgFormatArgs 信息字符串格式化参数（替换如%1$s的格式化参数）
	 */
	public static void showToast(Context ctx, int msgResId, Object... msgFormatArgs)
	{
		if (ctx != null)
		{
			String msg = ctx.getString(msgResId, msgFormatArgs);
			showToast(ctx, msg);
		}
	}

	/**
	 * 显示Toast
	 * @param ctx
	 * @param message
	 * @param duration
	 */
	public static void showToast(Context ctx, String message, int duration)
	{
		if (!TextUtils.isEmpty(message) && ctx != null)
		{
			cancelLastToast();
			if (CUSTOM_LAYOUT_RES_ID != 0 && CUSTOM_LAYOUT_TEXT_VIEW_ID != 0)
			{
				mLastToast = getCustomToast(ctx, CUSTOM_LAYOUT_RES_ID, CUSTOM_LAYOUT_TEXT_VIEW_ID,
						message);
				mLastToast.setDuration(duration);
				mLastToast.setGravity(Gravity.CENTER, 0, 0);
				mLastToast.show();
			}
			else
			{
				Toast.makeText(ctx, message, duration).show();
			}
		}
	}
	
	/**
	 * 显示Toast
	 * @param ctx
	 * @param msgResId
	 * @param duration
	 */
	public static void showToast(Context ctx, int msgResId, int duration)
	{
		if (ctx != null)
		{
			String msg = ctx.getString(msgResId, msgResId);
			showToast(ctx, msg, duration);
		}
	}

	/**
	 * 取消上一次显示的Toast
	 */
	public static void cancelLastToast()
	{
		if (mLastToast != null)
		{
			try
			{
				mLastToast.cancel();
			}
			catch (Exception e)
			{
			}
		}
	}
}
