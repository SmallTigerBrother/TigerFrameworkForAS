package com.mn.tiger.robot.policy;

import com.mn.tiger.log.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tiger on 16/8/23.
 */
public class SchedulerRunPolicy implements GardenRunPolicy
{
    private static final Logger LOG = Logger.getLogger(SchedulerRunPolicy.class);

    private ScheduledExecutorService schedulers = Executors.newScheduledThreadPool(1);

    @Override
    public void run(Runnable runnable)
    {
        LOG.i("[Method:run]");
        schedulers.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown()
    {
        schedulers.shutdown();
    }
}
