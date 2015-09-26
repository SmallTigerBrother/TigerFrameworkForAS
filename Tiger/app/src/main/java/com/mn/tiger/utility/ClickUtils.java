package com.mn.tiger.utility;

import java.util.HashMap;
import java.util.Map;

/**
 * 视图点击帮助类
 */
public class ClickUtils
{
    /**
     * 保存视图点击时间的类
     */
    private static Map<Integer, Long> clickTimeMap = new HashMap<Integer, Long>();

    /**
     * 是否是重复点击，两次点击事件间隔小于500ms认为是重复点击
     * @param viewId
     * @return
     */
    public static boolean isReClick(Integer viewId)
    {
        if (null != clickTimeMap.get(viewId))
        {
            if ((System.currentTimeMillis() - clickTimeMap.get(viewId)) <= 500)
            {
                return true;
            }
        }
        clickTimeMap.put(viewId, System.currentTimeMillis());
        return false;
    }
}
