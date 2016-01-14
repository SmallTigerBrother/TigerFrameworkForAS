package com.mn.tiger.datastorage.db.sqlite;

import android.database.Cursor;

import com.mn.tiger.datastorage.TGDBManager;
import com.mn.tiger.datastorage.db.table.Column;
import com.mn.tiger.datastorage.db.table.ColumnObject;
import com.mn.tiger.datastorage.db.table.DbModel;
import com.mn.tiger.datastorage.db.table.Foreign;
import com.mn.tiger.datastorage.db.table.Id;
import com.mn.tiger.datastorage.db.table.Table;
import com.mn.tiger.datastorage.db.util.core.DoubleKeyValueMap;
import com.mn.tiger.log.Logger;

/**
 *
 * 游标操作类
 *
 */
public class CursorUtils
{
	private static final Logger LOG = Logger.getLogger(CursorUtils.class);
	/**
	 * 把游标中的数据设置到对应的entity中
	 *
	 * @param db
	 *            数据库管理对象
	 * @param cursor
	 *            游标
	 * @param entityType
	 *            对象类型
	 * @param findCacheSequence
	 *            缓存序列
	 * @return T 包含数据的对象
	 */
	public static <T> T getEntity(final TGDBManager db, final Cursor cursor, Class<T> entityType,
								  long findCacheSequence)
	{
		if (db == null || cursor == null)
			return null;

		EntityTempCache.setSeq(findCacheSequence);
		try
		{
			Table table = Table.get(db, entityType);
			Id id = table.getId();
			String idColumnName = id.getColumnName();
			int idIndex = cursor.getColumnIndex(idColumnName);
			String idStr = cursor.getString(idIndex);
			T entity = EntityTempCache.get(entityType, idStr);
			if (entity == null)
			{
				entity = entityType.newInstance();
				id.setValue2Entity(entity, cursor, idIndex);
				EntityTempCache.put(entity, idStr);
			}
			else
			{
				return entity;
			}
			int columnCount = cursor.getColumnCount();
			String columnName = null;
			Column column = null;
			Foreign foreign = null;
			for (int i = 0; i < columnCount; i++)
			{
				columnName = cursor.getColumnName(i);
				column = table.columnMap.get(columnName);
				if (column != null)
				{
					if (column instanceof Foreign)
					{
						foreign = (Foreign) column;
						foreign.db = db;
						foreign.setValue2Entity(entity, cursor, i);
					}
					else if(column instanceof ColumnObject)
					{
						column.setValue2Entity(entity, cursor, i);
					}
					else
					{
						column.setValue2Entity(entity, cursor, i);
					}
				}
			}

			return entity;
		}
		catch (Throwable e)
		{
			LOG.e("[Method:getEntity]", e);
		}

		return null;
	}

	/**
	 * 把游标中的数据转换为键值对对象
	 *
	 * @param cursor
	 * @return DbModel
	 */
	public static DbModel getDbModel(final Cursor cursor)
	{
		DbModel result = null;
		if (cursor != null)
		{
			result = new DbModel();
			int columnCount = cursor.getColumnCount();
			for (int i = 0; i < columnCount; i++)
			{
				result.add(cursor.getColumnName(i), cursor.getString(i));
			}
		}
		return result;
	}

	/**
	 *
	 * 缓存序列类
	 *
	 */
	public static class FindCacheSequence
	{
		private static long seq = 0;
		private static final String FOREIGN_LAZY_LOADER_CLASS_NAME = ForeignLazyLoader.class
				.getName();

		public static long getSeq()
		{
			String findMethodCaller = Thread.currentThread().getStackTrace()[4].getClassName();
			if (!findMethodCaller.equals(FOREIGN_LAZY_LOADER_CLASS_NAME))
			{
				++seq;
			}
			return seq;
		}
	}

	/**
	 * 对象缓存
	 *
	 */
	private static class EntityTempCache
	{
		private EntityTempCache()
		{
		}

		/**
		 * k1: entityType; k2: idValue value: entity
		 */
		private static final DoubleKeyValueMap<Class<?>, String, Object> cache = new DoubleKeyValueMap<Class<?>, String, Object>();

		private static long seq = 0;

		public static void put(Object entity, String idStr)
		{
			if (entity != null && idStr != null)
			{
				cache.put(entity.getClass(), idStr, entity);
			}
		}

		@SuppressWarnings("unchecked")
		public static <T> T get(Class<T> entityType, String idStr)
		{
			return (T) cache.get(entityType, idStr);
		}

		public static void setSeq(long seq)
		{
			if (EntityTempCache.seq != seq)
			{
				cache.clear();
				EntityTempCache.seq = seq;
			}
		}
	}
}
