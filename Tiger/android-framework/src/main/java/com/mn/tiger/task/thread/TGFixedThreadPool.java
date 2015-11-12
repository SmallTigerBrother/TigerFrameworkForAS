package com.mn.tiger.task.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 该类作用及功能说明
 * 线程池
 * @date 2014年8月23日
 */
public class TGFixedThreadPool extends TGThreadPool
{
	/**
	 * executor
	 */
	private ExecutorService executorService;

	/**
	 * 默认线程数
	 */
	private static final int DEFAULT_POOL_SIZE = 256;

	public TGFixedThreadPool(int poolSize)
	{
		executorService = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
	}
	
	/**
	 * 该方法的作用:
	 * 执行线程
	 * @date 2014年8月23日
	 * @param runnale
	 */
	public void execute(Runnable runnale)
	{
		executorService.execute(runnale);
	}

	/**
	 * 该方法的作用:
	 * 销毁
	 * @date 2014年8月23日
	 */
	public void destroy()
	{
		executorService.shutdownNow();
	}
}
