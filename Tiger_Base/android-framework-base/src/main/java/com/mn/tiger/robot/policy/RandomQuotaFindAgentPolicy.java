package com.mn.tiger.robot.policy;

import com.mn.tiger.log.Logger;
import com.mn.tiger.robot.Agent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tiger on 16/8/22.
 */
public class RandomQuotaFindAgentPolicy implements FindAgentPolicy
{
    private static final Logger LOG = Logger.getLogger(RandomQuotaFindAgentPolicy.class);

    private int quota;

    public RandomQuotaFindAgentPolicy(int quota)
    {
        this.quota = quota;
    }

    @Override
    public List<Agent> findFromAllAgents(List<Agent> allAgents)
    {
        LOG.i("[Method:findFromAllAgents]");

        int allCount = allAgents.size();
        if(quota >= allCount)
        {
            return allAgents;
        }
        else
        {
            ArrayList<Agent> result = new ArrayList<>(quota);
            while(result.size() < quota)
            {
                int index = (int) (Math.random() * allCount);
                Agent agent = allAgents.get(index);
                if(!result.contains(agent) && isAgentValid(agent))
                {
                    result.add(agent);
                }
            }
            return result;
        }
    }

    public boolean isAgentValid(Agent agent)
    {
        return true;
    }
}
