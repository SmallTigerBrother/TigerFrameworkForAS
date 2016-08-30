package com.mn.tiger.db;

import android.database.sqlite.SQLiteDatabase;

import com.mn.tiger.log.Logger;

/**
 * Created by Tiger on 16/8/30.
 */
public abstract class TGDatabaseUpdateExecutor
{
    private static final Logger LOG = Logger.getLogger(TGDatabaseUpdateExecutor.class);

    private int fromVersion;

    private int toVersion;

    public TGDatabaseUpdateExecutor(int fromVersion, int toVersion)
    {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }

    void execute(SQLiteDatabase db)
    {
        LOG.d("[Method:execute] fromVersion == " + fromVersion + " toVersion == " + toVersion + " start!");
        //开始事务
        db.beginTransaction();
        try
        {
            //执行升级命令
            updateDatabaseInTransaction(db, fromVersion, toVersion);
            //设置事务成功
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        db.endTransaction();

        LOG.d("[Method:execute] fromVersion == " + fromVersion + " toVersion == " + toVersion + " success!");
    }

    public int getFromVersion()
    {
        return fromVersion;
    }

    public int getToVersion()
    {
        return toVersion;
    }

    protected abstract void updateDatabaseInTransaction(SQLiteDatabase db, int fromVersion, int toVersion);

    protected void renameColumn(SQLiteDatabase db,String tableName, String originalColumn, String newColumn)
    {
        db.execSQL("ALERT TABLE " + tableName + " CHANGE COLUMN " + originalColumn + " " + newColumn);
    }

    protected void dropTable(SQLiteDatabase db, String tableName)
    {
        db.execSQL("DROP TABLE IF EXISTS" + tableName);
    }
}
