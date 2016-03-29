package com.mn.tiger.app;

import android.app.Application;
import android.os.Handler;
import android.os.Message;

import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.system.AppConfigs;
import com.mn.tiger.system.SystemConfigs;

/**
 * Created by peng on 16/3/29.
 */
public class TGApplicationInitializer
{
    private static boolean initialized = false;

    public interface OnInitializeListener
    {
        void onInitializeInBackground();

        void afterInitialized(long initializeTimeLength);
    }

    private OnInitializeListener onInitializeListener;

    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(null != onInitializeListener)
            {
                onInitializeListener.afterInitialized(System.currentTimeMillis() - (Long)msg.obj);
            }
        }
    };

    public TGApplicationInitializer()
    {

    }

    public void initializeAsync(final Application application)
    {
        if(initialized)
        {
            long startTime = System.currentTimeMillis();
            Message message = Message.obtain();
            message.obj = startTime;
            handler.sendMessage(message);
        }
        else
        {
            new Thread()
            {
                @Override
                public void run()
                {
                    long startTime = System.currentTimeMillis();
                    TGApplicationProxy.initWithApplication(application);
                    SystemConfigs.initSystemConfigs(TGApplicationProxy.getApplication());
                    AppConfigs.initAppConfigs(TGApplicationProxy.getApplication());
                    if(null != onInitializeListener)
                    {
                        onInitializeListener.onInitializeInBackground();
                    }
                    initialized = true;
                    Message message = Message.obtain();
                    message.obj = startTime;
                    handler.sendMessage(message);
                }
            }.start();
        }
    }

    public void setOnInitializeListener(OnInitializeListener listener)
    {
        this.onInitializeListener = listener;
    }

}
