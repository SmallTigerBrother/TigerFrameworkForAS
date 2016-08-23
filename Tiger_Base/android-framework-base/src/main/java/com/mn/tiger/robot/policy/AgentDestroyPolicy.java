package com.mn.tiger.robot.policy;

import com.mn.tiger.robot.Agent;

/**
 * Created by Tiger on 16/8/23.
 */
public interface AgentDestroyPolicy
{
    boolean canDestroy(Agent agent);
}
