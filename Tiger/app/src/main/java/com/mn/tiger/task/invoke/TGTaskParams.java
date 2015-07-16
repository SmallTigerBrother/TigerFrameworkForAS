package com.mn.tiger.task.invoke;

import java.io.Serializable;
import java.util.HashMap;

import android.os.Bundle;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 任务参数
 */
public class TGTaskParams implements Serializable, Parcelable
{
	/**
	 * @date 2014年4月14日
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 内部存储的Bundle
	 */
	private Bundle data = null;
	
	/**
	 * 参数类型——Map
	 */
	public static final int PARAM_TYPE_MAP = 1;

	/**
	 * 参数类型——String
	 */
	public static final int PARAM_TYPE_STRING = 2;

	/**
	 * 参数类型——Bundle
	 */
	public static final int PARAM_TYPE_BUNDLE = 3;

	/**
	 * 参数类型——未知（未知类型会出现异常）
	 */
	public static final int PARAM_TYPE_UNKNOW = 4;

	/**
	 * 任务运行模式
	 */
	private int taskMode = 1;
	
	/**
	 * 任务类型
	 */
	private int taskType = 0;
	
	/**
	 * 任务ID
	 */
	private int taskID;

	/**
	 * 回调信使
	 */
	private Messenger messenger = null;

	public TGTaskParams()
	{
		this.data = new Bundle();
	}

	protected TGTaskParams(Parcel source)
	{
		taskMode = source.readInt();
		taskType = source.readInt();
		taskID = source.readInt();
		data = source.readBundle();
		messenger = source.readParcelable(Messenger.class.getClassLoader());
	}
	
	/**
	 * 设置Map类型的参数
	 * @param params
	 */
	public void setMapParams(HashMap<String, String> params)
	{
		data.putInt("paramType", TGTaskParams.PARAM_TYPE_MAP);
		data.putSerializable("params", params);
	}
	
	/**
	 * 设置String类型的参数
	 * @param params
	 */
	public void setStringParams(String params)
	{
		data.putInt("paramType", TGTaskParams.PARAM_TYPE_STRING);
		data.putString("params", params);
	}
	
	/**
	 * 设置Bundle类型的参数
	 * @param params
	 */
	public void setBundleParams(Bundle params)
	{
		data.putInt("paramType", TGTaskParams.PARAM_TYPE_BUNDLE);
		data.putParcelable("params", params);
	}
	
	/**
	 * 设置任务类的类名
	 * @param taskClsName
	 */
	public void setTaskClsName(String taskClsName)
	{
		data.putString("taskClassName", taskClsName);
	}
	
	/**
	 * 获取任务类的类名
	 * @return
	 */
	public String getTaskClsName()
	{
		return data.getString("taskClassName");
	}
	
	/**
	 * 获取参数
	 * @return
	 */
	public Object getParams()
	{
		switch (data.getInt("paramType", TGTaskParams.PARAM_TYPE_UNKNOW))
		{
			case TGTaskParams.PARAM_TYPE_MAP:
				return data.getSerializable("params");
			case TGTaskParams.PARAM_TYPE_BUNDLE:
				return data.getBundle("params");
			case TGTaskParams.PARAM_TYPE_STRING:
				return data.getString("params");
			default:
				return null;
		}
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	/**
	 * 获取任务运行模式
	 * @return
	 */
	public int getTaskMode()
	{
		return taskMode;
	}

	/**
	 * 设置任务运行模式
	 * @param taskMode
	 */
	public void setTaskMode(int taskMode)
	{
		this.taskMode = taskMode;
	}

	/**
	 * 获取任务类型
	 * @return
	 */
	public int getTaskType()
	{
		return taskType;
	}

	/**
	 * 设置任务类型
	 * @param taskType
	 */
	public void setTaskType(int taskType)
	{
		this.taskType = taskType;
	}

	/**
	 * 获取任务ID
	 * @return
	 */
	public int getTaskID()
	{
		return taskID;
	}

	/**
	 * 设置任务ID
	 * @param taskID
	 */
	public void setTaskID(int taskID)
	{
		this.taskID = taskID;
	}

	/**
	 * 获取回调信使
	 * @return
	 */
	public Messenger getMessenger()
	{
		return messenger;
	}

	/**
	 * 设置回调信使
	 * @param messenger
	 */
	public void setMessenger(Messenger messenger)
	{
		this.messenger = messenger;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(taskMode);
		dest.writeInt(taskType);
		dest.writeInt(taskID);
		dest.writeBundle(data);
		
		if (messenger != null)
		{
			dest.writeParcelable(messenger, flags);
		}
	}

	/**
	 * Parcelable构建类
	 */
	public static final Parcelable.Creator<TGTaskParams> CREATOR = new Parcelable.Creator<TGTaskParams>()
	{
		@Override
		public TGTaskParams createFromParcel(Parcel source)
		{
			return new TGTaskParams(source);
		}

		@Override
		public TGTaskParams[] newArray(int size)
		{
			return new TGTaskParams[size];
		}
	};

	@Override
	public String toString()
	{
		return "MPTaskParams [data=" + data + ", taskMode=" + taskMode + ", taskType=" + taskType + ", taskID=" + taskID
				 + ", messenger=" + messenger + "]";
	}

}
