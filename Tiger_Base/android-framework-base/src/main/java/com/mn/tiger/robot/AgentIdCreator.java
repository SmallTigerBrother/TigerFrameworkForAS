package com.mn.tiger.robot;

/**
 * Created by Tiger on 16/8/23.
 */
class AgentIdCreator
{
    private static int id = 1;

    public static long createId()
    {
        return ++id;
    }
}
