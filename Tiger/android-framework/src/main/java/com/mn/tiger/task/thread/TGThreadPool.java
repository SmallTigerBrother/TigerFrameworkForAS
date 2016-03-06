package com.mn.tiger.task.thread;

/**
 * 线程池
 */
public abstract class TGThreadPool
{
	/**
	 * 该方法的作用:
	 * 执行线程
	 */
	public abstract void execute(Runnable runnale);

	/**
	 * 该方法的作用:
	 * 销毁
	 */
	public abstract void destroy();
}
