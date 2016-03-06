package com.mn.tiger.task;

/**
 *任务回调接口
 */
public interface ITaskListener
{
	/**
	 * 该方法的作用:
	 * 任务启动回调方法
	 */
	void onTaskStart();
	
	/**
	 * 该方法的作用:
	 * 任务变化回调方法
	 */
	void onTaskChanged(int progress);

	/**
	 * 该方法的作用:
	 * 任务结束回调方法
	 */
	void onTaskFinished(int taskId);
	
	/**
	 * 该方法的作用:
	 * 任务出错回调方法
	 */
	void onTaskError(int taskId, int code, Object msg);
	
	/**
	 * 该方法的作用:
	 */
	void onTaskCancel(int taskId);
	
	/**
	 * 该方法的作用:
	 * 任务暂停
	 */
	void onTaskPause(int taskId);
	
}
