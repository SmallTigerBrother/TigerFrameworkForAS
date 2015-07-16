package com.mn.tiger.task;

import java.util.ArrayList;

import android.content.Context;

import com.mn.tiger.task.invoke.TGTaskInvoker;
import com.mn.tiger.task.invoke.TGTaskParams;
import com.mn.tiger.task.utils.TGTaskIDCreator;


/**
 * 有序任务列表
 * @author Dalang
 */
public class TGScheduleTaskList extends ArrayList<TGTask>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 当前队列运行模式
	 */
	private int taskMode = TGTaskManager.TASK_START_MODE;
	
	/**
	 * 任务列表ID
	 */
	private int taskListId = 0;

	public TGScheduleTaskList()
	{
		taskListId = TGTaskIDCreator.createNextTaskID();
	}
	
	public void setTaskMode(int taskMode)
	{
		this.taskMode = taskMode;
	}
	
	public int getTaskMode()
	{
		return taskMode;
	}
	
	public int getTaskListId()
	{
		return taskListId;
	}
	
	/**
	 * 通过任务参数添加新任务到列表中
	 * @param context
	 * @param taskParams
	 * @return
	 */
	public boolean addTaskByParams(Context context, TGTaskParams taskParams)
	{
		return this.add(TGTaskInvoker.createTask(context, taskParams));
	}
}
