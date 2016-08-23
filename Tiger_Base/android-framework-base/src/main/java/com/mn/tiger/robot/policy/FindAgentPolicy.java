package com.mn.tiger.robot.policy;

import com.mn.tiger.robot.Agent;

import java.util.List;

/**
 * Created by Tiger on 16/8/22.
 */
public interface FindAgentPolicy
{
    List<Agent> findFromAllAgents(List<Agent> allAgents);
}
