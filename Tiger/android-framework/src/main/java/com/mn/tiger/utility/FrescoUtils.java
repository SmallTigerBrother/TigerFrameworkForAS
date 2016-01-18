package com.mn.tiger.utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
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
import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.log.Logger;

/**
 * Created by peng on 15/10/22.
 */
public class FrescoUtils
{
    private static final Logger LOG = Logger.getLogger(FrescoUtils.class);

    private static int PLACE_HOLDER_ID = -1;

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    private static final int MAX_MEMORY_CACHE_SIZE = (int) (MAX_HEAP_SIZE * 0.25);

    public static void initialize(Context context)
    {
        try
        {
            Class.forName("com.facebook.drawee.backends.pipeline.Fresco");
            final MemoryCacheParams memoryCacheParams = new MemoryCacheParams(
                    MAX_MEMORY_CACHE_SIZE,
                    Integer.MAX_VALUE,
                    MAX_MEMORY_CACHE_SIZE,
                    Integer.MAX_VALUE,
                    Integer.MAX_VALUE);

            Supplier<MemoryCacheParams> memoryCacheParamsSupplier = new Supplier<MemoryCacheParams>()
            {
                @Override
                public MemoryCacheParams get()
                {
                    return memoryCacheParams;
                }
            };

            ImagePipelineConfig.Builder builder = ImagePipelineConfig.newBuilder(context)
                    .setBitmapMemoryCacheParamsSupplier(memoryCacheParamsSupplier);

            Fresco.initialize(context, builder.build());
        }
        catch (ClassNotFoundException e)
        {
            LOG.e("[Method:initialize]", e);
        }
    }

    /**
     * 异步加载图片
     *
     * @param uri       图片地址
     * @param imageView
     */
    public static void displayImage(String uri, DraweeView imageView)
    {
        displayImage(uri, imageView, null, Color.TRANSPARENT);
    }

    /**
     * 异步加载图片
     *
     * @param uri                        图片地址
     * @param imageView
     * @param placeHolder                占位符
     * @param placeHolderBackgroundColor 占位符背景颜色
     */
    public static void displayImage(String uri, DraweeView imageView, Drawable placeHolder,
                                    int placeHolderBackgroundColor)
    {
        displayImage(uri, imageView, placeHolder, placeHolderBackgroundColor, ScalingUtils.ScaleType.CENTER_CROP);
    }

    /**
     * 异步加载图片
     *
     * @param uri                        图片地址
     * @param imageView
     * @param placeHolder                占位符
     * @param placeHolderBackgroundColor 占位符背景颜色
     */
    public static void displayImage(String uri, DraweeView imageView, Drawable placeHolder,
                                    int placeHolderBackgroundColor, ScalingUtils.ScaleType scaleType)
    {
        int id = getViewId(imageView);
        if (null == imageView.getTag(id))
        {
            GenericDraweeHierarchy draweeHierarchy = createDraweeHierarchy(imageView.getResources(), placeHolder,
                    placeHolderBackgroundColor, scaleType, false);
            imageView.setHierarchy(draweeHierarchy);
            imageView.setTag(id, true);
        }

        ImageRequest imageRequest = createImageRequest(uri, imageView.getLayoutParams().width, imageView.getLayoutParams().height, false);
        DraweeController draweeController = createDraweeController(uri, imageRequest, imageView.getController());
        imageView.setController(draweeController);
    }


    /**
     * 显示圆形图片
     *
     * @param uri
     * @param imageView
     * @param placeHolder
     */
    public static void displayImageAsCircle(String uri, DraweeView imageView, Drawable placeHolder)
    {
        displayImageAsCircle(uri, imageView, placeHolder, ScalingUtils.ScaleType.CENTER_CROP);
    }

    /**
     * 显示圆形图片
     *
     * @param uri
     * @param imageView
     * @param placeHolder
     * @param scaleType
     */
    public static void displayImageAsCircle(String uri, DraweeView imageView, Drawable placeHolder,
                                            ScalingUtils.ScaleType scaleType)
    {
        int id = getViewId(imageView);
        if (null == imageView.getTag(id))
        {
            GenericDraweeHierarchy draweeHierarchy = createDraweeHierarchy(imageView.getResources(), placeHolder,
                    Color.TRANSPARENT, scaleType, true);

            imageView.setHierarchy(draweeHierarchy);
            imageView.setTag(id, true);
        }
        else
        {
            if(null == ((GenericDraweeHierarchy)imageView.getHierarchy()).getRoundingParams())
            {
                GenericDraweeHierarchy draweeHierarchy = createDraweeHierarchy(imageView.getResources(), placeHolder,
                        Color.TRANSPARENT, scaleType, true);

                imageView.setHierarchy(draweeHierarchy);
                imageView.setTag(id, true);
            }
        }

        ImageRequest imageRequest = createImageRequest(uri, imageView.getLayoutParams().width, imageView.getLayoutParams().height, false);
        DraweeController draweeController = createDraweeController(uri, imageRequest, imageView.getController());
        imageView.setController(draweeController);
    }

    /**
     * 加载本地的图片资源
     *
     * @param filePath
     * @param imageView
     */
    public static void displayLocalImage(String filePath, DraweeView imageView)
    {
        displayLocalImage(filePath, imageView, null, Color.TRANSPARENT);
    }

    /**
     * 加载本地图片资源
     *
     * @param filePath
     * @param imageView
     * @param placeHolder
     * @param placeHolderBackgroundColor
     */
    public static void displayLocalImage(String filePath, DraweeView imageView, Drawable placeHolder,
                                         int placeHolderBackgroundColor)
    {
        int id = getViewId(imageView);
        if (null == imageView.getTag(id))
        {
            GenericDraweeHierarchy draweeHierarchy = createDraweeHierarchy(imageView.getResources(), placeHolder,
                    placeHolderBackgroundColor, ScalingUtils.ScaleType.CENTER_CROP, false);
            imageView.setHierarchy(draweeHierarchy);
            imageView.setTag(id, true);
        }

        ImageRequest imageRequest = createImageRequest("file://" + filePath, imageView.getLayoutParams().width,
                imageView.getLayoutParams().height, true);
        DraweeController draweeController = createDraweeController("file://" + filePath, imageRequest, imageView.getController());
        imageView.setController(draweeController);
    }

    /**
     * 显示圆形图片
     *
     * @param uri
     * @param imageView
     * @param placeHolder
     */
    public static void displayLocalImageAsCircle(String uri, DraweeView imageView, Drawable placeHolder)
    {
        displayLocalImageAsCircle(uri, imageView, placeHolder, ScalingUtils.ScaleType.CENTER_CROP);
    }

    /**
     * 显示圆形图片
     *
     * @param filePath
     * @param imageView
     * @param placeHolder
     * @param scaleType
     */
    public static void displayLocalImageAsCircle(String filePath, DraweeView imageView, Drawable placeHolder,
                                            ScalingUtils.ScaleType scaleType)
    {
        int id = getViewId(imageView);
        if (null == imageView.getTag(id))
        {
            GenericDraweeHierarchy draweeHierarchy = createDraweeHierarchy(imageView.getResources(), placeHolder,
                    Color.TRANSPARENT, scaleType, true);

            imageView.setHierarchy(draweeHierarchy);
            imageView.setTag(id, true);
        }
        else
        {
            if(null == ((GenericDraweeHierarchy)imageView.getHierarchy()).getRoundingParams())
            {
                GenericDraweeHierarchy draweeHierarchy = createDraweeHierarchy(imageView.getResources(), placeHolder,
                        Color.TRANSPARENT, scaleType, true);

                imageView.setHierarchy(draweeHierarchy);
                imageView.setTag(id, true);
            }
        }

        ImageRequest imageRequest = createImageRequest("file://" + filePath, imageView.getLayoutParams().width,
                imageView.getLayoutParams().height, true);
        DraweeController draweeController = createDraweeController("file://" + filePath, imageRequest, imageView.getController());
        imageView.setController(draweeController);
    }

    /**
     * 加载本地的图片资源
     * @param resourceId
     * @param imageView
     */
    public static void displayResourceImage(int resourceId, DraweeView imageView)
    {
        int id = getViewId(imageView);
        if (null == imageView.getTag(id))
        {
            GenericDraweeHierarchyBuilder draweeHierarchyBuilder = new GenericDraweeHierarchyBuilder(imageView.getResources());
            draweeHierarchyBuilder.setFadeDuration(0);
            draweeHierarchyBuilder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
            imageView.setHierarchy(draweeHierarchyBuilder.build());
            imageView.setTag(id, true);
        }

        ImageRequest imageRequest = createImageRequest("res:///" + resourceId, imageView.getLayoutParams().width,
                imageView.getLayoutParams().height, false);
        DraweeController draweeController = createDraweeController("res:///" + resourceId, imageRequest, imageView.getController());
        imageView.setController(draweeController);
    }

    public static void displayResourceImage(int resourceId, DraweeView imageView, ScalingUtils.ScaleType scaleType)
    {
        int id = getViewId(imageView);
        if (null == imageView.getTag(id))
        {
            GenericDraweeHierarchyBuilder draweeHierarchyBuilder = new GenericDraweeHierarchyBuilder(imageView.getResources());
            draweeHierarchyBuilder.setFadeDuration(0);
            draweeHierarchyBuilder.setActualImageScaleType(scaleType);
            imageView.setHierarchy(draweeHierarchyBuilder.build());
            imageView.setTag(id, true);
        }

        ImageRequest imageRequest = createImageRequest("res:///" + resourceId, imageView.getLayoutParams().width,
                imageView.getLayoutParams().height, false);
        DraweeController draweeController = createDraweeController("res:///" + resourceId, imageRequest, imageView.getController());
        imageView.setController(draweeController);
    }

    /**
     * 创建GenericDraweeHierarchy
     * @param resources
     * @param placeHolder
     * @param placeHolderBackgroundColor
     * @param scaleType
     * @param circle
     * @return
     */
    private static GenericDraweeHierarchy createDraweeHierarchy(Resources resources, Drawable placeHolder,
                                                                int placeHolderBackgroundColor, ScalingUtils.ScaleType scaleType, boolean circle)
    {
        GenericDraweeHierarchyBuilder draweeHierarchyBuilder = new GenericDraweeHierarchyBuilder(resources);
        if (null != placeHolder)
        {
            draweeHierarchyBuilder.setPlaceholderImage(placeHolder, ScalingUtils.ScaleType.CENTER_INSIDE);
        }
        draweeHierarchyBuilder.setBackground(new ColorDrawable(placeHolderBackgroundColor));

        if(circle)
        {
            RoundingParams roundingParams = new RoundingParams();
            roundingParams.setRoundAsCircle(true);
            roundingParams.setBorder(Color.TRANSPARENT, 0);
            draweeHierarchyBuilder.setRoundingParams(roundingParams).build();
        }

        draweeHierarchyBuilder.setActualImageScaleType(scaleType);
        return draweeHierarchyBuilder.build();
    }

    private static ImageRequest createImageRequest(String uri, int viewWidth, int viewHeight, boolean autoRotate)
    {
        ResizeOptions resizeOptions = new ResizeOptions(viewWidth, viewHeight);
        return ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                .setAutoRotateEnabled(autoRotate)
                .setProgressiveRenderingEnabled(true)
                .setResizeOptions(resizeOptions)
                .build();
    }

    private static AbstractDraweeController createDraweeController(String uri, ImageRequest imageRequest, DraweeController oldDraweeController)
    {
        return Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setTapToRetryEnabled(true)
                .setOldController(oldDraweeController)
                .setImageRequest(imageRequest)
                .build();
    }

    private static int getViewId(DraweeView draweeView)
    {
        if(PLACE_HOLDER_ID == -1)
        {
            PLACE_HOLDER_ID = CR.getViewId(TGApplicationProxy.getInstance().getApplication(), "placeholder");
        }
        return PLACE_HOLDER_ID;
    }
}
