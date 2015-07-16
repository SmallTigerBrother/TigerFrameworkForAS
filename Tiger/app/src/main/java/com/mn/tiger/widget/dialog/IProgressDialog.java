package com.mn.tiger.widget.dialog;


/**
 * 该类作用及功能说明
 * 进度对话框接口
 * @version V2.0
 * @see JDK1.6,android-8
 * @date 2014年2月10日
 */
public interface IProgressDialog extends IDialog
{
	/**
	 * 进度条类型——————未知类型
	 */
	public static final int PROGRESS_STYLE_NONE = -10;
	
	/**
	 * 进度条类型——————Horiziontal类型
	 */
	public static final int PROGRESS_STYLE_HORIZONTAL = 11;
	
	/**
	 * 进度条类型——————Spinner类型
	 */
	public static final int PROGRESS_STYLE_SPINNER = 12;
	
	/**
	 * 该方法的作用:
	 * 设置进度
	 * @date 2014年2月10日
	 * @param progress
	 */
	void setProgress(int progress);
	
	/**
	 * 该方法的作用:
	 * 获取进度
	 * @date 2014年2月10日
	 * @return
	 */
	int getProgress();
	
	/**
	 * 该方法的作用:
	 * 设置进度最大值
	 * @date 2014年2月10日
	 * @param max
	 */
	void setMax(int max);
	
	/**
	 * 该方法的作用:
	 * 设置进度文本
	 * @date 2014年2月10日
	 * @param text
	 */
	void setProgressText(String text);
	
	/**
	 * 该方法的作用:
	 * 获取进度文本
	 * @date 2014年2月10日
	 * @return
	 */
	String getProgressText();
	
	/**
	 * 该方法的作用:
	 * 设置进度框类型
	 * @date 2014年2月10日
	 * @param style
	 */
	public void setProgressStyle(int style);
}
