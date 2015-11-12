package com.mn.tiger.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mn.tiger.utility.CR;
import com.mn.tiger.utility.DisplayUtils;

/**
 * 对话框扩展类
 */
public class TGDialog extends Dialog implements IDialog
{
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();
	
	/**
	 * 根视图
	 */
	private LinearLayout rootView;
	
	/*********************Title*****************************/
	/**
	 * 标题栏Layout
	 */
	private LinearLayout titleLayout;
	
	/**
	 * 标题栏文本视图
	 */
	private TextView titleTextView;
	
	/**
	 * 标题栏是否被自定义
	 */
	private boolean isTitleCustom = false;
	
	
	/***********************Body***************************/
	
	/**
	 * Body区域layout
	 */
	private LinearLayout bodyLayout;
	
	/**
	 * Body区域文本视图
	 */
	private TextView bodyTextView;
	
	/**
	 * Body区域是否被自定义
	 */
	private boolean isBodyCustom = false;
	
	/***********************Bottom***************************/
	
	/**
	 * 底部栏Layout
	 */
	private RelativeLayout bottomLayout;
	
	/**
	 * 底部栏是否被自定义
	 */
	private boolean isBottomCustom = false;
	
	/**
	 * 底部栏左按钮
	 */
	private Button leftButton;
	
	/**
	 * 底部栏左按钮是否显示
	 */
	private boolean showLeftButton = false;
	
	/**
	 * 底部栏中按钮
	 */
	private Button middleButton;
	
	/**
	 * 底部栏中按钮是否显示
	 */
	private boolean showMiddleButton = false;
	
	/**
	 * 底部栏右按钮
	 */
	private Button rightButton;
	
	/**
	 * 底部栏右按钮是否显示
	 */
	private boolean showRightButton = false;
	
	/**
	 * 对话框显示参数
	 */
	private TGDialogParams dialogParams = null;
	
	public TGDialog(Context context)
	{
		super(context, CR.getStyleId(context, "tiger_baseDialog"));
		dialogParams = new TGDialogParams(getContext());
		setupDialog();
	}
	
	public TGDialog(Context context, TGDialogParams dialogParams)
	{
		super(context, dialogParams.getDialogTheme());
		this.dialogParams = dialogParams;
		setupDialog();
	}
	
	/**
	 * 初始化对话框
	 */
	@SuppressWarnings("deprecation")
	private void setupDialog()
	{
		//设置不现实标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//初始化主视图
		rootView = new LinearLayout(getContext());
		rootView.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				dialogParams.getDialogWidth(), dialogParams.getDialogHeight());
		params.gravity = Gravity.CENTER_HORIZONTAL;
		
		Drawable backgroundRes = dialogParams.getBackgroundResource();
		if(null != backgroundRes)
		{
			rootView.setBackgroundDrawable(backgroundRes);
		}
		
		setContentView(rootView, params);
		
		//初始化Title
		setupTitleView();
		
		//初始化Body
		setupBodyView();
		
		//初始化Bottom
		setupBottomView();
		
	}
	
	/**
	 * 初始化标题栏
	 * @date 2014年2月10日
	 */
	protected void setupTitleView()
	{
		//初始化标题栏Layout
		titleLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		titleLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
		
		//初始化标题栏Textview
		titleTextView = new TextView(getContext());
		titleTextView.setTextSize(dialogParams.getTitleTextSize());
		titleTextView.setTextColor(dialogParams.getTitleTextColor());
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		titleTextView.setGravity(Gravity.CENTER);
		textParams.leftMargin = DisplayUtils.dip2px(getContext(), 16);
		textParams.rightMargin = DisplayUtils.dip2px(getContext(), 16);
		textParams.topMargin = DisplayUtils.dip2px(getContext(), 8);
		
		titleLayout.addView(titleTextView, textParams);
		rootView.addView(titleLayout, layoutParams);
	}
	
	/**
	 * 初始化Body区域
	 * @date 2014年2月10日
	 */
	protected void setupBodyView()
	{
		//初始化中间显示区Layout
		bodyLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		bodyLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
		
		//初始化中间显示区Textview
		bodyTextView = new TextView(getContext());
		bodyTextView.setTextSize(dialogParams.getBodyTextSize());
		bodyTextView.setTextColor(dialogParams.getBodyTextColor());
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
	    bodyTextView.setGravity(Gravity.CENTER);
		
		textParams.leftMargin = DisplayUtils.dip2px(getContext(), 8);
		textParams.rightMargin = DisplayUtils.dip2px(getContext(), 8);
		textParams.topMargin = DisplayUtils.dip2px(getContext(), 8);
		textParams.bottomMargin = DisplayUtils.dip2px(getContext(), 8);
		
		bodyLayout.addView(bodyTextView, textParams);
		rootView.addView(bodyLayout, layoutParams);
	}
	
	/**
	 * 初始化底部操作栏
	 * @date 2014年2月10日
	 */
	protected void setupBottomView()
	{
		//初始化中间显示区Layout
		bottomLayout = new RelativeLayout(getContext());
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		rootView.addView(bottomLayout, layoutParams);
	}
	
	/**
	 * 设置左按钮
	 * @param text 按钮文本
	 * @param listener 点击事件监听器
	 */
	public void setLeftButton(CharSequence text, final OnClickListener listener)
	{
		if(isBottomCustom || null == text)
		{
			return;
		}
		
		if(!showLeftButton)
		{
			//初始化LeftButton
			showLeftButton = true;
			leftButton = initLeftButton();
			bottomLayout.addView(leftButton);
		}
		
		//设置显示参数，点击事件
		leftButton.setText(text);
		leftButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(null != listener)
				{
					listener.onClick(TGDialog.this, IDialog.BUTTON_LEFT);
				}
			}
		});
		
		//设置左按钮的位置
		setLeftButtonPosition();
		bottomLayout.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 初始化左按钮
	 * @date 2014年1月6日
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected Button initLeftButton()
	{
		Button leftButton = new Button(getContext());
		leftButton.setTextSize(dialogParams.getLeftButtonTextSize());
		leftButton.setTextColor(dialogParams.getLeftButtonTextColor());
		
		Drawable backgroundRes = dialogParams.getLeftButtonBackgroundRes();
		if(null != backgroundRes)
		{
			leftButton.setBackgroundDrawable(dialogParams.getLeftButtonBackgroundRes());
		}
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		leftButton.setLayoutParams(layoutParams);
		
		leftButton.setPadding(0, DisplayUtils.dip2px(getContext(), 8), 0,  
				DisplayUtils.dip2px(getContext(), 8));
		
		return leftButton;
	}
	
	/**
	 * 设置左按钮位置
	 * @date 2014年2月10日
	 */
	protected void setLeftButtonPosition()
	{
		if(!showLeftButton)
		{
			return;
		}
		
		RelativeLayout.LayoutParams leftParams = (RelativeLayout.LayoutParams) leftButton.getLayoutParams();
		//若存在中间按钮，则按照三个按钮计算大小，否则按两个按钮计算大小
		if(showMiddleButton)
		{
			//计算左按钮的位置
			leftParams.width = dialogParams.getButtonWidthOfThree();
			leftParams.leftMargin = (int)((dialogParams.getDialogWidth() - dialogParams.getButtonWidthOfThree() * 3) /6);
			//更新中间按钮的宽度
			
			onUpdateMiddleButtonWidth(middleButton, dialogParams.getButtonWidthOfThree());
		}
		else 
		{
			//计算左按钮的位置
			leftParams.width = dialogParams.getButtonWidthOfDouble();
			leftParams.leftMargin = (int)((dialogParams.getDialogWidth() - dialogParams.getButtonWidthOfDouble() * 2) / 4);
		}
		
		leftButton.setLayoutParams(leftParams);
	}
	
	/**
	 * 设置中按钮
	 * @param text 按钮文本
	 * @param listener 点击事件监听器
	 */
	public void setMiddleButton(CharSequence text, final OnClickListener listener)
	{
		if(isBottomCustom || null == text)
		{
			return;
		}
		
		if(!showMiddleButton)
		{
			//初始化MiddleButton
			showMiddleButton = true;
			middleButton = initMiddleButton();
			bottomLayout.addView(middleButton);
		}
		
		//设置显示参数，点击事件
		middleButton.setText(text);
		middleButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(null != listener)
				{
					listener.onClick(TGDialog.this, IDialog.BUTTON_MIDDLE);
				}
			}
		});
		
		//设置按钮显示位置
		setMiddleButtonPosition();
		bottomLayout.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 初始化中间按钮
	 * @date 2014年1月6日
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected Button initMiddleButton()
	{
		Button middleButton = new Button(getContext());
		middleButton.setTextSize(dialogParams.getMiddleButtonTextSize());
		middleButton.setTextColor(dialogParams.getMiddleButtonTextColor());
		
		Drawable backgroundRes = dialogParams.getMiddleButtonBackgroundRes();
		if(null != backgroundRes)
		{
			middleButton.setBackgroundDrawable(dialogParams.getMiddleButtonBackgroundRes());
		}
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		middleButton.setLayoutParams(layoutParams);
		
		middleButton.setPadding(0, DisplayUtils.dip2px(getContext(), 8), 0,  
				DisplayUtils.dip2px(getContext(), 8));
		
		return middleButton;
	}
	
	
	/**
	 * 设置中按钮位置
	 * @date 2014年2月10日
	 */
	protected void setMiddleButtonPosition()
	{
		if(!showMiddleButton)
		{
			return;
		}
		
		RelativeLayout.LayoutParams middleParams = (RelativeLayout.LayoutParams) middleButton.getLayoutParams();
		//若存在中间按钮，则按照三个按钮计算大小，否则按两个按钮计算大小
		if(showLeftButton || showRightButton)
		{
			middleParams.width = dialogParams.getButtonWidthOfThree();
			
			//计算左按钮的位置
			if(showLeftButton)
			{
				RelativeLayout.LayoutParams leftParams = (RelativeLayout.LayoutParams) leftButton.getLayoutParams();
				leftParams.width = dialogParams.getButtonWidthOfThree();
				leftParams.leftMargin = (int)((dialogParams.getDialogWidth() - dialogParams.getButtonWidthOfThree() * 3)/6);
				leftButton.setLayoutParams(leftParams);
			}
			
			//计算右按钮的位置
			if(showRightButton)
			{
				RelativeLayout.LayoutParams rightParams = (RelativeLayout.LayoutParams) rightButton.getLayoutParams();
				rightParams.width = dialogParams.getButtonWidthOfThree();
				rightParams.rightMargin = (int)((dialogParams.getDialogWidth() - dialogParams.getButtonWidthOfThree() * 3)/6);
				rightButton.setLayoutParams(rightParams);
			}
		}
		else 
		{
			//计算中按钮的大小
			onUpdateMiddleButtonWidth(middleButton, dialogParams.getButtonWidthOfOne());
			return;
		}
		
		middleButton.setLayoutParams(middleParams);
	}
	
	/**
	 * 设置右按钮
	 * @param text 按钮文本
	 * @param listener 点击事件监听器
	 */
	public void setRightButton(CharSequence text, final OnClickListener listener)
	{
		if(isBottomCustom || null == text)
		{
			return;
		}
		
		if(!showRightButton)
		{
			//初始化RightButton
			showRightButton = true;
			rightButton = initRightButton();
			bottomLayout.addView(rightButton);
		}
		
		//设置显示参数，点击事件
		rightButton.setText(text);
		rightButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(null != listener)
				{
					listener.onClick(TGDialog.this, IDialog.BUTTON_RIGHT);
				}
			}
		});
		
		//设置按钮显示位置
		setRightButtonPosition();
		bottomLayout.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 初始化右按钮
	 * @date 2014年1月6日
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected Button initRightButton()
	{
		Button rightButton = new Button(getContext());
		rightButton.setTextSize(dialogParams.getRightButtonTextSize());
		rightButton.setTextColor(dialogParams.getRightButtonTextColor());
		
		Drawable backgroundRes = dialogParams.getRightButtonBackgroundRes();
		if(null != backgroundRes)
		{
			rightButton.setBackgroundDrawable(dialogParams.getRightButtonBackgroundRes());
		}
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, 
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		rightButton.setLayoutParams(layoutParams);
		
		rightButton.setPadding(0, DisplayUtils.dip2px(getContext(), 8), 0,  
				DisplayUtils.dip2px(getContext(), 8));
		
		return rightButton;
	}
	
	/**
	 * 设置右按钮位置
	 * @date 2014年2月10日
	 */
	protected void setRightButtonPosition()
	{
		if(!showRightButton)
		{
			return;
		}
		
		RelativeLayout.LayoutParams rightParams = (RelativeLayout.LayoutParams) rightButton.getLayoutParams();
		//若存在中间按钮，则按照三个按钮计算大小，否则按两个按钮计算大小
		if(showMiddleButton)
		{
			//计算右按钮的位置
			rightParams.width = dialogParams.getButtonWidthOfThree();
			rightParams.rightMargin = (int)((dialogParams.getDialogWidth() - dialogParams.getButtonWidthOfThree() * 3) /6);
			
			//更新中间按钮的宽度
			onUpdateMiddleButtonWidth(middleButton, dialogParams.getButtonWidthOfThree());
		}
		else 
		{
			//计算右按钮的位置
			rightParams.width = dialogParams.getButtonWidthOfDouble();
			rightParams.rightMargin = (int)((dialogParams.getDialogWidth() - dialogParams.getButtonWidthOfDouble() * 2) /4);
		}
		
		rightButton.setLayoutParams(rightParams);
	}
	
	/**
	 * 更新中间按钮宽度
	 * @param middleButton 中间按钮
	 * @param width 按钮宽度
	 */
	protected void onUpdateMiddleButtonWidth(Button middleButton, int width)
	{
		RelativeLayout.LayoutParams middleParams = (RelativeLayout.LayoutParams)middleButton.getLayoutParams();
		middleParams.width = width;
		middleButton.setLayoutParams(middleParams);
	}
	
	/**
	 *  获取底部按钮
	 * @date 2014年2月10日
	 * @param witch 按钮标识
	 * @return 按钮
	 */
	protected Button getBottomButton(int witch)
	{
		switch (witch)
		{
		case IDialog.BUTTON_LEFT:
			return leftButton;
		case IDialog.BUTTON_MIDDLE:
			return middleButton;
		case IDialog.BUTTON_RIGHT:
			return rightButton;

		default:
			return null;
		}
	}
	
	/**
	 * 该方法的作用:获取左边按钮
	 * @date 2014年3月28日
	 * @return
	 */
	public Button getLeftButton()
	{
		return getBottomButton(IDialog.BUTTON_LEFT);
	}
	
	/**
	 * 获取右边按钮
	 * @date 2014年3月28日
	 * @return
	 */
	public Button getRightButton()
	{
		return getBottomButton(IDialog.BUTTON_RIGHT);
	}

	/**
	 * 获取中间按钮
	 * @date 2014年3月28日
	 * @return
	 */
	public Button getMiddleButton()
	{
		return getBottomButton(IDialog.BUTTON_MIDDLE);
	}
	
	/**
	 * 获取标题栏Layout
	 * @date 2014年2月10日
	 * @return
	 */
	protected LinearLayout getTitleLayout()
	{
		return titleLayout;
	}
	
	/**
	 * 获取对话框头部Layout填充的view
	 * @date 2013-1-30
	 * @return 对话框头部视图
	 */
	public View getTitleContentView()
	{
		return this.titleLayout.getChildAt(0);
	}
	
	/**
	 * 设置标题栏自定义视图
	 * @param contentView 自定义视图
	 * @param layoutParams 视图显示参数
	 */
	public void setTitleContentView(View contentView, ViewGroup.LayoutParams layoutParams)
	{
		if(null == contentView)
		{
			return;
		}
		
		this.titleLayout.removeAllViews();
		if(null == layoutParams)
		{
			this.titleLayout.addView(contentView);
		}
		else 
		{
			this.titleLayout.addView(contentView, layoutParams);
		}
		
		isTitleCustom = true;
	}
	
	/**
	 * 设置标题栏可见性
	 * @param visibility
	 */
	public void setTitleVisibility(int visibility)
	{
		this.titleLayout.setVisibility(visibility);
	}
	
	/**
	 * 设置标题栏文本
	 * @param title
	 */
	public void setTitleText(CharSequence title)
	{
		if(isTitleCustom || null == title)
		{
			return;
		}
		
		titleTextView.setText(title);
	}
	
	/**
	 * 设置标题栏文本颜色
	 * @param color
	 */
	public void setTitleTextColor(int color)
	{
		if(isTitleCustom)
		{
			return;
		}
		
		titleTextView.setTextColor(color);
	}
	
	/**
	 * 设置标题文本大小
	 * @param size sp
	 */
	public void setTitleTextSize(float size)
	{
		if(isTitleCustom)
		{
			return;
		}
		
		titleTextView.setTextSize(size);
	}
	
	/**
	 * 获取Body区域Layout
	 * @return
	 */
	protected LinearLayout getBodyLayout()
	{
		return bodyLayout;
	}
	
	/**
	 * 设置中间显示区域自定义视图
	 * @param contentView 自定义视图
	 * @param layoutParams 视图显示参数
	 */
	public void setBodyContentView(View contentView, ViewGroup.LayoutParams layoutParams)
	{
		if(null == contentView)
		{
			return;
		}
		
		this.bodyLayout.removeAllViews();
		if(null == layoutParams)
		{
			this.bodyLayout.addView(contentView);
		}
		else 
		{
			this.bodyLayout.addView(contentView, layoutParams);
		}
		
		isBodyCustom = true;
	}
	
	/**
	 * 获取Body区域文本视图
	 * @return
	 */
	public TextView getBodyTextView()
	{
		return bodyTextView;
	}
	
	/**
	 * 设置中间显示区域可见性
	 * @param visibility
	 */
	public void setBodyVisibility(int visibility)
	{
		this.bodyLayout.setVisibility(visibility);
	}
	
	/**
	 * 设置中间显示区域文本内容
	 * @param text
	 */
	public void setBodyText(CharSequence text)
	{
		if(isBodyCustom || null == text)
		{
			return;
		}
		
		bodyTextView.setText(text);
	}
	
	/**
	 * 设置中间显示区域文本颜色
	 * @param color
	 */
	public void setBodyTextColor(int color)
	{
		if(isBodyCustom)
		{
			return;
		}
		
		bodyTextView.setTextColor(color);
	}
	
	/**
	 * 设置中间显示区域文本颜色
	 * @param size
	 */
	public void setBodyTextSize(float size)
	{
		if(isBodyCustom)
		{
			return;
		}
		
		bodyTextView.setTextSize(size);
	}

	/**
	 * 获取底部操作栏Layout
	 * @return
	 */
	protected RelativeLayout getBottomLayout()
	{
		return bottomLayout;
	}
	
	/**
	 * 设置底部操作栏自定义视图
	 * @param contentView 自定义视图
	 * @param layoutParams 视图显示参数
	 */
	public void setBottomContentView(View contentView, ViewGroup.LayoutParams layoutParams)
	{
		if(null == contentView)
		{
			return;
		}
		
		this.bottomLayout.removeAllViews();
		if(null == layoutParams)
		{
			this.bottomLayout.addView(contentView);
		}
		else 
		{
			this.bottomLayout.addView(contentView, layoutParams);
		}
		
		isBottomCustom = true;
		showLeftButton = false;
		showRightButton = false;
		showMiddleButton = false;
	}
	
	/**
	 * 设置底部操作栏视图可见性
	 * @param visibility
	 */
	public void setBottomVisibility(int visibility)
	{
		bottomLayout.setVisibility(visibility);
	}
	
	/**
	 * 设置对话框背景
	 * @param drawable 对话框背景
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void setBackgroundDrawable(Drawable drawable)
	{
		rootView.setBackgroundDrawable(drawable);
	}
	
	/**
	 * 获取根视图
	 * @date 2014年1月6日
	 * @return
	 */
	protected LinearLayout getRootView()
	{
		return rootView;
	}
	
	/**
	 * 获取对话框参数
	 * @date 2014年1月6日
	 * @return
	 */
	protected TGDialogParams getDialogParams()
	{
		return dialogParams;
	}
	
	/**
	 * 左侧按钮是否显示
	 * @return
	 */
	protected boolean isShowLeftButton()
	{
		return showLeftButton;
	}
	
	/**
	 * 中间按钮是否显示
	 * @return
	 */
	protected boolean isShowMiddleButton()
	{
		return showMiddleButton;
	}
	
	/**
	 * 右侧按钮是否显示
	 * @return
	 */
	protected boolean isShowRightButton()
	{
		return showRightButton;
	}
	
	/**
	 * 对话框参数类
	 * @version V2.0
	 * @see JDK1.6,android-8
	 * @date 2014年2月10日
	 */
	public static class TGDialogParams 
	{
		/**
		 * 日志标签
		 */
		protected final String LOG_TAG = this.getClass().getSimpleName();
		
		private Context context;
		
		/**
		 * 对话框宽度
		 */
		private int dialogWidth = -10;
		
		public TGDialogParams(Context context)
		{
			this.context = context;
		}
		
		/**
		 * 获取对话框背景资源
		 * @date 2014年2月10日
		 * @return
		 */
		public Drawable getBackgroundResource()
		{
			return null;
		}
		
		/**
		 * 获取只有一个按钮时的宽度
		 * @return 只有一个按钮时的宽度
		 */
		public int getButtonWidthOfOne()
		{
			return (int) (getDialogWidth() * 0.45);
		}

		/**
		 * 获取只有两个按钮时的宽度
		 * @return 只有两个按钮时的宽度
		 */
		public int getButtonWidthOfDouble()
		{
			return (int) (getDialogWidth() * 0.4);
		}

		/**
		 * 获取只有三个按钮时的宽度
		 * @return 只有三个按钮时的宽度
		 */
		public int getButtonWidthOfThree()
		{
			return (int) (getDialogWidth() * 0.27);
		}

		/**
		 * 获取对话框的宽度
		 * @return 对话框的宽度
		 */
		@SuppressWarnings("deprecation")
		public int getDialogWidth()
		{
			if(dialogWidth == -10)
			{
				WindowManager windowManager = (WindowManager) getContext().getSystemService(
						Context.WINDOW_SERVICE);
				int screenWidth = windowManager.getDefaultDisplay().getWidth();
				
				dialogWidth = (int) (screenWidth * 0.9);
			}
			
			return dialogWidth;
		}
		
		/**
		 * 获取对话框的高度
		 * @return 对话框的高度
		 */
		public int getDialogHeight()
		{
			return LinearLayout.LayoutParams.WRAP_CONTENT;
		}

		/**
		 * 获取左按钮的文本颜色
		 * @return 左按钮的文本颜色
		 */
		public int getLeftButtonTextColor()
		{
			return 0xff1e1e1e;
		}
		
		/**
		 * 获取中按钮的文本颜色
		 * @return 中按钮的文本颜色
		 */
		public int getMiddleButtonTextColor()
		{
			return 0xff1e1e1e;
		}
		
		/**
		 * 获取右按钮的文本颜色
		 * @return 右按钮的文本颜色
		 */
		public int getRightButtonTextColor()
		{
			return 0xff1e1e1e;
		}
		
		/**
		 * 获取左按钮的文本颜色
		 * @return 左按钮的文本颜色
		 */
		public float getLeftButtonTextSize()
		{
			return 16f;
		}
		
		/**
		 * 获取中按钮的文本颜色
		 * @return 中按钮的文本颜色
		 */
		public float getMiddleButtonTextSize()
		{
			return 16f;
		}
		
		/**
		 * 获取右按钮的文本颜色
		 * @return 右按钮的文本颜色
		 */
		public float getRightButtonTextSize()
		{
			return 16f;
		}
		
		/**
		 * 获取左按钮的背景
		 * @return 左按钮的背景
		 */
		public Drawable getLeftButtonBackgroundRes()
		{
			return null;
		}
		
		/**
		 * 获取中按钮的背景
		 * @return 中按钮的背景
		 */
		public Drawable getMiddleButtonBackgroundRes()
		{
			return null;
		}
		
		/**
		 * 获取右按钮的背景
		 * @return 右按钮的背景
		 */
		public Drawable getRightButtonBackgroundRes()
		{
			return null;
		}

		/**
		 * 获取对话框的主题
		 * @return 对话框的主题
		 */
		public int getDialogTheme()
		{
			return CR.getStyleId(context, "tiger_baseDialog");
		}
		
		/**
		 * 标题栏文字大小
		 * @return
		 */
		public float getTitleTextSize()
		{
			return 22f;
		}
		
		/**
		 * 标题栏文字颜色
		 * @return
		 */
		public int getTitleTextColor()
		{
			return 0xff1e1e1e;
		}

		/**
		 * 获取中间显示区域文字大小
		 * @return
		 */
		public float getBodyTextSize()
		{
			return 18f;
		}
		
		/**
		 * 获取中间显示区域文字颜色
		 * @return
		 */
		public int getBodyTextColor()
		{
			return 0xff414141;
		}
		
		protected Context getContext()
		{
			return context;
		}
	}

	
}
