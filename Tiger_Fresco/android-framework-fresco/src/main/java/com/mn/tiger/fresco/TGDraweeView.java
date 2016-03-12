package com.mn.tiger.fresco;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

/**
 * Created by peng on 16/1/31.
 */
public class TGDraweeView extends DraweeView<GenericDraweeHierarchy>
{
    private static final Logger LOG = Logger.getLogger(TGDraweeView.class);

    private FrescoConfigs frescoConfigs;

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

    /**
     * Fresco参数
     */
    public final class FrescoConfigs
    {
        private GenericDraweeHierarchy draweeHierarchy;
        //图片资源
        private Variable<String> uri = new Variable<>();
        //占位图片
        private Variable<Drawable> placeHolder = new Variable<>();
        //占位图背景色
        private Variable<Integer> placeHolderBackgroundColor = new Variable<>();
        //加载图片的显示ScaleType
        private Variable<ScalingUtils.ScaleType> scaleType = new Variable<>(ScalingUtils.ScaleType.CENTER_CROP);
        //是否显示成圆形
        private Variable<Boolean> circle = new Variable<>(false);
        //圆角边框颜色
        private Variable<Integer> roundBorderColor = new Variable<>(Color.TRANSPARENT);
        //圆角边框宽度
        private Variable<Integer> roundBorderWidth = new Variable<>(0);
        //是否自动旋转
        private Variable<Boolean> autoRotate = new Variable<>(true);
        //视图宽度
        private Variable<Integer> width = new Variable<>(0);
        //视图高度
        private Variable<Integer> height = new Variable<>(0);
        /**
         * 显示图片
         */
        public void display()
        {
            if(null == draweeHierarchy)
            {
                draweeHierarchy = newHierarchy();
                TGDraweeView.this.setHierarchy(draweeHierarchy);
            }
            else
            {
                //如果占位背景色发生改变，重新生成draweeHierarchy
                if (!placeHolderBackgroundColor.isNull() && placeHolderBackgroundColor.isChanged())
                {
                    placeHolderBackgroundColor.resetChangeStatus();

                    draweeHierarchy = newHierarchy();
                }

                //设置占位图片
                if(!placeHolder.isNull() && placeHolder.isChanged())
                {
                    placeHolder.resetChangeStatus();

                    draweeHierarchy.setPlaceholderImage(placeHolder.getValue());
                }

                //设置圆角参数
                if((!circle.isNull() && circle.isChanged()) || (!roundBorderColor.isNull() && roundBorderColor.isChanged())
                        || (!roundBorderWidth.isNull() && roundBorderWidth.isChanged()))
                {
                    circle.resetChangeStatus();
                    roundBorderColor.resetChangeStatus();
                    roundBorderWidth.resetChangeStatus();

                    RoundingParams roundingParams = new RoundingParams();
                    roundingParams.setRoundAsCircle(circle.getValue());
                    roundingParams.setBorder(roundBorderColor.getValue(), roundBorderWidth.getValue());
                    draweeHierarchy.setRoundingParams(roundingParams);
                }

                //设置加载图ScaleType
                if(!scaleType.isNull() && scaleType.isChanged())
                {
                    scaleType.resetChangeStatus();

                    draweeHierarchy.setActualImageScaleType(scaleType.getValue());
                }
            }

            width.setValue(TGDraweeView.this.getLayoutParams().width);
            height.setValue(TGDraweeView.this.getLayoutParams().height);
            //设置图片，判断数据是否已发生变化
            if((!uri.isNull() && uri.isChanged()) || (!width.isNull() && width.isChanged()) ||
                    (!height.isNull() && !height.isChanged()) || (!autoRotate.isNull() && autoRotate.isChanged()))
            {
                uri.resetChangeStatus();
                width.resetChangeStatus();
                height.resetChangeStatus();
                autoRotate.resetChangeStatus();

                ResizeOptions resizeOptions = new ResizeOptions(width.getValue(), height.getValue());
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri.getValue()))
                        .setAutoRotateEnabled(autoRotate.getValue())
                        .setProgressiveRenderingEnabled(true)
                        .setResizeOptions(resizeOptions)
                        .build();

                //初始化DraweeController
                AbstractDraweeController draweeController = Fresco.newDraweeControllerBuilder()
                        .setUri(uri.getValue())
                        .setTapToRetryEnabled(true)
                        .setOldController(TGDraweeView.this.getController())
                        .setImageRequest(request)
                        .build();
                //设置图片加载监听器
                draweeController.addControllerListener(new TGDraweeControllerListener(draweeController));
                TGDraweeView.this.setController(draweeController);
            }
            else
            {
                TGDraweeView.this.setController(TGDraweeView.this.getController());
            }
        }

        /**
         * 新生成一个GenericDraweeHierarchy
         * @return
         */
        private GenericDraweeHierarchy newHierarchy()
        {
            GenericDraweeHierarchyBuilder draweeHierarchyBuilder = new GenericDraweeHierarchyBuilder(TGDraweeView.this.getResources());
            //设置占位图片
            if (!placeHolder.isNull())
            {
                draweeHierarchyBuilder.setPlaceholderImage(placeHolder.getValue(), ScalingUtils.ScaleType.CENTER_INSIDE);
            }
            //设置占位图背景色
            if(!placeHolderBackgroundColor.isNull())
            {
                draweeHierarchyBuilder.setBackground(new ColorDrawable(placeHolderBackgroundColor.getValue()));
            }

            //设置是否显示成圆形
            if(!circle.isNull())
            {
                RoundingParams roundingParams = new RoundingParams();
                roundingParams.setRoundAsCircle(circle.getValue());
                roundingParams.setBorder(roundBorderColor.getValue(), roundBorderWidth.getValue());
                draweeHierarchyBuilder.setRoundingParams(roundingParams).build();
            }
            //设置加载的图片显示的ScaleType
            if(!scaleType.isNull())
            {
                draweeHierarchyBuilder.setActualImageScaleType(scaleType.getValue());
            }

            return draweeHierarchyBuilder.build();
        }

        public FrescoConfigs placeHolder(Drawable drawable)
        {
            this.placeHolder.setValue(drawable);
            return this;
        }

        public FrescoConfigs placeHolder(int resId)
        {
            this.placeHolder.setValue(getResources().getDrawable(resId));
            return this;
        }

        public FrescoConfigs placeHolderBackgroundColor(int color)
        {
            this.placeHolderBackgroundColor.setValue(color);
            return this;
        }

        public FrescoConfigs scaleType(ScalingUtils.ScaleType scaleType)
        {
            this.scaleType.setValue(scaleType);
            return this;
        }

        public FrescoConfigs circle(boolean circle)
        {
            this.circle.setValue(circle);
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
    }

    private static class TGDraweeControllerListener implements ControllerListener
    {
        private AbstractDraweeController draweeController;

        private static final int MAX_RETRY = 2;

        private int retry = 0;

        public TGDraweeControllerListener(AbstractDraweeController controller)
        {
            this.draweeController = controller;
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
            LOG.e("[Method:TGDraweeControllerListener:onFailure] id == " + id + " error == " + throwable.getMessage());
            if(++retry <= MAX_RETRY)
            {
                draweeController.onClick();
            }
        }

        @Override
        public void onRelease(String id)
        {

        }
    }
}
