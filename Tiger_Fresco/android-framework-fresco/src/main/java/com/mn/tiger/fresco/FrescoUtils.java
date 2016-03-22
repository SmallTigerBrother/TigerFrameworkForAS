package com.mn.tiger.fresco;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.soloader.SoLoader;
import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.CR;

/**
 * Created by peng on 15/10/22.
 */
public class FrescoUtils
{
    private static final Logger LOG = Logger.getLogger(FrescoUtils.class);

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

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
     * 加载webp
     * @param context
     */
    public static void loadWebpJNI(Context context)
    {
        try
        {
            SoLoader.init(context,false);
            SoLoader.loadLibrary("webp");
        }
        catch (Throwable e)
        {
            LOG.e("[Method:loadWebpJNI]", e);
        }
    }
}
