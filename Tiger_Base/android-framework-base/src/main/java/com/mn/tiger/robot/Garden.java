package com.mn.tiger.robot;

import com.mn.tiger.log.Logger;
import com.mn.tiger.robot.policy.FindAgentPolicy;
import com.mn.tiger.robot.policy.GardenRunPolicy;
import com.mn.tiger.robot.policy.RandomQuotaFindAgentPolicy;
import com.mn.tiger.robot.policy.SchedulerRunPolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Tiger on 16/8/22.
 */
public class Garden
{
    private static final Logger LOG = Logger.getLogger(Garden.class);

    private FindAgentPolicy findAgentPolicy;

    private GardenRunPolicy gardenRunPolicy;

    private ArrayList<Agent> agents;

    private boolean running = false;

    private static Garden garden;

    public static Garden getSingleInstance()
    {
        if (null == garden)
        {
            synchronized (Garden.class)
            {
                if (null == garden)
                {
                    garden = new Garden();
                }
            }
        }
        return garden;
    }

    private Garden()
    {
        gardenRunPolicy = new SchedulerRunPolicy();
        agents = new ArrayList<>();
        setFindAgentPolicy(new RandomQuotaFindAgentPolicy(10));
    }

    public void setFindAgentPolicy(FindAgentPolicy findAgentPolicy)
    {
        this.findAgentPolicy = findAgentPolicy;
    }

    public void setGardenRunPolicy(GardenRunPolicy gardenRunPolicy)
    {
        this.gardenRunPolicy = gardenRunPolicy;
    }

    public void accept(Agent agent)
    {
        agents.add(agent);
    }

    public void accept(Collection<? extends Agent> newAgents)
    {
        agents.addAll(newAgents);
    }

    public void dissolve()
    {
        agents.clear();
        gardenRunPolicy.shutdown();
    }

    public void start()
    {
        if(running)
        {
            LOG.i("[Method:start] start already");
            return;
        }
        LOG.i("[Method:start]");

        //启动缓存线程调度
        gardenRunPolicy.run(new Runnable()
        {
            @Override
            public void run()
            {
                List<Agent> selectAgents = findAgentPolicy.findFromAllAgents(agents);
                int count = selectAgents.size();
                for (int i = 0; i < count; i++)
                {
                    Agent agent = selectAgents.get(i);
                    //如果Agent等待被清理,将Agent从列表从清除
                    if(agent.getStatus() == AgentStatus.UNDER_DESTROY)
                    {
                        agents.remove(agent);
                    }
                    else
                    {
                        agent.act();
                    }
                }
            }
        });

        running = true;
    }

    public void shutdown()
    {
        LOG.i("[Method:shutdown]");
        gardenRunPolicy.shutdown();
        running = false;
    }
}
