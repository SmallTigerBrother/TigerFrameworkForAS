package com.mn.tiger.task.invoke;

import android.content.Context;

import com.mn.tiger.log.LogTools;
import com.mn.tiger.task.TGScheduleTaskList;
import com.mn.tiger.task.TGTask;
import com.mn.tiger.task.TGTaskManager;
import com.mn.tiger.task.dispatch.TGDispatcher;

/**
 * 该类作用及功能说明 Android任务请求类
 * 
 * @date 2014年3月18日
 */
public class TGTaskInvoker
{
	/**
	 * 日志标签
	 */
	protected static final String LOG_TAG = TGTaskInvoker.class.getSimpleName();
	
	/**
	 * 构造函数
	 * @date 2014年7月26日
	 * @param context
	 */
	public TGTaskInvoker()
	{
	}

	/**
	 * 
	 * 该方法的作用: 任务调度
	 * @date 2014年7月26日
	 * @param taskParams
	 * @return
	 */
	public int invokeTask(Context context, TGTaskParams taskParams)
	{
		LogTools.p(LOG_TAG, "[Method:invoke]");
		TGTask task = null;
		switch (taskParams.getTaskMode())
		{
			case TGTaskManager.TASK_START_MODE:
				// 创建任务
				task = createTask(context, taskParams);
				// 分发并执行任务
				if (null != task)
				{
					TGDispatcher.getInstance().dispatchTask(task);
				}
				break;
			case TGTaskManager.TASK_CANCEL_MODE:
				// 结束任务并删除
				TGDispatcher.getInstance().cancelTask(taskParams.getTaskID(),
						taskParams.getTaskType());
				break;
			case TGTaskManager.TASK_PAUSE_MODE:
				// 结束任务并删除
				TGDispatcher.getInstance().pauseTask(taskParams.getTaskID(),
						taskParams.getTaskType());
				break;
			default:
				break;
		}
		
		// 返回任务id
		if(task == null || task.getTaskID() < 0)
		{
			return -1;
		}
		else
		{
			return task.getTaskID();
		}
	}
	
	/**
	 * 请求执行有序任务列表
	 * @param context
	 * @param taskList
	 * @return
	 */
	public int invokeScheduleTaskList(Context context, TGScheduleTaskList taskList)
	{
		LogTools.p(LOG_TAG, "[Method:invoke]");
		switch (taskList.getTaskMode())
		{
			case TGTaskManager.TASK_START_MODE:
				TGDispatcher.getInstance().dispatchScheduleTaskList(taskList);
				break;
				
			case TGTaskManager.TASK_CANCEL_MODE:
				// 结束任务并删除
				TGDispatcher.getInstance().cancelScheduleTaskList(taskList.getTaskListId());
				break;
				
			case TGTaskManager.TASK_PAUSE_MODE:
				// 结束任务并删除
				throw new RuntimeException("ScheduleTaskQueue can not be paused, please use cancel method!");
				
			default:
				break;
		}
		
		// 返回任务id
		return taskList.getTaskListId();
	}
	
	/**
	 * 该方法的作用:
	 * @date 2014年5月16日
	 * @param taskParams
	 * @return
	 */
	public static TGTask createTask(Context context, TGTaskParams taskParams)
	{
		LogTools.d(LOG_TAG, "[Method:createTask]");
		TGTask task = null;
		try
		{
			task = (TGTask) Class.forName(taskParams.getTaskClsName()).newInstance();
			task.setMessenger(taskParams.getMessenger());
			task.setTaskID(taskParams.getTaskID());
			task.setType(taskParams.getTaskType());
			task.setParams(taskParams.getParams());
			task.setContext(context);
			return task;
		}
		catch (Exception e)
		{
			LogTools.e(LOG_TAG, "[method:createTask] create task error.", e);
		}
		return task;
	}
}
