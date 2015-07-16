package com.mn.tiger.task.queue;

import android.content.Context;

import com.mn.tiger.log.LogTools;

/**
 * 该类作用及功能说明
 * 分发器管理锁
 * @date 2014年3月17日
 */
public class TGLock
{
	private Context context;
	
	/**
	 * 日志标签
	 */
	protected final String LOG_TAG = this.getClass().getSimpleName();
	
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
	 * @date 2014年3月17日
	 * @return
	 */
	public void lock(onLockListener onLockListener)
	{
		LogTools.p(LOG_TAG, "[Method:lock]");
		
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
	 * @date 2014年3月17日
	 * @return
	 */
	public void unLock(onUnLockListener onUnLockListener)
	{
		LogTools.p(LOG_TAG, "[Method:unLock]");
		
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
	 * @date 2014年3月17日
	 * @return
	 */
	public MPLockState getState()
	{
		return state;
	}

	/**
	 * 该方法的作用:
	 * 设置当前锁定状态
	 * @date 2014年3月17日
	 * @param state
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
	 * @date 2014年8月22日
	 */
	public static interface onLockListener
	{
		/**
		 * 该方法的作用:
		 * 加锁成功
		 * @date 2014年8月22日
		 */
		void onLockSuccess();
		
		/**
		 * 该方法的作用:
		 * 加锁失败
		 * @date 2014年8月22日
		 */
		void onLockFailed();
	}
	
	/**
	 * 该类作用及功能说明
	 * 解锁回调接口
	 * @date 2014年8月22日
	 */
	public static interface onUnLockListener
	{
		/**
		 * 该方法的作用:
		 * 解锁成功
		 * @date 2014年8月22日
		 */
		void onUnLockSuccess();
		
		/**
		 * 该方法的作用:
		 * 解锁失败
		 * @date 2014年8月22日
		 */
		void onUnLockFailed();
	}
	
	/**
	 * 该类作用及功能说明
	 * 锁定状态
	 * @date 2014年3月17日
	 */
	public static enum MPLockState
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
