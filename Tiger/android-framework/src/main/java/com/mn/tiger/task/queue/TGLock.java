package com.mn.tiger.task.queue;

import android.content.Context;

import com.mn.tiger.log.Logger;

/**
 * 该类作用及功能说明
 * 分发器管理锁
 */
public class TGLock
{
	private static final Logger LOG = Logger.getLogger(TGLock.class);

	private Context context;

	/**
	 * 当前锁定状态
	 */
	private MPLockState state;

	public TGLock()
	{
		//初始化锁，刚开始为已解锁状态
		this.state = MPLockState.UNLOCKED;
	}

	public TGLock(Context context)
	{
		this.context = context;

		//初始化锁，刚开始为已解锁状态
		this.state = MPLockState.UNLOCKED;
	}

	/**
	 * 该方法的作用:
	 * 上锁
	 */
	public void lock(onLockListener onLockListener)
	{
		LOG.d("[Method:lock]");

		//锁定必须在主线程执行
		this.state = MPLockState.LOCKING;
		//执行锁定操作

		if(null != onLockListener)
		{
			onLockListener.onLockSuccess();
		}

		//锁定成功后设置为已加锁状态
		this.state = MPLockState.LOCKED;

	}

	/**
	 * 该方法的作用:
	 * 解锁
	 */
	public void unLock(onUnLockListener onUnLockListener)
	{
		LOG.d("[Method:unLock]");

		this.state = MPLockState.UNLOCKING;
		//执行解锁操作

		if(null != onUnLockListener)
		{
			onUnLockListener.onUnLockSuccess();
		}

		//解锁成功设置为已解锁状态
		this.state = MPLockState.UNLOCKED;
	}

	/**
	 * 该方法的作用:
	 * 获取当前锁定状态
	 */
	public MPLockState getState()
	{
		return state;
	}

	/**
	 * 该方法的作用:
	 * 设置当前锁定状态
	 */
	protected void setState(MPLockState state)
	{
		this.state = state;
	}

	public void setContext(Context context)
	{
		this.context = context;
	}

	public Context getContext()
	{
		return context;
	}

	/**
	 * 该类作用及功能说明
	 * 加锁回调接口
	 */
	public interface onLockListener
	{
		/**
		 * 该方法的作用:
		 * 加锁成功
		 */
		void onLockSuccess();

		/**
		 * 该方法的作用:
		 * 加锁失败
		 */
		void onLockFailed();
	}

	/**
	 * 该类作用及功能说明
	 * 解锁回调接口
	 */
	public interface onUnLockListener
	{
		/**
		 * 该方法的作用:
		 * 解锁成功
		 */
		void onUnLockSuccess();

		/**
		 * 该方法的作用:
		 * 解锁失败
		 */
		void onUnLockFailed();
	}

	/**
	 * 该类作用及功能说明
	 * 锁定状态
	 */
	public enum MPLockState
	{
		/**
		 * 已锁定
		 */
		LOCKED,
		/**
		 * 正在解锁
		 */
		LOCKING,
		/**
		 * 已解锁
		 */
		UNLOCKED,
		/**
		 * 正在解锁
		 */
		UNLOCKING
	}
}
