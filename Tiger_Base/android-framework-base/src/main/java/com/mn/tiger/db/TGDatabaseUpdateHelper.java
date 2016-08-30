package com.mn.tiger.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tiger on 16/8/30.
 */
public class TGDatabaseUpdateHelper
{
    private ConcurrentHashMap<Integer, TGDatabaseUpdateExecutor> updateExecutorMap;

    private static TGDatabaseUpdateHelper instance;

    private static TGDatabaseUpdateHelper getInstance()
    {
        if(null == instance)
        {
            synchronized (TGDatabaseUpdateHelper.class)
            {
                if(null != instance)
                {
                    instance = new TGDatabaseUpdateHelper();
                }
            }
        }
        return instance;
    }

    private TGDatabaseUpdateHelper()
    {
        updateExecutorMap = new ConcurrentHashMap<>();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(checkUpdateExecutorEnough(oldVersion, newVersion))
        {
            //根据版本取出对应的UpdateExecutor执行升级过程
            for (int i = oldVersion + 1; i <= newVersion; i++)
            {
                updateExecutorMap.get(i).execute(db);
            }
        }
    }

    private boolean checkUpdateExecutorEnough(int oldVersion, int newVersion)
    {
        for(int i = oldVersion + 1; i <= newVersion; i++)
        {
            if(null == updateExecutorMap.get(i))
            {
                throw new SQLException("do not have update executor for version " + i);
            }
        }
        return true;
    }

    public void addDatabaseUpdateExecutor(int targetVersion, TGDatabaseUpdateExecutor updateExecutor)
    {
        updateExecutorMap.put(targetVersion, updateExecutor);
    }
}
