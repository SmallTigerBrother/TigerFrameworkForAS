package com.mn.tiger.task;

import android.content.Context;
import android.os.Bundle;

import com.mn.tiger.log.Logger;
import com.mn.tiger.task.dispatch.TGDispatcher;
import com.mn.tiger.task.result.TGTaskResultHandler;
import com.mn.tiger.task.thread.TGHttpDaemonThread;
import com.mn.tiger.task.utils.TGTaskIDCreator;

import java.util.HashMap;

/**
 * 任务管理器，单例类
 */
public class TGTaskManager
{
	private static final Logger LOG = Logger.getLogger(TGTaskManager.class);

	/**
	 * Manager单例对象
	 */
	private volatile static TGTaskManager instance;

	/**
	 * 任务动作: 开始任务
	 */
	public static final int TASK_START_MODE = 1;

	/**
	 * 任务动作: 取消任务
	 */
	public static final int TASK_CANCEL_MODE = 2;

	/**
	 * 任务动作: 暂停任务，任务会保留在任务队列中
	 */
	public static final int TASK_PAUSE_MODE = 3;

	/**
	 * 获取对象实例
	 */
	public static TGTaskManager getInstance()
	{
		if(null == instance)
		{
			synchronized (TGTaskManager.class)
			{
				if (null == instance)
				{
					instance = new TGTaskManager();
				}
			}
		}

		return instance;
	}

	private TGTaskManager()
	{

	}

	/**
	 * 该方法的作用: 添加开始任务
	 */
	public int startTask(Context context, TGTaskParams taskParams)
	{
		LOG.d("[Method:startTask]");
		if(taskParams == null)
		{
			LOG.w("[Method:startTask] the taskParams is null ");
			return -1;
		}

		taskParams.setTaskMode(TASK_START_MODE);

		TGTask task = createTask(context, taskParams);
		// 分发并执行任务
		if (null != task)
		{
			TGDispatcher.getInstance().dispatchTask(task);
			return task.getTaskID();
		}

		return -1;
	}

	/**
	 * 启动有序任务列表
	 */
	public int startScheduleTaskList(Context context, TGScheduleTaskList taskList)
	{
		LOG.d("[Method:startScheduleTaskList]");
		if(null == taskList || taskList.isEmpty())
		{
			LOG.w("[Method:startScheduleTaskList] the taskList is null or empty");
			return -1;
		}

		taskList.setTaskMode(TASK_START_MODE);
		TGDispatcher.getInstance().dispatchScheduleTaskList(taskList);
		return taskList.getTaskListId();
	}

	/**
	 * 取消任务
	 */
	public void cancelTask(int taskId, int taskType)
	{
		LOG.d("[Method:cancelTask] taskID == " + taskId + "  taskType == " + taskType);
		if(taskId < 0)
		{
			LOG.w("[Method:cancelTask] invalid taskID == " + taskId + "  taskType == " + taskType);
			return;
		}

		if(taskType != TaskType.TASK_TYPE_HTTP)
		{
			// 结束任务并删除
			TGDispatcher.getInstance().cancelTask(taskId, taskType);
		}
		else
		{
			TGHttpDaemonThread.getInstance().cancelTask(taskId);
		}
	}

	/**
	 * 取消有序任务列表
	 */
	public void cancelScheduleTaskList(Context context, TGScheduleTaskList taskList)
	{
		LOG.d("[Method:startScheduleTaskList]");
		if(null == taskList || taskList.isEmpty())
		{
			LOG.w("[Method:startScheduleTaskList] the taskList is null or empty");
			return;
		}

		taskList.setTaskMode(TASK_CANCEL_MODE);
		// 取消任务并删除
		TGDispatcher.getInstance().cancelScheduleTaskList(taskList.getTaskListId());
	}

	/**
	 * 停止任务
	 */
	public void pauseTask(int taskId, int taskType)
	{
		LOG.d("[Method:pauseTask] taskID == " + taskId + " taskType == " + taskType);
		if(taskId < 0)
		{
			LOG.w("[Method:pauseTask] invalid taskID == " + taskId + " taskType == " + taskType);
			return;
		}

		// 暂停任务
		TGDispatcher.getInstance().pauseTask(taskId, taskType);
	}

	/**
	 * 该方法的作用:
	 */
	public static TGTask createTask(Context context, TGTaskParams taskParams)
	{
		LOG.d("[Method:createTask]");
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
			LOG.e("[method:createTask]", e);
		}
		return task;
	}

	/**
	 * 该方法的作用:创建任务参数
	 */
	public static TGTaskParams createTaskParams(HashMap<String, String> params, String taskClsName,
												TGTaskResultHandler taskResultHandler)
	{
		return createTaskParams(params, taskClsName, taskResultHandler, TGTaskIDCreator.createNextTaskID());
	}

	/**
	 * 该方法的作用:创建任务参数
	 */
	public static TGTaskParams createTaskParams(HashMap<String, String> params, String taskClsName,
												TGTaskResultHandler taskResultHandler, int taskId)
	{
		TGTaskParams taskParams = new TGTaskParams();
		taskParams.setMapParams(params);
		taskParams.setTaskClsName(taskClsName);

		taskParams.setTaskID(taskId);
		taskParams.setTaskMode(TASK_START_MODE);
		if (null != taskResultHandler)
		{
			taskParams.setMessenger(taskResultHandler.getMessenger());
		}
		else
		{
			LOG.w("[Method:createTaskParams] the taskResultHandler is null, taskId == " + taskId);
		}

		return taskParams;
	}

	/**
	 * 该方法的作用:创建任务参数
	 */
	public static TGTaskParams createTaskParams(String param, String taskClsName,
												TGTaskResultHandler taskResultHandler)
	{
		return createTaskParams(param, taskClsName, taskResultHandler, TGTaskIDCreator.createNextTaskID());
	}

	/**
	 * 该方法的作用:创建任务参数
	 * @param taskId 指定的任务ID号
	 */
	public static TGTaskParams createTaskParams(String param, String taskClsName,
												TGTaskResultHandler taskResultHandler, int taskId)
	{
		TGTaskParams taskParams = new TGTaskParams();
		taskParams.setStringParams(param);
		taskParams.setTaskClsName(taskClsName);

		taskParams.setTaskID(taskId);
		taskParams.setTaskMode(TASK_START_MODE);
		if (null != taskResultHandler)
		{
			taskParams.setMessenger(taskResultHandler.getMessenger());
		}
		else
		{
			LOG.w("[Method:createTaskParams] the taskResultHandler is null, taskId == " + taskId);
		}

		return taskParams;
	}

	/**
	 * 该方法的作用:创建任务参数
	 */
	public static TGTaskParams createTaskParams(Bundle params, String taskClsName,
												TGTaskResultHandler taskResultHandler)
	{
		return createTaskParams(params, taskClsName, taskResultHandler, TGTaskIDCreator.createNextTaskID());
	}

	/**
	 * 该方法的作用:创建任务参数
	 * @param taskId 指定的任务ID号
	 */
	public static TGTaskParams createTaskParams(Bundle params, String taskClsName,
												TGTaskResultHandler taskResultHandler, int taskId)
	{
		TGTaskParams taskParams = new TGTaskParams();
		taskParams.setBundleParams(params);
		taskParams.setTaskClsName(taskClsName);

		taskParams.setTaskID(taskId);
		taskParams.setTaskMode(TASK_START_MODE);
		if (null != taskResultHandler)
		{
			taskParams.setMessenger(taskResultHandler.getMessenger());
		}
		else
		{
			LOG.w("[Method:createTaskParams] the taskResultHandler is null, taskId == " + taskId);
		}

		return taskParams;
	}
}
