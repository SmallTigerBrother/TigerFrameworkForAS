package com.mn.tiger.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.mn.tiger.app.TGApplication;
import com.mn.tiger.widget.viewflow.internal.PLA_AbsListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Imageloader工具类
 */
public class ImageLoaderUitls
{
    private static Handler retryHandler = new Handler();
    
    /**
     * 异步加载图片
     * @param uri 图片地址
     * @param imageView
     */
    public static void displayImage(String uri, ImageView imageView)
    {
        TGApplication.getInstance().getImageLoader().displayImage(uri, imageView);
    }
    
    /**
     * 异步加载图片
     * @param uri 图片地址
     * @param imageAware
     */
    public static void displayImage(String uri, ImageAware imageAware)
    {
        TGApplication.getInstance().getImageLoader().displayImage(uri, imageAware);
    }
    
    
    /**
     * 异步加载图片
     * @param uri 图片地址
     * @param imageView
     * @param listener 图片加载监听接口
     */
    public static void displayImage(String uri, ImageView imageView, ImageLoadingListener listener)
    {
        TGApplication.getInstance().getImageLoader().displayImage(uri, imageView, listener);
    }
    
    /**
     * 异步加载图片
     * @param uri 图片地址
     * @param imageView
     * @param options 图片显示参数
     */
    public static void displayImage(String uri, ImageView imageView, DisplayImageOptions options)
    {
        TGApplication.getInstance().getImageLoader().displayImage(uri, imageView, options);
    }
    
    /**
     * 异步加载图片
     * @param uri 图片地址
     * @param imageView
     * @param options 图片显示参数
     * @param listener 图片加载监听接口
     */
    public static void displayImage(String uri, ImageView imageView, DisplayImageOptions options,
                                    ImageLoadingListener listener)
    {
        TGApplication.getInstance().getImageLoader().displayImage(uri, imageView,
                                                                  options, listener);
    }
    
    /**
     * 加载Resource中的图片资源
     * @param resId
     * @param imageView
     */
    public static void displayResourceImage(int resId, ImageView imageView)
    {
        TGApplication.getInstance().getImageLoader().displayImage("drawable://" + resId, imageView,
                                                                  TGApplication.getInstance().getResourceDisplayImageOptions());
    }
    
    /**
     * 加载Resource中的图片资源
     * @param resId
     * @param imageView
     * @param options
     */
    public static void displayResourceImage(int resId, ImageView imageView, DisplayImageOptions options)
    {
        TGApplication.getInstance().getImageLoader().displayImage("drawable://" + resId, imageView, options);
    }
    
    /**
     * 异步下载图片
     * @param uri 图片地址
     * @param listener 图片加载监听接口
     */
    public static void loadImage(String uri, ImageLoadingListener listener)
    {
        TGApplication.getInstance().getImageLoader().loadImage(uri, listener);
    }
    
    /**
     * 异步下载图片
     * @param uri 图片地址
     * @param options 图片显示参数
     * @param listener 图片加载监听接口
     */
    public static void loadImage(String uri, DisplayImageOptions options, ImageLoadingListener listener)
    {
        TGApplication.getInstance().getImageLoader().loadImage(uri, options, listener);
    }
    
    /**
     * 获取磁盘缓存的大小
     * @return 磁盘缓存大小，单位为byte
     */
    @SuppressWarnings("deprecation")
    public static double getDiskCacheSize()
    {
        return FileUtils.getDirSize(TGApplication.getInstance().getImageLoader().getDiskCache().getDirectory());
    }
    
    /**
     * 清理磁盘缓存
     */
    public static void clearDiskCache()
    {
        TGApplication.getInstance().getImageLoader().clearDiskCache();
    }
    
    /**
     * 清理内存缓存
     */
    public static void clearMemoryCache()
    {
        TGApplication.getInstance().getImageLoader().clearMemoryCache();
    }
    
    /**
     * 暂停ImageLoader
     */
    public static void pauseImageLoader()
    {
        TGApplication.getInstance().getImageLoader().pause();
    }
    
    /**
     * 重启ImageLoader
     */
    public static void resumeImageLoader()
    {
        TGApplication.getInstance().getImageLoader().resume();
    }
    
    /**
     * 创建一个懒加载的OnScrollListener，当屏幕滚动时不加载图片
     * @return
     */
    public static AbsListView.OnScrollListener newLazyLoadOnScrollListener()
    {
        return  new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                switch (scrollState)
                {
                    case SCROLL_STATE_IDLE:
                    case SCROLL_STATE_TOUCH_SCROLL:
                        ImageLoaderUitls.resumeImageLoader();
                        break;
                    case SCROLL_STATE_FLING:
                        ImageLoaderUitls.pauseImageLoader();
                        break;
                }
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
            }
        };
    }
    
    /**
     * 创建一个懒加载的PLAOnScrollListener，当屏幕滚动时不加载图片
     * @return
     */
    public static PLA_AbsListView.OnScrollListener newLazyloadPLAOnScrollListener()
    {
        return new PLA_AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(PLA_AbsListView view, int scrollState)
            {
                switch (scrollState)
                {
                    case SCROLL_STATE_IDLE:
                    case SCROLL_STATE_TOUCH_SCROLL:
                        ImageLoaderUitls.resumeImageLoader();
                        break;
                    case SCROLL_STATE_FLING:
                        ImageLoaderUitls.pauseImageLoader();
                        break;
                }
            }
            
            @Override
            public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
            }
        };
    }
    
    /**
     * 创建一个当内存溢出时自动重试的ImageLoadingListener
     * @param listener
     * @return
     */
    public static ImageLoadingListener newOOMRetryImageLoadingListener(final ImageLoadingListener listener)
    {
        final ImageLoadingListener imageLoadingListener = new ImageLoadingListener()
        {
            private int retryCount = 0;
            
            @Override
            public void onLoadingStarted(String imageUri, View view)
            {
                if(null != listener)
                {
                    listener.onLoadingStarted(imageUri, view);
                }
            }
            
            @Override
            public void onLoadingFailed(final String imageUri,final View view, FailReason failReason)
            {
                switch (failReason.getType())
                {
                    case OUT_OF_MEMORY:
                        //回收内存
                        TGApplication.getInstance().getImageLoader().clearMemoryCache();
                        //重新加载图片
                        retryHandler.postDelayed(new Runnable()
                                                 {
                            @Override
                            public void run()
                            {
                                retryCount++;
                                if(retryCount <= 3)
                                {
                                    displayImage(imageUri, (ImageView) view, listener);
                                }
                            }
                        }, 200);
                        
                        break;
                        
                    default:
                        break;
                }
                
                if(null != listener)
                {
                    listener.onLoadingFailed(imageUri, view, failReason);
                }
            }
            
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
            {
                if(null != listener)
                {
                    listener.onLoadingComplete(imageUri, view, loadedImage);
                }
            }
            
            @Override
            public void onLoadingCancelled(String imageUri, View view)
            {
                if(null != listener)
                {
                    listener.onLoadingCancelled(imageUri, view);
                }
            }
        };
        
        return imageLoadingListener;
    }
    
    private class OOMRetryImageLoadingListener implements ImageLoadingListener
    {
        private int retryCount = 0;
        
        private ImageLoadingListener listener;
        
        public  OOMRetryImageLoadingListener(ImageLoadingListener listener)
        {
            this.listener = listener;
        }
        
        @Override
        public void onLoadingStarted(String imageUri, View view)
        {
            if(null != listener)
            {
                listener.onLoadingStarted(imageUri, view);
            }
        }
        
        @Override
        public void onLoadingFailed(final String imageUri,final View view, FailReason failReason)
        {
            switch (failReason.getType())
            {
                case OUT_OF_MEMORY:
                    //回收内存
                    TGApplication.getInstance().getImageLoader().clearMemoryCache();
                    //重新加载图片
                    retryHandler.postDelayed(new Runnable()
                                             {
                        @Override
                        public void run()
                        {
                            retryCount++;
                            if(retryCount <= 3)
                            {
                                displayImage(imageUri, (ImageView) view, OOMRetryImageLoadingListener.this);
                            }
                            else
                            {
                                displayImage(imageUri, (ImageView)view, listener);
                            }
                        }
                    }, 300);
                    
                    break;
                    
                default:
                    break;
            }
            
            if(null != listener)
            {
                listener.onLoadingFailed(imageUri, view, failReason);
            }
        }
        
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
        {
            if(null != listener)
            {
                listener.onLoadingComplete(imageUri, view, loadedImage);
            }
        }
        
        @Override
        public void onLoadingCancelled(String imageUri, View view)
        {
            if(null != listener)
            {
                listener.onLoadingCancelled(imageUri, view);
            }
        }
    }
    
    
    /**
     *新建一个可以设置占位符的ImageLoadingListener
     * @param backgroundColor 占位符背景色
     * @return
     */
    public static ImageLoadingListener newPlaceHolderImageLoadingListener(final int backgroundColor)
    {
        return new ImageLoadingListener()
        {
            @Override
            public void onLoadingStarted(String imageUri, View view)
            {
                ((ImageView)view).setScaleType(ImageView.ScaleType.CENTER);
                ((ImageView)view).setBackgroundColor(backgroundColor);
            }
            
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason)
            {
            }
            
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
            {
                ((ImageView)view).setScaleType(ImageView.ScaleType.FIT_XY);
            }
            
            @Override
            public void onLoadingCancelled(String imageUri, View view)
            {
            }
        };
    }
}
