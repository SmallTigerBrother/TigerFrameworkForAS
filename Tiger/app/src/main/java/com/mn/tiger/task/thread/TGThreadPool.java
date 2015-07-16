package com.mn.tiger.task.thread;

/**
 * 线程池
 */
public abstract class TGThreadPool
{
	/**
	 * 该方法的作用:
	 * 执行线程
	 * @date 2014年8月23日
	 * @param runnale
	 */
	public abstract void execute(Runnable runnale);

	/**
	 * 该方法的作用:
	 * 销毁
	 * @date 2014年8月23日
	 */
	public abstract void destroy();
}
