package com.mn.tiger.task.queue;

import android.util.SparseArray;

import com.mn.tiger.log.Logger;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.task.queue.TGLock.onLockListener;
import com.mn.tiger.task.queue.TGLock.onUnLockListener;

import java.util.LinkedList;

/**
 *
 * 该类作用及功能说明: 任务队列
 */
public abstract class AbsTaskQueue extends LinkedList<Integer>
{
	private static final Logger LOG = Logger.getLogger(AbsTaskQueue.class);

	private static final long serialVersionUID = 1L;

	/**
	 * 任务容器
	 */
	private SparseArray<TGTask> taskArray;

	/**
	 * 任务分发管理锁
	 */
	private TGLock lock;

	/**
	 * 构造函数
	 */
	public AbsTaskQueue()
	{
		taskArray = new SparseArray<TGTask>();
	}

	/**
	 *
	 * 该方法的作用: 排序队列中的任务
	 */
	protected abstract void sortTaskQueue();

	/**
	 *
	 * 该方法的作用: 执行下一个任务
	 */
	public abstract void executeNextTask();

	/**
	 *
	 * 该方法的作用: 移除任务
	 */
	@Override
	public boolean remove(Object object)
	{
		LOG.d("[Method:remove] taskId == " + object.toString());
		// 从队列中移除任务
		getTaskArray().remove((Integer) object);
		return super.remove(object);
	}

	/**
	 * 取消某个任务
	 */
	public abstract boolean cancelTask(int taskId);

	/**
	 * 该方法的作用:
	 * 将任务添加到队列中
	 */
	public void addLast(TGTask task)
	{
		LOG.d("[Method:addLast] taskId == " + task.getTaskID());
		this.addLast(task.getTaskID());
		getTaskArray().put(task.getTaskID(), task);
	}

	/**
	 * 该方法的作用: 暂停任务队列
	 */
	public void pauseTaskQueue()
	{

	}

	/**
	 * 该方法的作用: 重启任务队列
	 */
	public void restart()
	{

	}

	/**
	 * 该方法的作用:
	 * 取消所有任务
	 */
	public void cancelAllTasks()
	{
		LOG.d("[Method:cancelAllTasks]");
		//删除所有任务
		while (this.size() > 0)
		{
			this.getTask(this.getFirst()).cancel();
		}
	}

	/**
	 * 该方法的作用: 暂停某个指定任务
	 */
	public abstract boolean pauseTask(int taskId);

	@Override
	public void clear()
	{
		LOG.d("[Method:clear]");
		synchronized (this)
		{
			super.clear();
			getTaskArray().clear();
		}
	}

	/**
	 * 该方法的作用:
	 * 根据ID获取任务
	 */
	public TGTask getTask(int taskId)
	{
		return getTaskArray().get(taskId);
	}

	/**
	 * 该方法的作用:
	 * 获取任务列表
	 */
	public SparseArray<TGTask> getTaskArray()
	{
		if(null == taskArray)
		{
			taskArray = new SparseArray<TGTask>();
		}
		return taskArray;
	}

	/**
	 * 该方法的作用:
	 * 设置任务列表
	 */
	public void setTaskArray(SparseArray<TGTask> taskArray)
	{
		this.taskArray = taskArray;
	}

	/**
	 * 该方法的作用: 对分发器加锁，暂停所有已派发任务
	 */
	public void lock(final onLockListener onLockListener)
	{
		LOG.d("[Method:lock]");
		getLock().lock(new onLockListener()
		{
			@Override
			public void onLockSuccess()
			{
				LOG.i("[Method:lock:onLockSuccess]");
				if(null != onLockListener)
				{
					onLockListener.onLockSuccess();
				}
			}

			@Override
			public void onLockFailed()
			{
				LOG.i("[Method:lock:onLockFailed]");
				if(null != onLockListener)
				{
					onLockListener.onLockSuccess();
				}
			}
		});
	}

	/**
	 * 该方法的作用: 对分发器解锁
	 */
	public void unLock(final onUnLockListener onUnLockListener)
	{
		LOG.d("[Method:unLock]");
		getLock().unLock(new onUnLockListener()
		{
			@Override
			public void onUnLockSuccess()
			{
				LOG.d("[Method:lock]onUnLockSuccess");
				if(null != onUnLockListener)
				{
					onUnLockListener.onUnLockSuccess();
				}
			}

			@Override
			public void onUnLockFailed()
			{
				LOG.d("[Method:lock]onUnLockFailed");
				if(null != onUnLockListener)
				{
					onUnLockListener.onUnLockFailed();
				}
			}
		});
	}

	/**
	 * 该方法的作用: 获取锁
	 */
	public TGLock getLock()
	{
		if(null == lock)
		{
			lock = new TGLock();
		}

		return lock;
	}

	/**
	 * 该方法的作用: 设置锁
	 */
	public void setLock(TGLock lock)
	{
		this.lock = lock;
	}

	/**
	 * 该类作用及功能说明
	 * 队列状态
	 */
	public enum TGQueueState
	{
		/**
		 * 正在等待
		 */
		WAITING,
		/**
		 * 正在运行
		 */
		RUNNING,
		/**
		 * 暂停
		 */
		PAUSE
	}
}
