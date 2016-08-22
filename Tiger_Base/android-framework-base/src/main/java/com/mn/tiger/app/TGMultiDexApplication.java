package com.mn.tiger.app;

import android.support.multidex.MultiDexApplication;

/**
 * Created by peng on 15/11/16.
 */
public class TGMultiDexApplication extends MultiDexApplication
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        TGApplicationProxy.initWithMultiDexApplication(this);
    }
}
