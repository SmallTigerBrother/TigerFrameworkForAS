package com.mn.tiger.task.queue;

import java.util.ArrayList;
import java.util.List;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.task.ITaskListener;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.task.TGTask.TGTaskState;
import com.mn.tiger.task.TGTask.TaskError;
import com.mn.tiger.task.queue.TGLock.onLockListener;
import com.mn.tiger.task.queue.TGLock.onUnLockListener;
import com.mn.tiger.task.thread.TGFixedThreadPool;
import com.mn.tiger.task.thread.TGThreadPool;

/**
 * 
 * 该类作用及功能说明: 可并行执行多个任务的队列
 * 
 * @date 2014年6月25日
 */
public class TGTaskQueue extends AbsTaskQueue
{
	/**
	 * @date 2014年6月25日
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();
	
	/**
	 * 该队列中正在运行任务个数
	 */
	protected List<Integer> runningTaskList = new ArrayList<Integer>();
	
	/**
	 * 队列状态
	 */
	private MPQueueState state = MPQueueState.WAITING;
	
	/**
	 * 任务监听
	 */
	protected ITaskListener taskListener;
	
	/**
	 * 执行该队列任务的线程池
	 */
	private TGThreadPool threadPool;
	
	/**
	 * 最大并发线程数
	 */
	private int maxThreadNum;
	
	/**
	 * 构造函数
	 * @date 2014年6月25日
	 */
	public TGTaskQueue(int maxThreadNum)
	{
		super();
		this.maxThreadNum = maxThreadNum;
		threadPool = initThreadPool();
	}
	
	/**
	 * 初始化线程池
	 * @return
	 */
	protected TGThreadPool initThreadPool()
	{
		return new TGFixedThreadPool(getMaxThreadNum());
	}
	
	@Override
	public synchronized void executeNextTask()
	{
		LogTools.d(LOG_TAG, "[Method:executeNextTask]");

		//若当前队列状态为暂停，则直接退出
		if(state == MPQueueState.PAUSE)
		{
			LogTools.p(LOG_TAG, "[Method:executeNextTask] the queue has been paused!");
			return;
		}
		
		// Task按运行状态和weight排序
		sortTaskQueue();
		
		// 获取队列中的任务总数
		int totalTask = getTaskArray().size();
		LogTools.d(LOG_TAG, "[Method:executeNextTask]" + " count: " + totalTask);
		// 若已无任务，队列状态改为等待
		if(totalTask <= 0)
		{
			state = MPQueueState.WAITING;
		}
		
		// 队列中有任务，取最大个数等待任务执行
		boolean loop = true;
		int index = 0;
		
		do
		{
			LogTools.d(LOG_TAG, "[Method:executeNextTask] runningTaskNum: " + 
		        runningTaskList.size());
			if(totalTask > index && runningTaskList.size() <= getMaxThreadNum())
			{
				TGTask runTask = getTaskArray().get(this.get(index));
				if (runTask != null && runTask.getTaskState() == TGTaskState.WAITING)
				{
					runTask.executeTask(threadPool);
					runningTaskList.add(runTask.getTaskID());
					state = MPQueueState.RUNNING;
				}
			}
			else
			{
				loop = false;
			}
			
			index++;
			
		}while(loop);
	}
	
	@Override
	protected void sortTaskQueue()
	{
		//TODO 
	}
	
	@Override
	public synchronized void addLast(TGTask task)
	{
		task.setTaskListener(getTaskListener());
		super.addLast(task);
	}
	
	/**
	 * 该方法的作用: 移除任务
	 * @date 2014年6月25日
	 */
	protected synchronized boolean removeTask(Integer taskId)
	{
		// 获取被移除任务
		TGTask task = getTaskArray().get(taskId.intValue());
		if(task != null)
		{
			// 如果是正在运行的任务，队列中正在运行任务减1
			if(runningTaskList.size() > 0 && runningTaskList.contains(taskId))
			{
				runningTaskList.remove(taskId);
			}
    		//销毁任务
			task = null;
		}
		
		// 任务移除队列
		boolean result =  super.remove(taskId);
		
		// 执行队列中下一个等待任务
		LogTools.d(LOG_TAG, "[Method:onRemove] queue size : " + TGTaskQueue.this.size());
		this.executeNextTask();
		
		return result;
	}

	/**
	 * 该方法的作用: 暂停任务队列
	 * 
	 * @date 2014年3月17日
	 */
	public synchronized void pauseTaskQueue()
	{
		LogTools.d(LOG_TAG, "[Method:pauseTaskQueue]");
		//设置队列运行状态为PAUSE
		state = MPQueueState.PAUSE;
		
		//依次暂停任务
		TGTask task = null;
		for(int i = 0; i < runningTaskList.size(); i++)
		{
			// 克隆当前运行任务，重新加入到队列
			task = getTaskArray().get(runningTaskList.get(i));
			if(null != task)
			{
				pauseTask(task.getTaskID());
			}
		}
	}
	
	/**
	 * 该方法的作用: 暂停某个指定任务
	 * 
	 * @date 2014年3月17日
	 */
	public synchronized boolean pauseTask(int taskId)
	{
		LogTools.d(LOG_TAG, "[Method:pauseTask] taskId --> " + taskId);
		
		//将当前运行任务的Clone对象放入队列中
		TGTask task = getTaskArray().get(taskId);
		if(null != task)
		{
			//克隆一个新任务加入到队列中
			cloneTaskEnQueue(task);
			// 将当前运行的任务暂停
			task.pause();
			//从队列中移除原有任务
			return this.removeTask((Integer)task.getTaskID());
		}
		
		return false;
	}
	
	/**
	 * 克隆任务并插入队列
	 * @param task
	 */
	private void cloneTaskEnQueue(TGTask task)
	{
		TGTask cloneTask = null;
		try
		{
			cloneTask = (TGTask) task.clone();
			this.removeTask(task.getTaskID());
		}
		catch (CloneNotSupportedException e)
		{
			LogTools.e(LOG_TAG, e.getMessage(), e);
		}
		
		if(null != cloneTask)
		{
			//克隆一个对象，加入到队列中
			this.addLast(cloneTask);
		}
	}
	
	/**
	 * 该方法的作用: 重启任务队列
	 * 
	 * @date 2014年3月17日
	 */
	public synchronized void restart()
	{
		LogTools.d(LOG_TAG, "[Method:restart]");

		//设置队列运行状态为WAITING
		state = MPQueueState.WAITING;
		//执行下一个任务
		executeNextTask();
	}
	
	/**
	 * 取消某个任务
	 * @param taskId
	 * @return
	 */
	public synchronized boolean cancelTask(int taskId)
	{
		TGTask task = getTaskArray().get(taskId);
		if(null != task)
		{
			task.cancel();
			return TGTaskQueue.this.removeTask((Integer)taskId);
		}
		
		return false;
	}
	
	/**
	 * 该方法的作用:
	 * 取消所有任务
	 * @date 2014年9月4日
	 */
	public void cancelAllTasks()
	{
		super.cancelAllTasks();
		//修改队列状态
		state = MPQueueState.WAITING;
	}
	
	/**
	 * 该方法的作用:
	 * 获取任务状态
	 * @date 2014年8月22日
	 * @return
	 */
	public MPQueueState getState()
	{
		return state;
	}

	/**
	 * 该方法的作用:
	 * 设置任务状态
	 * @date 2014年8月22日
	 * @param state
	 */
	public void setState(MPQueueState state)
	{
		this.state = state;
	}

	/**
	 * 该方法的作用:
	 * 获取线程池
	 * @date 2014年8月22日
	 * @return
	 */
	public TGThreadPool getThreadPool()
	{
		return threadPool;
	}

	/**
	 * 该方法的作用:
	 * 设置线程池
	 * @date 2014年8月22日
	 * @param threadPool
	 */
	public void setThreadPool(TGThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}

	/**
	 * 该方法的作用: 获取默认任务监听
	 * @date 2014年6月25日
	 * @return
	 */
	protected ITaskListener getTaskListener()
	{
		if(taskListener == null)
		{
			return taskListener = new DefaultTaskListener();
		}
		return taskListener;
	}
	
	/**
	 * 设置任务监听器
	 * @param listener
	 */
	protected void setTaskListener(ITaskListener listener)
	{
		this.taskListener = listener;
	}
	
	/**
	 * 获取最大并发线程数
	 * @return
	 */
	public int getMaxThreadNum()
	{
		return maxThreadNum;
	}

	/**
	 * 设置最大并发线程数
	 * @param maxThreadNum
	 */
	public void setMaxThreadNum(int maxThreadNum)
	{
		this.maxThreadNum = maxThreadNum;
	}

	/**
	 * 该类作用及功能说明: 默认任务队列监听
	 * @date 2014年6月25日
	 */
	public class DefaultTaskListener implements ITaskListener
	{
		@Override
		public synchronized void onTaskStart()
		{
			LogTools.d(LOG_TAG, "[Method:DefaultTaskListener->onTaskStart]");
		}

		@Override
		public synchronized void onTaskChanged(int progress)
		{
			LogTools.d(LOG_TAG, "[Method:DefaultTaskListener->onTaskChanged] progress: " + progress);
		}

		@Override
		public synchronized void onTaskFinished(int taskId)
		{
			LogTools.d(LOG_TAG, "[Method:DefaultTaskListener->onTaskFinished] taskId: " + taskId);
			TGTaskQueue.this.removeTask((Integer)taskId);
		}

		@Override
		public synchronized void onTaskError(int taskId, int code, Object msg)
		{
			LogTools.p(LOG_TAG, "[Method:DefaultTaskListener->onTaskError] taskId: " + taskId + "; errorCode: " + code);
			switch (code)
			{
				case TaskError.LOCK_DISPATER_CODE:
					
					//若当前状态不为暂停状态，则立即暂停所有任务
					if(state != MPQueueState.PAUSE)
					{
						//先上锁
						TGTaskQueue.this.lock(new onLockListener()
						{
							@Override
							public void onLockSuccess()
							{
								TGTaskQueue.this.pauseTaskQueue();
							}
							
							@Override
							public void onLockFailed()
							{
							}
						});
						
						//执行解锁操作
						TGTaskQueue.this.unLock(new onUnLockListener()
						{
							@Override
							public void onUnLockSuccess()
							{
								TGTaskQueue.this.setState(MPQueueState.WAITING);
								TGTaskQueue.this.executeNextTask();
							}
							
							@Override
							public void onUnLockFailed()
							{
							}
						});
					}
					else
					{
						//当前队列为暂停状态，暂停当前任务
						TGTaskQueue.this.pauseTask(taskId);
					}
					
					break;

				default:
					TGTaskQueue.this.removeTask((Integer)taskId);
					break;
			}
		}

		@Override
		public synchronized void onTaskCancel(int taskId)
		{
		}
		
		@Override
		public synchronized void onTaskPause(int taskId)
		{
			
		}
	}
}
