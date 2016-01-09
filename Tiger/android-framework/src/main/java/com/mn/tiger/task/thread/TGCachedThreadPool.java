package com.mn.tiger.task.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dalang on 2016/1/9.
 */
public class TGCachedThreadPool extends TGThreadPool
{
    /**
     * executor
     */
    private ExecutorService executorService;

    /**
     * 默认线程数
     */
    private static final int MAX_POOL_SIZE = 256;

    public TGCachedThreadPool(int poolSize)
    {
        executorService = new ThreadPoolExecutor(3, MAX_POOL_SIZE,
                120L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    /**
     * 该方法的作用:
     * 执行线程
     * @date 2014年8月23日
     * @param runnale
     */
    public void execute(Runnable runnale)
    {
        executorService.execute(runnale);
    }

    /**
     * 该方法的作用:
     * 销毁
     * @date 2014年8月23日
     */
    public void destroy()
    {
        executorService.shutdownNow();
    }
}
