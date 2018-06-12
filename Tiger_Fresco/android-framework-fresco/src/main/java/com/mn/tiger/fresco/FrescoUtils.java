package com.mn.tiger.fresco;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.soloader.SoLoader;
import com.mn.tiger.log.Logger;

/**
 * Created by peng on 15/10/22.
 */
public class FrescoUtils
{
    private static final Logger LOG = Logger.getLogger(FrescoUtils.class);

    /**
     * 初始化
     * @param context
     */
    public static void initialize(Context context)
    {
        try
        {
            Class.forName("com.facebook.drawee.backends.pipeline.Fresco");
            Fresco.initialize(context);
        }
        catch (ClassNotFoundException e)
        {
            LOG.e("[Method:initialize]", e);
        }
    }

    /**
     * 加载webpJNI库
     */
    public static void loadWebpJNI(Context context)
    {
        try
        {
            SoLoader.init(context, false);
            SoLoader.loadLibrary("static-webp");
        }
        catch(UnsatisfiedLinkError nle)
        {
            LOG.e("[Method:loadWebpJNI]", nle);
        }
    }
}
