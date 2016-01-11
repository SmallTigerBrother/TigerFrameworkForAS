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
import com.mn.tiger.widget.wheelview.adapters.NumericWheelAdapter;

import java.util.Calendar;

/**
 * 该类作用及功能说明 A view for selecting a month / year / day based on a calendar like
 * layout. For a dialog using this view, see
 * 
 * @version V2.0
 */
public class DateWheel extends FrameLayout
{
	private static final int DEFAULT_START_YEAR = 1900;
	private static final int DEFAULT_END_YEAR = 2100;
	/** start and end year */
	private int startYear = DEFAULT_START_YEAR;
	private int endYear = DEFAULT_END_YEAR;

	/** 日期默认显示行数 */
	private final int VISIBLE_ITEMS = 3;
	/** 字体上/下/左/右的间距 */
	private final int text_padding_value = 3;
	/** UI Components */
	private WheelView mDayWheel;
	private WheelView mMonthWheel;
	private WheelView mYearWheel;

	private Context mContext;
	
	private int dayTextSize = NumericWheelAdapter.DEFAULT_TEXT_SIZE;

	/**
	 * How we notify users the date has changed.
	 */
	private OnDateChangedListener mOnDateChangedListener;

	private int mDay;
	private int mMonth;
	private int mYear;

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
		 *            {@link Calendar}.
		 * @param dayOfMonth
		 *            The day of the month that was set.
		 */
		void onDateChanged(DateWheel view, int year, int monthOfYear, int dayOfMonth);
	}

	public DateWheel(Context context)
	{
		super(context);
		this.mContext = context;
		this.initWheelView();
	}

	public DateWheel(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public DateWheel(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.mContext = context;
		this.initWheelView();
	}

	/**
	 * 该方法的作用:初始化滚轮式日期选择器 参数: 返回值: 异常: 在什么情况下调用:
	 * @date 2013-2-18
	 */
	private void initWheelView()
	{
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(CR.getLayoutId(mContext, "tiger_date_wheel_layout"), this, true);

		// year
		initYearWheel(mYearWheel);

		// month
		initMonthWheel(mMonthWheel);

		// day
		initDayWheel(mDayWheel);

		this.init(mYear, mMonth, mDay, null);
	}

	/**
	 * 初始化年WheelView
	 * @param mYearWheel
	 */
	protected void initYearWheel(WheelView mYearWheel)
	{
		Calendar calendar = Calendar.getInstance();

		this.mYearWheel = (WheelView) findViewById(CR.getViewId(mContext, "tiger_year"));
		this.mYear = calendar.get(Calendar.YEAR);
		int currYearIndex = this.mYear - this.startYear;
		this.mYearWheel.setViewAdapter(new DateNumericAdapter(this.getContext(), this.startYear,
				this.endYear, currYearIndex));
		this.mYearWheel.setVisibleItems(VISIBLE_ITEMS);
		this.mYearWheel.setCyclic(true);

		this.mYearWheel.addChangingListener(new OnWheelChangedListener()
		{
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue)
			{
				// oldValue and newValue are the Index of the Item.
				mYear = startYear + newValue;
				// Adjust max day for leap years if needed
				updateDaySpinner(true);
				notifyDateChanged();
			}
		});
	}

	/**
	 * 初始化月WheelView
	 * @param mMonthWheel
	 */
	protected void initMonthWheel(WheelView mMonthWheel)
	{
		Calendar calendar = Calendar.getInstance();
		this.mMonthWheel = (WheelView) findViewById(CR.getViewId(mContext, "tiger_month"));
		this.mMonth = calendar.get(Calendar.MONTH);
		int maxMonth = calendar.getMaximum(Calendar.MONTH) + 1;// save as
		this.mMonthWheel.setViewAdapter(new DateNumericAdapter(getContext(), 1, maxMonth,
				this.mMonth));
		this.mMonthWheel.setVisibleItems(VISIBLE_ITEMS);
		this.mMonthWheel.setCyclic(true);
		this.mMonthWheel.addChangingListener(new OnWheelChangedListener()
		{
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue)
			{
				/*
				 * We display the month 1-12 but store it 0-11 so always
				 * subtract by one to ensure our internal state is always 0-11
				 * newValue is Index,then must to add start value
				 */
				mMonth = newValue;
				updateDaySpinner(true);
				notifyDateChanged();
			}
		});
	}

	/**
	 * 初始化天WheelView
	 * @param mDayWheel
	 */
	protected void initDayWheel(WheelView mDayWheel)
	{
		Calendar calendar = Calendar.getInstance();
		this.mDayWheel = (WheelView) findViewById(CR.getViewId(mContext, "tiger_day"));
		this.mDay = calendar.get(Calendar.DAY_OF_MONTH);
		this.mDayWheel.setVisibleItems(VISIBLE_ITEMS);
		this.mDayWheel.setCyclic(true);
		this.mDayWheel.addChangingListener(new OnWheelChangedListener()
		{
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue)
			{
				mDay = newValue + 1;
				notifyDateChanged();
			}
		});
	}

	/**
	 * 设置年WheelView背景资源
	 * @param drawable
	 */
	public void setYearWheelBackgroud(Drawable drawable)
	{
		this.mYearWheel.setBackgroundDrawable(drawable);
	}

	/**
	 * 设置月WheelView背景资源
	 * @param drawable
	 */
	public void setMonthWheelBackgroud(Drawable drawable)
	{
		this.mMonthWheel.setBackgroundDrawable(drawable);
	}

	/**
	 * 设置天WheelView背景资源
	 * @param drawable
	 */
	public void setDayWheelBackground(Drawable drawable)
	{
		this.mDayWheel.setBackgroundDrawable(drawable);
	}


	/**
	 * 该方法的作用:设置起始年份
	 * @date 2013-2-18
	 * @param startYear
	 * @param endYear
	 */
	public void setYearRange(int startYear, int endYear)
	{
		if (startYear > 0 && endYear >= startYear)
		{
			this.startYear = startYear;
			this.endYear = endYear;
			int currYearIndex = this.mYear - this.startYear;
			this.mYearWheel.setViewAdapter(new DateNumericAdapter(this.getContext(),
					this.startYear, this.endYear, currYearIndex));
			this.mYearWheel.setCurrentItem(currYearIndex);
		}
	}

	/**
	 * 该方法的作用:指定年月日，更新滚轮式日期选择器视图
	 * @date 2013-2-18
	 * @param year
	 * @param monthOfYear
	 *            The month that was set (0-11) for compatibility with
	 *            {@link Calendar}.
	 * @param dayOfMonth
	 */
	public void updateDate(int year, int monthOfYear, int dayOfMonth)
	{
		if (mYear != year || mMonth != monthOfYear || mDay != dayOfMonth)
		{
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateSpinners();
			notifyDateChanged();
		}
	}

	/**
	 * Initialize the state.
	 * @param year
	 *            The initial year.
	 * @param monthOfYear
	 *            The initial month,The month that was set (0-11) for
	 *            compatibility with {@link Calendar}.
	 * @param dayOfMonth
	 *            The initial day of the month.
	 * @param onDateChangedListener
	 *            How user is notified date is changed by user, can be null.
	 */
	public void init(int year, int monthOfYear, int dayOfMonth,
			OnDateChangedListener onDateChangedListener)
	{
		mYear = year;
		mMonth = monthOfYear;
		mDay = dayOfMonth;
		mOnDateChangedListener = onDateChangedListener;
		updateSpinners();
	}

	/**
	 * 设置日期变化事件监听器
	 * @param onDateChangedListener
	 */
	public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener)
	{
		mOnDateChangedListener = onDateChangedListener;
	}

	/**
	 * 更新各个滚动视图
	 */
	private void updateSpinners()
	{
		int yearIndex = this.mYear - this.startYear;
		mYearWheel.setCurrentItem(yearIndex);
		/*
		 * The month display uses 1-12 but our internal state stores it 0-11 so
		 * add one when setting the display.
		 */
		mMonthWheel.setCurrentItem(this.mMonth - 1);
		/**
		 * Warning:第一次显示，必须设置无动画
		 */
		updateDaySpinner(false);
	}

	/**
	 * 该方法的作用:该方法的作用:Updates day wheel. Sets max days according to selected
	 * month and year
	 *
	 * @date 2013-2-19
	 * @param isAnimated
	 */
	private void updateDaySpinner(boolean isAnimated)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, startYear + mYearWheel.getCurrentItem());

		calendar.set(Calendar.MONTH, mMonthWheel.getCurrentItem());

		/** 设置day的最大范围 */
		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		mDay = Math.min(maxDays, mDay);

		mDayWheel.setViewAdapter(new DateNumericAdapter(this.getContext(), 1, maxDays, mDay));
		((NumericWheelAdapter)mDayWheel.getViewAdapter()).setTextSize(dayTextSize);

		mDayWheel.setCurrentItem(mDay - 1, isAnimated);
	}

	/**
	 * 获取年份
	 * @return
	 */
	public int getYear()
	{
		return mYear;
	}

	/**
	 * 获取月份
	 * @return
	 */
	public int getMonth()
	{
		return mMonth;
	}

	/**
	 * 获取天
	 * @return
	 */
	public int getDayOfMonth()
	{
		return mDay;
	}

	/**
	 * 设置年滚动视图字体大小
	 * @param textSize
	 */
	public void setYearTextSize(int textSize)
	{
		((NumericWheelAdapter)mYearWheel.getViewAdapter()).setTextSize(textSize);
	}

	/**
	 * 设置月滚动视图字体大小
	 * @param textSize
	 */
	public void setMonthTextSize(int textSize)
	{
		((NumericWheelAdapter)mMonthWheel.getViewAdapter()).setTextSize(textSize);
	}

	/**
	 * 设置天滚动视图字体大小
	 * @param textSize
	 */
	public void setDayTextSize(int textSize)
	{
		((NumericWheelAdapter)mDayWheel.getViewAdapter()).setTextSize(textSize);
		this.dayTextSize = textSize;
	}

	/**
	 * 状态保存类
	 */
	private static class SavedState extends BaseSavedState
	{
		private final int mYear;
		private final int mMonth;
		private final int mDay;

		/**
		 * Constructor called from {@link DatePicker#onSaveInstanceState()}
		 */
		private SavedState(Parcelable superState, int year, int month, int day)
		{
			super(superState);
			mYear = year;
			mMonth = month;
			mDay = day;
		}

		/**
		 * Constructor called from {@link #CREATOR}
		 */
		private SavedState(Parcel in)
		{
			super(in);
			mYear = in.readInt();
			mMonth = in.readInt();
			mDay = in.readInt();
		}

		public int getYear()
		{
			return mYear;
		}

		public int getMonth()
		{
			return mMonth;
		}

		public int getDay()
		{
			return mDay;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags)
		{
			super.writeToParcel(dest, flags);
			dest.writeInt(mYear);
			dest.writeInt(mMonth);
			dest.writeInt(mDay);
		}

		@SuppressWarnings("unused")
		public static final Creator<SavedState> CREATOR = new Creator<SavedState>()
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

		return new SavedState(superState, mYear, mMonth, mDay);
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		mYear = ss.getYear();
		mMonth = ss.getMonth();
		mDay = ss.getDay();
		updateSpinners();
	}

	private void notifyDateChanged()
	{
		if (mOnDateChangedListener != null)
		{
			mOnDateChangedListener.onDateChanged(this, mYear, mMonth, mDay);
		}
	}

	/**
	 * 该类作用及功能说明 Adapter for numeric wheels. Highlights the current value.
	 * 
	 * @version V2.0
	 */
	private class DateNumericAdapter extends NumericWheelAdapter
	{
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;

		/**
		 * Constructor
		 */
		public DateNumericAdapter(Context context, int minValue, int maxValue, int current)
		{
			super(context, minValue, maxValue);
			this.currentValue = current;
		}

		@Override
		protected void configureTextView(TextView view)
		{
			super.configureTextView(view);
			if (currentItem == currentValue)
			{
				// view.setTextColor(0xFF0000F0);
			}
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
