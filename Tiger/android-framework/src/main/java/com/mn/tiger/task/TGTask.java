package com.mn.tiger.task;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.mn.tiger.log.Logger;
import com.mn.tiger.task.result.TGTaskResult;

/**
 * 该类作用及功能说明
 * 可派发任务
 */
public class TGTask implements Runnable, Cloneable
{
	private static final Logger LOG = Logger.getLogger(TGTask.class);

	/**
	 * 任务ID
	 */
	private int taskID = -1;

	/**
	 * 任务状态
	 */
	private TGTaskState state = TGTaskState.WAITING;

	/**
	 * 任务回调启动者的messenger
	 */
	private Messenger messenger;

	/**
	 * 任务执行参数
	 */
	private Object params;

	/**
	 * 任务类型
	 */
	private int type;

	/**
	 * 上下文信息
	 */
	private Context context;

	/**
	 * 任务队列变化监听器
	 */
	protected ITaskListener taskListener;

	/**
	 * 错误码
	 */
	private int errorCode = TaskError.UNKNOWN_ERROR_CODE;

	/**
	 * 错误信息
	 */
	private String errorMsg = "unknown error";

	/**
	 * 构造函数
	 */
	public TGTask()
	{

	}

	/**
	 * 该方法的作用:
	 * 任务执行方法
	 */
	public void run()
	{
		LOG.d("[Method:run]");

		// 修改任务状态为正在运行
		state = TGTaskState.RUNNING;

		// 通知启动
		onTaskStart();

		TGTaskState state = executeOnSubThread();

		if (null == state)
		{
			throw new RuntimeException("The Task state can not be null");
		}

		switch (state)
		{
			case FINISHED:

				onTaskFinished();

				break;

			case ERROR:

				onTaskError(errorCode, errorMsg);

				break;

			case PAUSE:

				onTaskPause();

				break;

			default:
				break;
		}

		//销毁所有属性
		onDestroy();
	}

	/**
	 * 该方法的作用:
	 * 执行自身（任务真正的执行方法）
	 */
	protected TGTaskState executeOnSubThread()
	{
		return TGTaskState.FINISHED;
	}

	/**
	 * 该方法的作用:
	 * 发送任务结果
	 */
	protected void sendTaskResult(Object result)
	{
		LOG.d("[Method:sendTaskResult] taskId == " + taskID);
		if (messenger == null)
		{
			LOG.w("[Method:sendTaskResult] the messenger is null");
			return;
		}
		Message msg = Message.obtain();
		Bundle data = new Bundle();

		TGTaskResult taskResult = new TGTaskResult();
		taskResult.setTaskID(getTaskID());
		taskResult.setResult(result);
		data.putParcelable("result", taskResult);
		msg.setData(data);
		try
		{
			messenger.send(msg);
		}
		catch (RemoteException e)
		{
			LOG.e("[Method:sendTaskResult]", e);
		}
	}

	/**
	 * 该方法的作用:
	 * 暂停任务
	 */
	public final void pause()
	{
		// 修改任务状态为暂停
		state = TGTaskState.PAUSE;
		onTaskPause();
	}

	/**
	 * 该方法的作用:
	 * 取消任务
	 */
	public final void cancel()
	{
		state = TGTaskState.CANCEL;
		onTaskCancel();
	}

	/**
	 * 该方法的作用:
	 * 任务开始方法
	 */
	protected void onTaskStart()
	{
		LOG.d("[Method:onTaskStart]");
		// 通知任务执行启动
		if(state == TGTaskState.WAITING)
		{
			state = TGTaskState.RUNNING;
		}
		// 回调任务开始接口
		if(null != taskListener)
		{
			taskListener.onTaskStart();
		}
	}

	/**
	 * 该方法的作用:
	 * 任务变化方法
	 */
	protected void onTaskChanged(int progress)
	{
		LOG.d("[Method:onTaskChanged] taskId == " + taskID + " progress == " + progress);
		// 若当前任务状态为运行状态，则通知任务队列，任务执行变化
		if (state == TGTaskState.RUNNING && null != taskListener)
		{
			taskListener.onTaskChanged(progress);
		}
	}

	/**
	 * 该方法的作用:
	 * 任务完成方法
	 */
	protected void onTaskFinished()
	{
		LOG.d("[Method:onTaskFinished] taskId == " + taskID);

		// 修改任务状态为完成
		state = TGTaskState.FINISHED;

		// 回调任务完成接口
		if(null != taskListener)
		{
			taskListener.onTaskFinished(taskID);
		}
	}

	/**
	 * 该方法的作用:
	 * 任务出错方法
	 */
	protected void onTaskError(int code, Object msg)
	{
		LOG.d("[Method:onTaskError] taskId == " + taskID + " code == " + code);
		// 修改任务状态为异常
		state = TGTaskState.ERROR;

		// 回调异常接口
		if(null != taskListener)
		{
			taskListener.onTaskError(taskID, code, msg);
		}
	}

	/**
	 * 该方法的作用:
	 * 任务取消的方法
	 */
	protected void onTaskCancel()
	{
		LOG.d("[Method:onTaskCancel] taskId == " + taskID);
		// 回调取消任务接口
		if(null != taskListener)
		{
			taskListener.onTaskCancel(this.getTaskID());
		}

		onDestroy();
	}

	/**
	 *
	 * 该方法的作用: 暂停任务
	 */
	protected void onTaskPause()
	{
		LOG.d("[Method:onTaskPause] taskId == " + taskID);

		// 回调停止任务接口
		if(null != taskListener)
		{
			taskListener.onTaskPause(this.getTaskID());
		}

		onDestroy();
	}

	/**
	 * 该方法的作用: 清空回调接口，清空属性，销毁任务
	 */
	protected void onDestroy()
	{
		LOG.d("[Method:onDestroy] taskId == " + taskID);

		messenger = null;
		taskListener = null;

		context = null;
		params = null;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		TGTask task;
		try
		{
			task = this.getClass().newInstance();
			task.setTaskID(taskID);
			task.setType(type);
			task.setContext(context);
			task.setParams(params);
			task.setMessenger(messenger);
			task.setTaskListener(taskListener);

			return task;
		}
		catch (Exception e)
		{
			LOG.e("[Method:clone]", e);
		}

		return new TGTask();
	}

	/**
	 * 该方法的作用:
	 * 是否正在执行中
	 */
	public boolean isRunning()
	{
		return state == TGTaskState.RUNNING;
	}

	/**
	 * 该方法的作用:
	 * 获取任务状态
	 */
	public TGTaskState getTaskState()
	{
		return state;
	}

	/**
	 * 该方法的作用:
	 * 设置任务状态
	 */
	protected void setTaskState(TGTaskState state)
	{
		this.state = state;
	}

	/**
	 * 该方法的作用:
	 * 设置任务ID
	 */
	public void setTaskID(int taskID)
	{
		this.taskID = taskID;
	}

	/**
	 * 该方法的作用:
	 * 获取任务ID
	 */
	public Integer getTaskID()
	{
		return taskID;
	}

	/**
	 * 该方法的作用:
	 * 获取Messenger
	 */
	public Messenger getMessenger()
	{
		return messenger;
	}

	/**
	 * 该方法的作用:
	 * 设置Messenger
	 */
	public void setMessenger(Messenger messenger)
	{
		this.messenger = messenger;
	}

	/**
	 * 该方法的作用:
	 * 获取参数
	 */
	public Object getParams()
	{
		return params;
	}

	/**
	 * 该方法的作用:
	 * 设置参数
	 */
	public void setParams(Object params)
	{
		this.params = params;
	}

	protected Context getContext()
	{
		return context;
	}

	public void setContext(Context context)
	{
		this.context = context;
	}

	/**
	 * 该方法的作用:
	 * 获取类型
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * 该方法的作用:
	 * 设置任务类型
	 */
	public void setType(int type)
	{
		this.type = type;
	}

	/**
	 * 该方法的作用:
	 * 获取任务变化监听器
	 */
	public ITaskListener getTaskListener()
	{
		return taskListener;
	}

	/**
	 * 该方法的作用:
	 * 设置任务变化监听器
	 */
	public void setTaskListener(ITaskListener taskListener)
	{
		this.taskListener = taskListener;
	}

	/**
	 * 该方法的作用:
	 * 设置任务错误信息
	 */
	public void setTaskError(int errorCode, String errorMsg)
	{
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	/**
	 * 该类作用及功能说明
	 * 任务错误
	 */
	public static class TaskError
	{
		/**
		 * 请求超时错误
		 */
		public static final int SOCKET_TIMEOUT_CODE = 0x00000011;

		/**
		 * 派发器锁定错误
		 */
		public static final int LOCK_DISPATER_CODE = 0x00000013;

		/**
		 * Messenger为null的错误
		 */
		public static final int MESSENGER_NULLPOINTER_ERROR_CODE = 0x00000014;

		/**
		 * 远程调用异常
		 */
		public static final int REMOTEEXCEPTION_CODE = 0x00000015;

		/**
		 * 未知异常
		 */
		public static final int UNKNOWN_ERROR_CODE = 0x00000017;

		/**
		 * 网络连接不可用异常
		 */
		public static final int NETWORK_UNAVAILABLE_CODE = 0x00000018;
	}

	/**
	 * 该类作用及功能说明 任务状态
	 */
	public enum TGTaskState
	{
		/**
		 * 等待
		 */
		WAITING,
		/**
		 * 正在运行
		 */
		RUNNING,
		/**
		 * 已结束
		 */
		FINISHED,
		/**
		 * 已暂停
		 */
		PAUSE,
		/**
		 * 出错
		 */
		ERROR,

		/**
		 * 取消
		 */
		CANCEL
	}
}
