package com.mn.tiger.task.thread;

import com.mn.tiger.log.Logger;
import com.mn.tiger.task.TaskType;
import com.mn.tiger.task.dispatch.TGDispatcher;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by peng on 15/11/6.
 */
public class TGHttpDaemonThread extends Thread
{
    private static final Logger LOG = Logger.getLogger(TGHttpDaemonThread.class);

    private static TGHttpDaemonThread daemonThread;

    private volatile Vector<Integer> taskIds;

    public static TGHttpDaemonThread getInstance()
    {
        if(null == daemonThread)
        {
            synchronized (TGHttpDaemonThread.class)
            {
                if(null == daemonThread)
                {
                    daemonThread = new TGHttpDaemonThread();
                }
            }
        }
        return daemonThread;
    }

    public TGHttpDaemonThread()
    {
        super();
        taskIds = new Vector<Integer>();
        this.start();
    }

    public void cancelTask(int taskId)
    {
        taskIds.add(taskId);
    }

    @Override
    public void run()
    {
        Iterator<Integer> iterator;
        while (true)
        {
            try
            {
                Thread.sleep(500);
                iterator = taskIds.iterator();
                while (iterator.hasNext())
                {
                    // 结束任务并删除
                    TGDispatcher.getInstance().cancelTask(iterator.next(), TaskType.TASK_TYPE_HTTP);
                    iterator.remove();
                }
            }
            catch (Exception e)
            {
                LOG.e("[Method:run]", e);
            }
        }
    }
}
