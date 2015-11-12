package com.mn.tiger.utility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by peng on 15/10/22.
 */
public class FrescoUtils
{
    private static int PLACE_HOLDER_ID = -1;

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    private static final int MAX_MEMORY_CACHE_SIZE = (int) (MAX_HEAP_SIZE * 0.25);

    public static void initialize(Context context)
    {
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

    /**
     * 异步加载图片
     *
     * @param uri       图片地址
     * @param imageView
     */
    public static void displayImage(String uri, DraweeView imageView)
    {
        int id = getViewId(imageView);
        if (null == imageView.getTag(id))
        {
            imageView.setHierarchy(new GenericDraweeHierarchyBuilder(imageView.getResources())
                    .build());
            imageView.setTag(id, true);
        }
        ResizeOptions resizeOptions = new ResizeOptions(imageView.getLayoutParams().width, imageView.getLayoutParams().height);
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                .setProgressiveRenderingEnabled(true)
                .setResizeOptions(resizeOptions)
                .build();

        imageView.setController(Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setTapToRetryEnabled(true)
                .setOldController(imageView.getController())
                .setImageRequest(imageRequest)
                .build());
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
        int id = getViewId(imageView);
        if (null == imageView.getTag(id))
        {
            GenericDraweeHierarchyBuilder draweeHierarchyBuilder = new GenericDraweeHierarchyBuilder(imageView.getResources());

            if (null != placeHolder)
            {
                draweeHierarchyBuilder.setPlaceholderImage(placeHolder, ScalingUtils.ScaleType.CENTER_INSIDE);
            }
            draweeHierarchyBuilder.setBackground(new ColorDrawable(placeHolderBackgroundColor));
            imageView.setHierarchy(draweeHierarchyBuilder.build());
            imageView.setTag(id, true);
        }

        ResizeOptions resizeOptions = new ResizeOptions(imageView.getLayoutParams().width, imageView.getLayoutParams().height);
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                .setProgressiveRenderingEnabled(true)
                .setResizeOptions(resizeOptions)
                .build();

        imageView.setController(Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setTapToRetryEnabled(true)
                .setOldController(imageView.getController())
                .setImageRequest(imageRequest)
                .build());
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
            GenericDraweeHierarchyBuilder draweeHierarchyBuilder = new GenericDraweeHierarchyBuilder(imageView.getResources());
            if (null != placeHolder)
            {
                draweeHierarchyBuilder.setPlaceholderImage(placeHolder, ScalingUtils.ScaleType.CENTER_INSIDE);
            }

            RoundingParams roundingParams = new RoundingParams();
            roundingParams.setRoundAsCircle(true);
            roundingParams.setBorder(Color.TRANSPARENT, 0);
            draweeHierarchyBuilder.setRoundingParams(roundingParams).build();

            draweeHierarchyBuilder.setActualImageScaleType(scaleType);

            imageView.setHierarchy(draweeHierarchyBuilder.build());
            imageView.setTag(id, true);
        }

        ResizeOptions resizeOptions = new ResizeOptions(imageView.getLayoutParams().width,
                imageView.getLayoutParams().height);
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                .setProgressiveRenderingEnabled(true)
                .setResizeOptions(resizeOptions)
                .build();

        imageView.setController(Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setTapToRetryEnabled(true)
                .setOldController(imageView.getController())
                .setImageRequest(imageRequest)
                .build());
    }

    /**
     * 加载本地的图片资源
     *
     * @param filePath
     * @param imageView
     */
    public static void displayLocalImage(String filePath, DraweeView imageView)
    {
        int id = getViewId(imageView);

        if (null == imageView.getTag(id))
        {
            imageView.setHierarchy(new GenericDraweeHierarchyBuilder(imageView.getResources())
                    .build());
            imageView.setTag(id, true);
        }

        ResizeOptions resizeOptions = new ResizeOptions(imageView.getLayoutParams().width,
                imageView.getLayoutParams().height);
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + filePath))
                .setAutoRotateEnabled(true)
                .setProgressiveRenderingEnabled(true)
                .setResizeOptions(resizeOptions)
                .build();

        imageView.setController(Fresco.newDraweeControllerBuilder()
                .setUri("file://" + filePath)
                .setTapToRetryEnabled(true)
                .setOldController(imageView.getController())
                .setImageRequest(imageRequest)
                .build());
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
            GenericDraweeHierarchyBuilder draweeHierarchyBuilder = new GenericDraweeHierarchyBuilder(imageView.getResources());
            if (null != placeHolder)
            {
                draweeHierarchyBuilder.setPlaceholderImage(placeHolder, ScalingUtils.ScaleType.CENTER_INSIDE);
            }
            draweeHierarchyBuilder.setBackground(new ColorDrawable(placeHolderBackgroundColor));

            imageView.setHierarchy(draweeHierarchyBuilder.build());
            imageView.setTag(id, true);
        }

        ResizeOptions resizeOptions = new ResizeOptions(imageView.getLayoutParams().width, imageView.getLayoutParams().height);
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + filePath))
                .setAutoRotateEnabled(true)
                .setProgressiveRenderingEnabled(true)
                .setResizeOptions(resizeOptions)
                .build();

        imageView.setController(Fresco.newDraweeControllerBuilder()
                .setUri("file://" + filePath)
                .setTapToRetryEnabled(true)
                .setOldController(imageView.getController())
                .setImageRequest(imageRequest)
                .build());
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
            imageView.setHierarchy(draweeHierarchyBuilder.build());
            imageView.setTag(id, true);
        }

        ResizeOptions resizeOptions = new ResizeOptions(imageView.getLayoutParams().width, imageView.getLayoutParams().height);
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse("res:///" + resourceId))
                .setAutoRotateEnabled(true)
                .setProgressiveRenderingEnabled(true)
                .setResizeOptions(resizeOptions)
                .build();

        imageView.setController(Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("res:///" + resourceId))
                .setTapToRetryEnabled(true)
                .setOldController(imageView.getController())
                .setImageRequest(imageRequest)
                .build());
    }

    private static int getViewId(DraweeView draweeView)
    {
        if(PLACE_HOLDER_ID == -1)
        {
            PLACE_HOLDER_ID = CR.getViewId(draweeView.getContext(), "placeholder");
        }
        return PLACE_HOLDER_ID;
    }
}
