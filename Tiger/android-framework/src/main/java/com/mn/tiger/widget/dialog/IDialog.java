package com.mn.tiger.widget.dialog;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

/**
 * 对话框接口
 */
public interface IDialog extends DialogInterface
{
	/**
	 * 左按钮标识
	 */
	public static final int BUTTON_LEFT = 1;
	
	/**
	 * 中按钮标识
	 */
	public static final int BUTTON_MIDDLE = 2;
	
	/**
	 * 右按钮标识
	 */
	public static final int BUTTON_RIGHT = 3;
	
	/**
	 * 设置对话框背景
	 * @param drawable 对话框背景
	 */
	void setBackgroundDrawable(Drawable drawable);
	
	/**
	 * 设置取消事件监听器
	 * @param listener 取消事件监听器
	 */
	void setOnCancelListener(DialogInterface.OnCancelListener listener);
	
	/**
	 * 设置消失事件监听器
	 * @param listener 消失事件监听器
	 */
	void setOnDismissListener(DialogInterface.OnDismissListener listener);
	
	/**
	 * 设置键盘事件监听器
	 * @param listener 键盘事件监听器
	 */
	void setOnKeyListener(DialogInterface.OnKeyListener listener);
	
	/**
	 * 设置显示事件监听器
	 * @param listener 显示事件监听器
	 */
	void setOnShowListener(DialogInterface.OnShowListener listener);
	
	/**
	 * 设置左按钮
	 * @param text 按钮文本
	 * @param listener 点击事件监听器
	 */
	void setLeftButton(CharSequence text, final OnClickListener listener);
	
	/**
	 * 设置中按钮
	 * @param text 按钮文本
	 * @param listener 点击事件监听器
	 */
	void setMiddleButton(CharSequence text, final OnClickListener listener);
	
	/**
	 * 设置右按钮
	 * @param text 按钮文本
	 * @param listener 点击事件监听器
	 */
	void setRightButton(CharSequence text, final OnClickListener listener);
	
	/**
	 * 设置标题栏自定义视图
	 * @param contentView 自定义视图
	 * @param layoutParams 视图显示参数
	 */
	void setTitleContentView(View contentView, ViewGroup.LayoutParams layoutParams);
	
	/**
	 * 设置标题栏可见性
	 * @param visibility
	 */
	void setTitleVisibility(int visibility);
	
	/**
	 * 设置标题栏文本
	 * @param title
	 */
	void setTitleText(CharSequence title);
	
	/**
	 * 设置标题栏文本颜色
	 * @param color
	 */
	void setTitleTextColor(int color);
	
	/**
	 * 设置中间显示区域自定义视图
	 * @param contentView 自定义视图
	 * @param layoutParams 视图显示参数
	 */
	void setBodyContentView(View contentView, ViewGroup.LayoutParams layoutParams);
	
	/**
	 * 设置中间显示区域可见性
	 * @param visibility
	 */
	void setBodyVisibility(int visibility);
	
	/**
	 * 设置中间显示区域文本内容
	 * @param title
	 */
	void setBodyText(CharSequence text);
	
	/**
	 * 该方法的作用:
	 * 获取中间文本视图
	 * @date 2014年2月10日
	 * @return
	 */
	TextView getBodyTextView();
	
	/**
	 * 设置中间显示区域文本颜色
	 * @param color
	 */
	void setBodyTextColor(int color);
	
	/**
	 * 设置底部操作栏自定义视图
	 * @param contentView 自定义视图
	 * @param layoutParams 视图显示参数
	 */
	void setBottomContentView(View contentView, ViewGroup.LayoutParams layoutParams);
	
	/**
	 * 设置底部操作栏视图可见性
	 * @param visibility
	 */
	void setBottomVisibility(int visibility);
	
	/**
	 * 显示对话框
	 */
	void show();

	/**
	 * 该方法的作用:
	 * 设置是否可以取消
	 * @date 2014年2月10日
	 * @param cancelable
	 */
	void setCancelable(boolean cancelable);

	/**
	 * 该方法的作用:
	 * 设置点击外部是否可以取消
	 * @date 2014年2月10日
	 * @param cancelable
	 */
	void setCanceledOnTouchOutside(boolean cancelable);
	
	boolean isShowing();
	
	Window getWindow();
}
