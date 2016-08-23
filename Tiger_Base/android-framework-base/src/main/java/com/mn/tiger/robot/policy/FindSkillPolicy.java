package com.mn.tiger.robot.policy;

import com.mn.tiger.robot.Skill;
import com.mn.tiger.robot.SkillList;

/**
 * Created by Tiger on 16/8/22.
 */
public interface FindSkillPolicy
{
    Skill findFromAllSkills(SkillList skills);
}
