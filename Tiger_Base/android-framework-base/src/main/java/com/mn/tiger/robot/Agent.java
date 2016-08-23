package com.mn.tiger.robot;

import com.mn.tiger.log.Logger;
import com.mn.tiger.robot.policy.AgentDestroyPolicy;
import com.mn.tiger.robot.policy.FindSkillPolicy;
import com.mn.tiger.utility.Commons;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Tiger on 16/8/22.
 */
public abstract class Agent
{
    private static final Logger LOG = Logger.getLogger(Agent.class);

    private long id;

    protected SkillList skillList;

    private AgentStatus status = AgentStatus.WAITING;

    private FindSkillPolicy findSkillPolicy;

    private AgentDestroyPolicy agentDestroyPolicy;

    private int errorCount = 0;

    public Agent()
    {
        id = AgentIdCreator.createId();
        skillList = new SkillList();
        findSkillPolicy = new FindSkillPolicy()
        {
            @Override
            public Skill findFromAllSkills(SkillList skills)
            {
                LOG.i("[Method:findFromAllSkills] AgentId == " + id);
                return skills.getNextSkill();
            }
        };

        agentDestroyPolicy = new AgentDestroyPolicy()
        {
            @Override
            public boolean canDestroy(Agent agent)
            {
                return false;
            }
        };
    }

    public long getId()
    {
        return id;
    }

    public int getErrorCount()
    {
        return errorCount;
    }

    public AgentStatus getStatus()
    {
        return status;
    }

    public void setFindSkillPolicy(FindSkillPolicy findSkillPolicy)
    {
        this.findSkillPolicy = findSkillPolicy;
    }

    public void setAgentDestroyPolicy(AgentDestroyPolicy agentDestroyPolicy)
    {
        this.agentDestroyPolicy = agentDestroyPolicy;
    }

    public synchronized void learn(Skill... skills)
    {
        if (null != skills && skills.length > 0)
        {
            for (int i = 0; i < skills.length; i++)
            {
                learn(skills[i]);
            }
        }
    }

    public synchronized void learn(Skill newSkill)
    {
        LOG.i("[Method:learn] " + newSkill.getClass().getSimpleName() + " AgentId == " + id);

        int count = skillList.size();
        if (count == 0)
        {
            skillList.add(newSkill);
            return;
        }

        Skill skill;
        for (int i = 0; i < count; i++)
        {
            skill = skillList.get(i);
            //根据优先级插入技能
            if (skill.priority > newSkill.priority)
            {
                skillList.add(i, newSkill);
            }
        }
        //如果该Skill优先级最低,补充到列表尾部
        if(!skillList.contains(newSkill))
        {
            skillList.add(newSkill);
        }
    }

    public synchronized void deprecateSkill(Skill skill)
    {
        LOG.i("[Method:deprecateSkill] " + skill.getClass().getSimpleName() + " AgentId == " + id);
        skillList.remove(skill);
    }

    public boolean hasSkills()
    {
        return skillList.size() > 0;
    }

    protected final void act()
    {
        LOG.i("[Method:act] status = " + status + " AgentId == " + id);

        if(status == AgentStatus.WAITING)
        {
            final Skill skill = findSkillPolicy.findFromAllSkills(skillList);
            if(null != skill)
            {
                Observable observable = skill.execute(this, getParam(Commons.getClassOfGenericType(skill.getClass(), 0)));
                if(null != observable)
                {
                    observable.subscribe(new Subscriber()
                    {
                        @Override
                        public void onStart()
                        {
                            LOG.i("[Method:act:onStart] AgentId == " + id);
                            status = AgentStatus.RUNNING;
                        }

                        @Override
                        public void onCompleted()
                        {
                            LOG.i("[Method:act:onCompleted] AgentId == " + id);
                            skill.onExecuteComplete(Agent.this);
                            status = AgentStatus.WAITING;
                            if(agentDestroyPolicy.canDestroy(Agent.this))
                            {
                                status = AgentStatus.UNDER_DESTROY;
                            }
                        }

                        @Override
                        public void onError(Throwable e)
                        {
                            errorCount++;
                            LOG.i("[Method:act:onError] AgentId == " + id);
                            skill.onExecuteError(Agent.this, e);
                            status = AgentStatus.WAITING;
                            if(agentDestroyPolicy.canDestroy(Agent.this))
                            {
                                status = AgentStatus.UNDER_DESTROY;
                            }
                        }

                        @Override
                        public void onNext(Object o)
                        {
                        }
                    });
                }
                else
                {
                    LOG.i("[Method:act] observable == null;  status = " + status + " AgentId == " + id);
                    status = AgentStatus.WAITING;
                    if(agentDestroyPolicy.canDestroy(Agent.this))
                    {
                        status = AgentStatus.UNDER_DESTROY;
                    }
                }
            }
            else
            {
                LOG.i("[Method:act] skill == null;  status = " + status + " AgentId == " + id);
                status = AgentStatus.WAITING;
                if(agentDestroyPolicy.canDestroy(Agent.this))
                {
                    status = AgentStatus.UNDER_DESTROY;
                }
            }
        }
    }

    protected abstract <T> T getParam(Class<T> clazz);
}
