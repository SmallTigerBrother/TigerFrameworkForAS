package com.mn.tiger.task.queue;

import com.mn.tiger.task.ITaskListener;
import com.mn.tiger.task.TGScheduleTaskList;
import com.mn.tiger.task.dispatch.TGDispatcher;
import com.mn.tiger.task.thread.TGScheduleThreadPool;
import com.mn.tiger.task.thread.TGThreadPool;

public class TGScheduleTaskQueue extends TGTaskQueue
{
	private static final long serialVersionUID = 1L;
	
	private TGScheduleThreadPool threadPool;
	
	private TGScheduleTaskList taskList;

	public TGScheduleTaskQueue()
	{
		super(0);
	}
	
	@Override
	protected TGThreadPool initThreadPool()
	{
		return new TGScheduleThreadPool();
	}
	
	public void setTaskList(TGScheduleTaskList taskList)
	{
		this.taskList = taskList;
	}
	
	@Override
	public void executeNextTask()
	{
		setState(MPQueueState.RUNNING);
		taskList.get(taskList.size() - 1).setTaskListener(lastTaskListener);
		threadPool.executeTaskList(taskList);
	}

	@Override
	public void cancelAllTasks()
	{
		threadPool.destroy();
		this.taskList = null;
		this.threadPool = null;
	}
	
	@Override
	public boolean pauseTask(int taskId)
	{
		throw new RuntimeException("ScheduleTaskQueue can not be paused, please use cancel method!");
	}
	
	private ITaskListener lastTaskListener = new ITaskListener()
	{
		@Override
		public void onTaskStart()
		{
		}
		
		@Override
		public void onTaskPause(int taskId)
		{
		}
		
		@Override
		public void onTaskFinished(int taskId)
		{
			//销毁任务队列
			TGDispatcher.getInstance().cancelScheduleTaskList(taskId);
		}
		
		@Override
		public void onTaskError(int taskId, int code, Object msg)
		{
			//销毁任务队列
			TGDispatcher.getInstance().cancelScheduleTaskList(taskId);
		}
		
		@Override
		public void onTaskChanged(int progress)
		{
		}
		
		@Override
		public void onTaskCancel(int taskId)
		{
			//销毁任务队列
			TGDispatcher.getInstance().cancelScheduleTaskList(taskId);
		}
	};
}
