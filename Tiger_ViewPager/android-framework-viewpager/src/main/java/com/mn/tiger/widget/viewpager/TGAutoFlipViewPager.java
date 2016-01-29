package com.mn.tiger.widget.viewpager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mn.tiger.log.Logger;

import java.util.List;

/**
 * 可自动翻页的ViewPager
 */
public class TGAutoFlipViewPager extends ViewPager
{
	private static final Logger LOG = Logger.getLogger(TGAutoFlipViewPager.class);
	/**
	 * 当前显示的页码
	 */
	private int currentPageNum = 0;

	/**
	 * 翻页周期
	 */
	private int duration = 4000;

	private AutoFlipRunnable autoFlipRunnable;

	/**
	 * 用于接收定时翻页消息的Handler
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			setCurrentItem(msg.what);
		}
	};

	/**
	 * 内置的页码改变监听器
	 */
	private OnPageChangeListener internalPageChangeListener = null;

	public TGAutoFlipViewPager(Context context)
	{
		super(context);
		setListeners();
	}

	public TGAutoFlipViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setListeners();
	}

	/**
	 * 设置触摸事件监听器
	 */
	private void setListeners()
	{
		setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    //当按下、滑动时不能自动滚动
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        if (null != autoFlipRunnable)
                        {
                            autoFlipRunnable.setContinue(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP://当手指抬起后不能自动滚动
                        if (null != autoFlipRunnable)
                        {
                            autoFlipRunnable.setContinue(true);
                        }
                        break;
                    default:
                        if (null != autoFlipRunnable)
                        {
                            autoFlipRunnable.setContinue(true);
                        }
                        break;
                }
                return false;
            }
        });

		//设置默认的OnPageChangeListener，记录currentPageNum
		super.setOnPageChangeListener(new OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int page)
            {
                currentPageNum = page;
                if (null != internalPageChangeListener)
                {
                    internalPageChangeListener.onPageSelected(page);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {
                if (null != internalPageChangeListener)
                {
                    internalPageChangeListener.onPageScrolled(arg0, arg1, arg2);
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0)
            {
                if (null != internalPageChangeListener)
                {
                    internalPageChangeListener.onPageScrollStateChanged(arg0);
                }
            }
        });
	}

	@Override
	public void setAdapter(PagerAdapter adapter)
	{
		if(adapter instanceof CirclePagerAdapter)
		{
			super.setAdapter(adapter);
			//初始化起始页
			setCurrentItem(0);
		}
		else
		{
			throw new IllegalArgumentException("the parameter \"adapter\" must extends TGAutoFlipViewPager.CirclePagerAdapter");
		}
	}

	/**
	 * 开始滚动
	 */
	public void startScroll()
	{
		LOG.d("[Method:startScroll]");
		if(null == autoFlipRunnable && getAdapter().getCount() > 1)
		{
			autoFlipRunnable = new AutoFlipRunnable();
			autoFlipRunnable.setContinue(true);
			autoFlipRunnable.setScrolling(true);
			handler.postDelayed(autoFlipRunnable, duration);
		}
	}

	@Override
	public void setCurrentItem(int item)
	{
		currentPageNum = item;
		super.setCurrentItem(currentPageNum);
	}

	/**
	 * 停止自动滚动
	 */
	public void stopScroll()
	{
		LOG.d("[Method:stopScroll]");
        if(null != autoFlipRunnable)
        {
            autoFlipRunnable.setScrolling(false);
            autoFlipRunnable = null;
        }
	}

	/**
	 * 设置滚动周期
	 * @param duration
	 */
	public void setScrollDuration(int duration)
	{
		this.duration = duration;
	}

	/**
	 * 重写基类的设置页面切换监听接口
	 */
	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener)
	{
		this.internalPageChangeListener = listener;
	}

	private final class AutoFlipRunnable implements Runnable
	{
		/**
		 * 是否继续翻页
		 */
		private boolean isContinue = true;

		/**
		 * 是否正在滚动
		 */
		private boolean isScrolling = false;

		@Override
		public void run()
		{
			if (isContinue && isScrolling)
			{
				//定时重发
				currentPageNum++;
				handler.sendEmptyMessage(currentPageNum);
				handler.postDelayed(this, duration);

				LOG.d("[Method:AutoFlipRunnable:run] to next page == " + currentPageNum);
			}
		}

		public void setScrolling(boolean scrolling)
		{
			this.isScrolling = scrolling;
		}

		public boolean isScrolling()
		{
			return isScrolling;
		}

		public void setContinue(boolean isContinue)
		{
			this.isContinue = isContinue;
		}
	}

	/**
	 * 循环滚动PagerAdapter
	 */
	public static class CirclePagerAdapter<T> extends TGRecyclePagerAdapter<T>
	{
		public CirclePagerAdapter(Activity activity, List<T> pagerData,
								  Class<? extends TGPagerViewHolder<T>> viewHolderClazz)
		{
			super(activity, pagerData, viewHolderClazz);
		}

		@Override
		public int getCount()
		{
			//设置个数为最大值，无限循环
			return Integer.MAX_VALUE;
		}

		/**
		 * 获取真实页数（不循环的页数）
		 * @return
		 */
		public int getRealPageCount()
		{
			return getPagerData().size();
		}

		@Override
		public final Object instantiateItem(ViewGroup container, int position)
		{
			int size = 1;
			if(!getPagerData().isEmpty())
			{
				size = getPagerData().size();
			}
			//计算页码，取余数
			return super.instantiateItem(container, position % size);
		}
	}

}
