package com.mn.tiger.app;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.mn.tiger.widget.slidingmenu.SlidingActivityHelper;
import com.mn.tiger.widget.slidingmenu.SlidingMenu;
import com.mn.tiger.widget.slidingmenu.SlidingMenu.OnOpenListener;
import com.mn.tiger.widget.slidingmenu.SlidingMenu.SlideMode;
import com.mn.tiger.widget.slidingmenu.SlidingMenu.SlideTouchMode;

/**
 * 带侧滑栏的Activity
 */
public class TGSlidingActionBarActivity extends TGActionBarActivity
{
	/**
	 * 侧滑栏帮助类
	 */
	private SlidingActivityHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate(savedInstanceState);
	}

	@Override
	public View findViewById(int id)
	{
		View v = super.findViewById(id);
		if (v != null)
		{
			return v;
		}
			
		return mHelper.findViewById(id);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		mHelper.onSaveInstanceState(outState);
	}

	@Override
	public void setContentView(int id)
	{
		setContentView(getLayoutInflater().inflate(id, null));
	}

	@Override
	public void setContentView(View v)
	{
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setContentView(View v, LayoutParams params)
	{
		super.setContentView(v, params);
		//注册主视图
		mHelper.registerAboveContentView(v, params);
	}
	
	/**
	 * 设置左菜单
	 * @param id 左菜单布局id
	 */
	public void setLeftContentView(int id)
	{
		setLeftContentView(getLayoutInflater().inflate(id, null));
	}
	
	/**
	 * 设置左菜单
	 * @param view
	 */
	public void setLeftContentView(View view)
	{
		mHelper.setBehindContentView(view);
	}
	
	/**
	 * 设置右菜单
	 * @param id 右菜单布局id
	 */
	public void setRightContentView(int id)
	{
		setRightContentView(getLayoutInflater().inflate(id, null));
	}
	
	/**
	 * 设置右菜单
	 * @param view
	 */
	public void setRightContentView(View view)
	{
		mHelper.setSecondaryBehindContentView(view);
	}

	/**
	 * 设置主视图偏移量
	 * @param offset
	 */
	public void setAboveOffset(int offset)
	{
		mHelper.setAboveOffset(offset);
	}

	/**
	 * 设置菜单偏移量
	 * @param offset
	 */
	public void setBehindOffset(int offset)
	{
		mHelper.setBehindOffset(offset);
	}
	
	/**
	 * 设置滑动模式
	 * @param mode
	 */
	public void setSildeMode(SlideMode mode)
	{
		mHelper.setSildeMode(mode);
	}
	
	/**
	 * 设置触摸模式
	 * @param mode
	 */
	public void setTouchModeAbove(SlideTouchMode mode)
	{
		mHelper.setTouchModeAbove(mode);
	}

	/**
	 * 获取侧滑栏视图
	 * @return
	 */
	public SlidingMenu getSlidingMenu()
	{
		return mHelper.getSlidingMenu();
	}

	/**
	 * 切换左菜单：若左菜单未显示，显示菜单；若左菜单显示，则切换回主视图
	 */
	public void toggleLeftMenu()
	{
		mHelper.toggle();
	}
	
	/**
	 * 切换右菜单：若右菜单未显示，显示菜单；若右菜单显示，则切换回主视图
	 */
	public void toggleRightMenu()
	{
		mHelper.toggleSecondaryMenu();
	}

	/**
	 * 显示主视图
	 */
	public void showContent()
	{
		mHelper.showContent();
	}

	/**
	 * 显示左菜单
	 */
	public void showLeftMenu()
	{
		mHelper.showMenu();
	}
	
	/**
	 * 左菜单是否为显示状态
	 * @return
	 */
	public boolean isLeftMenuShowing()
	{
		return mHelper.getSlidingMenu().isMenuShowing();
	}

	/**
	 * 显示右菜单
	 */
	public void showRightMenu()
	{
		mHelper.showSecondaryMenu();
	}
	
	/**
	 * 右菜单是否为显示状态
	 * @return
	 */
	public boolean isRightMenuShowing()
	{
		return mHelper.getSlidingMenu().isSecondaryMenuShowing();
	}
	/**
	 * Sets the OnOpenListener. {@link OnOpenListener#onOpen()
	 * OnOpenListener.onOpen()} will be called when the SlidingMenu is opened
	 * 
	 * @param listener
	 *            the new OnOpenListener
	 */
	public void setLeftMenuOnOpenListener(OnOpenListener listener)
	{
		// mViewAbove.setOnOpenListener(listener);
		mHelper.setOnOpenListener(listener) ;
	}

	/**
	 * Sets the OnOpenListner for secondary menu {@link OnOpenListener#onOpen()
	 * OnOpenListener.onOpen()} will be called when the secondary SlidingMenu is
	 * opened
	 * 
	 * @param listener
	 *            the new OnOpenListener
	 */
	public void setRightMenuOnOpenListner(OnOpenListener listener)
	{
		mHelper.setSecondaryOnOpenListner(listener);;
	}

	/**
	 * 设置ActionBar是否可以跟随主视图进行滑动
	 * @param b
	 */
	public void setSlidingActionBarEnabled(boolean b)
	{
		mHelper.setSlidingActionBarEnabled(b);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		boolean b = mHelper.onKeyUp(keyCode, event);
		if (b)
		{
			return b;
		}
		return super.onKeyUp(keyCode, event);
	}

}
