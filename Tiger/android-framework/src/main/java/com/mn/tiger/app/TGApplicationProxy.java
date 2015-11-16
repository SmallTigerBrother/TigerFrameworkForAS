package com.mn.tiger.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDexApplication;

import com.mn.tiger.log.Logger;
import com.mn.tiger.system.AppConfigs;
import com.mn.tiger.system.SystemConfigs;
import com.mn.tiger.utility.FrescoUtils;
import com.squareup.otto.Bus;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by peng on 15/11/16.
 */
public class TGApplicationProxy
{
    private Application application;

    private Bus bus;

    /** 启动Activity列表 */
    private List<Activity> activities;

    private static TGApplicationProxy proxy;

    private TGApplicationProxy()
    {
        bus = new Bus();
        activities = new LinkedList<Activity>();
    }

    static TGApplicationProxy initWithApplication(Application application)
    {
        proxy = new TGApplicationProxy();
        proxy.application = application;
        Logger.setPackageName(application.getPackageName());

        SystemConfigs.initSystemConfigs(application);
        AppConfigs.initAppConfigs(application);
        FrescoUtils.initialize(application);
        return proxy;
    }

    static TGApplicationProxy initWithMultiDexApplication(MultiDexApplication application)
    {
        proxy = new TGApplicationProxy();
        proxy.application = application;
        Logger.setPackageName(application.getPackageName());

        SystemConfigs.initSystemConfigs(application);
        AppConfigs.initAppConfigs(application);
        FrescoUtils.initialize(application);
        return proxy;
    }

    public static TGApplicationProxy getInstance()
    {
        return proxy;
    }

    public Application getApplication()
    {
        return application;
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

    public Bus getBus()
    {
        return bus;
    }

    /**
     * Crash异常捕获类
     */
    static class CrashHandler implements Thread.UncaughtExceptionHandler
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

            this.collectDeviceInfo(TGApplicationProxy.getInstance().getApplication());
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
            catch (PackageManager.NameNotFoundException e)
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
