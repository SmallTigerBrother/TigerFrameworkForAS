package com.mn.tiger.datastorage.db.upgrade;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

import com.mn.tiger.datastorage.TGDBManager;
import com.mn.tiger.datastorage.db.DaoConfig;
import com.mn.tiger.datastorage.db.exception.DbException;
import com.mn.tiger.datastorage.db.sqlite.SqlInfo;
import com.mn.tiger.datastorage.db.sqlite.WhereBuilder;
import com.mn.tiger.datastorage.db.table.Column;
import com.mn.tiger.datastorage.db.table.DbModel;
import com.mn.tiger.datastorage.db.table.Table;
import com.mn.tiger.datastorage.db.table.TableUtils;
import com.mn.tiger.log.LogTools;
import com.mn.tiger.utility.FileUtils;

/**
 * 数据库升级默认实现类
 * 
 * @since 2014年2月18日
 */
public abstract class AbsDbUpgrade
{
	/**
	 * log tag
	 */
	public static final String TAG = AbsDbUpgrade.class.getSimpleName();

	/**
	 * 默认实现的数据库升级方法：备份原有数据库，根据当前版本entity新建表，再把备份数据库对应的数据迁移到新表中
	 * 
	 * @param db
	 *            数据库管理类
	 * @param oldVersion
	 *            原有数据库版本
	 * @param newVersion
	 *            新数据库版本
	 * @param daoConfig
	 *            新数据库配置 void
	 */
	public void onUpgrade(TGDBManager db, int oldVersion, int newVersion, DaoConfig daoConfig)
	{
		try
		{
			LogTools.p(TAG, "[Method: onUpgrade]  "
					+ "start upgrade DB. backup DB, clean old DB data.");
			// 备份原有数据库为临时数据库
			db.getDatabase().setVersion(newVersion);
			String oldDbPath = db.getDatabase().getPath();
			int index = oldDbPath.lastIndexOf(File.separator);
			String newDbPath = oldDbPath.substring(0, index + 1) + "temp_"
					+ oldDbPath.substring(index + 1, oldDbPath.length());
			File newDbFile = new File(newDbPath);
			if (!newDbFile.exists())
			{
				FileUtils.copyFile(oldDbPath, newDbPath);
			}

			// 删除原有数据库中所有业务表
			List<TigerTables> oldTableList = db.findAll(TigerTables.class);
			for (TigerTables table : oldTableList)
			{
				db.execNonQuery("DROP TABLE " + table.getName());
			}
			db.deleteAll(TigerTables.class);

			// 读取临时数据库
			String newDbName = "temp_" + oldDbPath.substring(index + 1, oldDbPath.length());
			daoConfig.setDbName(newDbName);
			daoConfig.setDbUpgrade(null);
			TGDBManager tempDb = TGDBManager.create(daoConfig);

			// 获取临时数据库中所有表
			List<String> tableNames = tempDb.getAllTableFromDb();

			// 遍历备份库中所有表，检查是否是业务表， 如果是业务表，从临时数据库迁移数据到新数据库
			LogTools.p(TAG, "[Method: onUpgrade]  "
					+ "create new table, move data to new table from backup.");
			for (String tableName : tableNames)
			{
				Log.i(TAG, "tableName : " + tableName);
				// 在备份库记录业务表名得表中查询，判断是否存在该业务表
				TigerTables table = tempDb.findFirst(TigerTables.class,
						WhereBuilder.b("name", "=", tableName));
				if (table == null)
				{
					continue;
				}

				// 检查新版本中是否还存在该表对应的entity，如不存在，则不需在新库中创建该表
				Class<?> newClass = findClassForName(table);
				if (newClass == null)
				{
					continue;
				}

				// 检查该备份库业务表中是否有数据
				SqlInfo sqlInfo = new SqlInfo();
				sqlInfo.setSql("SELECT * FROM " + tableName);
				List<DbModel> dbModelList = tempDb.findDbModelAll(sqlInfo);
				// 如果没有数据，在首次操作该表时，才建表
				LogTools.p(TAG, "[Method: onUpgrade]  " + tableName + " data size : "
						+ (dbModelList == null ? null : dbModelList.size()));
				if (dbModelList != null && dbModelList.size() > 0)
				{
					// 如果备份库的表中有数据，在新库中新建改表，迁移数据到新表
					db.createTableIfNotExist(newClass);
					// 如果表中有数据，创建新表，把旧表中原有数据迁移到新表
					moveDataToNewTable(newClass, dbModelList, db);
				}
			}

			// 迁移数据时，自动升级不支持的特殊情况，需要用户根据情况迁移这些数据
			this.onMoveSpecialData(db, tempDb);

			// 删除备份数据库文件
			FileUtils.deleteFile(newDbPath);
			FileUtils.deleteFile(newDbPath + "-journal");

			upgradeSuccess();
		}
		catch (DbException e)
		{
			LogTools.e(TAG, "", e);
			upgradeFail();
		}
	}

	/**
	 * 根据表信息对应的类全路径，查询对应的entity是否存在
	 * 
	 * @param tableInfo
	 * @return Class<?>
	 */
	private Class<?> findClassForName(TigerTables tableInfo)
	{
		Class<?> newClass = null;
		try
		{
			newClass = Class.forName(tableInfo.getClassPath());
		}
		catch (ClassNotFoundException e)
		{
			// 找不到改类，忽略
			Log.e(TAG, "tableName : " + e.getMessage());
		}

		return newClass;
	}

	/**
	 * 迁移表数据
	 * 
	 * @param newClass
	 *            数据库表对应的entity类
	 * @param tableInfo
	 *            表名称和对应的entity类信息
	 * @param dbModelList
	 *            原有表数据集合 void
	 */
	private void moveDataToNewTable(Class<?> newClass, List<DbModel> dbModelList, TGDBManager db)
	{
		// 获取原有表中所有的列
		List<String> columns = new ArrayList<String>();
		Iterator<String> it = dbModelList.get(0).getDataMap().keySet().iterator();
		while (it.hasNext())
		{
			String key = it.next();
			columns.add(key);
		}

		// 迁移旧表中所有数据到新表
		try
		{
			for (int i = 0; i < dbModelList.size(); i++)
			{
				db.execNonQuery(buildMoveSqlInfo(db, newClass, columns, dbModelList.get(i)));
			}
		}
		catch (DbException dbe)
		{
			// 插入数据失败，删除该表，还原临时表
			Log.e(TAG, "tableName : " + dbe.getMessage());
		}

	}

	/**
	 * 拼装迁移表数据sql语句，并执行数据迁移
	 * 
	 * @param db
	 * @param entityType
	 * @param oldColumns
	 * @param dbModel
	 * @return
	 * @throws DbException
	 *             SqlInfo
	 */
	public SqlInfo buildMoveSqlInfo(TGDBManager db, Class<?> entityType, List<String> oldColumns,
			DbModel dbModel) throws DbException
	{
		// 获取现有表所有列
		Table newTable = Table.get(db, entityType);
		HashMap<String, Column> columnMap = newTable.columnMap;
		Iterator<Entry<String, Column>> iter = columnMap.entrySet().iterator();
		int length = columnMap.size();

		SqlInfo result = new SqlInfo();
		StringBuffer sqlBuffer = new StringBuffer();

		sqlBuffer.append("INSERT INTO ");
		sqlBuffer.append(TableUtils.getTableName(entityType));
		sqlBuffer.append(" (");
		sqlBuffer.append(newTable.getId().getColumnName()).append(",");
		try
		{
			result.addBindArgWithoutConverter(dbModel.getString(newTable.getId().getColumnName()));
		}
		catch (RuntimeException re)
		{
			result.addBindArgWithoutConverter("");
		}
		
		while (iter.hasNext())
		{
			// 判断该列是否在原有数据库中存在，如存在，拷贝数据，如不存在，写入默认数据
			Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iter.next();
			Column column = entry.getValue();

			sqlBuffer.append(entry.getKey()).append(",");
			String columnName = column.getColumnName();
			// TODO String columnType = column.getColumnDbType();
			if (oldColumns.contains(columnName))
			{
				try
				{
					result.addBindArgWithoutConverter(dbModel.getString(columnName));
				}
				catch (RuntimeException re)
				{
					result.addBindArgWithoutConverter(column.getDefaultValue());
				}
			}
			else
			{
				result.addBindArgWithoutConverter(column.getDefaultValue());
			}

		}

		sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
		sqlBuffer.append(") VALUES (");
		sqlBuffer.append("?,");
		for (int i = 0; i < length; i++)
		{
			sqlBuffer.append("?,");
		}
		sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
		sqlBuffer.append(")");

		result.setSql(sqlBuffer.toString());

		LogTools.p(TAG, "[Method: onUpgrade]  " + "move data sql :　" + result);

		return result;
	}

	/**
	 * 某些特殊的类型变化列，默认方法不支持数据迁移，需要重新改方法手动迁移数据
	 * 
	 * @param newDb
	 * @param backupsDb
	 *            void
	 */
	public void onMoveSpecialData(TGDBManager newDb, TGDBManager backupsDb)
	{

	}

	/**
	 * 升级成功方法
	 * 
	 * void
	 */
	public abstract void upgradeSuccess();

	/**
	 * 升级失败方法
	 * 
	 * void
	 */
	public abstract void upgradeFail();

	/**
	 * 无需升级方法
	 * 
	 * void
	 */
	public abstract void upgradeNeedless();
}
