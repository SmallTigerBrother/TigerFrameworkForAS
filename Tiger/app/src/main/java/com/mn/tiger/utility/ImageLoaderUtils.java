package com.mn.tiger.utility;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.mn.tiger.app.TGApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Imageloader工具类
 */
public class ImageLoaderUtils
{
    private static final int PAUSE_IMAGE_LOADER_SCROLL_DY = 300;

    private static final int RESUME_IMAGE_LOADER_SCROLL_DY = 150;

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
        TGApplication.getInstance().getImageLoader().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(resId + ""), imageView,
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
        TGApplication.getInstance().getImageLoader().displayImage(ImageDownloader.Scheme.DRAWABLE.wrap(resId + ""), imageView, options);
    }

    /**
     * 加载本地的图片资源
     * @param filePath
     * @param imageView
     */
    public static void displayLocalImage(String filePath, ImageView imageView)
    {
        TGApplication.getInstance().getImageLoader().displayImage(ImageDownloader.Scheme.FILE.wrap(filePath), imageView,
                TGApplication.getInstance().getResourceDisplayImageOptions());
    }

    /**
     * 加载本地的图片资源
     * @param filePath
     * @param imageView
     * @param options
     */
    public static void displayLocalImage(String filePath, ImageView imageView, DisplayImageOptions options)
    {
        TGApplication.getInstance().getImageLoader().displayImage(ImageDownloader.Scheme.FILE.wrap(filePath), imageView, options);
    }

    /**
     * 加载本地的图片资源
     * @param filePath
     * @param imageView
     * @param listener
     */
    public static void displayLocalImage(String filePath, ImageView imageView, ImageLoadingListener listener)
    {
        TGApplication.getInstance().getImageLoader().displayImage(ImageDownloader.Scheme.FILE.wrap(filePath),imageView,
                TGApplication.getInstance().getResourceDisplayImageOptions() , listener);
    }

    /**
     * 加载本地的图片资源
     * @param filePath
     * @param imageView
     * @param listener
     */
    public static void displayLocalImage(String filePath, ImageView imageView, DisplayImageOptions options,
                                         ImageLoadingListener listener)
    {
        TGApplication.getInstance().getImageLoader().displayImage(ImageDownloader.Scheme.FILE.wrap(filePath),
                imageView, options, listener);
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
     * 异步转换图片大小
     * @param uri  图片地址
     * @param imageSize  转换大小
     * @return
     */
    public static Bitmap loadImageSync(String uri, ImageSize imageSize)
    {
        return TGApplication.getInstance().getImageLoader().loadImageSync(ImageDownloader.Scheme.FILE.wrap(uri),
                imageSize, simpleDisplayImageOptions());
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
                        ImageLoaderUtils.resumeImageLoader();
                        break;
                    case SCROLL_STATE_FLING:
                        ImageLoaderUtils.pauseImageLoader();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
            }
        };
    }

    public static RecyclerView.OnScrollListener newLazyLoadRecyclerViewOnScrollListener()
    {
        return new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                dy = Math.abs(dy);
                if(dy > PAUSE_IMAGE_LOADER_SCROLL_DY)
                {
                    ImageLoaderUtils.pauseImageLoader();
                }
                else if(dy < PAUSE_IMAGE_LOADER_SCROLL_DY)
                {
                    ImageLoaderUtils.resumeImageLoader();
                }
            }
        };
    }

    public static DisplayImageOptions simpleDisplayImageOptions()
    {
        DisplayImageOptions.Builder builder =  new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY);

        return builder.build();
    }

    public static class OOMRetryImageLoaderListener implements ImageLoadingListener
    {
        private static final Handler retryHandler = new Handler();

        volatile int retryCount = 0;

        @Override
        public void onLoadingStarted(String imageUri, View view)
        {
        }

        @Override
        public void onLoadingFailed(final String imageUri, final View view, FailReason failReason)
        {
            switch (failReason.getType())
            {
                case OUT_OF_MEMORY:
                    //回收内存
                    TGApplication.getInstance().getImageLoader().clearMemoryCache();
                    Runtime.getRuntime().gc();
                    //重新加载图片
                    retryHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            retryCount++;
                            if (retryCount <= 3)
                            {
                                ImageLoaderUtils.displayImage(imageUri, (ImageView) view, OOMRetryImageLoaderListener.this);
                            }
                        }
                    }, 1000);

                    break;

                default:
                    break;
            }
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
        {
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view)
        {
        }
    }

}
