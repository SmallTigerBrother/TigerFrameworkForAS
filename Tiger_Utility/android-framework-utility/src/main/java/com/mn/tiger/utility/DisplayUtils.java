package com.mn.tiger.utility;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;

import com.mn.tiger.log.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 显示工具类
 */
public class DisplayUtils
{
    private static final Logger LOG = Logger.getLogger(DisplayUtils.class);

    private volatile static int[] resolution;

    /**
     * 该方法的作用:dip转换为像素
     *
     * @param context
     * @param dipValue
     * @return
     * @date 2013-3-7
     */
    public static int dip2px(Context context, float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 该方法的作用:sp转换为px（文字大小单位）
     *
     * @param context
     * @param spValue
     * @return
     * @date 2013-3-8
     */
    public static int sp2px(Context context, float spValue)
    {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scaledDensity + 0.5f);
    }

    /**
     * 该方法的作用:px转换为sp（文字大小单位）
     *
     * @param context
     * @param pxValue
     * @return
     * @date 2013-3-8
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
     * @param context
     * @return
     * @date 2013-3-7
     */
    public static int getStatusBarHeight(Context context)
    {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * 该方法的作用:获取屏幕分辨率
     *
     * @param context
     * @return 返回int数组长度为2，0为x 1为y
     * @date 2014-1-23
     */
    public static int[] getResolution(Activity context)
    {
        if (null == resolution)
        {
            synchronized (DisplayUtils.class)
            {
                if (null == resolution)
                {
                    resolution = new int[2];
                    DisplayMetrics metrics = new DisplayMetrics();
                    context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    resolution[0] = metrics.widthPixels;
                    resolution[1] = metrics.heightPixels;
                }
            }
        }
        return resolution;
    }

    /**
     * 设置视图自适应图片大小
     *
     * @param view           需要自适应的View
     * @param srcImageWidth  图片宽度
     * @param srcImageHeight 图片高度
     * @param maxWidth       View可现实区域的最大宽度
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

        if (view.getLayoutParams() instanceof MarginLayoutParams)
        {
            MarginLayoutParams marginParams = (MarginLayoutParams) view.getLayoutParams();

            currentFitWidth = maxWidth - marginParams.leftMargin - marginParams.rightMargin;
        }
        else
        {
            currentFitWidth = maxWidth;
        }
        adjustHeight = (int) (currentFitWidth / ratio);

        if (null != lp)
        {
            if (lp.width <= 0 || lp.height <= 0 ||
                    currentFitWidth != view.getMeasuredWidth() ||
                    adjustHeight != view.getMeasuredHeight())
            {
                lp.height = adjustHeight;
                lp.width = currentFitWidth;
                view.setLayoutParams(lp);
            }
        }
        else
        {
            LOG.e("[Method:adjustViewSizeByWidth] the LayoutParams of View is null, please check your code");
        }
    }

    /**
     * 获取根据宽度适配图片时的高度
     *
     * @param srcImageWidth
     * @param srcImageHeight
     * @param maxWidth
     * @return
     */
    public static int getImageHeightAdjustSizeByWidth(int srcImageWidth, int srcImageHeight, int maxWidth)
    {
        if (srcImageWidth == 0 || srcImageHeight == 0)
        {
            return 0;
        }

        float ratio = (float) srcImageWidth / (float) srcImageHeight;

        return (int) (maxWidth / ratio);
    }

    /**
     * 设置视图自适应图片大小
     *
     * @param view           需要自适应的View
     * @param srcImageWidth  图片宽度
     * @param srcImageHeight 图片高度
     * @param maxHeight      View可现实区域的最大高度
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

        if (view.getLayoutParams() instanceof MarginLayoutParams)
        {
            MarginLayoutParams marginParams = (MarginLayoutParams) view.getLayoutParams();

            currentFitHeight = maxHeight - marginParams.topMargin - marginParams.bottomMargin;
        }
        else
        {
            currentFitHeight = maxHeight;
        }

        adjustWidth = (int) (currentFitHeight * ratio);

        if (null != lp)
        {
            if (lp.width <= 0 || lp.height <= 0 ||
                    adjustWidth != view.getMeasuredWidth() ||
                    currentFitHeight != view.getMeasuredHeight())
            {
                lp.height = currentFitHeight;
                lp.width = adjustWidth;
                view.setLayoutParams(lp);
            }
        }
        else
        {
            LOG.e("[Method:adjustViewSizeByHeight] the LayoutParams of View is null, please check your code");
        }
    }

    public static boolean isSupportFullTheme()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    private static boolean hasGetSmartBar = false;
    private static boolean hasSmartBar = false;
    private static int smartHeight = 0;

    /**
     * 兼容flame smartbar
     *
     * @return
     */
    public static boolean hasFlameSmartBar()
    {
        if (hasGetSmartBar)
        {
            return hasSmartBar;
        }
        try
        {
            Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
            hasSmartBar = ((Boolean) method.invoke(null)).booleanValue();
        }
        catch (Exception e)
        {
        }
        hasGetSmartBar = true;
        return hasSmartBar;
    }

    /**
     * 获取smartbar高度
     *
     * @param context 兼容flame smartbar
     * @return int SmartBar的高度值
     */
    public static int getSmartBarHeight(Context context)
    {
        if (hasSmartBar && smartHeight > 0)
        {
            return smartHeight;
        }
        try
        {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("mz_action_button_min_height");
            int height = Integer.parseInt(field.get(obj).toString());
            smartHeight = context.getResources().getDimensionPixelSize(height);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return smartHeight;
    }
}
