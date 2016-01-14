package com.mn.tiger.datastorage.db.table;

import android.database.Cursor;

import com.mn.tiger.datastorage.TGDBManager;
import com.mn.tiger.datastorage.db.converter.ColumnConverter;
import com.mn.tiger.datastorage.db.converter.ColumnConverterFactory;
import com.mn.tiger.datastorage.db.exception.DbException;
import com.mn.tiger.datastorage.db.sqlite.ForeignLazyLoader;
import com.mn.tiger.log.Logger;

import java.lang.reflect.Field;
import java.util.List;

public class Foreign extends Column
{
	private static final Logger LOG = Logger.getLogger(Foreign.class);

	public TGDBManager db;

	private final String foreignColumnName;
	@SuppressWarnings("rawtypes")
	private final ColumnConverter foreignColumnConverter;

	protected Foreign(Class<?> entityType, Field field)
	{
		super(entityType, field);

		foreignColumnName = ColumnUtils.getForeignColumnNameByField(field);
		Class<?> foreignColumnType = TableUtils.getColumnOrId(getForeignEntityType(),
				foreignColumnName).columnField.getType();
		foreignColumnConverter = ColumnConverterFactory.getColumnConverter(foreignColumnType);
	}

	public String getForeignColumnName()
	{
		return foreignColumnName;
	}

	public Class<?> getForeignEntityType()
	{
		return ColumnUtils.getForeignEntityType(this);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setValue2Entity(Object entity, Cursor cursor, int index)
	{
		Object filedValue = foreignColumnConverter.getFiledValue(cursor, index);
		if (filedValue == null)
			return;

		Object value = null;
		Class<?> columnType = columnField.getType();
		if (columnType.equals(ForeignLazyLoader.class))
		{
			value = new ForeignLazyLoader(this, filedValue);
		}
		else if (columnType.equals(List.class))
		{
			try
			{
				value = new ForeignLazyLoader(this, filedValue).getAllFromDb();
			}
			catch (DbException e)
			{
				LOG.e("[Method:setValue2Entity]", e);
			}
		}
		else
		{
			try
			{
				value = new ForeignLazyLoader(this, filedValue).getFirstFromDb();
			}
			catch (DbException e)
			{
				LOG.e("[Method:setValue2Entity]", e);
			}
		}

		if (setMethod != null)
		{
			try
			{
				setMethod.invoke(entity, value);
			}
			catch (Throwable e)
			{
				LOG.e("[Method:setValue2Entity]", e);
			}
		}
		else
		{
			try
			{
				this.columnField.setAccessible(true);
				this.columnField.set(entity, value);
			}
			catch (Throwable e)
			{
				LOG.e("[Method:setValue2Entity]", e);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getColumnValue(Object entity)
	{
		Object fieldValue = getFieldValue(entity);
		Object columnValue = null;

		if (fieldValue != null)
		{
			Class<?> columnType = columnField.getType();
			if (columnType.equals(ForeignLazyLoader.class))
			{
				columnValue = ((ForeignLazyLoader) fieldValue).getColumnValue();
			}
			else if (columnType.equals(List.class))
			{
				try
				{
					List<?> foreignEntities = (List<?>) fieldValue;
					if (foreignEntities.size() > 0)
					{

						Class<?> foreignEntityType = ColumnUtils.getForeignEntityType(this);
						Column column = TableUtils.getColumnOrId(foreignEntityType,
								foreignColumnName);
						columnValue = column.getColumnValue(foreignEntities.get(0));

						if (this.db != null && columnValue == null && column instanceof Id)
						{
							this.db.saveOrUpdateAll(foreignEntities);
						}

						columnValue = column.getColumnValue(foreignEntities.get(0));
					}
				}
				catch (Throwable e)
				{
					LOG.e("[Method:getColumnValue]", e);
				}
			}
			else
			{
				try
				{
					Column column = TableUtils.getColumnOrId(columnType, foreignColumnName);
					columnValue = column.getColumnValue(fieldValue);

					if (this.db != null && columnValue == null && column instanceof Id)
					{
						try
						{
							this.db.saveOrUpdate(fieldValue);
						}
						catch (DbException e)
						{
							LOG.e("[Method:getColumnValue]", e);
						}
					}

					columnValue = column.getColumnValue(fieldValue);
				}
				catch (Throwable e)
				{
					LOG.e("[Method:getColumnValue]", e);
				}
			}
		}

		return columnValue;
	}

	@Override
	public String getColumnDbType()
	{
		return foreignColumnConverter.getColumnDbType();
	}

	/**
	 * It always return null.
	 *
	 * @return null
	 */
	@Override
	public Object getDefaultValue()
	{
		return null;
	}
}
