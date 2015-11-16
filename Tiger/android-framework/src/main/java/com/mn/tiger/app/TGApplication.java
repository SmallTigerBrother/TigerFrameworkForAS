package com.mn.tiger.app;

import android.app.Application;

/**
 * 该类作用及功能说明 应用App类
 */
public class TGApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        TGApplicationProxy.initWithApplication(this);
    }
}
