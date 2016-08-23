package com.mn.tiger.robot.policy;

/**
 * Created by Tiger on 16/8/23.
 */
public interface GardenRunPolicy
{
    void run(Runnable runnable);

    void shutdown();
}
