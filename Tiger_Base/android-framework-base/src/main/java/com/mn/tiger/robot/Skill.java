package com.mn.tiger.robot;

import rx.Observable;

/**
 * Created by Tiger on 16/8/22.
 */
public abstract class Skill<Param,Result>
{
    protected int priority = 1000;

    public abstract Observable<Result> execute(Agent agent, Param param);

    public void onExecuteComplete(Agent agent)
    {

    }

    public void onExecuteError(Agent agent, Throwable throwable)
    {
        if(null != throwable)
        {
            throwable.printStackTrace();
        }
    }
}
