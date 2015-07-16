package com.mn.tiger.task.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TGLooper
{
	private ConcurrentLinkedQueue<Object> taskQueue = new ConcurrentLinkedQueue<Object>();
	
	private Thread internalThread = new Thread()
	{
		public void run() 
		{
			while (true)
			{
				//TODO 多线程控制
				if(taskQueue.size() > 0)
				{
					Object object = taskQueue.poll();
					if(null != object)
					{
						execute(object);
					}
					else
					{
						continue;
					}
				}
				else
				{
					try
					{
						this.wait();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		};
	};
	
	public TGLooper()
	{
		internalThread.start();
	}
	
	public void execute(Object object)
	{
		enqueue(object);
		loop();
	}
	
	private synchronized void enqueue(Object object)
	{
		if (!taskQueue.contains(object))
		{
			taskQueue.offer(object);
		}
	}
	
	public synchronized void cancel(Object object)
	{
		if(taskQueue.contains(object))
		{
			taskQueue.remove(object);
		}
		else
		{
			//调用各个Looper执行清理操作
		}
	}
	
	public void loop()
	{
		if(!internalThread.isAlive())
		{
			internalThread.notify();
		}
	}
}
