package com.mn.tiger.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.mn.tiger.utility.CR;

/**
 * 该类作用及功能说明:通讯录索引条
 */
public class TGLetterView extends View
{
	private WindowManager manager;
	/**
	 * LetterView中字体的颜色
	 */
	private int textColor = 0;
	/**
	 * 选中的LetterView的字体颜色
	 */
	private int selectTextColor = 0;
	/**
	 * LetterView的背景颜色
	 */
	private int backgroundColor = 0;
	/**
	 * 当前触摸的位置
	 */
	private int position = -1;
	/**
	 * 索引条中字体的大小
	 */
	private int textSize = 16;
	/**
	 * 要显示的字体集合
	 */
	private String[] strings = null;
	/**
	 * 用于返回用户当前触摸的是哪个字母的监听
	 */
	private LetterListener letterListener;
	/**
	 * onDraw中绘制字母的画笔
	 */
	private Paint paint;
	/**
	 * 显示在屏幕中央的TextView
	 */
	private TextView overLay;
	/**
	 * 点击letterview时 是否显示背景色
	 */
	private boolean isShowBackground = false;
	/**
	 * Overlay的字体样式
	 */
	private Typeface typeface = null;

	public TGLetterView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initOverlay();
	}

	public TGLetterView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initOverlay();
	}

	public TGLetterView(Context context)
	{
		super(context);
		initOverlay();
	}

	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		initPaint();
		// 判断是否绘制背景
		if (backgroundColor != 0 && isShowBackground)
		{
			canvas.drawColor(backgroundColor);
		}
		if (strings != null && strings.length > 0)
		{
			int averageHeight = getAverageHeight();
			int width = getLetterViewWidth();
			for (int i = 0; i < strings.length; i++)
			{
				showTextColor(i);
				// 计算字母所占的宽度
				float textWidth = paint.measureText(strings[i]);
				// 计算绘制字母的高度
				float height = averageHeight * (i + 1);
				// 绘制字母
				canvas.drawText(strings[i], (width - textWidth) / 2, height, paint);
			}

		}
	}

	/**
	 * 该方法的作用:初始化画笔
	 */
	private void initPaint()
	{
		if (paint == null)
		{
			paint = new Paint();
			paint.setAntiAlias(true);
		}
		if (textSize > 0)
		{
			paint.setTextSize(textSize);
		}
		if (typeface != null)
		{
			paint.setTypeface(typeface);
		}
	}

	/**
	 * 该方法的作用:设置LetterView中字体的样式
	 * 
	 * @param typeface
	 */
	public void setTextType(Typeface typeface)
	{
		this.typeface = typeface;
	}

	/**
	 * 该方法的作用:设置背景颜色
	 * 
	 * @param backgroundColor
	 */
	public void setLetterViewBackgroundColor(int backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}

	/**
	 * 该方法的作用:设置要显示的字体颜色
	 * @param currentPosition
	 */

	private void showTextColor(int currentPosition)
	{
		if (textColor != 0)
		{
			paint.setColor(textColor);
		}
		else
		{
			paint.setColor(Color.WHITE);
		}
		/**
		 * 设置当前选中内容的颜色
		 */
		if (selectTextColor != 0 && position == currentPosition)
		{
			paint.setColor(selectTextColor);
		}

	}

	/**
	 * 该方法的作用:设置选中item字体颜色的变化
	 * 
	 * @param selectTextColor
	 */
	public void setSelectTextColor(int selectTextColor)
	{
		this.selectTextColor = selectTextColor;
	}

	/**
	 * 该方法的作用:计算平均每个item所占的位置高度
	 * 
	 * @return
	 */
	private int getAverageHeight()
	{
		return getLetterViewHeight() / strings.length;

	}

	/**
	 * 该方法的作用:设置LetterView中每个item的字体大小
	 * 
	 * @param size
	 */
	public void setTextSize(int size)
	{
		this.textSize = size;
	}

	/**
	 * 该方法的作用:获取letterview自身的高度
	 * @return
	 */
	private int getLetterViewHeight()
	{
		return getMeasuredHeight();
	}

	/**
	 * 该方法的作用:获取letterview自身的宽度
	 * 
	 * @return
	 */
	private int getLetterViewWidth()
	{
		return getMeasuredWidth();
	}

	/**
	 * 该方法的作用:设置item的默认颜色
	 * 
	 * @param textColor
	 */
	public void setTextColor(int textColor)
	{
		this.textColor = textColor;
	}

	/**
	 * 该方法的作用:设置要显示的内容集合
	 * 
	 * @param strings
	 */
	public void setTextContent(String[] strings)
	{
		this.strings = strings;
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getAction();
		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
				overLay.setVisibility(View.VISIBLE);
				isShowBackground = true;
				computeTouchPosition(event);
				// 重绘
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				// 当前触摸的位置
				int ey = (int) event.getY();
				// 判断传入的字符集合是否有内容，且触摸位置是否在LetterView的高度范围
				if ((strings != null && strings.length > 0)
						&& (ey >= getAverageHeight() * strings.length || event.getY() < 0))
				{
					position = -1;
					invalidate();
				}
				else
				{
					computeTouchPosition(event);
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				overLay.setVisibility(View.INVISIBLE);
				isShowBackground = false;
				// 修改当前位置
				position = -1;
				invalidate();
				break;
			default:
				break;
		}
		return true;
	}

	/**
	 * 该方法的作用:计算单前触摸的位置
	 * 
	 * @date 2014-2-26
	 * @param event
	 */
	private void computeTouchPosition(MotionEvent event)
	{
		if (strings != null && strings.length > 0)
		{
			// 计算当前触摸的是第几个字母
			int currentPosition = (int) Math.floor(event.getY() / getAverageHeight());
			// 如果当前字母和上次的字母不是同一个，则修改overlay的内容并通过listener向用户返回该字符
			if (position != currentPosition)
			{
				position = currentPosition;
				//数组越界修改 by hulimin
				if(position < strings.length){
					overLay.setText(strings[position]);
					letterListener.onTouchPosition(strings[position]);
				}
			}

		}
	}

	/**
	 * 该方法的作用:初始化中间显示的TextView视图
	 * 
	 * @date 2014-3-12
	 */
	private void initOverlay()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		// 获取overlay的视图
		overLay = (TextView) inflater.inflate(CR.getLayoutId(getContext(), "tiger_overlay_layout"),
				null);
		overLay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSPARENT);
		manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		// 将overlay加入屏幕中
		manager.addView(overLay, params);
	}

	public void removeOverLay()
	{
		if (manager != null && overLay != null)
		{
			manager.removeView(overLay);
		}
	}

	/**
	 * 该方法的作用:修改中间显示的TextView的字體颜色
	 * 
	 * @date 2014-3-12
	 * @param color
	 */
	public void setOverlayTextColor(int color)
	{
		overLay.setTextColor(color);
	}

	public void setOnLetterListener(LetterListener listener)
	{
		this.letterListener = listener;
	}

	public interface LetterListener
	{
		/**
		 * 该方法的作用:向用户返回当前触摸的字符
		 * 
		 * @date 2014-3-13
		 * @param string
		 */
		public void onTouchPosition(String string);

	}
}
