package com.mn.tiger.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TabWidget;
import android.widget.TextView;

/**
 * A simple text label view that can be applied as a "badge" to any given
 * {@link android.view.View}. This class is intended to be instantiated at
 * runtime rather than included in XML layouts.
 * 
 */
public class TGBadgeView extends TGImageButton
{
	/** 显示在左上角 */
	public static final int POSITION_TOP_LEFT = 1;

	/** 显示在右上角 */
	public static final int POSITION_TOP_RIGHT = 2;

	/** 显示在左下角 */
	public static final int POSITION_BOTTOM_LEFT = 3;

	/** 显示在右下角 */
	public static final int POSITION_BOTTOM_RIGHT = 4;

	/** 默认padding值 */
	private final int DEFAULT_LR_PADDING_DIP = 0;

	/** 圆角 */
	private final int DEFAULT_CORNER_RADIUS_DIP = 8;

	/** 默认显示位置(右上角) */
	private final int DEFAULT_POSITION = POSITION_TOP_RIGHT;

	/** 默认背景颜色(红色) */
	private final int DEFAULT_BADGE_COLOR = Color.RED;

	/** 默认文字颜色(白色) */
	private final int DEFAULT_TEXT_COLOR = Color.WHITE;

	private Animation fadeIn;
	private Animation fadeOut;

	private Context context;
	private View target;

	private int badgePosition;
	private int badgeColor;

	/** margin 值 */
	private int badgeLeftMargin = 0;
	private int badgeRightMargin = 0;
	private int badgeTopMargin = 0;
	private int badgeBottomMargin = 0;

	private boolean isShown;

	private ShapeDrawable badgeBg;

	private int targetTabIndex;

	public TGBadgeView(Context context)
	{
		this(context, (AttributeSet) null, android.R.attr.textViewStyle);
	}

	public TGBadgeView(Context context, AttributeSet attrs)
	{
		this(context, attrs, android.R.attr.textViewStyle);
	}

	/**
	 * Constructor -
	 * 
	 * create a new BadgeView instance attached to a target
	 * {@link android.view.View}.
	 * 
	 * @param context
	 *            context for this view.
	 * @param target
	 *            the View to attach the badge to.
	 */
	public TGBadgeView(Context context, View target)
	{
		this(context, null, android.R.attr.textViewStyle, target, 0);
	}

	/**
	 * Constructor -
	 * 
	 * create a new BadgeView instance attached to a target
	 * {@link android.widget.TabWidget} tab at a given index.
	 * 
	 * @param context
	 *            context for this view.
	 * @param target
	 *            the TabWidget to attach the badge to.
	 * @param index
	 *            the position of the tab within the target.
	 */
	public TGBadgeView(Context context, TabWidget target, int index)
	{
		this(context, null, android.R.attr.textViewStyle, target, index);
	}

	public TGBadgeView(Context context, AttributeSet attrs, int defStyle)
	{
		this(context, attrs, defStyle, null, 0);
	}

	public TGBadgeView(Context context, AttributeSet attrs, int defStyle, View target, int tabIndex)
	{
		super(context, attrs);
		init(context, target, tabIndex);
	}

	private void init(Context context, View target, int tabIndex)
	{

		this.context = context;
		this.target = target;
		this.targetTabIndex = tabIndex;

		// apply defaults
		badgePosition = DEFAULT_POSITION;
		badgeColor = DEFAULT_BADGE_COLOR;

		int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
		setPadding(paddingPixels, 0, paddingPixels, 0);

		fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator());
		fadeIn.setDuration(200);

		fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator());
		fadeOut.setDuration(200);

		isShown = false;

		if (this.target != null)
		{
			applyTo(this.target);
		}
		else
		{
			show();
		}

	}

	private void applyTo(View target)
	{
		LayoutParams lp = (LayoutParams) target.getLayoutParams();
		ViewParent parent = target.getParent();
		FrameLayout container = new FrameLayout(context);

		if (target instanceof TabWidget)
		{

			// set target to the relevant tab child container
			target = ((TabWidget) target).getChildTabViewAt(targetTabIndex);
			this.target = target;

			((ViewGroup) target).addView(container, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));

			this.setVisibility(View.GONE);
			container.addView(this);

		}
		else
		{
			// TODO verify that parent is indeed a ViewGroup
			ViewGroup group = (ViewGroup) parent;
			int index = group.indexOfChild(target);

			group.removeView(target);
			container.addView(target);

			this.setVisibility(View.GONE);
			container.addView(this);

			group.addView(container, index, lp);

			group.invalidate();
		}
	}

	/**
	 * 该方法的作用:设置徽标显示的文字
	 * 
	 * @date 2013-8-13
	 * @param text
	 */
	public void setText(String text)
	{
		super.setText(text);
		if (getContentView() instanceof TextView)
		{
			TextView textView = ((TextView) getContentView());
			textView.setTypeface(Typeface.DEFAULT_BOLD);
			textView.setTextColor(DEFAULT_TEXT_COLOR);
		}
	}

	/**
	 * Make the badge visible in the UI.
	 * 
	 */
	public void show()
	{
		show(false, null);
	}

	/**
	 * Make the badge visible in the UI.
	 * 
	 * @param animate
	 *            flag to apply the default fade-in animation.
	 */
	public void show(boolean animate)
	{
		show(animate, fadeIn);
	}

	/**
	 * Make the badge visible in the UI.
	 * 
	 * @param anim
	 *            Animation to apply to the view when made visible.
	 */
	public void show(Animation anim)
	{
		show(true, anim);
	}

	/**
	 * Make the badge non-visible in the UI.
	 * 
	 */
	public void hide()
	{
		hide(false, null);
	}

	/**
	 * Make the badge non-visible in the UI.
	 * 
	 * @param animate
	 *            flag to apply the default fade-out animation.
	 */
	public void hide(boolean animate)
	{
		hide(animate, fadeOut);
	}

	/**
	 * Make the badge non-visible in the UI.
	 * 
	 * @param anim
	 *            Animation to apply to the view when made non-visible.
	 */
	public void hide(Animation anim)
	{
		hide(true, anim);
	}

	/**
	 * Toggle the badge visibility in the UI.
	 * 
	 */
	public void toggle()
	{
		toggle(false, null, null);
	}

	/**
	 * Toggle the badge visibility in the UI.
	 * 
	 * @param animate
	 *            flag to apply the default fade-in/out animation.
	 */
	public void toggle(boolean animate)
	{
		toggle(animate, fadeIn, fadeOut);
	}

	/**
	 * Toggle the badge visibility in the UI.
	 * 
	 * @param animIn
	 *            Animation to apply to the view when made visible.
	 * @param animOut
	 *            Animation to apply to the view when made non-visible.
	 */
	public void toggle(Animation animIn, Animation animOut)
	{
		toggle(true, animIn, animOut);
	}

	@SuppressWarnings("deprecation")
	private void show(boolean animate, Animation anim)
	{
		if (getBackground() == null)
		{
			if (badgeBg == null)
			{
				badgeBg = getDefaultBackground();
			}
			setBackgroundDrawable(badgeBg);
		}
		if (animate)
		{
			this.startAnimation(anim);
		}
		this.setVisibility(View.VISIBLE);

		applyLayoutParams();
		isShown = true;
	}

	private void hide(boolean animate, Animation anim)
	{
		this.setVisibility(View.GONE);
		if (animate)
		{
			this.startAnimation(anim);
		}
		isShown = false;
	}

	private void toggle(boolean animate, Animation animIn, Animation animOut)
	{
		if (isShown)
		{
			hide(animate && (animOut != null), animOut);
		}
		else
		{
			show(animate && (animIn != null), animIn);
		}
	}

	private ShapeDrawable getDefaultBackground()
	{

		int r = dipToPixels(DEFAULT_CORNER_RADIUS_DIP);
		float[] outerR = new float[] { r, r, r, r, r, r, r, r };

		RoundRectShape rr = new RoundRectShape(outerR, null, null);
		ShapeDrawable drawable = new ShapeDrawable(rr);
		drawable.getPaint().setColor(badgeColor);

		return drawable;

	}

	private void applyLayoutParams()
	{
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		int badgeMidX = this.getMeasuredWidth() / 2;
		int badgeMidY = this.getMeasuredHeight() / 2;

		int defaultLeftMargin = 0;
		int defaultTopMargin = 0;
		int defaultRightMargin = 0;
		int defaultBottomMargin = 0;

		switch (badgePosition)
		{
			case POSITION_TOP_LEFT:
				lp.gravity = Gravity.LEFT | Gravity.TOP;
				defaultLeftMargin = -badgeMidX;
				defaultTopMargin = -badgeMidY;
				break;
			case POSITION_TOP_RIGHT:
				lp.gravity = Gravity.RIGHT | Gravity.TOP;
				defaultRightMargin = -badgeMidX;
				defaultTopMargin = -badgeMidY;
				break;
			case POSITION_BOTTOM_LEFT:
				lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
				defaultLeftMargin = -badgeMidX;
				defaultBottomMargin = -badgeMidY;
				break;
			case POSITION_BOTTOM_RIGHT:
				lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
				defaultRightMargin = -badgeMidX;
				defaultBottomMargin = -badgeMidY;
				break;
			default:
				break;
		}

		if (badgeLeftMargin == 0 && badgeTopMargin == 0 && badgeRightMargin == 0
				&& badgeBottomMargin == 0)
		{
			lp.setMargins(defaultLeftMargin, defaultTopMargin, defaultRightMargin,
					defaultBottomMargin);
		}
		else
		{
			lp.setMargins(badgeLeftMargin, badgeTopMargin, badgeRightMargin, badgeBottomMargin);
		}
		setLayoutParams(lp);

	}

	/**
	 * Returns the target View this badge has been attached to.
	 * 
	 */
	public View getTarget()
	{
		return target;
	}

	/**
	 * Is this badge currently visible in the UI?
	 * 
	 */
	@Override
	public boolean isShown()
	{
		return isShown;
	}

	/**
	 * Returns the positioning of this badge.
	 * 
	 * one of POSITION_TOP_LEFT, POSITION_TOP_RIGHT, POSITION_BOTTOM_LEFT,
	 * POSITION_BOTTOM_RIGHT.
	 * 
	 */
	public int getBadgePosition()
	{
		return badgePosition;
	}

	/**
	 * Set the positioning of this badge.
	 * 
	 * @param layoutPosition
	 *            one of POSITION_TOP_LEFT, POSITION_TOP_RIGHT,
	 *            POSITION_BOTTOM_LEFT, POSITION_BOTTOM_RIGHT.
	 * 
	 */
	public void setBadgePosition(int layoutPosition)
	{
		this.badgePosition = layoutPosition;
	}

	/**
	 * Returns the horizontal/vertical margin from the target View that is
	 * applied to this badge.
	 * 
	 */
	public int getBadgeLeftMargin()
	{
		return badgeLeftMargin;
	}

	public int getBadgeTopMargin()
	{
		return badgeTopMargin;
	}

	public int getBadgeRightMargin()
	{
		return badgeRightMargin;
	}

	public int getBadgeBottomMargin()
	{
		return badgeBottomMargin;
	}

	/**
	 * Set the horizontal/vertical margin from the target View that is applied
	 * to this badge.
	 * 
	 * @param left
	 *            the left margin in pixels.
	 * @param top
	 *            the top margin in pixels.
	 * @param right
	 *            the right margin in pixels.
	 * @param bottom
	 *            the bottom margin in pixels.
	 */
	public void setBadgeMargin(int left, int top, int right, int bottom)
	{
		this.badgeLeftMargin = left;
		this.badgeTopMargin = top;
		this.badgeRightMargin = right;
		this.badgeBottomMargin = bottom;
	}

	/**
	 * Returns the color value of the badge background.
	 * 
	 */
	public int getBadgeBackgroundColor()
	{
		return badgeColor;
	}

	/**
	 * Set the color value of the badge background.
	 * 
	 * @param badgeColor
	 *            the badge background color.
	 */
	public void setBadgeBackgroundColor(int badgeColor)
	{
		this.badgeColor = badgeColor;
		badgeBg = getDefaultBackground();
	}

	private int dipToPixels(int dip)
	{
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
				r.getDisplayMetrics());
		return (int) px;
	}
}