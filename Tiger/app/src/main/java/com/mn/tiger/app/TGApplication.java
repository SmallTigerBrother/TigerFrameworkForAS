package com.mn.tiger.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowManager;

import com.mn.tiger.log.Logger;
import com.mn.tiger.system.AppConfigs;
import com.mn.tiger.system.SystemConfigs;
import com.mn.tiger.utility.FrescoUtils;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.otto.Bus;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 该类作用及功能说明 应用App类
 *
 * @version V2.0
 * @see JDK1.6,android-8
 */
public class TGApplication extends Application
{
    protected final Logger LOG = Logger.getLogger(this.getClass());

    /** 启动Activity列表 */
    private List<Activity> activities = new LinkedList<Activity>();

    /** Application实例 */
    private static TGApplication instance;

    /**
     * 事件总线
     */
    private static Bus bus;

    /**
     * 图像加载器
     */
    private ImageLoader imageLoader;

    /**
     * 屏幕大小
     */
    private Point screenSize;

    /**
     * 资源图片加载参数
     */
    private DisplayImageOptions resourceDisplayImageOptions;

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        SystemConfigs.initSystemConfigs(this);

        AppConfigs.initAppConfigs(this);

        initLogger();

        initSharePluginManager();

        initFresco();
    }

    /** 得到 Application实例 */
    public static TGApplication getInstance()
    {
        return instance;
    }

    /**
     * 初始化日志工具
     */
    protected void initLogger()
    {
        Logger.setPackageName(getPackageName());
    }

    /**
     * 初始化分先插件管理器（添加默认的分享插件）
     */
    protected void initSharePluginManager()
    {

    }

    /**
     * 初始化Fresco图片加载库
     */
    protected void initFresco()
    {
        FrescoUtils.initialize(this);
    }

    /**
     * 获取ImageLoader
     * @return
     */
    public ImageLoader getImageLoader()
    {
        if(null == imageLoader)
        {
            imageLoader = initImageLoader();
        }
        return imageLoader;
    }

    /**
     * 初始化ImageLoader
     */
    protected ImageLoader initImageLoader()
    {
        screenSize = new Point();
        ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(screenSize);
        int screenWidth = screenSize.x;

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(getDefaultImageLoaderConfigurationBuilder(screenWidth).build());
        return imageLoader;
    }

    /**
     * 获取默认的ImageLoaderConfiguration.Builder
     * @param screenWidth
     * @return
     */
    protected ImageLoaderConfiguration.Builder getDefaultImageLoaderConfigurationBuilder(int screenWidth)
    {
        //设置缓存大小，最大缓存文件个数
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSizePercentage(20)
                .diskCacheSize(80 * 1024 * 1024)
                .diskCacheFileCount(1024)
                .defaultDisplayImageOptions(getDefaultDisplayImageOptionsBuilder(screenWidth).build());

        //屏幕分辨率小于640时，屏蔽内存缓存多尺寸图片;
        if(Runtime.getRuntime().maxMemory() < 70 * 1024 * 1024)
        {
            builder.denyCacheImageMultipleSizesInMemory();
            builder.memoryCache(new WeakMemoryCache());
        }

        return builder;
    }

    /**
     * 获取默认的DisplayImageOptions.Builder
     * @param screenWidth
     * @return
     */
    protected DisplayImageOptions.Builder getDefaultDisplayImageOptionsBuilder(int screenWidth)
    {
        //启用内存缓存、磁盘缓存
        DisplayImageOptions.Builder builder =  new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY);

        if(Runtime.getRuntime().maxMemory() < 70 * 1024 * 1024)
        {
            builder.bitmapConfig(Bitmap.Config.RGB_565);
        }

        return builder;
    }

    /**
     * 初始化图片资源加载参数
     * @return
     */
    protected DisplayImageOptions initResourceDisplayImageOptions()
    {
        DisplayImageOptions.Builder builder =  new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY);

        if(Runtime.getRuntime().maxMemory() < 70 * 1024 * 1024)
        {
            builder.bitmapConfig(Bitmap.Config.RGB_565);
        }
        return builder.build();
    }

    /**
     * 获取图片资源加载参数
     * @return
     */
    public final DisplayImageOptions getResourceDisplayImageOptions()
    {
        if(null == resourceDisplayImageOptions)
        {
            resourceDisplayImageOptions = initResourceDisplayImageOptions();
        }

        return resourceDisplayImageOptions;
    }


    /**
     * 该方法的作用:添加Activity
     *
     * @date 2013-12-3
     * @param activity
     */
    public void addActivityToStack(Activity activity)
    {
        activities.add(activity);
    }

    /**
     * 该方法的作用: 删除Activity
     *
     * @date 2014年1月3日
     * @param activity
     */
    public void removeActivityFromStack(Activity activity)
    {
        activities.remove(activity);
    }

    /**
     * 退出应用时销毁所有启动的Activity
     */
    public void exit()
    {
        finishAllActivity();
        System.exit(0);
    }

    /**
     * 该方法的作用: 销毁所有的Activity
     *
     * @date 2014年3月4日
     */
    public void finishAllActivity()
    {
        Activity activity;
        for (int i = 0; i < activities.size(); i++)
        {
            activity = activities.get(i);
            if (null != activity && !activity.isFinishing())
            {
                activity.finish();
            }
        }

        activities.clear();
    }

    /**
     * 获取数据总线
     * @return
     */
    public static Bus getBus()
    {
        if(null == bus)
        {
            bus = new Bus();
        }
        return bus;
    }

    /**
     * 设置UncaughtExceptionHandler
     */
    protected void onSetUncaughtExceptionHandler()
    {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    }

    /**
     * 处理UncaughtException
     * @param thread
     * @param ex
     */
    protected void handleUncaughtException(Thread thread, Throwable ex)
    {

    }

    public Point getScreenSize()
    {
        return screenSize;
    }

    /**
     * Crash异常捕获类
     */
    private class CrashHandler implements UncaughtExceptionHandler
    {
        private Map<String, String> mSystemInformation = new HashMap<String, String>();

        /**
         * LOG 到文件中
         */
        private final Logger LOG = Logger.getLogger(this.getClass(), true);

        public CrashHandler()
        {
            Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public final void uncaughtException(Thread thread, Throwable ex)
        {
            LOG.e(ex);

            handleUnCaughtException(thread, ex);
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                LOG.e(e);
            }

            this.collectDeviceInfo(TGApplication.this);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

        /**
         * 处理UncaughtException
         * @param thread
         * @param ex
         */
        protected void handleUnCaughtException(Thread thread, Throwable ex)
        {
            TGApplication.this.handleUncaughtException(thread, ex);
        }

        /**
         * 收集设备信息
         * @param ctx
         */
        private void collectDeviceInfo(Context ctx)
        {
            try
            {
                PackageManager pm = ctx.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
                if (pi != null)
                {
                    String versionName = pi.versionName == null ? "null" : pi.versionName;
                    String versionCode = pi.versionCode + "";
                    mSystemInformation.put("versionName", versionName);
                    mSystemInformation.put("versionCode", versionCode);
                }
            }
            catch (NameNotFoundException e)
            {
                LOG.e("An error occured when collect package info : " + e.getMessage());
            }
            // 写入系統信息
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields)
            {
                try
                {
                    field.setAccessible(true);
                    mSystemInformation.put(field.getName(), field.get(null).toString());
                    LOG.e("SystemInformation " + field.getName() + " : " + field.get(null));
                }
                catch (Exception e)
                {
                    LOG.e("an error occured when collect crash info error : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
