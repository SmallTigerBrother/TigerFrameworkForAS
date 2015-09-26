package com.mn.tiger.datastorage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.mn.tiger.datastorage.db.DaoConfig;
import com.mn.tiger.datastorage.db.FindTempCache;
import com.mn.tiger.datastorage.db.exception.DbException;
import com.mn.tiger.datastorage.db.observe.TGDatasetObserveController;
import com.mn.tiger.datastorage.db.sqlite.CursorUtils;
import com.mn.tiger.datastorage.db.sqlite.TGModelSelector;
import com.mn.tiger.datastorage.db.sqlite.Selector;
import com.mn.tiger.datastorage.db.sqlite.SqlInfo;
import com.mn.tiger.datastorage.db.sqlite.SqlInfoBuilder;
import com.mn.tiger.datastorage.db.sqlite.WhereBuilder;
import com.mn.tiger.datastorage.db.table.DbModel;
import com.mn.tiger.datastorage.db.table.Id;
import com.mn.tiger.datastorage.db.table.KeyValue;
import com.mn.tiger.datastorage.db.table.Table;
import com.mn.tiger.datastorage.db.table.TableUtils;
import com.mn.tiger.datastorage.db.upgrade.AbsDbUpgrade;
import com.mn.tiger.datastorage.db.upgrade.DatabaseObject;
import com.mn.tiger.datastorage.db.upgrade.TigerTables;
import com.mn.tiger.datastorage.db.util.IOUtils;
import com.mn.tiger.log.LogTools;


/**
 * 数据库操作接口，提供数据库升级、创建等方法 实现对象的增删改查
 * 
 */
public class TGDBManager
{
	/**
	 * 数据库集合。key: dbName
	 */
	private static HashMap<String, TGDBManager> daoMap = new HashMap<String, TGDBManager>();

	/**
	 * 查询时的临时缓存
	 */
	private final FindTempCache findTempCache = new FindTempCache();

	/**
	 * database
	 */
	private SQLiteDatabase database;
	private DaoConfig daoConfig;
	private boolean debug = false;
	private boolean allowTransaction = false;

	/**
	 * 构造方法
	 * 
	 * @param config
	 */
	private TGDBManager(DaoConfig config)
	{
		if (config == null)
		{
			throw new IllegalArgumentException("daoConfig may not be null");
		}
		if (config.getContext() == null)
		{
			throw new IllegalArgumentException("context mey not be null");
		}
		this.database = createDatabase(config);
		this.daoConfig = config;
	}

	/**
	 * 数据库升级方法
	 * 
	 * @param context
	 * @param dbDir
	 * @param dbName
	 * @param dbVersion
	 * @param dbUpgrade
	 *            void
	 */
	public static void UpgradeDb(final Context context, final String dbDir, final String dbName, final int dbVersion,
			final AbsDbUpgrade dbUpgrade)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				create(context, dbDir, dbName, dbVersion, dbUpgrade);
			}
		}).start();
	}

	/**
	 * 获取数据库管理类实例
	 * 
	 * @param daoConfig
	 *            数据库设置
	 * @return MPDbManager
	 */
	private synchronized static TGDBManager getInstance(DaoConfig daoConfig)
	{
		// 检查是否存在该数据库
		TGDBManager dao = daoMap.get(daoConfig.getDbDir() + daoConfig.getDbName());
		if (dao == null)
		{
			dao = new TGDBManager(daoConfig);
			daoMap.put(daoConfig.getDbDir() + daoConfig.getDbName(), dao);
		}
		else
		{
			dao.daoConfig = daoConfig;
		}

		// 检查是否存在tigerTable表，如果不存在（数据库初次创建时），则创建该表
		try
		{
			dao.createTableIfNotExist(TigerTables.class);
		}
		catch (DbException e)
		{
			LogTools.e(e.getMessage(), e);
		}

		// 检查是否有数据库升级策略，如果有，执行升级流程
		AbsDbUpgrade dbUpgrade = daoConfig.getDbUpgrade();
		if (dbUpgrade != null)
		{
			// 检查数据库版本，如果需要，更新数据库版本
			SQLiteDatabase database = dao.database;
			int oldVersion = database.getVersion();
			int newVersion = daoConfig.getDbVersion();

			if (oldVersion != newVersion)
			{
				dbUpgrade.onUpgrade(dao, oldVersion, newVersion, daoConfig);
			}
			else
			{
				// 版本相同，不用升级
				dbUpgrade.upgradeNeedless();
			}
		}

		return dao;
	}

	/**
	 * 根据自定义名称，创建数据库 根据传入版本号，更新数据库
	 * 
	 * @param context
	 * @param dbDir
	 * @param dbName
	 * @return MPDbManager
	 */
	public static TGDBManager create(Context context, String dbName, int dbVersion, AbsDbUpgrade dbUpgrade)
	{
		DaoConfig config = new DaoConfig(context);
		config.setDbName(dbName);
		config.setDbVersion(dbVersion);
		config.setDbUpgrade(dbUpgrade);
		return getInstance(config);
	}

	/**
	 * 创建数据库
	 * 
	 * @param context
	 *            应用上下文信息
	 * @param dbDir
	 *            数据库文件保存路径
	 * @param dbName
	 *            数据库名称
	 * @param dbVersion
	 *            数据库版本信息
	 * @param dbUpgrade
	 *            数据库更新实现类（重写onUpgrade方法可自定义数据库更新策略）
	 * @return MPDbManager 数据库管理类
	 */
	public static TGDBManager create(Context context, String dbDir, String dbName, int dbVersion, AbsDbUpgrade dbUpgrade)
	{
		DaoConfig config = new DaoConfig(context);
		config.setDbDir(dbDir);
		config.setDbName(dbName);
		config.setDbVersion(dbVersion);
		config.setDbUpgrade(dbUpgrade);
		return getInstance(config);
	}

	/**
	 * 根据传入配置，创建数据库
	 * 
	 * @param daoConfig
	 * @return MPDbManager
	 */
	public static TGDBManager create(DaoConfig daoConfig)
	{
		return getInstance(daoConfig);
	}

	/**
	 * 
	 * 该方法的作用: 关闭DB, 并移除出缓存
	 * 
	 * @date 2014年8月29日
	 * @param dbFilePath
	 *            db文件全路径
	 */
	public static void closeDB(String dbFilePath)
	{
		if (daoMap.get(dbFilePath) != null && daoMap.get(dbFilePath).database != null)
		{
			daoMap.get(dbFilePath).database.close();
		}

		daoMap.remove(dbFilePath);
	}

	/**
	 * 
	 * 该方法的作用: 关闭DB, 并移除出缓存
	 * 
	 * @date 2014年8月29日
	 */
	public static void closeAllDB()
	{
		List<String> dbFilePathList = new ArrayList<String>();
		Iterator<String> iterator = daoMap.keySet().iterator();
		String key = "";
		while (iterator.hasNext())
		{
			key = iterator.next();
			dbFilePathList.add(key);
		}

		for (String filePath : dbFilePathList)
		{
			closeDB(filePath);
		}
	}

	/**
	 * 是否加入sql打印日志
	 * 
	 * @param debug
	 * @return MPDbManager
	 */
	public TGDBManager configDebug(boolean debug)
	{
		this.debug = debug;
		return this;
	}

	/**
	 * 是否支持事务
	 * 
	 * @param allowTransaction
	 * @return MPDbManager
	 */
	public TGDBManager configAllowTransaction(boolean allowTransaction)
	{
		this.allowTransaction = allowTransaction;
		return this;
	}

	/**
	 * 获取数据库信息
	 * 
	 * @return SQLiteDatabase
	 */
	public SQLiteDatabase getDatabase()
	{
		return database;
	}

	/**
	 * 获取数据库配置信息
	 * 
	 * @return DaoConfig
	 */
	public DaoConfig getDaoConfig()
	{
		return daoConfig;
	}

	/**
	 * 根据传入entity，做新增或更新表操作
	 * 
	 * @param entity
	 * @throws DbException
	 *             void
	 */
	public void saveOrUpdate(Object entity) throws DbException
	{
		try
		{
			beginTransaction();

			createTableIfNotExist(entity.getClass());
			saveOrUpdateWithoutTransaction(entity);

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entity.getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 根据传入entity集合，全部做新增或更新表操作
	 * 
	 * @param entity
	 * @throws DbException
	 *             void
	 */
	public void saveOrUpdateAll(List<?> entities) throws DbException
	{
		if (entities == null || entities.size() == 0)
		{
			return;
		}
			
		try
		{
			beginTransaction();

			createTableIfNotExist(entities.get(0).getClass());
			for (Object entity : entities)
			{
				saveOrUpdateWithoutTransaction(entity);
			}

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entities.get(0).getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 替换主键相同的entity
	 * 
	 * @param entity
	 * @throws DbException
	 *             void
	 */
	@SuppressWarnings("unused")
	private void replace(Object entity) throws DbException
	{
		try
		{
			beginTransaction();

			createTableIfNotExist(entity.getClass());
			execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(this, entity));

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entity.getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 替换list队列中主键相同的entity
	 * 
	 * @param entities
	 * @throws DbException
	 *             void
	 */
	@SuppressWarnings("unused")
	private void replaceAll(List<?> entities) throws DbException
	{
		if (entities == null || entities.size() == 0)
		{
			return;
		}
		
		try
		{
			beginTransaction();

			createTableIfNotExist(entities.get(0).getClass());
			for (Object entity : entities)
			{
				execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(this, entity));
			}

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entities.get(0).getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 保存相关entity对象信息
	 * 
	 * @param entity
	 * @throws DbException
	 *             void
	 */
	public void save(Object entity) throws DbException
	{
		try
		{
			beginTransaction();

			createTableIfNotExist(entity.getClass());
			execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(this, entity));

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entity.getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 保存列表中所有entity对象
	 * 
	 * @param entities
	 * @throws DbException
	 *             void
	 */
	public void saveAll(List<?> entities) throws DbException
	{
		if (entities == null || entities.size() == 0)
		{
			return;
		}
			
		try
		{
			beginTransaction();

			createTableIfNotExist(entities.get(0).getClass());
			for (Object entity : entities)
			{
				execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(this, entity));
			}

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entities.get(0).getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 保存对象关联数据库生成的id
	 * 
	 * @param entity
	 * @return
	 * @throws DbException
	 *             boolean
	 */
	public boolean saveBindingId(Object entity) throws DbException
	{
		boolean result = false;
		try
		{
			beginTransaction();

			createTableIfNotExist(entity.getClass());
			result = saveBindingIdWithoutTransaction(entity);

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entity.getClass());
		}
		finally
		{
			endTransaction();
		}
		return result;
	}

	/**
	 * 保存集合中所有对象关联数据库生成的id
	 * 
	 * @param entities
	 * @throws DbException
	 *             void
	 */
	public void saveBindingIdAll(List<?> entities) throws DbException
	{
		if (entities == null || entities.size() == 0)
		{
			return;
		}
		
		try
		{
			beginTransaction();

			createTableIfNotExist(entities.get(0).getClass());
			for (Object entity : entities)
			{
				if (!saveBindingIdWithoutTransaction(entity))
				{
					throw new DbException("saveBindingId error, transaction will not commit!");
				}
			}

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entities.get(0).getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 根据id删除数据
	 * 
	 * @param entityType
	 * @param idValue
	 * @throws DbException
	 *             void
	 */
	public void deleteById(Class<?> entityType, Object idValue) throws DbException
	{
		if (!tableIsExist(entityType))
		{
			return;
		}
		
		try
		{
			beginTransaction();

			execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(entityType, idValue));

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entityType);
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 删除对象
	 * 
	 * @param entity
	 * @throws DbException
	 *             void
	 */
	public void delete(Object entity) throws DbException
	{
		if (!tableIsExist(entity.getClass()))
		{
			return;
		}
		
		try
		{
			beginTransaction();

			execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(entity));

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entity.getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 根据条件删除对象
	 * 
	 * @param entityType
	 * @param whereBuilder
	 * @throws DbException
	 *             void
	 */
	public void delete(Class<?> entityType, WhereBuilder whereBuilder) throws DbException
	{
		if (!tableIsExist(entityType))
		{
			return;
		}
		
		try
		{
			beginTransaction();

			execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(entityType, whereBuilder));

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entityType);
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 删除集合中所有对象
	 * 
	 * @param entities
	 * @throws DbException
	 *             void
	 */
	public void deleteAll(List<?> entities) throws DbException
	{
		if (entities == null || entities.size() == 0 || 
				!tableIsExist(entities.get(0).getClass()))
		{
			return;
		}
		
		try
		{
			beginTransaction();

			for (Object entity : entities)
			{
				execNonQuery(SqlInfoBuilder.buildDeleteSqlInfo(entity));
			}

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entities.get(0).getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 删除对象对应的表中所有数据
	 * 
	 * @param entityType
	 * @throws DbException
	 *             void
	 */
	public void deleteAll(Class<?> entityType) throws DbException
	{
		delete(entityType, null);
	}

	/**
	 * 修改选择列的数据
	 * 
	 * @param entity
	 * @param updateColumnNames
	 * @throws DbException
	 *             void
	 */
	public void update(Object entity, String... updateColumnNames) throws DbException
	{
		if (!tableIsExist(entity.getClass()))
		{
			return;
		}
		
		try
		{
			beginTransaction();

			execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity, updateColumnNames));

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entity.getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 根据条件修改选择列的数据
	 * 
	 * @param entity
	 * @param whereBuilder
	 * @param updateColumnNames
	 * @throws DbException
	 *             void
	 */
	public void update(Object entity, WhereBuilder whereBuilder, String... updateColumnNames) throws DbException
	{
		if (!tableIsExist(entity.getClass()))
		{
			return;
		}
		
		try
		{
			beginTransaction();

			execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity, whereBuilder, updateColumnNames));

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entity.getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 修改集合中选择列的数据
	 * 
	 * @param entity
	 * @param updateColumnNames
	 * @throws DbException
	 *             void
	 */
	public void updateAll(List<?> entities, String... updateColumnNames) throws DbException
	{
		if (entities == null || entities.size() == 0 || !tableIsExist(entities.get(0).getClass()))
		{
			return;
		}
		
		try
		{
			beginTransaction();

			for (Object entity : entities)
			{
				execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity, updateColumnNames));
			}

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entities.get(0).getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 根据条件修改集合中选择列的数据
	 * @param entity
	 * @param whereBuilder
	 * @param updateColumnNames
	 * @throws DbException
	 *             void
	 */
	public void updateAll(List<?> entities, WhereBuilder whereBuilder, String... updateColumnNames) throws DbException
	{
		if (entities == null || entities.size() == 0 || !tableIsExist(entities.get(0).getClass()))
		{
			return;
		}
		
		try
		{
			beginTransaction();

			for (Object entity : entities)
			{
				execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity, whereBuilder, updateColumnNames));
			}

			setTransactionSuccessful();

			TGDatasetObserveController.getInstance().notifyChange(entities.get(0).getClass());
		}
		finally
		{
			endTransaction();
		}
	}

	/**
	 * 根据id,查找对象
	 * 
	 * @param entityType
	 * @param idValue
	 * @return
	 * @throws DbException
	 *             T
	 */
	@SuppressWarnings("unchecked")
	public <T> T findById(Class<T> entityType, Object idValue) throws DbException
	{
		if (!tableIsExist(entityType))
		{
			return null;
		}

		Id id = TableUtils.getId(entityType);
		Selector selector = Selector.from(entityType).where(id.getColumnName(), "=", idValue);

		String sql = selector.limit(1).toString();
		long seq = CursorUtils.FindCacheSequence.getSeq();
		findTempCache.setSeq(seq);
		Object obj = findTempCache.get(sql);
		if (obj != null)
		{
			return (T) obj;
		}

		Cursor cursor = execQuery(sql);
		try
		{
			if (cursor.moveToNext())
			{
				T entity = (T) CursorUtils.getEntity(this, cursor, entityType, seq);
				findTempCache.put(sql, entity);
				return entity;
			}
		}
		finally
		{
			IOUtils.closeQuietly(cursor);
		}
		return null;
	}

	/**
	 * 查找表中第一个对象
	 * 
	 * @param selector
	 * @return
	 * @throws DbException
	 *             T
	 */
	@SuppressWarnings("unchecked")
	public <T> T findFirst(Selector selector) throws DbException
	{
		if (!tableIsExist(selector.getEntityType()))
		{
			return null;
		}

		String sql = selector.limit(1).toString();
		long seq = CursorUtils.FindCacheSequence.getSeq();
		findTempCache.setSeq(seq);
		Object obj = findTempCache.get(sql);
		if (obj != null)
		{
			return (T) obj;
		}

		Cursor cursor = execQuery(sql);
		try
		{
			if (cursor.moveToNext())
			{
				T entity = (T) CursorUtils.getEntity(this, cursor, selector.getEntityType(), seq);
				findTempCache.put(sql, entity);
				return entity;
			}
		}
		finally
		{
			IOUtils.closeQuietly(cursor);
		}
		return null;
	}

	/**
	 * 查找表中第一个对象
	 * 
	 * @param entityType
	 * @return
	 * @throws DbException
	 *             T
	 */
	@SuppressWarnings("unchecked")
	public <T> T findFirst(Class<T> entityType) throws DbException
	{
		return (T) findFirst(Selector.from(entityType));
	}

	/**
	 * 根据条件查找表中第一个对象
	 * 
	 * @param entityType
	 * @param whereBuilder
	 * @return
	 * @throws DbException
	 *             T
	 */
	@SuppressWarnings("unchecked")
	public <T> T findFirst(Class<T> entityType, WhereBuilder whereBuilder) throws DbException
	{
		return (T) findFirst(Selector.from(entityType).where(whereBuilder));
	}

	/**
	 * 查找表中第一个对象
	 * 
	 * @param entity
	 * @return
	 * @throws DbException
	 *             T
	 */
	@SuppressWarnings("unchecked")
	public <T> T findFirst(Object entity) throws DbException
	{
		if (!tableIsExist(entity.getClass()))
		{
			return null;
		}

		Selector selector = Selector.from(entity.getClass());
		List<KeyValue> entityKvList = SqlInfoBuilder.entity2KeyValueList(this, entity);
		if (entityKvList != null)
		{
			WhereBuilder wb = WhereBuilder.b();
			Object value = null;
			for (KeyValue keyValue : entityKvList)
			{
				value = keyValue.getValue();
				if (value != null)
				{
					wb.and(keyValue.getKey(), "=", value);
				}
			}
			selector.where(wb);
		}
		return (T) findFirst(selector);
	}

	/**
	 * 根据Selector条件查询
	 * 
	 * @param selector
	 * @return
	 * @throws DbException
	 *             List<T>
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> findAll(Selector selector) throws DbException
	{
		if (!tableIsExist(selector.getEntityType()))
		{
			return new ArrayList<T>();
		}

		String sql = selector.toString();
		long seq = CursorUtils.FindCacheSequence.getSeq();
		findTempCache.setSeq(seq);
		Object obj = findTempCache.get(sql);
		if (obj != null)
		{
			return (List<T>) obj;
		}

		Cursor cursor = execQuery(sql);
		List<T> result = new ArrayList<T>();
		try
		{
			while (cursor.moveToNext())
			{
				T entity = (T) CursorUtils.getEntity(this, cursor, selector.getEntityType(), seq);
				result.add(entity);
			}
			findTempCache.put(sql, result);
		}
		finally
		{
			IOUtils.closeQuietly(cursor);
		}
		return result;
	}

	/**
	 * 根据对象类型查询
	 * 
	 * @param selector
	 * @return
	 * @throws DbException
	 *             List<T>
	 */
	public <T> List<T> findAll(Class<T> entityType) throws DbException
	{
		return findAll(Selector.from(entityType));
	}

	/**
	 * 根据条件查询
	 * 
	 * @param selector
	 * @return
	 * @throws DbException
	 *             List<T>
	 */
	public <T> List<T> findAll(Class<T> entityType, WhereBuilder whereBuilder) throws DbException
	{
		return findAll(Selector.from(entityType).where(whereBuilder));
	}

	/**
	 * 根据对象查询
	 * 
	 * @param selector
	 * @return
	 * @throws DbException
	 *             List<T>
	 */
	public <T> List<T> findAll(Object entity) throws DbException
	{
		if (!tableIsExist(entity.getClass()))
		{
			return new ArrayList<T>();
		}

		Selector selector = Selector.from(entity.getClass());
		List<KeyValue> entityKvList = SqlInfoBuilder.entity2KeyValueList(this, entity);
		if (entityKvList != null)
		{
			WhereBuilder wb = WhereBuilder.b();
			Object value = null;
			for (KeyValue keyValue : entityKvList)
			{
				value = keyValue.getValue();
				if (value != null)
				{
					wb.and(keyValue.getKey(), "=", value);
				}
			}
			selector.where(wb);
		}
		return findAll(selector);
	}

	/**
	 * 根据sql语句查询第一条数据
	 * 
	 * @param sqlInfo
	 *            查询sql语句
	 * @return DbModel 查询结果键值对对象
	 * @throws DbException
	 * 
	 */
	public DbModel findDbModelFirst(SqlInfo sqlInfo) throws DbException
	{
		Cursor cursor = execQuery(sqlInfo);
		try
		{
			if (cursor.moveToNext())
			{
				return CursorUtils.getDbModel(cursor);
			}
		}
		finally
		{
			IOUtils.closeQuietly(cursor);
		}
		return null;
	}

	/**
	 * 根据DbModelSelector条件查询第一条数据
	 * 
	 * @param selector
	 *            查询条件
	 * @return DbModel 查询结果键值对对象
	 * @throws DbException
	 * 
	 */
	public DbModel findDbModelFirst(TGModelSelector selector) throws DbException
	{
		if (!tableIsExist(selector.getEntityType()))
		{
			return null;
		}

		Cursor cursor = execQuery(selector.limit(1).toString());
		try
		{
			if (cursor.moveToNext())
			{
				return CursorUtils.getDbModel(cursor);
			}
		}
		finally
		{
			IOUtils.closeQuietly(cursor);
		}
		return null;
	}

	/**
	 * 根据sql条件查询
	 * 
	 * @param sqlInfo
	 *            查询sql语句
	 * @return List<DbModel> 查询结果键值对对象集合
	 * @throws DbException
	 * 
	 */
	public List<DbModel> findDbModelAll(SqlInfo sqlInfo) throws DbException
	{
		Cursor cursor = execQuery(sqlInfo);
		List<DbModel> dbModelList = new ArrayList<DbModel>();
		try
		{
			while (cursor.moveToNext())
			{
				dbModelList.add(CursorUtils.getDbModel(cursor));
			}
		}
		finally
		{
			IOUtils.closeQuietly(cursor);
		}
		return dbModelList;
	}

	/**
	 * 根据DbModelSelector条件查询
	 * 
	 * @param selector
	 *            查询条件
	 * @return List<DbModel> 查询结果键值对对象集合
	 * @throws DbException
	 * 
	 */
	public List<DbModel> findDbModelAll(TGModelSelector selector) throws DbException
	{
		if (!tableIsExist(selector.getEntityType()))
		{
			return new ArrayList<DbModel>();
		}

		Cursor cursor = execQuery(selector.toString());
		List<DbModel> dbModelList = new ArrayList<DbModel>();
		try
		{
			while (cursor.moveToNext())
			{
				dbModelList.add(CursorUtils.getDbModel(cursor));
			}
		}
		finally
		{
			IOUtils.closeQuietly(cursor);
		}
		return dbModelList;
	}

	/**
	 * 根据Selector查询数据条数
	 * 
	 * @param selector
	 * @return long
	 * @throws DbException
	 */
	public long count(Selector selector) throws DbException
	{
		Class<?> entityType = selector.getEntityType();
		if (!tableIsExist(entityType))
		{
			return 0;
		}

		TGModelSelector dmSelector = selector.select("count(" + 
		    TableUtils.getId(entityType).getColumnName() + ") as count");
		return findDbModelFirst(dmSelector).getLong("count");
	}

	/**
	 * 根据对象类型查询数据条数
	 * 
	 * @param entityType
	 * @return long
	 * @throws DbException
	 * 
	 */
	public long count(Class<?> entityType) throws DbException
	{
		return count(Selector.from(entityType));
	}

	/**
	 * 根据条件和对象类型查询数据条数
	 * 
	 * @param entityType
	 *            表对应的entity类
	 * @param whereBuilder
	 *            查询条件
	 * @return long 返回满足查询条件的记录条数
	 * @throws DbException
	 */
	public long count(Class<?> entityType, WhereBuilder whereBuilder) throws DbException
	{
		return count(Selector.from(entityType).where(whereBuilder));
	}

	/**
	 * 根据对象查询数据条数
	 * 
	 * @param entity
	 *            查询对象参数
	 * @return long 返回满足查询对象条件的记录条数
	 * @throws DbException
	 * 
	 */
	public long count(Object entity) throws DbException
	{
		if (!tableIsExist(entity.getClass()))
		{
			return 0;
		}

		Selector selector = Selector.from(entity.getClass());
		List<KeyValue> entityKvList = SqlInfoBuilder.entity2KeyValueList(this, entity);
		if (entityKvList != null)
		{
			WhereBuilder wb = WhereBuilder.b();
			Object value = null;
			for (KeyValue keyValue : entityKvList)
			{
				value = keyValue.getValue();
				if (value != null)
				{
					wb.and(keyValue.getKey(), "=", value);
				}
			}
			selector.where(wb);
		}
		return count(selector);
	}

	/**
	 * 创建数据库
	 * 
	 * @param config
	 * @return SQLiteDatabase
	 */
	private SQLiteDatabase createDatabase(DaoConfig config)
	{
		SQLiteDatabase result = null;
		boolean isExist = false;

		String dbDir = config.getDbDir();
		if (!TextUtils.isEmpty(dbDir))
		{
			File dir = new File(dbDir);
			if (!dir.exists())
			{
				dir.mkdirs();
			}

			File dbFile = new File(dbDir, config.getDbName());
			isExist = dbFile.exists();
			result = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		}
		else
		{
			File dbFile = config.getContext().getDatabasePath(config.getDbName());
			isExist = dbFile.exists();
			result = config.getContext().openOrCreateDatabase(config.getDbName(), 0, null);
		}

		if (!isExist)
		{
			result.setVersion(config.getDbVersion());
		}

		return result;
	}

	// ***************************** private operations with out transaction
	// *****************************
	/**
	 * 根据对象，构造新增或修改sql语句，并执行新增或修改操作
	 * 
	 * @param entity
	 * @throws DbException
	 *             void
	 */
	private void saveOrUpdateWithoutTransaction(Object entity) throws DbException
	{
		Id id = TableUtils.getId(entity.getClass());
		if (id.isAutoIncrement())
		{
			if (id.getColumnValue(entity) != null)
			{
				execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(this, entity));
			}
			else
			{
				saveBindingIdWithoutTransaction(entity);
			}
		}
		else
		{
			execNonQuery(SqlInfoBuilder.buildReplaceSqlInfo(this, entity));
		}
	}

	/**
	 * 根据对象，构造新增或修改sql语句，并执行新增或修改操作;如果id为自增长列，设置增长数据。
	 * 
	 * @param entity
	 * @return boolean
	 * @throws DbException
	 * 
	 */
	private boolean saveBindingIdWithoutTransaction(Object entity) throws DbException
	{
		Class<?> entityType = entity.getClass();
		String tableName = TableUtils.getTableName(entityType);
		Id idColumn = TableUtils.getId(entityType);
		if (idColumn.isAutoIncrement())
		{
			List<KeyValue> entityKvList = SqlInfoBuilder.entity2KeyValueList(this, entity);
			if (entityKvList != null && entityKvList.size() > 0)
			{
				ContentValues cv = new ContentValues();
				TGDBManager.fillContentValues(cv, entityKvList);
				long id = database.insert(tableName, null, cv);
				if (id == -1)
				{
					return false;
				}
				idColumn.setAutoIncrementId(entity, id);
				return true;
			}
		}
		else
		{
			execNonQuery(SqlInfoBuilder.buildInsertSqlInfo(this, entity));
			return true;
		}
		return false;
	}

	// ************************************************ tools
	// ***********************************

	/**
	 * 把键值对集合数据设置到contentValue中
	 * 
	 * @param contentValues
	 * @param list
	 *            void
	 */
	private static void fillContentValues(ContentValues contentValues, List<KeyValue> list)
	{
		if (list != null && contentValues != null)
		{
			Object value = null;
			for (KeyValue kv : list)
			{
				value = kv.getValue();
				if (value != null)
				{
					contentValues.put(kv.getKey(), value.toString());
				}
			}
		}
		else
		{
			LogTools.w("List<KeyValue> is empty or ContentValues is empty!");
		}
	}

	/**
	 * 检查表是否存在，如果不存在，根据entity创建新表
	 * 
	 * @param entityType
	 * @throws DbException
	 *             void
	 */
	public void createTableIfNotExist(Class<?> entityType) throws DbException
	{
		if (!tableIsExist(entityType))
		{
			SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(entityType);
			execNonQuery(sqlInfo);
			String execAfterTableCreated = TableUtils.getExecAfterTableCreated(entityType);
			if (!TextUtils.isEmpty(execAfterTableCreated))
			{
				execNonQuery(execAfterTableCreated);
			}

			addTGTable(entityType);
		}
	}

	/**
	 * 把新加的业务表表名和对应的entity路径添加到tiger系统表
	 * 
	 * void
	 * 
	 * @throws DbException
	 */
	private void addTGTable(Class<?> entityType) throws DbException
	{
		// 获取表名
		String tableName = TableUtils.getTableName(entityType);
		// tigerTables表本身不添加进业务表
		if ("TigerTables".equals(entityType.getSimpleName()))
		{
			return;
		}
		// 添加业务表信息到tigerTables表
		TigerTables table = new TigerTables();
		table.setName(tableName);
		table.setClassPath(entityType.getName());
		save(table);
	}

	/**
	 * 根据entity类型，检查其对应的表是否存在
	 * 
	 * @param entityType
	 * @return boolean
	 * @throws DbException
	 * 
	 */
	public boolean tableIsExist(Class<?> entityType) throws DbException
	{
		Table table = Table.get(this, entityType);
		if (table.isCheckedDatabase())
		{
			return true;
		}

		Cursor cursor = null;
		try
		{
			cursor = execQuery("SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='"
					+ table.getTableName() + "'");
			if (cursor != null && cursor.moveToNext())
			{
				int count = cursor.getInt(0);
				if (count > 0)
				{
					table.setCheckedDatabase(true);
					return true;
				}
			}
		}
		finally
		{
			IOUtils.closeQuietly(cursor);
		}

		return false;
	}

	/**
	 * 删除数据库中所有表
	 * 
	 * @throws DbException
	 *             void
	 */
	public void dropDb() throws DbException
	{
		Cursor cursor = null;
		try
		{
			cursor = execQuery("SELECT name FROM sqlite_master WHERE type ='table'");
			if (cursor != null)
			{
				while (cursor.moveToNext())
				{
					try
					{
						String tableName = cursor.getString(0);
						execNonQuery("DROP TABLE " + tableName);
						Table.remove(this, tableName);
					}
					catch (Throwable e)
					{
						LogTools.e(e.getMessage(), e);
					}
				}
			}
		}
		finally
		{
			IOUtils.closeQuietly(cursor);
		}
	}

	/**
	 * 根据entity类型，删除对应的表
	 * 
	 * @param entityType
	 * @throws DbException
	 *             void
	 */
	public void dropTable(Class<?> entityType) throws DbException
	{
		if (!tableIsExist(entityType))
		{
			return;
		}
			
		String tableName = TableUtils.getTableName(entityType);
		execNonQuery("DROP TABLE " + tableName);
		Table.remove(this, entityType);
	}

	// /////////////////////////////////// exec sql
	// /////////////////////////////////////////////////////
	private void debugSql(String sql)
	{
		if (debug)
		{
			LogTools.d(sql);
		}
	}

	private void beginTransaction()
	{
		if (allowTransaction)
		{
			database.beginTransaction();
		}
	}

	private void setTransactionSuccessful()
	{
		if (allowTransaction)
		{
			database.setTransactionSuccessful();
		}
	}

	private void endTransaction()
	{
		if (allowTransaction)
		{
			database.endTransaction();
		}
	}

	/**
	 * 根据sql语句和参数，执行非查询操作
	 * 
	 * @param sqlInfo
	 * @throws DbException
	 *             void
	 */
	public void execNonQuery(SqlInfo sqlInfo) throws DbException
	{
		debugSql(sqlInfo.getSql());
		try
		{
			if (sqlInfo.getBindArgs() != null)
			{
				database.execSQL(sqlInfo.getSql(), sqlInfo.getBindArgsAsArray());
			}
			else
			{
				database.execSQL(sqlInfo.getSql());
			}
		}
		catch (Throwable e)
		{
			throw new DbException(e);
		}
	}

	/**
	 * 根据sql语句，执行非查询操作
	 * 
	 * @param sql
	 * @throws DbException
	 *             void
	 */
	public void execNonQuery(String sql) throws DbException
	{
		debugSql(sql);
		try
		{
			database.execSQL(sql);
		}
		catch (Throwable e)
		{
			throw new DbException(e);
		}
	}

	/**
	 * 根据sql语句和参数，执行查询操作
	 * 
	 * @param sqlInfo
	 * @return Cursor
	 * @throws DbException
	 * 
	 */
	public Cursor execQuery(SqlInfo sqlInfo) throws DbException
	{
		debugSql(sqlInfo.getSql());
		try
		{
			return database.rawQuery(sqlInfo.getSql(), sqlInfo.getBindArgsAsStrArray());
		}
		catch (Throwable e)
		{
			throw new DbException(e);
		}
	}

	/**
	 * 根据sql语句，执行查询操作
	 * 
	 * @param sql
	 * @return Cursor
	 * @throws DbException
	 * 
	 */
	public Cursor execQuery(String sql) throws DbException
	{
		debugSql(sql);
		try
		{
			return database.rawQuery(sql, null);
		}
		catch (Throwable e)
		{
			throw new DbException(e);
		}
	}

	/**
	 * 获取数据库中所有的表名称集合
	 * 
	 * @return
	 * @throws DbException
	 *             List<String>
	 */
	public List<String> getAllTableFromDb() throws DbException
	{
		List<String> tableNameList = null;
		Cursor cursor = null;
		try
		{
			cursor = execQuery("SELECT name FROM sqlite_master WHERE type ='table'");
			if (cursor != null)
			{
				tableNameList = new ArrayList<String>();
				while (cursor.moveToNext())
				{
					try
					{
						String tableName = cursor.getString(0);
						tableNameList.add(tableName);
					}
					catch (Throwable e)
					{
						LogTools.e(e.getMessage(), e);
					}
				}
			}
		}
		finally
		{
			IOUtils.closeQuietly(cursor);
		}

		return tableNameList;
	}

	/**
	 * 获取数据库中所有的非表 对象名称集合
	 * 
	 * @return
	 * @throws DbException
	 *             List<String>
	 */
	public List<DatabaseObject> getAllOtherFromDb() throws DbException
	{
		List<DatabaseObject> objNameList = null;
		Cursor cursor = null;
		try
		{
			cursor = execQuery("SELECT name,type FROM sqlite_master WHERE type <>'table'");
			if (cursor != null)
			{
				objNameList = new ArrayList<DatabaseObject>();
				DatabaseObject obj = null;
				while (cursor.moveToNext())
				{
					obj = new DatabaseObject();
					try
					{
						obj.setName(cursor.getString(0));
						obj.setType(cursor.getString(1));
						objNameList.add(obj);
					}
					catch (Throwable e)
					{
						LogTools.e(e.getMessage(), e);
					}
				}
			}
		}
		finally
		{
			IOUtils.closeQuietly(cursor);
		}

		return objNameList;
	}
}
