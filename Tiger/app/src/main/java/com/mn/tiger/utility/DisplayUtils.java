package com.mn.tiger.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import com.mn.tiger.log.LogTools;

/**
 * 显示工具类
 */
public class DisplayUtils
{
	private static final String LOG_TAG = DisplayUtils.class.getSimpleName();
	
	/**
	 * 该方法的作用:dip转换为像素
	 * 
	 * @date 2013-3-7
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 该方法的作用:sp转换为px（文字大小单位）
	 * 
	 * @date 2013-3-8
	 * @param context
	 * @param spValue
	 * @return
	 */
	public static int sp2px(Context context, float spValue)
	{
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * scaledDensity + 0.5f);
	}

	/**
	 * 该方法的作用:px转换为sp（文字大小单位）
	 * 
	 * @date 2013-3-8
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue)
	{
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / scaledDensity + 0.5f);
	}

	/**
	 * px转换为dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue)
	{
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / density);
	}

	/**
	 * 该方法的作用:获取状态栏的高度
	 * 
	 * @date 2013-3-7
	 * @param activity
	 * @return
	 */
	public static int getStatusHeight(Activity activity)
	{
		int statusHeihgt = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		statusHeihgt = localRect.top;

		if (statusHeihgt == 0)
		{
			Class<?> localClass;
			try
			{
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int statusBar_Id = Integer.parseInt(localClass.getField("status_bar_height")
						.get(localObject).toString());
				statusHeihgt = activity.getResources().getDimensionPixelSize(statusBar_Id);
			}
			catch (Exception e)
			{
				LogTools.e(LOG_TAG,"", e);
			}
		}
		return statusHeihgt;
	}
	
	/**
	 * 该方法的作用:获取屏幕分辨率
	 * @date 2014-1-23
	 * @param context
	 * @return 返回int数组长度为2，0为x 1为y
	 */
	public static int[] getResolution(Activity context)
	{
		int[] size = new int[2];
		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		size[0] = metrics.widthPixels;
		size[1] = metrics.heightPixels;
		return size;
	}
	
	/**
	 * 设置视图自适应图片大小
	 * @param view 需要自适应的View
	 * @param srcImageWidth 图片宽度
	 * @param srcImageHeight 图片高度
	 * @param maxWidth View可现实区域的最大宽度
	 */
	public static void adjustViewSizeByWidth(View view, int srcImageWidth,
			int srcImageHeight, int maxWidth)
	{
		if (srcImageWidth == 0 || srcImageHeight == 0)
		{
			return;
		}
		
		float ratio = (float) srcImageWidth / (float) srcImageHeight;
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		int currentFitWidth = 0;
		int adjustHeight = 0;
		
		if(view.getLayoutParams() instanceof MarginLayoutParams)
		{
			MarginLayoutParams marginParams = (MarginLayoutParams) view.getLayoutParams();
			
			currentFitWidth = maxWidth - marginParams.leftMargin - marginParams.rightMargin;
		}
		else
		{
			currentFitWidth = maxWidth;
		}
		adjustHeight = (int) (currentFitWidth / ratio);

		if(currentFitWidth != view.getWidth() || adjustHeight != view.getHeight())
		{
			if (lp != null)
			{
				lp.height = adjustHeight;
				lp.width = currentFitWidth;
				view.setLayoutParams(lp);
			}
		}
	}
	
	/**
	 * 设置视图自适应图片大小
	 * @param view 需要自适应的View
	 * @param srcImageWidth 图片宽度
	 * @param srcImageHeight 图片高度
	 * @param maxHeight View可现实区域的最大高度
	 */
	public static void adjustViewSizeByHeight(View view, int srcImageWidth,
			int srcImageHeight, int maxHeight)
	{
		if (srcImageWidth == 0 || srcImageHeight == 0)
		{
			return;
		}
		
		float ratio = (float) srcImageWidth / (float) srcImageHeight;
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		int currentFitHeight = 0;
		int adjustWidth = 0;
		
		if(view.getLayoutParams() instanceof MarginLayoutParams)
		{
			MarginLayoutParams marginParams = (MarginLayoutParams) view.getLayoutParams();
			
			currentFitHeight = maxHeight - marginParams.topMargin - marginParams.bottomMargin;
		}
		else
		{
			currentFitHeight = maxHeight;
		}
		
		adjustWidth = (int) (currentFitHeight * ratio);

		if(adjustWidth != view.getWidth() || currentFitHeight != view.getHeight())
		{
			if (lp != null)
			{
				lp.height = currentFitHeight;
				lp.width = adjustWidth;
				view.setLayoutParams(lp);
			}
		}
	}
}
