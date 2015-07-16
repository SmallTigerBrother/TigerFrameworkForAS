package com.mn.tiger.task.dispatch;

import android.util.SparseArray;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.task.TGScheduleTaskList;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.task.queue.TGScheduleTaskQueue;
import com.mn.tiger.task.queue.TGTaskQueue;

/**
 * 该类作用及功能说明 任务分发管理器
 * 
 * @date 2014年3月17日
 */
public class TGDispatcher
{
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();

	/**
	 * 任务队列数组
	 */
	private SparseArray<TGTaskQueue> taskQueues;
	
	/**
	 * 有序任务队列数组
	 */
	private SparseArray<TGScheduleTaskQueue> scheduleTaskQueues;
	
	/**
	 * 分发器单例对象
	 */
	private static TGDispatcher dispatcher;
	
	/**
	 * 该方法的作用: 获取单例对象
	 * 
	 * @date 2014年3月17日
	 * @return
	 */
	public synchronized static TGDispatcher getInstance()
	{
		if (null == dispatcher)
		{
			dispatcher = new TGDispatcher();
		}

		return dispatcher;
	}

	/**
	 * 构造方法
	 * 
	 * @date 2014年3月17日
	 */
	private TGDispatcher()
	{
	}

	/**
	 * 该方法的作用: 分配并执行任务
	 * 
	 * @date 2014年3月17日
	 * @param task
	 */
	public void dispatchTask(TGTask task)
	{
		LogTools.p(LOG_TAG, "[Method:dispatchAndExecuteTask]");
		
		// 缓存中查找该任务应该加入的队列
		TGTaskQueue taskQueue = getTaskQueue(task.getType());
		
		// 如果任务在队列中不存在，将任务加入队列
		if(taskQueue.getTask(task.getTaskID()) == null)
		{
    		// 将任务添加到末尾
    		taskQueue.addLast(task);
		}

		// 执行下一个任务
		taskQueue.executeNextTask();
	}
	
	/**
	 * 派发有序任务列表
	 * @param taskList
	 */
	public void dispatchScheduleTaskList(TGScheduleTaskList taskList)
	{
		TGScheduleTaskQueue taskQueue = getScheduleTaskQueue(taskList.getTaskListId());
		taskQueue.setTaskList(taskList);
		taskQueue.executeNextTask();
	}
	
	/**
	 * 该方法的作用:
	 * 根据任务类型获取TaskQueue
	 * @date 2014年8月15日
	 * @param taskType
	 * @return
	 */
	protected TGTaskQueue getTaskQueue(int taskType)
	{
		if(getTaskQueues().indexOfKey(taskType) >= 0)
		{
			// 已有队列，直接使用
			return getTaskQueues().get(taskType);
		}
		else
		{
			// 队列不存在，创建新队列，并把队列加入队列集合
			TGTaskQueue taskQueue = null;
			
			switch (taskType)
			{
				case TGTask.TASK_TYPE_HTTP:
					taskQueue = new TGTaskQueue(256);
					break;
					
				case TGTask.TASK_TYPE_UPLOAD:
					taskQueue = new TGTaskQueue(3);
					break;
					
				case TGTask.TASK_TYPE_DOWNLOAD:
					taskQueue = new TGTaskQueue(5);
					break;
					
				case TGTask.TASK_TYPE_OTHER:
					taskQueue = new TGTaskQueue(256);
					break;
				default:
					throw new RuntimeException("The taskType is error taskType = " + taskType);
			}
			
			getTaskQueues().append(taskType, taskQueue);
			
			return taskQueue;
		}
	}
	
	/**
	 * 该方法的作用:
	 * 获取任务队列列表
	 * @date 2014年8月22日
	 * @return
	 */
	private SparseArray<TGTaskQueue> getTaskQueues()
	{
		if (taskQueues == null)
		{
			taskQueues = new SparseArray<TGTaskQueue>();
		}
		
		return taskQueues;
	}
	
	/**
	 * 获取所有有序任务队列
	 * @return
	 */
	private SparseArray<TGScheduleTaskQueue> getScheduleTaskQueues()
	{
		if (scheduleTaskQueues == null)
		{
			scheduleTaskQueues = new SparseArray<TGScheduleTaskQueue>();
		}
		
		return scheduleTaskQueues;
	}
	
	/**
	 * 该方法的作用:
	 * 获取有序任务队列
	 * @date 2014年8月22日
	 * @return
	 */
	private TGScheduleTaskQueue getScheduleTaskQueue(int taskListId)
	{
		TGScheduleTaskQueue taskQueue = getScheduleTaskQueues().get(taskListId);
		if(null == taskQueue)
		{
			taskQueue = new TGScheduleTaskQueue();
		}
		
		scheduleTaskQueues.append(taskListId, taskQueue);
		return taskQueue;
	}
	
	/**
	 * 该方法的作用: 执行所有任务队列
	 * @date 2014年3月17日
	 */
	public void executeAllTaskQueues()
	{
		LogTools.p(LOG_TAG, "[Method:executeAllTaskQueues]");
		TGTaskQueue taskQueue = null;
		for (int i = 0; i < getTaskQueues().size(); i++)
		{
			taskQueue = getTaskQueues().valueAt(i);
			taskQueue.restart();
		}
	}
	
	/**
	 * 该方法的作用:
	 * 暂停任务
	 * @date 2014年3月20日
	 * @param task
	 * @return
	 */
	public boolean pauseTask(int taskId, int taskType)
	{
		if(taskId < 0 || taskType < 0)
		{
			LogTools.p(LOG_TAG, "[Method:pauseTask] task info is error.");
			return false;
		}
		
		TGTaskQueue taskQueue = getTaskQueue(taskType);
		return taskQueue.pauseTask(taskId);
	}

	/**
	 * 该方法的作用: 暂停所有任务队列
	 * 
	 * @date 2014年3月17日
	 */
	public void pauseAllTaskQueues()
	{
		LogTools.p(LOG_TAG, "[Method:pauseAllTaskQueues]");
		TGTaskQueue taskQueue = null;
		for (int i = 0; i < getTaskQueues().size(); i++)
		{
			taskQueue = getTaskQueues().valueAt(i);
			taskQueue.pauseTaskQueue();
		}
	}
	
	/**
	 * 该方法的作用:
	 * 根据任务类型暂停任务队列
	 * @date 2014年9月4日
	 * @param taskType
	 */
	public void pauseTaskQueue(int taskType)
	{
		LogTools.p(LOG_TAG, "[Method:pauseTaskQueue]");
		TGTaskQueue taskQueue = getTaskQueues().get(taskType);
		
		if(null != taskQueue)
		{
			taskQueue.pauseTaskQueue();
		}
	}
	
	/**
	 * 该方法的作用:
	 * @date 2014年9月4日
	 */
	public void cancelAllTasks()
	{
		LogTools.p(LOG_TAG, "[Method:cancelAllTasks]");
		TGTaskQueue taskQueue = null;
		for (int i = 0; i < getTaskQueues().size(); i++)
		{
			taskQueue = getTaskQueues().valueAt(i);
			if(null != taskQueue)
			{
				taskQueue.cancelAllTasks();
			}
		}
		
		TGScheduleTaskQueue scheduleTaskQueue = null;
		for(int j = 0; j < getScheduleTaskQueues().size(); j++)
		{
			scheduleTaskQueue = getScheduleTaskQueues().valueAt(j);
			if(null != scheduleTaskQueue)
			{
				scheduleTaskQueue.cancelAllTasks();
			}
		}
		getScheduleTaskQueues().clear();
	}
	
	/**
	 * 该方法的作用:
	 * 取消任务
	 * @date 2014年3月20日
	 * @param task
	 * @return
	 */
	public boolean cancelTask(int taskId, int taskType)
	{
		if(taskId < 0 || taskType < 0)
		{
			LogTools.p(LOG_TAG, "[Method:cancelTask] task info is error.");
			return false;
		}
		
		TGTaskQueue taskQueue = getTaskQueue(taskType);
		return taskQueue.cancelTask(taskId);
	}
	
	/**
	 * 该方法的作用:
	 * 取消有序任务列表
	 * @date 2014年3月20日
	 * @param task
	 * @return
	 */
	public boolean cancelScheduleTaskList(int taskListId)
	{
		TGScheduleTaskQueue taskQueue = getScheduleTaskQueues().get(taskListId);
		taskQueue.cancelAllTasks();
		getScheduleTaskQueues().delete(taskListId);
		return true;
	}
	
}
