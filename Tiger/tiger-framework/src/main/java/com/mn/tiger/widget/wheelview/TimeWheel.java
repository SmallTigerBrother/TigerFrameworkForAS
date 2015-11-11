package com.mn.tiger.widget.wheelview;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mn.tiger.utility.CR;
import com.mn.tiger.utility.DisplayUtils;
import com.mn.tiger.widget.wheelview.adapters.ArrayWheelAdapter;
import com.mn.tiger.widget.wheelview.adapters.NumericWheelAdapterForTime;

import java.util.Calendar;

/**
 * 该类作用及功能说明 A view for selecting a hour / minute / sencond based on a calendar like
 * layout
 * @version V2.0
 * @see JDK1.6,android-8
 */
public class TimeWheel extends FrameLayout
{
	/** default text size */
	private int defaultTextSize = 14;
	/** 日期默认显示行数 */
	private final int VISIBLE_ITEMS = 3;
	/** 字体上/下/左/右的间距 */
	private final int text_padding_value = 4;
	/** UI Components */
	private WheelView mSecondWheel;
	private WheelView mMinuteWheel;
	private WheelView mHourWheel;

	private Context mContext;
	/**
	 * How we notify users the time has changed.
	 */
	private OnTimeChangedListener mOnTimeChangedListener;

	private int mSecond;
	private int mMinute;
	private int mHour;

	/**
	 * The callback used to indicate the user changes the date.
	 */
	public interface OnDateChangedListener
	{

		/**
		 * @param view
		 *            The view associated with this listener.
		 * @param year
		 *            The year that was set.
		 * @param monthOfYear
		 *            The month that was set (0-11) for compatibility with
		 *            {@link java.util.Calendar}.
		 * @param dayOfMonth
		 *            The day of the month that was set.
		 */
		void onDateChanged(TimeWheel view, int year, int monthOfYear, int dayOfMonth);
	}

	public TimeWheel(Context context)
	{
		this(context, null);
	}

	public TimeWheel(Context context, int textSize)
	{
		super(context);
		defaultTextSize = textSize;
		this.mContext = context;
		this.initWheelView();
	}

	public TimeWheel(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public TimeWheel(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.mContext = context;
		this.initWheelView();
	}

	/**
	 * 该方法的作用:初始化滚轮式日期选择器
	 * @date 2013-2-18
	 */
	private void initWheelView()
	{
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(CR.getLayoutId(mContext, "tiger_date_wheel_layout"), this, true);
		//hourWheel
		this.mHourWheel = (WheelView) findViewById(CR.getViewId(mContext, "tiger_year"));
		initHourWheel(mHourWheel);

		//minuteWheel
		this.mMinuteWheel = (WheelView) findViewById(CR.getViewId(mContext, "tiger_month"));
		initMinuteWheel(mMinuteWheel);

		//secondWheel
		this.mSecondWheel = (WheelView) findViewById(CR.getViewId(mContext, "tiger_day"));
		initSecondWheel(mSecondWheel);

		this.init(mHour, mMinute, mSecond, null);
	}

	/**
	 * 初始化小时WheelView
	 * @param mHourWheel
	 */
	protected void initHourWheel(WheelView mHourWheel)
	{
		this.mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		this.mHourWheel.setViewAdapter(new TimeNumericAdapter(
				this.getContext(), 1, 24, this.mHour));
		this.mHourWheel.setVisibleItems(VISIBLE_ITEMS);
		this.mHourWheel.setCyclic(true);
		this.mHourWheel.addChangingListener(new OnWheelChangedListener()
		{
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue)
			{
				// oldValue and newValue are the Index of the Item.
				mHour = newValue;
				notifyDateChanged();
			}
		});
	}

	/**
	 * 初始化分钟WheelView
	 * @param mHourWheel
	 */
	protected void initMinuteWheel(WheelView mHourWheel)
	{
		this.mMinute = Calendar.getInstance().get(Calendar.MINUTE);
		this.mMinuteWheel.setViewAdapter(new TimeNumericAdapter(getContext(), 1, 60, this.mMinute));
		this.mMinuteWheel.setVisibleItems(VISIBLE_ITEMS);
		this.mMinuteWheel.setCyclic(true);
		this.mMinuteWheel.addChangingListener(new OnWheelChangedListener()
		{
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue)
			{
				/*
				 * newValue is Index,then must to add start value
				 */
				mMinute = newValue;
				notifyDateChanged();
			}
		});
	}

	/**
	 * 初始化秒WheelView
	 * @param mHourWheel
	 */
	protected void initSecondWheel(WheelView mHourWheel)
	{
		this.mSecond = Calendar.getInstance().get(Calendar.SECOND);
		this.mSecondWheel.setVisibleItems(VISIBLE_ITEMS);
		this.mSecondWheel.setViewAdapter(new TimeNumericAdapter(getContext(), 1, 60, this.mSecond));
		this.mSecondWheel.setCyclic(true);
		this.mSecondWheel.addChangingListener(new OnWheelChangedListener()
		{
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue)
			{
				mSecond = newValue;
				notifyDateChanged();
			}
		});

	}

	/**
	 * 设置小时WheelView背景资源
	 * @param drawable
	 */
	protected void setHourWheelBackground(Drawable drawable)
	{
		this.mHourWheel.setBackgroundDrawable(drawable);
	}

	/**
	 * 设置分钟WheelView背景资源
	 * @param drawable
	 */
	protected void setMinuteWheelBackground(Drawable drawable)
	{
		this.mMinuteWheel.setBackgroundDrawable(drawable);
	}

	/**
	 * 设置秒WheelView背景资源
	 * @param drawable
	 */
	protected void setSecondWheelBackground(Drawable drawable)
	{
		this.mSecondWheel.setBackgroundDrawable(drawable);
	}

	/**
	 * 该方法的作用:指定时分秒，更新滚轮式时间选择器视图
	 * @date 2013-2-18
	 * @param hour
	 * @param minute
	 * @param second
	 */
	public void updateTime(int hour, int minute, int second)
	{
		if (mHour != hour || mMinute != minute || mSecond != second)
		{
			mHour = hour;
			mMinute = minute;
			mSecond = second;
			updateSpinners();
			notifyDateChanged();
		}
	}

	/**
	 * Initialize the state.
	 * @param hour
	 * @param minute
	 * @param second
	 * @param onDateChangedListener
	 */
	public void init(int hour, int minute, int second,
			OnTimeChangedListener onTimeChangedListener)
	{
		mHour = hour;
		mMinute = minute;
		mSecond = second;
		mOnTimeChangedListener = onTimeChangedListener;
		updateSpinners();
	}

	/**
	 * 设置时间变化事件监听器
	 * @param OnTimeChangedListener
	 */
	public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener)
	{
		mOnTimeChangedListener = onTimeChangedListener;
	}

	/**
	 * 更新各个滚动视图
	 */
	private void updateSpinners()
	{
		mHourWheel.setCurrentItem(this.mHour);

		mMinuteWheel.setCurrentItem(this.mMinute);

		/**
		 * Warning:第一次显示，必须设置无动画
		 */
		mSecondWheel.setViewAdapter(new TimeNumericAdapter(this.getContext(), 1, 60, mSecond));
		mSecondWheel.setCurrentItem(mSecond, false);
	}

	/**
	 * 获取小时
	 * @return
	 */
	public int getHour()
	{
		return mHour;
	}

	/**
	 * 获取分钟
	 * @return
	 */
	public int getMinute()
	{
		return mMinute;
	}

	/**
	 * 获取秒
	 * @return
	 */
	public int getSecond()
	{
		return mSecond;
	}

	private static class SavedState extends BaseSavedState
	{
		private final int mHour;
		private final int mMinute;
		private final int mSecond;

		/**
		 * Constructor called from {@link DatePicker#onSaveInstanceState()}
		 */
		private SavedState(Parcelable superState, int hour, int minute, int second)
		{
			super(superState);
			mHour = hour;
			mMinute = minute;
			mSecond = second;
		}

		/**
		 * Constructor called from {@link #CREATOR}
		 */
		private SavedState(Parcel in)
		{
			super(in);
			mHour = in.readInt();
			mMinute = in.readInt();
			mSecond = in.readInt();
		}

		public int getHour()
		{
			return mHour;
		}

		public int getMinute()
		{
			return mMinute;
		}

		public int getSecond()
		{
			return mSecond;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags)
		{
			super.writeToParcel(dest, flags);
			dest.writeInt(mHour);
			dest.writeInt(mMinute);
			dest.writeInt(mSecond);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>()
		{
			public SavedState createFromParcel(Parcel in)
			{
				return new SavedState(in);
			}

			public SavedState[] newArray(int size)
			{
				return new SavedState[size];
			}
		};
	}

	/**
	 * Override so we are in complete control of save / restore for this widget.
	 */
	@Override
	protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container)
	{
		dispatchThawSelfOnly(container);
	}

	@Override
	protected Parcelable onSaveInstanceState()
	{
		Parcelable superState = super.onSaveInstanceState();
		return new SavedState(superState, mHour, mMinute, mSecond);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		mHour = ss.getHour();
		mMinute = ss.getMinute();
		mSecond = ss.getSecond();
		updateSpinners();
	}

	private void notifyDateChanged()
	{
		if (mOnTimeChangedListener != null)
		{
			mOnTimeChangedListener.onTimeChanged(this, mHour, mMinute, mSecond);
		}
	}
	
	/**
     * The callback interface used to indicate the time has been adjusted.
     */
    public interface OnTimeChangedListener 
    {
        /**
         * @param view The view associated with this listener.
         * @param hourOfDay The current hour.
         * @param minute The current minute.
         */
        void onTimeChanged(TimeWheel view, int hourOfDay, int minute, int second);
    }

	/**
	 * 该类作用及功能说明 Adapter for numeric wheels. Highlights the current value.
	 * @version V2.0
	 * @see JDK1.6,android-8
	 */
	private class TimeNumericAdapter extends NumericWheelAdapterForTime
	{
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;

		/**
		 * Constructor
		 */
		public TimeNumericAdapter(Context context, int minValue, int maxValue, int current)
		{
			super(context, minValue, maxValue);
			this.currentValue = current;
			setTextSize(defaultTextSize);
		}

		@Override
		protected void configureTextView(TextView view)
		{
			super.configureTextView(view);
			if (currentItem == currentValue)
			{
				// view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(Typeface.SANS_SERIF);
			view.setPadding(0, DisplayUtils.dip2px(context, text_padding_value), 0,
					DisplayUtils.dip2px(context, text_padding_value));
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent)
		{
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	/**
	 * 该类作用及功能说明 Adapter for string based wheel. Highlights the current value.
	 * 
	 * @version V2.0
	 * @see JDK1.6,android-8
	 */
	@SuppressWarnings("unused")
	private class TimeArrayAdapter extends ArrayWheelAdapter<String>
	{
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;

		/**
		 * Constructor
		 */
		public TimeArrayAdapter(Context context, String[] items, int current)
		{
			super(context, items);
			this.currentValue = current;
			setTextSize(defaultTextSize);
		}

		@Override
		protected void configureTextView(TextView view)
		{
			super.configureTextView(view);
			if (currentItem == currentValue)
			{
				// view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(Typeface.SANS_SERIF);
			view.setPadding(0, DisplayUtils.dip2px(context, text_padding_value), 0,
					DisplayUtils.dip2px(context, text_padding_value));
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent)
		{
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}
}
