package com.mn.tiger.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.mn.tiger.log.Logger;

/**
 * 终止时间，倒计时TextView
 */
public class CountDownTimeView extends TextView
{
	private static final Logger LOG = Logger.getLogger(CountDownTimeView.class);
	
	/**
	 * 终止时间差，即距离结束还有多少毫秒
	 */
	private long deltaTimeMillis;
	
	private Handler handler = new Handler();
	
	/**
	 * 循环更新的线程
	 */
	private TimeRunnable timeRunnable; 
	
	/**
	 * 时间变化回调接口
	 */
	private OnTimeChangedListener listener;
	
	/**
	 * 时间变化回调接口
	 */
	public static interface OnTimeChangedListener
	{
		/**
		 * 启动倒计时开始方法
		 * @param view
		 * @param lastTime
		 */
		void onTimeStart(View view, long lastTime);
		
		/**
		 * 时间变化回调方法
		 * @param view
		 * @param lastTime
		 */
		void onTimeChanged(View view, long lastTime);
		
		/**
		 * 倒计时结束回调方法
		 * @param view
		 */
		void onTimeEnd(View view);
		
		/**
		 * 倒计时停止回调方法
		 * @param view
		 * @param lastTime 停止时的剩余时间
		 */
		void onStop(View view, long lastTime);
	}
	
	public CountDownTimeView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
	}
	
	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		//视图从界面上移除时，停止倒计时
		stop();
	}
	
	/**
	 * 设置终止时间差，单位是毫秒，即距离结束还有多少毫秒
	 * @param deltaTimeMillis 终止时间差
	 */
	public void setDeltaTime(long deltaTimeMillis, OnTimeChangedListener listener)
	{
		if(null != timeRunnable)
		{
			timeRunnable.stop();
		}
		
		this.deltaTimeMillis = deltaTimeMillis;
		this.listener = listener;
		
		timeRunnable = new TimeRunnable();
	}
	
	/**
	 * 启动倒计时
	 */
	public void start()
	{
		if(deltaTimeMillis > 0)
		{
			if(null != listener)
			{
				listener.onTimeStart(this, deltaTimeMillis);
			}
			handler.postDelayed(timeRunnable, 1000);
		}
		else
		{
			LOG.e("[Method:start] the deltaTimeMillis must be larger than 0");
		}
	}
	
	/**
	 * 停止计时
	 */
	public void stop()
	{
		if(null != timeRunnable)
		{
			timeRunnable.stop();
		}
	}
	
	/**
	 * 计时线程
	 */
	private class TimeRunnable implements Runnable
	{
		private boolean stop = false;
		
		@Override
		public void run()
		{
			//倒计时停止，直接返回
			if(stop)
			{
				if(null != listener)
				{
					listener.onStop(CountDownTimeView.this, deltaTimeMillis);
				}
				return;
			}
			
			//调用listener方法，通知界面更新
			deltaTimeMillis = deltaTimeMillis - 1000;
			if(deltaTimeMillis < 0)
			{
				if(null != listener)
				{
					listener.onTimeEnd(CountDownTimeView.this);
				}
				return;
			}
			else
			{
				if(null != listener)
				{
					listener.onTimeChanged(CountDownTimeView.this, deltaTimeMillis);
				}
				//1秒后重新发送
				handler.postDelayed(this, 1000);
			}
		}
		
		/**
		 * 设置倒计时停止
		 */
		public void stop()
		{
			this.stop = true;
		}
	}
	
}
