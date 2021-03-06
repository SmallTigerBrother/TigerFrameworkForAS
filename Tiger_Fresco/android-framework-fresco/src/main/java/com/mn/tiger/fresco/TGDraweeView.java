package com.mn.tiger.fresco;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mn.tiger.log.Logger;

import java.util.HashMap;

/**
 * Created by peng on 16/1/31.
 */
public class TGDraweeView extends DraweeView<GenericDraweeHierarchy>
{
    private static final Logger LOG = Logger.getLogger(TGDraweeView.class);

    private FrescoConfigs frescoConfigs;

    private static final Handler HANDLER = new Handler();

    private static final HashMap<Integer,Boolean> HASH_CODE_CACHE = new HashMap<>();

    public TGDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        frescoConfigs = new FrescoConfigs();
    }

    public TGDraweeView(Context context)
    {
        super(context);
        frescoConfigs = new FrescoConfigs();
    }

    public TGDraweeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        frescoConfigs = new FrescoConfigs();
    }

    public TGDraweeView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        frescoConfigs = new FrescoConfigs();
    }

    public FrescoConfigs getFrescoConfigs()
    {
        return frescoConfigs;
    }

    @Override
    protected void onDetach()
    {
        super.onDetach();
        //移除缓存记录
        if(HASH_CODE_CACHE.size() > 0)
        {
            HASH_CODE_CACHE.remove(frescoConfigs.hashCode());
        }
    }

    /**
     * Fresco参数
     */
    public final class FrescoConfigs
    {
        private GenericDraweeHierarchy draweeHierarchy;
        //图片资源
        private Variable<String> uri = new Variable<>();
        //占位图片
        private Drawable placeHolder = null;
        //占位图背景色
        private Variable<Integer> placeHolderBackgroundColor = new Variable<>(Color.TRANSPARENT);
        //加载图片的显示ScaleType
        private ScalingUtils.ScaleType scaleType = ScalingUtils.ScaleType.CENTER_CROP;
        //是否显示成圆形
        private boolean circle = false;
        //圆角边框颜色
        private int roundBorderColor = Color.TRANSPARENT;
        //圆角边框宽度
        private int roundBorderWidth =0;
        //是否自动旋转
        private boolean autoRotate = false;

        private TGDraweeControllerListener controllerListener;

        private int fadeDuration = 300;

        private boolean tapToRetryEnabled = true;

        /**
         * 显示图片
         */
        public void display()
        {
            //如果占位背景色发生改变，重新生成draweeHierarchy
            if(null == draweeHierarchy || (!placeHolderBackgroundColor.isNull() && placeHolderBackgroundColor.isChanged()))
            {
                placeHolderBackgroundColor.resetChangeStatus();
                draweeHierarchy = newHierarchy();
            }

            //设置占位图片
            if(null != placeHolder)
            {
                draweeHierarchy.setPlaceholderImage(placeHolder, ScalingUtils.ScaleType.CENTER_INSIDE);
            }
            draweeHierarchy.setFadeDuration(fadeDuration);

            //设置圆角参数
            RoundingParams roundingParams = new RoundingParams();
            roundingParams.setRoundAsCircle(circle);
            roundingParams.setBorder(roundBorderColor, roundBorderWidth);
            draweeHierarchy.setRoundingParams(roundingParams);

            //设置加载图ScaleType
            draweeHierarchy.setActualImageScaleType(scaleType);
            TGDraweeView.this.setHierarchy(draweeHierarchy);

            //设置图片
            int width = TGDraweeView.this.getLayoutParams().width > 0 ? TGDraweeView.this.getLayoutParams().width : TGDraweeView.this.getMeasuredWidth();
            int height = TGDraweeView.this.getLayoutParams().height > 0 ? TGDraweeView.this.getLayoutParams().height : TGDraweeView.this.getMeasuredHeight();
            ResizeOptions resizeOptions = new ResizeOptions(width,height);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri.getValue()))
                    .setAutoRotateEnabled(autoRotate)
                    .setProgressiveRenderingEnabled(true)
                    .setResizeOptions(resizeOptions)
                    .build();

            //初始化DraweeController
            AbstractDraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setUri(uri.getValue())
                    .setTapToRetryEnabled(tapToRetryEnabled)
                    .setOldController(TGDraweeView.this.getController())
                    .setImageRequest(request)
                    .build();

            if(null == controllerListener)
            {
                controllerListener = new TGDraweeControllerListener(this);
            }
            //设置图片加载监听器
            draweeController.addControllerListener(controllerListener);
            TGDraweeView.this.setController(draweeController);
        }

        /**
         * 新生成一个GenericDraweeHierarchy
         * @return
         */
        private GenericDraweeHierarchy newHierarchy()
        {
            GenericDraweeHierarchyBuilder draweeHierarchyBuilder = new GenericDraweeHierarchyBuilder(TGDraweeView.this.getResources());
            //设置占位图背景色
            if(!placeHolderBackgroundColor.isNull())
            {
                placeHolderBackgroundColor.resetChangeStatus();
                draweeHierarchyBuilder.setBackground(new ColorDrawable(placeHolderBackgroundColor.getValue()));
            }

            return draweeHierarchyBuilder.build();
        }

        public FrescoConfigs placeHolder(Drawable drawable)
        {
            this.placeHolder = drawable;
            return this;
        }

        public FrescoConfigs placeHolder(int resId)
        {
            this.placeHolder = getResources().getDrawable(resId);
            return this;
        }

        public FrescoConfigs placeHolderBackgroundColor(int color)
        {
            this.placeHolderBackgroundColor.setValue(color);
            return this;
        }

        public FrescoConfigs scaleType(ScalingUtils.ScaleType scaleType)
        {
            this.scaleType = scaleType;
            return this;
        }

        public FrescoConfigs circle(boolean circle)
        {
            this.circle  = circle;
            return this;
        }

        public FrescoConfigs webImage(String url)
        {
            uri.setValue(url);
            return this;
        }

        public FrescoConfigs localImage(String filePath)
        {
            uri.setValue("file://" + filePath);
            return this;
        }

        public FrescoConfigs resourceImage(int resourceId)
        {
            uri.setValue("res:///" + resourceId);
            return this;
        }

        public FrescoConfigs fadeDuration(int fadeDuration)
        {
            this.fadeDuration = fadeDuration;
            return this;
        }

        public FrescoConfigs tapToRetryEnabled(boolean enabled)
        {
            this.tapToRetryEnabled = enabled;
            return this;
        }
    }

    private class TGDraweeControllerListener implements ControllerListener
    {
        private FrescoConfigs configs;

        private static final int MAX_RETRY = 10;

        private int retry = 0;

        public TGDraweeControllerListener(FrescoConfigs configs)
        {
            this.configs = configs;
        }
        @Override
        public void onSubmit(String id, Object callerContext)
        {

        }

        @Override
        public void onFinalImageSet(String id, Object imageInfo, Animatable animatable)
        {

        }

        @Override
        public void onIntermediateImageSet(String id, Object imageInfo)
        {

        }

        @Override
        public void onIntermediateImageFailed(String id, Throwable throwable)
        {

        }

        @Override
        public void onFailure(String id, Throwable throwable)
        {
            final int configsHashCode = configs.hashCode();
            HASH_CODE_CACHE.put(configsHashCode,true);
            retry++;
            LOG.e("[Method:TGDraweeControllerListener:onFailure] id == " + id + "retry == " + retry + " error == " + throwable.getMessage());
            if(retry <= MAX_RETRY)
            {
                HANDLER.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(HASH_CODE_CACHE.containsKey(configsHashCode))
                        {
                            configs.display();
                        }
                    }
                }, 500);
            }
        }

        @Override
        public void onRelease(String id)
        {

        }
    }
}
