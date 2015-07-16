package com.mn.tiger.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mn.tiger.utility.CR;
import com.mn.tiger.utility.ToastUtils;
import com.mn.tiger.widget.TGImageButton;
import com.mn.tiger.widget.TGNavigationBar;

/**
 * 带导航条的Activity基类
 */
public class TGActionBarActivity extends Activity implements IView
{
	/**
	 * 导航条
	 */
	private TGNavigationBar navigationBar;

	/**
	 * 主视图
	 */
	private FrameLayout panelLayout;
	
	/**
	 * 加载框
	 */
	private DialogFragment loadingDialog;
	
	/**
	 * laoding对话框显示次数（使用计数器，防止对话框无法显示，或者无法消失的问题）
	 */
	private int dialogShowCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(CR.getLayoutId(this, "tiger_fragment"));
		panelLayout = (FrameLayout) findViewById(CR.getViewId(this, "panel"));
		navigationBar = (TGNavigationBar) findViewById(CR.getViewId(this, "navigationbar"));
		navigationBar.getLeftNaviButton().setVisibility(View.VISIBLE);
		initNavigationResource(navigationBar);
		initPanelLayout(panelLayout);
	
		//添加到Application中
		((TGApplication)getApplication()).addActivityToStack(this);
	}

	@Override
	public void setContentView(View view, LayoutParams params)
	{
		panelLayout.addView(view, params);
	}
	
	@Override
	public void setContentView(int layoutResID)
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		View contentView = inflater.inflate(layoutResID, null);
		
		this.setContentView(contentView, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setContentView(View view)
	{
		if(null == view.getLayoutParams())
		{
			this.setContentView(view,new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
		}
		else
		{
			this.setContentView(view, view.getLayoutParams());
		}
	}
	
	/**
	 * 该方法的作用: 初始化导航条资源
	 * @date 2013-11-8
	 * @param navigationBar
	 */
	protected void initNavigationResource(TGNavigationBar navigationBar)
	{
		navigationBar.setBackgroundResource(CR.getDrawableId(this,
				"tiger_header_background"));

		navigationBar.getLeftNaviButton().setBackgroundResource(
				CR.getDrawableId(this, "tiger_nav_back_button_selector"));

		showLeftBarButton(true);
		navigationBar.getLeftNaviButton().setOnClickListener(
				new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						finish();
					}
				});
		navigationBar.getRightNaviButton().setBackgroundResource(
				CR.getDrawableId(this, "tiger_nav_refresh_button_selector"));
	}

	/**
	 * 初始化PanelLayout
	 * @param panelLayout
	 */
	protected void initPanelLayout(FrameLayout panelLayout)
	{
		
	}
	
	/**
	 * 该方法的作用: 获取导航条左按钮
	 * 
	 * @date 2013-11-18
	 * @return
	 */
	public TGImageButton getLeftBarButton()
	{
		return navigationBar.getLeftNaviButton();		
	}

	/**
	 * 该方法的作用: 获取导航条右按钮
	 * 
	 * @date 2013-11-18
	 * @return
	 */
	public TGImageButton getRightBarButton()
	{
		return navigationBar.getRightNaviButton();
	}

	/**
	 * 该方法的作用: 获取导航条
	 * 
	 * @date 2013-11-8
	 * @return
	 */
	public TGNavigationBar getNavigationBar()
	{
		return navigationBar;
	}

	/**
	 * 该方法的作用: 获取导航条中间TextView
	 * 
	 * @date 2013-11-18
	 * @return
	 */
	public TextView getMiddleTextView()
	{
		return navigationBar.getMiddleTextView();
	}

	/**
	 * 该方法的作用: 设置导航条标题文本
	 * 
	 * @date 2013-11-18
	 * @param titleText
	 * @return
	 */
	public boolean setBarTitleText(String titleText)
	{
		TextView middleTextView = getMiddleTextView();
		if (null != middleTextView)
		{
			middleTextView.setText(titleText);
			return true;
		}

		return false;
	}

	/**
	 * 该方法的作用: 显示导航条左按钮
	 * 
	 * @date 2013-11-18
	 * @param show
	 */
	public void showLeftBarButton(boolean show)
	{
		TGImageButton leftButton = getLeftBarButton();
		if (null != leftButton)
		{
			if (show)
			{
				leftButton.setVisibility(View.VISIBLE);
			}
			else
			{
				leftButton.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 该方法的作用: 显示导航条右按钮
	 * 
	 * @date 2013-11-18
	 * @param show
	 */
	public void showRightBarButton(boolean show)
	{
		TGImageButton rightButton = getRightBarButton();
		if (null != rightButton)
		{
			if (show)
			{
				rightButton.setVisibility(View.VISIBLE);
			}
			else
			{
				rightButton.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onDestroy() 
	{
		((TGApplication)getApplication()).removeActivityFromStack(this);
		super.onDestroy();
	}
	
	/**
	 * 设置导航条是否可见
	 * @param navigationBarVisible
	 */
	public void setNavigationBarVisible(boolean navigationBarVisible)
	{
		if(navigationBarVisible)
		{
			navigationBar.setVisibility(View.VISIBLE);
		}
		else
		{
			navigationBar.setVisibility(View.GONE);
		}
	}
	
	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) 
	{
		//方式flyme系统中弹出清理内存工具条时，应用崩溃
		return false;
	}
	
	/**
	 * 显示进度对话框
	 */
	public void showLoadingDialog()
	{
		if(null == loadingDialog)
		{
			loadingDialog = initLoadingDialog();
		}
		
		if(null != loadingDialog)
		{
			if(dialogShowCount <= 0)
			{
				dialogShowCount = 0;
				loadingDialog.show(getFragmentManager(), "loadingDialog");
			}
			dialogShowCount++;
		}
	}
	
	/**
	 * 隐藏进度对话框
	 */
	public void dismissLoadingDialog()
	{
		dialogShowCount--;
		if(loadingDialog != null && dialogShowCount<= 0)
		{
			loadingDialog.dismissAllowingStateLoss();
		}
	}
	
	/**
	 * 初始化进度对话框
	 * @return
	 */
	protected DialogFragment initLoadingDialog()
	{
		return null;
	}
	
	public void showToast(int textResId)
	{
		ToastUtils.showToast(this, textResId);
	}
	
	public void showToast(String text)
	{
		ToastUtils.showToast(this, text);
	}

}
