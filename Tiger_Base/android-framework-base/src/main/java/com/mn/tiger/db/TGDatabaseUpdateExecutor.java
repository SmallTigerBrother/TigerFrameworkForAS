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

    /**
     * 执行更新操作
     * @param db
     */
    final void execute(SQLiteDatabase db)
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

    /**
     * 在事务中更新数据库
     * @param db
     * @param fromVersion
     * @param toVersion
     */
    protected abstract void updateDatabaseInTransaction(SQLiteDatabase db, int fromVersion, int toVersion);

    /**
     * 新增字段
     * @param db
     * @param tableName
     * @param columnName
     * @param dataType
     * @param defaultValue
     * @param notNull
     */
    protected void addColumn(SQLiteDatabase db, String tableName, String columnName, String dataType, Object defaultValue, boolean notNull)
    {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("ALERT TABLE ");
        sqlBuilder.append(tableName);
        sqlBuilder.append(" ADD COLUMN ");
        sqlBuilder.append(columnName);
        sqlBuilder.append(" " + dataType + " ");
        if (notNull)
        {
            sqlBuilder.append(" NOT NULL ");
        }

        if(null != defaultValue)
        {
            sqlBuilder.append("DEFAULT " + defaultValue);
        }
    }

    /**
     * 删除一个字段
     * @param tableName
     * @param columnName
     */
    protected void dropColumn(SQLiteDatabase db, String tableName, String columnName)
    {
        db.execSQL("ALERT TABLE " + tableName + " DROP COLUMN " + columnName);
    }

    /**
     * 重命名一个字段名
     * @param db
     * @param tableName
     * @param originalColumn
     * @param newColumn
     */
    protected void renameColumn(SQLiteDatabase db, String tableName, String originalColumn, String newColumn)
    {
        db.execSQL("ALERT TABLE " + tableName + " RENAME COLUMN " + originalColumn + " TO " + newColumn);
    }

    /**
     * 重命名一张表
     * @param db
     * @param originalTable
     * @param toTable
     */
    protected void renameTable(SQLiteDatabase db, String originalTable, String toTable)
    {
        db.execSQL("ALERT TABLE " + originalTable + " RENAME TO " + toTable);
    }

    /**
     * 删除
     * @param db
     * @param tableName
     */
    protected void dropTable(SQLiteDatabase db, String tableName)
    {
        db.execSQL("DROP TABLE IF EXISTS" + tableName);
    }
}
