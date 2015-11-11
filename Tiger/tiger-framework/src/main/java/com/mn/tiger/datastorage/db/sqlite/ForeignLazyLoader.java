package com.mn.tiger.datastorage.db.sqlite;

import java.util.List;

import com.mn.tiger.datastorage.db.exception.DbException;
import com.mn.tiger.datastorage.db.table.ColumnUtils;
import com.mn.tiger.datastorage.db.table.Foreign;
import com.mn.tiger.datastorage.db.table.TableUtils;

public class ForeignLazyLoader<T>
{
	private final Foreign foreignColumn;
	private Object columnValue;

	public ForeignLazyLoader(Class<?> entityType, String columnName, Object value)
	{
		this.foreignColumn = (Foreign) TableUtils.getColumnOrId(entityType, columnName);
		this.columnValue = ColumnUtils.convert2DbColumnValueIfNeeded(value);
	}

	public ForeignLazyLoader(Foreign foreignColumn, Object value)
	{
		this.foreignColumn = foreignColumn;
		this.columnValue = ColumnUtils.convert2DbColumnValueIfNeeded(value);
	}

	public List<T> getAllFromDb() throws DbException
	{
		List<T> entities = null;
		if (foreignColumn != null && foreignColumn.db != null)
		{
			entities = foreignColumn.db.findAll(Selector.from(foreignColumn.getForeignEntityType())
					.where(foreignColumn.getForeignColumnName(), "=", columnValue));
		}
		return entities;
	}

	@SuppressWarnings("unchecked")
	public T getFirstFromDb() throws DbException
	{
		T entity = null;
		if (foreignColumn != null && foreignColumn.db != null)
		{
			entity = (T) foreignColumn.db.findFirst(Selector.from(
					foreignColumn.getForeignEntityType()).where(
					foreignColumn.getForeignColumnName(), "=", columnValue));
		}
		return entity;
	}

	public void setColumnValue(Object value)
	{
		this.columnValue = ColumnUtils.convert2DbColumnValueIfNeeded(value);
	}

	public Object getColumnValue()
	{
		return columnValue;
	}
}
