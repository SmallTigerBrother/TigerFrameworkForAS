package com.mn.tiger.task.queue;

import com.mn.tiger.log.Logger;
import com.mn.tiger.task.ITaskListener;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.task.TGTask.TGTaskState;
import com.mn.tiger.task.TGTask.TaskError;
import com.mn.tiger.task.queue.TGLock.onLockListener;
import com.mn.tiger.task.queue.TGLock.onUnLockListener;
import com.mn.tiger.task.thread.TGFixedThreadPool;
import com.mn.tiger.task.thread.TGThreadPool;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 该类作用及功能说明: 可并行执行多个任务的队列
 *
 * @date 2014年6月25日
 */
public class TGTaskQueue extends AbsTaskQueue
{
	private static final Logger LOG = Logger.getLogger(TGTaskQueue.class);

	/**
	 * @date 2014年6月25日
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 该队列中正在运行任务个数
	 */
	protected List<Integer> runningTaskList = new ArrayList<Integer>();

	/**
	 * 队列状态
	 */
	private TGQueueState state = TGQueueState.WAITING;

	/**
	 * 任务监听
	 */
	protected volatile ITaskListener taskListener;

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
		//若当前队列状态为暂停，则直接退出
		if(state == TGQueueState.PAUSE)
		{
			LOG.w("[Method:executeNextTask] the queue has been paused!");
			return;
		}

		// Task按运行状态和weight排序
		sortTaskQueue();

		// 获取队列中的任务总数
		int totalTask = getTaskArray().size();
		LOG.i("[Method:executeNextTask]" + " totalTaskCount: " + totalTask);
		LOG.i("[Method:executeNextTask] runningTaskNum: " + runningTaskList.size());
		// 若已无任务，队列状态改为等待
		if(totalTask <= 0)
		{
			state = TGQueueState.WAITING;
		}

		// 队列中有任务，取最大个数等待任务执行
		boolean loop = true;
		int index = 0;

		do
		{
			if(totalTask > index && runningTaskList.size() <= getMaxThreadNum())
			{
				TGTask runTask = getTaskArray().get(this.get(index));
				if (runTask != null && runTask.getTaskState() == TGTaskState.WAITING)
				{
					runTask.executeTask(threadPool);
					runningTaskList.add(runTask.getTaskID());
					state = TGQueueState.RUNNING;

					LOG.i("[Method:executeNextTask] runningTaskNum: " + runningTaskList.size());
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
	public void addLast(TGTask task)
	{
		synchronized (this)
		{
			task.setTaskListener(getTaskListener());
			super.addLast(task);
		}
	}

	/**
	 * 该方法的作用: 移除任务
	 * @date 2014年6月25日
	 */
	protected boolean removeTask(Integer taskId)
	{
		// 获取被移除任务
		TGTask task = getTaskArray().get(taskId.intValue());
		if(task != null)
		{
			// 如果是正在运行的任务，队列中正在运行任务减1
			if(runningTaskList.size() > 0 && runningTaskList.contains(taskId))
			{
				synchronized (this)
				{
					runningTaskList.remove(taskId);
				}
			}
			//销毁任务
			task = null;
		}

		boolean result = false;
		// 任务移除队列
		synchronized (this)
		{
			result =  super.remove(taskId);
		}

		// 执行队列中下一个等待任务
		LOG.i("[Method:onRemove] queue size : " + TGTaskQueue.this.size());
		this.executeNextTask();

		return result;
	}

	/**
	 * 该方法的作用: 暂停任务队列
	 *
	 * @date 2014年3月17日
	 */
	public void pauseTaskQueue()
	{
		LOG.i("[Method:pauseTaskQueue]");
		//设置队列运行状态为PAUSE
		state = TGQueueState.PAUSE;

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
	public boolean pauseTask(int taskId)
	{
		LOG.i("[Method:pauseTask] taskId == " + taskId);

		//将当前运行任务的Clone对象放入队列中
		TGTask task = getTaskArray().get(taskId);
		if(null != task)
		{
			//克隆一个新任务加入到队列中
			cloneTaskEnQueue(task);
			// 将当前运行的任务暂停
			task.pause();
			//从队列中移除原有任务
			return this.removeTask(task.getTaskID());
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
			LOG.e("[Method:cloneTaskEnQueue]", e);
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
	public void restart()
	{
		LOG.d("[Method:restart]");

		//设置队列运行状态为WAITING
		state = TGQueueState.WAITING;
		//执行下一个任务
		executeNextTask();
	}

	/**
	 * 取消某个任务
	 * @param taskId
	 * @return
	 */
	public boolean cancelTask(int taskId)
	{
		LOG.i("[Method:cancelTask] taskId == " + taskId + "  ThreadId == " + Thread.currentThread().getId());

		TGTask task = getTaskArray().get(taskId);
		if(null != task)
		{
			task.cancel();
			return TGTaskQueue.this.removeTask(taskId);
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
		state = TGQueueState.WAITING;
	}

	/**
	 * 该方法的作用:
	 * 获取任务状态
	 * @date 2014年8月22日
	 * @return
	 */
	public TGQueueState getState()
	{
		return state;
	}

	/**
	 * 该方法的作用:
	 * 设置任务状态
	 * @date 2014年8月22日
	 * @param state
	 */
	public void setState(TGQueueState state)
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
			taskListener = new DefaultTaskListener();
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
		public void onTaskStart()
		{
			LOG.d("[Method:DefaultTaskListener:onTaskStart]");
		}

		@Override
		public void onTaskChanged(int progress)
		{
			LOG.d("[Method:DefaultTaskListener:onTaskChanged] progress == " + progress);
		}

		@Override
		public void onTaskFinished(int taskId)
		{
			LOG.d("[Method:DefaultTaskListener:onTaskFinished] taskId == " + taskId);
			TGTaskQueue.this.removeTask(taskId);
		}

		@Override
		public void onTaskError(int taskId, int code, Object msg)
		{
			LOG.w("[Method:DefaultTaskListener:onTaskError] taskId == " + taskId + "; errorCode == " + code);
			switch (code)
			{
				case TaskError.LOCK_DISPATER_CODE:

					//若当前状态不为暂停状态，则立即暂停所有任务
					if(state != TGQueueState.PAUSE)
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
								TGTaskQueue.this.setState(TGQueueState.WAITING);
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
					TGTaskQueue.this.removeTask(taskId);
					break;
			}
		}

		@Override
		public void onTaskCancel(int taskId)
		{
			LOG.d("[Method:DefaultTaskListener:onTaskCancel] taskId == " + taskId);
		}

		@Override
		public void onTaskPause(int taskId)
		{
			LOG.d("[Method:DefaultTaskListener:onTaskPause] taskId == " + taskId);
		}
	}
}
