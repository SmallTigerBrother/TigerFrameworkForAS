package com.mn.tiger.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.mn.tiger.utility.CR;
import com.mn.tiger.widget.wheelview.DateWheel;
import com.mn.tiger.widget.wheelview.DateWheel.OnDateChangedListener;

import java.util.Calendar;

/**
 * 该类作用及功能说明
 * 
 * @version V2.0
 * @see JDK1.6,android-8
 */

public class TGDateWheelDialog extends TGDialog
{
	private Context mContext;
	/** start and end year */
	private int mStartYear;
	private int mEndYear;

	private final String YEAR = "year";
	private final String MONTH = "month";
	private final String DAY = "day";

	/** 日历对象 */
	private Calendar mCalendar;
	private final java.text.DateFormat mTitleDateFormat;
	/** 布局解析对象 */
	private LayoutInflater mInflater;

	private DateWheel mDateWheel;
	private final OnDateSetListener mCallBack;

	private int mInitialYear;
	private int mInitialMonth;
	private int mInitialDay;

	/** 标题栏显示日期文本控件 */
	private TextView mDateTextView;
	/** 自定义的标题视图 */
	private View custormTitleView;

	/** body根布局 */
	private LinearLayout bodyRootLayout;
	/**
	 * The callback used to indicate the user is done filling in the date.
	 */
	public interface OnDateSetListener
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
		void onDateSet(DateWheel view, int year, int monthOfYear, int dayOfMonth);
	}

	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param callBack
	 *            How the parent is notified that the date is set.
	 * @param year
	 *            The initial year of the dialog.
	 * @param monthOfYear
	 *            The initial month of the dialog.
	 * @param dayOfMonth
	 *            The initial day of the dialog.
	 */
	public TGDateWheelDialog(Context context, OnDateSetListener callBack, int year,
							 int monthOfYear, int dayOfMonth)
	{
		this(context, null, callBack, year, monthOfYear,
				dayOfMonth);
	}

	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param callBack
	 *            How the parent is notified that the date is set.
	 * @param year
	 *            The initial year of the dialog.
	 * @param monthOfYear
	 *            The initial month of the dialog.
	 * @param dayOfMonth
	 *            The initial day of the dialog.
	 */
	public TGDateWheelDialog(Context context, TGDialogParams params, OnDateSetListener callBack, int year,
							 int monthOfYear, int dayOfMonth)
	{
		super(context, params);
		mContext = context;
		mCallBack = callBack;
		mInitialYear = year;
		mInitialMonth = monthOfYear;
		mInitialDay = dayOfMonth;
		mCalendar = Calendar.getInstance();
		/** 根据当前应用的语言，格式化对应的时间 */
		Configuration config = context.getResources().getConfiguration();
		mTitleDateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL,
				config.locale);
		setupDialog();
	}

	/**
	 *
	 * 该方法的作用:设置起始年份 参数: 返回值:无 异常: 在什么情况下调用:
	 *
	 * @date 2013-2-19
	 * @param startYear
	 * @param endYear
	 */
	public void setYearRange(int startYear, int endYear)
	{
		if (startYear > 0 && endYear >= startYear)
		{
			mStartYear = startYear;
			mEndYear = endYear;
			mDateWheel.setYearRange(mStartYear, mEndYear);
		}
	}

	/**
	 *
	 * 该方法的作用:添加弹出框内容视图
	 *
	 * @date 2013-3-5
	 */
	private void addBodyContentView()
	{
		mDateWheel = new DateWheel(this.mContext);
		mDateWheel.init(mInitialYear, mInitialMonth, mInitialDay, dateChangeListener);
		LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		bodyParams.gravity = Gravity.CENTER;
		mDateWheel.setPadding(dip2px(mContext, 12), dip2px(mContext, 12), dip2px(mContext, 12),
				dip2px(mContext, 12));

		/** 创建一个填充满父布局的子布局，解决设置背景颜色的问题 */
		bodyRootLayout = new LinearLayout(mContext);
		bodyRootLayout.setBackgroundColor(Color.TRANSPARENT);
		bodyRootLayout.addView(mDateWheel, bodyParams);
		this.setBodyContentView(bodyRootLayout, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	/**
	 *
	 * 该方法的作用:初始化提示框 参数: 返回值: 异常: 在什么情况下调用:
	 *
	 * @date 2013-2-19
	 */
	protected void setupDialog()
	{
		this.addBodyContentView();
		this.setRightButton(this.mContext.getString(CR.getStringId(mContext, "tiger_setting")),
				new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if (mCallBack != null)
						{
							mCallBack.onDateSet(mDateWheel, mDateWheel.getYear(),
									mDateWheel.getMonth(), mDateWheel.getDayOfMonth());
							dialog.dismiss();
						}
					}
				});
		this.setLeftButton(
				this.mContext.getString(CR.getStringId(mContext, "tiger_alert_dialog_cancel")),
				new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				});
		updateTitleText(mInitialYear, mInitialMonth, mInitialDay);
	}

	/**
	 * 日期改变的监听对象
	 */
	private OnDateChangedListener dateChangeListener = new OnDateChangedListener()
	{
		@Override
		public void onDateChanged(DateWheel view, int year, int monthOfYear, int dayOfMonth)
		{
			updateTitleText(year, monthOfYear, dayOfMonth);
		}
	};

	/**
	 *
	 * 该方法的作用:更新日期 参数: 返回值: 异常: 在什么情况下调用:
	 *
	 * @date 2013-2-19
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 */
	public void updateDate(int year, int monthOfYear, int dayOfMonth)
	{
		mInitialYear = year;
		mInitialMonth = monthOfYear;
		mInitialDay = dayOfMonth;
		mDateWheel.updateDate(year, monthOfYear, dayOfMonth);
	}

	/**
	 *
	 * 该方法的作用:设置标题视图 参数: 返回值: 异常: 在什么情况下调用:
	 *
	 * @date 2013-2-19
	 * @param view
	 */
	public void setTitleView(View view)
	{
		if (view != null)
		{
			this.custormTitleView = view;
			this.mDateTextView = null;
			this.addTitleView(this.custormTitleView);
		}
	}

	/**
	 *
	 * 该方法的作用:创建标题视图 参数: 返回值: 异常: 在什么情况下调用:
	 *
	 * @date 2013-2-2
	 * @return
	 */
	private void createTitleView()
	{
		if (mInflater == null)
		{
			mInflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		LinearLayout title_layout = (LinearLayout) mInflater.inflate(
				CR.getLayoutId(mContext, "tiger_date_dialog_title"), null);
		this.mDateTextView = (TextView) title_layout.findViewById(CR.getViewId(mContext,
				"tiger_date_selected_textview"));
		this.mDateTextView.setSingleLine();
		this.mDateTextView.setEllipsize(TruncateAt.END);
		this.addTitleView(title_layout);
	}

	/**
	 *
	 * 该方法的作用:添加标题视图到提示框中 参数: 返回值: 异常: 在什么情况下调用:
	 *
	 * @date 2013-2-21
	 * @param view
	 */
	private void addTitleView(View view)
	{
		if (view != null)
		{
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.gravity = Gravity.CENTER;
			this.setTitleContentView(view, params);
			this.updateTitleText(mInitialYear, mInitialMonth, mInitialDay);
		}
	}

	/**
	 * 
	 * 该方法的作用:设置标题文本 参数: 返回值: 异常: 在什么情况下调用:
	 * 
	 * @date 2013-2-2
	 */
	private void updateTitleText(int mYear, int mMonth, int mDay)
	{
		if (custormTitleView == null && this.mDateTextView == null)
		{
			this.createTitleView();
		}
		mCalendar.set(Calendar.YEAR, mYear);
		mCalendar.set(Calendar.MONTH, mMonth);
		mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
		String dateStr = mTitleDateFormat.format(mCalendar.getTime());
		if (mDateTextView != null)
		{
			mDateTextView.setText(dateStr);
		}
		else if (this.custormTitleView != null && this.custormTitleView instanceof ViewGroup)
		{
			ViewGroup viewGroup = (ViewGroup) this.custormTitleView;
			for (int i = 0; i < viewGroup.getChildCount(); i++)
			{
				View child = viewGroup.getChildAt(i);
				if (child instanceof TextView)
				{
					((TextView) child).setText(dateStr);
					break;
				}
			}
		}
	}

	@Override
	public Bundle onSaveInstanceState()
	{
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDateWheel.getYear());
		state.putInt(MONTH, mDateWheel.getMonth());
		state.putInt(DAY, mDateWheel.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		int year = savedInstanceState.getInt(YEAR);
		int month = savedInstanceState.getInt(MONTH);
		int day = savedInstanceState.getInt(DAY);
		mDateWheel.init(year, month, day, dateChangeListener);
		updateTitleText(year, month, day);
	}

	/**
	 * 
	 * 该方法的作用:dp转px，sp转px
	 * 
	 * @date 2013-3-8
	 * @param context
	 * @param dipValue
	 * @return
	 */
	private int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
