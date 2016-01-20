package com.mn.tiger.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class TGImageButton extends LinearLayout
{
	private final int TEXT_VIEW = 1;
	private final int IMAGE_VIEW = 2;
	private Context mContext;

	private ImageView imageView;
	private TextView textView;

	/** 徽章视图 */
	private TGBadgeView badgeView;

	private float textSize = 18f;
	private int textColor = Color.BLACK;

	public TGImageButton(Context context)
	{
		this(context, null);
	}

	public TGImageButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		initView();
	}

	private void initView()
	{
		this.setFocusable(true);
		this.setClickable(true);
		LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		this.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
		this.setLayoutParams(params);
	}

	/**
	 * 
	 * 该方法的作用:获取内容视图
	 * 
	 * @date 2013-4-11
	 * @param type
	 *            根据指定的类型，获取对应类型的视图
	 * @return
	 */
	private View getContentView(int type)
	{
		if (type == IMAGE_VIEW)
		{
			if (imageView == null)
			{
				imageView = new ImageView(mContext);
				imageView.setClickable(false);
				imageView.setFocusable(false);
				LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				if (this.getChildCount() > 0)
				{
					this.removeAllViews();
					textView = null;
				}
				this.addView(imageView, params);
			}
			return imageView;
		}
		else if (type == TEXT_VIEW)
		{
			if (textView == null)
			{
				textView = new TextView(mContext);
				textView.setClickable(false);
				textView.setFocusable(false);
				textView.setSingleLine();
				textView.setEllipsize(TruncateAt.END);
				LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				if (this.getChildCount() > 0)
				{
					this.removeAllViews();
					imageView = null;
				}
				this.addView(textView, params);
			}
			return textView;
		}
		return null;
	}

	public void setImageDrawable(Drawable drawable)
	{
		View view = getContentView(IMAGE_VIEW);
		if (view != null && view instanceof ImageView)
		{
			((ImageView) view).setImageDrawable(drawable);
		}
	}

	public void setImageResource(int resId)
	{
		View view = getContentView(IMAGE_VIEW);
		if (view != null && view instanceof ImageView)
		{
			((ImageView) view).setImageResource(resId);
		}
	}

	public void setImageBitmap(Bitmap bm)
	{
		View view = getContentView(IMAGE_VIEW);
		if (view != null && view instanceof ImageView)
		{
			((ImageView) view).setImageBitmap(bm);
		}
	}

	public void setText(CharSequence text)
	{
		setText(text, null);
	}

	public void setText(CharSequence text, BufferType type)
	{
		View view = getContentView(TEXT_VIEW);
		if (view != null && view instanceof TextView)
		{
			((TextView) view).setText(text, type);
			((TextView) view).setTextSize(textSize);
			((TextView) view).setTextColor(textColor);
		}
	}

	public void setText(int resId)
	{
		setText(getContext().getString(resId));
	}

	public void setTextColor(int color)
	{
		textColor = color;
		/**
		 * 当前显示的是文本时，则设置字体颜色， 否则缓存到setText的时候设置
		 */
		if (this.getChildCount() == 1)
		{
			View view = this.getChildAt(getChildCount() - 1);
			if (view != null && view instanceof TextView)
			{
				((TextView) view).setTextColor(color);
			}
		}
	}

	/**
	 * 该方法的作用:设置文本大小 注意：设置px的值即可，此方法转换成sp值再设置
	 * 
	 * @date 2013-4-12
	 * @param size
	 */
	public void setTextSize(float size)
	{
		textSize = size;
		/**
		 * 当前显示的是文本时，则设置字体大小， 否则缓存到setText的时候设置
		 */
		if (this.getChildCount() == 1)
		{
			View view = this.getChildAt(getChildCount() - 1);
			if (view != null && view instanceof TextView)
			{
				((TextView) view).setTextSize(textSize);
			}
		}
	}

	/**
	 * 该方法的作用:设置内容视图与MPImageButton的间距
	 * @date 2013-4-12
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setViewPadding(int left, int top, int right, int bottom)
	{
		if (this.getChildCount() == 1)
		{
			View view = this.getChildAt(getChildCount() - 1);
			if (view != null)
			{
				view.setPadding(left, top, right, bottom);
				invalidate();
			}
		}
	}

	/**
	 * 该方法的作用:
	 * @date 2013-8-13
	 */
	private void initBadgeView()
	{
		if (getContentView() != null)
		{
			badgeView = new TGBadgeView(mContext, getContentView());
			badgeView.setFocusable(false);
			badgeView.setClickable(false);
		}
	}

	/**
	 * 该方法的作用:设置徽章图片背景资源
	 * @date 2013-8-13
	 * @param resid
	 */
	public void setBadgeBackgroudResource(int resid)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setBackgroundResource(resid);
	}

	/**
	 * 该方法的作用:设置徽章视图背景图片
	 * 
	 * @date 2013-8-13
	 * @param drawable
	 */
	@SuppressWarnings("deprecation")
	public void setBadgeBackgroudDrawable(Drawable drawable)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setBackgroundDrawable(drawable);
	}

	/**
	 * 该方法的作用:设置徽章视图背景颜色
	 * 
	 * @date 2013-8-13
	 * @param color
	 */
	public void setBadgeBackgroudColor(int color)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setBackgroundColor(color);
	}

	/**
	 * 该方法的作用:
	 * 
	 * @date 2013-8-13
	 * @param drawable
	 */
	public void setBadgeDrawable(Drawable drawable)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setImageDrawable(drawable);
	}

	/**
	 * 该方法的作用:
	 * 
	 * @date 2013-8-13
	 * @param resId
	 */
	public void setBadgeResource(int resId)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setImageResource(resId);
	}

	/**
	 * 该方法的作用:
	 * 
	 * @date 2013-8-13
	 * @param bm
	 */
	public void setBadgeBitmap(Bitmap bm)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setImageBitmap(bm);
	}

	/**
	 * 该方法的作用:设置徽章视图文本
	 * 
	 * @date 2013-8-13
	 * @param text
	 */
	public void setBadgeText(String text)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setText(text);
	}

	/**
	 * 该方法的作用:设置徽标文本颜色
	 * 
	 * @date 2013-8-13
	 * @param color
	 */
	public void setBadgeTextColor(int color)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setTextColor(color);
	}

	/**
	 * 该方法的作用:
	 * 
	 * @date 2013-8-13
	 * @param size
	 */
	public void setBadgeTextSize(float size)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setTextSize(size);
	}

	/**
	 * 该方法的作用:设置显示位置
	 * 
	 * @date 2013-8-13
	 * @param layoutPosition
	 */
	public void setBadgePosition(int layoutPosition)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setBadgePosition(layoutPosition);
	}

	/**
	 * 该方法的作用:设置徽章的margin值
	 * 
	 * @date 2013-8-13
	 * @param badgeMargin
	 */
	public void setBadgeMargin(int left, int top, int right, int bottom)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setBadgeMargin(left, top, right, bottom);
	}

	/**
	 * 该方法的作用:设置padding值
	 * 
	 * @date 2013-8-13
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setBadgePadding(int left, int top, int right, int bottom)
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.setPadding(left, top, right, bottom);
	}

	/**
	 * 显示徽标
	 */
	public void showBadgeView()
	{
		if (badgeView == null)
		{
			initBadgeView();
		}
		badgeView.show();
	}
	
	/**
	 * 隐藏徽标
	 */
	public void hideBadgeView()
	{
		if (badgeView != null)
		{
			badgeView.hide();
		}
	}

	/**
	 * 该方法的作用:获取内容视图(文本内容或者图片内容)
	 * 
	 * @date 2013-4-12
	 * @return
	 */
	public View getContentView()
	{
		if (textView != null)
		{
			return textView;
		}
		else if (imageView != null)
		{
			return imageView;
		}
		return null;
	}

}
