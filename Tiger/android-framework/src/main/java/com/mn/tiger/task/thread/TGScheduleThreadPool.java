package com.mn.tiger.task.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mn.tiger.task.TGScheduleTaskList;

/**
 * 顺序执行的线程池
 */
public class TGScheduleThreadPool extends TGThreadPool
{
	/**
	 * Executor
	 */
	private ExecutorService executorService;

	public TGScheduleThreadPool()
	{
		executorService = Executors.newSingleThreadScheduledExecutor();
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
	 * 执行有序任务列表
	 * @param taskList
	 */
	public void executeTaskList(TGScheduleTaskList taskList)
	{
		for(int i = 0; i < taskList.size(); i++)
		{
			this.execute(taskList.get(i));
		}
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
