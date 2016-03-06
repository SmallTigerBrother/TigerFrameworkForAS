package com.mn.tiger.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.mn.tiger.utility.CR;


/**
 * 该类作用及功能说明
 * 顶部导航条
 * @version V2.0
 */
public class TGNavigationBar extends RelativeLayout
{	
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();
	
	/**
	 * 左导航区Layout
	 */
	private RelativeLayout leftNaviLayout;
	
	/**
	 * 右导航区Layout
	 */
	private RelativeLayout rightNaviLayout;
	
	/**
	 * 中间导航区Layout
	 */
	private RelativeLayout middleNaviLayout;
	
	/**
	 * 默认左导航按钮
	 */
	private TGImageButton leftNaviButton;
	
	/**
	 * 默认右导航按钮
	 */
	private TGImageButton rightNaviButton;
	
	/**
	 * 中间标题TextView
	 */
	private TGImageButton middleTextView;


	public TGNavigationBar(Context context) 
	{
		super(context);
		setupViews();
	}

	public TGNavigationBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setupViews();
	}
	
	/**
	 * 该方法的作用:
	 * 初始化试图
	 */
	protected void setupViews()
	{
        inflate(getContext(), CR.getLayoutId(getContext(), "tiger_navigationbar"), this);
        leftNaviLayout = (RelativeLayout)findViewById(CR.getViewId(getContext(),"tiger_navi_left_layout"));
        rightNaviLayout = (RelativeLayout)findViewById(CR.getViewId(getContext(), "tiger_navi_right_layout"));
        middleNaviLayout = (RelativeLayout)findViewById(CR.getViewId(getContext(), "tiger_navi_middle_layout"));

        middleTextView = (TGImageButton)findViewById(CR.getViewId(getContext(), "tiger_navi_middle_text"));
        leftNaviButton = (TGImageButton)findViewById(CR.getViewId(getContext(), "tiger_navi_left_btn"));
        rightNaviButton = (TGImageButton)findViewById(CR.getViewId(getContext(), "tiger_navi_right_btn"));
	}
	
	/**
	 * 该方法的作用:
	 * 获取左导航Layout
	 * @return
	 */
	public RelativeLayout getLeftNaviLayout()
	{
		return leftNaviLayout;
	}
	
	/**
	 * 该方法的作用:
	 * 获取右导航Layout
	 * @return
	 */
	public RelativeLayout getRightNaviLayout()
	{
		return rightNaviLayout;
	} 
	
	/**
	 * 该方法的作用:
	 * 获取中间导航Layout
	 * @return
	 */
	public RelativeLayout getMiddleNaviLayout()
	{
		return middleNaviLayout;
	}
	
	/**
	 * 该方法的作用:
	 * 获取左导航按钮
	 * @return
	 */
	public TGImageButton getLeftNaviButton()
	{
		return leftNaviButton;
	}
	
	/**
	 * 该方法的作用:
	 * 设置左侧导航按钮
	 * @param leftNaviButton
	 */
	public void setLeftNaviButton(TGImageButton leftNaviButton)
	{
		leftNaviLayout.removeAllViews();
		this.leftNaviButton = leftNaviButton;
		leftNaviLayout.addView(leftNaviButton);
	}
	
	/**
	 * 该方法的作用:
	 * 获取右导航按钮
	 * @return
	 */
	public TGImageButton getRightNaviButton()
	{
		return rightNaviButton;
	}
	
	/**
	 * 该方法的作用:
	 * 设置右侧导航按钮
	 * @param rightNaviButton
	 */
	public void setRightNaviButton(TGImageButton rightNaviButton)
	{
		rightNaviLayout.removeAllViews();
		this.rightNaviButton = rightNaviButton;
		rightNaviLayout.addView(rightNaviButton);
	}
	
	/**
	 * 该方法的作用:
	 * 获取中间标题TextView
	 * @return
	 */
	public TGImageButton getMiddleTextView()
	{
		return middleTextView;
	}
	
	/**
	 * 该方法的作用:
	 * 设置标题文本
	 * @param text
	 * @return
	 */
	public boolean setMiddleText(String text)
	{
		TGImageButton middleText = getMiddleTextView();
		if (null != middleText)
		{
			middleText.setText(text);
			return true;
		}
		
		return false;
	}
		
	/**
	 * 该方法的作用:
	 * 设置左导航按钮是否可用
	 * @param enabled
	 */
	public void setLeftButtonEnabled(boolean enabled) 
	{
		getLeftNaviButton().setEnabled(enabled);
	}
	
	/**
	 * 该方法的作用:
	 * 设置右导航按钮是否可用
	 * @param enabled
	 */
	public void setRightButtonEnabled(boolean enabled) 
	{
		getRightNaviButton().setEnabled(enabled);
	}
	
}
