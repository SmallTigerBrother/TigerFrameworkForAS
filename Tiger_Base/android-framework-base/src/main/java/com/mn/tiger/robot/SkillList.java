package com.mn.tiger.robot;

import com.mn.tiger.log.Logger;

import java.util.ArrayList;

/**
 * Created by Tiger on 16/8/22.
 */
public class SkillList extends ArrayList<Skill>
{
    private static final Logger LOG = Logger.getLogger(SkillList.class);

    private int cursor = -1;

    public Skill getNextSkill()
    {
        if(size() > 0)
        {
            cursor++;
            if(cursor < size())
            {
                LOG.i("[Method:getNextSkill] cursor == " + cursor);
                return get(cursor);
            }
            else
            {
                cursor = -1;
                LOG.i("[Method:getNextSkill] reset cursor");
                return getNextSkill();
            }
        }
        return null;
    }

    public int getCursor()
    {
        return cursor;
    }

    @Override
    public boolean remove(Object object)
    {
        cursor--;
        return super.remove(object);
    }

    @Override
    public Skill remove(int index)
    {
        cursor--;
        return super.remove(index);
    }
}
