package com.mn.tiger.task.thread;

import com.mn.tiger.task.TGScheduleTaskList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	 */
	public void execute(Runnable runnale)
	{
		executorService.execute(runnale);
	}
	
	/**
	 * 执行有序任务列表
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
	 */
	public void destroy()
	{
		executorService.shutdownNow();
	}
}
